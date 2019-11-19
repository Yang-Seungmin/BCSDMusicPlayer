package com.ysmstudio.bcsdmusicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;

import static com.ysmstudio.bcsdmusicplayer.MainActivity.CHANNEL_ID;

public class MusicPlayService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final int MUSIC_NOTIFICATION_ID = 100;
    private MusicState musicState = MusicState.STOPPED;

    private OnMusicChangedListener onMusicChangedListener;

    private NotificationCompat.Builder builder;
    private Notification notification;

    private PendingIntent pendingIntent;

    private MusicItem nowPlayingMusicItem;

    private MediaPlayer mediaPlayer = null;

    private final IBinder binder = new MusicPlayServiceBinder();

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setMusicState(MusicState.STOPPED);
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(false);
        notifyNotification();
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("TAG", what + ", " + extra);
        return false;
    }

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

    public void setMusicState(MusicState musicState) {
        this.musicState = musicState;
        if(onMusicChangedListener != null) onMusicChangedListener.onMusicStateChanged(this.musicState);
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

    private void initMusicPlayer(MusicItem musicItem) throws IOException {
        Log.d("TAG", String.valueOf(musicItem.getMusicUri()));
        //Log.d("TAG", musicItem.getMusicTitle());
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getApplicationContext(), musicItem.getMusicUri());
        mediaPlayer.prepareAsync();
    }

    public void changeMusicItem(MusicItem musicItem) throws IOException {
        nowPlayingMusicItem = musicItem;
        setMusicState(MusicState.PLAYING);
        Intent intentMainActivity = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intentMainActivity, 0);

        setMusicState(MusicState.PLAYING);
        if (onMusicChangedListener != null) {
            onMusicChangedListener.onMusicStateChanged(musicState);
            onMusicChangedListener.onMusicChanged(nowPlayingMusicItem);
        }
        initMusicPlayer(musicItem);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(musicItem.getMusicTitle())
                .setContentText(musicItem.getMusicArtist())
                .setSmallIcon(musicState.id)
                .setContentIntent(pendingIntent)
                .setSubText(String.valueOf(musicState))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notification = builder.build();

        startForeground(MUSIC_NOTIFICATION_ID, notification);
    }

    public class MusicPlayServiceBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    public void playMusic() throws IOException {
        setMusicState(MusicState.PLAYING);
        if (onMusicChangedListener != null) {
            onMusicChangedListener.onMusicStateChanged(musicState);
            onMusicChangedListener.onMusicChanged(nowPlayingMusicItem);
        }

        mediaPlayer.start();

        notifyNotification();
        startForeground(MUSIC_NOTIFICATION_ID, notification);
    }

    public void playMusic(MusicItem musicItem) throws IOException {
        changeMusicItem(musicItem);
    }

    public void pauseMusic() {
        setMusicState(MusicState.PAUSED);
        if (onMusicChangedListener != null)
            onMusicChangedListener.onMusicStateChanged(musicState);
        stopForeground(false);

        if(mediaPlayer != null) mediaPlayer.pause();

        notifyNotification();
    }

    private void notifyNotification() {
        builder.setSubText(String.valueOf(musicState));
        builder.setSmallIcon(musicState.id);
        notification = builder.build();

        NotificationManagerCompat.from(this).notify(MUSIC_NOTIFICATION_ID, notification);
    }
}
