package soarvivor.lib;

import java.lang.reflect.Field;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.util.DebugInfo;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class PlayerTickHandler implements ITickHandler {
	/*
	 * This Tick Handler will fire for whatever TickType's you construct and
	 * register it with.
	 */
	public PlayerTickHandler() {
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// Get player, extended properties and world for the tick
		EntityPlayer player = (EntityPlayer) tickData[0];
		ExtendedPlayer props = ExtendedPlayer.get(player);

		World worldObj = player.worldObj;

		// Check if player is using an item
		if (player.getItemInUse() != null) {
			ItemStack itemUsing = player.inventory.getCurrentItem();

			if (itemUsing == player.getItemInUse()) {
				// Has item been used up?
				if (player.getItemInUseCount() == 1) {
					// Check the item in use, and change hydration as
					// appropriate

					// Potion effects on hydradtion
					if (itemUsing.itemID == Item.potion.itemID)
						switch (itemUsing.getItemDamage()) {
						case 0: // Water bottle
							props.addWaterStats(5, 0, 1f);
							break;
						case 6: // Instant Health
							props.addWaterStats(10, 0, 2f);
							break;
						case 7: // Instant Damage
							props.addWaterStats(-5, 0, -0.5f);
							break;
						case 9: // Nausea
							props.addWaterStats(-2, 0, 0f);
							break;
						case 10: // Regeneration
							props.addWaterStats(5, 0, 2f);
							break;
						case 13: // Water breathing
							props.addWaterStats(20, 0, 1f);
							break;
						case 17: // Hunger
							props.addWaterStats(-5, 0, -1f);
							break;
						case 21: // Health boost
							props.addWaterStats(15, 0, 2f);
							break;
						default:
							props.addWaterStats(2, 0, 1f);
						}

					// Food items
					if (itemUsing.itemID == Item.appleRed.itemID) {
						props.addWaterStats(2, 0, 1f);
					}

					if (itemUsing.itemID == Item.appleGold.itemID) {
						props.addWaterStats(10, 0, 2f);
					}

					if (itemUsing.itemID == Item.melon.itemID) {
						props.addWaterStats(5, 0, 1f);
					}

					if (itemUsing.itemID == Item.bowlSoup.itemID) {
						props.addWaterStats(5, 0, 1f);
					}
				}
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// Get player, extended properties and world for the tick
		EntityPlayer player = (EntityPlayer) tickData[0];
		ExtendedPlayer props = ExtendedPlayer.get(player);

		World worldObj = player.worldObj;

		// Process tick
		if (!worldObj.isRemote) {
			// WaterStats waterStats = props.getWaterStats();
			props.onUpdate();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return null;
	}
}
