package com.example.spms.check;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.example.spms.R;
import com.example.spms.utils.Constant;
import com.example.spms.utils.Dbmanager;
import com.example.spms.utils.MySqliteHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import static android.content.ContentValues.TAG;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class ListActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 0x1111;

    private ListView lv;
    private SQLiteDatabase db;
    private MySqliteHelper helper;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private String queryNumber;

    final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(ListActivity.this);

    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        queryNumber = bundle.getString("queryNumber");
        Log.i(TAG, "onCreate: "+queryNumber);
        lv = (ListView) findViewById(R.id.lv);
        helper = Dbmanager.getInstance(this);
        db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from check_detail where checkjob_id_copy =?",new String[]{queryNumber});
        if(cursor!=null&cursor.moveToFirst()){
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex("item_id"));
                Log.i(TAG, "onCreate: "+name);
            }
        }
        adapter=new SimpleCursorAdapter(this, R.layout.item,cursor,
                new String[]{Constant.ITEM_ID,Constant.REQUESTNUMBER,Constant.ACTUALNUMBER,Constant.CHECKJOB_ID_COPY},
                new int[]{R.id.li_modelnumber,R.id.li_name,R.id.li_number,R.id.li_location},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            }
        });
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(ListActivity.this, CaptureActivity.class);
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
                        if(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT).length()==9){
                            builder.setTitle("料号是"+data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }else{
                            builder.setTitle("库位码是"+data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        builder
//                             .setTitle("验收单号： "+queryNumber)
                               .setPlaceholder("在此输入数量")
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确认", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        Toast.makeText( ListActivity.this, builder.getEditText().getText(), Toast.LENGTH_SHORT ).show();
                                        MySqliteHelper helper = Dbmanager.getInstance(ListActivity.this);
                                        SQLiteDatabase db = helper.getWritableDatabase();
                                        String sql = "update check_detail set actual_number ="+builder.getEditText().getText()+" where item_id ="+data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT) +" AND checkjob_id_copy = '"+queryNumber+"'";
                                        db.execSQL(sql);
                                        db.close();
                                        Intent intent = new Intent(ListActivity.this,ListActivity.class);
                                        intent.putExtra("queryNumber",queryNumber);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                }).show();
                          Toast.makeText( ListActivity.this, data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT), Toast.LENGTH_SHORT ).show();
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            Toast.makeText( ListActivity.this, data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT), Toast.LENGTH_SHORT ).show();
                        }
                        break;
                }
                break;
        }
    }
}
