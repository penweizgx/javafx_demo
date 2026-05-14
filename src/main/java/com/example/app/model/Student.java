package com.example.app.model;

import lombok.Data;

@Data
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
}
