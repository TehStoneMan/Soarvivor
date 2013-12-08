package soarvivor.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import soarvivor.soarvivor;
import soarvivor.inventory.InventoryQuiver;
import soarvivor.lib.ModInfo;
import soarvivor.lib.config.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Quiver extends Item
{
	public Quiver(int id)
	{
		super(id);
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setUnlocalizedName(Names.quiver_unlocalizedName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister icon)
	{
		itemIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":" + Names.quiver_unlocalizedName);
	}

	/*
	 * NOTE: If you want to open your gui on right click and your ItemStore, you
	 * MUST override getMaxItemUseDuration to return a value of at least 1,
	 * otherwise you won't be able to open the Gui. That's just how it works.
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
	{
		return 1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
			player.openGui(soarvivor.instance, soarvivor.InventoryQuiverGuiIndex, world,
					(int)player.posX, (int)player.posY, (int)player.posZ);

		return itemStack;
	}

}
