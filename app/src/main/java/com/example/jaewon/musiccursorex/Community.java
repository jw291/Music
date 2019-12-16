package com.example.jaewon.musiccursorex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

class communityitem {
    private String icon;
    private String name;

    public communityitem(String icon, String name){
        this.icon = icon;
        this.name = name;
    }//add를 위한 메소드

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

}
class ViewHolderCommunity{
 TextView tv_name;
 ImageView iv_img;
}
class CommunityAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int layout;
    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<communityitem> communitylist = new ArrayList<>();
    //!#$필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트를 보유하고있음.
    private ArrayList<communityitem> filteredItemList = communitylist;

    Filter listFilter;

    public CommunityAdapter(Context context, int layout, ArrayList<communityitem> communitylist){
        this.context = context;
        this.layout = layout;
        this.communitylist=communitylist;
        this.filteredItemList = communitylist;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    public class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = communitylist;
                results.count = communitylist.size();
            } else {
                ArrayList<communityitem> itemList = new ArrayList<communityitem>();

                for (communityitem item : communitylist) {
                    if (item.getName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<communityitem>) results.values;
            //notify
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }

    //!#$Adapter에 사용되는 데이터의 개수를 리턴한다. : 필수 구현
    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public communityitem getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        filteredItemList.remove(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        final ViewHolderCommunity viewHolder;
        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            viewHolder = new ViewHolderCommunity();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);

            viewHolder.iv_img = (ImageView) convertView.findViewById(R.id.friendimage);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.friendname);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolderCommunity) convertView.getTag();
        }
        //****************************************

        //****************************************
        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        //MyItem myItem = getItem(position);
        //!@#$Data set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        communityitem myItem = filteredItemList.get(position);


        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        Glide.with(context).load(myItem.getIcon()).into(viewHolder.iv_img);
        //viewHolder.iv_img.setImageResource(myItem.getIcon());
        viewHolder.tv_name.setText(myItem.getName());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

        return convertView;
    }
}

public class Community extends AppCompatActivity {
    Dialog dialog;
    Button dialog_fiendadd_btn;
    EditText et;
    private SwipeMenuListView mListView;
    CommunityAdapter mMyAdapter;
    communityitem community;
    ArrayList<communityitem> arrayList;
    //**************************
    //Community클래스에서 Add시킨 ArrayList를 ChattingList클래스에서 setAdapter해주고, load,save해야함.
    //parcelable로 ChattingList클래스에 보내줄 것
    //ChattingList클래스에 arraylist를 보내줬기 때문에 ChattingList클래스에서는 Gson을 이용 할 수 있음.


