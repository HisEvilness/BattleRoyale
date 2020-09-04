package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.minimap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;

public class AreaSelection {
	
	private static final Map<UUID, AreaSelection> SELECTIONS = new HashMap<UUID, AreaSelection>();
	
	public static AreaSelection getSafeSelection(UUID id) {
		if (SELECTIONS.get(id) == null) {
			SELECTIONS.put(id, new AreaSelection(id));
		}
		return SELECTIONS.get(id);
	}
	
	private final UUID    id;
	private Vector3i cornerA;
	private Vector3i cornerB;
	
	public AreaSelection(final UUID id) {
		this.id = id;
	}

	public Vector3i getCornerA() {
		return cornerA;
	}
	
	public Vector3i getCornerB() {
		return cornerB;
	}
	
	public Area getResult() {
		/* corners cannot be null */
		if (cornerA == null || cornerB == null) {
			return null;
		}
		return new Area(getCornerA(), getCornerB());
	}

	public void setCornerA(Vector3i cornerA) {
		this.cornerA = cornerA;
	}
	
	public void setCornerB(Vector3i cornerB) {
		this.cornerB = cornerB;
	}
	
	public boolean isDone() {
		return getCornerA() != null && getCornerB() != null;
	}
	
	public void end() {
		cornerA = null;
		cornerB = null;
		SELECTIONS.put(id, null);
	}
}