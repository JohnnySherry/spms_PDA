package com.example.spms.binderService;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.spms.check.entity.CheckJobs;
import com.example.spms.check.entity.CheckResults;
import com.example.spms.check.entity.QueryObject;
import com.example.spms.check.services.CheckStatus;
import com.example.spms.utils.Constant;
import com.example.spms.utils.Dbmanager;
import com.example.spms.utils.MySqliteHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InboundService extends Service {

    private final static String TAG = "wzj";
    private int count;
    private boolean quit;
    private Thread thread;
    MyBinder myBinder = new MyBinder();
    private CheckJobs jobs;
    private List<CheckResults> li;
    MySqliteHelper helper;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends Binder {
        public InboundService getService(){
            return InboundService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getCheckJobs();
        getCheckResponse();
        Log.i(TAG, "Service is invoke Created");
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 每间隔一秒count加1 ，直到quit为true。
                while (!quit) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        });
        thread.start();
    }

    public void getCheckJobs () {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://aa006913-6b67-43db-adb5-4e2e5b2b2efe.mock.pstmn.io").addConverterFactory(GsonConverterFactory.create()).build();
        CheckStatus checkStatus = retrofit.create(CheckStatus.class);
//
        Call<CheckJobs> call = checkStatus.getCheckJobs();
        call.enqueue(new Callback<CheckJobs>() {
            @Override
            public void onResponse(Call<CheckJobs> call, Response<CheckJobs> response) {
                try{
                    jobs = response.body();
                    if(jobs!=null){
                        Log.i(TAG, "onResponse: jobsCatching success");
                        Log.i(TAG, "onResponse: "+jobs.getApplicantOrderList().get(0).getCheckOrder().getCheckOrderId());
                        helper = Dbmanager.getInstance(InboundService.this);
                        SQLiteDatabase db=helper.getWritableDatabase();
                        for(int i=0;i<jobs.getApplicantOrderList().size();i++) {
//校验
//                            String sql0 = "select * from check_head where checkjob_id = ?";
////                            Cursor cursor = db.rawQuery(sql0, new String[]{jobs.getApplicantOrderList().get(i).getCheckOrder().getCheckOrderId()});
//                            Cursor cursor = db.rawQuery(sql0, new String[]{"gana20200612"});
//                            Log.i(TAG, "onResponse:验收表单数量 "+cursor.getCount());
//                            if (cursor.getCount()==0) {
//                                String sql = "insert into " + Constant.TABLE_NAME_CHECKHEAD + "(" + Constant._ID + "," + Constant.CHECKJOB_ID + ") values(null,'" + jobs.getApplicantOrderList().get(i).getCheckOrder().getCheckOrderId() + "')";
//                                for (int j = 0; j < jobs.getApplicantOrderList().get(i).getCheckOrderDetails().size(); j++) {
//                                    String sql_detail = "insert into " + Constant.TABLE_NAME_CHECKDETAIL + "(" + Constant._ID + "," + Constant.ITEM_ID + "," + Constant.REQUESTNUMBER + "," + Constant.ACTUALNUMBER + "," + Constant.CHECKJOB_ID_COPY + ") values(null,'" + jobs.getApplicantOrderList().get(i).getCheckOrderDetails().get(j).getMaterialId() + "','" + jobs.getApplicantOrderList().get(i).getCheckOrderDetails().get(j).getRequestNumber() + "','" + jobs.getApplicantOrderList().get(i).getCheckOrderDetails().get(j).getActualNumber() + "','" + jobs.getApplicantOrderList().get(i).getCheckOrder().getCheckOrderId() + "')";
//                                    db.execSQL(sql_detail);
//                                }
//                                db.execSQL(sql);
//                            }
//                        }


//无校验
                            String sql="insert into "+ Constant.TABLE_NAME_CHECKHEAD+"("+Constant._ID+","+Constant.CHECKJOB_ID+") values(null,'"+jobs.getApplicantOrderList().get(i).getCheckOrder().getCheckOrderId()+"')";
                            for(int j=0;j<jobs.getApplicantOrderList().get(i).getCheckOrderDetails().size();j++){
                                String sql_detail="insert into "+ Constant.TABLE_NAME_CHECKDETAIL+"("+Constant._ID+","+Constant.ITEM_ID+","+Constant.REQUESTNUMBER+","+Constant.ACTUALNUMBER+","+Constant.CHECKJOB_ID_COPY+") values(null,'"+jobs.getApplicantOrderList().get(i).getCheckOrderDetails().get(j).getMaterialId()+"','"+jobs.getApplicantOrderList().get(i).getCheckOrderDetails().get(j).getRequestNumber()+"','"+jobs.getApplicantOrderList().get(i).getCheckOrderDetails().get(j).getActualNumber()+"','"+jobs.getApplicantOrderList().get(i).getCheckOrder().getCheckOrderId()+"')";
                                db.execSQL(sql_detail);
                            }
                            db.execSQL(sql);
                        }
//--------------------分割线-----------------------------
                        db.close();
                    }else{
                        Log.i(TAG, "onResponse: job is not yet executed!");
                    }
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
        CheckStatus checkService = retrofit.create(CheckStatus.class);
        Call<List<CheckResults>> call = checkService.getCheckResults();
        call.enqueue(new Callback<List<CheckResults>>() {
            @Override
            public void onResponse(Call<List<CheckResults>> call, Response<List<CheckResults>> response) {
                try {
                    li = response.body();
                    Log.d(TAG, "onResponse: " + li.get(0).getGoodName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<CheckResults>> call, Throwable t) {

            }
        });
    }

    /**
     * 公共方法
     * @return
     */
    public int getCount(){
        return count;
    }

    public CheckJobs getJobs(){
        return jobs;
    }

    public List<CheckResults> getList(){
        return li;
    }

    /**
     * 解除绑定时调用
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service is invoke onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is invoke Destroyed");
        this.quit = true;
        super.onDestroy();
    }

}
