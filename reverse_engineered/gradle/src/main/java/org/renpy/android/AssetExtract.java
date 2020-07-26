package org.renpy.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

public class AssetExtract {
    private AssetManager mAssetManager = null;

    public AssetExtract(Context context) {
        this.mAssetManager = context.getAssets();
    }

    public boolean extractTar(String asset, String target) {
        byte[] buf = new byte[1048576];
        try {
            InputStream assetStream = this.mAssetManager.open(asset, 2);
            TarInputStream tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(assetStream, 8192)), 8192));
            while (true) {
                try {
                    TarEntry entry = tis.getNextEntry();
                    if (entry == null) {
                        try {
                            tis.close();
                            assetStream.close();
                        } catch (IOException e) {
                        }
                        return true;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("extracting ");
                    sb.append(entry.getName());
                    Log.v("python", sb.toString());
                    if (entry.isDirectory()) {
                        try {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(target);
                            sb2.append("/");
                            sb2.append(entry.getName());
                            new File(sb2.toString()).mkdirs();
                        } catch (SecurityException e2) {
                        }
                    } else {
                        OutputStream out = null;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(target);
                        sb3.append("/");
                        sb3.append(entry.getName());
                        String path = sb3.toString();
                        try {
                            out = new BufferedOutputStream(new FileOutputStream(path), 8192);
                        } catch (FileNotFoundException | SecurityException e3) {
                        }
                        if (out == null) {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("could not open ");
                            sb4.append(path);
                            Log.e("python", sb4.toString());
                            return false;
                        }
                        while (true) {
                            try {
                                int len = tis.read(buf);
                                if (len == -1) {
                                    break;
                                }
                                out.write(buf, 0, len);
                            } catch (IOException e4) {
                                Log.e("python", "extracting zip", e4);
                                return false;
                            }
                        }
                        out.flush();
                        out.close();
                    }
                } catch (IOException e5) {
                    Log.e("python", "extracting tar", e5);
                    return false;
                }
            }
        } catch (IOException e6) {
            Log.e("python", "opening up extract tar", e6);
            return false;
        }
    }
}
