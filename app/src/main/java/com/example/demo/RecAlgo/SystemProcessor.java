package com.example.demo.RecAlgo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

public class SystemProcessor {
    private static final String TAG="OpenCV_MySystem";
    @TargetApi(Build.VERSION_CODES.KITKAT)


    public static String handleFilePath(Uri uri,Context context) {
        Log.d("lmy", uri.toString());
        String path = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isMediaDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri).split(":")[1];
                String selection = "_id = " + id;
                String type = DocumentsContract.getDocumentId(uri).split(":")[0];
                if (type.equals("music"))
                    path = getFilePath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection,context);
                else if (type.equals("movie"))
                    path = getFilePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection,context);
                else if (type.equals("image"))
                    path = getFilePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,context);
            } else if (isDownloadsDocument(uri)) {
                path = DocumentsContract.getDocumentId(uri).substring(DocumentsContract.getDocumentId(uri).indexOf(":"));
            } else if (isExternalStorageDocument(uri)) {
                path = Environment.getExternalStorageDirectory() + "/" + DocumentsContract.getDocumentId(uri).split(":")[1];
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getFilePath(uri, null,context);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getFilePath(Uri uri,String selection,Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("_data"));
            }
            cursor.close();
        }
        return null;
    }


    public static void RequestPerm(FragmentActivity activity) {
        PermissionX.init(activity).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {

            }
        });
    }



}
