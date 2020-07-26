package org.libsdl.app;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Build.VERSION;
import android.util.Log;
import java.util.Arrays;
import org.kamranzafar.jtar.TarHeader;
import org.test.myapp.BuildConfig;

class HIDDeviceUSB implements HIDDevice {
    private static final String TAG = "hidapi";
    protected UsbDeviceConnection mConnection;
    protected UsbDevice mDevice;
    protected int mDeviceId;
    protected boolean mFrozen;
    protected UsbEndpoint mInputEndpoint;
    protected InputThread mInputThread;
    protected int mInterface;
    protected HIDDeviceManager mManager;
    protected UsbEndpoint mOutputEndpoint;
    protected boolean mRunning = false;

    protected class InputThread extends Thread {
        protected InputThread() {
        }

        public void run() {
            byte[] data;
            int packetSize = HIDDeviceUSB.this.mInputEndpoint.getMaxPacketSize();
            byte[] packet = new byte[packetSize];
            while (HIDDeviceUSB.this.mRunning) {
                try {
                    int r = HIDDeviceUSB.this.mConnection.bulkTransfer(HIDDeviceUSB.this.mInputEndpoint, packet, packetSize, 1000);
                    if (r > 0) {
                        if (r == packetSize) {
                            data = packet;
                        } else {
                            data = Arrays.copyOfRange(packet, 0, r);
                        }
                        if (!HIDDeviceUSB.this.mFrozen) {
                            HIDDeviceUSB.this.mManager.HIDDeviceInputReport(HIDDeviceUSB.this.mDeviceId, data);
                        }
                    }
                } catch (Exception e) {
                    String str = HIDDeviceUSB.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Exception in UsbDeviceConnection bulktransfer: ");
                    sb.append(e);
                    Log.v(str, sb.toString());
                    return;
                }
            }
        }
    }

    public HIDDeviceUSB(HIDDeviceManager manager, UsbDevice usbDevice, int interface_number) {
        this.mManager = manager;
        this.mDevice = usbDevice;
        this.mInterface = interface_number;
        this.mDeviceId = manager.getDeviceIDForIdentifier(getIdentifier());
    }

    public String getIdentifier() {
        return String.format("%s/%x/%x", new Object[]{this.mDevice.getDeviceName(), Integer.valueOf(this.mDevice.getVendorId()), Integer.valueOf(this.mDevice.getProductId())});
    }

    public int getId() {
        return this.mDeviceId;
    }

    public int getVendorId() {
        return this.mDevice.getVendorId();
    }

    public int getProductId() {
        return this.mDevice.getProductId();
    }

    public String getSerialNumber() {
        String result = null;
        if (VERSION.SDK_INT >= 21) {
            result = this.mDevice.getSerialNumber();
        }
        if (result == null) {
            return BuildConfig.FLAVOR;
        }
        return result;
    }

    public int getVersion() {
        return 0;
    }

    public String getManufacturerName() {
        String result = null;
        if (VERSION.SDK_INT >= 21) {
            result = this.mDevice.getManufacturerName();
        }
        if (result != null) {
            return result;
        }
        return String.format("%x", new Object[]{Integer.valueOf(getVendorId())});
    }

    public String getProductName() {
        String result = null;
        if (VERSION.SDK_INT >= 21) {
            result = this.mDevice.getProductName();
        }
        if (result != null) {
            return result;
        }
        return String.format("%x", new Object[]{Integer.valueOf(getProductId())});
    }

    public UsbDevice getDevice() {
        return this.mDevice;
    }

