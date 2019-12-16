package com.example.jaewon.musiccursorex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

class Getmail{
    String name;
    String title;
    public Getmail(String name , String title){
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
class ViewHolderGetMailBox{
    TextView title, name;
}
class GetMailAdapter extends BaseAdapter{
    private ArrayList<Getmail> arrayList = new ArrayList<>();
    private Context context;
    private int layout;
    public GetMailAdapter(Context context, int layout, ArrayList<Getmail> arrayList){
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Getmail getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        final ViewHolderGetMailBox viewHolderGetMailBox;
        if(convertView ==null){
            viewHolderGetMailBox = new ViewHolderGetMailBox();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            viewHolderGetMailBox.name = (TextView)convertView.findViewById(R.id.getmailboxgetname);
            viewHolderGetMailBox.title = (TextView)convertView.findViewById(R.id.getmailboxtitle);

            convertView.setTag(viewHolderGetMailBox);
        }else {
            viewHolderGetMailBox = (ViewHolderGetMailBox)convertView.getTag();
        }

        Getmail myItem = arrayList.get(position);
        viewHolderGetMailBox.name.setText(myItem.getname());
        viewHolderGetMailBox.title.setText(myItem.gettitle());
        return convertView;
    }
}
public class GetmailBox extends AppCompatActivity {
    ArrayList<Getmail> arrayList;
    ListView listView;
    GetMailAdapter adapter;
    Button button;
    Getmail getmail;
    private void saveData() {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("mailtest",MODE_PRIVATE);
        String getmailuser = sharedPreferences.getString("mailget","");
        login,getmailbox,sendmailactivity
        */
        SharedPreferences sharepreferences = getSharedPreferences("final test1",MODE_PRIVATE);
        String username = sharepreferences.getString("final",null);//현재 로그인한 아이디.
        SharedPreferences preferences = getSharedPreferences(username, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("qwerqewrrr", json);
        editor.apply();
    }


    private void loadData() {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("mailtest",MODE_PRIVATE);
        String getmailuser = sharedPreferences.getString("mailget","");
        System.out.println(getmailuser+"getmailuser!!!!!!!!!!!!1");*/
        SharedPreferences sharepreferences = getSharedPreferences("final test1",MODE_PRIVATE);
        String username = sharepreferences.getString("final",null);//현재 로그인한 아이디.
        SharedPreferences preferences = getSharedPreferences(username, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("qwerqewrrr", "");
        Type type = new TypeToken<ArrayList<Getmail>>() {
        }.getType();//read
        arrayList = gson.fromJson(json, type);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getmail);
        System.out.println("****************************onCreate");

        loadData();

        listView = (ListView)findViewById(R.id.GetmailListview);
        adapter = new GetMailAdapter(this,R.layout.custom_getmailbox,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(view+"클릭 view값");
                getmail = adapter.getItem(position);
                Intent intent = new Intent(GetmailBox.this,GetMailDetail.class);
                intent.putExtra("name",getmail.getname());
                intent.putExtra("title",getmail.gettitle());
                startActivity(intent);
            }
        });
        TextView tv = (TextView)findViewById(R.id.font2);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/haneul.ttf");
        tv.setTypeface(typeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("****************************onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("****************************onStart");
        SharedPreferences preferences = getSharedPreferences("final test1",MODE_PRIVATE);
        String username = preferences.getString("final",null);//현재 로그인한 아이디.


        SharedPreferences sharedPreferences = getSharedPreferences("mailtest", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String getname = sharedPreferences.getString("GetmailBoxname",null);
        System.out.println(getname);
        String mailname = sharedPreferences.getString("beforename",null);
        String mailtitle = sharedPreferences.getString("GetmailBoxtitle", null);
        if (mailname != null && username.equals(getname)) {//resume에서 지우는데 다시 들어올때 null이 되므로 추가를 안함.
            //느낀점. 한가지 생각에 머물러 있으면 계속 실패한다.
            //나는 load에서 어떻게 해야 한다는 생각에만 머물러서 키값만 계속 바꿨었는데
            //생각해보니까 그냥 add할때 if문을 주면 됐던 것이다....
            //느낀점 : 한가지 생각에 머무르기 보단 다양한 해결방안을 모색하자
            //그리고 머리 아프고 안풀리면 바람을 쐬자..ㅎㅎ
            //여기선 새로 입력했으므로 null이 아니니까 추가함.
            System.out.println(mailname + "null이 아닐때 add");
            System.out.println(mailtitle + "null이 아닐때 add");
            System.out.println("username : " + username + "getname : " + getname + "이 같으므로 추가한다.");
            int position = adapter.getCount();
            arrayList.add(new Getmail(mailname, mailtitle));
            adapter.notifyDataSetChanged();
            editor.remove("beforename");
            editor.commit();

        } else {
                System.out.println("username : " + username + "getname : " + getname + "이 같지 않으므로 추가하지 않겠다..");
                System.out.println(mailname + "mailname은 null이다.");
                System.out.println(mailtitle + "mailtitle은 null이다.");
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

        System.out.println("****************************onDestroy");
        finish();
    }
}
