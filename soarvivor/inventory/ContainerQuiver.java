package soarvivor.inventory;

import java.util.logging.Level;

import soarvivor.lib.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerQuiver extends Container
{
	// The Item Inventory for this container
	public InventoryQuiver		inventory;

	/*
	 * Using these will make transferStackInSlot easier to understand and
	 * implement. INV_START is the index of the first slot in the Player's
	 * Inventory, so our InventoryQuiver's number of slots (e.g. 5 slots is
	 * array indices 0-4, so start at 5). Notice how we don't have to remember
	 * how many slots we made? we can just use InventoryQuiver.INV_SIZE and if
	 * we ever change it, the Container updates automatically.
	 */
	// private static final int ARMOUR_START = InventoryQuiver.INV_SIZE;
	// private static final int ARMOUR_END = ARMOUR_START + 3;
	// private static final int INV_START = ARMOUR_END + 1;
	private static final int	INV_START		= InventoryQuiver.INV_SIZE;
	private static final int	INV_END			= INV_START + 26;
	private static final int	HOTBAR_START	= INV_END + 1;
	private static final int	HOTBAR_END		= INV_START + 8;

	public ContainerQuiver(EntityPlayer entPlayer, InventoryPlayer invPlayer,
			InventoryQuiver invQuiver)
	{
		inventory = invQuiver;

		int i;

		for (i = 0; i < InventoryQuiver.INV_SIZE; ++i)
			addSlotToContainer(new SlotQuiver(inventory, i, 71 + (18 * i), 35));

		/**
		 * If you want, you can add ARMOR SLOTS here as well, but you need to
		 * make a public version of SlotArmor. I won't be doing that in this
		 * tutorial.
		 * 
		 * <pre>
		 * for (i = 0; i &lt; 4; ++i)
		 * {
		 * 	// These are the standard positions for survival inventory layout
		 * 	this.addSlotToContainer(new SlotArmor(this.player, inventoryPlayer, inventoryPlayer
		 * 			.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));
		 * }
		 * </pre>
		 */

		// PLAYER INVENTORY - uses default locations for standard inventory
		// texture file
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		}

		// PLAYER ACTION BAR - uses default locations for standard action bar
		// texture file
		for (i = 0; i < 9; ++i)
		{
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return true;
	}

	/*
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
	{
		ItemStack stackTarget = null;
		Slot slot = (Slot)inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackSource = slot.getStack();
			stackTarget = stackSource.copy();

			// If item is in our custom Inventory or armour slot
			if (fromSlot < INV_START)
			{
				// try to place in player inventory / action bar
				if (!mergeItemStack(stackSource, INV_START, HOTBAR_END + 1, true)) return null;
			}
			// Item is in inventory / hotbar, try to place in custom inventory
			// or armour slots
			else
			{
				/**
				 * If your inventory only stores certain instances of Items, you
				 * can implement shift-clicking to your inventory like this:
				 * 
				 */
				// Check that the item is the right type
				if (stackSource.getItem().itemID == Item.arrow.itemID)
				// Try to merge into your custom inventory slots
				// We use 'InventoryItem.INV_SIZE' instead of INV_START just
				// in case you also add armour or other custom slots
					if (!mergeItemStack(stackSource, 0, InventoryQuiver.INV_SIZE, false))
						return null;
			}

			if (stackSource.stackSize == 0)
				slot.putStack((ItemStack)null);
			else slot.onSlotChanged();

			if (stackSource.stackSize == stackTarget.stackSize) return null;

			slot.onPickupFromSlot(player, stackSource);
		}

		return stackTarget;
	}

	/**
	 * merges provided ItemStack with the first available one in the
	 * container/player inventory
	 */
	@Override
	protected boolean mergeItemStack(ItemStack stackSource, int slotStart, int slotEnd,
			boolean reverse)
	{
		// Setup success flag
		boolean success = false;

		// Setup main counter
		int count = slotStart;
		if (reverse) count = slotEnd - 1;

		Slot slot;
		ItemStack itemstack;

		// Is the item stackable?
		if (stackSource.isStackable())
		{
			while (stackSource.stackSize > 0
					&& (!reverse && count < slotEnd || reverse && count >= slotStart))
			{
				slot = (Slot)inventorySlots.get(count);
				itemstack = slot.getStack();

				if (itemstack != null
						&& itemstack.itemID == stackSource.itemID
						&& (!stackSource.getHasSubtypes() || stackSource.getItemDamage() == itemstack
								.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stackSource, itemstack))
				{
					int stackSize = itemstack.stackSize + stackSource.stackSize;

					// Get the maximum amount that this slot can hold
					int maxStackSize = Math.min(slot.getSlotStackLimit(),
							stackSource.getMaxStackSize());

					if (stackSize <= maxStackSize)
					{
						stackSource.stackSize = 0;
						itemstack.stackSize = stackSize;
						slot.onSlotChanged();
						success = true;
					} else if (itemstack.stackSize < maxStackSize)
					{
						stackSource.stackSize -= maxStackSize - itemstack.stackSize;
						itemstack.stackSize = maxStackSize;
						slot.onSlotChanged();
						success = true;
					}
				}

				if (reverse)
					--count;
				else ++count;
			}
		}

		if (stackSource.stackSize > 0)
		{
			if (reverse)
				count = slotEnd - 1;
			else count = slotStart;

			// Loop through slots, transferring as many items as possible
			while (!reverse && count < slotEnd || reverse && count >= slotStart)
			{
				slot = (Slot)inventorySlots.get(count);
				itemstack = slot.getStack();

				// Check for empty slot
				if (itemstack == null)
				{
					int stackSize = stackSource.stackSize;
					int maxStackSize = Math.min(slot.getSlotStackLimit(),
							stackSource.getMaxStackSize());

					// Transfer as many items as this stack can hold
					slot.putStack(stackSource.copy());
					slot.onSlotChanged();
					stackSource.stackSize = stackSize - maxStackSize;

					success = true;
					break;
				}

				if (reverse)
					--count;
				else ++count;
			}
		}

		return success;
	}
}
