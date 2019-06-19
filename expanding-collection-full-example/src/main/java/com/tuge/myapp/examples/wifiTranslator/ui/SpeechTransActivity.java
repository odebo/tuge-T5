package com.tuge.myapp.examples.wifiTranslator.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.asr.OnRecognizeListener;
import com.baidu.translate.asr.TransAsrClient;
import com.baidu.translate.asr.TransAsrConfig;
import com.baidu.translate.asr.data.RecognitionResult;
import com.baidu.translate.wifitranslator.WifiTranslatorConfig;
import com.example.library.banner.BannerLayout;
import com.facebook.rebound.SpringConfig;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.ListBean;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MenuListener;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MyAdapter;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.adapter.WebBannerAdapter;
import com.tuge.myapp.examples.wifiTranslator.view.WaveLineView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public  class SpeechTransActivity extends Activity implements MenuListener {
    SpringMenu mSpringMenu;
    private WaveLineView waveLineView;

    TitleBar mTitleBar;
    TextView mRecogResult;
    TextView mTransRusult;


    //    // 【重要】 - 语音翻译功能关键类
    private TransAsrClient client;
    private TransAsrConfig config;
    private static final String APP_ID = "20190514000297564";
    private static final String SECRET_KEY = "CCsnJhtXmT4MHULTQpNI";
    private static final String TAG = "444444";

    private LinearLayout mCardLayout;
//    SeekBar mTensionbar, mFrictionBar;
//
//    TextView mTvTension, mTvFriction;
//
//    ImageView mIvIgnore;
//
//    RadioGroup mRgFade;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_trans);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mRecogResult = findViewById(R.id.recogResult);
        mTransRusult = findViewById(R.id.transResult);
        mCardLayout = findViewById(R.id.cardLayout);

        initClient();
//        initData();
        String string = getSignature();

        Log.i("string",string+"00000"+this.getPackageName());


//        mRgFade = (RadioGroup) findViewById(R.id.rg_enablefade);
//        mFrictionBar = (SeekBar) findViewById(R.id.sb_friction);
//        mTensionbar = (SeekBar) findViewById(R.id.sb_tension);
//        mTvFriction = (TextView) findViewById(R.id.tv_friction);
//        mTvTension = (TextView) findViewById(R.id.tv_tension);
//        mIvIgnore = (ImageView) findViewById(R.id.iv_ignore);
//        mRgFade.setOnCheckedChangeListener(this);
//        mTensionbar.setOnSeekBarChangeListener(this);
//        mFrictionBar.setOnSeekBarChangeListener(this);
//        mFrictionBar.setMax(100);
//        mTensionbar.setMax(100);
        //init SpringMenu
        mSpringMenu = new SpringMenu(this, R.layout.view_menu);
        mSpringMenu.setMenuListener(this);
        mSpringMenu.setFadeEnable(true);
        mSpringMenu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));
        mSpringMenu.setDragOffset(0.4f);
//        mSpringMenu.addIgnoredView(mFrictionBar
//        );
//        mSpringMenu.addIgnoredView(mTensionbar);
        // init titlebar
//        mTitleBar.setLeftText("回退");
//        mTitleBar.setBackgroundColor(Color.parseColor("#008cc7"));
        mTitleBar.setBackgroundColor(Color.parseColor("#cccccc"));
        mTitleBar.setDividerColor(Color.GRAY);
        mTitleBar.setTitleColor(Color.WHITE);
//        mTitleBar.setElevation(10);
//        mTitleBar.setLeftTextColor(Color.WHITE);
        mTitleBar.setActionTextColor(Color.WHITE);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                mSpringMenu.setDirection(SpringMenu.DIRECTION_LEFT);
//                mSpringMenu.openMenu();
            }
        });
        mTitleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return "";
            }

            @Override
            public int getDrawable() {
                return R.mipmap.icon_menu;
            }

            @Override
            public void performAction(View view) {
//                点击菜单
                mSpringMenu.setDirection(SpringMenu.DIRECTION_RIGHT);
                mSpringMenu.openMenu();
            }
        });

        ListBean[] listBeen = {new ListBean(R.mipmap.icon_home, getString(R.string.home)), new ListBean(R.mipmap.icon_speech, getString(R.string.speechTranslate)), new ListBean(R.mipmap.icon_photo, getString(R.string.photoTranslate)), new ListBean(R.mipmap.icon_ask, getString(R.string.ask)),new ListBean(R.mipmap.icon_simu, getString(R.string.simultaneous)),new ListBean(R.mipmap.icon_group, getString(R.string.GroupTranslate)),new ListBean(R.mipmap.icon_setting, getString(R.string.Setting))};
        MyAdapter adapter = new MyAdapter(this, listBeen);
        ListView listView = (ListView) mSpringMenu.findViewById(R.id.test_listView);
        listView.setAdapter(adapter);

//        LayoutInflater.from(this).inflate(getContentView(),viewContent);
//        init(savedInstanceState);

//        mFrictionBar.setProgress(3);
//        mTensionbar.setProgress(20);

