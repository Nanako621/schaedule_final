package com.example.schedule;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DialogModal extends Activity {
    Button close_activity;
    Button save_activity;
    Spinner selected_time;
    Spinner selected_day;
    EditText subject;
    EditText teacher;

    // 数据库名，表名
    public final String DB_NAME = "classes_db.db";
    public final String TABLE_NAME = "classes_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set);

        // 当点击 dialog 之外完成此 activity
        setFinishOnTouchOutside(true);

        // 关闭按钮操作
        close_activity = findViewById(R.id.close_activity);
        close_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogModal.this.finish();
            }
        });

        // 保存按钮操作
        save_activity = findViewById(R.id.save_activity);
        save_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取选择的数据
                selected_day = findViewById(R.id.selected_day);
                String day = selected_day.getSelectedItem().toString();

                // 判断是否选择了日期
                if (day.equals("--请选择--")) {
                    Toast.makeText(DialogModal.this, "请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }

                selected_time = findViewById(R.id.selected_time);
                String time = selected_time.getSelectedItem().toString();

                // 判断是否选择了时间
                if (time.equals("--请选择--")) {
                    Toast.makeText(DialogModal.this, "请选择时间", Toast.LENGTH_SHORT).show();
                    return;
                }

                subject = findViewById(R.id.subject);
                String subjectText = subject.getText().toString();

                // 判断课程名称是否为空
                if ("".equals(subjectText)) {
                    Toast.makeText(DialogModal.this, "课程名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                teacher = findViewById(R.id.teacher);
                String teacherText = teacher.getText().toString();

                // 判断教师名称是否为空
                if ("".equals(teacherText)) {
                    Toast.makeText(DialogModal.this, "教师信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 创建数据库对象
                DBHelper dbHelper = new DBHelper(DialogModal.this, DB_NAME, null, 1);

                // 将数据存入 ContentValues 中
                ContentValues contentValues = new ContentValues();
                contentValues.put("c_id", combineId(day, time));
                contentValues.put("c_name", subjectText);
                contentValues.put("c_time", time);
                contentValues.put("c_day", day);
                contentValues.put("c_teacher", teacherText);

                // 更新数据库记录
                update(dbHelper, contentValues);

                // 清空栈内所有 activity
                Intent intent = new Intent(DialogModal.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    // 通过星期几和第几节课计算课程 ID
    public String combineId(String day, String time) {
        // 星期几转换成 int 类型
        int day1 = utils.getDay(day);

        // 处理时间的格式 "1-2节" 或 "1节" 等
        int time1;
        try {
            time1 = Integer.parseInt(time.substring(0, 1));  // 获取时间段的第一个数字
        } catch (NumberFormatException e) {
            time1 = 0; // 如果解析失败，设置默认值
        }

        return String.valueOf((day1 - 1) * 5 + ((time1 - 1) / 2 + 1));
    }


    // 更新数据库记录
    public void update(DBHelper dbHelper, ContentValues contentValues) {
        String[] args = {contentValues.get("c_id").toString()};

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // 更新数据
            db.update(TABLE_NAME, contentValues, "c_id=?", args);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DialogModal.this, "数据更新失败", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();  // 释放数据库连接
        }
    }
}