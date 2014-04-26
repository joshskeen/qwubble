package com.bignerdranch.qwubble;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseBounceOut;

public class ZoomSprite extends Sprite {
    public OnZoomListener getOnZoomListener() {
        return mOnZoomListener;
    }

    public void setOnZoomListener(OnZoomListener onZoomListener) {
        mOnZoomListener = onZoomListener;
    }

    public interface OnZoomListener {
        public void onZoomRatioChange(ZoomSprite zoomSprite, float percentComplete, float zoomRatio);
    }

    private final float mZoomX;
    private final float mZoomY;
    private float mZoomRatio = 0;
    private float mZoomScale;

    public Sprite getTargetSprite() {
        return mTargetSprite;
    }

    private final Sprite mTargetSprite;
    private OnZoomListener mOnZoomListener;

    IEntityModifier mZoomModifier;

    public ZoomSprite(float zoomX, float zoomY, float zoomWidth, Sprite targetSprite) {
        super(targetSprite.getX(), targetSprite.getY(), targetSprite.getTextureRegion(), targetSprite.getVertexBufferObjectManager());
        mZoomX = zoomX;
        mZoomY = zoomY;
        mZoomScale = zoomWidth / targetSprite.getWidth();
        mTargetSprite = targetSprite;
        setRotation(targetSprite.getRotation());
    }

    public void zoomIn() {
        zoomTo(1);
    }

    public void zoomOut() {
        zoomTo(0);
    }

    private void zoomTo(float zoom) {
        if (mZoomModifier != null) {
            unregisterEntityModifier(mZoomModifier);
        }

        mZoomModifier = new SingleValueSpanEntityModifier(0.6f, mZoomRatio, zoom, EaseBounceOut.getInstance()) {

            @Override
            protected void onSetInitialValue(IEntity pItem, float pValue) {
                setZoomRatio(pValue);
            }

            @Override
            protected void onSetValue(IEntity pItem, float pPercentageDone, float pValue) {
                setZoomRatio(pValue);
                if (mOnZoomListener != null) {
                    mOnZoomListener.onZoomRatioChange(ZoomSprite.this, pPercentageDone, mZoomRatio);
                }

            }

            @Override
            public IEntityModifier deepCopy() throws DeepCopyNotSupportedException {
                throw new DeepCopyNotSupportedException();
            }
        };

        mZoomModifier.setAutoUnregisterWhenFinished(true);
        registerEntityModifier(mZoomModifier);
    }

    public float getZoomRatio() {
        return mZoomRatio;
    }

    public void setZoomRatio(float zoomRatio) {
        mZoomRatio = zoomRatio;
        updateZoomPosition();
    }

    private void updateZoomPosition() {
        setX(scale(mTargetSprite.getX(), mZoomX, mZoomRatio));
        setY(scale(mTargetSprite.getY(), mZoomY, mZoomRatio));
        setRotation(scale(mTargetSprite.getRotation(), 720 * 2, mZoomRatio));
        setScale(scale(1, mZoomScale, mZoomRatio));
    }

    private static float scale(float start, float end, float proportion) {
        float d = end - start;

        return start + d * proportion;
    }
}
