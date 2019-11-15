package com.ysmstudio.bcsdmusicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import static com.ysmstudio.bcsdmusicplayer.MainActivity.CHANNEL_ID;

public class MusicPlayService extends Service {
    private static final int MUSIC_NOTIFICATION_ID = 100;
    private NotificationCompat.Builder builder;
    private Notification notification;

    private PendingIntent pendingIntent;

    private MusicItem nowPlayingMusicItem;

    private final IBinder binder = new MusicPlayServiceBinder();

    public MusicPlayService() {
    }

    public MusicItem getNowPlayingMusicItem() {
        return nowPlayingMusicItem;
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
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notification = builder.build();

        startForeground(MUSIC_NOTIFICATION_ID, notification);
    }

    public class MusicPlayServiceBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }
}
