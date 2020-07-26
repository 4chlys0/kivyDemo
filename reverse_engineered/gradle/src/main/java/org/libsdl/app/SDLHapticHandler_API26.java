package org.libsdl.app;

import android.os.VibrationEffect;
import android.util.Log;

/* compiled from: SDLControllerManager */
class SDLHapticHandler_API26 extends SDLHapticHandler {
    SDLHapticHandler_API26() {
    }

    public void run(int device_id, float intensity, int length) {
        SDLHaptic haptic = getHaptic(device_id);
        if (haptic != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Rtest: Vibe with intensity ");
            sb.append(intensity);
            sb.append(" for ");
            sb.append(length);
            Log.d("SDL", sb.toString());
            if (intensity == 0.0f) {
                stop(device_id);
                return;
            }
            int vibeValue = Math.round(255.0f * intensity);
            if (vibeValue > 255) {
                vibeValue = 255;
            }
            if (vibeValue < 1) {
                stop(device_id);
            } else {
                try {
                    haptic.vib.vibrate(VibrationEffect.createOneShot((long) length, vibeValue));
                } catch (Exception e) {
                    haptic.vib.vibrate((long) length);
                }
            }
        }
    }
}
