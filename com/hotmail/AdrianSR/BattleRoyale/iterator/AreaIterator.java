package com.hotmail.AdrianSR.BattleRoyale.iterator;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;

/**
 * Represents a Area Iterator.
 * <p>
 * @author AdrianSR
 */
public class AreaIterator implements ListIterator<Location> {
	
	/**
	 * Class values.
	 */
    private final List<Location> points;
    private       int nextIndex;
    private       Boolean lastDirection;
    
    /**
     * Construct a new Area Iterator.
     * <p>
     * @param points the points list.
     */
    public AreaIterator(final List<Location> points) {
    	this.points    = points;
    	this.nextIndex = 0;
    }
    
	@Override
	public boolean hasNext() {
		return nextIndex < points.size();
	}

	@Override
	public Location next() {
		lastDirection = Boolean.TRUE;
		return points.get(nextIndex ++);
	}
	
	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex > 0;
	}

	@Override
	public Location previous() {
		lastDirection = false;
		return points.get(-- nextIndex);
	}

	@Override
	public int previousIndex() {
		return nextIndex - 1;
	}
	
	@Override
	public void set(Location location) {
		// check last direction
        if (lastDirection == null) {
            throw new IllegalStateException("No current location!");
        }
        
        // set location
        int i = lastDirection ? nextIndex - 1 : nextIndex;
        points.set(i, location);
	}
	
	@Override
	public void add(Location location) {
		throw new UnsupportedOperationException("Cannot change the size of an point list!");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot change the size of an point list!");
	}
}
