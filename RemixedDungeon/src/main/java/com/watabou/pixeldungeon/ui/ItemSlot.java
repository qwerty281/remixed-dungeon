/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.ui;

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.items.EquipableItem;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.PseudoItem;
import com.watabou.pixeldungeon.items.rings.Artifact;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.pixeldungeon.utils.Utils;

public class ItemSlot extends Button {

    public static final int DEGRADED = 0xFF4444;
    public static final int UPGRADED = 0x44FF44;
    public static final int WARNING  = 0xFF8800;

    private static final float ENABLED  = 1.0f;
    private static final float DISABLED = 0.3f;

    protected ItemSprite icon;
    protected Emitter    emitter;

    private BitmapText topLeft;
    private BitmapText topRight;
    private BitmapText bottomRight;

    private boolean inQuickSlot;

    private static final String TXT_STRENGTH    = ":%d";
    private static final String TXT_TYPICAL_STR = "%d?";

    private static final String TXT_LEVEL = "%+d";

    // Special items for containers
    public static final Item CHEST        = new PseudoItem() {
        public int image() {
            return ItemSpriteSheet.CHEST;
        }
    };
    public static final Item LOCKED_CHEST = new PseudoItem() {
        public int image() {
            return ItemSpriteSheet.LOCKED_CHEST;
        }
    };
    public static final Item TOMB         = new PseudoItem() {
        public int image() {
            return ItemSpriteSheet.TOMB;
        }
    };
    public static final Item SKELETON     = new PseudoItem() {
        public int image() {
            return ItemSpriteSheet.BONES;
        }
    };

    public ItemSlot() {
        super();
    }

    public ItemSlot(Item item) {
        this();
        item(item);
    }

    @Override
    protected void createChildren() {

        super.createChildren();

        icon = new ItemSprite();
        add(icon);

        emitter = new Emitter();
        add(emitter);

        topLeft = new BitmapText(PixelScene.font1x);
        topLeft.setScale(0.8f, 0.8f);
        add(topLeft);

        topRight = new BitmapText(PixelScene.font1x);
        topRight.setScale(0.8f, 0.8f);
        add(topRight);

        bottomRight = new BitmapText(PixelScene.font1x);
        bottomRight.setScale(0.8f, 0.8f);
        add(bottomRight);
    }

    @Override
    protected void layout() {
        super.layout();

        icon.setX(x + (width - icon.width) / 2);
        icon.setY(y + (height - icon.height) / 2);

        emitter.pos(icon);

        if (topLeft != null) {
            topLeft.setX(x);
            topLeft.setY(y);
        }

        if (topRight != null) {
            topRight.setX(x + (width - topRight.width()));
            topRight.setY(y);
        }

        if (bottomRight != null) {
            bottomRight.setX(x + (width - bottomRight.width()));
            bottomRight.setY(y + (height - bottomRight.height()));
        }
    }

    public void item(Item item) {
        if (item == null) {
            setActive(false);

            icon.setVisible(false);
            emitter.setVisible(false);
            emitter.on = false;
            topLeft.setVisible(false);
            topRight.setVisible(false);
            bottomRight.setVisible(false);
            return;
        }

        setActive(true);
        icon.setVisible(true);
        topLeft.setVisible(true);
        topRight.setVisible(true);
        bottomRight.setVisible(true);

        icon.view(item);

        if (item.emitter() != null) {
            emitter.setVisible(true);
            emitter.pour(item.emitter(), item.emitterInterval());
        } else {
            emitter.setVisible(false);
            emitter.on = false;
        }

        topLeft.text(item.status());

        if(item instanceof EquipableItem) {

            EquipableItem eitem = (EquipableItem)item;
            int typicalStr = eitem.typicalSTR();

            if(typicalStr>0 && !inQuickSlot) {
                if (item.isLevelKnown()) {

                    int str = eitem.requiredSTR();
                    topRight.text(Utils.format(TXT_STRENGTH, str));
                    if (str > Dungeon.hero.effectiveSTR()) {
                        topRight.hardlight(DEGRADED);
                    } else {
                        topRight.resetColor();
                    }
                } else {
                    topRight.text(Utils.format(TXT_TYPICAL_STR, typicalStr));
                    topRight.hardlight(WARNING);
                }
            } else {
                topRight.text(Utils.EMPTY_STRING);
            }
        }

        int level = item.visiblyUpgraded();

        if (level != 0) {
            bottomRight.text(Utils.format(TXT_LEVEL, level));
            bottomRight.hardlight(level > 0 ? UPGRADED : DEGRADED);
        } else {
            bottomRight.text(Utils.EMPTY_STRING);
        }

        if (item instanceof Artifact) {
            Artifact artifact = (Artifact) item;
            String text = artifact.getText();

            if (text != null) {
                topLeft.text(artifact.getText());
                topLeft.hardlight(artifact.getColor());
                topLeft.setVisible(true);
            }
        }

        layout();
    }

    public void enable(boolean value) {

        setActive(value);

        float alpha = value ? ENABLED : DISABLED;
        icon.alpha(alpha);
        topLeft.alpha(alpha);
        topRight.alpha(alpha);
        bottomRight.alpha(alpha);
    }

    public void showParams(boolean value) {
        if (value) {
            add(topRight);
            add(bottomRight);
        } else {
            remove(topRight);
            remove(bottomRight);
        }
    }

    public void setInQuickSlot(boolean inQuickSlot) {
        this.inQuickSlot = inQuickSlot;
    }
}
