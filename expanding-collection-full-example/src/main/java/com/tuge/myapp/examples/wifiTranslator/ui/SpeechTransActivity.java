package com.tuge.myapp.examples.wifiTranslator.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.asr.OnRecognizeListener;
import com.baidu.translate.asr.OnTTSPlayListener;
import com.baidu.translate.asr.TransAsrClient;
import com.baidu.translate.asr.TransAsrConfig;
import com.baidu.translate.asr.data.RecognitionResult;
import com.baidu.translate.wifitranslator.WifiTranslatorConfig;
import com.example.library.banner.BannerLayout;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.githang.statusbar.StatusBarCompat;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.ListBean;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.LogUtil;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MenuListener;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MyAdapter;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.MainActivity;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.adapter.WebBannerAdapter;
import com.tuge.myapp.examples.wifiTranslator.view.PrinterTextView;
import com.tuge.myapp.examples.wifiTranslator.view.WaveLineView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.tuge.translatorlib.TranslatorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import com.skydoves.powermenudemo.customs.adapters.CenterMenuAdapter;
//import com.skydoves.powermenudemo.customs.adapters.CustomDialogMenuAdapter;
//import com.skydoves.powermenudemo.customs.items.NameCardMenuItem;


public  class SpeechTransActivity extends Activity implements MenuListener, View.OnClickListener {

    private PrinterTextView mPrinterTViewLangA;
    private PrinterTextView mPrinterTViewLangB;

    SpringMenu mSpringMenu;
    private WaveLineView waveLineView;

    private DisplayMetrics metrics;

    TitleBar mTitleBar;
    TextView mRecogResult;
    TextView mTransRusult;
    ArrayList<String> mImagelist,mKeyWord;
    private  Dialog dialog;

    private  String resultId;
    private  ImageView mPlayIV;
    private int isOtherPage;

    // 【重要】 - 语音翻译功能关键类
    private TransAsrClient client;
    private TransAsrConfig config;
    private static final String APP_ID = "20190514000297564";
    private static final String SECRET_KEY = "CCsnJhtXmT4MHULTQpNI";
    private static final String TAG = "444444";

    private LinearLayout mCardLayout;
//
    private   String imamgeSearchStr ;
//    判断识别语言是否为中文
    private  boolean isAsrResult;
//    SeekBar mTensionbar, mFrictionBar;
//
//    TextView mTvTension, mTvFriction;
//
//    ImageView mIvIgnore;
//
//    RadioGroup mRgFade;

    // 翻译模式Map
    private Map<String, String> transModeMap = new HashMap<>();
    private Map<String, Object> transModes = new HashMap<>();

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
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mRecogResult = findViewById(R.id.recogResult);
        mTransRusult = findViewById(R.id.transResult);
        mCardLayout = findViewById(R.id.cardLayout);

        mPrinterTViewLangA = (PrinterTextView) findViewById(R.id.pt_langA);
        mPrinterTViewLangB = (PrinterTextView) findViewById(R.id.pt_langB);

        mPlayIV = findViewById(R.id.play);
        isOtherPage = getIntent().getIntExtra("flag",0);

        initClient();
//        initData();

        // 打印提示文案
        startPrint();

        // 翻译模式选项卡
        initTransModeSele();

        String string = getSignature();

        Log.i("string",string+"00000"+this.getPackageName());

        //init SpringMenu
        mSpringMenu = new SpringMenu(this, R.layout.view_menu);
        mSpringMenu.setMenuListener(this);
        mSpringMenu.setFadeEnable(true);
        mSpringMenu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));
        mSpringMenu.setDragOffset(0.4f);


        mTitleBar.setBackgroundColor(Color.WHITE);
//        mTitleBar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryBlue));
        mTitleBar.setDividerColor(Color.GRAY);
        mTitleBar.setTitleColor(this.getResources().getColor(R.color.colorPrimaryBlue));
        mTitleBar.setActionTextColor(Color.WHITE);
        mTitleBar.setTitle(curTransModeTxt);
        mTitleBar.setTitleSize(14);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOtherPage==1){

                    Intent intent = new Intent(SpeechTransActivity.this,MainActivity.class);
                    startActivity(intent);
                }else {
                    finish();
                }
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

        ListBean[] listBeen = {new ListBean(R.mipmap.icon_home, getString(R.string.home)), new ListBean(R.mipmap.icon_speech, getString(R.string.speechTranslate)), new ListBean(R.mipmap.icon_photo, getString(R.string.photoTranslate)), new ListBean(R.mipmap.icon_ask, getString(R.string.ask)),new ListBean(R.mipmap.icon_simu, getString(R.string.simultaneous)),new ListBean(R.mipmap.icon_group, getString(R.string.GroupTranslate)),new ListBean(R.mipmap.icon_setting, getString(R.string.Setting))};
        MyAdapter adapter = new MyAdapter(this, listBeen);
        ListView listView = (ListView) mSpringMenu.findViewById(R.id.test_listView);
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyAdapter.onListener() {
            @Override
            public void OnListener(int position) {
                if (position==0){

                    finish();
                }else if(position==1){

                    mSpringMenu.closeMenu();
                }else if(position==2){

                    Intent intent = new Intent(SpeechTransActivity.this,CameraActivity.class);
                    intent.putExtra("flag",1);
                    startActivity(intent);
                }else{

                    Toast.makeText(SpeechTransActivity.this,"该功能正在开发中",Toast.LENGTH_SHORT).show();

                }
            }
        });


