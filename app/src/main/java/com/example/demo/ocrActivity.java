package com.example.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ocrActivity extends AppCompatActivity {
    static final String DEFAULT_LANGUAGE = "chi_sim";
    private static String TAG = "OCR";
    static String dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tesseract";


    public static TessBaseAPI initTessBaseAPI(Context context) throws IOException {
        TessBaseAPI baseAPI = new TessBaseAPI();
        Log.e(TAG, "initTessBaseAPI: " + dataPath);
        baseAPI.setDebug(true);

        File dir = new File(dataPath);
        if (!dir.exists())
            dir.mkdir();
        File tessdataDir = new File(dataPath + "/tessdata");
        if (!tessdataDir.exists()) tessdataDir.mkdir();
        InputStream input = context.getResources().openRawResource(R.raw.chi_sim);
        File file = new File(tessdataDir, "chi_sim.traineddata");
        FileOutputStream output = new FileOutputStream(file);
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = input.read(buff)) != -1) output.write(buff, 0, len);
        input.close();
        output.close();

        boolean success = baseAPI.init(dataPath, DEFAULT_LANGUAGE);
        if (success) Log.e(TAG, "initTessBaseAPI: OK");
        else Log.e(TAG, "initTessBaseAPI: Wrong");
        return baseAPI;
    }


    public static String recognizeTextImage(TessBaseAPI baseAPI, Bitmap bm) {
        String text = "";
        if (bm == null) return null;
        baseAPI.setImage(bm);
        text = baseAPI.getUTF8Text();
        return text;
    }

}