package com.tuge.myapp.examples.wifiTranslator.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.translatorlib.TranslatorUtils;

public class ObjectRecActivity extends AppCompatActivity {
    private ImageView mPic;
    private String    mPicpath;
    private FrameLayout mcontainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏无状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_object_rec);

        mPicpath = getIntent().getStringExtra("picPath");

        Bitmap bitmap = BitmapFactory.decodeFile(mPicpath);

        mPic =findViewById(R.id.picIV);
        mPic.setImageBitmap(bitmap);
        mcontainer = findViewById(R.id.container);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    objectRecog(mPicpath);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();




    }

    private  void objectRecog(String path){


      String  result = TranslatorUtils.getImageInfo(path);

//      mcontainer.setVisibility(View.VISIBLE);

        Log.i("识别到的结果",result);

    }
}
