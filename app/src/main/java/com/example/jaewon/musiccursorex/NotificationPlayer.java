package com.example.jaewon.musiccursorex;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
/*
현재 음악이 background에서 동작하고 있는데 장시간 또는 메모리가 부족하다던지 할 경우에는 백그라운드에서 동작하는 앱은
무작위로 processkill이 될 수 있다.(일정시간 후에 다시 살아남). 그래서 background로 동작하는 서비스를 foreground로 동작하게끔 작업을 했다.
서비스가 foreground이기 때문에 notification을 이용해서 해당 음악을 버튼이벤트로 조작 할 수 있게 된다.
background는 배경, foreground는 그와 반대인 전경이다. 즉 조작가능한 실제 실행중인 프로그램을 뜻한다. 이 자체가 여기서 쓰이는 notification이다.
 */
public class NotificationPlayer {
    private final static int NOTIFICATION_PLAYER_ID = 0x3;
    private AudioService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;//foreground상태로 변경해주고있다. mService.startForeground함수 이용.

    public NotificationPlayer(AudioService service) {
        mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //해당 함수는 음악이 변경되거나 재생상태가 변경될 경우 호출될 예정이며 알림바에 등록 된 Notification을 업데이트(다시등록)하는 역할을 하게 된다.
    @SuppressLint("StaticFieldLeak")
    public void updateNotificationPlayer() {
         cancel();
         mNotificationManagerBuilder = new NotificationManagerBuilder();
          mNotificationManagerBuilder.execute();

    }


    //사용자가 알림바에서 NotificationPlayer를 종료하고자 할 때 호출된다.
    //그리고 서비스를 foreground에서 내려놓는다.
    public void removeNotificationPlayer() {
        cancel();
        mService.stopForeground(true);
        isForeground = false;
    }

    //NotificationManagerBuilder를 취소한다.
    private void cancel() {
        if (mNotificationManagerBuilder != null) {
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    //AsyncTask를 상속받은 클래스로서 위에서 만들어준 레이아웃 notification_player.xml을
    //RemoteView로 만들고 각각의 버튼들에 대한 이벤트를 설정한 뒤 알림바에서 등록하는 역할과 최초 등록시 서비스를 Foreground로 변경한다.
    //주요 내용은 createRemoteview와 updateRemoteView함수이다.
    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;
        //백그라운드 작업이 시작되기 전 호출되는 메소드
        //여기서는 notification.xml파일을 remoteview로 바꿔주고 그 파일의 content를 set해주고 있다.
        //그리고 이것을 foreground로 바꿔서 조작 가능한 상태로 바꿔준다.
        //즉 준비작업.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainActivity = new Intent(mService, PlayListActivity.class);
            mMainPendingIntent = PendingIntent.getActivity(mService, 0, mainActivity, 0);
            mRemoteViews = createRemoteView(R.layout.notification_player);
            //xml파일을 RemoteView로 만듬.
            //모두 전역변수로 넣어주는 역할 넣어주고 실행은 doin에서 함. 즉 준비과정만 하고 있음.

            mNotificationBuilder = new NotificationCompat.Builder(mService);
            mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)//PlayListActivity로 이동한다.
                    .setContent(mRemoteViews);
            //Notification build하는데 내용은 xml파일의 뷰들과, AudioService이다.
            //Notification을 빌드한다.
            Notification notification = mNotificationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendingIntent;
            if (!isForeground) {
                isForeground = true;
                // 서비스를 Foreground 상태로 만든다
                mService.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... params) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            //title,singer,picasso를 update할 수 있음.
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //생성자로 받은 AudioService와 notification_player.xml을 이용하여 RemoteViews를 생성하고 있으며, 생성한 RemoteViews에 포함된 버튼들에 대한'
        //클릭 이벤트를 등록한다.
        //일반 view들과는 다르게 RemoteViews에 클릭이벤트를 연결할 경우 PendingIntent를 사용하게된다.(버튼을 클릭하면 호출이 된다.)
        //각각 버튼들은 Intent Action을 갖고 있는 PendingIntent를 가지고 있으며 Intent Action은 CommandActions클래스에서 정의했다.
        private RemoteViews createRemoteView(int layoutId) {
            RemoteViews remoteView = new RemoteViews(mService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSE);
            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);
            //클릭 리스너를 등록해준다.
            remoteView.setOnClickPendingIntent(R.id.btn_play_pause, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.btn_forward, forward);
            remoteView.setOnClickPendingIntent(R.id.btn_rewind, rewind);
            remoteView.setOnClickPendingIntent(R.id.btn_close, close);
            return remoteView;
        }
        //이렇게 등록된 RemoteViews의 버튼들을 클릭하게 될 경우 AudioService의 onStartCommand를 통해 이벤트가 들어오게된다.
        private void updateRemoteView(RemoteViews remoteViews, Notification notification) {
            if (mService.isPlaying()) {
                remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.ic_stop);
            } else {
                remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.ic_play);
            }

            String title = mService.getAudioItem().mTitle;
            remoteViews.setTextViewText(R.id.txt_title, title);

            //가수 바꿔주는 역할.
            String singer = mService.getAudioItem().mArtist;
            remoteViews.setTextViewText(R.id.txt_singer,singer);

            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), mService.getAudioItem().mAlbumId);
            Picasso.with(mService).load(albumArtUri).error(R.drawable.empty_albumart).into(remoteViews, R.id.img_albumart, NOTIFICATION_PLAYER_ID, notification);
        }
    }
}
