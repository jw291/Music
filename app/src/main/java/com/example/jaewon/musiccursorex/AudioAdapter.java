package com.example.jaewon.musiccursorex;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jaewon on 2018-03-09.
 */

public class AudioAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{
    ArrayList<Long> audioIds;

    //아이템 롱클릭시 실행 함수.
    private ItemLongClick itemLongClick;
    public interface ItemLongClick{
        public void onClick(View view,int position,String title,String singer,Uri albumart);
    }
    public void setItemLongClick (ItemLongClick itemLongClick){
        this.itemLongClick = itemLongClick;
    }

    //생성자는 cursor와 context를 가지고 있음.
    public AudioAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }
    //AudioViewHolder에 오디오 목록이 있는 cursor를 불러와서 값을 세팅한다.
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        AudioItem audioItem = AudioItem.bindCursor(cursor);
        ((AudioViewHolder) viewHolder).setAudioItem(audioItem, cursor.getPosition());


        //롱클릭 함수 생성.
        ((AudioViewHolder) viewHolder).view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AudioItem item = new AudioItem();
                if(itemLongClick !=null){//현재 포지션 위치 설정.
                    System.out.println(((AudioViewHolder) viewHolder).img+"imgeid adapter");
                    System.out.println(((AudioViewHolder) viewHolder).title);//선택한 뷰의 타이틀을 받음. // 대신 문제는 노래를 한번 onclick하고 Longclick해야 노래 제목이 들어옴
                    itemLongClick.onClick(v,((AudioViewHolder) viewHolder).mPosition,((AudioViewHolder) viewHolder).title,((AudioViewHolder) viewHolder).singer,((AudioViewHolder) viewHolder).img);//현재 포지션
                }
                return false;
            }
        });

    }

    //listitem_audio.xml 레이아웃을 불러와서 ViewHolder를 생성해준다.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_audio, parent, false);
        /*Animation animation = AnimationUtils.loadAnimation(parent.getContext(),R.anim.slide_left);
        v.startAnimation(animation);*/
        return new AudioViewHolder(v);
    }


    public static class AudioItem {
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

        //bindCursor함수를 하나 만들어줌으로 curosr에 있는 값을 각각의 멤버 변수에 셋팅되도록 구현한다.
        public static AudioItem bindCursor(Cursor cursor) {
            AudioItem audioItem = new AudioItem();
            audioItem.mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            audioItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            audioItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            audioItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));//음악 시간 Thread에서 사용.
            audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            System.out.println("#####################!@#!@#!@#"+audioItem.mDataPath);
            return audioItem;
        }
    }

    public ArrayList<Long> getAudioIds() {
        int count = getItemCount();
        System.out.println(getItemCount()+"ddddddddddddddddddaaaaaaa");
        audioIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            audioIds.add(getItemId(i));
        }
        return audioIds;
    }

    //한개의 imageView와 3개의 TextView를 가지고 있는 ViewHolder
    //setAudioItem함수는 AudioItem에 있는 값을 각각의 뷰에 적용해주는 역할. set해주고 있음
    //picasso라이브러리를 이용해서 엘범 아트를 불러와 imageview에 보여줌.
    //이 AudioItem,AudioViewHolder를 AudioAdapter에 적용해야함..->OncreateViewHolder,OnBindViewHolder
    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private ImageView mImgAlbumArt;
        private TextView mTxtTitle;
        private TextView mTxtSubTitle;
        private TextView mTxtDuration;
        private AudioItem mItem;
        private int mPosition;
        private String title;
        private String singer;
        private Uri img;
        View view;//현재 뷰
        private AudioViewHolder(View view) {
            super(view);
            this.view = view;//롱클릭 뷰를 보내준다.
            mImgAlbumArt = (ImageView) view.findViewById(R.id.img_albumart);
            mTxtTitle = (TextView) view.findViewById(R.id.txt_title);
            mTxtSubTitle = (TextView) view.findViewById(R.id.txt_sub_title);
            mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
            view.setOnClickListener(new View.OnClickListener() { //뷰를 누를 누르고 여기서 포지션 값을 보내주고.그게 service의 list에 getposition으로 등록됨.
                @Override
                public void onClick(View v) {
                    AudioApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                    AudioApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    title = mItem.getmTitle();//선택한 뷰의 title을 보내줌.
                    singer = mItem.getmArtist();
                    img = ContentUris.withAppendedId(artworkUri, mItem.getmAlbumId());
                    return false;
                }
            });
        }

        public void setAudioItem(AudioItem item, int position) {
            mItem = item;
            mPosition = position;
            mTxtTitle.setText(item.mTitle);
            mTxtSubTitle.setText(item.mArtist + "(" + item.mAlbum + ")");
            mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.empty_albumart).into(mImgAlbumArt);
        }
    }
}

