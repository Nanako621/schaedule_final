package com.example.schedule;

/**
 * 課程資料物件
 */
public class Course {

    private int courseId;       // Primary Key
    private String courseName;
    private String courseTime;
    private String courseDay;
    private String teacherName;

    // Constructors
    public Course() {}

    public Course(int courseId, String courseName, String courseTime, String courseDay, String teacherName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseTime = courseTime;
        this.courseDay = courseDay;
        this.teacherName = teacherName;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseTime() { return courseTime; }
    public void setCourseTime(String courseTime) { this.courseTime = courseTime; }

    public String getCourseDay() { return courseDay; }
    public void setCourseDay(String courseDay) { this.courseDay = courseDay; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    // For debugging purposes
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", courseTime='" + courseTime + '\'' +
                ", courseDay='" + courseDay + '\'' +
                ", teacherName='" + teacherName + '\'' +
                '}';
    }
}
