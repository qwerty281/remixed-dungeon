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
package com.watabou.pixeldungeon.levels.painters;

import com.nyrds.pixeldungeon.items.Treasury;
import com.nyrds.pixeldungeon.levels.objects.LevelObjectsFactory;
import com.nyrds.pixeldungeon.utils.ItemsList;
import com.watabou.pixeldungeon.items.Codex;
import com.watabou.pixeldungeon.items.Heap;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.keys.IronKey;
import com.watabou.pixeldungeon.items.scrolls.BlankScroll;
import com.watabou.pixeldungeon.items.scrolls.Scroll;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Room;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class LibraryPainter extends Painter {

	public static void paint(Level level, Room room) {

		fill(level, room, Terrain.WALL);
		fill(level, room, 1, Terrain.EMPTY);

		Room.Door entrance = room.entrance();
		Point a = null;
		Point b = null;

		if (entrance.x == room.left) {
			a = new Point(room.left + 1, entrance.y - 1);
			b = new Point(room.left + 1, entrance.y + 1);
			fill(level, room.right - 1, room.top + 1, 1, room.height() - 1,
					Terrain.BOOKSHELF);
		} else if (entrance.x == room.right) {
			a = new Point(room.right - 1, entrance.y - 1);
			b = new Point(room.right - 1, entrance.y + 1);
			fill(level, room.left + 1, room.top + 1, 1, room.height() - 1,
					Terrain.BOOKSHELF);
		} else if (entrance.y == room.top) {
			a = new Point(entrance.x + 1, room.top + 1);
			b = new Point(entrance.x - 1, room.top + 1);
			fill(level, room.left + 1, room.bottom - 1, room.width() - 1, 1,
					Terrain.BOOKSHELF);
		} else if (entrance.y == room.bottom) {
			a = new Point(entrance.x + 1, room.bottom - 1);
			b = new Point(entrance.x - 1, room.bottom - 1);
			fill(level, room.left + 1, room.top + 1, room.width() - 1, 1,
					Terrain.BOOKSHELF);
		}
		if (a != null) {
			final int cell = level.cell(a.x, a.y);
			if (level.map[cell] == Terrain.EMPTY) {
				level.putLevelObject( LevelObjectsFactory.STATUE, cell);
			}
		}
		if (b != null) {
			final int cell = level.cell(b.x, b.y);
			if (level.map[cell] == Terrain.EMPTY) {
				level.putLevelObject( LevelObjectsFactory.STATUE, cell);
			}
		}

		int n = Random.IntRange(2, 3);
		for (int i = 0; i < n; i++) {
			int pos;
			do {
				pos = room.random(level);
			} while (!level.isCellSafeForPrize(pos));
			level.drop(prize(level), pos, Heap.Type.HEAP);

			if (Random.Int(4) == 0) {
				level.drop(new BlankScroll(), pos, Heap.Type.HEAP);
			} else {
				if (Random.Int(4) == 0) {
					level.drop(new Codex(), pos, Heap.Type.HEAP);
				}
			}
		}

		entrance.set(Room.Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey());
	}

	private static Item prize(Level level) {

		Item prize = level.itemToSpanAsPrize();
		if (prize instanceof Scroll) {
			return prize;
		} else if (prize != ItemsList.DUMMY) {
			level.addItemToSpawn(prize);
		}

		return Treasury.getLevelTreasury().random(Treasury.Category.SCROLL);
	}
}
