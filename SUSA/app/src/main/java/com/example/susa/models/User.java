package com.example.susa.models;

public class User {
    private static boolean loginStatus;
    private static String token;


    public static void loginUser(boolean loginStatus, String token) {
        User.loginStatus = loginStatus;
        User.token = token;
    }

    public static void logoutUser(){
        User.loginStatus = false;
        User.token = null;
    }


    public static boolean isLogin() {
        return loginStatus;
    }

    public static String getToken() {
        return token;
    }

    private class UserObj{
        private boolean loginStatus;
        private String token;
        public UserObj() {
        }
    }
}
