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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
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
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.LogUtil;
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
    private String oriLan,desLan;
//    扫描线
    Animation mTop2Bottom, mBottom2Top;
    boolean stopAnimation = false;
    private ImageView  scanImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo_trans);

        mPicpath = getIntent().getStringExtra("picPath");
        oriLan = getIntent().getStringExtra("ori");
        desLan = getIntent().getStringExtra("des");
        LogUtil.showTestInfo(oriLan+desLan);


        mPic =findViewById(R.id.picIV);
        mcontainer = findViewById(R.id.container);
        scanImage = findViewById(R.id.scan_line);
        Bitmap bitmap = BitmapFactory.decodeFile(mPicpath);
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inScaled = false;
        Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),R.drawable.test1,options);

        mPic.setImageBitmap(bitmap);

        findViewById(R.id.scroller).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

//扫描实现
        mTop2Bottom = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.7f);

        mBottom2Top = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.7f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f);

        mBottom2Top.setRepeatMode(Animation.RESTART);
        mBottom2Top.setInterpolator(new LinearInterpolator());
        mBottom2Top.setDuration(1200);
        mBottom2Top.setFillEnabled(true);//使其可以填充效果从而不回到原地
        mBottom2Top.setFillAfter(true);//不回到起始位置
//如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        mBottom2Top.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (!stopAnimation){
                    scanImage.startAnimation(mTop2Bottom);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTop2Bottom.setRepeatMode(Animation.RESTART);
        mTop2Bottom.setInterpolator(new LinearInterpolator());
        mTop2Bottom.setDuration(1200);
        mTop2Bottom.setFillEnabled(true);
        mTop2Bottom.setFillAfter(true);
        mTop2Bottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!stopAnimation){
                    scanImage.startAnimation(mBottom2Top);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scanImage.startAnimation(mTop2Bottom);
        startTrans(mPicpath);


        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        LogUtil.showTestInfo(width+"==999"+height);

        Log.i("wk","图片的宽度:"+bitmap.getWidth()+"图片的高度"+bitmap.getHeight());


    }



    public void  startTrans(String path){


        OcrClient client = OcrClientFactory.create(this, appId, appKey);

        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),R.drawable.test1,options);

        // 源语言方向：Language.ZH，目标语言方向:Language.EN，详见技术文档
        client.getOcrResult(oriLan, desLan, bitmap, new OcrCallback() {
            @Override
            public void onOcrResult(OcrResult ocrResult) {

                stopAnimation=true;
                scanImage.clearAnimation();
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

                    layoutParams.width = (right-left) ;

                    layoutParams.height = (bottom-top);
                    layoutParams.setMargins(left,top,0,0);//4个参数按顺序分别是左上右下

                    mcontainer.addView(textView);

                    textView.setLayoutParams(layoutParams);

                    Log.i("TTTTTT", ocrContent.getDst()+"zuobian"+ocrContent.getRect());


                }

            }
        });

    }
}
