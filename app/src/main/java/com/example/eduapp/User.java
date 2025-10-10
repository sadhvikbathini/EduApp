package com.example.eduapp; // your app package name
public class User {
    private String name;
    private String email;
    private String password;
    private String goal;

    public User(String name, String email, String password, String goal){
        this.name = name;
        this.email = email;
        this.password = password;
        this.goal = goal;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    // getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
