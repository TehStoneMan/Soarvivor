package soarvivor.inventory;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import soarvivor.lib.LogHelper;
import soarvivor.lib.config.Settings;

public class InventoryQuiver implements IInventory {
    /** Define the inventory of this quiver */
    public static final int INV_SIZE = 2;
    public ItemStack[] inventory = new ItemStack[INV_SIZE];

    /** The name of this inventory */
    private String name = "Quiver";

    /** Reference to the quiver item that stores the NBT Tag */
    private ItemStack quiver;

    /**
     * @param itemStack
     *            - the ItemStack to which this inventory belongs
     */
    public InventoryQuiver(ItemStack itemStack) {
	quiver = itemStack;

	// Create a new NBT Tag Compound if one doesn't already exist, or you
	// will crash
	if (!quiver.hasTagCompound())
	    quiver.setTagCompound(new NBTTagCompound());

	// Read the inventory contents from NBT
	loadNBTData(quiver.getTagCompound());
    }

    @Override
    public int getSizeInventory() {
	return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
	return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
	ItemStack itemStack = this.inventory[slot];

	// Check stack for contents
	if (itemStack != null) {
	    // Stack is not empty. Decrease stack by amount
	    if (itemStack.stackSize <= amount) {
		itemStack = this.inventory[slot];
		this.inventory[slot] = null;
	    } else {
		itemStack = itemStack.splitStack(amount);
		if (this.inventory[slot].stackSize == 0)
		    this.inventory[slot] = null;
	    }
	    this.onInventoryChanged();
	    return itemStack;
	}
	return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
	ItemStack stack = getStackInSlot(slot);
	setInventorySlotContents(slot, null);
	return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
	this.inventory[slot] = itemStack;

	if (itemStack != null
		&& itemStack.stackSize > this.getInventoryStackLimit())
	    itemStack.stackSize = this.getInventoryStackLimit();

	this.onInventoryChanged();
    }

    @Override
    public String getInvName() {
	return this.name;
    }

    @Override
    public boolean isInvNameLocalized() {
	return this.name.length() > 0;
    }

    @Override
    public int getInventoryStackLimit() {
	// Can only hold a quarter of a full stack. Part of the
	// "Limited Inventory" rule
	return Settings.limitStackSize;
    }

    /*
     * This is the method that will handle saving the inventory contents, as it
     * is called (or should be called!) any time the inventory changes. Perfect.
     * Much better than using onUpdate on an Item, as this will also let you
     * change things in your inventory without ever opening a Gui, if you want.
     */
    @Override
    public void onInventoryChanged() {
	// Check for empty slots
	for (int i = 0; i < getSizeInventory(); ++i)
	    if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0)
		this.inventory[i] = null;

	// Save inventory to NBT
	saveNBTData(quiver.getTagCompound());
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
	return true;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
	// Inventory slots should only accept arrows
	return (itemStack.getItem().itemID == Item.arrow.itemID);
    }

    /**
     * Get if a specified item id is inside the inventory.
     */
    public boolean hasItem(int itemID) {
	int slot = getInventorySlotContainItem(itemID);
	return slot >= 0;
    }

    /**
     * Returns a slot index in main inventory containing a specific itemID
     */
    private int getInventorySlotContainItem(int itemID) {
	for (int i = 0; i < this.inventory.length; ++i)
	    if (this.inventory[i] != null && this.inventory[i].itemID == itemID)
		return i;

	return -1;
    }

    /**
     * Remove one item of specified itemID from inventory (if it is in a stack,
     * the stack size will reduce with 1)
     */
    public boolean consumeInventoryItem(int itemID) {
	int slot = getInventorySlotContainItem(itemID);

	if (slot < 0)
	    return false;
	else {
	    if (--this.inventory[slot].stackSize <= 0)
		this.inventory[slot] = null;
	    onInventoryChanged();
	    return true;
	}
    }

    /**
     * A custom method to write our inventory to an ItemStack's NBT compound
     */
    public void saveNBTData(NBTTagCompound compound) {
	// Create a new NBT Tag List to store itemStacks as NBT Tags
	NBTTagList itemList = new NBTTagList();

	for (int i = 0; i < inventory.length; ++i) {
	    // Only write stacks that contain items
	    if (inventory[i] != null) {
		// Make a new NBT Tag Compound to write the itemStack and slot
		// index to
		NBTTagCompound item = new NBTTagCompound();
		item.setByte("Slot", (byte) i);
		// Writes the itemStack in slot(i) to the Tag Compound we just
		// made
		inventory[i].writeToNBT(item);

		// add the tag compound to our tag list
		itemList.appendTag(item);
	    }
	}
	// Add the TagList to the ItemStack's Tag Compound with the name
	// "Items"
	compound.setTag("Items", itemList);
    }

    /**
     * A custom method to read our inventory from an ItemStack's NBT compound
     */
    public void loadNBTData(NBTTagCompound compound) {
	// Gets the custom taglist we wrote to this compound, if any
	NBTTagList itemList = compound.getTagList("Items");
	inventory = new ItemStack[getSizeInventory()];

	for (int i = 0; i < itemList.tagCount(); ++i) {
	    NBTTagCompound item = (NBTTagCompound) itemList.tagAt(i);
	    int slot = item.getByte("Slot") & 255;

	    // Just double-checking that the saved slot index is within our
	    // inventory array bounds
	    if (slot >= 0 && slot < inventory.length)
		inventory[slot] = ItemStack.loadItemStackFromNBT(item);
	}
    }
}
