package com.example.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // 建構函數
    public DBHelper(@Nullable Context context,
                    @Nullable String name,
                    @Nullable SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 創建課程表
        sqLiteDatabase.execSQL("create table classes_db" +
                "(c_id Integer not null primary key autoincrement," +
                " c_name varchar(50) not null," +
                " c_time varchar(50) not null," +
                " c_day varchar(50) not null," +
                " c_teacher varchar(50) not null)");

        // 初始化一些假數據
        ContentValues contentValues = new ContentValues();
        contentValues.put("c_name", "Sample Course");
        contentValues.put("c_time", "1");
        contentValues.put("c_day", "Monday");
        contentValues.put("c_teacher", "Teacher Name");

        // 插入 36 條初始數據
        for (int i = 1; i < 37; i++) {
            contentValues.put("c_id", i);
            sqLiteDatabase.insert("classes_db", null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 資料庫升級時的操作（如果有結構變更）
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS classes_db");
        onCreate(sqLiteDatabase);
    }

    // 刪除課程方法
    public void deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 根據課程 ID 刪除課程
        db.delete("classes_db", "c_id = ?", new String[]{String.valueOf(courseId)});
        db.close();
    }
}