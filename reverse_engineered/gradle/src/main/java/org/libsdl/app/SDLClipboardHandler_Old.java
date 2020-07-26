package org.libsdl.app;

import android.text.ClipboardManager;

/* compiled from: SDLActivity */
class SDLClipboardHandler_Old implements SDLClipboardHandler {
    protected ClipboardManager mClipMgrOld = ((ClipboardManager) SDL.getContext().getSystemService("clipboard"));

    SDLClipboardHandler_Old() {
    }

    public boolean clipboardHasText() {
        return this.mClipMgrOld.hasText();
    }

    public String clipboardGetText() {
        CharSequence text = this.mClipMgrOld.getText();
        if (text != null) {
            return text.toString();
        }
        return null;
    }

    public void clipboardSetText(String string) {
        this.mClipMgrOld.setText(string);
    }
}
