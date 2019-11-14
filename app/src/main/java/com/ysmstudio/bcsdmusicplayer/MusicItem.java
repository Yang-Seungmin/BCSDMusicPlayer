package com.ysmstudio.bcsdmusicplayer;

public class MusicItem {
    private String musicTitle, musicArtist;

    public MusicItem(String musicTitle, String musicArtist) {
        this.musicTitle = musicTitle;
        this.musicArtist = musicArtist;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }

    @Override
    public String toString() {
        return "[" + musicArtist + "] " + musicTitle;
    }
}
