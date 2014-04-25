package com.bignerdranch.qwubble;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by bphillips on 4/25/14.
 */
public class HighlightSprite extends Sprite {

    public HighlightSprite(Sprite highlightTarget, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
        super(0, 0, textureRegion, vertexBufferObjectManager);

        setWidth(highlightTarget.getWidth());
        setHeight(highlightTarget.getHeight());

    }

}
