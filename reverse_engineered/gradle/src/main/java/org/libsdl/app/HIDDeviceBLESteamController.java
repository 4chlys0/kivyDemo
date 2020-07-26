package org.libsdl.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

class HIDDeviceBLESteamController extends BluetoothGattCallback implements HIDDevice {
    private static final int CHROMEBOOK_CONNECTION_CHECK_INTERVAL = 10000;
    private static final String TAG = "hidapi";
    private static final int TRANSPORT_AUTO = 0;
    private static final int TRANSPORT_BREDR = 1;
    private static final int TRANSPORT_LE = 2;
    private static final byte[] enterValveMode = {-64, -121, 3, 8, 7, 0};
    public static final UUID inputCharacteristic = UUID.fromString("100F6C33-1735-4313-B402-38567131E5F3");
    public static final UUID reportCharacteristic = UUID.fromString("100F6C34-1735-4313-B402-38567131E5F3");
    public static final UUID steamControllerService = UUID.fromString("100F6C32-1735-4313-B402-38567131E5F3");
    GattOperation mCurrentOperation = null;
    private BluetoothDevice mDevice;
    private int mDeviceId;
    private boolean mFrozen = false;
    /* access modifiers changed from: private */
    public BluetoothGatt mGatt;
    private Handler mHandler;
    private boolean mIsChromebook = false;
    private boolean mIsConnected = false;
    private boolean mIsReconnecting = false;
    private boolean mIsRegistered = false;
    private HIDDeviceManager mManager;
    /* access modifiers changed from: private */
    public LinkedList<GattOperation> mOperations;

