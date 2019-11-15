package com.ysmstudio.bcsdmusicplayer;

import androidx.annotation.NonNull;

public enum MusicState {
    PLAYING("Now playing"), PAUSED("Paused"), STOPPED("Stopped");

    String string;
    MusicState(String s) {
        string = s;
    }


    @NonNull
    @Override
    public String toString() {
        return string;
    }
}
