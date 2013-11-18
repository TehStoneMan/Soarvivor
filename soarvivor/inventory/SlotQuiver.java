package soarvivor.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import soarvivor.items.Quiver;

public class SlotQuiver extends Slot
{
	public SlotQuiver(IInventory inventory, int par1, int par2, int par3)
	{
		super(inventory, par1, par2, par3);
	}

	/*
	 * This is the only method we need to override so that we can't place our
	 * inventory-storing item within it's own inventory (thus making it
	 * permanently inaccessible) as well as preventing abuse of storing
	 * backpacks within backpacks.
	 */

	// Check if stack is valid for this slot
	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		// Inventory slots should only accept arrows
		return (itemStack.getItem().itemID == Item.arrow.itemID);
	}
}