    /* renamed from: org.libsdl.app.HIDDeviceBLESteamController$5 reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation = new int[Operation.values().length];

        static {
            try {
                $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation[Operation.CHR_READ.ordinal()] = HIDDeviceBLESteamController.TRANSPORT_BREDR;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation[Operation.CHR_WRITE.ordinal()] = HIDDeviceBLESteamController.TRANSPORT_LE;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation[Operation.ENABLE_NOTIFICATION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static class GattOperation {
        BluetoothGatt mGatt;
        Operation mOp;
        boolean mResult = true;
        UUID mUuid;
        byte[] mValue;

        private enum Operation {
            CHR_READ,
            CHR_WRITE,
            ENABLE_NOTIFICATION
        }

        private GattOperation(BluetoothGatt gatt, Operation operation, UUID uuid) {
            this.mGatt = gatt;
            this.mOp = operation;
            this.mUuid = uuid;
        }

        private GattOperation(BluetoothGatt gatt, Operation operation, UUID uuid, byte[] value) {
            this.mGatt = gatt;
            this.mOp = operation;
            this.mUuid = uuid;
            this.mValue = value;
        }

        public void run() {
            byte[] value;
            switch (AnonymousClass5.$SwitchMap$org$libsdl$app$HIDDeviceBLESteamController$GattOperation$Operation[this.mOp.ordinal()]) {
                case HIDDeviceBLESteamController.TRANSPORT_BREDR /*1*/:
                    if (this.mGatt.readCharacteristic(getCharacteristic(this.mUuid))) {
                        this.mResult = true;
                        break;
                    } else {
                        String str = HIDDeviceBLESteamController.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unable to read characteristic ");
                        sb.append(this.mUuid.toString());
                        Log.e(str, sb.toString());
                        this.mResult = false;
                        break;
                    }
                case HIDDeviceBLESteamController.TRANSPORT_LE /*2*/:
                    BluetoothGattCharacteristic chr = getCharacteristic(this.mUuid);
                    chr.setValue(this.mValue);
                    if (this.mGatt.writeCharacteristic(chr)) {
                        this.mResult = true;
                        break;
                    } else {
                        String str2 = HIDDeviceBLESteamController.TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Unable to write characteristic ");
                        sb2.append(this.mUuid.toString());
                        Log.e(str2, sb2.toString());
                        this.mResult = false;
                        break;
                    }
                case 3:
                    BluetoothGattCharacteristic chr2 = getCharacteristic(this.mUuid);
                    if (chr2 != null) {
                        BluetoothGattDescriptor cccd = chr2.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        if (cccd != null) {
                            int properties = chr2.getProperties();
                            if ((properties & 16) == 16) {
                                value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                            } else if ((properties & 32) == 32) {
                                value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                            } else {
                                Log.e(HIDDeviceBLESteamController.TAG, "Unable to start notifications on input characteristic");
                                this.mResult = false;
                                return;
                            }
                            this.mGatt.setCharacteristicNotification(chr2, true);
                            cccd.setValue(value);
                            if (this.mGatt.writeDescriptor(cccd)) {
                                this.mResult = true;
                                break;
                            } else {
                                String str3 = HIDDeviceBLESteamController.TAG;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("Unable to write descriptor ");
                                sb3.append(this.mUuid.toString());
                                Log.e(str3, sb3.toString());
                                this.mResult = false;
                                return;
                            }
                        }
                    }
                    break;
            }
        }

        public boolean finish() {
            return this.mResult;
        }

        private BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
            BluetoothGattService valveService = this.mGatt.getService(HIDDeviceBLESteamController.steamControllerService);
            if (valveService == null) {
                return null;
            }
            return valveService.getCharacteristic(uuid);
        }

        public static GattOperation readCharacteristic(BluetoothGatt gatt, UUID uuid) {
            return new GattOperation(gatt, Operation.CHR_READ, uuid);
        }

        public static GattOperation writeCharacteristic(BluetoothGatt gatt, UUID uuid, byte[] value) {
            return new GattOperation(gatt, Operation.CHR_WRITE, uuid, value);
        }

        public static GattOperation enableNotification(BluetoothGatt gatt, UUID uuid) {
            return new GattOperation(gatt, Operation.ENABLE_NOTIFICATION, uuid);
        }
    }

    public HIDDeviceBLESteamController(HIDDeviceManager manager, BluetoothDevice device) {
        this.mManager = manager;
        this.mDevice = device;
        this.mDeviceId = this.mManager.getDeviceIDForIdentifier(getIdentifier());
        this.mIsRegistered = false;
        this.mIsChromebook = this.mManager.getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
        this.mOperations = new LinkedList<>();
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mGatt = connectGatt();
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                this.checkConnectionForChromebookIssue();
            }
        }, 10000);
    }

    public String getIdentifier() {
        Object[] objArr = new Object[TRANSPORT_BREDR];
        objArr[TRANSPORT_AUTO] = this.mDevice.getAddress();
        return String.format("SteamController.%s", objArr);
    }

    public BluetoothGatt getGatt() {
        return this.mGatt;
    }

    private BluetoothGatt connectGatt(boolean managed) {
        try {
            Class[] clsArr = new Class[4];
            clsArr[TRANSPORT_AUTO] = Context.class;
            clsArr[TRANSPORT_BREDR] = Boolean.TYPE;
            clsArr[TRANSPORT_LE] = BluetoothGattCallback.class;
            clsArr[3] = Integer.TYPE;
            Method m = this.mDevice.getClass().getDeclaredMethod("connectGatt", clsArr);
            BluetoothDevice bluetoothDevice = this.mDevice;
            Object[] objArr = new Object[4];
            objArr[TRANSPORT_AUTO] = this.mManager.getContext();
            objArr[TRANSPORT_BREDR] = Boolean.valueOf(managed);
            objArr[TRANSPORT_LE] = this;
            objArr[3] = Integer.valueOf(TRANSPORT_LE);
            return (BluetoothGatt) m.invoke(bluetoothDevice, objArr);
        } catch (Exception e) {
            return this.mDevice.connectGatt(this.mManager.getContext(), managed, this);
        }
    }

    private BluetoothGatt connectGatt() {
        return connectGatt(false);
    }

    /* access modifiers changed from: protected */
    public int getConnectionState() {
        Context context = this.mManager.getContext();
        if (context == null) {
            return TRANSPORT_AUTO;
        }
        BluetoothManager btManager = (BluetoothManager) context.getSystemService("bluetooth");
        if (btManager == null) {
            return TRANSPORT_AUTO;
        }
        return btManager.getConnectionState(this.mDevice, 7);
    }

    public void reconnect() {
        if (getConnectionState() != TRANSPORT_LE) {
            this.mGatt.disconnect();
            this.mGatt = connectGatt();
        }
    }

    /* access modifiers changed from: protected */
    public void checkConnectionForChromebookIssue() {
        if (this.mIsChromebook) {
            switch (getConnectionState()) {
                case TRANSPORT_AUTO /*0*/:
                    Log.v(TAG, "Chromebook: We have either been disconnected, or the Chromebook BtGatt.ContextMap bug has bitten us.  Attempting a disconnect/reconnect, but we may not be able to recover.");
                    this.mIsReconnecting = true;
                    this.mGatt.disconnect();
                    this.mGatt = connectGatt(false);
                    break;
                case TRANSPORT_BREDR /*1*/:
                    Log.v(TAG, "Chromebook: We're still trying to connect.  Waiting a bit longer.");
                    break;
                case TRANSPORT_LE /*2*/:
                    if (!this.mIsConnected) {
                        Log.v(TAG, "Chromebook: We are in a very bad state; the controller shows as connected in the underlying Bluetooth layer, but we never received a callback.  Forcing a reconnect.");
                        this.mIsReconnecting = true;
                        this.mGatt.disconnect();
                        this.mGatt = connectGatt(false);
                        break;
                    } else if (!isRegistered()) {
                        if (this.mGatt.getServices().size() <= 0) {
                            Log.v(TAG, "Chromebook: We are connected to a controller, but never discovered services.  Trying to recover.");
                            this.mIsReconnecting = true;
                            this.mGatt.disconnect();
                            this.mGatt = connectGatt(false);
                            break;
                        } else {
                            Log.v(TAG, "Chromebook: We are connected to a controller, but never got our registration.  Trying to recover.");
                            probeService(this);
                            break;
                        }
                    } else {
                        Log.v(TAG, "Chromebook: We are connected, and registered.  Everything's good!");
                        return;
                    }
            }
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    this.checkConnectionForChromebookIssue();
                }
            }, 10000);
        }
    }

    private boolean isRegistered() {
        return this.mIsRegistered;
    }

    private void setRegistered() {
        this.mIsRegistered = true;
    }

    private boolean probeService(HIDDeviceBLESteamController controller) {
        if (isRegistered()) {
            return true;
        }
        if (!this.mIsConnected) {
            return false;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("probeService controller=");
        sb.append(controller);
        Log.v(str, sb.toString());
        for (BluetoothGattService service : this.mGatt.getServices()) {
            if (service.getUuid().equals(steamControllerService)) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Found Valve steam controller service ");
                sb2.append(service.getUuid());
                Log.v(str2, sb2.toString());
                for (BluetoothGattCharacteristic chr : service.getCharacteristics()) {
                    if (chr.getUuid().equals(inputCharacteristic)) {
                        Log.v(TAG, "Found input characteristic");
                        if (chr.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")) != null) {
                            enableNotification(chr.getUuid());
                        }
                    }
                }
                return true;
            }
        }
        if (this.mGatt.getServices().size() == 0 && this.mIsChromebook && !this.mIsReconnecting) {
            Log.e(TAG, "Chromebook: Discovered services were empty; this almost certainly means the BtGatt.ContextMap bug has bitten us.");
            this.mIsConnected = false;
            this.mIsReconnecting = true;
            this.mGatt.disconnect();
            this.mGatt = connectGatt(false);
        }
        return false;
    }

    private void finishCurrentGattOperation() {
        GattOperation op = null;
        synchronized (this.mOperations) {
            if (this.mCurrentOperation != null) {
                op = this.mCurrentOperation;
                this.mCurrentOperation = null;
            }
        }
        if (op != null && !op.finish()) {
            this.mOperations.addFirst(op);
        }
        executeNextGattOperation();
    }

    private void executeNextGattOperation() {
        synchronized (this.mOperations) {
            if (this.mCurrentOperation == null) {
                if (!this.mOperations.isEmpty()) {
                    this.mCurrentOperation = (GattOperation) this.mOperations.removeFirst();
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            synchronized (HIDDeviceBLESteamController.this.mOperations) {
                                if (HIDDeviceBLESteamController.this.mCurrentOperation == null) {
                                    Log.e(HIDDeviceBLESteamController.TAG, "Current operation null in executor?");
                                } else {
                                    HIDDeviceBLESteamController.this.mCurrentOperation.run();
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    private void queueGattOperation(GattOperation op) {
        synchronized (this.mOperations) {
            this.mOperations.add(op);
        }
        executeNextGattOperation();
    }

    private void enableNotification(UUID chrUuid) {
        queueGattOperation(GattOperation.enableNotification(this.mGatt, chrUuid));
    }

    public void writeCharacteristic(UUID uuid, byte[] value) {
        queueGattOperation(GattOperation.writeCharacteristic(this.mGatt, uuid, value));
    }

    public void readCharacteristic(UUID uuid) {
        queueGattOperation(GattOperation.readCharacteristic(this.mGatt, uuid));
    }

    public void onConnectionStateChange(BluetoothGatt g, int status, int newState) {
        this.mIsReconnecting = false;
        if (newState == TRANSPORT_LE) {
            this.mIsConnected = true;
            if (!isRegistered()) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        HIDDeviceBLESteamController.this.mGatt.discoverServices();
                    }
                });
            }
        } else if (newState == 0) {
            this.mIsConnected = false;
        }
    }

    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status != 0) {
            return;
        }
        if (gatt.getServices().size() == 0) {
            Log.v(TAG, "onServicesDiscovered returned zero services; something has gone horribly wrong down in Android's Bluetooth stack.");
            this.mIsReconnecting = true;
            this.mIsConnected = false;
            gatt.disconnect();
            this.mGatt = connectGatt(false);
            return;
        }
        probeService(this);
    }

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (characteristic.getUuid().equals(reportCharacteristic) && !this.mFrozen) {
            this.mManager.HIDDeviceFeatureReport(getId(), characteristic.getValue());
        }
        finishCurrentGattOperation();
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (characteristic.getUuid().equals(reportCharacteristic) && !isRegistered()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Registering Steam Controller with ID: ");
            sb.append(getId());
            Log.v(str, sb.toString());
            this.mManager.HIDDeviceConnected(getId(), getIdentifier(), getVendorId(), getProductId(), getSerialNumber(), getVersion(), getManufacturerName(), getProductName(), TRANSPORT_AUTO);
            setRegistered();
        }
        finishCurrentGattOperation();
    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(inputCharacteristic) && !this.mFrozen) {
            this.mManager.HIDDeviceInputReport(getId(), characteristic.getValue());
        }
    }

    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    }

    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        BluetoothGattCharacteristic chr = descriptor.getCharacteristic();
        if (chr.getUuid().equals(inputCharacteristic)) {
            BluetoothGattCharacteristic reportChr = chr.getService().getCharacteristic(reportCharacteristic);
            if (reportChr != null) {
                Log.v(TAG, "Writing report characteristic to enter valve mode");
                reportChr.setValue(enterValveMode);
                gatt.writeCharacteristic(reportChr);
            }
        }
        finishCurrentGattOperation();
    }

    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    }

    public int getId() {
        return this.mDeviceId;
    }

    public int getVendorId() {
        return 10462;
    }

    public int getProductId() {
        return 4358;
    }

    public String getSerialNumber() {
        return "12345";
    }

    public int getVersion() {
        return TRANSPORT_AUTO;
    }

    public String getManufacturerName() {
        return "Valve Corporation";
    }

    public String getProductName() {
        return "Steam Controller";
    }

    public boolean open() {
        return true;
    }

    public int sendFeatureReport(byte[] report) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted sendFeatureReport before Steam Controller is registered!");
            if (this.mIsConnected) {
                probeService(this);
            }
            return -1;
        }
        writeCharacteristic(reportCharacteristic, Arrays.copyOfRange(report, TRANSPORT_BREDR, report.length - TRANSPORT_BREDR));
        return report.length;
    }

    public int sendOutputReport(byte[] report) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted sendOutputReport before Steam Controller is registered!");
            if (this.mIsConnected) {
                probeService(this);
            }
            return -1;
        }
        writeCharacteristic(reportCharacteristic, report);
        return report.length;
    }

    public boolean getFeatureReport(byte[] report) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted getFeatureReport before Steam Controller is registered!");
            if (this.mIsConnected) {
                probeService(this);
            }
            return false;
        }
        readCharacteristic(reportCharacteristic);
        return true;
    }

    public void close() {
    }

    public void setFrozen(boolean frozen) {
        this.mFrozen = frozen;
    }

    public void shutdown() {
        close();
        BluetoothGatt g = this.mGatt;
        if (g != null) {
            g.disconnect();
            g.close();
            this.mGatt = null;
        }
        this.mManager = null;
        this.mIsRegistered = false;
        this.mIsConnected = false;
        this.mOperations.clear();
    }
}
