package com.example.jaewon.musiccursorex;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Membership extends AppCompatActivity {
    //--------------------카메라 관련--------------
    private static final int CAMERA_CODE = 10;
    private static final int GALLERY_CODE = 0;
    private String mCurrentPhotoPath;
    String uristr;
    Bitmap resizes;
    Uri uri;
    //--------------------카메라 관련--------------

    Button btn_signupcompletebutton;
    EditText et_newuserid;
    EditText et_newuserpassword2;
    EditText et_newuserpassword;
    ImageView imageView;

    //-----------------------------------------------
    //----------------카메라 함수--------------
    void pickUpPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_CODE);
    }
    void requirePermission(){
        String permissions[] = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED){
                listPermissionsNeeded.add(permission);
            }
        }
        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
        }
    }
    void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this,"com.example.jaewon.musiccursorex.fileprovider",photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent,CAMERA_CODE);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private  File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAMERA_CODE){
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"사진을 저장하셨습니다.",Toast.LENGTH_SHORT).show();
    }
/*
    public String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);

    }
    */

//---------------------------------카메라 함수수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        //-------------------------카메라 사진, 갤러리----------------------
        requirePermission();
        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUpPicture();
            }
        });
        Button save_button = (Button)findViewById(R.id.savecamera);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryAddPic();
            }
        });
        Button camerabutton = (Button)findViewById(R.id.camerabutton);
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean camera = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                if(camera){
                    takePicture();
                }else {
                    Toast.makeText(Membership.this,"권한에 동의하세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //---------------------------------------------------
        et_newuserid = (EditText)findViewById(R.id.newuserid);
        et_newuserpassword = (EditText)findViewById(R.id.newuserpassword);
        et_newuserpassword2 = (EditText)findViewById(R.id.newuserpassword2);
        imageView = (ImageView)findViewById(R.id.signupimage);
        btn_signupcompletebutton =(Button)findViewById(R.id.signupcompletebutton);
        btn_signupcompletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newuserid = et_newuserid.getText().toString();
                String newuserpassword = et_newuserpassword.getText().toString();
                String newuserpassword2 = et_newuserpassword2.getText().toString();

                SharedPreferences preferences = getSharedPreferences("final test1", MODE_PRIVATE); //Register파일 불러옴 현재 LOGIN이랑 Register는 같은 파일에서 키값을 서로 비교하고있음.

                boolean checkID = preferences.contains(newuserid);//contains를 통해 해당 키값을 가지고 있는 놈이 있는지 판별한다.

                if (checkID == true) {//가지고 있으면 회원가입 실패
                    Toast.makeText(Membership.this, "이미 등록된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_newuserid.startAnimation(animation);
                    et_newuserid.setError("아이디를 다시 입력하세요");
                }else {//id이상이 없으면 진행한다.
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(newuserid, newuserid);
                    editor.putString(newuserpassword, newuserpassword);
                    editor.putString(newuserid + "userimagedata", uri.toString());
                    if(newuserid.equals("") || newuserpassword.equals("") || newuserpassword2.equals("")){
                        Toast.makeText(Membership.this,"입력되지 않은 항목이 존재합니다.",Toast.LENGTH_SHORT).show();
                        System.out.println("membership URI***"+uri);
                        System.out.println("membership URIPATH*********"+uri.getPath());
                        System.out.println("membership URI.TOstring*******"+uri.toString());
                    }else {
                        if(newuserpassword2.equals(newuserpassword)){

                            Toast.makeText(Membership.this,newuserid+"로 회원가입 하셨습니다.",Toast.LENGTH_SHORT).show();
                            editor.putString(newuserid + newuserpassword + "data", newuserid);
                            editor.putString(newuserid + newuserpassword + "imagedata", uri.toString());
                            //*****
                            editor.commit();
                            Intent intent = new Intent(Membership.this,LogIn.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(Membership.this,"다르게 입력하셨습니다.",Toast.LENGTH_SHORT).show();
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                            et_newuserpassword2.startAnimation(animation);
                            et_newuserpassword2.setError("다시 입력하세요.");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i("Membership","--OnStart--");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("Membership","--OnResume--");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i("Membership","--OnStop--");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("Membership","--OnRestart--");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.i("Membership","--OnPause--");
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        Log.i("Membership","--OnDestroy--");
        finish();
        super.onDestroy();
    }
}