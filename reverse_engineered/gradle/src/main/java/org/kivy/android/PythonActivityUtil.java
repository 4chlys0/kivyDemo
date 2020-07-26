package org.kivy.android;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.renpy.android.AssetExtract;
import org.renpy.android.ResourceManager;
import org.test.myapp.BuildConfig;

public class PythonActivityUtil {
    private static final String TAG = "pythonactivityutil";
    /* access modifiers changed from: private */
    public Activity mActivity = null;
    private ResourceManager mResourceManager = null;

    public PythonActivityUtil(Activity activity, ResourceManager resourceManager) {
        this.mActivity = activity;
        this.mResourceManager = resourceManager;
    }

    private void toastError(final String msg) {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(PythonActivityUtil.this.mActivity, msg, 1).show();
            }
        });
        synchronized (this.mActivity) {
            try {
                this.mActivity.wait(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    private void recursiveDelete(File f) {
        if (f.isDirectory()) {
            for (File r : f.listFiles()) {
                recursiveDelete(r);
            }
        }
        f.delete();
    }

    public void unpackData(String resource, File target) {
        String diskVersion;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("UNPACKING!!! ");
        sb.append(resource);
        sb.append(" ");
        sb.append(target.getName());
        Log.v(str, sb.toString());
        ResourceManager resourceManager = this.mResourceManager;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(resource);
        sb2.append("_version");
        String dataVersion = resourceManager.getString(sb2.toString());
        String str2 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Data version is ");
        sb3.append(dataVersion);
        Log.v(str2, sb3.toString());
        if (dataVersion != null) {
            String filesDir = target.getAbsolutePath();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(filesDir);
            sb4.append("/");
            sb4.append(resource);
            sb4.append(".version");
            String diskVersionFn = sb4.toString();
            try {
                byte[] buf = new byte[64];
                InputStream is = new FileInputStream(diskVersionFn);
                diskVersion = new String(buf, 0, is.read(buf));
                is.close();
            } catch (Exception e) {
                diskVersion = BuildConfig.FLAVOR;
            }
            if (!dataVersion.equals(diskVersion)) {
                String str3 = TAG;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Extracting ");
                sb5.append(resource);
                sb5.append(" assets.");
                Log.v(str3, sb5.toString());
                recursiveDelete(target);
                target.mkdirs();
                AssetExtract ae = new AssetExtract(this.mActivity);
                StringBuilder sb6 = new StringBuilder();
                sb6.append(resource);
                sb6.append(".mp3");
                if (!ae.extractTar(sb6.toString(), target.getAbsolutePath())) {
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("Could not extract ");
                    sb7.append(resource);
                    sb7.append(" data.");
                    toastError(sb7.toString());
                }
                try {
                    new File(target, ".nomedia").createNewFile();
                    FileOutputStream os = new FileOutputStream(diskVersionFn);
                    os.write(dataVersion.getBytes());
                    os.close();
                } catch (Exception e2) {
                    Log.w("python", e2);
                }
            }
        }
    }
}
