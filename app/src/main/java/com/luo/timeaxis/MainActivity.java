package com.luo.timeaxis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    private TimeAxisView mTimeAxisView;
    private String text[] = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimeAxisView = (TimeAxisView) findViewById(R.id.ta_view);
        mTimeAxisView.setTextList(text);
    }
}