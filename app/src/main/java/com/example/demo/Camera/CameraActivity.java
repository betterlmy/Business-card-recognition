package com.example.demo.Camera;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demo.R;

public class CameraActivity extends AppCompatActivity {

    private Button button;
    private CameraSurfaceView mCameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 全屏显示
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);

        mCameraSurfaceView = findViewById(R.id.cameraSurfaceView);
        button = findViewById(R.id.takePic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.takePicture();
            }
        });
    }


//    public void autoFocus() {
//        mCameraSurfaceView.setAutoFocus();
//    }
}