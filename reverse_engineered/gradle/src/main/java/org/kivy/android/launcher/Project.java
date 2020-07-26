package org.kivy.android.launcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.test.myapp.BuildConfig;

public class Project {
    String author = null;
    public String dir = null;
    Bitmap icon = null;
    public boolean landscape = false;
    String title = null;

    static String decode(String s) {
        try {
            return new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static Project scanDirectory(File dir2) {
        if (dir2.getAbsolutePath().endsWith(".link")) {
            try {
                FileInputStream in = new FileInputStream(new File(dir2, "android.txt"));
                Properties p = new Properties();
                p.load(in);
                in.close();
                String directory = p.getProperty("directory", null);
                if (directory == null) {
                    return null;
                }
                dir2 = new File(directory);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Couldn't open link file ");
                sb.append(dir2);
                Log.i("Project", sb.toString(), e);
            }
        }
        if (!dir2.isDirectory()) {
            return null;
        }
        try {
            FileInputStream in2 = new FileInputStream(new File(dir2, "android.txt"));
            Properties p2 = new Properties();
            p2.load(in2);
            in2.close();
            String title2 = decode(p2.getProperty("title", "Untitled"));
            String author2 = decode(p2.getProperty("author", BuildConfig.FLAVOR));
            boolean landscape2 = p2.getProperty("orientation", "portrait").equals("landscape");
            Project rv = new Project();
            rv.title = title2;
            rv.author = author2;
            rv.icon = BitmapFactory.decodeFile(new File(dir2, "icon.png").getAbsolutePath());
            rv.landscape = landscape2;
            rv.dir = dir2.getAbsolutePath();
            return rv;
        } catch (Exception e2) {
            Log.i("Project", "Couldn't open android.txt", e2);
            return null;
        }
    }
}
