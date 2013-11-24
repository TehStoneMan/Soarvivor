package soarvivor.lib;

import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.util.WaterStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class PlayerTickHandler implements ITickHandler
{
	/*
	 * This Tick Handler will fire for whatever TickType's you construct and
	 * register it with.
	 */
	public PlayerTickHandler()
	{}

	@Override
	public void onPlayerTickStart(EnumSet< TickType > type, Object... tickData)
	{
		// Get player, extended properties and world for the tick
		EntityPlayer player = (EntityPlayer)tickData[0];
		ExtendedPlayer props = ExtendedPlayer.get(player);

		World worldObj = player.worldObj;

		// Check if player is using an item
		if (player.getItemInUse() != null)
		{
			ItemStack itemUsing = player.inventory.getCurrentItem();

			if (itemUsing == player.getItemInUse())
			{
				// Has item been used up?
				if (player.getItemInUseCount() == 1)
				{
					// ItemStack waterBottle = new ItemStack(Item.potion);

					// Check if item is water bottle, and add to hydration if it is
					if (itemUsing.itemID == Item.potion.itemID && itemUsing.getItemDamage() == 0)
						props.getWaterStats().addStats(5, 0, 0);
				}
			}
		}

	}

	@Override
	public void onPlayerTickEnd(EnumSet< TickType > type, Object... tickData)
	{
		// Get player, extended properties and world for the tick
		EntityPlayer player = (EntityPlayer)tickData[0];
		ExtendedPlayer props = ExtendedPlayer.get(player);

		World worldObj = player.worldObj;

		// Process tick
		if (!worldObj.isRemote)
		{
			WaterStats waterStats = props.getWaterStats();
			waterStats.onUpdate(player);
		}
	}

	@Override
	public EnumSet< TickType > ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return null;
	}

}
