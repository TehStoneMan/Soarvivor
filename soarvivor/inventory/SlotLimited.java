package soarvivor.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import soarvivor.items.Quiver;

// Custom Slot:
public class SlotLimited extends Slot
{
	/**
	 * 
	 * @param inventory
	 * @param index
	 * @param xPos
	 * @param yPos
	 */
	public SlotLimited(IInventory inventory, int index, int xPos, int yPos)
	{
		super(inventory, index, xPos, yPos);
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for
	 * the armor slots (and now also not always true for our custom inventory
	 * slots)
	 */
	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		// We only want our custom item to be storable in this slot
		return itemstack.getItem() instanceof Quiver;
	}
}
