package com.example.app.model;

/**
 * Simple student info model for the form page.
 */
public class Student {
    private String name;
    private String studentId;
    private int age;
    private String email;
    private String phone;

    public Student() {}

    public Student(String name, String studentId, int age, String email, String phone) {
        this.name = name;
        this.studentId = studentId;
        this.age = age;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

