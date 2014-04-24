package com.bignerdranch.qwubble;

import android.util.Log;
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

    public QwubbleSprite(float x, float y, TextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
        super(x, y, textureRegion, vertexBufferObjectManager);
    }

    @Override
    public boolean onAreaTouched(TouchEvent sceneTouchEvent, float touchAreaLocalX, float touchAreaLocalY) {
        if (sceneTouchEvent.isActionDown()) {
            Debug.d("TOUCHED");
            //Display a dialog, overthrow universe

            Log.i(TAG, "sceneTouchEvent: " + sceneTouchEvent);

            return true;
        } else {
            Debug.d("TOUCHED..sorta. " + sceneTouchEvent);
            return false;
        }
    }
}
