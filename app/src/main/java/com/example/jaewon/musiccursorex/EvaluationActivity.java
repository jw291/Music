package com.example.jaewon.musiccursorex;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Commented {
    private String icon;
    private String name;
    private String contents;
    private String date;

    public Commented(String icon, String name,  String contents, String date){
        this.icon = icon;
        this.name = name;
        this.contents = contents;
        this.date = date;
    }//add를 위한 메소드

    public String getDate(){return date;}

    public void setDate(String date){this.date = date;}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

}
class ViewHolder{
TextView tv_name,tv_comment,tv_date;
ImageView iv_img;
        }
class CommentAdapter extends BaseAdapter {
    /* 아이템을 세트로 담기 위한 어레이 */
    private Context context;
    private int layout;
    private ArrayList<Commented> commentlist = new ArrayList<>();

    public CommentAdapter(Context context, int layout, ArrayList<Commented> commentlist){
        this.context = context;
        this.layout = layout;
        this.commentlist = commentlist;
    }
    @Override
    public int getCount() {return commentlist.size();}
    @Override
    public Commented getItem(int position) {
        return commentlist.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        final  ViewHolder viewHolder;
        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            viewHolder.iv_img= (ImageView) convertView.findViewById(R.id.commentimg);
            viewHolder.tv_name =(TextView) convertView.findViewById(R.id.commentname);
            viewHolder.tv_comment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.date);
            
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //****************************************

        //****************************************
        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        
        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        Commented myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        //glide
        Glide.with(context).load(myItem.getIcon()).error(R.drawable.profileimage).into(viewHolder.iv_img);
        viewHolder.tv_name.setText(myItem.getName());
        viewHolder.tv_comment.setText(myItem.getContents());
        viewHolder.tv_date.setText(myItem.getDate());
        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
        return convertView;
    }
    public void removeItem(int position) {commentlist.remove(position);}

    public void update(int position, String text){ //해당 포지션과 바꿀 인덱스를 받는다.
        Commented item = commentlist.get(position); //ArrayList의 지정된 포지션값을 불러온다.
        EvaluationActivity date = new EvaluationActivity();
        item.setContents(text); // 그 포지션의 Contents에게 바꿀 인덱스를 set해준다.
        commentlist.remove(position);
        commentlist.add(position,item); // 그 포지션에게 item을 통째로 전달한다.
        notifyDataSetChanged(); // 재 갱신한다.

        //해당 포지션 값을 get한 걸 item이 가지고 있고.
        //그 item에 String(textview)에 set을 해준다.
        //position의 item을 몽땅지우고
        //set해준 item과 그 포지션에 add를 해준다.

    }

}
public class EvaluationActivity extends AppCompatActivity {
    EditText et;
    EditText det;
    Dialog modi_dialog;
    private ArrayList<Commented> arrayList;
    private CommentAdapter adapter;
    private ListView commentList;
    private Button input_btn;
    private TextView date;
    Commented item;

    public String getDate(){
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        Date date = new Date();
        String strDate = dateFormat.format(date);
        return strDate;
    }

