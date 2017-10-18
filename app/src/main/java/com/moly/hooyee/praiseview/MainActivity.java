package com.moly.hooyee.praiseview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moly.hooyee.praise.PraiseView;
import com.moly.hooyee.praise.RecordView;

public class MainActivity extends AppCompatActivity {
    private PraiseView mPraiseView;
    private RecordView mRecordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPraiseView = (PraiseView) findViewById(R.id.pv_praise);

        mRecordView = (RecordView) findViewById(R.id.rv_record);
        mRecordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordView.addOne();
            }
        });

        findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordView.addOne();
            }
        });

        findViewById(R.id.bt_reduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordView.reduceOne();
            }
        });
//        mPraiseView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPraiseView.animation();
//            }
//        });
    }
}
