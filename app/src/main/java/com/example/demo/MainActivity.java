package com.example.demo;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demo.Gson.gsonExample;
import com.example.demo.QR.QrCodeActivity;
import com.example.demo.RecAlgo.ImageProcessor;
import com.example.demo.RecAlgo.SystemProcessor;
import com.example.demo.RecAlgo.analysisProcessor;
import com.example.demo.bean.Contact;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.RecAlgo.SystemProcessor.handleFilePath;
import static com.example.demo.ocrActivity.initTessBaseAPI;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int LoadStatus = 1;
    private final int TakeStatus = 2;
    private final String TAG = "OpenCV";
    Button btnProcess, btnLoad, btnTake, btnscan;
    private ImageView iv1;
    //    private Result result;
    String json = "";
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        SystemProcessor.RequestPerm(this);
        initView();
    }

    //???????????????
    private void initView() {
        InitBtn(btnProcess, R.id.Process);

        InitBtn(btnLoad, R.id.load);
        InitBtn(btnTake, R.id.take);
        InitBtn(btnscan, R.id.scan);

        iv1 = findViewById(R.id.ImgView);
        System.out.println("?????????????????????");
    }

    //??????????????? btn????????????id???????????????
    private void InitBtn(Button btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    //????????????
    /**
     * ????????????????????????????????????ImageView
     * @param imgPath ????????????????????????
     * @param imgView ???????????????ImageView
     */
    public void setImg(String imgPath, ImageView imgView) {
        File file = new File(imgPath);
        if (file.exists() && file.canRead()) {
            // -------1.????????????--------
            // ??????????????????
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int dw = metric.widthPixels; // ?????????
            int dh = metric.heightPixels; // ?????????
            // ???????????????????????????????????????
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // ????????????????????????????????????
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            // ?????????????????????????????????
            int heightRatio = (int) Math.ceil(options.outHeight / (float) dh);
            int widthRatio = (int) Math.ceil(options.outWidth / (float) dw);
            // ???????????????
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    options.inSampleSize = heightRatio;
                } else {
                    options.inSampleSize = widthRatio;
                }
            }
            // ????????????
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imgPath, options);
            // -------2.??????????????????--------
            try {
                ExifInterface exif = new ExifInterface(imgPath);
                int degree = 0; // ??????????????????
                if (exif != null) {
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, -1);
                    if (orientation != -1) {
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                degree = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                degree = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                degree = 270;
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (degree != 0) { // ??????????????????
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.preRotate(degree);
                    Bitmap mRotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,width,height, matrix, true);
                    imgView.setImageBitmap(mRotateBitmap);
                } else {
                    imgView.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
            }
        }
    }

    //??????????????????????????????
    private void selectPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, LoadStatus);
    }

    private void initPhotoError() {
        // android 7.0???????????????????????????
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    String mPath;

    private void takePic() {
        initPhotoError();
        mPath = Environment.getExternalStorageDirectory() + "/test.jpg"; //????????????????????????

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPath)));
        startActivityForResult(it, TakeStatus);
    }


    // ??????onActivityResult???????????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //??????????????????????????????
            if (requestCode == LoadStatus) {
                Uri uri = data.getData();
                Log.e("uri", uri.toString());
                //??????content?????????
                ContentResolver cr = this.getContentResolver();
                try {
                    //????????????
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    String path = handleFilePath(uri, this);
                    iv1.setImageBitmap(bitmap);
//                    setBitMap(path, iv1);
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }
            }

            if (requestCode == TakeStatus) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    setImg(mPath,iv1);
//                    iv1.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult:??????");
                }
            }
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                String result = scanResult.getContents();//?????????Result???json
                Log.e(TAG, "onActivityResult: " + result);
                Contact contact2 = gsonExample.fromGson(result);
                if (contact2 == null)
                    Toast.makeText(this.getApplicationContext(), "???????????????????????????????????????", Toast.LENGTH_LONG).show();
                else {
                    startContact(contact2);
                }
            }
        } else {
            //?????????????????????????????????
            Log.i("MainActivtiy2", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startContact(Contact contact) {
        Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    Bitmap bm;
//    List<Mat> tempMatList = new ArrayList<Mat>();
    Mat premat = new Mat();
    List<Rect> infoRectList = new ArrayList<Rect>();
    boolean flag=true;//????????????????????????
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.Process:

                if (count == 1) {
                    count++;
                    count = count % 2;
                    List<String> result = new ArrayList<String>();
                    infoRectList = ImageProcessor.recognition(premat);
                    List<Mat> infoMatlist = new ArrayList<Mat>();//infoMatList?????????????????????????????????????????????
                    try {
                        //??????OCR??????
                        TessBaseAPI baseAPI = initTessBaseAPI(this);
                        infoMatlist = checkRectList(premat, infoRectList);
                        for (int i = 0; i < infoMatlist.size(); i++) {
                            Mat mat = infoMatlist.get(i);
                            bm = ImageProcessor.showBm(mat);
                            result.add(ocrActivity.recognizeTextImage(baseAPI, bm));
                        }
//                        for (int i = 0; i < infoMatlist.size(); i++) {
//                            Mat mat = new Mat(premat, infoRectList.get(i));
//
//                            infoMatlist.add(mat);
//                            bm = ImageProcessor.showBm(infoMatlist.get(i));
//                            result.add(ocrActivity.recognizeTextImage(baseAPI, bm));
//                        }
                        baseAPI.end();
                    } catch (IOException e) {
                        Log.e(TAG, "OCR??????");
                        e.printStackTrace();
                    }
                    //????????????
//                        for (int i = 0; i < result.size(); i++) {
//                            Log.e("????????????: ", result.get(i));
//                        }
                    //????????????
                    Contact contact = analysisProcessor.analysis(result);
                    infoMatlist=new ArrayList<>();
                    //contact = new Contact("l", "123", "@qq.com", "??????????????????", "??????????????????", "??????");
                    //??????????????????
                    startContact(contact);
                }

                if (count == 0&&flag) {
                    flag=false;
                    count++;
                    count = count % 2;
                    Mat rotated = ImageProcessor.rotate(iv1);//???????????????
                    bm = ImageProcessor.showBm(rotated);
//                    tempMatList = ImageProcessor.setMatList(BitmapFactory.decodeResource(this.getResources(), R.drawable.temp_number));
//                    premat = ImageProcessor.preProc(rotated);
//                    bm = ImageProcessor.showBm(premat);
                    premat=rotated;
                    iv1.setImageBitmap(bm);
                }else flag=true;
                break;
            case R.id.take:
                takePic();
                break;
            case R.id.load:
                selectPic();
                break;
            case R.id.scan:
                new IntentIntegrator(this)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// ???????????????,????????????????????????????????????/?????????
                        .setPrompt("???????????????????????????????????????")// ???????????????
                        .setCameraId(0)// ???????????????,???????????????????????????
                        .setBeepEnabled(true)// ??????????????????,??????????????????"???"?????????
                        .setCaptureActivity(QrCodeActivity.class)//?????????????????????
                        .initiateScan();// ???????????????
                break;
        }
    }

    private List<Mat> checkRectList(Mat premat, List<Rect> infoRectList) {
        List<Mat> list = new ArrayList<Mat>();
        for (int i = 0; i < infoRectList.size(); i++) {
            Rect rect = infoRectList.get(i);
            Mat mat = new Mat(premat, rect);
//            if (ImageProcessor.checkGray(mat)) {//????????????????????????
                list.add(mat);
//            }
        }
        return list;
    }


}
