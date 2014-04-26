package com.bignerdranch.qwubble;

import android.graphics.Bitmap;
import android.util.Log;
import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.IQwubble;
import com.bignerdranch.qwubble.event.ShowAnswerEvent;
import com.bignerdranch.qwubble.event.ShowQwubbleEvent;
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
public class QwubbleSprite extends Sprite {
    private static final String TAG = "QwubbleSprite";

    private boolean mZoomed = false;
    private ZoomLayerEntity mZoomLayer;

    private IQwubble mQwubble;

    private ScaleModifier mScaleModifier;
    private Bitmap mBitmap;

    public QwubbleSprite(float x, float y, TextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, IQwubble qwubble) {
        super(x, y, textureRegion, vertexBufferObjectManager);
        mQwubble = qwubble;
    }

    @Override
    public boolean onAreaTouched(TouchEvent sceneTouchEvent, float touchAreaLocalX, float touchAreaLocalY) {
        if (sceneTouchEvent.isActionDown()) {
            Debug.d("TOUCHED");
            //Display a dialog, overthrow universe

            if (mQwubble instanceof AnswerData) {
                EventBus.getDefault().post(new ShowAnswerEvent(mQwubble, this));
            } else {
                EventBus.getDefault().post(new ShowQwubbleEvent(mQwubble, this));
            }
            Log.i(TAG, "sceneTouchEvent: " + sceneTouchEvent);
            return true;
        } else {
            Debug.d("TOUCHED..sorta. " + sceneTouchEvent);
            return false;
        }
    }

    public ZoomLayerEntity getZoomLayer() {
        return mZoomLayer;
    }

    public void setZoomLayer(ZoomLayerEntity zoomLayer) {
        mZoomLayer = zoomLayer;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
