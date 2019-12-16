package com.example.jaewon.musiccursorex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    Button btn_membershipbutton;
    Button btn_loginbutton;
    EditText et_userid;
    EditText et_userpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LogIn","--OnCreate--");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        btn_membershipbutton = (Button)findViewById(R.id.membershipbutton);
        btn_membershipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, Membership.class);
                startActivity(intent);
            }
        });

        //********************************로그인 온클릭*******************************
        et_userid = (EditText)findViewById(R.id.userid);
        et_userpassword= (EditText)findViewById(R.id.userpassword);
        btn_loginbutton = (Button)findViewById(R.id.loginbutton);
        btn_loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userid = et_userid.getText().toString();
                String userpassword = et_userpassword.getText().toString();
                //sharedpreference객체 불러옴
                SharedPreferences preferences = getSharedPreferences("final test1",MODE_PRIVATE); // Register라는 파일명의 저장소를 불러옴.
                boolean checkID = preferences.contains(userid);
                boolean checkpassword = preferences.contains(userpassword);
                //회원가입의 edittext로 작성한 키값과 일치하지 않으면 등록되지않은 회원을 getstring함.
                if (userid.equals("") || userpassword.equals(" ")) {   //아무것도 입력하지 않으면 로그인되지 않고 메시지 출력
                    Toast.makeText(LogIn.this, "ID를 입력해주세요.", Toast.LENGTH_LONG).show();
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_userid.startAnimation(animation);
                    et_userid.setError("ID를 입력해주세요");
                } else if (checkID == false) {  //입력한 ID 존재하지 않으면 아래 출력
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_userid.startAnimation(animation);
                    Toast.makeText(LogIn.this, "ID가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    et_userid.setError("ID를 다시 입력하세요.");

                }else if (checkpassword == false){  // 입력한 비번 존재하지 않으면 아래 출력
                    Toast.makeText(LogIn.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_userpassword.startAnimation(animation);
                    et_userpassword.setError("비밀번호를 다시 입력하세요.");
                }else {  //ID와 비번이 일치하면 아래 실행*/
                    String user = preferences.getString(userid + userpassword + "data", "로그인중인 아이디");
                    SharedPreferences.Editor editor = preferences.edit();
                    String userimage = preferences.getString(userid + userpassword + "imagedata", "로그인한 아이디 이미지");
                    editor.putString("final", user);
                    editor.putString("GetmailBox", user);//보낸사람 출력용
                    editor.putString("finalimage", userimage);
                    System.out.println("login userimage tostring" + userimage);
                    editor.commit();
                    /*
                    SharedPreferences sharedPreferences = getSharedPreferences("mailtest", MODE_PRIVATE);
                    String getuser = sharedPreferences.getString(userid, "메일 받은사람 계정");
                    System.out.println("로그인 class의 getuser" + getuser);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("mailget", getuser);//메일 받은사람 계정으로 들어온 현재 아이디가 들어감.
                    edit.commit();*/
                    //String savedPassword = preferences.getString(password, "");
                    //String savedUserName = preferences.getString(user, "");
                    Intent intent = new Intent(LogIn.this, PlayListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i("LogIn","--OnStart--");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.i("LogIn","--OnPause--");
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        Log.i("LogIn","--OnStop--");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("LogIn","--OnRestart--");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i("LogIn","--OnDestroy--");
        super.onDestroy();
    }
}
