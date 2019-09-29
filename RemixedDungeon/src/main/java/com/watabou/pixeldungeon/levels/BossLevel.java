package com.watabou.pixeldungeon.levels;

import com.nyrds.Packable;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

public abstract class BossLevel extends RegularLevel {

    protected static final String DOOR	    = "door";
    protected static final String ENTERED	= "entered";


    protected int arenaDoor = INVALID_CELL;

    protected boolean enteredArena = false;

    @Packable
    protected int stairs = INVALID_CELL;

    protected boolean[] water() {
        return Patch.generate(this, 0.45f, 5 );
    }
    protected boolean[] grass() {
        return Patch.generate(this, 0.30f, 4 );
    }

    @Override
    public void seal() {
        if (entrance != 0) {

            set( entrance, Terrain.WATER_TILES );
            GameScene.updateMap( entrance );
            GameScene.ripple( entrance );

            stairs = entrance;
            entrance = 0;
        }
    }

    public void unseal() {
        if (cellValid(stairs)) {

            entrance = stairs;
            stairs = INVALID_CELL;

            set( entrance, Terrain.ENTRANCE );
            GameScene.updateMap( entrance );
        }

        if(cellValid(arenaDoor)) {
            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10);

            set(arenaDoor, Terrain.EMPTY_DECO);
            GameScene.updateMap(arenaDoor);
        }

        Dungeon.observe();
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( DOOR, arenaDoor );
        bundle.put( ENTERED, enteredArena );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        arenaDoor = bundle.getInt( DOOR );
        enteredArena = bundle.getBoolean( ENTERED );
    }

    @Override
    public boolean isBossLevel() {
        return true;
    }
}
