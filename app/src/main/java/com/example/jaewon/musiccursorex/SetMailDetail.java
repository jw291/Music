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

import org.w3c.dom.Text;

import java.io.IOException;

public class SetMailDetail extends AppCompatActivity {
    final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    TextView tv_name;
    TextView tv_title;
    TextView tv_content;
    TextView sendmailsongsinger;
    TextView sendmailsongtitle;
    ImageView sendmailsongalbum;
    SeekBar sendmailsongseekbar;
    MediaPlayer mediaPlayer;
    ImageButton btn_play;
    Handler handler = new Handler();
    public class SendSeekbarThread extends Thread{
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer != null) {
                    sendmailsongseekbar.post(new Runnable() {
                        @Override
                        public void run() {
                            sendmailsongseekbar.setProgress(mediaPlayer.getCurrentPosition());
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
        setContentView(R.layout.activity_maildetail);

        tv_name = (TextView) findViewById(R.id.mailgetnametext);
        tv_title = (TextView) findViewById(R.id.mailtitletext);
        tv_content = (TextView) findViewById(R.id.mailcontenttext);
        sendmailsongsinger = (TextView) findViewById(R.id.sendmailsongsinger);
        sendmailsongtitle = (TextView) findViewById(R.id.sendmailsongtitle);
        sendmailsongalbum = (ImageView) findViewById(R.id.sendmailsongalbum);
        sendmailsongseekbar = (SeekBar)findViewById(R.id.sendmailsongseekbar) ;
        btn_play = (ImageButton) findViewById(R.id.sendmailsongplay);
        Intent intent = getIntent();
        tv_name.setText(intent.getStringExtra("mailname"));
        tv_title.setText(intent.getStringExtra("mailTitle"));
        String mailTitle = intent.getStringExtra("mailTitle");
        SharedPreferences sharedPreferences = getSharedPreferences("mailtest", MODE_PRIVATE);
        String content = sharedPreferences.getString(mailTitle + "content", "");

        final String datapath = sharedPreferences.getString(mailTitle + "datapath", "");
        String singer = sharedPreferences.getString(mailTitle + "singer", "");
        String title = sharedPreferences.getString(mailTitle + "title", "");
        String albumid = sharedPreferences.getString(mailTitle + "albumid", "");
        tv_content.setText(content);
        sendmailsongtitle.setText(title);
        sendmailsongsinger.setText(singer);
        int albumidint = Integer.parseInt(albumid);
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri, albumidint);
        Picasso.with(SetMailDetail.this).load(albumArtUri).into(sendmailsongalbum);

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable img = btn_play.getDrawable(); // playbutton
                Drawable res = getResources().getDrawable(R.drawable.ic_stop_black_24dp);//pause

                final Bitmap bitmap = ((BitmapDrawable) img).getBitmap();
                final Bitmap bitmap1 = ((BitmapDrawable) res).getBitmap();
                if (bitmap.equals(bitmap1)) {
                    btn_play.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } else {
                    Runnable rr = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(datapath);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaPlayer.start();
                                        sendmailsongseekbar.setProgress(0);//0에서 시작.
                                        sendmailsongseekbar.setMax(mediaPlayer.getDuration());
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
        Thread thread = new SendSeekbarThread();
        thread.start();
        //-----음악 실행
    }
}
