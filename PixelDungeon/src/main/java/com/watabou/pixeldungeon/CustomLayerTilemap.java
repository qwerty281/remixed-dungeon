package com.watabou.pixeldungeon;

import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.CompositeImage;
import com.watabou.noosa.Image;
import com.watabou.noosa.MaskedTilemapScript;
import com.watabou.pixeldungeon.effects.CircleMask;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mike on 15.02.2018.
 * This file is part of Remixed Pixel Dungeon.
 */

public class CustomLayerTilemap extends DungeonTilemap {

    private ArrayList<CustomLayerTilemap> mLayers     = new ArrayList<>();
    private boolean                       transparent = false;

    private FloatBuffer mask;
    private float maskData[];

    public CustomLayerTilemap(Level level, String tiles, int[] map) {
        super(level, tiles);
        map(map, level.getWidth());

        mask = ByteBuffer.
                allocateDirect( size * 8 * Float.SIZE / 8 ).
                order( ByteOrder.nativeOrder() ).
                asFloatBuffer();

        maskData = new float[size * 8];
    }

    public void addLayer(String tiles, int[] map) {
        mLayers.add(new CustomLayerTilemap(level, tiles, map));
    }

    public void setAlpha(int layer, float alpha) {
        mLayers.get(layer).alpha(alpha);
    }

    @Override
    public Image tile(int pos) {
        ArrayList<Image> imgs = new ArrayList<>();

        if (data[pos] >= 0) {
            Image img = new Image(getTexture());
            img.frame(getTileset().get(data[pos]));
            imgs.add(img);
        }

        for (CustomLayerTilemap layer : mLayers) {
            if (layer.data[pos] >= 0) {
                Image img = new Image(getTexture());
                img.frame(getTileset().get(layer.data[pos]));
                imgs.add(img);
            }
        }
        if (!imgs.isEmpty()) {
            return new CompositeImage(imgs);
        }
        return null;
    }

    public void updateAll() {
        updated.set(0, 0, level.getWidth(), level.getHeight());

        for (CustomLayerTilemap layer : mLayers) {
            layer.updated.set(0, 0, level.getWidth(), level.getHeight());
        }
    }

    public void updateCell(int cell, Level level) {
        int x = level.cellX(cell);
        int y = level.cellY(cell);

        updated.union(x, y);
        for (CustomLayerTilemap layer : mLayers) {
            layer.updated.union(x, y);
        }
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        for (CustomLayerTilemap layer : mLayers) {
            layer.setTransparent(transparent);
        }
    }

    @Override
    public void brightness(float value) {
        super.brightness(value);

        for (CustomLayerTilemap layer : mLayers) {
            layer.brightness(value);
        }
    }

    @Override
    protected void updateVertices() {
        super.updateVertices();

        PointF hpos = Dungeon.hero.getHeroSprite().worldCoords();
        float hx = hpos.x;
        float hy = hpos.y;

        Arrays.fill(maskData,0);

        mask.position(0);

        for (int i=0; i < level.getHeight(); i++) {
            for (int j=0; j < level.getWidth(); j++) {

                int p = (i * level.getHeight() + j) * 8;

                maskData[p+0] = getTCoord(hx, j - 0.5f);
                maskData[p+1] = getTCoord(hy, i - 0.5f);

                maskData[p+2] = getTCoord(hx, j + 0.5f);
                maskData[p+3] = getTCoord(hy, i - 0.5f);

                maskData[p+4] = getTCoord(hx, j + 0.5f);
                maskData[p+5] = getTCoord(hy, i + 0.5f);

                maskData[p+6] = getTCoord(hx, j - 0.5f);
                maskData[p+7] = getTCoord(hy, i + 0.5f);
            }
        }

        mask.put(maskData);
    }

    private float getTCoord(float h, float i) {
        float d = (h - i)  /  (1f) ;

        d = Math.max(-2,Math.min(2,d));
        d = (d + 1) * 0.5f;
        return d;
    }

    @Override
    public void draw() {
        if (transparent) {
            updateMatrix();

            MaskedTilemapScript script = MaskedTilemapScript.get();
            script.resetCamera();

            Texture.activate(1);
            CircleMask.ensureTexture();
            TextureCache.get(CircleMask.class).bind();

            Texture.activate(0);
            getTexture().bind();

            script.uModel.valueM4(matrix);
            script.lighting(
                    rm, gm, bm, am,
                    ra, ga, ba, aa);

            updateVertices();

            script.camera(camera);
            script.drawQuadSet(quads, mask, size);

            for (CustomLayerTilemap layer : mLayers) {
                layer.updateVertices();
                script.drawQuadSet(layer.quads, mask, size);
                //layer.draw();
            }
        } else {

            super.draw();
            for (CustomLayerTilemap layer : mLayers) {
                layer.draw();
            }
        }

    }
}
