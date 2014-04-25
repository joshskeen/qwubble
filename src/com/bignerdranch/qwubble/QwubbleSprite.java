package com.bignerdranch.qwubble;

import android.util.Log;
import com.bignerdranch.qwubble.data.QwubbleData;
import de.greenrobot.event.EventBus;
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
    private QwubbleZoomLayerEntity mZoomLayer;

    private QwubbleData mQwubble;

    private ScaleModifier mScaleModifier;

    public QwubbleSprite(float x, float y, TextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
        super(x, y, textureRegion, vertexBufferObjectManager);
        mQwubble = new QwubbleData();
    }

    @Override
    public boolean onAreaTouched(TouchEvent sceneTouchEvent, float touchAreaLocalX, float touchAreaLocalY) {
        if (sceneTouchEvent.isActionDown()) {
            Debug.d("TOUCHED");
            //Display a dialog, overthrow universe

            EventBus.getDefault().post(new ShowQwubbleEvent(mQwubble, this));


            Log.i(TAG, "sceneTouchEvent: " + sceneTouchEvent);
            return true;
        } else {
            Debug.d("TOUCHED..sorta. " + sceneTouchEvent);
            return false;
        }
    }

    public QwubbleZoomLayerEntity getZoomLayer() {
        return mZoomLayer;
    }

    public void setZoomLayer(QwubbleZoomLayerEntity zoomLayer) {
        mZoomLayer = zoomLayer;
    }
}
