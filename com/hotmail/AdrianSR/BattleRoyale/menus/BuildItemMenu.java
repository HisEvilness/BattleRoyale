package com.hotmail.AdrianSR.BattleRoyale.menus;

import org.bukkit.entity.Player;

/**
 * Represents a buildable Item Menu.
 * 
 * @author AdrianSR
 */
public abstract class BuildItemMenu extends ItemMenu {

	/**
	 * Construct a new buildable Item Menu from a void.
	 * 
	 * @param name the Menu Title.
	 * @param size the Menu Size.
	 */
	public BuildItemMenu(String name, Size size) {
		// create.
		super(name, size);
		
		// build.
		buildItems();
	}

	/**
	 * The void to set the items.
	 */
	public abstract void buildItems();
	
	@Override
	public void open(final Player p) {
		// build.
		buildItems();
		
		// open.
		super.open(p);
		
		// update.
		this.update(p);
	}
}