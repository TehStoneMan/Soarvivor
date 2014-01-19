/**
 * 
 */
package soarvivor.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import soarvivor.items.Quiver;

/**
 * Custom inventory slot that can only hold a quiver
 * 
 * @author TehStoneMan
 */
public class SlotQuiver extends Slot {
    public SlotQuiver(IInventory inventory, int index, int xPos, int yPos) {
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
    public boolean isItemValid(ItemStack itemStack) {
	// Inventory slots should only accept arrows
	return (itemStack.getItem() instanceof Quiver);
    }

    public int getSlotStackLimit() {
	return 1;
    }

    @Override
    public void onSlotChanged() {
	// ((InventoryLimitedPlayer) this.inventory).quiverFlag = false;
	((InventoryLimitedPlayer) this.inventory).loadFromQuiver();
	((InventoryLimitedPlayer) this.inventory).onInventoryChanged();
	super.onSlotChanged();
    }
}
