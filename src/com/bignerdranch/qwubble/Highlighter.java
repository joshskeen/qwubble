package com.bignerdranch.qwubble;

import android.content.Context;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import java.io.IOException;
import java.io.InputStream;

public class Highlighter {
    private ITextureRegion mSpriteTextureRegion = null;

    public Highlighter(final Context context, TextureManager textureManager) {
        ITexture texture = null;
        try {
            texture = new BitmapTexture(textureManager, new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {
                    return context.getAssets().open("gfx/bubble.png");
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        texture.load();

        mSpriteTextureRegion = TextureRegionFactory.extractFromTexture(texture);
    }

    public void addHighlight(Sprite sprite) {
        Sprite highlightSprite = new HighlightSprite(sprite, mSpriteTextureRegion, sprite.getVertexBufferObjectManager());
        sprite.attachChild(highlightSprite);
    }
}
