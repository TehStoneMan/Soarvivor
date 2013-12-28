package soarvivor.inventory;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import soarvivor.lib.LogHelper;

/**
 * 
 * @author TehStoneMan
 * 
 */
public class ContainerLimitedPlayer extends Container
{
	/**
	 * Avoid magic numbers! This will greatly reduce the chance of you making
	 * errors in 'transferStackInSlot' method
	 */
	private static final int	ARMOR_START		= InventoryLimitedPlayer.INV_SIZE;
	private static final int	ARMOR_END		= ARMOR_START + 3;
	private static final int	INV_START		= ARMOR_END + 1;
	private static final int	INV_END			= INV_START + 26;
	private static final int	HOTBAR_START	= INV_END + 1;
	private static final int	HOTBAR_END		= HOTBAR_START + 8;

	public ContainerLimitedPlayer(EntityPlayer player, InventoryPlayer inventoryPlayer,
			InventoryLimitedPlayer inventoryCustom)
	// public ContainerLimitedPlayer(InventoryPlayer par1InventoryPlayer,
	// boolean par2,
	// EntityPlayer par3EntityPlayer)
	{
		int i;

		// Add CUSTOM slots - we'll just add two for now, both of the same type.
		// Make a new Slot class for each different item type you want to add
		this.addSlotToContainer(new SlotLimited(inventoryCustom, 0, 8, 84));
		this.addSlotToContainer(new SlotLimited(inventoryCustom, 1, 26, 84));
		this.addSlotToContainer(new SlotLimited(inventoryCustom, 2, 44, 84));

		// Add ARMOR slots; note you need to make a public version of SlotArmor
		// just copy and paste the vanilla code into a new class and change what
		// you need
		for (i = 0; i < 4; ++i)
		{
			this.addSlotToContainer(new SlotArmor(player, inventoryPlayer, inventoryPlayer
					.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
		}

		// Add vanilla PLAYER INVENTORY - Limited to one row
		i = 2;
		for (int j = 0; j < 9; ++j)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18,
					84 + i * 18));
		}

		// Add ACTION BAR - just copied/pasted from vanilla classes
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	/**
	 * This should always return true, since custom inventory can be accessed
	 * from anywhere
	 */
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
	{
		LogHelper.log(Level.INFO, "Player attempt Shift-click");
		ItemStack stackTarget = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackSource = slot.getStack();
			stackTarget = stackSource.copy();

			// Either armor slot or custom item slot was clicked
			if (fromSlot < INV_START)
			{
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(stackSource, INV_START + 17, HOTBAR_END + 1, true))
					return null;

				slot.onSlotChange(stackSource, stackTarget);
			}
			// Item is in inventory / hotbar, try to place either in custom or
			// armor slots
			else
			{
				// if item is our custom item
				// if (itemstack1.getItem() instanceof ItemUseMana)
				// {
				// if (!this.mergeItemStack(itemstack1, 0,
				// InventoryCustomPlayer.INV_SIZE, false)){ return null; }
				// }
				// if item is armor
				// else
				if (stackSource.getItem() instanceof ItemArmor)
				{
					int type = ((ItemArmor) stackSource.getItem()).armorType;
					if (!this.mergeItemStack(stackSource, ARMOR_START + type, ARMOR_START + type
							+ 1, false)) { return null; }
				}
				// item in player's inventory, but not in action bar
				else if (fromSlot >= INV_START + 17 && fromSlot < HOTBAR_START)
				{
					// place in action bar
					if (!this.mergeItemStack(stackSource, HOTBAR_START, HOTBAR_START + 1, false)) { return null; }
				}
				// item in action bar - place in player inventory
				else if (fromSlot >= HOTBAR_START && fromSlot < HOTBAR_END + 1)
				{
					if (!this.mergeItemStack(stackSource, INV_START + 17, INV_END + 1, false)) { return null; }
				}
			}

			if (stackSource.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			} else
			{
				slot.onSlotChanged();
			}

			if (stackSource.stackSize == stackTarget.stackSize) { return null; }

			slot.onPickupFromSlot(player, stackSource);
		}

		return stackTarget;
	}
}
