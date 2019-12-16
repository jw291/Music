package com.example.jaewon.musiccursorex;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("ValidFragment")
class ImageFragment extends Fragment{
    // static이기 때문에 100장이든 몇장이든 이거 하나로 처리 가능
    public static ImageFragment newInstance(int imageUrl) { //매개변수로 받음

        Bundle args = new Bundle();
        args.putInt("imageUrl",imageUrl);//url값이 번들에 담기고
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.imagefragment,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(getArguments().getInt("imageUrl"));
        //담긴걸 뽑아옴.
        return view;
    }
}
class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    int image[] = {R.drawable.musicplay,R.drawable.share,R.drawable.evaluate};
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ImageFragment().newInstance(image[position]);
    }

    @Override
    public int getCount() {
        return image.length;
    }
}

public class ExplainActivity extends AppCompatActivity {
    Button btn_loginactivitybutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Explain","--OnCreate--");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);

        ViewPager viewPager = (ViewPager)findViewById(R.id.main_viewpager);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(myViewPagerAdapter);

        btn_loginactivitybutton=(Button)findViewById(R.id.loginactivitybutton);
        btn_loginactivitybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExplainActivity.this, LogIn.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i("Explain","--OnStart--");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("Explain","--OnCreate--");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Explain","--OnPause--");
        super.onPause();
        finish(); // 로그인 화면에서 다시 뒤로가기 버튼 눌러도 재실행 불가
    }

    @Override
    protected void onStop() {
        Log.i("Explain","--OnStop--");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("Explain","--OnRestart--");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i("Explain","--OnDestroy--");
        super.onDestroy();
    }
}
