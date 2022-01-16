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
package com.watabou.pixeldungeon.effects;

import com.nyrds.pixeldungeon.game.GameLoop;
import com.nyrds.platform.gl.Gl;
import com.watabou.pixeldungeon.sprites.CharSprite;

public class TorchHalo extends Halo {

	private CharSprite target;
	
	private float phase = 0;
	
	public TorchHalo( CharSprite sprite ) {
		super( 24, 0xFFDDCC, 0.15f );
		target = sprite;
		am = 0;
	}
	
	@Override
	public void update() {
		super.update();

		final float elapsed = GameLoop.elapsed;
		if (phase < 0) {
			if ((phase += elapsed) >= 0) {
				killAndErase();
			} else {
				scale.set( (2 + phase) * radius / RADIUS );
				am = -phase * brightness;
			}
		} else if (phase < 1) {
			if ((phase += elapsed) >= 1) {
				phase = 1;
			}
			scale.set( phase * radius / RADIUS );
			am = phase * brightness;
		}
		
		point( target.x + target.width / 2, target.y + target.height / 2 );
	}
	
	@Override
	public void draw() {
		Gl.blendSrcAlphaOne();
		super.draw();
		Gl.blendSrcAlphaOneMinusAlpha();
	}
	
	public void putOut() {
		phase = -1;
	}
}
