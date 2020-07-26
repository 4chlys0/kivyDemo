package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HIDDeviceManager {
    private static final String ACTION_USB_PERMISSION = "org.libsdl.app.USB_PERMISSION";
    private static final String TAG = "hidapi";
    private static HIDDeviceManager sManager;
    private static int sManagerRefCount = 0;
    private final BroadcastReceiver mBluetoothBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                String str = HIDDeviceManager.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Bluetooth device connected: ");
                sb.append(device);
                Log.d(str, sb.toString());
                if (HIDDeviceManager.this.isSteamController(device)) {
                    HIDDeviceManager.this.connectBluetoothDevice(device);
                }
            }
            if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                BluetoothDevice device2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                String str2 = HIDDeviceManager.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Bluetooth device disconnected: ");
                sb2.append(device2);
                Log.d(str2, sb2.toString());
                HIDDeviceManager.this.disconnectBluetoothDevice(device2);
            }
        }
    };
    private HashMap<BluetoothDevice, HIDDeviceBLESteamController> mBluetoothDevices = new HashMap<>();
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private HashMap<Integer, HIDDevice> mDevicesById = new HashMap<>();
    private Handler mHandler;
    private boolean mIsChromebook = false;
    private List<BluetoothDevice> mLastBluetoothDevices;
    private int mNextDeviceId = 0;
    private SharedPreferences mSharedPreferences = null;
    private HashMap<UsbDevice, HIDDeviceUSB> mUSBDevices = new HashMap<>();
    private final BroadcastReceiver mUsbBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                HIDDeviceManager.this.handleUsbDeviceAttached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                HIDDeviceManager.this.handleUsbDeviceDetached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals(HIDDeviceManager.ACTION_USB_PERMISSION)) {
                HIDDeviceManager.this.handleUsbDevicePermission((UsbDevice) intent.getParcelableExtra("device"), intent.getBooleanExtra("permission", false));
            }
        }
    };
    private UsbManager mUsbManager;

    private native void HIDDeviceRegisterCallback();

    private native void HIDDeviceReleaseCallback();

    /* access modifiers changed from: 0000 */
    public native void HIDDeviceConnected(int i, String str, int i2, int i3, String str2, int i4, String str3, String str4, int i5);

    /* access modifiers changed from: 0000 */
    public native void HIDDeviceDisconnected(int i);

    /* access modifiers changed from: 0000 */
    public native void HIDDeviceFeatureReport(int i, byte[] bArr);

    /* access modifiers changed from: 0000 */
    public native void HIDDeviceInputReport(int i, byte[] bArr);

    /* access modifiers changed from: 0000 */
    public native void HIDDeviceOpenPending(int i);

    /* access modifiers changed from: 0000 */
    public native void HIDDeviceOpenResult(int i, boolean z);

    public static HIDDeviceManager acquire(Context context) {
        if (sManagerRefCount == 0) {
            sManager = new HIDDeviceManager(context);
        }
        sManagerRefCount++;
        return sManager;
    }

    public static void release(HIDDeviceManager manager) {
        if (manager == sManager) {
            sManagerRefCount--;
            if (sManagerRefCount == 0) {
                sManager.close();
                sManager = null;
            }
        }
    }

    private HIDDeviceManager(final Context context) {
        this.mContext = context;
        try {
            SDL.loadLibrary(TAG);
            HIDDeviceRegisterCallback();
            this.mSharedPreferences = this.mContext.getSharedPreferences(TAG, 0);
            this.mIsChromebook = this.mContext.getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
            this.mNextDeviceId = this.mSharedPreferences.getInt("next_device_id", 0);
            initializeUSB();
            initializeBluetooth();
        } catch (Throwable e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Couldn't load hidapi: ");
            sb.append(e.toString());
            Log.w(str, sb.toString());
            Builder builder = new Builder(context);
            builder.setCancelable(false);
            builder.setTitle("SDL HIDAPI Error");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Please report the following error to the SDL maintainers: ");
            sb2.append(e.getMessage());
            builder.setMessage(sb2.toString());
            builder.setNegativeButton("Quit", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        ((Activity) context).finish();
                    } catch (ClassCastException e) {
                    }
                }
            });
            builder.show();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getDeviceIDForIdentifier(String identifier) {
        Editor spedit = this.mSharedPreferences.edit();
        int result = this.mSharedPreferences.getInt(identifier, 0);
        if (result == 0) {
            int i = this.mNextDeviceId;
            this.mNextDeviceId = i + 1;
            result = i;
            spedit.putInt("next_device_id", this.mNextDeviceId);
        }
        spedit.putInt(identifier, result);
        spedit.commit();
        return result;
    }

    private void initializeUSB() {
        this.mUsbManager = (UsbManager) this.mContext.getSystemService("usb");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        this.mContext.registerReceiver(this.mUsbBroadcast, filter);
        for (UsbDevice usbDevice : this.mUsbManager.getDeviceList().values()) {
            handleUsbDeviceAttached(usbDevice);
        }
    }

    /* access modifiers changed from: 0000 */
    public UsbManager getUSBManager() {
        return this.mUsbManager;
    }

    private void shutdownUSB() {
        try {
            this.mContext.unregisterReceiver(this.mUsbBroadcast);
        } catch (Exception e) {
        }
    }

    private boolean isHIDDeviceUSB(UsbDevice usbDevice) {
        for (int interface_number = 0; interface_number < usbDevice.getInterfaceCount(); interface_number++) {
            if (isHIDDeviceInterface(usbDevice, interface_number)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHIDDeviceInterface(UsbDevice usbDevice, int interface_number) {
        UsbInterface usbInterface = usbDevice.getInterface(interface_number);
        if (usbInterface.getInterfaceClass() == 3) {
            return true;
        }
        if (interface_number != 0 || (!isXbox360Controller(usbDevice, usbInterface) && !isXboxOneController(usbDevice, usbInterface))) {
            return false;
        }
        return true;
    }

    private boolean isXbox360Controller(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] SUPPORTED_VENDORS = {121, 1103, 1118, 1133, 1390, 1699, 1848, 2047, 3695, 3853, 4553, 4779, 5168, 5227, 5426, 5604, 5678, 5769, 7085, 9414};
        if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 93 && usbInterface.getInterfaceProtocol() == 1) {
            int vendor_id = usbDevice.getVendorId();
            for (int supportedVid : SUPPORTED_VENDORS) {
                if (vendor_id == supportedVid) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isXboxOneController(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] SUPPORTED_VENDORS = {1118, 1848, 3695, 3853, 5426, 9414};
        if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 71 && usbInterface.getInterfaceProtocol() == 208) {
            int vendor_id = usbDevice.getVendorId();
            for (int supportedVid : SUPPORTED_VENDORS) {
                if (vendor_id == supportedVid) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void handleUsbDeviceAttached(UsbDevice usbDevice) {
        if (isHIDDeviceUSB(usbDevice)) {
            connectHIDDeviceUSB(usbDevice);
        }
    }

    /* access modifiers changed from: private */
    public void handleUsbDeviceDetached(UsbDevice usbDevice) {
        HIDDeviceUSB device = (HIDDeviceUSB) this.mUSBDevices.get(usbDevice);
        if (device != null) {
            int id = device.getId();
            this.mUSBDevices.remove(usbDevice);
            this.mDevicesById.remove(Integer.valueOf(id));
            device.shutdown();
            HIDDeviceDisconnected(id);
        }
    }

    /* access modifiers changed from: private */
    public void handleUsbDevicePermission(UsbDevice usbDevice, boolean permission_granted) {
        HIDDeviceUSB device = (HIDDeviceUSB) this.mUSBDevices.get(usbDevice);
        if (device != null) {
            boolean opened = false;
            if (permission_granted) {
                opened = device.open();
            }
            HIDDeviceOpenResult(device.getId(), opened);
        }
    }

    private void connectHIDDeviceUSB(UsbDevice usbDevice) {
        synchronized (this) {
            int interface_number = 0;
            while (true) {
                if (interface_number >= usbDevice.getInterfaceCount()) {
                    break;
                } else if (isHIDDeviceInterface(usbDevice, interface_number)) {
                    HIDDeviceUSB device = new HIDDeviceUSB(this, usbDevice, interface_number);
                    int id = device.getId();
                    this.mUSBDevices.put(usbDevice, device);
                    this.mDevicesById.put(Integer.valueOf(id), device);
                    HIDDeviceConnected(id, device.getIdentifier(), device.getVendorId(), device.getProductId(), device.getSerialNumber(), device.getVersion(), device.getManufacturerName(), device.getProductName(), interface_number);
                    break;
                } else {
                    interface_number++;
                }
            }
        }
    }

    private void initializeBluetooth() {
        Log.d(TAG, "Initializing Bluetooth");
        if (this.mContext.getPackageManager().checkPermission("android.permission.BLUETOOTH", this.mContext.getPackageName()) != 0) {
            Log.d(TAG, "Couldn't initialize Bluetooth, missing android.permission.BLUETOOTH");
            return;
        }
        this.mBluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        if (this.mBluetoothManager != null) {
            BluetoothAdapter btAdapter = this.mBluetoothManager.getAdapter();
            if (btAdapter != null) {
                for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bluetooth device available: ");
                    sb.append(device);
                    Log.d(str, sb.toString());
                    if (isSteamController(device)) {
                        connectBluetoothDevice(device);
                    }
                }
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
                filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
                this.mContext.registerReceiver(this.mBluetoothBroadcast, filter);
                if (this.mIsChromebook) {
                    this.mHandler = new Handler(Looper.getMainLooper());
                    this.mLastBluetoothDevices = new ArrayList();
                }
            }
        }
    }

    private void shutdownBluetooth() {
        try {
            this.mContext.unregisterReceiver(this.mBluetoothBroadcast);
        } catch (Exception e) {
        }
    }

    public void chromebookConnectionHandler() {
        if (this.mIsChromebook) {
            ArrayList<BluetoothDevice> disconnected = new ArrayList<>();
            ArrayList<BluetoothDevice> connected = new ArrayList<>();
            List<BluetoothDevice> currentConnected = this.mBluetoothManager.getConnectedDevices(7);
            for (BluetoothDevice bluetoothDevice : currentConnected) {
                if (!this.mLastBluetoothDevices.contains(bluetoothDevice)) {
                    connected.add(bluetoothDevice);
                }
            }
            for (BluetoothDevice bluetoothDevice2 : this.mLastBluetoothDevices) {
                if (!currentConnected.contains(bluetoothDevice2)) {
                    disconnected.add(bluetoothDevice2);
                }
            }
            this.mLastBluetoothDevices = currentConnected;
            Iterator it = disconnected.iterator();
            while (it.hasNext()) {
                disconnectBluetoothDevice((BluetoothDevice) it.next());
            }
            Iterator it2 = connected.iterator();
            while (it2.hasNext()) {
                connectBluetoothDevice((BluetoothDevice) it2.next());
            }
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    this.chromebookConnectionHandler();
                }
            }, 10000);
        }
    }

    public boolean connectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("connectBluetoothDevice device=");
        sb.append(bluetoothDevice);
        Log.v(str, sb.toString());
        synchronized (this) {
            if (this.mBluetoothDevices.containsKey(bluetoothDevice)) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Steam controller with address ");
                sb2.append(bluetoothDevice);
                sb2.append(" already exists, attempting reconnect");
                Log.v(str2, sb2.toString());
                ((HIDDeviceBLESteamController) this.mBluetoothDevices.get(bluetoothDevice)).reconnect();
                return false;
            }
            HIDDeviceBLESteamController device = new HIDDeviceBLESteamController(this, bluetoothDevice);
            int id = device.getId();
            this.mBluetoothDevices.put(bluetoothDevice, device);
            this.mDevicesById.put(Integer.valueOf(id), device);
            return true;
        }
    }

    public void disconnectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            HIDDeviceBLESteamController device = (HIDDeviceBLESteamController) this.mBluetoothDevices.get(bluetoothDevice);
            if (device != null) {
                int id = device.getId();
                this.mBluetoothDevices.remove(bluetoothDevice);
                this.mDevicesById.remove(Integer.valueOf(id));
                device.shutdown();
                HIDDeviceDisconnected(id);
            }
        }
    }

    public boolean isSteamController(BluetoothDevice bluetoothDevice) {
        boolean z = false;
        if (bluetoothDevice == null || bluetoothDevice.getName() == null) {
            return false;
        }
        if (bluetoothDevice.getName().equals("SteamController") && (bluetoothDevice.getType() & 2) != 0) {
            z = true;
        }
        return z;
    }

    private void close() {
        shutdownUSB();
        shutdownBluetooth();
        synchronized (this) {
            for (HIDDevice device : this.mDevicesById.values()) {
                device.shutdown();
            }
            this.mDevicesById.clear();
            this.mBluetoothDevices.clear();
            HIDDeviceReleaseCallback();
        }
    }

    public void setFrozen(boolean frozen) {
        synchronized (this) {
            for (HIDDevice device : this.mDevicesById.values()) {
                device.setFrozen(frozen);
            }
        }
    }

    private HIDDevice getDevice(int id) {
        HIDDevice result;
        synchronized (this) {
            result = (HIDDevice) this.mDevicesById.get(Integer.valueOf(id));
            if (result == null) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("No device for id: ");
                sb.append(id);
                Log.v(str, sb.toString());
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Available devices: ");
                sb2.append(this.mDevicesById.keySet());
                Log.v(str2, sb2.toString());
            }
        }
        return result;
    }

    public boolean openDevice(int deviceID) {
        Iterator it = this.mUSBDevices.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            HIDDeviceUSB device = (HIDDeviceUSB) it.next();
            if (deviceID == device.getId()) {
                UsbDevice usbDevice = device.getDevice();
                if (!this.mUsbManager.hasPermission(usbDevice)) {
                    HIDDeviceOpenPending(deviceID);
                    try {
                        this.mUsbManager.requestPermission(usbDevice, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0));
                    } catch (Exception e) {
                        String str = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Couldn't request permission for USB device ");
                        sb.append(usbDevice);
                        Log.v(str, sb.toString());
                        HIDDeviceOpenResult(deviceID, false);
                    }
                    return false;
                }
            }
        }
        String str2 = TAG;
        try {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("openDevice deviceID=");
            sb2.append(deviceID);
            Log.v(str2, sb2.toString());
            HIDDevice device2 = getDevice(deviceID);
            if (device2 != null) {
                return device2.open();
            }
            HIDDeviceDisconnected(deviceID);
            return false;
        } catch (Exception e2) {
            String str3 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Got exception: ");
            sb3.append(Log.getStackTraceString(e2));
            Log.e(str3, sb3.toString());
            return false;
        }
    }

    public int sendOutputReport(int deviceID, byte[] report) {
        String str = TAG;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("sendOutputReport deviceID=");
            sb.append(deviceID);
            sb.append(" length=");
            sb.append(report.length);
            Log.v(str, sb.toString());
            HIDDevice device = getDevice(deviceID);
            if (device != null) {
                return device.sendOutputReport(report);
            }
            HIDDeviceDisconnected(deviceID);
            return -1;
        } catch (Exception e) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Got exception: ");
            sb2.append(Log.getStackTraceString(e));
            Log.e(str2, sb2.toString());
            return -1;
        }
    }

    public int sendFeatureReport(int deviceID, byte[] report) {
        String str = TAG;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("sendFeatureReport deviceID=");
            sb.append(deviceID);
            sb.append(" length=");
            sb.append(report.length);
            Log.v(str, sb.toString());
            HIDDevice device = getDevice(deviceID);
            if (device != null) {
                return device.sendFeatureReport(report);
            }
            HIDDeviceDisconnected(deviceID);
            return -1;
        } catch (Exception e) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Got exception: ");
            sb2.append(Log.getStackTraceString(e));
            Log.e(str2, sb2.toString());
            return -1;
        }
    }

    public boolean getFeatureReport(int deviceID, byte[] report) {
        String str = TAG;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("getFeatureReport deviceID=");
            sb.append(deviceID);
            Log.v(str, sb.toString());
            HIDDevice device = getDevice(deviceID);
            if (device != null) {
                return device.getFeatureReport(report);
            }
            HIDDeviceDisconnected(deviceID);
            return false;
        } catch (Exception e) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Got exception: ");
            sb2.append(Log.getStackTraceString(e));
            Log.e(str2, sb2.toString());
            return false;
        }
    }

    public void closeDevice(int deviceID) {
        String str = TAG;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("closeDevice deviceID=");
            sb.append(deviceID);
            Log.v(str, sb.toString());
            HIDDevice device = getDevice(deviceID);
            if (device == null) {
                HIDDeviceDisconnected(deviceID);
            } else {
                device.close();
            }
        } catch (Exception e) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Got exception: ");
            sb2.append(Log.getStackTraceString(e));
            Log.e(str2, sb2.toString());
        }
    }
}
