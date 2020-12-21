package com.example.stakers.utility;

public class User {
    private String email, dob, gender, user_image_path;

    public User() {

    }

    public User(String email, String dob, String gender, String user_image_path) {
        this.email = email;
        this.dob = dob;
        this.gender  = gender;
        this.user_image_path = user_image_path;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUser_image_path() {
        return user_image_path;
    }

    public void setUser_image_path(String user_image_path) {
        this.user_image_path = user_image_path;
    }
}
