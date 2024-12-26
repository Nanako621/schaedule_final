package com.example.schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final String DB_NAME = "classes_db.db";
    public static final String TABLE_NAME = "classes_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化資料庫
        DBHelper dbHelper = new DBHelper(MainActivity.this, DB_NAME, null, 1);

        // 動態渲染課程表框架
        framework();

        // 將資料庫中的課程數據映射到課程表
        applyDraw(dbHelper);
    }

    // 根據星期選擇對應的 GridLayout
    public GridLayout LayoutColumn(int i) {
        switch (i) {
            case 1: return findViewById(R.id.d1);
            case 2: return findViewById(R.id.d2);
            case 3: return findViewById(R.id.d3);
            case 4: return findViewById(R.id.d4);
            case 5: return findViewById(R.id.d5);
            case 6: return findViewById(R.id.d6);
            case 7: return findViewById(R.id.d7);
            default: return findViewById(R.id.d1);
        }
    }

    // 渲染課程表格框架
    public void framework() {
        int id = 1;
        for (int i = 1; i <= 7; i++) {
            GridLayout gridLayout = LayoutColumn(i);
            for (int j = 1; j < 10; j += 2) {
                TextView textView = new TextView(this);
                textView.setId(id++);
                textView.setText("");
                textView.setMaxLines(5);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setBackgroundColor(Color.parseColor("#F0FFFF"));
                textView.setGravity(Gravity.CENTER);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(j, 2, 1);
                params.setMargins(5, 10, 5, 10);
                params.width = GridLayout.LayoutParams.MATCH_PARENT;
                params.height = 0;

                gridLayout.addView(textView, params);
            }
        }
    }

    // 渲染課程到課程表中
    public void applyDraw(DBHelper dbHelper) {
        List<Classes> classes = query(dbHelper);

        for (Classes aClass : classes) {
            try {
                // 檢查 c_time 是否格式正確
                String cTime = aClass.getC_time();
                if (TextUtils.isEmpty(cTime) || !Character.isDigit(cTime.charAt(0))) {
                    Log.w("applyDraw", "Invalid c_time format: " + cTime);
                    continue;
                }

                // 第幾節課
                int i = Integer.parseInt(cTime.charAt(0) + "");
                // 星期幾
                int j = utils.getDay(aClass.getC_day());

                // 計算 TextView 的 ID
                int textViewId = (j - 1) * 5 + ((i - 1) / 2 + 1);
                TextView classTextView = findViewById(textViewId);
                if (classTextView == null) {
                    Log.e("applyDraw", "TextView not found for id: " + textViewId);
                    continue;
                }

                // 映射課程數據
                classTextView.setText(aClass.getC_name());
                classTextView.setOnTouchListener(this);

            } catch (NumberFormatException e) {
                Log.e("applyDraw", "Error parsing c_time: " + aClass.getC_time(), e);
            } catch (Exception e) {
                Log.e("applyDraw", "Unexpected error while processing class: " + aClass.getC_name(), e);
            }
        }
    }

    // 從資料庫查詢課程數據
    @SuppressLint("Range")
    public List<Classes> query(DBHelper dbHelper) {
        List<Classes> classes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (!cursor.getString(cursor.getColumnIndex("c_day")).equals("0")) {
                Classes aClass = new Classes();
                aClass.setC_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex("c_id"))));
                aClass.setC_name(cursor.getString(cursor.getColumnIndex("c_name")));
                aClass.setC_time(cursor.getString(cursor.getColumnIndex("c_time")));
                aClass.setC_day(cursor.getString(cursor.getColumnIndex("c_day")));
                classes.add(aClass);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return classes;
    }

    // 初始化選單
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_menu);
        menuItem.setOnMenuItemClickListener(menuItem1 -> {
            Intent intent = new Intent(MainActivity.this, DialogModal.class);
            startActivity(intent);
            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // 跳轉到 MapActivity 加載 map.xml
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // 處理觸摸事件
    @SuppressLint("Range")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            TextView textView = (TextView) view;

            DBHelper dbHelper = new DBHelper(MainActivity.this, DB_NAME, null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, "c_id=?", new String[]{String.valueOf(textView.getId())}, null, null, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                Intent intent = new Intent();
                intent.putExtra("name", cursor.getString(cursor.getColumnIndex("c_name")));
                intent.putExtra("time", cursor.getString(cursor.getColumnIndex("c_time")));
                intent.putExtra("day", cursor.getString(cursor.getColumnIndex("c_day")));
                intent.putExtra("teacher", cursor.getString(cursor.getColumnIndex("c_teacher")));
                intent.setClass(MainActivity.this, Detail.class);
                startActivity(intent);
            }

            cursor.close();
            db.close();
            return true;
        }
        return false;
    }
}