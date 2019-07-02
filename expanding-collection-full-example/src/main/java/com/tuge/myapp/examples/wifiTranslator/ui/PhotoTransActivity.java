package com.tuge.myapp.examples.wifiTranslator.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.ocr.OcrCallback;
import com.baidu.translate.ocr.OcrClient;
import com.baidu.translate.ocr.OcrClientFactory;
import com.baidu.translate.ocr.entity.Language;
import com.baidu.translate.ocr.entity.OcrContent;
import com.baidu.translate.ocr.entity.OcrResult;
import com.facebook.rebound.SpringConfig;
import com.githang.statusbar.StatusBarCompat;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.ListBean;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.LogUtil;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MenuListener;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MyAdapter;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.view.AutoFitTextView;
import com.tuge.myapp.examples.wifiTranslator.view.CameraSurfaceView;
import com.tuge.myapp.examples.wifiTranslator.view.FitHeightTextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PipedReader;
import java.math.BigDecimal;
import java.util.List;

public class PhotoTransActivity extends Activity implements MenuListener {

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
    TitleBar mTitleBar;
    SpringMenu mSpringMenu;
    Double x=1.0,y=1.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        StatusBarCompat.setStatusBarColor(this, Color.WHITE);

        setContentView(R.layout.activity_photo_trans);

        mPicpath = getIntent().getStringExtra("picPath");
        oriLan = getIntent().getStringExtra("ori");
        desLan = getIntent().getStringExtra("des");
        LogUtil.showTestInfo(oriLan+desLan);


        mPic =findViewById(R.id.picIV);
        mcontainer = findViewById(R.id.container);
        scanImage = findViewById(R.id.scan_line);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setBackgroundColor(Color.WHITE);

        //init SpringMenu
        mSpringMenu = new SpringMenu(this, R.layout.view_menu);
        mSpringMenu.setMenuListener(this);
        mSpringMenu.setFadeEnable(true);
        mSpringMenu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));
        mSpringMenu.setDragOffset(0.4f);
        mSpringMenu.setAdapter(this);
//        ListBean[] listBeen = {new ListBean(R.mipmap.icon_home, getString(R.string.home)), new ListBean(R.mipmap.icon_speech, getString(R.string.speechTranslate)), new ListBean(R.mipmap.icon_photo, getString(R.string.photoTranslate)), new ListBean(R.mipmap.icon_ask, getString(R.string.ask)),new ListBean(R.mipmap.icon_simu, getString(R.string.simultaneous)),new ListBean(R.mipmap.icon_group, getString(R.string.GroupTranslate)),new ListBean(R.mipmap.icon_setting, getString(R.string.Setting))};
//        MyAdapter adapter = new MyAdapter(this, listBeen);
//        ListView listView = (ListView) mSpringMenu.findViewById(R.id.test_listView);
//        listView.setAdapter(adapter);

        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpringMenu.setDirection(SpringMenu.DIRECTION_RIGHT);
                mSpringMenu.openMenu();
            }
        });

        Bitmap bitmap = BitmapFactory.decodeFile(mPicpath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),R.drawable.test1,options);

        mPic.setImageBitmap(bitmap);

//        findViewById(R.id.scroller).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });
//扫描实现
        mTop2Bottom = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.7f);

        mBottom2Top = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.7f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f);

        mBottom2Top.setRepeatMode(Animation.RESTART);
        mBottom2Top.setInterpolator(new LinearInterpolator());
        mBottom2Top.setDuration(1500);
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
        mTop2Bottom.setDuration(1500);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                startTrans(mPicpath);

            }
        }).start();
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mTitleBar.measure(w, h);
        int height1 = mTitleBar.getMeasuredHeight();
//
        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();

        int height = wm.getDefaultDisplay().getHeight()-getStatusBarHeight(this)-50;

        x= KeepTwoDecimal(width,bitmap.getWidth());
        y= KeepTwoDecimal(height,bitmap.getHeight());

        Log.i("wk","图片的宽度:"+bitmap.getWidth()+"---"+width+"gaodu"+height+bitmap.getHeight()+"99"+height1);
        LogUtil.showTestInfo(width+"==999"+height);

//        Log.i("wk","图片的宽度:"+bitmap.getWidth()+"---"+width+"图片的高度"+bitmap.getHeight()+"--"+height);


    }
    private static double KeepTwoDecimal (int number1, int number2){
        double a=Double.valueOf(number1);
        double b=Double.valueOf(number2);
        double c=a/b;
        return c=(double)((int)(c*100.0))/100;
    }
    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        LogUtil.showTestInfo(height);
        return height;
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

                stopAnimation=true;
                scanImage.clearAnimation();
                scanImage.setVisibility(View.GONE);
                Log.i("TTTTTT", ocrResult.getErrorMsg()+ocrResult.getError());
                if (ocrResult.getError()!=0){
                    if (ocrResult.getError()==69004){

                        Toast.makeText(PhotoTransActivity.this,"未识别到可翻译的内容",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(PhotoTransActivity.this,"识别有误",Toast.LENGTH_SHORT).show();
                    return;
                }
                List<OcrContent> list = ocrResult.getContents();

                PhotoTransActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            for (int i = 0; i < list.size(); i++) {

                                OcrContent ocrContent = list.get(i);

                                AutoFitTextView textView = new AutoFitTextView(PhotoTransActivity.this,null);
                                textView.setText(ocrContent.getDst());

                                textView.setBackgroundResource(android.R.color.darker_gray);


                                int left = (int) (ocrContent.getRect().left);
                                int top = (int) (ocrContent.getRect().top);
                                int bottom = (int) (ocrContent.getRect().bottom);
                                int right = (int) (ocrContent.getRect().right);


                                FrameLayout.LayoutParams layoutParams= new FrameLayout.LayoutParams(-1,-1);

                                layoutParams.width = getInt((right-left)*x);
//                            (int) ((right-left)*x) ;

                                layoutParams.height = getInt((bottom-top)*y);;
                                LogUtil.showTestInfo(x+"-"+y+"-"+(right-left)*x+"-"+layoutParams.width+"-"+(bottom-top)*y+"-"+layoutParams.height);
                                layoutParams.setMargins(getInt(left*x),getInt(top*y),0,0);//4个参数按顺序分别是左上右下

//                    layoutParams.setMargins((int) (left*x),(int) (top*y),0,0);//4个参数按顺序分别是左上右下

                                mcontainer.addView(textView);

                                textView.setLayoutParams(layoutParams);

                                Log.i("TTTTTT", ocrContent.getDst()+"zuobian"+ocrContent.getRect());


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });



            }
        });

    }
    public static int getInt(double number){
        BigDecimal bd=new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }
    public int stringToInt(String string){  int j = 0;

        String str = string.substring(0, string.indexOf(".")) + string.substring(string.indexOf(".") + 1);

        int intgeo = Integer.parseInt(str);

        return intgeo;
    }

    @Override
    public void onMenuOpen() {

    }

    @Override
    public void onMenuClose() {

    }

    @Override
    public void onProgressUpdate(float value, boolean bouncing) {

    }
}
