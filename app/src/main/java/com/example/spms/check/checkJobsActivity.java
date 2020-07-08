package com.example.spms.check;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.acker.simplezxing.activity.CaptureActivity;
import com.example.spms.R;
import com.example.spms.binderService.InboundService;
import com.example.spms.check.entity.CheckJobs;
import com.example.spms.check.entity.CheckResults;
import com.example.spms.check.services.CheckStatus;
import com.example.spms.utils.Dbmanager;
import com.example.spms.utils.MySqliteHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class checkJobsActivity extends AppCompatActivity {

    @BindView(R.id.checkCard)
    QMUIGroupListView checkCard;

    @BindView(R.id.checkReceive)
    com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton button;

    private static final int REQ_CODE_PERMISSION = 0x1111;

    InboundService checkService;

    CheckJobs jobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkjobs);
        ButterKnife.bind(this);

        Intent intent = new Intent(this, InboundService.class);
        bindService(intent,conn,BIND_AUTO_CREATE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCheckJobsData();
                Log.d(TAG, "onClick: "+checkService.getCount());
                Log.d(TAG, "onClick: "+checkService.getJobs());
            }
        });
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            InboundService.MyBinder myBinder = (InboundService.MyBinder)binder;
            checkService = (InboundService)myBinder.getService();
            Log.d(TAG, "onServiceConnected: "+checkService.getCount());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof QMUICommonListItemView) {
                CharSequence text = ((QMUICommonListItemView) v).getText();
                Intent intent = new Intent(checkJobsActivity.this,ListActivity.class);
                intent.putExtra("queryNumber",((QMUICommonListItemView) v).getDetailText());
                startActivity(intent);
                Toast.makeText(checkJobsActivity.this, text + " is Clicked", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void getCheckJobsData() {
        MySqliteHelper helper = Dbmanager.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("check_head", null, null, null, null, null, null);//查询并获得游标
        for (int i = 0; i < c.getCount() && c != null; i++) {
            if (c.moveToFirst()) {//判断游标是否为空
                c.move(i);//移动到指定记录
                String name = c.getString(c.getColumnIndex("checkjob_id"));
                QMUICommonListItemView NameItem = checkCard.createItemView("验收单号");
                NameItem.setOrientation(QMUICommonListItemView.VERTICAL); //默认文字在左边
                NameItem.setDetailText(name);
                QMUIGroupListView.newSection(this).addItemView(NameItem, onClickListener).addTo(checkCard);
                Toast.makeText(checkJobsActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getCheckJobs () {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://aa006913-6b67-43db-adb5-4e2e5b2b2efe.mock.pstmn.io").addConverterFactory(GsonConverterFactory.create()).build();
        CheckStatus checkStatus = retrofit.create(CheckStatus.class);
        Call<CheckJobs> call = checkStatus.getCheckJobs();
        call.enqueue(new Callback<CheckJobs>() {
            @Override
            public void onResponse(Call<CheckJobs> call, Response<CheckJobs> response) {
                try{
                    CheckJobs jobs = response.body();
                    Log.i(TAG, "onResponse: jobsCatching success");
                    Log.i(TAG, "onResponse: "+jobs.getApplicantOrderList().get(0).getCheckOrder().getCheckOrderId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CheckJobs> call, Throwable t) {

            }
        });
    }

    private void getCheckResponse(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://aa006913-6b67-43db-adb5-4e2e5b2b2efe.mock.pstmn.io").addConverterFactory(GsonConverterFactory.create()).build();
        CheckStatus bookService = retrofit.create(CheckStatus.class);
        Call<List<CheckResults>> call = bookService.getCheckResults();
        call.enqueue(new Callback<List<CheckResults>>() {
            @Override
            public void onResponse(Call<List<CheckResults>> call, Response<List<CheckResults>> response) {
                try {
                    List<CheckResults> li = response.body();
                    Log.d(TAG, "onResponse: " + li.get(0).getGoodName());
                    for(int i=0;i<li.size();i++){
                        QMUICommonListItemView NameItem = checkCard.createItemView("备件名称");
                        NameItem.setOrientation(QMUICommonListItemView.VERTICAL); //默认文字在左边
                        NameItem.setDetailText(li.get(i).getGoodName());

                        QMUICommonListItemView requestNumber = checkCard.createItemView("应收数量");
                        requestNumber.setOrientation(QMUICommonListItemView.VERTICAL); //默认文字在左边
                        requestNumber.setDetailText(li.get(i).getRequestNumber());

                        QMUICommonListItemView actualNumber = checkCard.createItemView("实收数量");
                        actualNumber.setOrientation(QMUICommonListItemView.VERTICAL); //默认文字在左边
                        actualNumber.setDetailText(li.get(i).getRequestNumber());

                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v instanceof QMUICommonListItemView) {
                                    CharSequence text = ((QMUICommonListItemView) v).getText();
                                    Intent intent = new Intent(checkJobsActivity.this,ListActivity.class);
                                    intent.putExtra("queryNumber",((QMUICommonListItemView) v).getText());
                                    startActivity(intent);
                                    Toast.makeText(checkJobsActivity.this, text + " is Clicked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };//



                        QMUIGroupListView.newSection(checkJobsActivity.this)
                                .addItemView(NameItem,onClickListener)
                                .addItemView(requestNumber,onClickListener)
                                .addItemView(actualNumber,onClickListener)
                                .addTo(checkCard);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<CheckResults>> call, Throwable t) {

            }
        });
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(checkJobsActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "You must agree the camera permission request before you use the code scan function", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
//                        tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
//                            tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                }
                break;
        }
    }
}
