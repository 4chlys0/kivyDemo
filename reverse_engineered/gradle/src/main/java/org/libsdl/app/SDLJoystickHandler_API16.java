package org.libsdl.app;

import android.view.InputDevice;

/* compiled from: SDLControllerManager */
class SDLJoystickHandler_API16 extends SDLJoystickHandler_API12 {
    SDLJoystickHandler_API16() {
    }

    public String getJoystickDescriptor(InputDevice joystickDevice) {
        String desc = joystickDevice.getDescriptor();
        if (desc == null || desc.isEmpty()) {
            return super.getJoystickDescriptor(joystickDevice);
        }
        return desc;
    }
}
