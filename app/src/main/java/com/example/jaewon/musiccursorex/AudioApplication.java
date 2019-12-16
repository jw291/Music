package com.example.jaewon.musiccursorex;

import android.app.Application;

/**
 * Created by jaewon on 2018-03-16.
 */

public class AudioApplication extends Application {
    private static AudioApplication mInstance;
    private AudioServiceInterface mInterface;

    //mInstance 변수를 static으로 선언하여 어느 위치에서도 접근 할 수 있도록 getInstance함수를 만들었다.
    //oncreate함수에서 AudioServiceInterface객체를 생성 및 BindService할 수 있도록 구현했다.
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInterface = new AudioServiceInterface(getApplicationContext());
    }

    public static AudioApplication getInstance() {
        return mInstance;
    }

    public AudioServiceInterface getServiceInterface() {
        return mInterface;
    }
}
