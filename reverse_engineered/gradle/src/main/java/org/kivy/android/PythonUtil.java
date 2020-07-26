package org.kivy.android;

import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class PythonUtil {
    private static final String TAG = "pythonutil";

    protected static void addLibraryIfExists(ArrayList<String> libsList, String pattern, File libsDir) {
        File[] files = libsDir.listFiles();
        StringBuilder sb = new StringBuilder();
        sb.append("lib");
        sb.append(pattern);
        sb.append("\\.so");
        String pattern2 = sb.toString();
        Pattern p = Pattern.compile(pattern2);
        for (File file : files) {
            String name = file.getName();
            String str = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Checking pattern ");
            sb2.append(pattern2);
            sb2.append(" against ");
            sb2.append(name);
            Log.v(str, sb2.toString());
            if (p.matcher(name).matches()) {
                String str2 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Pattern ");
                sb3.append(pattern2);
                sb3.append(" matched file ");
                sb3.append(name);
                Log.v(str2, sb3.toString());
                libsList.add(name.substring(3, name.length() - 3));
            }
        }
    }

    protected static ArrayList<String> getLibraries(File libsDir) {
        ArrayList<String> libsList = new ArrayList<>();
        addLibraryIfExists(libsList, "sqlite3", libsDir);
        addLibraryIfExists(libsList, "ffi", libsDir);
        addLibraryIfExists(libsList, "png16", libsDir);
        addLibraryIfExists(libsList, "ssl.*", libsDir);
        addLibraryIfExists(libsList, "crypto.*", libsDir);
        addLibraryIfExists(libsList, "SDL2", libsDir);
        addLibraryIfExists(libsList, "SDL2_image", libsDir);
        addLibraryIfExists(libsList, "SDL2_mixer", libsDir);
        addLibraryIfExists(libsList, "SDL2_ttf", libsDir);
        libsList.add("python3.5m");
        libsList.add("python3.6m");
        libsList.add("python3.7m");
        libsList.add("python3.8m");
        libsList.add("main");
        return libsList;
    }

    public static void loadLibraries(File filesDir, File libsDir) {
        boolean foundPython = false;
        Iterator it = getLibraries(libsDir).iterator();
        while (it.hasNext()) {
            String lib = (String) it.next();
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Loading library: ");
            sb.append(lib);
            Log.v(str, sb.toString());
            try {
                System.loadLibrary(lib);
                if (lib.startsWith("python")) {
                    foundPython = true;
                }
            } catch (UnsatisfiedLinkError e) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Library loading error: ");
                sb2.append(e.getMessage());
                Log.v(str2, sb2.toString());
                if (lib.startsWith("python3.8") && !foundPython) {
                    throw new RuntimeException("Could not load any libpythonXXX.so");
                } else if (!lib.startsWith("python")) {
                    String str3 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("An UnsatisfiedLinkError occurred loading ");
                    sb3.append(lib);
                    Log.v(str3, sb3.toString());
                    throw e;
                }
            }
        }
        Log.v(TAG, "Loaded everything!");
    }
}
