package com.example.jaewon.musiccursorex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Setting extends AppCompatActivity {
    Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn_logout = (Button) findViewById(R.id.logoutbutton);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "로그아웃을 완료했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Setting.this, LogIn.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i("Setting", "--OnStart--");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("Setting", "--OnResume--");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Setting", "--OnPause--");
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        Log.i("Setting", "--OnStop--");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("Setting", "--OnReStart--");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i("Setting", "--OnDestroy--");
        super.onDestroy();
    }
}