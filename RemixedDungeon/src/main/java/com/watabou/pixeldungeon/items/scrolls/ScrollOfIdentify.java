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
package com.watabou.pixeldungeon.items.scrolls;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.util.StringsManager;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.effects.Identification;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.pixeldungeon.windows.WndBag;

import org.jetbrains.annotations.NotNull;

public class ScrollOfIdentify extends InventoryScroll {

	{
        inventoryTitle = StringsManager.getVar(R.string.ScrollOfIdentify_InvTitle);
		mode = WndBag.Mode.UNIDENTIFED;
	}

	static public void identify(@NotNull Char ch, @NotNull Item item) {
		GameScene.addToMobLayer( new Identification( ch.getSprite().center().offset( 0, -16 ) ) );

		item.identify();
        GLog.i(Utils.format(R.string.ScrollOfIdentify_Info1, item));

		Badges.validateItemLevelAcquired( item );
	}

	@Override
	protected void onItemSelected(Item item, Char selector) {
		identify(selector,item);
	}

	@Override
	public int price() {
		return isKnown() ? 30 * quantity() : super.price();
	}
}
