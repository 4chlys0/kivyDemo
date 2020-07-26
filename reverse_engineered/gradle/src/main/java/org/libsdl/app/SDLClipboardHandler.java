package org.libsdl.app;

/* compiled from: SDLActivity */
interface SDLClipboardHandler {
    String clipboardGetText();

    boolean clipboardHasText();

    void clipboardSetText(String str);
}
