package com.example.spms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.spms.binderService.InboundService;
import com.example.spms.check.checkJobsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.block11)
    LinearLayout block11;

    @BindView(R.id.block12)
    LinearLayout block12;

    @BindView(R.id.block21)
    LinearLayout block21;

    @BindView(R.id.block22)
    LinearLayout block22;

    @BindView(R.id.block31)
    LinearLayout block31;

    @BindView(R.id.block32)
    LinearLayout block32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        block11.setOnClickListener(new buttonListener());
        block12.setOnClickListener(new buttonListener());
        block21.setOnClickListener(new buttonListener());
        block22.setOnClickListener(new buttonListener());
        block31.setOnClickListener(new buttonListener());
        block32.setOnClickListener(new buttonListener());

        Intent intent = new Intent(this, InboundService.class);
        startService(intent);
    }

//    所有的block绑定ButtonListener
    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.block11:
                    Intent intent = new Intent(MainActivity.this, checkJobsActivity.class);
                    startActivity(intent);
                    Log.d(TAG, "onClick: this is block11");
                    break;
                case R.id.block12:
                    Log.d(TAG, "onClick: this is block12");
                    break;
                case R.id.block21:
                    Log.d(TAG, "onClick: this is block21");
                    break;
                case R.id.block22:
                    Log.d(TAG, "onClick: this is block22");
                    break;
                case R.id.block31:
                    Log.d(TAG, "onClick: this is block31");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, InboundService.class);
        stopService(intent);
    }
}