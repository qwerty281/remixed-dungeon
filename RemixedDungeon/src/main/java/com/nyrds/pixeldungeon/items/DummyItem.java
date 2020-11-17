package com.nyrds.pixeldungeon.items;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Belongings;
import com.watabou.pixeldungeon.items.EquipableItem;

import org.jetbrains.annotations.NotNull;

public class DummyItem extends EquipableItem {
    @Override
    public Belongings.Slot slot(Belongings belongings) {
        return Belongings.Slot.NONE;
    }

    @Override
    public float impactDelayFactor(Char user, float delayFactor) {
        return delayFactor;
    }

    @Override
    public float impactAccuracyFactor(Char user, float accuracyFactor) {
        return accuracyFactor;
    }

    @Override
    public boolean dontPack() {
        return true;
    }

    @Override
    protected boolean doUnequip(Char hero, boolean collect, boolean single) {
        return true;
    }

    @Override
    public boolean goodForMelee() {
        return false;
    }

    @Override
    public void setCursed(boolean cursed) {
    }

    @Override
    public String getVisualName() {
        return "none";
    }

    @Override
    public void doDrop(@NotNull Char hero) {
    }

    @Override
    public int requiredSTR() {
        return 0;
    }

    @Override
    public int quantity() {
        return 0;
    }

    @Override
    public boolean valid() {
        return false;
    }
}
