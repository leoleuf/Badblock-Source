package fr.badblock.game.core18R3.worldedit.iterators;

import fr.badblock.gameapi.utils.selections.CuboidSelection;

public class WEEmptyCuboidIterator extends WEAbstractCuboidIterator {
	public WEEmptyCuboidIterator(CuboidSelection selection) {
		super(selection);
	}

	@Override
	protected boolean accept(int[] coords) {
		return coords[0] == minX || coords[0] == maxX || coords[2] == minZ || coords[2] == maxZ;
	}
}
