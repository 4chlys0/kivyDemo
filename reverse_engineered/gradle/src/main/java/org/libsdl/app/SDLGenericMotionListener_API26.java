package org.libsdl.app;

import android.os.Build.VERSION;
import android.view.MotionEvent;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API26 extends SDLGenericMotionListener_API24 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API26() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0022, code lost:
        if (r0 != 16777232) goto L_0x006f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onGenericMotion(android.view.View r7, android.view.MotionEvent r8) {
        /*
            r6 = this;
            int r0 = r8.getSource()
            r1 = 513(0x201, float:7.19E-43)
            if (r0 == r1) goto L_0x0070
            r1 = 1025(0x401, float:1.436E-42)
            if (r0 == r1) goto L_0x0070
            r1 = 8194(0x2002, float:1.1482E-41)
            r2 = 9
            r3 = 10
            r4 = 1
            r5 = 0
            if (r0 == r1) goto L_0x004a
            r1 = 12290(0x3002, float:1.7222E-41)
            if (r0 == r1) goto L_0x004a
            r1 = 131076(0x20004, float:1.83677E-40)
            if (r0 == r1) goto L_0x0025
            r1 = 16777232(0x1000010, float:2.3509932E-38)
            if (r0 == r1) goto L_0x0070
            goto L_0x006f
        L_0x0025:
            boolean r0 = org.libsdl.app.SDLActivity.mSeparateMouseAndTouch
            if (r0 != 0) goto L_0x002a
            goto L_0x006f
        L_0x002a:
            int r0 = r8.getActionMasked()
            switch(r0) {
                case 7: goto L_0x003e;
                case 8: goto L_0x0032;
                default: goto L_0x0031;
            }
        L_0x0031:
            goto L_0x006f
        L_0x0032:
            float r1 = r8.getAxisValue(r3, r5)
            float r2 = r8.getAxisValue(r2, r5)
            org.libsdl.app.SDLActivity.onNativeMouse(r5, r0, r1, r2, r5)
            return r4
        L_0x003e:
            float r1 = r8.getX(r5)
            float r2 = r8.getY(r5)
            org.libsdl.app.SDLActivity.onNativeMouse(r5, r0, r1, r2, r4)
            return r4
        L_0x004a:
            boolean r0 = org.libsdl.app.SDLActivity.mSeparateMouseAndTouch
            if (r0 != 0) goto L_0x004f
            goto L_0x006f
        L_0x004f:
            int r0 = r8.getActionMasked()
            switch(r0) {
                case 7: goto L_0x0063;
                case 8: goto L_0x0057;
                default: goto L_0x0056;
            }
        L_0x0056:
            goto L_0x006f
        L_0x0057:
            float r1 = r8.getAxisValue(r3, r5)
            float r2 = r8.getAxisValue(r2, r5)
            org.libsdl.app.SDLActivity.onNativeMouse(r5, r0, r1, r2, r5)
            return r4
        L_0x0063:
            float r1 = r8.getX(r5)
            float r2 = r8.getY(r5)
            org.libsdl.app.SDLActivity.onNativeMouse(r5, r0, r1, r2, r5)
            return r4
        L_0x006f:
            return r5
        L_0x0070:
            boolean r0 = org.libsdl.app.SDLControllerManager.handleJoystickMotionEvent(r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLGenericMotionListener_API26.onGenericMotion(android.view.View, android.view.MotionEvent):boolean");
    }

    public boolean supportsRelativeMouse() {
        return !SDLActivity.isDeXMode() || VERSION.SDK_INT >= 27;
    }

    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        if (SDLActivity.isDeXMode() && VERSION.SDK_INT < 27) {
            return false;
        }
        if (enabled) {
            SDLActivity.getContentView().requestPointerCapture();
        } else {
            SDLActivity.getContentView().releasePointerCapture();
        }
        this.mRelativeModeEnabled = enabled;
        return true;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
        if (this.mRelativeModeEnabled && !SDLActivity.isDeXMode()) {
            SDLActivity.getContentView().requestPointerCapture();
        }
    }

    public float getEventX(MotionEvent event) {
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        return event.getY(0);
    }
}
