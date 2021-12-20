package com.example.demo.RecAlgo;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import org.opencv.android.Utils;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;

///背景是黑色的0 字是白色的255
public class ImageProcessor {
    private static final String TAG = "OpenCV";
    private static Scalar red = new Scalar(0, 0, 255);
    private static int width = 1670;
    private static int height = 1000;
    private static Size namecarddefaultSize = new Size(width, height);
    private static Size numberSize = new Size(150, 230);

    //预处理方法，获得名片裁剪后的区域，显示imageView，返回预处理后的灰度mat
    public static Mat preProc(@NonNull Mat rotated) {
        if (rotated.empty()) {
            Log.e(TAG, "Proc: 加载的图片是空的");
            return null;
        }
        Bitmap bm = showBm(rotated);
        //轮廓发现
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        findContours(rotated, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, new Point(0, 0));

        //轮廓绘制
        Paint p = new Paint();
        p.setStrokeWidth(10);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        Canvas canvas = new Canvas(bm);
        Rect rect = new Rect();
        int offset = 10;
        List<Rect> lr = new ArrayList<Rect>();

        //筛选轮廓
        for (int i = 0; i < contours.size(); i++) {
            rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width>0.5*rotated.width()) //边缘条件
            {
                lr.add(rect);
                android.graphics.Rect rect2 = new android.graphics.Rect();
                rect2.set(rect.x + offset, rect.y + offset, rect.x + rect.width - offset, rect.y + rect.height - offset);
                canvas.drawRect(rect2, p);
            }
        }
        List<Integer> series = new ArrayList<Integer>();
        List<Integer> series_bak = new ArrayList<Integer>();

        for (int i = 0; i < lr.size(); i++) {
            rect = lr.get(i);
            series.add(rect.width);
            series_bak.add(rect.width);
        }
        sortList(series_bak, "<");
        int secNum=0;
        if(series_bak.size()>1)
        secNum = series.indexOf(series_bak.get(1));//得到第二大的数的序号
        else         secNum = series.indexOf(series_bak.get(0));//得到第二大的数的序号
        android.graphics.Rect rect2 = new android.graphics.Rect();
        rect2.set(lr.get(secNum).x + offset, lr.get(secNum).y + offset, lr.get(secNum).x + lr.get(secNum).width - offset, lr.get(secNum).y + lr.get(secNum).height - offset);
        canvas.drawRect(rect2, p);



        Mat namecardMat = new Mat();

        //缩放识别的边框(适当缩小)
        double[] doubles = {lr.get(secNum).x + offset, lr.get(secNum).y + offset, lr.get(secNum).width - offset, lr.get(secNum).height - offset};
        Rect rect1 = new Rect(doubles);
        if (rect.width > 0)
            namecardMat = new Mat(rotated, rect1);

