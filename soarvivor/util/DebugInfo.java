package soarvivor.util;

import soarvivor.entity.ExtendedPlayer;
import soarvivor.lib.config.Settings;
import net.minecraft.client.gui.FontRenderer;

/**
 * A class that describes the information structure used for debuging the mod
 * 
 * @author TehStoneMan
 * 
 */
public class DebugInfo
{
	public static int	wetLevel				= ExtendedPlayer.MAX_WET_LEVEL / 2;
	public static int	iceLevel				= 0;
	public static float	waterSaturationLevel	= 5.0F;
	public static int	waterTimer				= 0;
	public static int	prevWetLevel			= wetLevel;
	public static int	prevIceLevel			= iceLevel;
	public static int	envTimer				= 0;
	public static float	biomeTemperature		= 0f;
	public static float	heat					= 0f;
	public static float	distance				= 0f;

	public static float	foodExhuastion			= 0f;
	public static float	waterExhaustionLevel	= 0f;

	public static float	misc					= 0f;

	public DebugInfo()
	{}

	public static void init()
	{}

	public static void display(FontRenderer fontRender)
	{
		if (!Settings.debug) return;
		fontRender.drawStringWithShadow("envTimer : " + envTimer, 8, 8 * 1,
				0xFFFFFF);

		fontRender.drawStringWithShadow("foodExhuastion : " + foodExhuastion,
				8, 8 * 3, 0xFFFFFF);
		fontRender.drawStringWithShadow("waterExhaustionLevel : "
				+ waterExhaustionLevel, 8, 8 * 4, 0xFFFFFF);

		fontRender.drawStringWithShadow("misc : " + misc, 8, 8 * 6, 0xFFFFFF);

		fontRender.drawStringWithShadow("waterSaturationLevel : "
				+ waterSaturationLevel, 8, 8 * 8, 0xFFFFFF);
		/*
		 * fontRender.drawStringWithShadow("Temp : " + biomeTemperature, 8, 24,
		 * 0xFFFFFF);
		 * fontRender.drawStringWithShadow("heat : " + heat + " distance : "
		 * + distance, 8, 32, 0xFFFFFF);
		 * 
		 * fontRender.drawStringWithShadow("waterTimer : " + waterTimer, 8, 40,
		 * 0xFFFFFF);
		 * 
		 * fontRender.drawStringWithShadow("wetLevel : " + wetLevel, 8, 64,
		 * 0xFFFFFF);
		 * fontRender.drawStringWithShadow("iceLevel : " + iceLevel, 8, 72,
		 * 0xFFFFFF);
		 */

	}
}
