package com.sian0412.privatelocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_SAVE = 100;
    private static final int LOCATION_REVISE = 101;
    private ListView listView;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor maincursor; 
    private SearchView searchView;
    private int index;
    private Context context;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        refreshListView();
        searchWitgets();
    }

    private void searchWitgets() {
        //修改搜尋圖片
        int imageSearchId =
                searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon",null,null);
        ImageView searchButton = (ImageView)searchView.findViewById(imageSearchId);
        searchButton.setImageResource(R.drawable.search);
        searchView.setIconifiedByDefault(false);
        //修改字體大小顏色
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp
        textView.setTextColor(getResources().getColor(R.color.light_green));
        textView.setHintTextColor(getResources().getColor(R.color.light_green));
        searchView.setQueryHint("搜尋名稱");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText.trim();
                Cursor cursor = TextUtils.isEmpty(newText) ? null : queryData(newText);
                if(cursor != null) {
                    adapter.changeCursor(cursor);
                }else {
                    maincursor = null;
                    refreshListView();
                }
                return false;
            }
        });

    }
    private Cursor queryData(String s) {
        Cursor cursor = db.rawQuery("SELECT _id, name, address, phone, remark FROM location_list WHERE name LIKE '%"+s+"%'",null);
        return cursor;
    }

    private void refreshListView() {
        if(maincursor == null){
            //1.取得查詢所有資料的cursor
            maincursor = db.rawQuery("SELECT _id, name, address, phone, remark FROM location_list",null);
            adapter = new SimpleCursorAdapter(context,R.layout.row, maincursor,
                    new String[] {"_id","name","address","phone"},
                    new int[]{R.id.itemId,R.id.itemName,R.id.itemAddress,R.id.itemPhone},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            listView.setAdapter(adapter);

        }else{
            if(maincursor.isClosed()){
                //彌補requery() 不會檢查cursor closed的問題
                maincursor = null;
                refreshListView();
            }else{
                maincursor.requery();//若資料龐大 不建議使用此方法(應改用CursorLoader)
                adapter.changeCursor(maincursor);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void findView() {
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener());
        searchView = (SearchView) findViewById(R.id.searchView);
        context =this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_menu:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,AddLocation.class);
                startActivityForResult(intent,LOCATION_SAVE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        refreshListView();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == LOCATION_SAVE && resultCode == RESULT_OK){
            LocationData mLocationData = new LocationData();
            mLocationData = data.getParcelableExtra("locationData");
            ContentValues cv = new ContentValues();
            cv.put("name",mLocationData.getName());
            cv.put("address",mLocationData.getAddress());
            cv.put("phone",mLocationData.getPhone());
            cv.put("remark",mLocationData.getRemarkColumn());
            long id = db.insert("location_list",null,cv);
            refreshListView();


        }else if(requestCode == LOCATION_REVISE && resultCode == RESULT_OK){
            String name = data.getStringExtra("name");
            String address = data.getStringExtra("address");
            String phone = data.getStringExtra("phone");
            String remark = data.getStringExtra("remark");
            ContentValues cv = new ContentValues();
            cv.put("name",name);
            cv.put("address",address);
            cv.put("phone",phone);
            cv.put("remark",remark);
            int rowcount = db.update("location_list",cv,"_id=?",
                    new String[]{String.valueOf(index)});
            refreshListView();

        }else if(requestCode == LOCATION_REVISE && resultCode == RESULT_FIRST_USER){
            int rowcount = db.delete("location_list","_id=?",
                    new String[]{String.valueOf(index)});
            refreshListView();
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Details.class);
            Cursor cursor = (Cursor)parent.getItemAtPosition(position);
            db = dbHelper.getReadableDatabase();

            cursor.moveToPosition(position);
            int curid = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor = db.rawQuery("SELECT _id, name, address, phone, remark FROM location_list WHERE _id=?",
                    new String[]{String.valueOf(curid)});

            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String address = cursor.getString(2);
                String phone = cursor.getString(3);
                String remark = cursor.getString(4);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("remark", remark);
                startActivity(intent);
            }
            cursor.close();
        }
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this,"長按進行修改及刪除",Toast.LENGTH_SHORT).show();
            Cursor cursor = (Cursor)parent.getItemAtPosition(position);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Revise.class);
            db = dbHelper.getReadableDatabase();
            cursor.moveToPosition(position);
            int curid = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor = db.rawQuery("SELECT _id, name, address, phone, remark FROM location_list WHERE _id=?",
                    new String[]{String.valueOf(curid)});
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String address = cursor.getString(2);
                String phone = cursor.getString(3);
                String remark = cursor.getString(4);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("remark", remark);
                startActivityForResult(intent,LOCATION_REVISE);
            }
            index = curid;
            cursor.close();
            return false;
        }

    }
}