        double scale=lr.get(secNum).width/namecarddefaultSize.width;
        namecarddefaultSize=new Size(lr.get(secNum).width/scale,lr.get(secNum).height/scale);
        resize(namecardMat, namecardMat, namecarddefaultSize);
//        namecardMat = turnOver(namecardMat);
        //内存释放
        rotated.release();
        Log.e(TAG, "预处理完成！！");
        return namecardMat;
    }

    //OCR通过形态学操作 获得可能包含信息的Rect列表
    public static List<Rect> recognition(Mat srcmat1) {
        List<Rect> rectList = new ArrayList<Rect>();
        Mat srcmat = srcmat1.clone();
        if (srcmat.empty()) {
            Log.e(TAG, "Proc: 加载的图片是空的");
            return null;
        }
        Mat gray = checkMat(srcmat);//是否灰度
        Bitmap b = showBm(gray);
//        Mat binary = new Mat();
//        threshold(gray, binary, 0, 255, THRESH_BINARY | THRESH_OTSU);
        //获取边缘图
        Mat gradxy = sobel(gray);
        b = showBm(gradxy);

        //闭操作，连接近似连通的区域
        Mat MorphMat = new Mat();
        morphologyEx(gradxy, MorphMat, MORPH_CLOSE, getStructuringElement(MORPH_RECT, new Size(15, 7)), new Point(-1, -1), 1);
        b = showBm(MorphMat);

        morphologyEx(gradxy, MorphMat, MORPH_CLOSE, getStructuringElement(MORPH_RECT, new Size(15, 7)), new Point(-1, -1), 4);
        b = showBm(MorphMat);


        threshold(MorphMat, MorphMat, 0, 255, THRESH_BINARY | THRESH_OTSU);
        b = showBm(MorphMat);

        //轮廓发现
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        findContours(MorphMat, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, new Point(0, 0));

        //p0画轮廓
        Paint p0 = new Paint();
        p0.setStrokeWidth(7);
        p0.setColor(Color.RED);
        p0.setStyle(Paint.Style.STROKE);

        //bitmap仅用于在Debug时显示轮廓信息
        Bitmap bitmap = Bitmap.createBitmap(srcmat.width(), srcmat.height(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gray, bitmap);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect();
        int number = 0;
        //挨个判断条件，符合条件的画框并加入rectList中 rectList是名片信息所在的矩形List
        for (int i = 0; i < contours.size(); i++) {
            rect = Imgproc.boundingRect(contours.get(i));
            float ratio = (float) rect.width / rect.height;
            if (ratio > 1.2 && rect.width > (0.1 * width)&&rect.width < (0.8 * width)) {
                rectList.add(rect);
                number += 1;
                android.graphics.Rect rect1 = new android.graphics.Rect();
                rect1.set(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
                canvas.drawRect(rect1, p0);

                //p1写字
                Paint p1 = new Paint();
                p1.setColor(Color.WHITE);
                p1.setTextSize(40);
                canvas.drawText(String.valueOf(i), rect.x, rect.y, p1);
            }
        }


        //存储着电话的Mat
//        Mat numberMat = new Mat(binary, rectList.get(3));
//        match(numberMat, matList);

        //内存释放
//        srcmat.release();
        gray.release();
        return rectList;
    }


    //匹配方法（第一个参数是要匹配的原图，第二个参数是模板组成的matList）
    private static void match(Mat srcMat, List<Mat> matList) {
        //检测原图是否为空,否则二值化，并进行轮廓选择
        srcMat = checkMat(srcMat, true);
        if (srcMat.empty()) return;

        List<MatOfPoint> srcCnts = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        findContours(srcMat, srcCnts, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);


        //p0画轮廓
        Paint p0 = new Paint();
        p0.setStrokeWidth(7);
        p0.setColor(Color.RED);
        p0.setStyle(Paint.Style.STROKE);

        //bitmap仅用于在Debug时显示轮廓信息
        Bitmap bitmap = Bitmap.createBitmap(srcMat.width(), srcMat.height(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(srcMat, bitmap);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect();
        List<Rect> rectList = new ArrayList<Rect>();

        //挨个判断条件，符合条件的画框并加入rectList中
        for (int i = 0; i < srcCnts.size(); i++) {
            rect = Imgproc.boundingRect(srcCnts.get(i));
            android.graphics.Rect rect1 = new android.graphics.Rect();
            rect1.set(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
            canvas.drawRect(rect1, p0);
            //p1写字
            Paint p1 = new Paint();
            p1.setColor(Color.WHITE);
            p1.setTextSize(40);
            canvas.drawText(String.valueOf(i), rect.x, rect.y, p1);
            rectList.add(rect);
        }
        //从左至右排序
        rectList = sortRectList(rectList);

//        Size numberSize=new Size(40,30);
        Bitmap b;
        String str = "";
        for (int i = 0; i < rectList.size(); i++) {
            Rect nowRect = rectList.get(i);
            Mat nowMat = new Mat(srcMat, nowRect);//47*18
            resize(nowMat, nowMat, numberSize);
            b = showBm(nowMat);
            double maxv = 0;
            int index = 0;
            //每个字符挨个配对
            for (int j = 0; j < matList.size(); j++) {
                b = showBm(matList.get(j));//Matlist是模版list
                Mat resultMat = new Mat();
                matchTemplate(nowMat, matList.get(j), resultMat, TM_CCOEFF_NORMED);//归一化
                Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(resultMat);
                double tmp = minMaxLocResult.maxVal;
                if (tmp > maxv) {
                    maxv = tmp;
                    index = j;
                }
            }
            str += String.valueOf(index);
        }
        Log.e(TAG, "识别的字符 " + str);
    }


    //将mat类型转为Bitmap并返回在指定的image view中，toast显示文字 无返回值
    private static void showBm(Mat mat, ImageView iv, Context context, String str) {
        Bitmap dstBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, dstBitmap);
        iv.setImageBitmap(dstBitmap);
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        Log.e(TAG, str + "completed");
    }

    //单Mat参数，将mat转为Bitmap并返回（因为debug可以查看bitmap类型的图片，但是不能查看mat类型的图片）
    public static Bitmap showBm(Mat mat) {
        if (mat.empty()) return null;
        Bitmap dstBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, dstBitmap);
        return dstBitmap;
    }


    //将Bitmap格式的模版图片传入，获取MatList（） 这里主要是传入数字模板，返回0-9是个数字组成的MatList
    public static List<Mat> setMatList(Bitmap tempBm) {
        List<Mat> matList = new ArrayList<Mat>();
        Mat tempMat = new Mat();
        Utils.bitmapToMat(tempBm, tempMat);

        //模版转灰度
        cvtColor(tempMat, tempMat, COLOR_BGR2GRAY);
        threshold(tempMat, tempMat, 0, 255, THRESH_BINARY_INV | THRESH_OTSU);
        //轮廓写入到List中
        Bitmap tmp = showBm(tempMat);

        List<MatOfPoint> contoursTmp = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        findContours(tempMat, contoursTmp, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, new Point(0, 0));

        //轮廓绘制
        Paint p = new Paint();
        p.setStrokeWidth(7);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        Bitmap bitmap = tempBm.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect();
        List<Rect> rectList = new ArrayList<Rect>();
        for (int i = 0; i < contoursTmp.size(); i++) {
            rect = Imgproc.boundingRect(contoursTmp.get(i));
            if (rect.height > 170 && rect.height < 230) {//将满足条件模板按序号存入RectList中
                android.graphics.Rect rect1 = new android.graphics.Rect();
                rect1.set(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
                canvas.drawRect(rect1, p);
                Paint p1 = new Paint();
                p1.setColor(Color.BLUE);
                p1.setTextSize(30);
                canvas.drawText(String.valueOf(i), rect.x, rect.y, p1);

                //设置偏移值适当放大范围
                int offset = 10;
                //防止越界
                int x = rect.x - offset > 0 ? rect.x - offset : 0;
                int y = rect.y - offset > 0 ? rect.y - offset : 0;
                int rectWidth = rect.width + offset < tempMat.width() ? rect.width + offset : tempMat.width();
                int rectHeight = rect.height + offset < tempMat.height() ? rect.height + offset : tempMat.height();

                double[] doubles = {x, y, rectWidth, rectHeight};
                rectList.add(new Rect(doubles));
            }
        }
        //对获取的rectList按x坐标从左到右排序
        rectList = sortRectList(rectList);
        for (int i = 0; i < rectList.size(); i++) {
            Mat mat = new Mat(tempMat, rectList.get(i));
            resize(mat, mat, numberSize);
            matList.add(mat);
        }
        testMatList(matList);
        Log.w(TAG, "");
        return matList;
    }


    //传入序列i，以及图像的裁剪矩形，mat 通过该方法，将符合条件的按序列存入hashmap<index，mat>中
    //传入Rect列表，按x从小到大排序
    private static List<Rect> sortRectList(List<Rect> rectList) {
        List<Integer> xList = new ArrayList<Integer>();
        List<Rect> newRectList = new ArrayList<Rect>();
        for (int i = 0; i < rectList.size(); i++) {
            Rect tempRect = rectList.get(i);
            xList.add(tempRect.x);
        }
        int listSize = xList.size();
        for (int i = 0; i < listSize; i++) {
            int nowMinIndex = getMinIndex(xList, i);
            xList.set(nowMinIndex, 99999);
            newRectList.add(rectList.get(nowMinIndex));
        }

        return newRectList;
    }

    //传入一个int型list，返回最小值的下标
    private static int getMinIndex(List<Integer> xList, int times) {
        if (xList.size() == 0) return -1;
        int min = 999999;
        int index = times;
        for (int i = 0; i < xList.size(); i++) {
            int tmp = xList.get(i);
            if (tmp < min) {
                min = tmp;
                index = i;
            }
        }
        return index;
    }

    //计算加权后的Sobel算子
    private static Mat sobel(Mat mat) {
        Mat dstMat = new Mat();
        //X梯度
        Mat gradX = new Mat();
        Imgproc.Sobel(mat, gradX, CvType.CV_32F, 1, 0);
        Core.convertScaleAbs(gradX, gradX);

        //Y梯度
        Mat gradY = new Mat();
        Imgproc.Sobel(mat, gradY, CvType.CV_32F, 0, 1);
        Core.convertScaleAbs(gradY, gradY);

        //计算加权
        Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, dstMat);
        return dstMat;
    }

    //测试hashmap中存入的mat图
    private static void testMatList(List<Mat> matList) {
        Bitmap bm;
        for (int i = 0; i < matList.size(); i++) {
            Mat mat = matList.get(i);
            bm = showBm(mat);
        }
    }

    //灰度图像的颜色反转（黑白反转）
    public static Mat turnOver(@NonNull Mat mat) {
        int width = mat.width();
        int height = mat.height();
        Log.e(TAG, "width: " + width);
        Log.e(TAG, "height: " + height);
        //判断参数是否正确
        if (width <= 0 || height <= 0) Log.e(TAG, "turnOver: Wrong");

        byte[] data = new byte[width * height];//新建一个字节

        mat.get(0, 0, data);//从（0，0）传mat的数据到data字节数组里
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
                data[row * width + col] = (byte) (~data[row * width + col]);//按像素反转

        mat.put(0, 0, data);

        return mat;
    }

    //检查mat的格式(是否为灰度,是否需要二值化)
    private static Mat checkMat(Mat mat) {
        return checkMat(mat, false);
    }

    private static Mat checkMat(Mat mat, boolean needBin) {
        if (mat.empty()) return null;
        if (mat.channels() != 1) {
            cvtColor(mat, mat, COLOR_BGRA2GRAY);
        }
        if (needBin) {
            threshold(mat, mat, 0, 255, THRESH_BINARY | THRESH_OTSU);
        }
        return mat;
    }

    //对list排序
    public static void sortList(List list, String operation) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                int a = Integer.parseInt(list.get(j).toString());
                int b = Integer.parseInt(list.get(j + 1).toString());
                if (">".equals(operation)) {
                    if (a > b) {
                        list.set(j, b);
                        list.set(j + 1, a);
                    }
                } else {
                    if (a < b) {
                        list.set(j, b);
                        list.set(j + 1, a);
                    }
                }
            }
        }
    }

    //解决OOM问题的显示设置方法 是用factory的options  将路径上的文件传到imageview中去
    public static void setBitMap(@NonNull String Path, @NonNull ImageView iv) {
//        Log.e(TAG, "setBM: " + Path);
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path, op);

        int width = op.outWidth;//350
        int height = op.outHeight; //230

        //正常为宽大于高
        if (height > width) {
            int tmp = height;
            height = width;
            width = tmp;
        }

        //系数求最大 等比例压缩
        double widthS = width / 3264.0;
        double heightS = height / 1836.0;
        double bigger = widthS > heightS ? widthS : heightS;

        op.inJustDecodeBounds = false;
        op.inSampleSize = (int) bigger;
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bm = BitmapFactory.decodeFile(Path, op);
        iv.setImageBitmap(bm);

    }

    public static boolean checkGray(Mat mat) {
        //判断图中是不是大部分为字符 否则删除
        byte data[] = new byte[1];
        if (mat.channels() == 1) {
            int num = 0;
            int width = mat.width();
            int height = mat.height();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    mat.get(row, col, data);
                    if (data[0] < 250) {
                        num++;
                    }
                }
            }
            float result = num / (width * height);
            if (result > 0.80) return true;
        }
        return false;
    }

    //////
    private Mat getWarpPersPective(Mat in, Point[] point) {
        MatOfPoint2f reusltPoint2f = null, srcPoint2f = null;
        Mat out = new Mat();
        Point[] targetPoints = new Point[4];
        for (int i = 0; i < 4; i++) {
            targetPoints[i] = new Point();
        }
        //这里拿到倾斜的长度作为宽高 结果可能比真正矫正的图片略小点 但是矫正效果还是很不错的
        double rect_width = Math.sqrt(Math.abs(point[0].x - point[1].x) * Math.abs(point[0].x - point[1].x) +
                Math.abs(point[0].y - point[1].y) * Math.abs(point[0].y - point[1].y));
        double rect_height = Math.sqrt(Math.abs(point[0].x - point[2].x) * Math.abs(point[0].x - point[2].x) +
                Math.abs(point[0].y - point[2].y) * Math.abs(point[0].y - point[2].y));

        double moveValueX = 0.0;
        double moveValueY = 0.0;

        targetPoints[0].x = 0.0 + moveValueX;
        targetPoints[0].y = 0 + moveValueY;// top_left
        targetPoints[2].x = 0.0 + moveValueX;
        targetPoints[2].y = rect_height + moveValueY;// bottom_Left
        targetPoints[1].x = rect_width + moveValueX;
        targetPoints[1].y = 0.0 + moveValueY;// top_Right
        targetPoints[3].x = rect_width + moveValueX;
        targetPoints[3].y = rect_height + moveValueY;// bottom_Right
        reusltPoint2f = new MatOfPoint2f(targetPoints);//这里需要将四个点转换成Mat
        srcPoint2f = new MatOfPoint2f(point);

        Mat tranform = getPerspectiveTransform(reusltPoint2f, srcPoint2f); // 透视变换
        warpPerspective(in, out, tranform, new Size(rect_width, rect_height), INTER_LINEAR | WARP_INVERSE_MAP);
        return out;//变换后的Mat
    }

    public static Mat rotate(@NonNull ImageView imageView) {
//        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ncb1);
        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Mat srcmat = new Mat();
        Utils.bitmapToMat(bm, srcmat);//将bitmap转为Mat类型供OpenCV操作
        if (srcmat.empty()) {
            Log.e(TAG, "Proc: 加载的图片是空的");
            return null;
        }
        Mat corrected=correct(srcmat);
        return corrected;
    }

    private static MatOfPoint findMaxcon(List<MatOfPoint> contours) {
        int num = 0;
        double max = 0;
        for (int i = 0; i < contours.size(); i++) {
            Mat nowMat = contours.get(i);
            int width=nowMat.rows();
            if(width>1600) continue;
            double area = contourArea(nowMat);
            if (area > max) {
                max = area;
                num = i;
            }
        }
        return contours.get(num);
    }

    //
    public static Mat canny(Mat src) {
        Mat mat = src.clone();
        Imgproc.Canny(src, mat, 10, 200);
        return mat;
    }
    //寻找最大矩形
    public static RotatedRect findMaxRect(Mat cannyMat) {
        Bitmap bitmap = Bitmap.createBitmap(cannyMat.width(), cannyMat.height(), Bitmap.Config.ARGB_8888);
        Paint p0 = new Paint();
        p0.setStrokeWidth(7);
        p0.setColor(Color.RED);
        p0.setStyle(Paint.Style.STROKE);
        
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Utils.matToBitmap(cannyMat, bitmap);
        Canvas canvas = new Canvas(bitmap);
        // 寻找轮廓
        Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE,
                new Point(0, 0));
        Rect rect = Imgproc.boundingRect(contours.get(0));

        // 找出匹配到的最大轮廓
        double area = rect.area();
        int index = 0;
        android.graphics.Rect rect1 = new android.graphics.Rect();

        // 找出匹配到的最大轮廓的下标 index
        for (int i = 0; i < contours.size(); i++) {
            rect = Imgproc.boundingRect(contours.get(i));
            rect1.set(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
            canvas.drawRect(rect1, p0);
            double tempArea = Imgproc.boundingRect(contours.get(i)).area();//返回包裹着输入信息的最小正矩形的面积
            if (tempArea > area) {
                area = tempArea;
                index = i;
            }
        }


        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());

        RotatedRect Rrect = Imgproc.minAreaRect(matOfPoint2f);//返回最小斜矩形

        return Rrect;
    }
    public static Mat rotation(Mat cannyMat, RotatedRect rect) {
        // 获取矩形的四个顶点
        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);


        double angle = rect.angle +90;
        if((angle<98&&angle>82)||(angle<8&&angle>-8)){//如果旋转角度不大
            return cannyMat;
        }

        Point center = rect.center;

        Mat CorrectImg = new Mat(cannyMat.size(), cannyMat.type());


        cannyMat.copyTo(CorrectImg);
        // 得到旋转矩阵算子
        Mat matrix = Imgproc.getRotationMatrix2D(center, angle, 1);//旋转中心点  旋转角度 图像缩放

        Imgproc.warpAffine(CorrectImg, CorrectImg, matrix, CorrectImg.size(), 1, 0, new Scalar(0, 0, 0));
        //flag为插值算法
        return CorrectImg;
    }

    /**
     * 把矫正后的图像切割出来
     *
     * @param correctMat
     *            图像矫正后的Mat矩阵
     */
    public static Mat cutRect(Mat correctMat) {
        // 再次获取最大矩形
        RotatedRect rect = findMaxRect(correctMat);

        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);

        int startLeft = (int)Math.abs(rectPoint[0].x);
        int startUp = (int)Math.abs(rectPoint[0].y < rectPoint[1].y ? rectPoint[0].y : rectPoint[1].y);
        int width = (int)Math.abs(rectPoint[2].x - rectPoint[0].x);
        int height = (int)Math.abs(rectPoint[1].y - rectPoint[0].y);

