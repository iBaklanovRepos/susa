package com.example.susa.database.requests.tasks;


import java.util.concurrent.Callable;

public class AddSportsmanRequestTask implements Callable<String> {
    SportsmanEntity entity;

    public AddSportsmanRequestTask(){

    }

    @Override
    public String call() throws Exception {
        return null;
    }


    private class SportsmanEntity{
        String name;
        String lastname;
        String gender;
        String birthday;
        String phone;
        String kindofsport;
    }
}
