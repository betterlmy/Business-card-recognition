package com.example.demo.baidu;

import android.util.Log;
import com.example.demo.bean.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URLEncoder;

import static com.example.demo.baidu.AuthService.getAuth;

/**
 * @author Lmy
 * @date 2021/4/10 12:11 上午
 */

public class OCR {
    private static String TAG="BAIDU_API";
    public static Result businessCard(String path) {
        // 请求url
        Log.e(TAG, "businessCard: "+path );
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_card";
        try {
            // 本地文件路径
            byte[] imgData = FileUtil.readFileByBytes(path);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAuth();;
            Log.e(TAG, "token "+accessToken);
            String result = HttpUtil.post(url, accessToken, param);
            Gson gson=new Gson();
            Type collectionType = new TypeToken<Result>(){}.getType();
            Result result1 = gson.fromJson(result, collectionType);
            return result1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
