package com.example.demo;

import android.app.Application;
import android.widget.Toast;
import org.opencv.android.OpenCVLoader;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Toast.makeText(this.getApplicationContext(), "加载OpenCV库成功", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this.getApplicationContext(), "注意：：加载OpenCV库失败", Toast.LENGTH_LONG).show();
    }
}
