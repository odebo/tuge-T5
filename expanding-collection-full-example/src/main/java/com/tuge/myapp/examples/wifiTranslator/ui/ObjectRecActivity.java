package com.tuge.myapp.examples.wifiTranslator.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

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

public class ObjectRecActivity extends Activity {
    private ImageView mPic;
    private String    mPicpath;
    private FrameLayout mcontainer;
    private ArrayList<String> results;
    private String[] data = {
            "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    private void initData(){

        RecogResultAdapter adapter = new RecogResultAdapter(this,results);

        ListView listView =  findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }

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
                results.add("名称：" + "&" + object.getString("currencyName"));
                results.add("代码：" + "&" + object.getString("currencyCode"));
                results.add("面值：" + "&" + object.getString("currencyDenomination"));
                results.add("年份：" + "&" + object.getString("year"));
            }else{

                results.add("currencyName");
            }
        }else if(obj.has("redwine")){


             JSONObject object = obj.getJSONObject("redwine");
             if (object.getInt("hasdetail")==1) {
                 results.add("名称：" + "&" + object.getString("wineNameCn"));
                 results.add("国家：" + "&" + object.getString("countryCn"));
                 results.add("产区：" + "&" + object.getString("regionCn"));
                 results.add("酒庄：" + "&" + object.getString("wineryCn"));
                 results.add("糖分：" + "&" + object.getString("classifyBySugar"));

             }else{

                 results.add("wineNameCn");
             }

         }else {




                 try {

                     Iterator it = obj.keys();
                     String vol = "";//值
                     String key = null;//键
                     while (it.hasNext()) {//遍历JSONObject
                         key = (String) it.next().toString();
                         vol = obj.getString(key);
                         Log.i("vvvvv", vol);

                         results.add(vol);

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
}
