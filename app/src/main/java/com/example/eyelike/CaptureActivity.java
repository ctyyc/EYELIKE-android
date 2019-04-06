package com.example.eyelike;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_CAMERA =1111;
    private static final int REQUEST_TAKE_PHOTO=2222;
    private static final int REQUEST_TAKE_ALBUM=3333;
    private static final int REQUEST_IMAGE_CROP=4444;
    private static final int REQUEST_IMAGEVIEW=5555;
    Button btn_capture, btn_album;
    ImageView iv_view;
    String mCurrentPhotoPath;
    Uri imageUri;
    Uri photoURI, albumURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_album = (Button) findViewById(R.id.btn_album);
        iv_view = (ImageView) findViewById(R.id.iv_view);

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureCamera();
            }
        });
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum();
            }
        });
        checkPermission();
    }

    private void captureCamera(){
        Log.i("this",getPackageName());
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePicturesIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePicturesIntent.resolveActivity(getPackageManager())!=null){
                File photoFile=null;

                try{
                    photoFile=createImageFile();
                }catch (IOException ex){
                    Log.e("captureCamera Error", ex.toString());
                }
                if(photoFile !=null){
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;
                    takePicturesIntent.putExtra(MediaStore.EXTRA_OUTPUT,providerURI);
                    startActivityForResult(takePicturesIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }else{
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_LONG).show();
            return;
        }
    }
    public File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp + ".jpg";
        File imageFile =null;
        File storageDir =new File(Environment.getExternalStorageDirectory()+"/Pictures", "gyeom");

            if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }
        imageFile=new File(storageDir, imageFileName);
        mCurrentPhotoPath=imageFile.getAbsolutePath();
        return imageFile;
    }

    private void getAlbum(){
        Log.i("getAlbum", "Call");
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_LONG).show();
    }

    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : "+photoURI+" / albumURI : "+albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQUEST_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try{
                        Log.i("REQUEST_TAKE_PHOTO", "ok");
                        galleryAddPic();
                        Intent intent = new Intent(getApplicationContext(), ImgViewActivity.class);
                        intent.putExtra("imageUri", imageUri);
                        startActivityForResult(intent, REQUEST_IMAGEVIEW);
                    }catch (Exception e){
                        Log.e("REQUST_TAKE_PHOTO", e.toString());
                    }
                }else{
                    Toast.makeText(CaptureActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if(resultCode ==Activity.RESULT_OK){
                    if(data.getData()!=null){
                        try{
                            File albumFile =null;
                            albumFile = createImageFile();
                            photoURI=data.getData();
                            albumURI=Uri.fromFile(albumFile);

                            cropImage();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(resultCode==Activity.RESULT_OK){
                    galleryAddPic();
                    Intent intent2  =new Intent(this, ImgViewActivity.class);
                    intent2.putExtra("imageUri", albumURI);
                    startActivityForResult(intent2, REQUEST_IMAGEVIEW);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_CAMERA:
                for(int i=0;i<grantResults.length;i++){
                    if(grantResults[i]<0){
                        Toast.makeText(CaptureActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                break;
        }
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))){
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSION_CAMERA);
            }
        }

    }
}
