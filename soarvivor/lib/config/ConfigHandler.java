package soarvivor.lib.config;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class ConfigHandler
{
	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);

		config.load();
		
		// Block IDs

		// General settings
		Settings.limitStackSize = config.get(config.CATEGORY_GENERAL, "limitStackSize", 16).getInt();

		// Item IDs
		Ids.quiver = config.getItem(config.CATEGORY_ITEM, Names.quiver_Name, 17000).getInt() - 256;

		config.save();
	}
}
