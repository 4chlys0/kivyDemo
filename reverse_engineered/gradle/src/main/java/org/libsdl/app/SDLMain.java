package org.libsdl.app;

import android.util.Log;

/* compiled from: SDLActivity */
class SDLMain implements Runnable {
    SDLMain() {
    }

    public void run() {
        String library = SDLActivity.mSingleton.getMainSharedObject();
        String function = SDLActivity.mSingleton.getMainFunction();
        String[] arguments = SDLActivity.mSingleton.getArguments();
        StringBuilder sb = new StringBuilder();
        sb.append("Running main function ");
        sb.append(function);
        sb.append(" from library ");
        sb.append(library);
        Log.v("SDL", sb.toString());
        SDLActivity.mSingleton.appConfirmedActive();
        SDLActivity.nativeRunMain(library, function, arguments);
        Log.v("SDL", "Finished main function");
        if (!SDLActivity.mExitCalledFromJava) {
            SDLActivity.handleNativeExit();
        }
    }
}
