package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View.OnGenericMotionListener;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API12 implements OnGenericMotionListener {
    SDLGenericMotionListener_API12() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0014, code lost:
        if (r0 != 16777232) goto L_0x0041;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onGenericMotion(android.view.View r6, android.view.MotionEvent r7) {
        /*
            r5 = this;
            int r0 = r7.getSource()
            r1 = 513(0x201, float:7.19E-43)
            if (r0 == r1) goto L_0x0042
            r1 = 1025(0x401, float:1.436E-42)
            if (r0 == r1) goto L_0x0042
            r1 = 8194(0x2002, float:1.1482E-41)
            r2 = 0
            if (r0 == r1) goto L_0x0017
            r1 = 16777232(0x1000010, float:2.3509932E-38)
            if (r0 == r1) goto L_0x0042
            goto L_0x0041
        L_0x0017:
            boolean r0 = org.libsdl.app.SDLActivity.mSeparateMouseAndTouch
            if (r0 != 0) goto L_0x001c
            goto L_0x0041
        L_0x001c:
            int r0 = r7.getActionMasked()
            r1 = 1
            switch(r0) {
                case 7: goto L_0x0035;
                case 8: goto L_0x0025;
                default: goto L_0x0024;
            }
        L_0x0024:
            goto L_0x0041
        L_0x0025:
            r3 = 10
            float r3 = r7.getAxisValue(r3, r2)
            r4 = 9
            float r4 = r7.getAxisValue(r4, r2)
            org.libsdl.app.SDLActivity.onNativeMouse(r2, r0, r3, r4, r2)
            return r1
        L_0x0035:
            float r3 = r7.getX(r2)
            float r4 = r7.getY(r2)
            org.libsdl.app.SDLActivity.onNativeMouse(r2, r0, r3, r4, r2)
            return r1
        L_0x0041:
            return r2
        L_0x0042:
            boolean r0 = org.libsdl.app.SDLControllerManager.handleJoystickMotionEvent(r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLGenericMotionListener_API12.onGenericMotion(android.view.View, android.view.MotionEvent):boolean");
    }

    public boolean supportsRelativeMouse() {
        return false;
    }

    public boolean inRelativeMode() {
        return false;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        return false;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
    }

    public float getEventX(MotionEvent event) {
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        return event.getY(0);
    }
}
