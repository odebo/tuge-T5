package com.tuge.myapp.examples.wifiTranslator.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.ocr.entity.Language;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.LogUtil;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.MainActivity;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.adapter.ArrayWheelAdapter;
import com.tuge.myapp.examples.wifiTranslator.view.BottomView;
import com.tuge.myapp.examples.wifiTranslator.view.CameraLineView;
import com.tuge.myapp.examples.wifiTranslator.view.CameraSurfaceView;
import com.tuge.myapp.examples.wifiTranslator.view.FocusImageView;
import com.tuge.myapp.examples.wifiTranslator.view.WheelView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CameraActivity";
    private CameraSurfaceView mCameraSurfaceView;
    private BottomView mBottomView;
    private RelativeLayout mLanLayout;
    private TextView mOriTV,mDesTV;
    private CameraLineView mCameraLineView;
    private ImageView mBackButton;
    private FocusImageView mFocusImageView;
    private boolean mSelectVisibility;
    boolean isTransPhoto = true;
    int isOtherPage;
    private  String oriLan="zh",desLan="en";
//    选择的语种
    private  String oriSelLan="中文",desSelLan="英语";

    // 翻译模式Map
    private Map<String, String> transModeMap = new HashMap<>();
    private String[] mAllLan ;
    Point mPoint;
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            if (mPoint!=null) {
                mFocusImageView.onFocusSuccess();
            }
            Log.i("bbb", String.valueOf(b));
        }
    };

    private Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String fileName = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + "tuge-T5"
                    + File.separator
                    + "PicTest_" + 1 + ".jpg";
            savePic(data, fileName);

            if (isTransPhoto) {
                startIntent(PhotoTransActivity.class, fileName);
            } else {
                startIntent(ObjectRecActivity.class, fileName);
            }
//            Toast.makeText(CameraActivity.this, "拍照成功", Toast.LENGTH_SHORT).show();
        }
    };

    private void startIntent(Class activity, String path) {
        Intent intent = new Intent(CameraActivity.this, activity);
        intent.putExtra("picPath", path);
        intent.putExtra("ori",oriLan);
        intent.putExtra("des",desLan);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ImageView img_take_photo = (ImageView) findViewById(R.id.img_take_photo);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.sv_camera);
        mOriTV = findViewById(R.id.tv_language_original);
        mDesTV = findViewById(R.id.tv_language_target);
        mLanLayout = findViewById(R.id.lan_select);
        mCameraLineView = findViewById(R.id.cameraLine);
        mCameraSurfaceView.setmAutoFocusCallback(autoFocusCallback);
        mFocusImageView = findViewById(R.id.focusImageView);
        isOtherPage = getIntent().getIntExtra("flag",0);
        mCameraSurfaceView.setListener(new CameraSurfaceView.onListener() {
            @Override
            public void OnListener(Point point)
            {

                mPoint = point;
                mFocusImageView.startFocus(point);
            }
        });
        mBackButton = findViewById(R.id.back);
        mBackButton.setOnClickListener(this);

        img_take_photo.setOnClickListener(this);


        //适配器
        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(this, R.layout.lang_layout, getResources().getStringArray(R.array.lang));
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBottomView = (BottomView) findViewById(R.id.bottomView);
        mBottomView.init();
        initTransData();

        mBottomView.findViewById(R.id.recog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomView.moveRight();
                mLanLayout.setVisibility(View.INVISIBLE);
                mCameraLineView.setVisibility(View.INVISIBLE);
                findViewById(R.id.wheel_layout).setVisibility(View.GONE);

                isTransPhoto = false;

            }
        });
        mBottomView.findViewById(R.id.trans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomView.moveLeft();
                mLanLayout.setVisibility(View.VISIBLE);
//                mCameraLineView.setVisibility(View.VISIBLE);
                isTransPhoto = true;

            }
        });
        initView();
