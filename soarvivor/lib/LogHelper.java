
package soarvivor.lib;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

public class LogHelper
{
	private static Logger	logger	= Logger.getLogger(ModInfo.ID);

	public static void init()
	{
		logger.setParent(FMLLog.getLogger());
	}

	public static void log(Level logLevel, String message)
	{
		logger.log(logLevel, message);
	}
}
