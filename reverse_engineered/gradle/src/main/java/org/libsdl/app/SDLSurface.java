package org.libsdl.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity.NativeState;

/* compiled from: SDLActivity */
class SDLSurface extends SurfaceView implements Callback, OnKeyListener, OnTouchListener, SensorEventListener {
    protected static Display mDisplay;
    protected static float mHeight;
    protected static SensorManager mSensorManager;
    protected static float mWidth;

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        mSensorManager = (SensorManager) context.getSystemService("sensor");
        if (VERSION.SDK_INT >= 12) {
            setOnGenericMotionListener(SDLActivity.getMotionListener());
        }
        mWidth = 1.0f;
        mHeight = 1.0f;
    }

    public void handlePause() {
        enableSensor(1, false);
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("SDL", "surfaceCreated()");
        holder.setType(2);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("SDL", "surfaceDestroyed()");
        SDLActivity.mNextNativeState = NativeState.PAUSED;
        SDLActivity.handleNativeState();
        SDLActivity.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
    }

    /* JADX INFO: finally extract failed */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int i = format;
        int i2 = width;
        int i3 = height;
        Log.v("SDL", "surfaceChanged()");
        if (SDLActivity.mSingleton != null) {
            int sdlFormat = 353701890;
            switch (i) {
                case 1:
                    Log.v("SDL", "pixel format RGBA_8888");
                    sdlFormat = 373694468;
                    break;
                case 2:
                    Log.v("SDL", "pixel format RGBX_8888");
                    sdlFormat = 371595268;
                    break;
                case 3:
                    Log.v("SDL", "pixel format RGB_888");
                    sdlFormat = 370546692;
                    break;
                case 4:
                    Log.v("SDL", "pixel format RGB_565");
                    sdlFormat = 353701890;
                    break;
                case 6:
                    Log.v("SDL", "pixel format RGBA_5551");
                    sdlFormat = 356782082;
                    break;
                case 7:
                    Log.v("SDL", "pixel format RGBA_4444");
                    sdlFormat = 356651010;
                    break;
                case 8:
                    Log.v("SDL", "pixel format A_8");
                    break;
                case 9:
                    Log.v("SDL", "pixel format L_8");
                    break;
                case 10:
                    Log.v("SDL", "pixel format LA_88");
                    break;
                case 11:
                    Log.v("SDL", "pixel format RGB_332");
                    sdlFormat = 336660481;
                    break;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("pixel format unknown ");
                    sb.append(i);
                    Log.v("SDL", sb.toString());
                    break;
            }
            int sdlFormat2 = sdlFormat;
            mWidth = (float) i2;
            mHeight = (float) i3;
            int nDeviceWidth = i2;
            int nDeviceHeight = i3;
            try {
                if (VERSION.SDK_INT >= 17) {
                    DisplayMetrics realMetrics = new DisplayMetrics();
                    mDisplay.getRealMetrics(realMetrics);
                    nDeviceWidth = realMetrics.widthPixels;
                    nDeviceHeight = realMetrics.heightPixels;
                }
            } catch (Throwable th) {
            }
            int nDeviceWidth2 = nDeviceWidth;
            int nDeviceHeight2 = nDeviceHeight;
            synchronized (SDLActivity.getContext()) {
                try {
                    SDLActivity.getContext().notifyAll();
                } catch (Throwable th2) {
                    while (true) {
                        throw th2;
                    }
                }
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Window size: ");
            sb2.append(i2);
            sb2.append("x");
            sb2.append(i3);
            Log.v("SDL", sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Device size: ");
            sb3.append(nDeviceWidth2);
            sb3.append("x");
            sb3.append(nDeviceHeight2);
            Log.v("SDL", sb3.toString());
            SDLActivity.onNativeResize(i2, i3, nDeviceWidth2, nDeviceHeight2, sdlFormat2, mDisplay.getRefreshRate());
            boolean skip = false;
            int requestedOrientation = SDLActivity.mSingleton.getRequestedOrientation();
            if (requestedOrientation != -1) {
                if (requestedOrientation == 1 || requestedOrientation == 7) {
                    if (mWidth > mHeight) {
                        skip = true;
                    }
                } else if ((requestedOrientation == 0 || requestedOrientation == 6) && mWidth < mHeight) {
                    skip = true;
                }
            }
            if (skip) {
                if (((double) Math.max(mWidth, mHeight)) / ((double) Math.min(mWidth, mHeight)) < 1.2d) {
                    Log.v("SDL", "Don't skip on such aspect-ratio. Could be a square resolution.");
                    skip = false;
                }
            }
            if (skip) {
                Log.v("SDL", "Skip .. Surface is not ready.");
                SDLActivity.mIsSurfaceReady = false;
                return;
            }
            SDLActivity.mIsSurfaceReady = true;
            SDLActivity.onNativeSurfaceChanged();
            SDLActivity.handleNativeState();
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (SDLControllerManager.isDeviceSDLJoystick(event.getDeviceId())) {
            if (event.getAction() == 0) {
                if (SDLControllerManager.onNativePadDown(event.getDeviceId(), keyCode) == 0) {
                    return true;
                }
            } else if (event.getAction() == 1 && SDLControllerManager.onNativePadUp(event.getDeviceId(), keyCode) == 0) {
                return true;
            }
        }
        if ((event.getSource() & 257) != 0) {
            if (event.getAction() == 0) {
                if (SDLActivity.isTextInputEvent(event)) {
                    SDLInputConnection.nativeCommitText(String.valueOf((char) event.getUnicodeChar()), 1);
                }
                SDLActivity.onNativeKeyDown(keyCode);
                return true;
            } else if (event.getAction() == 1) {
                SDLActivity.onNativeKeyUp(keyCode);
                return true;
            }
        }
        if ((event.getSource() & 8194) != 0 && (keyCode == 4 || keyCode == 125)) {
            switch (event.getAction()) {
                case 0:
                case 1:
                    return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00cc, code lost:
        if (r2 != -1) goto L_0x00d2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ce, code lost:
        r2 = r20.getActionIndex();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00d2, code lost:
        r13 = r2;
        r14 = r1.getPointerId(r13);
        r15 = r1.getX(r13) / mWidth;
        r16 = r1.getY(r13) / mHeight;
        r2 = r1.getPressure(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ed, code lost:
        if (r2 <= 1.0f) goto L_0x00f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00ef, code lost:
        r2 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00f1, code lost:
        org.libsdl.app.SDLActivity.onNativeTouch(r8, r14, r10, r15, r16, r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.View r19, android.view.MotionEvent r20) {
        /*
            r18 = this;
            r1 = r20
            int r8 = r20.getDeviceId()
            int r9 = r20.getPointerCount()
            int r10 = r20.getActionMasked()
            r2 = -1
            int r3 = r20.getSource()
            r11 = 1
            r4 = 8194(0x2002, float:1.1482E-41)
            if (r3 == r4) goto L_0x0020
            int r3 = r20.getSource()
            r4 = 12290(0x3002, float:1.7222E-41)
            if (r3 != r4) goto L_0x005f
        L_0x0020:
            boolean r3 = org.libsdl.app.SDLActivity.mSeparateMouseAndTouch
            if (r3 == 0) goto L_0x005f
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 14
            if (r3 >= r4) goto L_0x002c
            r3 = 1
            goto L_0x004a
        L_0x002c:
            java.lang.Class r3 = r20.getClass()     // Catch:{ Exception -> 0x0046 }
            java.lang.String r4 = "getButtonState"
            r5 = 0
            java.lang.Class[] r6 = new java.lang.Class[r5]     // Catch:{ Exception -> 0x0046 }
            java.lang.reflect.Method r3 = r3.getMethod(r4, r6)     // Catch:{ Exception -> 0x0046 }
            java.lang.Object[] r4 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0046 }
            java.lang.Object r3 = r3.invoke(r1, r4)     // Catch:{ Exception -> 0x0046 }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x0046 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x0046 }
            goto L_0x004a
        L_0x0046:
            r0 = move-exception
            r3 = r0
            r3 = 1
        L_0x004a:
            org.libsdl.app.SDLGenericMotionListener_API12 r4 = org.libsdl.app.SDLActivity.getMotionListener()
            float r5 = r4.getEventX(r1)
            float r6 = r4.getEventY(r1)
            boolean r7 = r4.inRelativeMode()
            org.libsdl.app.SDLActivity.onNativeMouse(r3, r10, r5, r6, r7)
            goto L_0x00fd
        L_0x005f:
            r12 = 1065353216(0x3f800000, float:1.0)
            switch(r10) {
                case 0: goto L_0x00ca;
                case 1: goto L_0x00ca;
                case 2: goto L_0x0098;
                case 3: goto L_0x0066;
                case 4: goto L_0x0064;
                case 5: goto L_0x00cb;
                case 6: goto L_0x00cb;
                default: goto L_0x0064;
            }
        L_0x0064:
            goto L_0x00fd
        L_0x0066:
            r2 = 0
            r13 = r2
        L_0x0068:
            if (r13 >= r9) goto L_0x00fe
            int r14 = r1.getPointerId(r13)
            float r2 = r1.getX(r13)
            float r3 = mWidth
            float r15 = r2 / r3
            float r2 = r1.getY(r13)
            float r3 = mHeight
            float r16 = r2 / r3
            float r2 = r1.getPressure(r13)
            int r3 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r3 <= 0) goto L_0x0088
            r2 = 1065353216(0x3f800000, float:1.0)
        L_0x0088:
            r17 = r2
            r4 = 1
            r2 = r8
            r3 = r14
            r5 = r15
            r6 = r16
            r7 = r17
            org.libsdl.app.SDLActivity.onNativeTouch(r2, r3, r4, r5, r6, r7)
            int r13 = r13 + 1
            goto L_0x0068
        L_0x0098:
            r2 = 0
            r13 = r2
        L_0x009a:
            if (r13 >= r9) goto L_0x00fe
            int r14 = r1.getPointerId(r13)
            float r2 = r1.getX(r13)
            float r3 = mWidth
            float r15 = r2 / r3
            float r2 = r1.getY(r13)
            float r3 = mHeight
            float r16 = r2 / r3
            float r2 = r1.getPressure(r13)
            int r3 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r3 <= 0) goto L_0x00ba
            r2 = 1065353216(0x3f800000, float:1.0)
        L_0x00ba:
            r17 = r2
            r2 = r8
            r3 = r14
            r4 = r10
            r5 = r15
            r6 = r16
            r7 = r17
            org.libsdl.app.SDLActivity.onNativeTouch(r2, r3, r4, r5, r6, r7)
            int r13 = r13 + 1
            goto L_0x009a
        L_0x00ca:
            r2 = 0
        L_0x00cb:
            r3 = -1
            if (r2 != r3) goto L_0x00d2
            int r2 = r20.getActionIndex()
        L_0x00d2:
            r13 = r2
            int r14 = r1.getPointerId(r13)
            float r2 = r1.getX(r13)
            float r3 = mWidth
            float r15 = r2 / r3
            float r2 = r1.getY(r13)
            float r3 = mHeight
            float r16 = r2 / r3
            float r2 = r1.getPressure(r13)
            int r3 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r3 <= 0) goto L_0x00f1
            r2 = 1065353216(0x3f800000, float:1.0)
        L_0x00f1:
            r12 = r2
            r2 = r8
            r3 = r14
            r4 = r10
            r5 = r15
            r6 = r16
            r7 = r12
            org.libsdl.app.SDLActivity.onNativeTouch(r2, r3, r4, r5, r6, r7)
            goto L_0x00fe
        L_0x00fd:
            r13 = r2
        L_0x00fe:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLSurface.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    public void enableSensor(int sensortype, boolean enabled) {
        if (enabled) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(sensortype), 1, null);
        } else {
            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(sensortype));
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float x;
        float y;
        int newOrientation;
        if (event.sensor.getType() == 1) {
            switch (mDisplay.getRotation()) {
                case 1:
                    y = -event.values[1];
                    x = event.values[0];
                    newOrientation = 1;
                    break;
                case 2:
                    y = -event.values[1];
                    x = -event.values[0];
                    newOrientation = 4;
                    break;
                case 3:
                    y = event.values[1];
                    x = -event.values[0];
                    newOrientation = 2;
                    break;
                default:
                    newOrientation = 3;
                    float f = event.values[0];
                    x = event.values[1];
                    y = f;
                    break;
            }
            if (newOrientation != SDLActivity.mCurrentOrientation) {
                SDLActivity.mCurrentOrientation = newOrientation;
                SDLActivity.onNativeOrientationChanged(newOrientation);
            }
            SDLActivity.onNativeAccel((-y) / 9.80665f, x / 9.80665f, event.values[2] / 9.80665f);
        }
    }

    public boolean onCapturedPointerEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case 2:
            case 7:
                SDLActivity.onNativeMouse(0, action, event.getX(0), event.getY(0), true);
                return true;
            case 8:
                SDLActivity.onNativeMouse(0, action, event.getAxisValue(10, 0), event.getAxisValue(9, 0), false);
                return true;
            case 11:
            case 12:
                if (action == 11) {
                    action = 0;
                } else if (action == 12) {
                    action = 1;
                }
                SDLActivity.onNativeMouse(event.getButtonState(), action, event.getX(0), event.getY(0), true);
                return true;
            default:
                return false;
        }
    }
}
