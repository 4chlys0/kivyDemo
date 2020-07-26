package org.kivy.android;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class PythonService extends Service implements Runnable {
    public static PythonService mService = null;
    private String androidArgument;
    private String androidPrivate;
    private boolean autoRestartService = false;
    private String pythonHome;
    private String pythonName;
    private String pythonPath;
    private String pythonServiceArgument;
    private Thread pythonThread = null;
    private String serviceEntrypoint;
    private Intent startIntent = null;

    public static native void nativeStart(String str, String str2, String str3, String str4, String str5, String str6, String str7);

    public void setAutoRestartService(boolean restart) {
        this.autoRestartService = restart;
    }

    public int startType() {
        return 2;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.pythonThread != null) {
            Log.v("python service", "service exists, do not start again");
            return 2;
        }
        this.startIntent = intent;
        Bundle extras = intent.getExtras();
        this.androidPrivate = extras.getString("androidPrivate");
        this.androidArgument = extras.getString("androidArgument");
        this.serviceEntrypoint = extras.getString("serviceEntrypoint");
        this.pythonName = extras.getString("pythonName");
        this.pythonHome = extras.getString("pythonHome");
        this.pythonPath = extras.getString("pythonPath");
        boolean serviceStartAsForeground = extras.getString("serviceStartAsForeground").equals("true");
        this.pythonServiceArgument = extras.getString("pythonServiceArgument");
        this.pythonThread = new Thread(this);
        this.pythonThread.start();
        if (serviceStartAsForeground) {
            doStartForeground(extras);
        }
        return startType();
    }

    /* access modifiers changed from: protected */
    public int getServiceId() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public void doStartForeground(Bundle extras) {
        Notification notification;
        Bundle bundle = extras;
        String serviceTitle = bundle.getString("serviceTitle");
        String serviceDescription = bundle.getString("serviceDescription");
        Context context = getApplicationContext();
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, PythonActivity.class), 134217728);
        if (VERSION.SDK_INT < 26) {
            notification = new Notification(context.getApplicationInfo().icon, serviceTitle, System.currentTimeMillis());
            try {
                notification.getClass().getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class}).invoke(notification, new Object[]{context, serviceTitle, serviceDescription, pIntent});
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            }
        } else {
            String NOTIFICATION_CHANNEL_ID = "org.kivy.p4a";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Background Service", 0);
            chan.setLightColor(-16776961);
            chan.setLockscreenVisibility(0);
            ((NotificationManager) getSystemService("notification")).createNotificationChannel(chan);
            Builder builder = new Builder(context, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(serviceTitle);
            builder.setContentText(serviceDescription);
            builder.setContentIntent(pIntent);
            builder.setSmallIcon(context.getApplicationInfo().icon);
            notification = builder.build();
        }
        startForeground(getServiceId(), notification);
    }

    public void onDestroy() {
        super.onDestroy();
        this.pythonThread = null;
        if (this.autoRestartService && this.startIntent != null) {
            Log.v("python service", "service restart requested");
            startService(this.startIntent);
        }
        Process.killProcess(Process.myPid());
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFilesDir().getAbsolutePath());
        sb.append("/app");
        PythonUtil.loadLibraries(new File(sb.toString()), new File(getApplicationInfo().nativeLibraryDir));
        mService = this;
        nativeStart(this.androidPrivate, this.androidArgument, this.serviceEntrypoint, this.pythonName, this.pythonHome, this.pythonPath, this.pythonServiceArgument);
        stopSelf();
    }
}
