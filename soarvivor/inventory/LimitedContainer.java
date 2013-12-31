/**
 * 
 */
package soarvivor.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Class derived from Minecraft Container with limited inventory
 * 
 * @author TehStoneMan
 */
public abstract class LimitedContainer extends Container
{
	@Override
	protected boolean mergeItemStack(ItemStack stackSource, int slotStart, int slotEnd,
			boolean reverse)
	{
		boolean success = false;

		int count = slotStart;
		if (reverse) count = slotEnd - 1;

		Slot slot;
		ItemStack stackTarget;

		if (stackSource.isStackable())
		{
			while (stackSource.stackSize > 0
					&& (!reverse && count < slotEnd || reverse && count >= slotStart))
			{
				slot = (Slot) this.inventorySlots.get(count);
				stackTarget = slot.getStack();

				// Check if target is empty or contains the same item as source
				if (stackTarget != null
						&& stackTarget.itemID == stackSource.itemID
						&& (!stackSource.getHasSubtypes() || stackSource.getItemDamage() == stackTarget
								.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stackSource, stackTarget))
				{
					// Get combined stack size of both target and source
					int combinedStackSize = stackTarget.stackSize + stackSource.stackSize;

					// Get the maximum amount that this slot can hold
					int maxStackSize = Math.min(slot.getSlotStackLimit(),
							stackSource.getMaxStackSize());

					if (combinedStackSize <= maxStackSize)
					{
						stackSource.stackSize = 0;
						stackTarget.stackSize = combinedStackSize;
						slot.onSlotChanged();
						success = true;
					} else if (stackTarget.stackSize < maxStackSize)
					{
						stackSource.stackSize -= (maxStackSize - stackTarget.stackSize);
						stackTarget.stackSize = maxStackSize;
						slot.onSlotChanged();
						success = true;
					}
				}

				if (reverse) --count;
				else ++count;
			}
		}

		if (stackSource.stackSize > 0)
		{
			if (reverse) count = slotEnd - 1;
			else count = slotStart;

			while (!reverse && count < slotEnd || reverse && count >= slotStart)
			{
				slot = (Slot) this.inventorySlots.get(count);
				stackTarget = slot.getStack();

				if (stackTarget == null)
				{
					// Get the maximum amount that this slot can hold
					int maxStackSize = Math.min(slot.getSlotStackLimit(),
							stackSource.getMaxStackSize());
					if (maxStackSize >= stackSource.stackSize)
					{
						slot.putStack(stackSource.copy());
						slot.onSlotChanged();
						stackSource.stackSize = 0;
						success = true;
						break;
					} else
					{
						slot.putStack(stackSource.splitStack(maxStackSize));
						slot.onSlotChanged();
					}
				}

				if (reverse) --count;
				else ++count;
			}
		}

		return success;
	}
}
