package com.example.jaewon.musiccursorex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

class Sendmail{
    String name;
    String title;
    public Sendmail(String name , String title){
        this.name = name;
        this.title = title;
    }
    public String getname(){
        return this.name;
    }
    public String gettitle(){
        return this.title;
    }
}
class ViewHolderSendMailBox{
    TextView title, name;
}
class SendmailAdapter extends BaseAdapter{
    private ArrayList<Sendmail> arrayList = new ArrayList<>();
    private Context context;
    private int layout;

    public SendmailAdapter(Context context , int layout , ArrayList<Sendmail>arrayList){
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Sendmail getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        final ViewHolderSendMailBox viewHolderSendMailBox;
        if(convertView ==null){
            viewHolderSendMailBox = new ViewHolderSendMailBox();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            viewHolderSendMailBox.name = (TextView)convertView.findViewById(R.id.sendmailboxgetname);
            viewHolderSendMailBox.title = (TextView)convertView.findViewById(R.id.sendmailboxtitle);

            convertView.setTag(viewHolderSendMailBox);
        }else {
            viewHolderSendMailBox = (ViewHolderSendMailBox)convertView.getTag();
        }

        Sendmail myItem = arrayList.get(position);
        viewHolderSendMailBox.name.setText(myItem.getname());
        viewHolderSendMailBox.title.setText(myItem.gettitle());
        return convertView;
    }
    public void removeItem(int position){
        arrayList.remove(position);
    }
}
public class SendmailBox extends AppCompatActivity {
    ArrayList<Sendmail> arrayList;
    ListView listView;
    SendmailAdapter adapter;
    Button button;
    Sendmail myItem;
    private void saveData() {//리스트뷰 save onstop에서 해주는중.
        SharedPreferences preferences = getSharedPreferences("final test1", MODE_PRIVATE);
        String userid = preferences.getString("final", "");//현재 로그인중인 아이디가 들어있다.

        SharedPreferences sharedPreferences = getSharedPreferences(userid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("eerrerere", json);
        editor.apply();
    }

    private void loadData() {//리스트뷰 load oncreate에서 해주는 중.
        SharedPreferences preferences = getSharedPreferences("final test1", MODE_PRIVATE);
        String userid = preferences.getString("final", "");//현재 로그인 중인 아이디가 들어있다.

        SharedPreferences sharedPreferences = getSharedPreferences(userid, MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("eerrerere", "");
        Type type = new TypeToken<ArrayList<Sendmail>>() {
        }.getType();//read
        arrayList = gson.fromJson(json, type);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmail_box);

        loadData();
        listView = (ListView)findViewById(R.id.sendmailListview);
        adapter = new SendmailAdapter(this,R.layout.custom_sendmailbox,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myItem = adapter.getItem(position);
                Intent intent = new Intent(SendmailBox.this,SetMailDetail.class);
                intent.putExtra("mailname",myItem.getname());
                intent.putExtra("mailTitle",myItem.gettitle());
                startActivity(intent);
                //처음엔 제목이랑 받는 사람을 remove해버리기 때문에 어떻게 이 데이터를 유지 시킬까 고민했는데
                //position의 get을 할 수 있기 때문에 상관 없음.
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(listView.getChildAt(position));
                adapter.removeItem(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        TextView tv = (TextView)findViewById(R.id.font1);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/haneul.ttf");
        tv.setTypeface(typeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("mailtest",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name");
        editor.remove("title");
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("mailtest", MODE_PRIVATE);
        String mailname = sharedPreferences.getString("name", null);
        String mailtitle = sharedPreferences.getString("title", null);
       if(mailname !=null && mailtitle!=null){
           System.out.println(mailname+"null이 아닐때 add");
           System.out.println(mailtitle+"null이 아닐때 add");
           System.out.println("add했으니 resume에서 remove하겠다. 그러면 다시 들어오면 null이므로 추가가 안된다.");
            arrayList.add(new Sendmail(mailname, mailtitle));
            adapter.notifyDataSetChanged();
        } else {
            //널이면 add안해주겠다.
            System.out.println(mailname+"mailname은 null이다.");
            System.out.println(mailtitle+"mailtitle은 null이다.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