    public String getDeviceName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getManufacturerName());
        sb.append(" ");
        sb.append(getProductName());
        sb.append("(0x");
        sb.append(String.format("%x", new Object[]{Integer.valueOf(getVendorId())}));
        sb.append("/0x");
        sb.append(String.format("%x", new Object[]{Integer.valueOf(getProductId())}));
        sb.append(")");
        return sb.toString();
    }

    public boolean open() {
        this.mConnection = this.mManager.getUSBManager().openDevice(this.mDevice);
        if (this.mConnection == null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to open USB device ");
            sb.append(getDeviceName());
            Log.w(str, sb.toString());
            return false;
        }
        for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
            if (!this.mConnection.claimInterface(this.mDevice.getInterface(i), true)) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Failed to claim interfaces on USB device ");
                sb2.append(getDeviceName());
                Log.w(str2, sb2.toString());
                close();
                return false;
            }
        }
        UsbInterface iface = this.mDevice.getInterface(this.mInterface);
        for (int j = 0; j < iface.getEndpointCount(); j++) {
            UsbEndpoint endpt = iface.getEndpoint(j);
            int direction = endpt.getDirection();
            if (direction != 0) {
                if (direction == 128 && this.mInputEndpoint == null) {
                    this.mInputEndpoint = endpt;
                }
            } else if (this.mOutputEndpoint == null) {
                this.mOutputEndpoint = endpt;
            }
        }
        if (this.mInputEndpoint == null || this.mOutputEndpoint == null) {
            String str3 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Missing required endpoint on USB device ");
            sb3.append(getDeviceName());
            Log.w(str3, sb3.toString());
            close();
            return false;
        }
        this.mRunning = true;
        this.mInputThread = new InputThread();
        this.mInputThread.start();
        return true;
    }

    public int sendFeatureReport(byte[] report) {
        int offset = 0;
        int length = report.length;
        boolean skipped_report_id = false;
        byte report_number = report[0];
        if (report_number == 0) {
            offset = 0 + 1;
            length--;
            skipped_report_id = true;
        }
        int res = this.mConnection.controlTransfer(33, 9, report_number | TarHeader.LF_OLDNORM, 0, report, offset, length, 1000);
        if (res < 0) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("sendFeatureReport() returned ");
            sb.append(res);
            sb.append(" on device ");
            sb.append(getDeviceName());
            Log.w(str, sb.toString());
            return -1;
        }
        if (skipped_report_id) {
            length++;
        }
        return length;
    }

    public int sendOutputReport(byte[] report) {
        int r = this.mConnection.bulkTransfer(this.mOutputEndpoint, report, report.length, 1000);
        if (r != report.length) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("sendOutputReport() returned ");
            sb.append(r);
            sb.append(" on device ");
            sb.append(getDeviceName());
            Log.w(str, sb.toString());
        }
        return r;
    }

    public boolean getFeatureReport(byte[] report) {
        byte[] data;
        byte[] bArr = report;
        int offset = 0;
        int length = bArr.length;
        boolean skipped_report_id = false;
        byte report_number = bArr[0];
        if (report_number == 0) {
            offset = 0 + 1;
            length--;
            skipped_report_id = true;
        }
        int length2 = length;
        boolean skipped_report_id2 = skipped_report_id;
        int res = this.mConnection.controlTransfer(161, 1, report_number | TarHeader.LF_OLDNORM, 0, bArr, offset, length2, 1000);
        if (res < 0) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getFeatureReport() returned ");
            sb.append(res);
            sb.append(" on device ");
            sb.append(getDeviceName());
            Log.w(str, sb.toString());
            return false;
        }
        if (skipped_report_id2) {
            res++;
            length2++;
        }
        if (res == length2) {
            data = bArr;
        } else {
            data = Arrays.copyOfRange(bArr, 0, res);
        }
        this.mManager.HIDDeviceFeatureReport(this.mDeviceId, data);
        return true;
    }

    public void close() {
        this.mRunning = false;
        if (this.mInputThread != null) {
            while (this.mInputThread.isAlive()) {
                this.mInputThread.interrupt();
                try {
                    this.mInputThread.join();
                } catch (InterruptedException e) {
                }
            }
            this.mInputThread = null;
        }
        if (this.mConnection != null) {
            for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                this.mConnection.releaseInterface(this.mDevice.getInterface(i));
            }
            this.mConnection.close();
            this.mConnection = null;
        }
    }

    public void shutdown() {
        close();
        this.mManager = null;
    }

    public void setFrozen(boolean frozen) {
        this.mFrozen = frozen;
    }
}
