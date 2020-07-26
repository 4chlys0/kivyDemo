package org.renpy.android;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

public class ResourceManager {
    private Activity act;
    private Resources res = this.act.getResources();

    public ResourceManager(Activity activity) {
        this.act = activity;
    }

    public int getIdentifier(String name, String kind) {
        Log.v("SDL", "getting identifier");
        StringBuilder sb = new StringBuilder();
        sb.append("kind is ");
        sb.append(kind);
        sb.append(" and name ");
        sb.append(name);
        Log.v("SDL", sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("result is ");
        sb2.append(this.res.getIdentifier(name, kind, this.act.getPackageName()));
        Log.v("SDL", sb2.toString());
        return this.res.getIdentifier(name, kind, this.act.getPackageName());
    }

    public String getString(String name) {
        String str = "SDL";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("asked to get string ");
            sb.append(name);
            Log.v(str, sb.toString());
            return this.res.getString(getIdentifier(name, "string"));
        } catch (Exception e) {
            Log.v("SDL", "got exception looking for string!");
            return null;
        }
    }

    public View inflateView(String name) {
        return this.act.getLayoutInflater().inflate(getIdentifier(name, "layout"), null);
    }

    public View getViewById(View v, String name) {
        return v.findViewById(getIdentifier(name, "id"));
    }
}
