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

import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.Dungeon;

public class BusyIndicator extends Image {
	
	public BusyIndicator() {
		super();
		copy( Icons.BUSY.get() );

		setOrigin( width / 2, height / 2 );
		angularSpeed = 720;
	}
	
	@Override
	public void update() {
		super.update();
		setVisible(Dungeon.hero.isAlive() && !Dungeon.hero.isReady());
	}
}
