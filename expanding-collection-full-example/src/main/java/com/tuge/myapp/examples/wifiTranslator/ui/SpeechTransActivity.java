package com.tuge.myapp.examples.wifiTranslator.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuEffect;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
//import com.skydoves.powermenudemo.customs.adapters.CenterMenuAdapter;
//import com.skydoves.powermenudemo.customs.adapters.CustomDialogMenuAdapter;
//import com.skydoves.powermenudemo.customs.items.NameCardMenuItem;


public  class SpeechTransActivity extends Activity implements MenuListener {

    SpringMenu mSpringMenu;
    private WaveLineView waveLineView;

    TitleBar mTitleBar;
    TextView mRecogResult;
    TextView mTransRusult;


    // 【重要】 - 语音翻译功能关键类
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

    // 翻译模式Map
    private Map<String, String> transModeMap = new HashMap<>();
    // 翻译语种选择菜单
    private static String curTransModeTxt = "中文 <-> 英语"; // 当前翻译模式
    private PowerMenu langMenu;
    private OnMenuItemClickListener<PowerMenuItem> onLanMenuItemClickListener =
            new OnMenuItemClickListener<PowerMenuItem>() {
                @Override
                public void onItemClick(int position, PowerMenuItem item) {

                    curTransModeTxt = item.getTitle();
                    langMenu.setSelectedPosition(position);

                    // 更新TitleBar的翻译模式文案
                    mTitleBar.setTitle(curTransModeTxt);
                }
            };
    private OnDismissedListener onLanMenuDismissedListener =
            new OnDismissedListener() {
                @Override
                public void onDismissed() {
                    Log.d("Test", "onDismissed hamburger menu");
                }
            };



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

        //init SpringMenu
        mSpringMenu = new SpringMenu(this, R.layout.view_menu);
        mSpringMenu.setMenuListener(this);
        mSpringMenu.setFadeEnable(true);
        mSpringMenu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));
        mSpringMenu.setDragOffset(0.4f);

//        mTitleBar.setBackgroundColor(Color.parseColor("#008cc7"));
        mTitleBar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryBlue));
        mTitleBar.setDividerColor(Color.GRAY);
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setActionTextColor(Color.WHITE);
        mTitleBar.setTitle(curTransModeTxt);
        mTitleBar.setTitleSize(14);
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
        mTitleBar.setCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (langMenu.isShowing()) {
                    langMenu.dismiss();
                    return;
                }
                langMenu.showAsDropDown(v);
            }
        });
//        mTitleBar.addAction(new TitleBar.Action() {
//            @Override
//            public String getText() {
//                return "";
//            }
//
//            @Override
//            public int getDrawable() {
//                return R.mipmap.icon_menu;
//            }
//
//            @Override
//            public void performAction(View view) {
////                点击菜单
//                mSpringMenu.setDirection(SpringMenu.DIRECTION_RIGHT);
//                mSpringMenu.openMenu();
//            }
//        });

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


        transModeMap.put("中文 <-> 英语", "mix_zh_en");
        transModeMap.put("中文 <-> 日语", "mix_zh_jp");
        transModeMap.put("中文 <-> 韩语", "mix_zh_kor");
        transModeMap.put("中文 <-> 法语", "mix_zh_fra");
        transModeMap.put("中文 <-> 德语", "mix_zh_de");
        transModeMap.put("中文 <-> 俄语", "mix_zh_ru");
        transModeMap.put("中文 <-> 泰语", "mix_zh_th");
        transModeMap.put("中文 <-> 西班牙语", "mix_zh_spa");
        transModeMap.put("中文 <-> 葡萄牙语", "mix_zh_pt");
        transModeMap.put("中文 <-> 阿拉伯语", "mix_zh_ara");
        transModeMap.put("日语 <-> 英语", "mix_jp_en");
        transModeMap.put("日语 <-> 韩语", "mix_jp_kor");
        transModeMap.put("英语 <-> 韩语", "mix_en_kor");

        langMenu = new PowerMenu.Builder(this)
                .addItem(new PowerMenuItem("中文 <-> 英语", true))
                .addItem(new PowerMenuItem("中文 <-> 日语", false))
                .addItem(new PowerMenuItem("中文 <-> 韩语", false))
                .addItem(new PowerMenuItem("中文 <-> 法语", false))
                .addItem(new PowerMenuItem("中文 <-> 德语", false))
                .addItem(new PowerMenuItem("中文 <-> 俄语", false))
                .addItem(new PowerMenuItem("中文 <-> 泰语", false))
                .addItem(new PowerMenuItem("中文 <-> 西班牙语", false))
                .addItem(new PowerMenuItem("中文 <-> 葡萄牙语", false))
                .addItem(new PowerMenuItem("中文 <-> 阿拉伯语", false))
                .addItem(new PowerMenuItem("日语 <-> 英语", false))
                .addItem(new PowerMenuItem("日语 <-> 韩语", false))
                .addItem(new PowerMenuItem("英语 <-> 韩语", false))
                .setAutoDismiss(true)
//                .setLifecycleOwner((LifecycleOwner)this)
                .setAnimation(MenuAnimation.SHOWUP_BOTTOM_RIGHT)
                .setWidth(300)
                .setMenuEffect(MenuEffect.BODY)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setTextColor(this.getResources().getColor(R.color.md_grey_800))
                .setTextSize(14)
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(this.getResources().getColor(R.color.colorPrimaryBlue))
                .setOnMenuItemClickListener(onLanMenuItemClickListener)
                .setOnDismissListener(onLanMenuDismissedListener)
                .setPreferenceName("HamburgerPowerMenu")
                .setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
                .build();

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

        Map<String, ?> extraParams = WifiTranslatorConfig.getTranslatorConfig(this, transModeMap.get(curTransModeTxt));

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
