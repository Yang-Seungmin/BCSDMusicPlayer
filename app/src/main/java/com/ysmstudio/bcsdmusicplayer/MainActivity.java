package com.ysmstudio.bcsdmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, MusicPlayService.OnMusicChangedListener {
    public static final String CHANNEL_ID = "CHANNEL_PLAYING_MUSIC";
    private boolean musicServiceBound = false;
    private boolean musicServiceCreated = false;

    private MusicPlayService musicPlayService;

    private static final int REQUEST_PERMISSION_CODE = 100;

    private RecyclerView musicRecyclerView;
    private MusicRecyclerAdapter musicRecyclerAdapter;
    private ArrayList<MusicItem> musicItems;

    private int selectedMusicPosition = -1;

    private TextView textViewNowPlaying;

    private AppCompatImageButton buttonPlayPause, buttonPrev, buttonNext;

    private MusicRecyclerAdapter.OnItemClickListener onItemClickListenerMusic = new MusicRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            selectedMusicPosition = position;
            if (musicServiceBound) {
                try {
                    musicPlayService.playMusic(musicItems.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayService.MusicPlayServiceBinder binder = (MusicPlayService.MusicPlayServiceBinder) service;
            musicPlayService = binder.getService();
            musicServiceBound = true;

            musicPlayService.setOnMusicChangedListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        if (checkPermission()) getMusicList();
        createNotificationChannel();

        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicRecyclerView.setAdapter(musicRecyclerAdapter);

        musicRecyclerAdapter.setOnItemClickListener(onItemClickListenerMusic);

        buttonPlayPause.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        buttonPrev.setOnClickListener(this);

        startMusicService();
    }

    private void startMusicService() {
        Intent intent = new Intent(MainActivity.this, MusicPlayService.class);
        startService(intent);
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Now playing music notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void getMusicList() {
        //Log.d("Public Music Dir", String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));

        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "Cursor null or empty");
        } else {
            do {
                Uri contentUri = ContentUris.withAppendedId(
                        externalUri,
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

                musicItems.add(new MusicItem(
                        contentUri,
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        MusicConverter.convertDuration(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))))
                ));

            } while (cursor.moveToNext());
            musicRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private boolean checkPermission() {
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readStoragePermission == PackageManager.PERMISSION_GRANTED) {
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
        musicRecyclerView = findViewById(R.id.recycler_view_music_item);
        musicItems = new ArrayList<>();
        musicRecyclerAdapter = new MusicRecyclerAdapter(musicItems);
        textViewNowPlaying = findViewById(R.id.text_view_now_playing);

        buttonPlayPause = findViewById(R.id.button_music_control_play_pause);
        buttonPrev = findViewById(R.id.button_music_control_prev);
        buttonNext = findViewById(R.id.button_music_control_next);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMusicList();
            } else finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_music_control_play_pause:
                if (musicPlayService != null && musicPlayService.getNowPlayingMusicItem() != null) {
                    if (musicPlayService.getMusicState() == MusicState.PAUSED) {
                        try {
                            musicPlayService.playMusic();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (musicPlayService.getMusicState() == MusicState.PLAYING) {
                        musicPlayService.pauseMusic();
                    }
                }
                break;
            case R.id.button_music_control_next:
                if (musicPlayService != null) {
                    selectedMusicPosition++;
                    if (selectedMusicPosition == musicItems.size()) selectedMusicPosition = 0;
                    try {
                        musicPlayService.playMusic(musicItems.get(selectedMusicPosition));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.button_music_control_prev:
                if (musicPlayService != null && selectedMusicPosition > -1) {
                    selectedMusicPosition--;

                    if (selectedMusicPosition == -1) selectedMusicPosition = musicItems.size() - 1;

                    try {
                        musicPlayService.playMusic(musicItems.get(selectedMusicPosition));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onMusicStateChanged(MusicState musicState) {
        if (musicState == MusicState.PLAYING) {
            buttonPlayPause.setEnabled(true);
            buttonPlayPause.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_pause_black_24dp)
            );
        } else if (musicState == MusicState.PAUSED) {
            buttonPlayPause.setEnabled(true);
            buttonPlayPause.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_black_24dp)
            );
        } else if (musicState == MusicState.STOPPED) {
            buttonPlayPause.setEnabled(false);
            buttonPlayPause.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_black_24dp)
            );
        }
    }

    @Override
    public void onMusicChanged(MusicItem musicItem) {
        if (musicItem != null) textViewNowPlaying.setText(String.valueOf(musicItem));
    }

    @Override
    protected void onDestroy() {
        unbindService(musicServiceConnection);
        super.onDestroy();
    }
}
