package com.bignerdranch.qwubble;

import android.util.Log;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;

/**
 * Created by bphillips on 4/24/14.
 */
public class ZoomLayerEntity extends Entity implements ZoomSprite.OnZoomListener {
    private Object mZoomData;

    public ZoomListener getZoomListener() {
        return mZoomListener;
    }

    public void setZoomListener(ZoomListener zoomListener) {
        mZoomListener = zoomListener;
    }

    public interface ZoomListener {
        public void onZoomComplete(ZoomSprite zoomSprite, Object zoomData);
    }

    private static final String TAG = "QwubbleZoomLayerEntity";
    private Sprite mNextZoomSprite;
    private ZoomSprite mZoomSprite;
    private CameraSize mCameraSize;
    private Highlighter mHighlighter;
    private ZoomListener mZoomListener;
    private float mZoomSpriteWidth = 400;
    private float mZoomSpriteX = 200;
    private float mZoomSpriteY = 200;

    public ZoomLayerEntity(CameraSize cameraSize, Highlighter highlighter) {
        super();
        mCameraSize = cameraSize;
        mHighlighter = highlighter;
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    public void zoomOut() {
        if (mZoomSprite != null) {
            mZoomSprite.zoomOut();
        }
    }


    public void zoomToSprite(Sprite sprite, Object zoomData) {
        if (mZoomSprite != null && mZoomSprite.getZoomRatio() != 0) {
            mNextZoomSprite = sprite;
            mZoomSprite.zoomOut();
        } else {
            if (mZoomSprite != null) {
                detachChild(mZoomSprite);
            }

            mZoomData = zoomData;

            float[] center = new float[] {
                    mZoomSpriteX,
                    mZoomSpriteY,
            };

            Log.i(TAG, "Center coords: " + center[0] + ", " + center[1]);

            mZoomSprite = new ZoomSprite(center[0], center[1], 500, sprite);
            mZoomSprite.setOnZoomListener(this);
            attachChild(mZoomSprite);
            mHighlighter.addHighlight(mZoomSprite);

            mZoomSprite.zoomIn();
            sprite.setVisible(false);
        }
    }

    @Override
    public void onZoomRatioChange(ZoomSprite zoomSprite, float percentComplete, float zoomRatio) {
        if (zoomRatio == 0 && percentComplete == 1) {
            mZoomSprite.getTargetSprite().setVisible(true);
            mZoomSprite.detachSelf();
            mZoomSprite = null;

            if (mNextZoomSprite != null) {
                Sprite sprite = mNextZoomSprite;
                mNextZoomSprite = null;
                zoomToSprite(sprite, null);
            }
        }

        if (zoomRatio == 1 && percentComplete == 1) {
            if (mZoomListener != null) {
                mZoomListener.onZoomComplete(zoomSprite, mZoomData);
            }
        }
    }

    public void setHighlighter(Highlighter highlighter) {
        mHighlighter = highlighter;
    }

    public Highlighter getHighlighter() {
        return mHighlighter;
    }

    public float getZoomSpriteWidth() {
        return mZoomSpriteWidth;
    }

    public void setZoomSpriteWidth(float zoomSpriteWidth) {
        mZoomSpriteWidth = zoomSpriteWidth;
    }

    public float getZoomSpriteX() {
        return mZoomSpriteX;
    }

    public void setZoomSpriteX(float zoomSpriteX) {
        mZoomSpriteX = zoomSpriteX;
    }

    public float getZoomSpriteY() {
        return mZoomSpriteY;
    }

    public void setZoomSpriteY(float zoomSpriteY) {
        mZoomSpriteY = zoomSpriteY;
    }
}
