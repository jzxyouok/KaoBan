package com.dezhou.lsy.projectdezhoureal;

import android.app.Application;

import com.easemob.chat.EMChat;

public class TestKaoban extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EMChat.getInstance().init(this);
        EMChat.getInstance().setDebugMode(true);
    }
}
