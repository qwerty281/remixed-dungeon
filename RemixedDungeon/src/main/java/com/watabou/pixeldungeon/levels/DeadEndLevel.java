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
package com.watabou.pixeldungeon.levels;

import com.nyrds.pixeldungeon.levels.LevelTools;
import com.nyrds.pixeldungeon.levels.objects.Sign;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;

import java.util.Arrays;

public class DeadEndLevel extends Level {

	private static final int SIZE = 5;
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public void create(int w, int h) {
		super.create(32, 32);
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}
	
	@Override
	protected boolean build() {

		Arrays.fill( map, Terrain.WALL );
		
		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * getWidth() + j] = Terrain.EMPTY;
			}
		}
		
		for (int i=1; i <= SIZE; i++) {
			map[getWidth() + i] = 
			map[getWidth() * SIZE + i] =
			map[getWidth() * i + 1] =
			map[getWidth() * i + SIZE] =
				Terrain.WATER;
		}
		
		entrance = SIZE * getWidth() + SIZE / 2 + 1;
		//map[entrance] = Terrain.ENTRANCE;

		setExit(-1,0);

		addLevelObject(new Sign((SIZE / 2 + 1) * (getWidth() + 1), Dungeon.tip(this)));
		
		return true;
	}

	@Override
	protected void decorate() {
		LevelTools.northWallDecorate(this, 10, 8);
	}

	@Override
	protected void createMobs() {
	}

	@Override
	protected void createItems() {
	}
	
	@Override
	public int randomRespawnCell() {
		return -1;
	}


	@Override
	public void activateScripts() {
		super.activateScripts();

		if(cellValid(entrance)) {
			entrance = INVALID_CELL;
		}
	}
}
