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
package com.watabou.pixeldungeon.sprites;

import com.nyrds.pixeldungeon.ai.Sleeping;
import com.watabou.noosa.Animation;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.scenes.GameScene;

public class MobSprite extends CharSprite {

	private static final float FADE_TIME	= 3f;

	@Override
	public void update() {
		ch.ifPresent( chr -> {
			if (chr instanceof Mob) {
				Mob mob = (Mob) chr;
				sleeping = mob.getState() instanceof Sleeping;
				controlled = mob.isPet();
			}
		});
		super.update();
	}
	
	@Override
	public void onComplete( Animation anim ) {
		
		super.onComplete( anim );
		
		if (anim == die) {
			GameScene.addToMobLayer(new AlphaTweener(this, 0, FADE_TIME) {
					@Override
					protected void onComplete() {
						MobSprite.this.killAndErase();
					}
				});
		}
	}

}
