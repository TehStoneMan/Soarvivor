package soarvivor.items;

import net.minecraft.item.Item;
import soarvivor.lib.config.Ids;
import soarvivor.lib.config.Names;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Items
{
	public static Item	quiver;

	public static void init()
	{
		quiver = new Quiver(Ids.quiver);
	}

	public static void addNames()
	{
		LanguageRegistry.addName(quiver, Names.quiver_Name);
	}
}
