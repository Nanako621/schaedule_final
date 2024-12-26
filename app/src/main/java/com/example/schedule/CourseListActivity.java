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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 課表主畫面，顯示所有課程
 */
public class CourseListActivity extends AppCompatActivity implements View.OnTouchListener {

    private CourseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new CourseDatabaseHelper(this);

        // 建立課表框架
        initializeTimetableGrid();
        // 顯示資料庫課程
        displayCourses();
    }

    /**
     * 建立 7 天 × 5 區塊(每區塊2節)，預設 TextView
     */
    private void initializeTimetableGrid() {
        int id = 1;
        for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {
            GridLayout gridLayout = getDayColumnLayout(dayIndex);
            // 一天假設 10 節(2,4,6,8,10...) => j: 1,3,5,7,9
            for (int j = 1; j < 10; j += 2) {
                TextView textView = new TextView(this);
                textView.setId(id++);
                textView.setText("");
                textView.setMaxLines(5);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setBackgroundColor(Color.parseColor("#F0FFFF"));
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(5, 5, 5, 5);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                // 每一格佔2行 (上下兩節)
                params.rowSpec = GridLayout.spec(j, 2, 1);
                params.setMargins(5, 10, 5, 10);
                params.width = GridLayout.LayoutParams.MATCH_PARENT;
                params.height = 0;
                gridLayout.addView(textView, params);
            }
        }
    }

    /**
     * 回傳對應的 GridLayout (週一~週日)
     */
    private GridLayout getDayColumnLayout(int dayIndex) {
        switch (dayIndex) {
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

    /**
     * 讀取資料並放到對應 TextView
     */
    private void displayCourses() {
        List<Course> courseList = queryAllCourses();
        for (Course course : courseList) {
            try {
                String timeStr = course.getCourseTime();
                if (TextUtils.isEmpty(timeStr)) continue;
                // 取第一個字元 => "1","2","3"...
                int timeIndex = Integer.parseInt(timeStr.substring(0, 1));
                int dayIndex = DayUtils.getDayIndex(course.getCourseDay());
                if (dayIndex < 1 || dayIndex > 7) continue;

                // 計算 TextView ID
                int textViewId = (dayIndex - 1) * 5 + ((timeIndex - 1) / 2 + 1);
                TextView classTextView = findViewById(textViewId);
                if (classTextView == null) {
                    Log.e("CourseListActivity", "TextView not found for id " + textViewId);
                    continue;
                }

                // 顯示課名
                String existingText = classTextView.getText().toString();
                if (!existingText.isEmpty()) {
                    // 如果已經有課程，將其內容換行顯示
                    classTextView.setText(existingText + "\n" + course.getCourseName());
                } else {
                    classTextView.setText(course.getCourseName());
                }

                // 在 textView 的 tag 記錄 "courseId"
                classTextView.setTag(course.getCourseId());
                // 綁定觸控監聽
                classTextView.setOnTouchListener(this);
            } catch (NumberFormatException e) {
                Log.e("displayCourses", "Parse time error: " + course.getCourseTime(), e);
            }
        }
    }

    /**
     * 從 DB 查全部課程
     */
    @SuppressLint("Range")
    private List<Course> queryAllCourses() {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(CourseDatabaseHelper.TABLE_NAME,
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Course c = new Course();
                c.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_ID)));
                c.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_NAME)));
                c.setCourseTime(cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_TIME)));
                c.setCourseDay(cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_DAY)));
                c.setTeacherName(cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_TEACHER)));
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 觸控 TextView => 顯示課程詳細
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 從 tag 取出對應的 courseId
            if (v.getTag() != null) {
                int courseId = (int) v.getTag();
                // 跳到 CourseDetailActivity
                Intent detailIntent = new Intent(this, CourseDetailActivity.class);
                detailIntent.putExtra("courseId", courseId);
                startActivity(detailIntent);
            }
            return true;
        }
        return false;
    }

    /**
     * 載入右上角選單 (menu.xml)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // 監聽「+ 新增課程」按鈕
        MenuItem addItem = menu.findItem(R.id.action_menu);
        addItem.setOnMenuItemClickListener(item -> {
            // 不帶 courseId => 表示新增模式
            Intent intent = new Intent(this, CourseEditActivity.class);
            startActivity(intent);
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 其他選單項 (如地圖)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // TODO: 如果要做地圖，可以跳轉到 CampusMapActivity
            // Intent mapIntent = new Intent(this, CampusMapActivity.class);
            // startActivity(mapIntent);
            Toast.makeText(this, "設定功能尚未實作", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 當此 Activity 再次啟動時，重新載入課程詳細資訊
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 清空現有的課程顯示
        clearCourseDisplays();
        // 重新載入課程
        displayCourses();
    }

    /**
     * 清空所有課程 TextView 的內容
     */
    private void clearCourseDisplays() {
        for (int id = 1; id <= 35; id++) { // 假設有 7 天 × 5 個格子
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setText("");
                tv.setTag(null);
                tv.setOnTouchListener(null);
            }
        }
    }
}
