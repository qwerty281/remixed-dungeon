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
package com.watabou.pixeldungeon.items.armor.glyphs;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.util.StringsManager;
import com.watabou.noosa.Camera;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.buffs.Roots;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.particles.EarthParticle;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.armor.Armor.Glyph;
import com.watabou.pixeldungeon.plants.Earthroot;
import com.watabou.pixeldungeon.sprites.Glowing;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Random;

public class Entanglement extends Glyph {
	
	private static Glowing GREEN = new Glowing( 0x448822 );
	
	@Override
	public int defenceProc(Armor armor, Char attacker, Char defender, int damage ) {

		int level = Math.max( 0, armor.level() );
		
		if (Random.Int( 4 ) == 0) {
			
			Buff.prolong( defender, Roots.class, 5 - level / 5 );
			Buff.affect( defender, Earthroot.Armor.class ).level( 5 * (level + 1) );
			CellEmitter.bottom( defender.getPos() ).start( EarthParticle.FACTORY, 0.05f, 8 );
			Camera.main.shake( 1, 0.4f );
			
		}

		return damage;
	}
	
	@Override
	public String name( String weaponName) {
        return Utils.format(R.string.Entanglement_Txt, weaponName );
	}

	@Override
	public Glowing glowing() {
		return GREEN;
	}
		
}
