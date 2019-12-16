package com.example.jaewon.musiccursorex;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;

public class Intro extends AppCompatActivity {
    private KenBurnsView kbv;
    private TextView tv;
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
// 4초뒤에 다음화면(MainActivity)으로 넘어가기 Handler 사용
            Intent intent = new Intent(getApplicationContext(), ExplainActivity.class);
            startActivity(intent); // 다음화면으로 넘어가기
            finish(); // Activity 화면 제거
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("intro","--OnCreate--");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml과 java소스를 연결
        kbv = findViewById(R.id.kbv);
        tv = findViewById(R.id.tv);
        fadeIn.setDuration(2000);
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(3000);
        tv.setAnimation(fadeIn);
    } // end of onCreate

    @Override
    protected void onStop() {
        Log.i("intro","--OnStop--");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.i("intro","--OnResume--");
        super.onResume();
// 다시 화면에 들어어왔을 때 예약 걸어주기
        handler.postDelayed(r, 9000); // 4초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        Log.i("intro","--OnPause--");
        super.onPause();
// 화면을 벗어나면, handler 에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r); // 예약 취소
    }

    @Override
    protected void onRestart() {
        Log.i("intro","--OnReStart--");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i("intro","--OnDestroy--");
        super.onDestroy();
    }
}
