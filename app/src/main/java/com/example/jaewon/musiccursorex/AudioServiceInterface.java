package com.example.jaewon.musiccursorex;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;
/*
AudioService에서 정의했던 public함수들(setPlayList, play,pause,forward,rewind
해당 함수들을 사용하기 위해서 Service에 Bind를 해야 직접 접근이 가능하다.
그래서 AudioService와 직접 바인딩하고 접근 할 수 있게 도와주는
AudioServiceInterface클래스를 만든다.
 */
/*
객체 생성시에 BindService를 통해서 AudioService와 직접 연결하게 된다. 바인딩된 이후에는
AudioService객체를 mService에 할당하고, 이후에 setPlayList,pause,forward,rewind함수를 호출하는데 사용된다.
 */
/*
앰이 실행되어 process가 생성될 때 호출되는 application에서 단 한번 객체를 생선해준다.
그러기 위해서는 application을 상속받는 audioApplicationㅇ르 만들고 menifest에 등록한다.
 */
public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private AudioService mService;

    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class).setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (mService != null) {
            mService.setPlayList(audioIds);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public AudioAdapter.AudioItem getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }

    public void pause() {
        if (mService != null) {
            mService.pause();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }
}
