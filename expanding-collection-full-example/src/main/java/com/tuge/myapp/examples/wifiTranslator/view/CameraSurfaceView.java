package com.tuge.myapp.examples.wifiTranslator.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tuge.myapp.examples.wifiTranslator.DetailActivity.LogUtil;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private static final int ORIENTATION = 90;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isOpen;
    private  Camera.AutoFocusCallback mAutoFocusCallback;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenMatrix(context);
        mHolder = getHolder();
        mHolder.addCallback(this);


        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //设置聚焦
                Point point = new Point((int) motionEvent.getX(), (int) motionEvent.getY());

                if (listener!=null) {
                    listener.OnListener(point);
                }//                onCameraFocus(point);

             Camera.Parameters mParameters =  mCamera.getParameters();

                int areaX = (int) (motionEvent.getX() / CameraSurfaceView.this.getWidth() * 2000) - 1000; // 获取映射区域的X坐标
                int areaY = (int) (motionEvent.getY() / CameraSurfaceView.this.getWidth()  * 2000) - 1000; // 获取映射区域的Y坐标
                Log.e("MainActivity", "X坐标：" + motionEvent.getX()+",Y坐标："+motionEvent.getY());
                // 创建Rect区域
                Rect focusArea = new Rect();
                focusArea.left = Math.max(areaX - 100, -1000); // 取最大或最小值，避免范围溢出屏幕坐标
                focusArea.top = Math.max(areaY - 100, -1000);
                focusArea.right = Math.min(areaX + 100, 1000);
                focusArea.bottom = Math.min(areaY + 100, 1000);
                // 创建Camera.Area
                Camera.Area cameraArea = new Camera.Area(focusArea, 1000);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                if (mParameters.getMaxNumMeteringAreas() > 0) {
                    meteringAreas.add(cameraArea);
                    focusAreas.add(cameraArea);
                }
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置对焦模式
                mParameters.setFocusAreas(focusAreas); // 设置对焦区域
                mParameters.setMeteringAreas(meteringAreas); // 设置测光区域
                try {
                    mCamera.cancelAutoFocus(); // 每次对焦前，需要先取消对焦
                    mCamera.setParameters(mParameters); // 设置相机参数
                    mCamera.autoFocus(mAutoFocusCallback); // 开启对焦

                } catch (Exception e) {
                }

                    return false;
            }
        });
    }
    /**
     * 相机对焦  默认不需要延时
     *
     * @param point
     */
    private void onCameraFocus(final Point point) {
        onCameraFocus(point, false);

    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    /**
     * 相机对焦
     *
     * @param point
     * @param needDelay 是否需要延时
     */
    public void onCameraFocus(final Point point, boolean needDelay) {
        long delayDuration = needDelay ? 300 : 0;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {


//                    if (onFocus(point, mAutoFocusCallback)) {
////                        mFocusImageView.startFocus(point);
//
//
//                }
            }
        }, delayDuration);
    }
    /**
     * 手动聚焦
     *
     * @param point 触屏坐标
     */
    protected boolean onFocus(Point point, Camera.AutoFocusCallback callback) {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //不支持设置自定义聚焦，则使用自动聚焦，返回

        if(Build.VERSION.SDK_INT >= 14) {

            if (parameters.getMaxNumFocusAreas() <= 0) {
                return focus(callback);
            }


            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int left = point.x - 300;
            int top = point.y - 300;
            int right = point.x + 300;
            int bottom = point.y + 300;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            try {
                //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
                //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return false;
            }
        }


        return focus(callback);
    }
    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setmAutoFocusCallback(Camera.AutoFocusCallback mAutoFocusCallback) {
        this.mAutoFocusCallback = mAutoFocusCallback;
    }

    private void getScreenMatrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }
    /**
     * 开始预览相机内容
     */
    private void startPreview(Camera camera ,SurfaceHolder holder){
        try {
            camera.setPreviewDisplay(holder);
            //将Camera预览角度进行调整
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takePicture(Camera.ShutterCallback mShutterCallback, Camera.PictureCallback rawPictureCallback, Camera.PictureCallback jpegPictureCallback) {
        if (mCamera != null)

            mCamera.takePicture(mShutterCallback, rawPictureCallback, jpegPictureCallback);
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!checkCameraHardware(getContext()))
            return;
        if (mCamera == null) {
            isOpen = safeCameraOpen(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (!isOpen) {
            return;
        }
        mCamera.setDisplayOrientation(ORIENTATION);
        try {
            mCamera.setPreviewDisplay(holder);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
//           setCameraParams(mScreenWidth, mScreenHeight);

            Camera.Parameters parameters = mCamera.getParameters();
            // 设置照片格式
            parameters.setPictureFormat(PixelFormat.JPEG);

            //parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);//加上闪光灯模式会报错
            // 1连续对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            Log.i("7777",this.getWidth()+"888"+this.getHeight());



            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();//获取所有支持的camera尺寸
            Camera.Size size1 = sizeList.get(0);

            for (int i=0;i<sizeList.size();i++){
                Camera.Size size = sizeList.get(i);
                LogUtil.showTestInfo(size.width+"=="+size.height);


            }
            Camera.Size optionSize = getOptimalPreviewSize(sizeList, this.getWidth(),this.getHeight());//获取一个最为适配的camera.size
//            parameters.setPreviewSize(optionSize.width,optionSize.height);//把camera.size赋值到parameters
          parameters.setPictureSize(640,480);//把camera.size赋值到parameters
//           parameters.setPictureSize(size1.width,size1.height);//把camera.size赋值到parameters

//            LogUtil.showTestInfo(optionSize.width+"=="+optionSize.height);


//
//            //设置大小和方向等参数
//		 设置照相机参数
            mCamera.setParameters(parameters);
            // 开始拍照
            mCamera.startPreview();
//            mCamera.cancelAutoFocus();// 一定要加上这句，才可以连续聚集

            mCamera.startPreview();
            mCamera.autoFocus(mAutoFocusCallback);

        }
    }

    /**
     * 解决预览变形问题
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCameraAndPreview();
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);

            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.i("xingji",e.getMessage());
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    public interface  onListener{
        void OnListener(Point point);
    }
    /**
     *定义一个变量储存数据
     */
    private onListener listener;
    /**
     *提供公共的方法,并且初始化接口类型的数据
     */
    public void setListener( onListener listener){
        this.listener = listener;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

           Toast.makeText(getContext(), "聚焦成功", Toast.LENGTH_SHORT).show();
    }

    private void setCameraParams(int width, int height) {

        Camera.Parameters parameters = mCamera.getParameters();
//        Log.i("4444", String.valueOf(width+"-"height));

        Log.i("4444", String.valueOf(mCamera.getParameters().getPreviewSize().width)+String.valueOf(mCamera.getParameters().getPreviewSize().height));

        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            picSize = parameters.getPictureSize();
        }
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);

        this.setLayoutParams(new RelativeLayout.LayoutParams((int) (height * (h / w)), height));
        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);

        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 选取合适的分辨率
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }
}
