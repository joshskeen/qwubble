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

    public QwubbleZoomLayerEntity(CameraSize cameraSize) {
        super();
        mCameraSize = cameraSize;
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
                    mCameraSize.getWidth(),
                    mCameraSize.getHeight(),
            };

//            center[0] -= sprite.getWidth();
//            center[1] -= sprite.getHeight();

            Log.i(TAG, "Center coords: " + center[0] + ", " + center[1]);

            mZoomSprite = new ZoomSprite(center[0], center[1], 4, sprite);
            mZoomSprite.setOnZoomListener(this);
            attachChild(mZoomSprite);
            mZoomSprite.zoomIn();
        }
    }

    @Override
    public void onZoomRatioChange(ZoomSprite zoomSprite, float zoomRatio) {
        if (zoomRatio == 0 && mNextZoomSprite != null) {
            Sprite sprite = mNextZoomSprite;
            mNextZoomSprite = null;
            zoomToSprite(sprite);
        }
    }
}
