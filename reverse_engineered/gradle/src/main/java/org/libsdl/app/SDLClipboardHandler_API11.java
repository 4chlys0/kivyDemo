package org.libsdl.app;

import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;

/* compiled from: SDLActivity */
class SDLClipboardHandler_API11 implements SDLClipboardHandler, OnPrimaryClipChangedListener {
    protected ClipboardManager mClipMgr = ((ClipboardManager) SDL.getContext().getSystemService("clipboard"));

    SDLClipboardHandler_API11() {
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    public boolean clipboardHasText() {
        return this.mClipMgr.hasText();
    }

    public String clipboardGetText() {
        CharSequence text = this.mClipMgr.getText();
        if (text != null) {
            return text.toString();
        }
        return null;
    }

    public void clipboardSetText(String string) {
        this.mClipMgr.removePrimaryClipChangedListener(this);
        this.mClipMgr.setText(string);
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    public void onPrimaryClipChanged() {
        SDLActivity.onNativeClipboardChanged();
    }
}
