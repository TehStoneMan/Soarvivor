package soarvivor.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import soarvivor.items.Quiver;
import soarvivor.lib.config.Settings;

/**
 * Custom inventory slot that can only hold arrows
 * Also conforms to limited stack size
 * 
 * @author TehStoneMan
 */
public class SlotArrow extends Slot
{
	public boolean enabled = true;
	
	public SlotArrow(IInventory inventory, int index, int xPos, int yPos)
	{
		super(inventory, index, xPos, yPos);
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
		return (enabled && itemStack.getItem().itemID == Item.arrow.itemID);
	}

	public int getSlotStackLimit()
	{
		return Settings.limitStackSize;
	}

}
