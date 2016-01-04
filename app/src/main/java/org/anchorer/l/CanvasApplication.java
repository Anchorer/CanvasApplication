package org.anchorer.l;

import android.app.Application;
import android.os.Environment;

import org.anchorer.l.consts.Const;

import java.io.File;

/**
 * Application Class
 * Created by Anchorer on 2016/1/4.
 */
public class CanvasApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            String path = Environment.getExternalStorageDirectory() + Const.PATH_BITMAP;
            File newFile = new File(path);
            if (!newFile.exists()) {
                newFile.mkdir();
            }
        } catch (Exception e) {}
    }
}
