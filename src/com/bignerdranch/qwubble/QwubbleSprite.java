package com.bignerdranch.qwubble;

import android.util.Log;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityComparator;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

/**
* Created by bphillips on 4/24/14.
*/
class QwubbleSprite extends Sprite {
    private static final String TAG = "QwubbleSprite";

    private boolean mZoomed = false;

    private long mClickTime = 0;

    public long getClickTime() {
        return mClickTime;
    }

    private ScaleModifier mScaleModifier;

    public QwubbleSprite(float x, float y, TextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
        super(x, y, textureRegion, vertexBufferObjectManager);
    }

    @Override
    public boolean onAreaTouched(TouchEvent sceneTouchEvent, float touchAreaLocalX, float touchAreaLocalY) {
        if (sceneTouchEvent.isActionDown()) {
            Debug.d("TOUCHED");
            mClickTime = System.currentTimeMillis();

            //Display a dialog, overthrow universe

            Log.i(TAG, "sceneTouchEvent: " + sceneTouchEvent);

            toggleZoom();

            return true;
        } else {
            Debug.d("TOUCHED..sorta. " + sceneTouchEvent);
            return false;
        }
    }

    private void toggleZoom() {
        mZoomed = !mZoomed;

        float start = getScaleX(), end;

        if (mZoomed) {
            end = 2;
        } else {
            end = 1;
        }

        if (mScaleModifier != null) {
            unregisterEntityModifier(mScaleModifier);
        }

        mScaleModifier = new ScaleModifier(1, start, end);
        mScaleModifier.setAutoUnregisterWhenFinished(true);
        getParent().sortChildren(new IEntityComparator() {
            @Override
            public int compare(IEntity lhs, IEntity rhs) {
                if (lhs instanceof QwubbleSprite && rhs instanceof QwubbleSprite) {
                    Long lhsClickTime = ((QwubbleSprite)lhs).getClickTime();
                    Long rhsClickTime = ((QwubbleSprite)lhs).getClickTime();

                    return rhsClickTime.compareTo(lhsClickTime);
                }

                return 0;
            }
        });

        registerEntityModifier(mScaleModifier);
    }

}
