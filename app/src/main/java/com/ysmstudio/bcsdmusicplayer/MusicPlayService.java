package com.ysmstudio.bcsdmusicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.ysmstudio.bcsdmusicplayer.MainActivity.CHANNEL_ID;

public class MusicPlayService extends Service {
    private static final int MUSIC_NOTIFICATION_ID = 100;
    private MusicState musicState = MusicState.STOPPED;

    private OnMusicChangedListener onMusicChangedListener;

    private NotificationCompat.Builder builder;
    private Notification notification;

    private PendingIntent pendingIntent;

    private MusicItem nowPlayingMusicItem;

    private final IBinder binder = new MusicPlayServiceBinder();

    public interface OnMusicChangedListener {
        void onMusicStateChanged(MusicState musicState);
        void onMusicChanged(MusicItem musicItem);
    }

    public MusicPlayService() {
    }

    public MusicItem getNowPlayingMusicItem() {
        return nowPlayingMusicItem;
    }

    public MusicState getMusicState() {
        return musicState;
    }

    public void setOnMusicChangedListener(OnMusicChangedListener onMusicChangedListener) {
        this.onMusicChangedListener = onMusicChangedListener;
        onMusicChangedListener.onMusicStateChanged(musicState);
        onMusicChangedListener.onMusicChanged(nowPlayingMusicItem);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void changeMusicItem(MusicItem musicItem) {
        nowPlayingMusicItem = musicItem;
        Intent intentMainActivity = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intentMainActivity, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(musicItem.getMusicTitle())
                .setContentText(musicItem.getMusicArtist())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setSubText(String.valueOf(musicState))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notification = builder.build();

        startForeground(MUSIC_NOTIFICATION_ID, notification);

        playMusic();
    }

    public class MusicPlayServiceBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    public void playMusic() {
        musicState = MusicState.PLAYING;
        if (onMusicChangedListener != null) {
            onMusicChangedListener.onMusicStateChanged(musicState);
            onMusicChangedListener.onMusicChanged(nowPlayingMusicItem);
        }
        startForeground(MUSIC_NOTIFICATION_ID, notification);
    }

    public void playMusic(MusicItem musicItem) {
        changeMusicItem(musicItem);
        musicState = MusicState.PLAYING;
        if (onMusicChangedListener != null) {
            onMusicChangedListener.onMusicStateChanged(musicState);
            onMusicChangedListener.onMusicChanged(nowPlayingMusicItem);
        }
    }

    public void pauseMusic() {
        musicState = MusicState.PAUSED;
        if (onMusicChangedListener != null)
            onMusicChangedListener.onMusicStateChanged(musicState);
        stopForeground(false);

        builder.setSubText(String.valueOf(musicState));
        notification = builder.build();

        NotificationManagerCompat.from(this).notify(MUSIC_NOTIFICATION_ID, notification);
    }
}
