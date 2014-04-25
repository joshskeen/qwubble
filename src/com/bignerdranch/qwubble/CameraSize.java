package com.bignerdranch.qwubble;

public class CameraSize {
    private final int mWidth;
    private final int mHeight;

    public CameraSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getHeightWithButtonOffset(){
        return getHeight() - ( MainActivity.BUTTON_HEIGHT + 50);
    }

    public int percentWidthPx(int percent){
        return getWidth() / percent;
    }

    public int percentHeightPx(int percent){
        return getHeight() / percent;
    }
}
