package com.sian0412.privatelocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

public class AddLocation extends AppCompatActivity {


    private EditText etName, etAddress, etPhone, etRemarkColumn;
    private String name, address, phone, remarkColumn;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        findView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void findView() {
        etName = (EditText) findViewById(R.id.etName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etRemarkColumn = (EditText) findViewById(R.id.etRemarkColumn);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }else{
            name = new String(etName.getText().toString().trim());
            address = new String(etAddress.getText().toString().trim());
            phone = String.valueOf(etPhone.getText().toString().trim());
            remarkColumn = new String(etRemarkColumn.getText().toString().trim());
            if(name == null || name.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddLocation.this);
                builder.setMessage(R.string.restName);
                builder.setPositiveButton(R.string.ok, null);
                builder.show();
            }else{
                PacelableMethod();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void PacelableMethod() {
        LocationData locationData = new LocationData();
        locationData.setName(name);
        locationData.setAddress(address);
        locationData.setPhone(phone);
        locationData.setRemarkColumn(remarkColumn);

        intent = getIntent();
        intent.putExtra("locationData", locationData);
        setResult(RESULT_OK, intent);
        finish();
    }
}