//        LayoutInflater.from(this).inflate(getContentView(),viewContent);
//        init(savedInstanceState);

//        mFrictionBar.setProgress(3);
//        mTensionbar.setProgress(20);

//        mSpringMenu.addIgnoredView(mIvIgnore);





        waveLineView = (WaveLineView) findViewById(R.id.waveLineView);
//        initData();

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

    private void initTransModeSele() {
        getMetrics();

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
                .setWidth(metrics.widthPixels)
                .setMenuShadow(10)
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



    public  void  initData(String result, ArrayList<String> keyWord){

        if (keyWord.size()>0) {
            SpannableString spannableString =  getString(result,keyWord);

            if (isAsrResult) {
                mRecogResult.setText(spannableString);
            }else {

                mTransRusult.setText(spannableString);


            }
            waveLineView.setVisibility(View.INVISIBLE);

        }else{

//            mRecogResult.setText(result);

        }

        BannerLayout recyclerBanner =  findViewById(R.id.recycler);
        mCardLayout.setVisibility(View.VISIBLE);
        recyclerBanner.setItemSpace(50);

        recyclerBanner.setCenterScale(Float.valueOf("1.2"));
        recyclerBanner.setShowIndicator(false);
//         mImagelist = new ArrayList<>();
//        mImagelist.add("http://img0.imgtn.bdimg.com/it/u=1906633814,2989154540&fm=26&gp=0.jpg");
//        mImagelist.add("http://img1.imgtn.bdimg.com/it/u=2070401313,2266534250&fm=26&gp=0.jpg");
//        mImagelist.add("http://img0.imgtn.bdimg.com/it/u=2148165365,2638783661&fm=26&gp=0.jpg");
//        mImagelist.add("http://img3.imgtn.bdimg.com/it/u=2293177440,3125900197&fm=27&gp=0.jpg");
//        mImagelist.add("http://img4.imgtn.bdimg.com/it/u=1794621527,1964098559&fm=27&gp=0.jpg");
//        mImagelist.add("http://img4.imgtn.bdimg.com/it/u=1243617734,335916716&fm=27&gp=0.jpg");
        WebBannerAdapter  webBannerAdapter=new WebBannerAdapter(this,mImagelist);

        webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position, ImageView imageView) {

                Drawable drawable = imageView.getDrawable();
                ImageView imageView1 = new ImageView(SpeechTransActivity.this);
                imageView1.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
                imageView1.setImageDrawable(drawable);
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog!=null && dialog.isShowing()){
                            LogUtil.showTestInfo("取消dialog");
                            dialog.dismiss();
                        }
                    }
                });
                dialog = new Dialog(SpeechTransActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(imageView1);
                dialog.show();
            }
            });


