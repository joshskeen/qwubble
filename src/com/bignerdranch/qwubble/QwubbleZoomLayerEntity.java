package com.bignerdranch.qwubble;

import android.util.Log;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;

/**
 * Created by bphillips on 4/24/14.
 */
public class QwubbleZoomLayerEntity extends Entity implements ZoomSprite.OnZoomListener {
    private static final String TAG = "QwubbleZoomLayerEntity";
    private Sprite mNextZoomSprite;
    private ZoomSprite mZoomSprite;
    private CameraSize mCameraSize;
    private Highlighter mHighlighter;

    public QwubbleZoomLayerEntity(CameraSize cameraSize, Highlighter highlighter) {
        super();
        mCameraSize = cameraSize;
        mHighlighter = highlighter;
    }

    @Override
    public void onAttached() {
        super.onAttached();
    }

    public void zoomToSprite(Sprite sprite) {
        if (mZoomSprite != null && mZoomSprite.getZoomRatio() != 0) {
            mNextZoomSprite = sprite;
            mZoomSprite.zoomOut();
        } else {
            if (mZoomSprite != null) {
                detachChild(mZoomSprite);
            }

            float[] center = new float[] {
                    mCameraSize.getWidth() / 2,
                    mCameraSize.getHeight() / 2,
            };

            Log.i(TAG, "Center coords: " + center[0] + ", " + center[1]);

            mZoomSprite = new ZoomSprite(center[0], center[1], 4, sprite);
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
                zoomToSprite(sprite);
            }
        }
    }

    public void setHighlighter(Highlighter highlighter) {
        mHighlighter = highlighter;
    }

    public Highlighter getHighlighter() {
        return mHighlighter;
    }
}
