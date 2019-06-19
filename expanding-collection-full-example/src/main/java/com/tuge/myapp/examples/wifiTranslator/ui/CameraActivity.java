package com.tuge.myapp.examples.wifiTranslator.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.MainActivity;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.view.CameraSurfaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends Activity implements View.OnClickListener{
    private CameraSurfaceView mCameraSurfaceView;

    private Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String fileName= Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    +"tuge-T5"
                    +File.separator
                    +"PicTest_"+1+".jpg";
            savePic(data,fileName);

            Intent intent = new Intent(CameraActivity.this, PhotoTransActivity.class);

            intent.putExtra("picPath",fileName);


            startActivity(intent);


//            Toast.makeText(CameraActivity.this, "拍照成功", Toast.LENGTH_SHORT).show();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        ImageView img_take_photo = (ImageView) findViewById(R.id.img_take_photo);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.sv_camera);

        img_take_photo.setOnClickListener(this);

    }
    public void savePic(byte[] data,String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
//
        options.inSampleSize = 2;  //这里表示原来图片的1/2
        // 保存图片操作
        Bitmap bmp= BitmapFactory.decodeByteArray(data, 0, data.length,options);
        Matrix m = new Matrix();
        m.setRotate(90,(float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
        WindowManager wm = this.getWindowManager();

        final Bitmap bm = Bitmap.createBitmap(bmp, 0, 0,bmp.getWidth(), bmp.getHeight(), m, true);
        Log.i("4444",bmp.getWidth()+"999"+bmp.getHeight());

        File file=new File(fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdir();//创建文件夹
        }
        try {
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//向缓冲区压缩图片
            bos.flush();
            bos.close();
           setPictureDegreeZero(fileName);
//
        } catch (Exception e) {

            Log.i("4444",e.toString());
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
    public void takePhoto() {

        mCameraSurfaceView.takePicture(null, null, jpegPictureCallback);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.img_take_photo:

                takePhoto();

                break;

            default:
                break;


        }

    }
}
