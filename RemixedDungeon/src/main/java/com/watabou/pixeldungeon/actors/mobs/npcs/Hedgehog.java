package com.watabou.pixeldungeon.actors.mobs.npcs;

import com.nyrds.Packable;
import com.nyrds.pixeldungeon.ai.MobAi;
import com.nyrds.pixeldungeon.ai.Wandering;
import com.nyrds.pixeldungeon.mechanics.NamedEntityKind;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.food.Pasty;
import com.watabou.pixeldungeon.levels.RegularLevel;

public class Hedgehog extends NPC {

	{
		setState(MobAi.getStateByClass(Wandering.class));
	}
	
	@Override
	public float speed() {
		return speed;
	}

	@Override
	public void damage(int dmg, NamedEntityKind src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	private static boolean spawned;

	@Packable
	private int    action = 0;
	@Packable
	private float  speed  = 0.5f;

	public static void spawn( RegularLevel level ) {
		if (!spawned && Dungeon.depth == 23) {
			Hedgehog hedgehog = new Hedgehog();
			do {
				hedgehog.setPos(level.randomRespawnCell());
			} while (hedgehog.getPos() == -1);
			level.mobs.add( hedgehog );
			Actor.occupyCell( hedgehog );
			
			spawned = true;
		}
	}

	@Override
	public boolean interact(final Hero hero) {
		getSprite().turnTo( getPos(), hero.getPos() );
		
		switch (action)
		{
			case 0:
				say(Game.getVar(R.string.Hedgehog_Info1));
			break;
		
			case 1:
				say(Game.getVar(R.string.Hedgehog_Info2));
			break;
			
			case 2:
				say(Game.getVar(R.string.Hedgehog_Info3));
			break;
			
			case 3:
				say(Game.getVar(R.string.Hedgehog_Info4));
				
				Pasty pie = new Pasty();
				
				level().drop( pie, getPos() ).sprite.drop();
			break;
			
			default:
				say(Game.getVar(R.string.Hedgehog_ImLate));
				action = 4;
				speed  = 3;
		}
		speed += 0.5f;
		action++;
		
		return true;
	}

}
