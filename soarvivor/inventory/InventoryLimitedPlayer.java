package soarvivor.inventory;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import soarvivor.lib.LogHelper;
import soarvivor.lib.config.Ids;
import soarvivor.lib.config.Settings;

public class InventoryLimitedPlayer implements IInventory {
    /** The name for your custom inventory, possibly just "Inventory" */
    private final String name = "Inventory";

    /**
     * In case your inventory name is too generic, define a name to store the
     * NBT tag in as well
     */
    private final String tagName = "LtdInvTag";

    /** Define the inventory size here for easy reference */
    // This is also the place to define which slot is which if you have
    // different types, for example SLOT_SHIELD = 0, SLOT_AMULET = 1;
    public static final int INV_SIZE = 3;
    public static final int SLOT_QUIVER = 0;

    /**
     * Inventory's size must be same as number of slots you add to the Container
     * class
     */
    ItemStack[] inventory = new ItemStack[INV_SIZE];
    public boolean quiverFlag = false;

    public InventoryLimitedPlayer() {
	// don't need anything here!
    }

    @Override
    public int getSizeInventory() {
	return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
	return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
	ItemStack stack = getStackInSlot(slot);
	if (stack != null) {
	    if (stack.stackSize > amount) {
		stack = stack.splitStack(amount);
		this.onInventoryChanged();
	    } else
		setInventorySlotContents(slot, null);
	}
	return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
	ItemStack stack = getStackInSlot(slot);
	setInventorySlotContents(slot, null);
	return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
	this.inventory[slot] = itemstack;

	if (itemstack != null
		&& itemstack.stackSize > this.getInventoryStackLimit())
	    itemstack.stackSize = this.getInventoryStackLimit();

	this.onInventoryChanged();
    }

    @Override
    public String getInvName() {
	return name;
    }

    @Override
    public boolean isInvNameLocalized() {
	return name.length() > 0;
    }

    @Override
    public int getInventoryStackLimit() {
	return Settings.limitStackSize;
    }

    @Override
    public void onInventoryChanged() {
	if (this.quiverFlag)
	    saveToQuiver();
	else
	    loadFromQuiver();

	for (int i = 0; i < this.getSizeInventory(); ++i) {
	    if (this.inventory[i] != null && this.inventory[i].stackSize == 0)
		this.inventory[i] = null;
	}
	if (this.inventory[0] != null)
	    LogHelper.log(Level.INFO, "onInventoryChanged : Quiver " + this.inventory[1] + " : "
		    + this.inventory[2]);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
	return true;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
	if (slot == SLOT_QUIVER && itemstack.itemID == Ids.quiver)
	    return true;
	else if (slot != SLOT_QUIVER && itemstack.itemID == Item.arrow.itemID) {
	    if (getStackInSlot(SLOT_QUIVER) == null)
		return false;
	    else
		return true;
	}
	return false;
    }

    /*----------------*
     * Load/save functions
     *----------------*/

    /**
     * Saves inventory contents to NBT
     * 
     * @param tagcompound
     */
    public void saveNBTData(NBTTagCompound tagcompound) {
	NBTTagList items = new NBTTagList();

	NBTTagCompound quiver = new NBTTagCompound();
	quiver.setBoolean("quiverFlag", quiverFlag);
	items.appendTag(quiver);

	if (getStackInSlot(0) != null) {
	    NBTTagCompound item = new NBTTagCompound();
	    item.setByte("Slot", (byte) 0);
	    getStackInSlot(0).writeToNBT(item);
	    items.appendTag(item);
	}

	// We're storing our items in a custom tag list using our 'tagName' from
	// above to prevent potential conflict
	tagcompound.setTag(tagName, items);
	saveToQuiver();
    }

    /**
     * Helper method to save contents to equipped quiver
     */
    public void saveToQuiver() {
	if (this.inventory[SLOT_QUIVER] != null) {
	    ItemStack quiver = this.inventory[SLOT_QUIVER];
	    InventoryQuiver invQuiver = new InventoryQuiver(quiver);

	    invQuiver.inventory[0] = this.inventory[SLOT_QUIVER + 1];
	    invQuiver.inventory[1] = this.inventory[SLOT_QUIVER + 2];
	    invQuiver.onInventoryChanged();

	    this.quiverFlag = true;
	} else
	    this.quiverFlag = false;
    }

    /**
     * Loads inventory contents from NBT
     * 
     * @param tagcompound
     */
    public void loadNBTData(NBTTagCompound tagcompound) {
	NBTTagList items = tagcompound.getTagList(tagName);

	NBTTagCompound quiver = (NBTTagCompound) items.tagAt(0);
	quiverFlag = quiver.getBoolean("quiverFlag");

	if (items.tagCount() > 0) {
	    NBTTagCompound item = (NBTTagCompound) items.tagAt(1);
	    byte slot = item.getByte("Slot");

	    if (slot >= 0 && slot < getSizeInventory()) {
		inventory[slot] = ItemStack.loadItemStackFromNBT(item);
	    }
	}
	loadFromQuiver();
	onInventoryChanged();
    }

    /**
     * Helper method to load contents from equipped quiver
     */
    public void loadFromQuiver() {
	LogHelper.log(Level.INFO, "loadFromQuiver "
		+ this.inventory[SLOT_QUIVER]);
	if (this.inventory[SLOT_QUIVER] != null) {
	    ItemStack quiver = this.inventory[SLOT_QUIVER];
	    InventoryQuiver invQuiver = new InventoryQuiver(quiver);

	    // invQuiver.loadNBTData(quiver.getTagCompound());

	    LogHelper.log(Level.INFO, "Quiver contains "
		    + invQuiver.inventory[0] + " : " + invQuiver.inventory[1]);

	    this.inventory[SLOT_QUIVER + 1] = invQuiver.inventory[0];
	    this.inventory[SLOT_QUIVER + 2] = invQuiver.inventory[1];

	    this.quiverFlag = true;
	} else {
	    this.inventory[SLOT_QUIVER + 1] = null;
	    this.inventory[SLOT_QUIVER + 2] = null;

	    this.quiverFlag = false;
	}
    }
}
