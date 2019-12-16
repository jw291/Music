package com.example.jaewon.musiccursorex;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class GetMailDetail extends AppCompatActivity {

    final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    TextView tv_name;
    TextView tv_title;
    TextView tv_content;
    TextView getmailsongsinger;
    TextView getmailsongtitle;
    ImageView getmailsongalbum;
    SeekBar getmailsongseekbar;
    MediaPlayer mediaPlayer;
    ImageButton btn_play;
    Handler handler = new Handler();
    public class GetSeekbarThread extends Thread{
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer != null) {
                    getmailsongseekbar.post(new Runnable() {
                        @Override
                        public void run() {
                            getmailsongseekbar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    });
                    Log.d("jaewon", "run:" + mediaPlayer.getCurrentPosition());
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getmaildetail);
        tv_name = (TextView)findViewById(R.id.mailsendnametext);
        tv_title = (TextView)findViewById(R.id.mailtitletext2);
        tv_content = (TextView)findViewById(R.id.mailcontenttext2);
        getmailsongsinger = (TextView) findViewById(R.id.getmailsongsinger);
        getmailsongtitle = (TextView) findViewById(R.id.getmailsongtitle);
        getmailsongalbum = (ImageView) findViewById(R.id.getmailsongalbum);
        getmailsongseekbar = (SeekBar)findViewById(R.id.getmailsongseekbar) ;
        btn_play = (ImageButton)findViewById(R.id.getmailsongplay);
        Intent intent = getIntent();
        tv_name.setText(intent.getStringExtra("name"));
        tv_title.setText(intent.getStringExtra("title"));
        String mailTitle = intent.getStringExtra("title");
        SharedPreferences sharedPreferences = getSharedPreferences("mailtest",MODE_PRIVATE);
        String content = sharedPreferences.getString(mailTitle + "content","");

        final String datapath = sharedPreferences.getString(mailTitle + "datapath", "");
        String singer = sharedPreferences.getString(mailTitle + "singer", "");
        String title = sharedPreferences.getString(mailTitle + "title", "");
        String albumid = sharedPreferences.getString(mailTitle + "albumid", "");

        tv_content.setText(content);
        getmailsongtitle.setText(title);
        getmailsongsinger.setText(singer);
        int albumidint = Integer.parseInt(albumid);
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri, albumidint);
        Picasso.with(GetMailDetail.this).load(albumArtUri).into(getmailsongalbum);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable img = btn_play.getDrawable(); // playbutton
                Drawable res = getResources().getDrawable(R.drawable.ic_stop_black_24dp);//pause

                final Bitmap bitmap = ((BitmapDrawable) img).getBitmap();
                final Bitmap bitmap1 = ((BitmapDrawable) res).getBitmap();
                if (bitmap.equals(bitmap1)) {
                    btn_play.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                    Toast.makeText(GetMailDetail.this, "pause", Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } else {
                    Runnable rr = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(GetMailDetail.this, "start", Toast.LENGTH_SHORT).show();
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(datapath);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaPlayer.start();
                                        getmailsongseekbar.setProgress(0);//0에서 시작.
                                        getmailsongseekbar.setMax(mediaPlayer.getDuration());
                                        System.out.println(mediaPlayer.getDuration() + "듀레이션");
                                    }
                                });
                                btn_play.setImageResource(R.drawable.ic_stop_black_24dp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    handler.postDelayed(rr, 100);
                }
            }
        });
        Thread thread = new GetSeekbarThread();
        thread.start();
    }
}