    private  void saveData(){
        Intent intent = getIntent();
        String songname = intent.getStringExtra("songname");//노래 이름 들어가있음.

        SharedPreferences sharedPreferences = getSharedPreferences("나"+songname , MODE_PRIVATE); // songname이 바뀔때마다 새로운 음악이 저장될 것임. 그리고 어차피 모든 계장이 봐야하니까 songname만 다르게.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Commented>>() {}.getType();//read
        String json = gson.toJson(arrayList,type); // save
        editor.putString("task list3",json);
        editor.apply();

    }
    private void loadData(){
        Intent intent = getIntent();
        String songname = intent.getStringExtra("songname");//노래 이름 들어가있음.
        SharedPreferences sharedPreferences = getSharedPreferences("나"+ songname,MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list3","");
        Type type = new TypeToken<ArrayList<Commented>>() {}.getType();//read
        arrayList = gson.fromJson(json,type);

        if(arrayList == null){
            arrayList = new ArrayList<>();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        //************************date************************
        //****************************************************
        //************************listview**************************

        loadData();
        commentList = (ListView)findViewById(R.id.commentlistview);
        adapter = new CommentAdapter(this,R.layout.custom_commentlistitem,arrayList);
        commentList.setAdapter(adapter);

        input_btn = (Button) findViewById(R.id.buttonSend);
        et = (EditText)findViewById(R.id.chatText);
        date = (TextView)findViewById(R.id.date);
        input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = et.getText().toString();
                System.out.println(""+input);
                SharedPreferences sharedPreferences = getSharedPreferences("final test1",MODE_PRIVATE);
                String userid = sharedPreferences.getString("final","");//내 아이디가 들어가있음
                String userimage = sharedPreferences.getString("finalimage","");
                arrayList.add(new Commented(userimage,userid,input,getDate())); // 추가 메소드
                et.setText("");
                adapter.notifyDataSetChanged();//화면에 다시 띄우기
            }
        });
        //*******************리스트뷰 아이템 클릭시 스피너가 나오면서 delete와 수정을 선택 할 수 있다.**************************
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("final test1",MODE_PRIVATE);
                final String userid = sharedPreferences.getString("final","");
                item = arrayList.get(position); //ArrayList의 지정된 포지션값을 불러온다.
                if(item.getName().equals(userid)) {
                    final String[] sample = new String[]{"수정", "삭제"};
                    new AlertDialog.Builder(EvaluationActivity.this).setTitle("").setItems(sample, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    System.out.println("수정클릭");
                                    //삭제 - > 추가
                                    modi_dialog = new Dialog(EvaluationActivity.this);
                                    modi_dialog.setContentView(R.layout.dialog_modify);
                                    det = (EditText) modi_dialog.findViewById(R.id.modifyet);
                                    Button btn = (Button) modi_dialog.findViewById(R.id.modifybtn);
                                    item = arrayList.get(position); //ArrayList의 지정된 포지션값을 불러온다.
                                    System.out.println(item.getContents());
                                    String getContents = item.getContents();
                                    det.setText(getContents);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String update = det.getText().toString();
                                            System.out.println("" + update);
                                            adapter.update(position, update); //adapter의 update메소드에게 해당 position과 바꿀text를 보내준다.
                                            //adapter.readdItem(position,R.drawable.profileimageadd,"나재원",update,getDate());
                                            Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                                            modi_dialog.dismiss();
                                        }
                                    });
                                        modi_dialog.show();
                                    break;
                                case 1:
                                    Commented item = arrayList.get(position); //ArrayList의 지정된 포지션값을 불러온다.
                                    System.out.println(userid);
                                    System.out.println(item.getName());
                                        System.out.println("삭제클릭");
                                        adapter.removeItem(position); //해당 포지션의 아이템을 삭제
                                        commentList.clearChoices(); //선택 해제
                                        adapter.notifyDataSetChanged(); // 재 갱신
                                        Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }).setNegativeButton("", null).show();
                }else{
                    Toast.makeText(EvaluationActivity.this, "다른 사람의 댓글은 수정하거나 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });





        //********************************************************
        Intent intent = getIntent();
        ImageView evaluationimg = (ImageView) findViewById(R.id.evaluationimage);
        Uri gettingImageUri = intent.getParcelableExtra("img");//img라는 키값의 Uri를 parse할 수 있는 메서드.
        System.out.println(intent.getParcelableExtra("img")+"URI");
        Picasso.with(EvaluationActivity.this).load(gettingImageUri).into((evaluationimg));//evaluationimg라는 imageview에 picasso를 적용시키겠다.
        TextView evaluationname = (TextView) findViewById(R.id.evaluationname);
        evaluationname.setText(intent.getStringExtra("songname"));
        TextView evaluationsinger = (TextView) findViewById(R.id.evaluationsinger);
        evaluationsinger.setText(intent.getStringExtra("singer"));
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }
}