    private void saveData() {
        SharedPreferences preferences = getSharedPreferences("final test1", MODE_PRIVATE);
        String userid = preferences.getString("final", "");

        SharedPreferences sharedPreferences = getSharedPreferences(userid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("community", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences preferences = getSharedPreferences("final test1", MODE_PRIVATE);
        String userid = preferences.getString("final", "");

        SharedPreferences sharedPreferences = getSharedPreferences(userid, MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("community", "");
        Type type = new TypeToken<ArrayList<communityitem>>() {
        }.getType();//read
        arrayList = gson.fromJson(json, type);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
    }

    private void runanimation(ListView listView, int type){
        Context context = listView.getContext();
        LayoutAnimationController controller = null;
        if(type == 0)
            controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);

        mMyAdapter = new CommunityAdapter(this, R.layout.cutom_communitylistitem, arrayList);
        mListView.setAdapter(mMyAdapter);
       listView.setLayoutAnimation(controller);
       mMyAdapter.notifyDataSetChanged();
       listView.scheduleLayoutAnimation();
    }

    protected void removeListItem(View rowView,final int position){
        final Animation animation = AnimationUtils.loadAnimation(Community.this, R.anim.slide_out);
        rowView.startAnimation(animation);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
         @Override
             public void run() {
                 mMyAdapter.removeItem(position);
                 mMyAdapter.notifyDataSetChanged();
                 animation.cancel();
             }
        },100);
   }


    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Community", "--OnCreate--");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        //****************검색**********************
        loadData();
        mListView = (SwipeMenuListView) findViewById(R.id.communitylistview);
        runanimation(mListView,0);
        mMyAdapter = new CommunityAdapter(this, R.layout.cutom_communitylistitem, arrayList);
        mListView.setAdapter(mMyAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0x66, 0xff,
                        0x8c)));
                // set item width
                openItem.setWidth(170);
                // set item title
                openItem.setIcon(R.drawable.ic_contact_mail_black_24dp);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xff,
                        0x66, 0x66)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, final int index) {
                switch (index) {
                    case 0:
                        // 쪽지 쓰는 곳으로 이동
                        // 받는 사람이 보내져야한다.
                        // 보낸 사람은 이미 shared로 넣었다.
                        community = mMyAdapter.getItem(position);
                        Intent intent = new Intent(Community.this,SendmailActivity.class);
                        intent.putExtra("getfriendname",community.getName());
                        startActivity(intent);
                        //Toast.makeText(Community.this, "받는 사람"+community.getName(), Toast.LENGTH_SHORT).show();

                        break;
                    case 1:
                        removeListItem(mListView.getChildAt(position),position);
                        //Toast.makeText(Community.this, "아이템클릭"+index, Toast.LENGTH_SHORT).show();
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        //____________________________________________________
        EditText editTextFilter = (EditText) findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString() ;
                //필터링 텍스트 팝업은 ListView가 표시하는 UI이며,
                // ListView의 setFilterText() 함수를 통해 필터링 텍스트가 전달되면 무조건 표시되게 만들어져 있다.
                // 그래서 필터링 텍스트 팝업을 보이지 않게 만들고자 한다면,
                // setFilterText() 함수 이외의 방법을 사용하여 필터링을 수행하면 된다.
                // 즉, ListView를 통하지 않고 Adapter로부터 직접 Filter 객체의 참조를 가져와서
                // filter() 함수를 호출하면 되는 것이다.

                /*
                     if (filterText.length() > 0) {
                    listview.setFilterText(filterText) ;
                 } else {
                    listview.clearTextFilter() ;
                    }
                */
                //swipelistview는 listview를 상속 받은 클래스이기 때문에
                //cleartextfilter를 이용 할 수 있음. 그런데 무슨 문제인지 잘 해결이 안됨.
                //그래서 직접 어댑터의 getfilter클래스를 이용해서 filtering해줌줌
              mMyAdapter.getFilter().filter(filterText);//와.. 이렇게 하니까 되네 ㅎ

            }
        });

        //********************리스트뷰 끝************************

        //*******************플로팅버튼***********************
        final com.github.clans.fab.FloatingActionButton mAddbutton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.friendaddbutton);
        final com.github.clans.fab.FloatingActionButton msendbutton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.sendfloatingbutton);
        final com.github.clans.fab.FloatingActionButton mgetbutton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.getfloatingbutton);
        //**********************************추가*********************
        mAddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다이얼로그 생성하기
                dialog = new Dialog(Community.this);
                //다이얼로그 화면 구성하기
                dialog.setContentView(R.layout.dialog_friendadd);
                et = (EditText) dialog.findViewById(R.id.friendet);
                //dialog_fiendadd_btn = (Button)findViewById(R.id.dialog_friendadd_btn); 이렇게 하면 안됨 다이얼로그의 보튼은 activity_main의 버튼이 아님.
                dialog_fiendadd_btn = (Button) dialog.findViewById(R.id.dialog_friendadd_btn);
                dialog_fiendadd_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //추가버튼;
                        String name = et.getText().toString(); // EDITTEXT에서 STRING빼오기
                        SharedPreferences preferences = getSharedPreferences("final test1", MODE_PRIVATE); // Register라는 파일명의 저장소를 불러옴.
                        String userid = preferences.getString("final", "");
                        System.out.println(userid);
                        boolean checkname = preferences.contains(name);
                        if (et.equals("")) {
                            Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                        } else if (checkname == false) {
                            Toast.makeText(getApplicationContext(), "찾을 수 없는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        } else if (name.equals(userid)) {//현재 로그인한 아이디인 userid와 edittext에서 뽑아낸 애가 같으면 안됌.
                            Toast.makeText(getApplicationContext(), "자기 자신은 추가 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            String userimagedata = preferences.getString(name + "userimagedata","");
                            //drawable대신에 로그인한 아이디의 사진이 들어가야함.
                            arrayList.add(new communityitem(userimagedata, name));
                            mMyAdapter.notifyDataSetChanged();
                            et.setText("");
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    msendbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Community.this,SendmailBox.class);
            startActivity(intent);
            //보낸 메일함으로 이동
        }
    });

    mgetbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //받은 메일함으로 이동.
            Intent intent = new Intent(Community.this,GetmailBox.class);
            startActivity(intent);

        }
    });
        //**********************플로팅버튼 끝**************************
    }

    @Override
    protected void onStart() {
        Log.i("Community", "--OnStart--");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("Community", "--OnResume--");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i("Community", "--OnStop--");
        super.onStop();
        finish();
    }

    @Override
    protected void onRestart() {
        Log.i("Community", "--OnReStart--");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.i("Community", "--OnPause--");
        super.onPause();
        saveData();

    }

    @Override
    protected void onDestroy() {
        Log.i("Community", "--OnDestroy--");
        super.onDestroy();
    }
}
