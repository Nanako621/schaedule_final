package com.example.schedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 課程詳細資訊頁面
 */
public class CourseDetailActivity extends AppCompatActivity {

    private int courseId = -1;
    private TextView subTextView, timeTextView, dayTextView, teacherTextView;
    private Button backButton, deleteButton, editButton;
    private CourseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        dbHelper = new CourseDatabaseHelper(this);

        // 1) 拿到從 CourseListActivity 帶過來的 courseId
        Intent intent = getIntent();
        courseId = intent.getIntExtra("courseId", -1);

        // 綁定 View
        subTextView = findViewById(R.id.sub);
        timeTextView = findViewById(R.id.time);
        dayTextView = findViewById(R.id.clsNum);
        teacherTextView = findViewById(R.id.teacher);

        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        // 2) 從 DB 讀取對應的課程
        loadCourseDetail(courseId);

        // 返回按鈕
        backButton.setOnClickListener(v -> finish());

        // 刪除課程
        deleteButton.setOnClickListener(v -> {
            if (courseId != -1) {
                int rowsDeleted = dbHelper.deleteCourse(courseId);
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "刪除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "刪除失敗", Toast.LENGTH_SHORT).show();
                }
            }
            finish(); // 回到上一頁
        });

        // 修改 => 跳到 CourseEditActivity
        editButton.setOnClickListener(v -> {
            if (courseId != -1) {
                // 帶著課程 ID 進去 => 編輯模式
                Intent modifyIntent = new Intent(this, CourseEditActivity.class);
                modifyIntent.putExtra("courseId", courseId);
                startActivity(modifyIntent);
                finish(); // 編輯完成後回到主畫面，主畫面會在 onResume() 重新載入
            }
        });
    }

    /**
     * 藉由 courseId 查資料庫，然後顯示到畫面上
     */
    private void loadCourseDetail(int courseId) {
        if (courseId == -1) return;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                CourseDatabaseHelper.TABLE_NAME,
                null,
                CourseDatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(courseId)},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            // 顯示
            String courseName = cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_NAME));
            String courseTime = cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_TIME));
            String courseDay = cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_DAY));
            String teacherName = cursor.getString(cursor.getColumnIndexOrThrow(CourseDatabaseHelper.COLUMN_TEACHER));

            subTextView.setText(courseName);
            timeTextView.setText(courseTime);
            dayTextView.setText(courseDay);
            teacherTextView.setText(teacherName);
        } else {
            Toast.makeText(this, "課程不存在", Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
        db.close();
    }

    /**
     * 當此 Activity 再次啟動時，重新載入課程詳細資訊
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (courseId != -1) {
            loadCourseDetail(courseId);
        }
    }
}
