package com.moly.hooyee.praiseview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moly.hooyee.praise.PraiseView;

public class MainActivity extends AppCompatActivity {
    private PraiseView mPraiseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPraiseView = (PraiseView) findViewById(R.id.pv_praise);
//        mPraiseView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPraiseView.animation();
//            }
//        });
    }
}
