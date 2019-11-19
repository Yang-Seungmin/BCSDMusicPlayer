package com.ysmstudio.bcsdmusicplayer;

public class MusicConverter {
    public static String convertDuration(long duration) {
        long hours, minutes, seconds;

        String hoursString, minutesString, secondsString;

        hours = duration / 3600000;
        minutes = (duration - hours * 3600000) / 60000;
        seconds = (duration - hours * 3600000 - minutes * 60000) / 1000;

        if (hours == 0) hoursString = "";
        else hoursString = hours + ":";

        minutesString = String.format("%02d:", minutes);
        secondsString = String.format("%02d", seconds);

        return hoursString + minutesString + secondsString;

    }
}
