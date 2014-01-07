/**
 * 
 */
package soarvivor.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import soarvivor.items.Quiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Custom inventory slot that can only hold a quiver
 * 
 * @author TehStoneMan
 */
public class SlotQuiver extends Slot
{
	public SlotQuiver(IInventory inventory, int index, int xPos, int yPos)
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
		return (itemStack.getItem() instanceof Quiver);
	}

	public int getSlotStackLimit()
	{
		return 1;
	}

//	@SideOnly(Side.CLIENT)
//	public Icon getBackgroundIconIndex()
//	{
//		return ItemArmor.func_94602_b(this.armorType);
//	}
}
