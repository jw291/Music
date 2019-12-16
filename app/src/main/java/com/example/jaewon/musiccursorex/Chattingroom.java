package com.example.jaewon.musiccursorex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

class ChatMessage {
    public boolean left;
    public String message;
    public String userid;
    public ChatMessage(boolean left, String message,String userid) {
        //super();
        this.left = left;
        this.message = message;
    }
    public String getuserid(){
        return this.userid;
    }
}
class ChatArrayAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private TextView chatText;
    private ArrayList<ChatMessage> chatMessageList = new ArrayList();
    private LinearLayout singleMessageContainer;
    public ChatArrayAdapter(Context context, int layout, ArrayList<ChatMessage> chatMessageList) {
        this.context = context;
        this.layout = layout;
        this.chatMessageList = chatMessageList;
    }


    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
        }

        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.message);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);

        //chatMessageObj.getuserid() == 현재 로그인한 아이디
        //



        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);//left가 true면 왼쪽 아니면 right
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
public class Chattingroom extends Activity {
    Intent intent;
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private int img;
    private ArrayList<ChatMessage> arrayList;
    private boolean side;
    private void saveData() {
        Intent intent = getIntent();
        String otheruser = intent.getStringExtra("name");//친구리스트를 Longclick했을 시에 받아오는 친구의 이름 = otheruser

        SharedPreferences sharedPreferences = getSharedPreferences("final test1", MODE_PRIVATE);// "bbb"라는 회원가입 Sharepreference를 통해서 현재로그인 한 아이디를 알 수 있음
        String userid = sharedPreferences.getString("final","");//현재 로그인한 아이디 == userid

        ArrayList<String> sortarrayList = new ArrayList<>();
        sortarrayList.add(userid);
        sortarrayList.add(otheruser);
        Collections.sort(sortarrayList);
        for(String str : sortarrayList){
            System.out.println(str);
        }
        String key = sortarrayList.get(0)+sortarrayList.get(1);
        System.out.println(key);

        SharedPreferences preferences = getSharedPreferences(key , MODE_PRIVATE);//내가 상대방에게 보낸 대화방.
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(arrayList); // 나->상대에게 보낸 대화내용 json이 가지고 있음.
        //이 json을 상대도 봐야함.
        editor.putString("chatt", json);//내가 상대방에게 보낸 대화내용은 json임
        editor.apply();

    }

    private void loadData() {
        Intent intent = getIntent();
        String otheruser = intent.getStringExtra("name");//상대방(key) 가능함 확인.

        SharedPreferences sharedPreferences = getSharedPreferences("final test1", MODE_PRIVATE);
        String userid = sharedPreferences.getString("final","");//나

        //Arraylist에 userid와 otheruser를 넣어놓고
        //Collections를 이용하여 솔트 시킴 그럼 알파벳 대로 정렬이 될것임.
        //그리고 그 정렬된 어레이리스트를 get하면
        //정렬된 문자열로 출력이 됌. 결국엔 userid가 A이고 otheruser가 B
        //userid가 B이고 otheruser가 A이든 AB가 될것임.
        ArrayList<String> sortarrayList = new ArrayList<>();
        sortarrayList.add(userid);
        sortarrayList.add(otheruser);
        Collections.sort(sortarrayList);
        for(String str : sortarrayList){
            System.out.println(str);
        }
        String key = sortarrayList.get(0)+sortarrayList.get(1);
        System.out.println(key);
        //
        SharedPreferences preferences = getSharedPreferences(key, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("chatt", "");
        Type type = new TypeToken<ArrayList<ChatMessage>>() {
        }.getType();//read
        arrayList=gson.fromJson(json,type);
        if(arrayList == null){
            arrayList = new ArrayList<>();
        }

        //만약 인텐트가 2면 1,2 3이면 1,3
        //내가 2일때 1이면 1,2 3이면 2,3
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intent = getIntent();


        ImageView profileimage = (ImageView) findViewById(R.id.getimageprofile);
        img = Integer.parseInt(intent.getStringExtra("profile"));
        profileimage.setImageResource(img);
        TextView friendname = (TextView) findViewById(R.id.getfriendname);
        friendname.setText(intent.getStringExtra("name"));
        buttonSend = (Button) findViewById(R.id.buttonSend);

        listView = (ListView) findViewById(R.id.listView1);
        loadData();
        chatArrayAdapter = new ChatArrayAdapter(this, R.layout.activity_chat_singlemessage, arrayList);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) { //arraylist추가.
                SharedPreferences sharedPreferences = getSharedPreferences("final test1",MODE_PRIVATE);
                String userid = sharedPreferences.getString("final","");//현재 로그인한 아이디.
                arrayList.add(new ChatMessage(!side, chatText.getText().toString(),userid)); // 여기서의 add
                chatText.setText("");
                //side가 false면 오른쪽 true면 왼쪽. 처음엔 false로 초기화 해둬서 오른쪽에 들어가고
                //side = !side를 이용해서 true로 바꿔주고
                //그걸 전역변수에 넣어놨으므로 다음 입력은 true 일것. 그다음 또 side = !side를 통해 false가 됨
                //그게 다시 전역변수로 들어가는 방식임. 이젠 이 방식을 빼고
                //만약 내가 보낸다면? side는 false이다.
                //만약 상대방이 보낸다면? side는 true이다.로 바꿔줘야함.
                chatArrayAdapter.notifyDataSetChanged();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);
        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}