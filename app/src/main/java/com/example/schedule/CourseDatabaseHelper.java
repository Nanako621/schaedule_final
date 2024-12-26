package com.example.schedule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import androidx.annotation.Nullable;

/**
 * 資料庫管理類別
 */
public class CourseDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "classes_db.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "classes_db";
    public static final String COLUMN_ID = "c_id";
    public static final String COLUMN_NAME = "c_name";
    public static final String COLUMN_TIME = "c_time";
    public static final String COLUMN_DAY = "c_day";
    public static final String COLUMN_TEACHER = "c_teacher";

    public CourseDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 建立資料表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // c_id 使用 AUTOINCREMENT，避免手動指定導致主鍵衝突
        String createTableSql = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_TIME + " TEXT NOT NULL, "
                + COLUMN_DAY + " TEXT NOT NULL, "
                + COLUMN_TEACHER + " TEXT NOT NULL"
                + ")";
        db.execSQL(createTableSql);

        // 可插入一筆測試資料 (非必需)
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, "Sample Course");
        cv.put(COLUMN_TIME, "1");
        cv.put(COLUMN_DAY, "星期一");
        cv.put(COLUMN_TEACHER, "Teacher Name");
        db.insert(TABLE_NAME, null, cv);
    }

    /**
     * 資料表升級
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除舊表並重建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * 新增課程
     * @return 新插入的課程 ID，-1 表示失敗
     */
    public long insertCourse(String courseName, String courseTime, String courseDay, String teacherName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, courseName);
        cv.put(COLUMN_TIME, courseTime);
        cv.put(COLUMN_DAY, courseDay);
        cv.put(COLUMN_TEACHER, teacherName);

        long newId = db.insert(TABLE_NAME, null, cv);
        db.close();
        return newId;
    }

    /**
     * 編輯課程
     * @return 影響的行數
     */
    public int updateCourse(int courseId, String courseName, String courseTime, String courseDay, String teacherName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, courseName);
        cv.put(COLUMN_TIME, courseTime);
        cv.put(COLUMN_DAY, courseDay);
        cv.put(COLUMN_TEACHER, teacherName);

        int rowsAffected = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(courseId)});
        db.close();
        return rowsAffected;
    }

    /**
     * 刪除課程
     * @return 影響的行數
     */
    public int deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(courseId)});
        db.close();
        return rows;
    }

    /**
     * 取得特定課程
     * @return Course 物件，若不存在則回傳 null
     */
    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(courseId)},
                null,
                null,
                null
        );

        Course course = null;
        if (cursor.moveToFirst()) {
            course = new Course();
            course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            course.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            course.setCourseTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
            course.setCourseDay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)));
            course.setTeacherName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER)));
        }

        cursor.close();
        db.close();
        return course;
    }
}
