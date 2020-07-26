package org.kamranzafar.jtar;

import java.io.File;

public class TarUtils {
    public static long calculateTarSize(File path) {
        return tarSize(path) + 1024;
    }

    private static long tarSize(File dir) {
        long j;
        long size = 0;
        if (dir.isFile()) {
            return entrySize(dir.length());
        }
        File[] subFiles = dir.listFiles();
        if (subFiles == null || subFiles.length <= 0) {
            return 512;
        }
        for (File file : subFiles) {
            if (file.isFile()) {
                j = entrySize(file.length());
            } else {
                j = tarSize(file);
            }
            size += j;
        }
        return size;
    }

    private static long entrySize(long fileSize) {
        long size = 0 + 512 + fileSize;
        long extra = size % 512;
        if (extra > 0) {
            return size + (512 - extra);
        }
        return size;
    }

    public static String trim(String s, char c) {
        StringBuffer tmp = new StringBuffer(s);
        int i = 0;
        while (i < tmp.length() && tmp.charAt(i) == c) {
            tmp.deleteCharAt(i);
            i++;
        }
        int i2 = tmp.length() - 1;
        while (i2 >= 0 && tmp.charAt(i2) == c) {
            tmp.deleteCharAt(i2);
            i2--;
        }
        return tmp.toString();
    }
}
