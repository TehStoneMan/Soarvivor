package soarvivor.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipies
{
	public static final int WILDCARD = Short.MAX_VALUE;
	
	public static void init()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(Items.quiver, 1), new Object[] {
				new ItemStack(Item.bootsLeather, 1, WILDCARD), Item.silk, Item.silk });
	}
}
