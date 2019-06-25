package com.tuge.myapp.examples.wifiTranslator.ui;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.rebound.SpringConfig;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.ListBean;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MenuListener;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.MyAdapter;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;
import com.tuge.myapp.examples.wifiTranslator.MainActivity;
import com.tuge.myapp.examples.wifiTranslator.R;
import com.tuge.myapp.examples.wifiTranslator.adapter.RecogResultAdapter;
import com.tuge.translatorlib.TranslatorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObjectRecActivity extends Activity implements MenuListener {
    private ImageView mPic;
    TitleBar mTitleBar;
    private String    mPicpath;
    private FrameLayout mcontainer;
    private ArrayList<String> results;
    private String[] data = {
            "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };
    //    扫描线
    Animation mTop2Bottom, mBottom2Top;
    boolean stopAnimation = false;
    private ImageView  scanImage;
    SpringMenu mSpringMenu;
    private boolean isGeneral=false;
    private  JSONObject generalRecResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_object_rec);

        mPicpath = getIntent().getStringExtra("picPath");



        Bitmap bitmap = BitmapFactory.decodeFile(mPicpath);
        Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),R.drawable.test1);

        mPic =findViewById(R.id.picIV);
        scanImage = findViewById(R.id.scan_line);
        mPic.setImageBitmap(bitmap);
        mcontainer = findViewById(R.id.container);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);


        //init SpringMenu
        mSpringMenu = new SpringMenu(this, R.layout.view_menu);
        mSpringMenu.setMenuListener(this);
        mSpringMenu.setFadeEnable(true);
        mSpringMenu.setChildSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20, 5));
        mSpringMenu.setDragOffset(0.4f);
        ListBean[] listBeen = {new ListBean(R.mipmap.icon_home, getString(R.string.home)), new ListBean(R.mipmap.icon_speech, getString(R.string.speechTranslate)), new ListBean(R.mipmap.icon_photo, getString(R.string.photoTranslate)), new ListBean(R.mipmap.icon_ask, getString(R.string.ask)),new ListBean(R.mipmap.icon_simu, getString(R.string.simultaneous)),new ListBean(R.mipmap.icon_group, getString(R.string.GroupTranslate)),new ListBean(R.mipmap.icon_setting, getString(R.string.Setting))};
        MyAdapter adapter = new MyAdapter(this, listBeen);
        ListView listView = (ListView) mSpringMenu.findViewById(R.id.test_listView);
        listView.setAdapter(adapter);
        mTitleBar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimaryBlue));
        mTitleBar.setDividerColor(Color.GRAY);
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setActionTextColor(Color.WHITE);
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

//        initData();



        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                   objectRecog(mPicpath);
//                    objectRecog(getResourcesUri(R.drawable.pic_rec_test));


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();




    }
    private View addView() {
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);  //LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//  LayoutInflater inflater2 = getLayoutInflater();
        LayoutInflater inflater3 = LayoutInflater.from(this);
        View view = inflater3.inflate(R.layout.item_gerenal_rec, null);
     TextView des =  view.findViewById(R.id.des);
     TextView name =  view.findViewById(R.id.name);
//     ImageView iv= view.findViewById(R.id.pic);
//     iv.setImageDrawable(getResources().getDrawable(R.drawable.card01));

     name.setText(results.get(0));
        try {
            JSONObject baike_info = new JSONObject(results.get(1));
            if (baike_info.length()==0){
                des.setVisibility(View.GONE);
            }
            des.setText(baike_info.getString("description"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Glide.with(this).load("http://baike.baidu.com/item/%E7%AC%94%E8%AE%B0%E6%9C%AC%E7%94%B5%E8%84%91/213561").into(iv);

//        view.setLayoutParams(lp);

        return view;
    }
    private void initData(){


        stopAnimation=true;
        scanImage.clearAnimation();
        mcontainer.setVisibility(View.VISIBLE);
        if (isGeneral){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1,-2);
            layoutParams.gravity = Gravity.BOTTOM;

            mcontainer.addView(addView(),layoutParams);


            return;

        }


        RecogResultAdapter adapter = new RecogResultAdapter(this,results);

        ListView listView =  findViewById(R.id.listView);
        listView.setAdapter(adapter);
        isGeneral =false;

    }
    private String getResourcesUri(@DrawableRes int id) {
        Resources resources = getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }


    //万物识别
    private  void objectRecog(String path) throws JSONException {

        results = new ArrayList<>();

      String  result =
//              "{ \"wineNameCn\": \"奔富Bin2西拉幕合怀特红葡萄酒\", \"regionEn\": \"South Australia\", \"color\": \"深紫红色/Dark Violet\", \"wineNameEn\": \"Penfolds Bin 2 Shiraz Mataro\", \"hasdetail\": 1, \"wineryCn\": \"奔富\", \"classifyBySugar\": \"干型/Dry\", \"tasteTemperature\": \"12-18℃\", \"regionCn\": \"南澳\", \"wineryEn\": \"Penfolds\", \"countryEn\": \"Australia\", \"description\": \"此酒为深紫红色\"}";
              TranslatorUtils.getImageInfo(path);
        Log.i("识别到的结果",result);



        JSONObject obj = new JSONObject(result);

         if (obj.has("currency")){

            JSONObject object = obj.getJSONObject("currency");
            if (object.getInt("hasdetail")==1) {
                results.add("名称 " + "&" + object.getString("currencyName"));
                results.add("代码 " + "&" + object.getString("currencyCode"));
                results.add("面值 " + "&" + object.getString("currencyDenomination"));
                results.add("年份 " + "&" + object.getString("year"));
            }else{

                results.add("currencyName");
            }
        }else if(obj.has("redwine")){


             JSONObject object = obj.getJSONObject("redwine");
             if (object.getInt("hasdetail")==1) {
                 results.add("名称 " + "&" + object.getString("wineNameCn"));
                 results.add("国家 " + "&" + object.getString("countryCn"));
                 results.add("产区 " + "&" + object.getString("regionCn"));
                 results.add("酒庄 " + "&" + object.getString("wineryCn"));
                 results.add("糖分 " + "&" + object.getString("classifyBySugar"));

             }else{

                 results.add("wineNameCn");
             }

         }
         else {

//                generalRecResult = obj;

//
                 try {

                     Iterator it = obj.keys();
                     String vol = "";//值
                     String key = null;//键
                     while (it.hasNext()) {//遍历JSONObject
                         key = (String) it.next().toString();
                         vol = obj.getString(key);
                         if (key.equals("score")||key.equals("root")) continue;
                         if (key.equals("baike_info")){
                            isGeneral = true;
                             JSONObject jsonObject = new JSONObject(vol);
                             results.add(jsonObject.toString());

                         }else {

                             results.add(0,vol);
                         }

                     }




                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }



        ObjectRecActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    initData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
//      mcontainer.setVisibility(View.VISIBLE);






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
