package com.ysmstudio.bcsdmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int REQUEST_PERMISSION_CODE = 100;

    private RecyclerView musicRecyclerView;
    private MusicRecyclerAdapter musicRecyclerAdapter;
    private ArrayList<MusicItem> musicItems;

    private TextView textViewNowPlaying;

    private interface PermissionListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private PermissionListener permissionListener;

    private MusicRecyclerAdapter.OnItemClickListener onItemClickListenerMusic = new MusicRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            textViewNowPlaying.setText(musicItems.get(position).getMusicTitle());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                getMusicList();

                musicRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                musicRecyclerView.setAdapter(musicRecyclerAdapter);

                musicRecyclerAdapter.setOnItemClickListener(onItemClickListenerMusic);
            }
            @Override
            public void onPermissionDenied() {
                showPermissionDialog();
            }
        };
        checkPermission();

    }

    private void getMusicList() {
        //Log.d("Public Music Dir", String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));

        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);
        if(cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "Cursor null or empty");
        } else {
            do {
                musicItems.add(new MusicItem(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        MusicConverter.convertDuration(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))))
                ));

            } while(cursor.moveToNext());
            musicRecyclerAdapter.notifyDataSetChanged();
        }


    }

    private void checkPermission() {
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(readStoragePermission == PackageManager.PERMISSION_GRANTED) {
            permissionListener.onPermissionGranted();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                 REQUEST_PERMISSION_CODE);
    }

    private void init() {
        musicRecyclerView = findViewById(R.id.recycler_view_music_item);
        musicItems = new ArrayList<>();
        musicRecyclerAdapter = new MusicRecyclerAdapter(musicItems);
        textViewNowPlaying = findViewById(R.id.text_view_now_playing);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionListener.onPermissionGranted();
            } else {
                permissionListener.onPermissionDenied();
            }
        }

    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("권한 허용 필요")
                .setMessage("기능을 사용하려면 권한을 허용해야 합니다.")
                .setPositiveButton("다시 시도", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermission();
                    }
                })
                .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
