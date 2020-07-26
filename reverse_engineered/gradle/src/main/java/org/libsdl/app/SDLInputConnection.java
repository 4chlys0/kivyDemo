package org.libsdl.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

/* compiled from: SDLActivity */
class SDLInputConnection extends BaseInputConnection {
    public static native void nativeCommitText(String str, int i);

    public native void nativeGenerateScancodeForUnichar(char c);

    public native void nativeSetComposingText(String str, int i);

    public SDLInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);
    }

    public boolean sendKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 66) {
            String imeHide = SDLActivity.nativeGetHint("SDL_RETURN_KEY_HIDES_IME");
            if (imeHide != null && imeHide.equals("1")) {
                Context c = SDL.getContext();
                if (c instanceof SDLActivity) {
                    ((SDLActivity) c).sendCommand(3, null);
                    return true;
                }
            }
        }
        return super.sendKeyEvent(event);
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        for (int i = 0; i < text.length(); i++) {
            nativeGenerateScancodeForUnichar(text.charAt(i));
        }
        nativeCommitText(text.toString(), newCursorPosition);
        return super.commitText(text, newCursorPosition);
    }

    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        nativeSetComposingText(text.toString(), newCursorPosition);
        return super.setComposingText(text, newCursorPosition);
    }

    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        if (beforeLength <= 0 || afterLength != 0) {
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
        int beforeLength2 = beforeLength;
        boolean ret = true;
        while (true) {
            int beforeLength3 = beforeLength2 - 1;
            if (beforeLength2 <= 0) {
                return ret;
            }
            boolean z = false;
            boolean ret_key = sendKeyEvent(new KeyEvent(0, 67)) && sendKeyEvent(new KeyEvent(1, 67));
            if (ret && ret_key) {
                z = true;
            }
            ret = z;
            beforeLength2 = beforeLength3;
        }
    }
}
