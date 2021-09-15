package com.sian0412.privatelocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

public class Revise extends AppCompatActivity {

    private EditText etReviseName,etReviseAddress,etRevisePhone,etReviseRemarkColumn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        findView();
        intent = getIntent();
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        String remark = intent.getStringExtra("remark");

        etReviseName.setText(name);
        etReviseAddress.setText(address);
        etRevisePhone.setText(phone);
        etReviseRemarkColumn.setText(remark);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void findView() {
        etReviseName = (EditText)findViewById(R.id.etReviseName);
        etReviseAddress = (EditText)findViewById(R.id.etReviseAddress);
        etRevisePhone = (EditText)findViewById(R.id.etRevisePhone);
        etReviseRemarkColumn = (EditText)findViewById(R.id.etReviseRemarkColumn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.revise_delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else{
            switch (item.getItemId()) {
                case R.id.revise:

                    if (etReviseName.getText().toString() == null || etReviseName.getText().toString().equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Revise.this);
                        builder.setMessage(R.string.restName);
                        builder.setPositiveButton(R.string.ok, null);
                        builder.show();
                    } else {
                        String name = etReviseName.getText().toString();
                        String address = etReviseAddress.getText().toString();
                        String phone = etRevisePhone.getText().toString();
                        String remark = etReviseRemarkColumn.getText().toString();
                        intent.putExtra("name", name);
                        intent.putExtra("address", address);
                        intent.putExtra("phone", phone);
                        intent.putExtra("remark", remark);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    break;
                case R.id.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(Revise.this);

                    builder.setIcon(R.drawable.baseline_info_black_48dp);
                    builder.setTitle(R.string.notice);
                    builder.setMessage(R.string.sure_Delete);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_FIRST_USER,intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel,null);
                    builder.show();

                    break;
            }
        }
        return true;
    }

}