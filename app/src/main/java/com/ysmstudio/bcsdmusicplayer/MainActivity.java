package com.ysmstudio.bcsdmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int REQUEST_PERMISSION_CODE = 100;

    private ListView listView;
    private ArrayList<MusicItem> musicItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        if(checkPermission()) getMusicList();
    }

    private void getMusicList() {
        //Log.d("Public Music Dir", String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));

        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "Cursor null or empty");
        } else {
            do {
                String contentUri = externalUri.toString() + "/" + cursor.getString(0);
                Log.d("TAG", contentUri);
            } while(cursor.moveToNext());
        }


    }

    private boolean checkPermission() {
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(readStoragePermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission();
        }
        return false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                 REQUEST_PERMISSION_CODE);

    }

    private void init() {
        listView = findViewById(R.id.list_view_music);
        musicItems = new ArrayList<>();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMusicList();
            } else finish();
        }

    }
}
