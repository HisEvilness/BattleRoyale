package com.hotmail.AdrianSR.BattleRoyale.util.tps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 07 July, 2020 / 12:29 PM
 */
public class TPS {
	
	/**
	 * Gets current server ticks per second.
	 * <p>
	 * @return server current ticks per second.
	 */
	public static double getTicksPerSecond ( ) {
		try {
			final Class < ? > server_class = ClassReflection.getNmsClass ( "MinecraftServer" );
			final Method     server_getter = server_class.getMethod ( "getServer" );
			
			final Object  server = server_getter.invoke ( null );
			final double [ ] tps = (double [ ]) server.getClass ( ).getField ( "recentTps" ).get ( server );
			
			return tps [ 0 ];
		} catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException | ClassNotFoundException ex ) {
			ex.printStackTrace ( );
		}
		return 0D;
	}
}
