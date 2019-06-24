package com.tuge.myapp.examples.wifiTranslator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.tuge.myapp.ECBackgroundSwitcherView;
import com.tuge.myapp.ECCardData;
import com.tuge.myapp.ECPagerView;
import com.tuge.myapp.ECPagerViewAdapter;
import com.tuge.myapp.examples.wifiTranslator.pojo.CardData;
import com.tuge.myapp.examples.wifiTranslator.ui.CameraActivity;
import com.tuge.myapp.examples.wifiTranslator.ui.PhotoTransActivity;
import com.tuge.myapp.examples.wifiTranslator.ui.SpeechTransActivity;
import com.tuge.myapp.examples.wifiTranslator.view.ItemsCountView;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class MainActivity extends Activity {

    private ECPagerView ecPagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initPermission();
//      String string = getSignature();


        // Create adapter for pager

        ExampleDataset  exampleDataset = new ExampleDataset();

        List<ECCardData> datasets = exampleDataset.getDataset();

        ECPagerViewAdapter adapter = new ECPagerViewAdapter(this, datasets) {
            @Override
            public void instantiateCard(LayoutInflater inflaterService, ViewGroup head, ListView list, final ECCardData data) {
                final CardData cardData = (CardData) data;


                int position = exampleDataset.getPostion(cardData);



                // Create adapter for list inside a card and set adapter to card content
                CommentArrayAdapter commentArrayAdapter = new CommentArrayAdapter(getApplicationContext(), cardData.getListItems());
                list.setAdapter(commentArrayAdapter);
                list.setDivider(getResources().getDrawable(R.drawable.list_divider));
                list.setDividerHeight((int) pxFromDp(getApplicationContext(), 0.5f));
                list.setSelector(R.color.transparent);
                list.setBackgroundColor(Color.WHITE);
                list.setCacheColorHint(Color.TRANSPARENT);


                // Add gradient to root header view
                View gradient = new View(getApplicationContext());
                gradient.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
                gradient.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_head_gradient));
                head.addView(gradient);

                // Inflate main header layout and attach it to header root view
                inflaterService.inflate(R.layout.simple_head, head);

                // Set header data from data object
                TextView title = (TextView) head.findViewById(R.id.title);
                title.setText(cardData.getHeadTitle());
                ImageView avatar = (ImageView) head.findViewById(R.id.avatar);
                avatar.setImageResource(cardData.getPersonPictureResource());
                TextView name = (TextView) head.findViewById(R.id.name);
                name.setText(cardData.getPersonName() + ":");
                TextView message = (TextView) head.findViewById(R.id.message);
                message.setText(cardData.getPersonMessage());

                // Add onclick listener to card header for toggle card state
                head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {


                        switch (position){

                            case 0:
                                startIntent(SpeechTransActivity.class);
                                break;

                            case 1:
                                startIntent(CameraActivity.class);
                                break;

                                default:

                                    break;

                        }






                    }
                });
            }
        };

        ecPagerView = (ECPagerView) findViewById(R.id.ec_pager_element);

        ecPagerView.setPagerViewAdapter(adapter);
        ecPagerView.setBackgroundSwitcherView((ECBackgroundSwitcherView) findViewById(R.id.ec_bg_switcher_element));

        final ItemsCountView itemsCountView = (ItemsCountView) findViewById(R.id.items_count_view);
        ecPagerView.setOnCardSelectedListener(new ECPagerView.OnCardSelectedListener() {
            @Override
            public void cardSelected(int newPosition, int oldPosition, int totalElements) {

               itemsCountView.update(newPosition, oldPosition, totalElements);
            }
        });
    }
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CHANGE_WIFI_STATE
        };
        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }
    private void  startIntent(Class activity){



        Intent intent = new Intent(MainActivity.this, activity);

        startActivity(intent);
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
    public void onBackPressed() {
        if (!ecPagerView.collapse())
            super.onBackPressed();
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
