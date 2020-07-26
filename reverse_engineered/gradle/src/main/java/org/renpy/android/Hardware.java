package org.renpy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.util.List;
import org.kivy.android.PythonActivity;
import org.test.myapp.BuildConfig;

public class Hardware {
    public static generic3AxisSensor accelerometerSensor = null;
    static Context context;
    public static final float[] defaultRv = {0.0f, 0.0f, 0.0f};
    static List<ScanResult> latestResult;
    public static generic3AxisSensor magneticFieldSensor = null;
    public static DisplayMetrics metrics = new DisplayMetrics();
    public static boolean network_state = false;
    public static generic3AxisSensor orientationSensor = null;
    static View view;

    public static class generic3AxisSensor implements SensorEventListener {
        private final Sensor sSensor = this.sSensorManager.getDefaultSensor(this.sSensorType);
        SensorEvent sSensorEvent;
        private final SensorManager sSensorManager = ((SensorManager) Hardware.context.getSystemService("sensor"));
        private final int sSensorType;

        public generic3AxisSensor(int sensorType) {
            this.sSensorType = sensorType;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            this.sSensorEvent = event;
        }

        public void changeStatus(boolean enable) {
            if (enable) {
                this.sSensorManager.registerListener(this, this.sSensor, 3);
            } else {
                this.sSensorManager.unregisterListener(this, this.sSensor);
            }
        }

        public float[] readSensor() {
            if (this.sSensorEvent != null) {
                return this.sSensorEvent.values;
            }
            return Hardware.defaultRv;
        }
    }

    public static void vibrate(double s) {
        Vibrator v = (Vibrator) context.getSystemService("vibrator");
        if (v != null) {
            v.vibrate((long) ((int) (1000.0d * s)));
        }
    }

    public static String getHardwareSensors() {
        List<Sensor> allSensors = ((SensorManager) context.getSystemService("sensor")).getSensorList(-1);
        if (allSensors == null) {
            return BuildConfig.FLAVOR;
        }
        String resultString = BuildConfig.FLAVOR;
        for (Sensor s : allSensors) {
            StringBuilder sb = new StringBuilder();
            sb.append(resultString);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Name=");
            sb2.append(s.getName());
            sb.append(String.format(sb2.toString(), new Object[0]));
            String resultString2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(resultString2);
            StringBuilder sb4 = new StringBuilder();
            sb4.append(",Vendor=");
            sb4.append(s.getVendor());
            sb3.append(String.format(sb4.toString(), new Object[0]));
            String resultString3 = sb3.toString();
            StringBuilder sb5 = new StringBuilder();
            sb5.append(resultString3);
            StringBuilder sb6 = new StringBuilder();
            sb6.append(",Version=");
            sb6.append(s.getVersion());
            sb5.append(String.format(sb6.toString(), new Object[0]));
            String resultString4 = sb5.toString();
            StringBuilder sb7 = new StringBuilder();
            sb7.append(resultString4);
            StringBuilder sb8 = new StringBuilder();
            sb8.append(",MaximumRange=");
            sb8.append(s.getMaximumRange());
            sb7.append(String.format(sb8.toString(), new Object[0]));
            String resultString5 = sb7.toString();
            StringBuilder sb9 = new StringBuilder();
            sb9.append(resultString5);
            StringBuilder sb10 = new StringBuilder();
            sb10.append(",Power=");
            sb10.append(s.getPower());
            sb9.append(String.format(sb10.toString(), new Object[0]));
            String resultString6 = sb9.toString();
            StringBuilder sb11 = new StringBuilder();
            sb11.append(resultString6);
            StringBuilder sb12 = new StringBuilder();
            sb12.append(",Type=");
            sb12.append(s.getType());
            sb12.append("\n");
            sb11.append(String.format(sb12.toString(), new Object[0]));
            resultString = sb11.toString();
        }
        return resultString;
    }

    public static void accelerometerEnable(boolean enable) {
        if (accelerometerSensor == null) {
            accelerometerSensor = new generic3AxisSensor(1);
        }
        accelerometerSensor.changeStatus(enable);
    }

    public static float[] accelerometerReading() {
        if (accelerometerSensor == null) {
            return defaultRv;
        }
        return accelerometerSensor.readSensor();
    }

    public static void orientationSensorEnable(boolean enable) {
        if (orientationSensor == null) {
            orientationSensor = new generic3AxisSensor(3);
        }
        orientationSensor.changeStatus(enable);
    }

    public static float[] orientationSensorReading() {
        if (orientationSensor == null) {
            return defaultRv;
        }
        return orientationSensor.readSensor();
    }

    public static void magneticFieldSensorEnable(boolean enable) {
        if (magneticFieldSensor == null) {
            magneticFieldSensor = new generic3AxisSensor(2);
        }
        magneticFieldSensor.changeStatus(enable);
    }

    public static float[] magneticFieldSensorReading() {
        if (magneticFieldSensor == null) {
            return defaultRv;
        }
        return magneticFieldSensor.readSensor();
    }

    public static int getDPI() {
        PythonActivity.mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public static void hideKeyboard() {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void enableWifiScanner() {
        IntentFilter i = new IntentFilter();
        i.addAction("android.net.wifi.SCAN_RESULTS");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                Hardware.latestResult = ((WifiManager) c.getSystemService("wifi")).getScanResults();
            }
        }, i);
    }

    public static String scanWifi() {
        if (latestResult == null) {
            return BuildConfig.FLAVOR;
        }
        String latestResultString = BuildConfig.FLAVOR;
        for (ScanResult result : latestResult) {
            StringBuilder sb = new StringBuilder();
            sb.append(latestResultString);
            sb.append(String.format("%s\t%s\t%d\n", new Object[]{result.SSID, result.BSSID, Integer.valueOf(result.level)}));
            latestResultString = sb.toString();
        }
        return latestResultString;
    }

    public static boolean checkNetwork() {
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            return false;
        }
        return true;
    }

    public static void registerNetworkCheck() {
        IntentFilter i = new IntentFilter();
        i.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                Hardware.network_state = Hardware.checkNetwork();
            }
        }, i);
    }
}