//        mBottomView.moveLeft();
    }

    private void initView() {
        String [] languageArray = getResources().getStringArray(R.array.lang);
        List list = Arrays.asList(languageArray);

        WheelView originalWheelView = findViewById(R.id.wheel_original);
        WheelView targetWheelView = findViewById(R.id.wheel_target);
        originalWheelView.setAdapter(new ArrayWheelAdapter(list));
        targetWheelView.setAdapter(new ArrayWheelAdapter(list));
        originalWheelView.setCyclic(false);
        originalWheelView.setInitPosition(0);
        targetWheelView.setInitPosition(1);
        targetWheelView.setCyclic(false);

        originalWheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {

                oriSelLan = languageArray[index];
//
                list.remove(oriSelLan);
//                targetWheelView.setAdapter(new ArrayWheelAdapter(list));
//                targetWheelView.notify();

                if (oriSelLan.equals(desSelLan)){
                    targetWheelView.setCurrentItem(0);
                }
                Log.e(TAG, "源语言为: " + languageArray[index]);
            }
        });

        targetWheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                desSelLan = languageArray[index];
                if (oriSelLan.equals(desSelLan)){
                    originalWheelView.setCurrentItem(0);
                }
                Log.e(TAG, "翻译的源语言为: " + languageArray[index]);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void savePic(byte[] data, String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        options.inSampleSize = 2;  //这里表示原来图片的1/2
        // 保存图片操作
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        Matrix m = new Matrix();
        m.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
        WindowManager wm = this.getWindowManager();

        final Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
        Log.i("4444", bmp.getWidth() + "999" + bmp.getHeight());
//        int scale = Math.max(imageViewWidth / bitmapWidth, imageViewHeight / bitmapHeight);

        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();//创建文件夹
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//向缓冲区压缩图片
            bos.flush();
            bos.close();
            setPictureDegreeZero(fileName);
        } catch (Exception e) {
            Log.i("4444", e.toString());
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    public static void setPictureDegreeZero(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            // 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
            // 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_FLIP_VERTICAL));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //交换起始语言和目标语言
    public static String[] swapString(String ori,String des){

        String[] str = {ori,des};
        String temp=str[0];
        str[0]=str[1];
        str[1]=temp;

        return str;
    }

    public void takePhoto() {
        mCameraSurfaceView.takePicture(null, null, jpegPictureCallback);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_take_photo:
                takePhoto();
                break;

            case R.id.back:
                if (isOtherPage==1) {

                    Intent intent = new Intent(CameraActivity.this,MainActivity.class);
                    startActivity(intent);

                }else{


                    finish();

                }
                break;
            case R.id.trans:
                String [] lan = swapString(mOriTV.getText().toString(),mDesTV.getText().toString());
                oriLan = transModeMap.get(lan[0]);

                desLan = transModeMap.get(lan[1]);
                LogUtil.showTestInfo(oriLan+desLan);

                mOriTV.setText(lan[0]);
                mDesTV.setText(lan[1]);
                break;

            case R.id.lan_select:
                mSelectVisibility = !mSelectVisibility;
                int visibility = mSelectVisibility ? View.VISIBLE : View.GONE;
                findViewById(R.id.wheel_layout).setVisibility(visibility);
                findViewById(R.id.sure).setVisibility(visibility);
                break;
            case R.id.sure:
                mOriTV.setText(oriSelLan);
                mDesTV.setText(desSelLan);
                oriLan = transModeMap.get(oriSelLan);

                desLan = transModeMap.get(desSelLan);
                findViewById(R.id.wheel_layout).setVisibility(View.GONE);
                findViewById(R.id.sure).setVisibility(View.GONE);


                break;
        }
    }

    private  void initTransData (){

        transModeMap = new HashMap<>();


        transModeMap.put("中文", Language.ZH);
        transModeMap.put("英语", Language.EN);
        transModeMap.put("韩语", Language.KOR);
        transModeMap.put("日语", Language.JP);
        transModeMap.put("法语", Language.FRA);
        transModeMap.put("德语", Language.DE);
        transModeMap.put("意大利语", Language.IT);
        transModeMap.put("俄语", Language.RU);
        transModeMap.put("葡萄牙语", Language.PT);
        transModeMap.put("西班牙语", Language.SPA);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isOtherPage==1) {

            Intent intent = new Intent(CameraActivity.this,MainActivity.class);
            startActivity(intent);

        }
    }
}
