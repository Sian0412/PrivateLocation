package com.sian0412.privatelocation;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    private TextView tvDetailsName,tvDetailsAddress,tvDetailsPhone,tvDetailsRemarkColumn;
    private ImageButton imgbtnMap,imgbtnPhone;
    private Intent intent;
    private String phoneNum;
    private Context context;

    private String TAG="Details";
    private List<String> unPermissionList = new ArrayList<String>(); //申請未得到授權的許可權列表
    private AlertDialog mPermissionDialog = null;
    private static final int REQUEST_CALL_PHONE = 102;
    private String mPackName ;
    private String[] permissionList = new String[]{    //申請的許可權列表
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mPackName = getPackageName();
        context = this;
        findView();
        loadDetails();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void findView() {
        tvDetailsName = (TextView)findViewById(R.id.tvDetailsName);
        tvDetailsAddress = (TextView)findViewById(R.id.tvDetailsAddress);
        tvDetailsPhone = (TextView)findViewById(R.id.tvDetailsPhone);
        tvDetailsRemarkColumn = (TextView)findViewById(R.id.tvDetailsRemarkColumn);
        imgbtnMap = (ImageButton)findViewById(R.id.imgbtnMap);
        imgbtnMap.setOnClickListener(clickListener);
        imgbtnPhone = (ImageButton)findViewById(R.id.imgbtnPhone);
        imgbtnPhone.setOnClickListener(clickListener);
    }
    private void loadDetails() {

        intent = getIntent();
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        String remark = intent.getStringExtra("remark");
        tvDetailsName.setText(name);
        tvDetailsAddress.setText(address);
        tvDetailsPhone.setText(phone);
        tvDetailsRemarkColumn.setText(remark);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.imgbtnMap:
                    if(tvDetailsAddress.getText().toString() == null || tvDetailsAddress.getText().toString().equals("")){
                        Toast.makeText(context,R.string.address,Toast.LENGTH_SHORT).show();
                    }else{
                        String url = "https://www.google.com.tw/maps/place/"+tvDetailsAddress.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }

                    break;
                case R.id.imgbtnPhone:
                    if(tvDetailsPhone.getText().toString() == null || tvDetailsPhone.getText().toString().equals("") ){
                        Toast.makeText(Details.this,R.string.phoneNumber,Toast.LENGTH_SHORT).show();
                    }else{
                        phoneNum = tvDetailsPhone.getText().toString();
                        // 檢查手機版本是否是 6.0(含)以上
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            checkPermission();
                        }else {
                            // 已經是允許 或 6.0以下的版本，就可撥打電話
                            callPhone();
                        }
                    }
                    break;
            }
        }
    };

    //許可權判斷和申請
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        unPermissionList.clear();

        for(int i=0;i<permissionList.length;i++){
            if(ContextCompat.checkSelfPermission(this,permissionList[i])!=
                    PackageManager.PERMISSION_GRANTED){
                unPermissionList.add(permissionList[i]);
            }
        }

        if(unPermissionList.size()>0){
            requestPermissions(permissionList,REQUEST_CALL_PHONE);
            Log.i(TAG,"check 有許可權未通過");
        }else{
            Log.i(TAG,"check 許可權都已經申請通過");
            callPhone();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        boolean hasPermissionDismiss = false;
        if(REQUEST_CALL_PHONE == requestCode){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] == -1){
                    hasPermissionDismiss = true;
                    Log.i(TAG,"有許可權沒被通過");
                    break;
                }
            }
        }
        if(hasPermissionDismiss){//如果有 沒有被允許的許可權
            showPermissionDialog();
            Log.e("showPermissionDialog","showPermissionDialog");
        }else{
            //許可權已經通過了 程式可以打開
            Log.i(TAG,"onRequestPermissionsResult 都已經申請過");
        }
    }

    private void showPermissionDialog() {
        Log.i(TAG,"mPackName"+mPackName);
        if(mPermissionDialog == null){
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.no_Permission)
                    .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:"+mPackName);//到設定裡面設定
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //關閉頁面或者其他操作
                            cancelPermissionDialog();
                        }
                    })
                    .show();
        }
    }

    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }


    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}