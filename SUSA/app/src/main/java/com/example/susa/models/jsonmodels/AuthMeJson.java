package com.example.susa.models.jsonmodels;

public class AuthMeJson{
    boolean success;
    Data data;
    public AuthMeJson(boolean success, String role, String _id, String name, String email, String sportrole){
        this.success = success;
        data = new Data(role, _id, name, email, sportrole);
    }

    private class Data{
        String role;
        String _id;
        String name;
        String email;
        String sportrole;

        Data(String role, String _id, String name, String email, String sportrole) {
            this.role = role;
            this._id = _id;
            this.name = name;
            this.email = email;
            this.sportrole = sportrole;
        }
    }
}