//        mSpringMenu.addIgnoredView(mIvIgnore);





        waveLineView = (WaveLineView) findViewById(R.id.waveLineView);

        findViewById(R.id.speechBtn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                    waveLineView.setVisibility(View.VISIBLE);

                    waveLineView.startAnim();
                    mCardLayout.setVisibility(View.GONE);
                    startRecognize();

                }else if (motionEvent.getAction()== MotionEvent.ACTION_UP){

                    waveLineView.stopAnim();
                    stopRecognize();

                }


                return false;
            }
        });



    }

    public  void  initData(){



        BannerLayout recyclerBanner =  findViewById(R.id.recycler);
        mCardLayout.setVisibility(View.VISIBLE);
        recyclerBanner.setItemSpace(50);
        recyclerBanner.setCenterScale(Float.valueOf("1.5"));
        recyclerBanner.setShowIndicator(false);
        List<String> list = new ArrayList<>();
        list.add("http://img0.imgtn.bdimg.com/it/u=3184221534,2238244948&fm=27&gp=0.jpg");
        list.add("http://img3.imgtn.bdimg.com/it/u=3967183915,4078698000&fm=27&gp=0.jpg");
        list.add("http://img0.imgtn.bdimg.com/it/u=1352823040,1166166164&fm=27&gp=0.jpg");
        list.add("http://img3.imgtn.bdimg.com/it/u=2293177440,3125900197&fm=27&gp=0.jpg");
        list.add("http://img4.imgtn.bdimg.com/it/u=1794621527,1964098559&fm=27&gp=0.jpg");
        list.add("http://img4.imgtn.bdimg.com/it/u=1243617734,335916716&fm=27&gp=0.jpg");
        WebBannerAdapter  webBannerAdapter=new WebBannerAdapter(this,list);
//        webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Toast.makeText(SpeechTransActivity.this, "点击了第  " + position+"  项", Toast.LENGTH_SHORT).show();
//            }
//        });
//
        recyclerBanner.setAdapter(webBannerAdapter);
        waveLineView.setVisibility(View.INVISIBLE);

    }

    public  String getSignature(){

        PackageManager manager = this.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(),manager.GET_SIGNATURES);
            Signature[] signatures= info.signatures;

            if (signatures!=null&&signatures.length>0){


                Signature signature = signatures[0];

                return signature.toCharsString();


            }


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return "";


    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mSpringMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onMenuOpen() {
        Toast.makeText(this, "Menu is opened!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuClose() {
        Toast.makeText(this, "Menu is closed!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressUpdate(float value, boolean bouncing) {

    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//        if (checkedId == R.id.radioButton) {
//            mSpringMenu.setFadeEnable(true);
//        } else {
//            mSpringMenu.setFadeEnable(false);
//        }
//    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (seekBar == mTensionbar) {
////            mTvTension.setText("Tension:" + progress);
//        } else {
////            mTvFriction.setText("Fricsion:" + progress);
//        }
////        mSpringMenu.setMenuSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 3));
//    }

//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//
//    }

    //    protected abstract int getContentView();
//
//    protected abstract void init(Bundle saveInstanceState);


    //开始识别
    private void startRecognize() {
        Log.d(TAG, "开始语音识别");


        // 重新设置一下config。
//        client.setConfig(config);
        // ======================== 配置结束================================


        Map<String, ?> extraParams = WifiTranslatorConfig.getTranslatorConfig(this, "mix_zh_en");

        Log.i("ttttttt",extraParams.toString());


        // 	开始识别，调用这个函数
        client.startRecognize(extraParams);
        // 【重要】开始语音识别
//        client.startRecognize("zh", "en");
    }

    // 停止识别
    private void stopRecognize() {
        Log.d(TAG, "语音识别结束");
        // 【重要】停止语音识别（有回调）
        client.stopRecognize();

        // 【重要】取消语音识别（没有回调）
        // client.cancelRecognize();
    }
    ////初始化翻译配置
    private void initClient() {
        // 【重要】语音翻译配置类
        // appId及私钥，可在API平台 管理控制台 查看
        config = new TransAsrConfig(APP_ID, SECRET_KEY);
        // 构造client
//        config.setTtsCombined(true);
        WifiTranslatorConfig wifiTranslatorConfig = new WifiTranslatorConfig();


        config.addExtraParams(WifiTranslatorConfig.getTranslatorConfig(this, "mix_zh_en"));
        Log.i("ttttttt",WifiTranslatorConfig.getTranslatorConfig(this, "mix_zh_en").toString());
        client = new TransAsrClient(this, config);


        // 设置回调
        client.setRecognizeListener(new OnRecognizeListener() {
            @Override
            public void onRecognized(int resultType, @NonNull RecognitionResult result) {
                if (resultType == OnRecognizeListener.TYPE_PARTIAL_RESULT) { // 中间结果
                    Log.d(TAG, "中间识别结果：" + result.getAsrResult());
//                    resultText.append(getString(R.string.partial_update_title, result.getAsrResult()));
//                    resultText.append("\n");

                } else if (resultType == OnRecognizeListener.TYPE_FINAL_RESULT) { // 最终结果
                    if (result.getError() == 0) { // 表示正常，有识别结果
                        Log.d(TAG, "最终识别结果：" + result.getAsrResult());

                        initData();

                        mRecogResult.setText(result.getAsrResult());
                        mTransRusult.setText(result.getTransResult());


                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
//                        DOWNLOAD();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                        Log.d(TAG, "翻译结果：" + result.getTransResult());

                    }else if (resultType == OnRecognizeListener.TYPE_TTS_MP3_DATA) { // 最终结果
                        if (result.getError() == 0) { // 表示正常，有识别结果
                            Log.d(TAG, "最终识别结果：" + result.getAsrResult());


                        }
                    }else if (resultType == OnRecognizeListener.TYPE_TTS_PCM_DATA) { // 最终结果
                        if (result.getError() == 0) { // 表示正常，有识别结果
                            Log.d(TAG, "最终识别结果：" + result.getAsrResult());


                        }
                    }


                    else { // 翻译出错
                        Log.d(TAG, "语音翻译出错 错误码：" + result.getError() + " 错误信息：" + result.getErrorMsg());

                    }

                }
            }

        });
    }
}
