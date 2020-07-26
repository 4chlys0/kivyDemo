package org.libsdl.app;

import android.view.MotionEvent;

/* compiled from: SDLControllerManager */
class SDLGenericMotionListener_API24 extends SDLGenericMotionListener_API12 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API24() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0014, code lost:
        if (r0 != 16777232) goto L_0x0054;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onGenericMotion(android.view.View r7, android.view.MotionEvent r8) {
        /*
            r6 = this;
            int r0 = r8.getSource()
            r1 = 513(0x201, float:7.19E-43)
            if (r0 == r1) goto L_0x0055
            r1 = 1025(0x401, float:1.436E-42)
            if (r0 == r1) goto L_0x0055
            r1 = 8194(0x2002, float:1.1482E-41)
            r2 = 0
            if (r0 == r1) goto L_0x0017
            r1 = 16777232(0x1000010, float:2.3509932E-38)
            if (r0 == r1) goto L_0x0055
            goto L_0x0054
        L_0x0017:
            boolean r0 = org.libsdl.app.SDLActivity.mSeparateMouseAndTouch
            if (r0 != 0) goto L_0x001c
            goto L_0x0054
        L_0x001c:
            int r0 = r8.getActionMasked()
            r1 = 1
            switch(r0) {
                case 7: goto L_0x0035;
                case 8: goto L_0x0025;
                default: goto L_0x0024;
            }
        L_0x0024:
            goto L_0x0054
        L_0x0025:
            r3 = 10
            float r3 = r8.getAxisValue(r3, r2)
            r4 = 9
            float r4 = r8.getAxisValue(r4, r2)
            org.libsdl.app.SDLActivity.onNativeMouse(r2, r0, r3, r4, r2)
            return r1
        L_0x0035:
            boolean r3 = r6.mRelativeModeEnabled
            if (r3 == 0) goto L_0x0046
            r3 = 27
            float r3 = r8.getAxisValue(r3)
            r4 = 28
            float r4 = r8.getAxisValue(r4)
            goto L_0x004e
        L_0x0046:
            float r3 = r8.getX(r2)
            float r4 = r8.getY(r2)
        L_0x004e:
            boolean r5 = r6.mRelativeModeEnabled
            org.libsdl.app.SDLActivity.onNativeMouse(r2, r0, r3, r4, r5)
            return r1
        L_0x0054:
            return r2
        L_0x0055:
            boolean r0 = org.libsdl.app.SDLControllerManager.handleJoystickMotionEvent(r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLGenericMotionListener_API24.onGenericMotion(android.view.View, android.view.MotionEvent):boolean");
    }

    public boolean supportsRelativeMouse() {
        return true;
    }

    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    public boolean setRelativeMouseEnabled(boolean enabled) {
        this.mRelativeModeEnabled = enabled;
        return true;
    }

    public float getEventX(MotionEvent event) {
        if (this.mRelativeModeEnabled) {
            return event.getAxisValue(27);
        }
        return event.getX(0);
    }

    public float getEventY(MotionEvent event) {
        if (this.mRelativeModeEnabled) {
            return event.getAxisValue(28);
        }
        return event.getY(0);
    }
}
