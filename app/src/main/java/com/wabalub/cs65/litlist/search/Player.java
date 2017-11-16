package com.wabalub.cs65.litlist.search;

import android.support.annotation.Nullable;

public interface Player {

    void play(String url);

    void pause();

    void resume();

    boolean isPlaying();

    @Nullable
    String getCurrentTrack();

    void release();
}
