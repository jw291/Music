package com.example.jaewon.musiccursorex;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

class SendmailAudioAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{

    private ItemClick itemClick;
    public interface ItemClick{
        public void onClick(View view,int position,String title, String singer,String datapath,long duration,long albumid);
    }
    public void setItemonClick (ItemClick itemClick){
        this.itemClick = itemClick;
    }
    public SendmailAudioAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        SendmailAudioItem audioItem = SendmailAudioItem.bindCursor(cursor);
        ((SendmailAudioViewHolder) viewHolder).setAudioItem(audioItem, cursor.getPosition());

        ((SendmailAudioViewHolder)viewHolder).view2.setOnClickListener(new View.OnClickListener() {
            private SendmailAudioItem mItem;
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v,((SendmailAudioViewHolder) viewHolder).mPosition,((SendmailAudioViewHolder)viewHolder).title,((SendmailAudioViewHolder)viewHolder).singer,
                            ((SendmailAudioViewHolder)viewHolder).datapath,((SendmailAudioViewHolder)viewHolder).duration,((SendmailAudioViewHolder)viewHolder).albumid);//현재 포지션
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_audio, parent, false);
        return new SendmailAudioViewHolder(v);

    }

    public static class SendmailAudioItem {
        public long mId; // 오디오 고유 ID
        public long mAlbumId; // 오디오 앨범아트 ID
        public String mTitle; // 타이틀 정보
        public String mArtist; // 아티스트 정보
        public String mAlbum; // 앨범 정보
        public long mDuration; // 재생시간
        public String mDataPath; // 실제 데이터위치

        public String getmTitle(){
            return this.mTitle;
        }
        public String getmArtist(){return this.mArtist;}
        public long getmAlbumId(){return this.mAlbumId;}
        public String getmDataPath(){return this.mDataPath;}
        public long getmDuration(){return this.mDuration;}
        public static SendmailAudioItem bindCursor(Cursor cursor) {
            SendmailAudioItem audioItem = new SendmailAudioItem();
            audioItem.mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            audioItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            audioItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            audioItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            return audioItem;
        }
    }
    public class SendmailAudioViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private ImageView mImgAlbumArt;
        private TextView mTxtTitle;
        private TextView mTxtSubTitle;
        private TextView mTxtDuration;
        private SendmailAudioItem mItem;
        private int mPosition;
        private String title;
        private String singer;
        private long duration;
        private String datapath;
        private long albumid;
        View view2;
        public SendmailAudioViewHolder(View view) {
            super(view);
            this.view2=view;
            mImgAlbumArt = (ImageView) view.findViewById(R.id.img_albumart);
            mTxtTitle = (TextView) view.findViewById(R.id.txt_title);
            mTxtSubTitle = (TextView) view.findViewById(R.id.txt_sub_title);
            mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    title = mItem.getmTitle();
                    singer =mItem.getmArtist();
                    duration = mItem.getmDuration();
                    datapath = mItem.getmDataPath();
                    albumid = mItem.getmAlbumId();
                    return false;
                }
            });
        }

        public void setAudioItem(SendmailAudioItem item, int position) {
            mItem = item;
            mPosition = position;
            mTxtTitle.setText(item.mTitle);
            mTxtSubTitle.setText(item.mArtist + "(" + item.mAlbum + ")");
            mTxtSubTitle.setSelected(true);
            mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.empty_albumart).into(mImgAlbumArt);
        }
    }

}
//sharedprefernce에 담아서 textview에 set해주기.
public class SendmailActivity extends AppCompatActivity {
    private ImageView mailsongalbum;
    private TextView mailsongtitle;
    private TextView mailsongsinger;
    private SeekBar mailsongseekbar;
    private MediaPlayer mediaPlayer;
    private final static int LOADER_ID = 0x0011;
    private RecyclerView mRecyclerView;
    private SendmailAudioAdapter mAdapter;
    ImageButton btn_play;
    Handler handler = new Handler();
    AlertDialog.Builder builder;
    View row;
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    public void open_dialog(View v){
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        getAudioListFromMediaDatabase();
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.dialog_recyclerview,null);
        mRecyclerView = (RecyclerView)row.findViewById(R.id.dialogrecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SendmailAudioAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        builder.setView(row);
        final AlertDialog dialog = builder.create();
        dialog.show();
        mAdapter.setItemonClick(new SendmailAudioAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position,String title, String singer,String datapath,long duration,long albumid) {
                System.out.println(position+"onclick position");
                mailsongtitle.setText(title);
                mailsongsinger.setText(singer);
                mailsongsinger.setSelected(true);
                Uri albumArtUri = ContentUris.withAppendedId(artworkUri, albumid);
                Picasso.with(mRecyclerView.getContext()).load(albumArtUri).error(R.drawable.empty_albumart).into(mailsongalbum);

                SharedPreferences sharedPreferences = getSharedPreferences("mailsong1",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("datapath",datapath);
                editor.putString("title",title);
                editor.putString("singer",singer);
                String albumidstr = String.valueOf(albumid);
                editor.putString("albumid",albumidstr);
                editor.commit();
                dialog.dismiss();
            }
        });

    }

    private void getAudioListFromMediaDatabase() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                };
                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
            }
            //조회 결과가 Cursor를 통해 저장되어 리턴된다.
            //만들어진 AudioAdapter에 LoaderManagaer를 통해 불러온 오디오 목록이 담긴 Cursor를 적용한다.
            @Override
            public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        });
    }
    public class SeekbarThread extends Thread{

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer != null) {
                    mailsongseekbar.post(new Runnable() {
                        @Override
                        public void run() {
                            mailsongseekbar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    });
                    Log.d("jaewon", "run:" + mediaPlayer.getCurrentPosition());
                }
            }
        }
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmail);
        Button showsong = (Button)findViewById(R.id.songsend);
        btn_play = (ImageButton) findViewById(R.id.mailsongplay);
        mailsongtitle = (TextView)findViewById(R.id.mailsongtitle);
        mailsongsinger = (TextView)findViewById(R.id.mailsongsinger);
        mailsongalbum = (ImageView)findViewById(R.id.mailsongalbum);
        mailsongseekbar = (SeekBar)findViewById(R.id.mailsongseekbar);
        Thread t = new SeekbarThread();
        showsong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_dialog(v);
            }
        });

        final Drawable temp;
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("mailsong1",MODE_PRIVATE);
                final String data = sharedPreferences.getString("datapath","");
                Drawable img = btn_play.getDrawable(); // playbutton
                Drawable res = getResources().getDrawable(R.drawable.ic_stop_black_24dp);//pause

                final Bitmap bitmap = ((BitmapDrawable)img).getBitmap();
                final Bitmap bitmap1 = ((BitmapDrawable)res).getBitmap();
                if (bitmap.equals(bitmap1)) {
                    btn_play.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                    Toast.makeText(SendmailActivity.this, "pause", Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }else {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(SendmailActivity.this, "start", Toast.LENGTH_SHORT).show();
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(data);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaPlayer.start();
                                        mailsongseekbar.setProgress(0);//0에서 시작.
                                        mailsongseekbar.setMax(mediaPlayer.getDuration());
                                        System.out.println(mediaPlayer.getDuration() + "듀레이션");
                                    }
                                });
                                btn_play.setImageResource(R.drawable.ic_stop_black_24dp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    handler.postDelayed(r, 100);
                }
            }
        });
        t.start();
        //-------------------------------------------------------------------
        final TextView tv_mailgetname = (TextView) findViewById(R.id.mailgetname);
        final EditText et_mailTitle = (EditText)findViewById(R.id.mailtitle);
        final TextView namesendname = (TextView)findViewById(R.id.mailsendname);
        final EditText et_mailcontent = (EditText)findViewById(R.id.mailcontent);
        Button btn_send = (Button)findViewById(R.id.send);

        //보내는 사람은 현재 로그인 한 아이디 이기 때문에 shared
        final SharedPreferences preferences = getSharedPreferences("final test1",MODE_PRIVATE);
        final String username = preferences.getString("final","");
        namesendname.setText(username);
        //받는 사람은 community리스트로 부터 받은 아이템의 name이기 때문에 intent
        Intent intent = getIntent();
        String getname = intent.getStringExtra("getfriendname");
        tv_mailgetname.setText(getname);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailgetname = tv_mailgetname.getText().toString();
                String mailTitle = et_mailTitle.getText().toString();
                String mailcontent = et_mailcontent.getText().toString();
                if(mailgetname.equals("")&&mailTitle.equals("")&&mailcontent.equals("")){

                    Toast.makeText(getApplicationContext(),"입력하지 않은 항목이 있습니다.",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences sharedPreferences = getSharedPreferences("mailtest", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("beforename",username);
                    editor.putString("name", mailgetname);//SendmailBox리스트뷰에 추가하는 용도 바로 삭제시킴
                    editor.putString("title", mailTitle);//SendmailBox리스트뷰에 추가하는 용도 바로 삭제시킴
                    editor.putString("GetmailBoxname", mailgetname);//GetmailBox리스트뷰에서 현재로그인, 받은사람 구별해주기 위함.
                    editor.putString("GetmailBoxtitle", mailTitle);//getmailBox리스트뷰에 추가하는 용도 바로 삭제시킴.
                    editor.putString("GetmailBoxcontent",mailcontent);//getmailBox리스트뷰에서 add할때 null로 바꾸는 용도 위에 두개는 아님.
                    editor.putString(mailgetname,mailgetname);//GetmailBox에서 로드용.
                    editor.putString(mailTitle + "name", mailgetname);
                    editor.putString(mailTitle + "content", mailcontent);
                    editor.putString(mailTitle + "title", mailTitle);

                    SharedPreferences preferences = getSharedPreferences("mailsong1",MODE_PRIVATE);
                    String datapath = preferences.getString("datapath",null);
                    String singer = preferences.getString("singer",null);
                    String title = preferences.getString("title",null);
                    String albumid = preferences.getString("albumid",null);
                    //노래 리스트를 받아와서 보낸,받은 메일함에서 사용한다.
                    editor.putString(mailTitle + "datapath",datapath);
                    editor.putString(mailTitle + "singer",singer);
                    editor.putString(mailTitle + "title",title);
                    editor.putString(mailTitle + "albumid",albumid);
                    /*editor.putString("datapath",datapath);
                    editor.putString("title",title);
                    editor.putString("singer",singer);
                    String albumidstr = String.valueOf(albumid);
                    editor.putString("albumid",albumidstr);*/
                    editor.commit();
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(SendmailActivity.this)
                            .setSmallIcon(R.drawable.bubble_b)
                            .setContentTitle("쪽지 도착")
                            .setContentText(username+"에게 메세지가 도착했습니다.")
                            .setAutoCancel(true)
                            ;
                    NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0,mBuilder.build());
                    Intent intent = new Intent(SendmailActivity.this,SendmailBox.class);
                    startActivity(intent);
                }
            }
        });


        //________________________________________dialog 리스트뷰 음악 띄우기 ....


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
