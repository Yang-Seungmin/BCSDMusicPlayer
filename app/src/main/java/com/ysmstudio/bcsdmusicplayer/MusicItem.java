package com.ysmstudio.bcsdmusicplayer;

import android.net.Uri;

public class MusicItem {
    private Uri musicUri;
    private String musicTitle, musicArtist, musicDuration;

    public MusicItem(Uri musicUri, String musicTitle, String musicArtist, String musicDuration) {
        this.musicUri = musicUri;
        this.musicTitle = musicTitle;
        this.musicArtist = musicArtist;
        this.musicDuration = musicDuration;
    }

    public String getMusicDuration() {
        //Log.d("TAG", musicDuration);
        return musicDuration;
    }

    public Uri getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(Uri musicUri) {
        this.musicUri = musicUri;
    }

    public void setMusicDuration(String musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getMusicTitle() {
        //Log.d("TAG", musicTitle);
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getMusicArtist() {
        //Log.d("TAG", musicArtist);
        return musicArtist;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }

    @Override
    public String toString() {
        return musicTitle + " - " + musicArtist;
    }
}
