package com.tuge.myapp.examples.wifiTranslator.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.ocr.OcrCallback;
import com.baidu.translate.ocr.OcrClient;
import com.baidu.translate.ocr.OcrClientFactory;
import com.baidu.translate.ocr.entity.Language;
import com.baidu.translate.ocr.entity.OcrContent;
import com.baidu.translate.ocr.entity.OcrResult;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.view.AutoFitTextView;
import com.tuge.myapp.examples.wifiTranslator.view.CameraSurfaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PipedReader;
import java.util.List;

public class PhotoTransActivity extends Activity {

    private static final String appId = "20190514000297564";
    private static final String appKey = "CCsnJhtXmT4MHULTQpNI";
    private ImageView  mPic;
    private String    mPicpath;
    private FrameLayout mcontainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_trans);

        mPicpath = getIntent().getStringExtra("picPath");


        mPic =findViewById(R.id.picIV);
        mcontainer = findViewById(R.id.container);
        Bitmap bitmap = BitmapFactory.decodeFile(mPicpath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),R.drawable.test1,options);

        mPic.setImageBitmap(bitmap);
        startTrans(mPicpath);




        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
       Log.i("wk","图片的宽度:"+bitmap.getWidth()+"图片的高度"+bitmap.getHeight());


    }



    public void  startTrans(String path){


        OcrClient client = OcrClientFactory.create(this, appId, appKey);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path);

      Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),R.drawable.test1,options);

        // 源语言方向：Language.ZH，目标语言方向:Language.EN，详见技术文档
        client.getOcrResult(Language.ZH, Language.EN, bitmap, new OcrCallback() {
            @Override
            public void onOcrResult(OcrResult ocrResult) {
                Log.i("TTTTTT", ocrResult.getErrorMsg()+ocrResult.getError());
                if (ocrResult.getError()!=0){

                    Toast.makeText(PhotoTransActivity.this,"识别有误",Toast.LENGTH_SHORT).show();
                    return;
                }

                List<OcrContent> list = ocrResult.getContents();

                for (int i = 0; i < list.size(); i++) {

                    OcrContent ocrContent = list.get(i);

                        AutoFitTextView textView = new AutoFitTextView(PhotoTransActivity.this,null);
                        textView.setText(ocrContent.getDst());

                        textView.setBackgroundResource(android.R.color.darker_gray);


                        int left = (int) (ocrContent.getRect().left);
                        int top = (int) (ocrContent.getRect().top);
                        int bottom = (int) (ocrContent.getRect().bottom);
                        int right = (int) (ocrContent.getRect().right);

                    FrameLayout.LayoutParams layoutParams= new FrameLayout.LayoutParams(-2,-2);

                    layoutParams.width = right-left ;

                    layoutParams.height = bottom-top;
                    layoutParams.setMargins(left,top,0,0);//4个参数按顺序分别是左上右下

                    mcontainer.addView(textView);

                    textView.setLayoutParams(layoutParams);

                    Log.i("TTTTTT", ocrContent.getDst()+"zuobian"+ocrContent.getRect());


                }

            }
        });

    }
}
