package com.bignerdranch.qwubble;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityComparator;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.IEntityParameterCallable;
import org.andengine.entity.modifier.DoubleValueChangeEntityModifier;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.transformation.Transformation;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBounceOut;

import java.util.ArrayList;
import java.util.List;

public class ZoomSprite extends Sprite {
    public OnZoomListener getOnZoomListener() {
        return mOnZoomListener;
    }

    public void setOnZoomListener(OnZoomListener onZoomListener) {
        mOnZoomListener = onZoomListener;
    }

    public interface OnZoomListener {
        public void onZoomRatioChange(ZoomSprite zoomSprite, float zoomRatio);
    }

    private final float mZoomX;
    private final float mZoomY;
    private float mZoomRatio = 0;
    private float mZoomScale;
    private Sprite mTargetSprite;
    private OnZoomListener mOnZoomListener;

    IEntityModifier mZoomModifier;

    public ZoomSprite(float zoomX, float zoomY, float zoomScale, Sprite targetSprite) {
        super(targetSprite.getX(), targetSprite.getY(), targetSprite.getTextureRegion(), targetSprite.getVertexBufferObjectManager());
        mZoomX = zoomX;
        mZoomY = zoomY;
        mZoomScale = zoomScale;
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

        if (mOnZoomListener != null) {
            mOnZoomListener.onZoomRatioChange(this, mZoomRatio);
        }
    }

    private void updateZoomPosition() {
        setX(scale(mTargetSprite.getX(), mZoomX, mZoomRatio));
        setY(scale(mTargetSprite.getY(), mZoomY, mZoomRatio));
        setRotation(scale(mTargetSprite.getRotation(), 0, mZoomRatio));
        setScale(scale(1, mZoomScale, mZoomRatio));
    }

    private static float scale(float start, float end, float proportion) {
        float d = end - start;

        return start + d * proportion;
    }
}
