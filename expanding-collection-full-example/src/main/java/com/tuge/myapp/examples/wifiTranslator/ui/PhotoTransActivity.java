package com.tuge.myapp.examples.wifiTranslator.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.tuge.myapp.examples.wifiTranslator.DetailActivity.SpringMenu;
import com.tuge.myapp.examples.wifiTranslator.DetailActivity.TitleBar;

public class PhotoTransActivity extends Activity {
    SpringMenu mSpringMenu;
    private LinearLayout parentLinearLayout; //把父类的activity和子类的activity的view都add到这里来

    TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("EEEEE","EEEE");

    }


}
