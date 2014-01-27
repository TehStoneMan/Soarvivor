package soarvivor.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.items.Quiver;

public class ContainerQuiver extends LimitedContainer {
    // The Item Inventory for this container
    public InventoryQuiver inventory;

    /*
     * Using these will make transferStackInSlot easier to understand and
     * implement. INV_START is the index of the first slot in the Player's
     * Inventory, so our InventoryQuiver's number of slots (e.g. 5 slots is
     * array indices 0-4, so start at 5). Notice how we don't have to remember
     * how many slots we made? we can just use InventoryQuiver.INV_SIZE and if
     * we ever change it, the Container updates automatically.
     */
    private static final int ARROW_START = 0;
    private static final int ARROW_END = ARROW_START + InventoryQuiver.INV_SIZE
	    - 1;

    private static final int QUIVER_START = ARROW_END + 1;
    private static final int QUIVER_END = QUIVER_START
	    + InventoryLimitedPlayer.INV_SIZE - 1;

    private static final int INV_START = QUIVER_END + 1;
    // Limited inventory size
    private static final int INV_END = INV_START + 8;
    private static final int HOTBAR_START = INV_END + 1;
    private static final int HOTBAR_END = HOTBAR_START + 8;

    public ContainerQuiver(EntityPlayer player, InventoryPlayer invPlayer,
	    InventoryQuiver invQuiver) {
	inventory = invQuiver;
	ExtendedPlayer props = ExtendedPlayer.get(player);
	InventoryLimitedPlayer invLtd = props.ltdInventory;

	int i, j;

	for (i = 0; i < InventoryQuiver.INV_SIZE; ++i)
	    addSlotToContainer(new SlotArrow(inventory, i, 71 + (18 * i), 35));

	// Quiver equip slot, and quiver inventory slots
	for (i = 0; i < 3; ++i)
	    if (i == 0)
		this.addSlotToContainer(new SlotQuiver(invLtd, i, 8 + i * 18,
			84));
	    else
		this.addSlotToContainer(new SlotArrow(invLtd, i, 8 + i * 18, 84));

	// Add vanilla PLAYER INVENTORY - Limited to one row
	i = 2;
	for (j = 0; j < 9; ++j)
	    this.addSlotToContainer(new SlotLimited(invPlayer, j + (i + 1) * 9,
		    8 + j * 18, 84 + i * 18));

	// Add ACTION BAR - just copied/pasted from vanilla classes
	for (i = 0; i < 9; ++i)
	    this.addSlotToContainer(new SlotLimited(invPlayer, i, 8 + i * 18,
		    142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
	return true;
    }

    /*
     * Called when a player shift-clicks on a slot. You must override this or
     * you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot) {
	ItemStack stackTarget = null;
	Slot slot = (Slot) inventorySlots.get(fromSlot);

	if (slot != null && slot.getHasStack()) {
	    ItemStack stackSource = slot.getStack();
	    stackTarget = stackSource.copy();

	    if (fromSlot <= ARROW_END) {
		/*
		 * Transfer from item inventory to player inventory/hotbar
		 */
		if (!mergeItemStack(stackSource, INV_START, HOTBAR_END + 1,
			true))
		    return null;
	    } else if (fromSlot >= QUIVER_START && fromSlot <= QUIVER_END) {
		/*
		 * Transfer from quiver slot to inventory/hotbar
		 */
		if (!this.mergeItemStack(stackSource, INV_START, HOTBAR_END,
			false))
		    return null;
	    } else if (stackSource.getItem() instanceof Quiver
		    && !((Slot) this.inventorySlots.get(QUIVER_START))
			    .getHasStack()) {
		/*
		 * Handle our custom quiver item
		 */
		if (!this.mergeItemStack(stackSource, QUIVER_START,
			QUIVER_START + 1, false))
		    return null;
	    } else if (stackSource.getItem().itemID == Item.arrow.itemID) {
		/*
		 * Handle arrows
		 */
		if (((Slot) this.inventorySlots.get(QUIVER_START))
			.getHasStack()) {
		    if (!this.mergeItemStack(stackSource, QUIVER_START + 1,
			    QUIVER_END + 1, false))
			if (!this.mergeItemStack(stackSource, ARROW_START + 1,
				ARROW_END + 1, false))
			    return null;
		} else if (!this.mergeItemStack(stackSource, ARROW_START + 1,
			ARROW_END + 1, false))
		    return null;

	    } else if (fromSlot >= INV_START && fromSlot <= INV_END) {
		/*
		 * item in player's inventory - place in hot bar
		 */
		if (!this.mergeItemStack(stackSource, HOTBAR_START, HOTBAR_END,
			false))
		    return null;
	    } else if (fromSlot >= HOTBAR_START && fromSlot <= HOTBAR_END) {
		/*
		 * item in hot bar - place in player inventory
		 */
		if (!this
			.mergeItemStack(stackSource, INV_START, INV_END, false))
		    return null;
	    }

	    if (stackSource.stackSize == 0)
		slot.putStack((ItemStack) null);
	    else
		slot.onSlotChanged();

	    if (stackSource.stackSize == stackTarget.stackSize)
		return null;

	    slot.onPickupFromSlot(player, stackSource);
	}

	return stackTarget;
    }

    public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer player) {
	if (slot == QUIVER_START) {
	    // Cannot change contents of quiver slot in this dialog
	    return null;
	}

	// Cannot interact with "arrow" slots if the "quiver" slot is empty
	if (slot > QUIVER_START
		&& slot <= QUIVER_END
		&& !((Slot) this.inventorySlots.get(QUIVER_START))
			.getHasStack())
	    return null;

	// Cannot interact with the slot that contains this item
	if (this.inventorySlots.get(slot) == player.getHeldItem())
	    return null;

	// Perform vanilla operations
	return super.slotClick(slot, par2, par3, player);
    }
}
