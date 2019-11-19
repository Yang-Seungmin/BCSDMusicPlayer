package com.ysmstudio.bcsdmusicplayer;

import androidx.annotation.NonNull;

public enum MusicState {
    PLAYING("Now playing", R.drawable.ic_play_circle_filled_black_24dp),
    PAUSED("Paused", R.drawable.ic_pause_circle_filled_black_24dp),
    STOPPED("Stopped", R.drawable.ic_pause_circle_filled_black_24dp);

    public String string;
    public int id;

    MusicState(String s, int id) {
        string = s;
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return string;
    }
}
