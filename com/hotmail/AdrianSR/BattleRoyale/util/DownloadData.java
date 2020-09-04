package com.hotmail.AdrianSR.BattleRoyale.util;

import java.lang.reflect.Field;

public enum DownloadData {
	
	A, B, C;

	@SuppressWarnings("unused") private static final String A_DATA = "%%__USER__%%";
	@SuppressWarnings("unused") private static final String B_DATA = "%%__RESOURCE__%%";
	@SuppressWarnings("unused") private static final String C_DATA = "%%__NONCE__%%";
	                            private static final String    KEY = "_DATA";
	DownloadData() {
		/* empty constructor */
	}
	
	public String getData() {
		try {
			return (String) reflection().get(getClass());
		} catch (NoSuchFieldException | SecurityException 
				| IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Field reflection() throws NoSuchFieldException, SecurityException {
		Field reflection = getClass().getDeclaredField( ( toString() + KEY ) ); reflection.setAccessible(true);
		return reflection;
	}
}