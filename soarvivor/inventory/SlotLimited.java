package soarvivor.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import soarvivor.items.Quiver;
import soarvivor.lib.config.Settings;

// Custom Slot:
public class SlotLimited extends Slot
{
	public SlotLimited(IInventory inventory, int index, int xPos, int yPos)
	{
		super(inventory, index, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return true;
	}

	public int getSlotStackLimit()
	{
		return Settings.limitStackSize;
	}
}
