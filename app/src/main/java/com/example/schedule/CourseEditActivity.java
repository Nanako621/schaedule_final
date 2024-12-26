package com.example.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * 新增或編輯課程的 Activity
 */
public class CourseEditActivity extends Activity {

    private Spinner daySpinner, timeSpinner;
    private EditText courseNameEditText, teacherEditText;
    private Button closeButton, saveButton;

    private CourseDatabaseHelper dbHelper;
    private int currentCourseId = -1;  // 預設 -1 (表示新增模式)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果想去除標題列，可使用以下方法
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_course_edit);

        dbHelper = new CourseDatabaseHelper(this);

        // 綁定 View
        daySpinner = findViewById(R.id.selected_day);
        timeSpinner = findViewById(R.id.selected_time);
        courseNameEditText = findViewById(R.id.subject);
        teacherEditText = findViewById(R.id.teacher);
        closeButton = findViewById(R.id.close_activity);
        saveButton = findViewById(R.id.save_activity);

        // 檢查是否帶了 courseId => 編輯模式
        Intent intent = getIntent();
        if (intent != null) {
            currentCourseId = intent.getIntExtra("courseId", -1);
            if (currentCourseId != -1) {
                // 從資料庫撈舊的紀錄、顯示到 UI
                loadExistingCourse(currentCourseId);
            }
        }

        // 關閉按鈕
        closeButton.setOnClickListener(v -> finish());

        // 點擊「儲存」按鈕
        saveButton.setOnClickListener(v -> onSaveButtonClicked());
    }

    /**
     * 取得舊資料並顯示
     */
    private void loadExistingCourse(int courseId) {
        Course course = dbHelper.getCourseById(courseId);
        if (course != null) {
            courseNameEditText.setText(course.getCourseName());
            teacherEditText.setText(course.getTeacherName());

            // 設定 Spinner 選項
            setSpinnerSelection(daySpinner, course.getCourseDay(), R.array.day);
            setSpinnerSelection(timeSpinner, course.getCourseTime(), R.array.time);
        } else {
            Toast.makeText(this, "課程不存在", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 根據字串設定 Spinner 的選項
     */
    private void setSpinnerSelection(Spinner spinner, String value, int arrayId) {
        String[] items = getResources().getStringArray(arrayId);
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * 按下「儲存」時，如果是新增就 insert，否則 update
     */
    private void onSaveButtonClicked() {
        String selectedDay = daySpinner.getSelectedItem().toString();
        String selectedTime = timeSpinner.getSelectedItem().toString();
        String courseName = courseNameEditText.getText().toString().trim();
        String teacherName = teacherEditText.getText().toString().trim();

        // 簡單檢查必填
        if ("--请选择--".equals(selectedDay) || "--请选择--".equals(selectedTime) ||
                courseName.isEmpty() || teacherName.isEmpty()) {
            Toast.makeText(this, "所有欄位均為必填", Toast.LENGTH_SHORT).show();
            return;
        }

        // 新增模式
        if (currentCourseId == -1) {
            long newId = dbHelper.insertCourse(courseName, selectedTime, selectedDay, teacherName);
            if (newId != -1) {
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "新增失敗", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 編輯模式 => update
            int rows = dbHelper.updateCourse(currentCourseId, courseName, selectedTime, selectedDay, teacherName);
            if (rows > 0) {
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "更新失敗", Toast.LENGTH_SHORT).show();
            }
        }

        // 回到上一頁 (CourseListActivity)
        finish();
    }
}
