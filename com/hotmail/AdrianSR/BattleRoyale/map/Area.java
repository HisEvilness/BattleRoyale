package com.hotmail.AdrianSR.BattleRoyale.map;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Objects;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class Area implements Cloneable {

	private Vector3i            min, max;
	private Rectangle2D.Double rectangle;

	public Area ( Vector3i min , Vector3i max ) {
		this.min = min;
		this.max = max;
		recalculate();
	}

	public Vector3i getMin() {
		return min;
	}

	public void setMin(Vector3i min) {
		this.min = min;
		recalculate();
	}

	public Vector3i getMax() {
		return max;
	}

	public void setMax(Vector3i max) {
		this.max = max;
		recalculate();
	}

	public Vector3i getMaxMin() {
		return new Vector3i(max.getX(), max.getY(), min.getZ());
	}

	public Vector3i getMinMax() {
		return new Vector3i(min.getX(), min.getY(), max.getZ());
	}

	public int getXSize() {
		return (max.getX() - min.getX()) + 1;
	}

	public int getYSize() {
		return (max.getY() - min.getY()) + 1;
	}

	public int getZSize() {
		return (max.getZ() - min.getZ()) + 1;
	}
	
	public int getRadius() {
		return Math.abs(getXSize()) + Math.abs(getZSize());
	}
	
	public void setRadius(int radius) {
		final int add = Math.abs(radius); // radius / 2;
		
		setMax(new Vector3i((getCenter().getX() + add), getCenter().getY(), (getCenter().getZ() + add)));
		
		setMin(new Vector3i((getCenter().getX() - add), getCenter().getY(), (getCenter().getZ() - add)));
	}

	private void recalculate() {
		Vector3i min = this.min, max = this.max;
		this.min = new Vector3i(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()));
		this.max = new Vector3i(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()));
		this.rectangle = new Double(min.getX(), min.getZ(), max.getX() - min.getX(), max.getZ() - min.getZ());
	}

	public Vector3d getCenter() {
		return min.add(max).toDouble().div(2);
	}

	public boolean isSquared ( ) {
		return getXSize ( ) == getZSize ( );
	}
	
	public boolean isInArea(Vector3d vector3d) {
		return vector3d.getX() <= max.getX() && vector3d.getX() >= min.getX() && vector3d.getY() <= max.getY() && vector3d.getY() >= min.getY() &&
			vector3d.getZ() <= max.getZ() && vector3d.getZ() >= min.getZ();
	}

	public boolean isInAreaNoY(Vector3d vector3d) {
		return vector3d.getX() <= max.getX() && vector3d.getX() >= min.getX() && vector3d.getZ() <= max.getZ() && vector3d.getZ() >= min.getZ();
	}

	public boolean isInArea(Vector3i vector3i) {
		return vector3i.getX() <= max.getX() && vector3i.getX() >= min.getX() && vector3i.getY() <= max.getY() && vector3i.getY() >= min.getY() &&
			vector3i.getZ() <= max.getZ() && vector3i.getZ() >= min.getZ();
	}

	public boolean isInAreaNoY(Vector3i vector3i) {
		return vector3i.getX() <= max.getX() && vector3i.getX() >= min.getX() && vector3i.getZ() <= max.getZ() && vector3i.getZ() >= min.getZ();
	}

	public boolean intersectsVector(Vector3d direction, Vector3d origin) {
		return rectangle.intersectsLine(
			direction.getX() + origin.getX(), direction.getZ() + origin.getZ(), (direction.getX() * 10000) + origin.getX(), (direction.getZ() * 10000) + origin.getZ());
	}

	public Area getSquared ( ) {
		boolean xBigger = getXSize() > getZSize();
		int dif = Math.abs(Math.subtractExact(getXSize(), getZSize()));
		int add1 = -(dif % 2 == 0 ? dif / 2 : (dif + 1) / 2);
		int add2 = dif % 2 == 0 ? dif / 2 : (dif - 1) / 2;
		return new Area(new Vector3i(getMin().getX() + (xBigger ? 0 : add1), 0, getMin().getZ() + (xBigger ? add1 : 0)), new Vector3i(
			getMax().getX() + (xBigger ? 0 : add2), 255, getMax().getZ() + (xBigger ? add2 : 0)));
	}
	
    @Override
    public Area clone() {
        try {
            return (Area) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

	@Override
	public int hashCode ( ) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		return result;
	}

	@Override
	public boolean equals ( Object obj ) {
		if ( obj instanceof Area ) {
			Area other = (Area) obj;
			return Objects.equals ( other.max , max ) && Objects.equals ( other.min , min );
		} else {
			return false;
		}
	}
}