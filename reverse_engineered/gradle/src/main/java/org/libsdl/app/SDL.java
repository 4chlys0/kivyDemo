package org.libsdl.app;

import android.content.Context;

public class SDL {
    protected static Context mContext;

    public static void setupJNI() {
        SDLActivity.nativeSetupJNI();
        SDLAudioManager.nativeSetupJNI();
        SDLControllerManager.nativeSetupJNI();
    }

    public static void initialize() {
        setContext(null);
        SDLActivity.initialize();
        SDLAudioManager.initialize();
        SDLControllerManager.initialize();
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void loadLibrary(String libraryName) throws UnsatisfiedLinkError, SecurityException, NullPointerException {
        if (libraryName == null) {
            throw new NullPointerException("No library name provided.");
        }
        try {
            Class relinkClass = mContext.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker");
            Class relinkListenerClass = mContext.getClassLoader().loadClass("com.getkeepsafe.relinker.ReLinker$LoadListener");
            Class contextClass = mContext.getClassLoader().loadClass("android.content.Context");
            Class stringClass = mContext.getClassLoader().loadClass("java.lang.String");
            Object relinkInstance = relinkClass.getDeclaredMethod("force", new Class[0]).invoke(null, new Object[0]);
            relinkInstance.getClass().getDeclaredMethod("loadLibrary", new Class[]{contextClass, stringClass, stringClass, relinkListenerClass}).invoke(relinkInstance, new Object[]{mContext, libraryName, null, null});
        } catch (Throwable th) {
            try {
                System.loadLibrary(libraryName);
            } catch (UnsatisfiedLinkError ule) {
                throw ule;
            } catch (SecurityException se) {
                throw se;
            }
        }
    }
}