//

        recyclerBanner.setAdapter(webBannerAdapter);

        waveLineView.setVisibility(View.INVISIBLE);

    }



    //    高亮显示专词
    private SpannableString getString(String all, ArrayList<String> keyWord){

        SpannableString spannableString = new SpannableString(all);
        for (int i=0;i<keyWord.size();i++) {

            Pattern p = Pattern.compile(keyWord.get(i));

            Matcher m = p.matcher(spannableString);

            while (m.find()) {
                int start = m.start();
                int end = m.end();

                spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
//}
        return spannableString;
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
//        Toast.makeText(this, "Menu is opened!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuClose() {
//        Toast.makeText(this, "Menu is closed!!!", Toast.LENGTH_SHORT).show();
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

        Log.i("ttttttt",transModeMap.get(curTransModeTxt));


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
        config.setTtsCombined(true);
        config.setAutoPlayTts(false);
        config.setLogEnabled(true);
        config.setPartialCallbackEnabled(true);
        WifiTranslatorConfig wifiTranslatorConfig = new WifiTranslatorConfig();
        config.addExtraParams(WifiTranslatorConfig.getTranslatorConfig(this, "mix_zh_en"));
        Log.i("ttttttt",WifiTranslatorConfig.getTranslatorConfig(this, "mix_zh_en").toString());
        client = new TransAsrClient(this, config);


        // 设置回调
        client.setRecognizeListener(new OnRecognizeListener() {
            @Override
            public void onRecognized(int resultType, @NonNull RecognitionResult result) {
                if (resultType == OnRecognizeListener.TYPE_PARTIAL_RESULT) { // 中间结果

                    mRecogResult.setText(result.getAsrResult());
                    mPrinterTViewLangA.setVisibility(View.INVISIBLE);
                    mPrinterTViewLangB.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "中间识别结果：" + result.getAsrResult());
//                    resultText.append(getString(R.string.partial_update_title, result.getAsrResult()));
//                    resultText.append("\n");

                } else if (resultType == OnRecognizeListener.TYPE_FINAL_RESULT) { // 最终结果
                    if (result.getError() == 0) { // 表示正常，有识别结果
                        Log.d(TAG, "最终识别结果：" + result.getAsrResult()+result.getFrom()+result.getTo());


//                        initData();


                        mPrinterTViewLangA.setVisibility(View.INVISIBLE);
                        mPrinterTViewLangB.setVisibility(View.INVISIBLE);

                        mRecogResult.setText(result.getAsrResult());
                        mTransRusult.setText(result.getTransResult());

                        mPlayIV.setVisibility(View.VISIBLE);
                        resultId =  result.getId();



                        if (result.getFrom().equals("zh")){

                            imamgeSearchStr = result.getAsrResult();
                            isAsrResult = true;

                        }else if(result.getTo().equals("zh")){

                            imamgeSearchStr = result.getTransResult();
                            isAsrResult = false;
                        }
                        if (imamgeSearchStr==null)return;


                        String finalImamgeSearchStr = imamgeSearchStr;
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {

                                    imageSearch(imamgeSearchStr);
                                    imamgeSearchStr=null;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                        Log.d(TAG, "翻译结果：" + result.getTransResult());

                    }

                }

                else if (resultType == OnRecognizeListener.TYPE_TTS_MP3_DATA) {
                    if (result.getError() == 0) { // 表示正常，有识别结果

                        Log.d(TAG, "最终mp3：" + result.getAsrResult());


                    }
                }else if (resultType == OnRecognizeListener.TYPE_TTS_PCM_DATA) {
                    if (result.getError() == 0) { // 表示正常，有识别结果

                        Log.d(TAG, "最终pcm：" + result.getAsrResult());

                    }
                }


                else { // 翻译出错

                    Toast.makeText(SpeechTransActivity.this,result.getErrorMsg(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "语音翻译出错 错误码：" + result.getError() + " 错误信息：" + result.getErrorMsg());

                }
            }

        });
    }

//     专词识别+图搜

    private  void  imageSearch(String result) throws JSONException {

        Log.i("search",result);

        String searchResult = TranslatorUtils.getSearchResult(result);
Log.i("search",searchResult);
        JSONArray searchResultArray = new JSONArray(searchResult);
        mImagelist = new ArrayList<>();
        mKeyWord = new ArrayList<>();


        for (int i=0;i<searchResultArray.length();i++){

            JSONObject object = searchResultArray.getJSONObject(i);


            mImagelist.add(object.getString("ObjUrl"));


            mKeyWord.add(object.getString("Lexer"));

        }
Log.i("666666666",mKeyWord.toString());
        SpeechTransActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

              initData(result,mKeyWord);
            }
        });

}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isOtherPage==1) {

            Intent intent = new Intent(SpeechTransActivity.this,MainActivity.class);
            startActivity(intent);

        }
    }


    // 获取屏幕像素
    private void getMetrics() {

        metrics =new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;

    }

    /**
     * 开始打印
     */
    private void startPrint() {
        mPrinterTViewLangA.setPrintText("Press and hold mic to speak", 100, "|");
        mPrinterTViewLangA.startPrint();

        mPrinterTViewLangB.setPrintText("按住麦克风说话", 300, "|");
        mPrinterTViewLangB.startPrint();

    }
    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case  R.id.play:
                Log.i("SHIFOUFHIFFF",String.valueOf(isMainThread()));
                mPlayIV.setImageDrawable(getResources().getDrawable(R.drawable.playing));


                client.play(resultId, new OnTTSPlayListener() {
                    @Override
                    public void onPlayStop(String s) {

                        mPlayIV.setImageDrawable(getResources().getDrawable(R.drawable.play));


                    }

                    @Override
                    public void onPlayFailed(String s, int i, String s1) {
//                        Looper.prepare();
//                        Toast.makeText(SpeechTransActivity.this,s+s1,Toast.LENGTH_SHORT).show();

                        LogUtil.showTestInfo(s+s1);
//                        Looper.loop();

                    }
                });

                break;
                default:
                    break;

        }

    }
}
