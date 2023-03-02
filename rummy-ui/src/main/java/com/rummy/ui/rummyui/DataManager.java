package com.rummy.ui.rummyui;

public class DataManager {
    static private String userName;

    static void setUserName(String userName) {
         DataManager.userName = userName;
    }

    static String getUserName() {
        return DataManager.userName;
    }
}
