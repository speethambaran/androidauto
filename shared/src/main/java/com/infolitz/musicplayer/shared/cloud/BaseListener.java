package com.infolitz.musicplayer.shared.cloud;

public interface BaseListener {

    void onStarted();

    void onCompleted();

    void onConnectionFailure(int errorCode);

}