//        System.out.println("startLeft = " + startLeft);
//        System.out.println("startUp = " + startUp);
//        System.out.println("width = " + width);
//        System.out.println("height = " + height);

        for(Point p : rectPoint) {
            System.out.println(p.x + " , " + p.y);
        }

        Mat temp = new Mat(correctMat , new Rect(startLeft , startUp , width , height ));
        Mat t = new Mat();
        temp.copyTo(t);
        return t;

    }
    public static Mat correct(Mat src) {
        cvtColor(src,src,COLOR_BGRA2GRAY);
        Bitmap bm= showBm(src);
//        threshold(src, src, 180, 255, THRESH_BINARY_INV);
        threshold(src, src, 0, 255, THRESH_BINARY_INV|THRESH_OTSU);

        bm= showBm(src);
        Mat sobel=new Mat();
        sobel=sobel(src);
//        Imgproc.Canny(src, src, 10, 100);
        bm= showBm(sobel);
        // 获取边框图最大斜矩形
        RotatedRect rect = findMaxRect(sobel);

        // 旋转矩形
        Mat CorrectImg = rotation(src , rect);
//        Mat NativeCorrectImg = rotation(sobel , rect);
        bm= showBm(CorrectImg);

        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);
        int offset = 10;
        int startLeft = (int)Math.abs(rectPoint[0].x);//左上 右上 右下 左下

        int startUp = (int)Math.abs(rectPoint[0].y < rectPoint[1].y ? rectPoint[0].y : rectPoint[1].y);
        int width = (int)Math.abs(rectPoint[2].x - rectPoint[0].x);
        int height = (int)Math.abs(rectPoint[1].y - rectPoint[0].y);
        Mat temp=new Mat();
        try {
            temp = new Mat(CorrectImg, new Rect(startLeft + offset, startUp + offset, width - offset, height - offset));
        }catch (Exception e){
            temp=preProc(CorrectImg);
            Log.e(TAG, "correct: 错误" );
        }

        double scale=temp.width()/1670.0;
        namecarddefaultSize=new Size(temp.width()/scale,temp.height()/scale);
        resize(temp, temp, namecarddefaultSize, 0, 0, INTER_AREA);//调整大小


        return temp;
    }
}
