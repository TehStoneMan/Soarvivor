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

public class InventoryLimitedPlayer implements IInventory
{
	/** The name for your custom inventory, possibly just "Inventory" */
	private final String	name		= "Inventory";

	/**
	 * In case your inventory name is too generic, define a name to store the
	 * NBT tag in as well
	 */
	private final String	tagName		= "LtdInvTag";

	/** Define the inventory size here for easy reference */
	// This is also the place to define which slot is which if you have
	// different types, for example SLOT_SHIELD = 0, SLOT_AMULET = 1;
	public static final int	INV_SIZE	= 3;
	public static final int	SLOT_QUIVER	= 0;

	/**
	 * Inventory's size must be same as number of slots you add to the Container
	 * class
	 */
	ItemStack[]				inventory	= new ItemStack[INV_SIZE];
	ItemStack[]				invOld		= new ItemStack[INV_SIZE];

	public InventoryLimitedPlayer()
	{
		// don't need anything here!
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stack = getStackInSlot(slot);
		if (stack != null)
		{
			if (stack.stackSize > amount)
			{
				stack = stack.splitStack(amount);
				this.onInventoryChanged();
			} else setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		this.inventory[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
			itemstack.stackSize = this.getInventoryStackLimit();

		if (slot == SLOT_QUIVER)
		{
			if (itemstack == null)
			{
				this.inventory[1] = null;
				this.inventory[2] = null;
				this.invOld[1] = null;
				this.invOld[2] = null;
			} else
			{
				InventoryQuiver inv = new InventoryQuiver(itemstack);
				this.inventory[1] = inv.getStackInSlot(0);
				this.inventory[2] = inv.getStackInSlot(1);
				this.invOld[1] = inv.getStackInSlot(0);
				this.invOld[2] = inv.getStackInSlot(1);
			}
			// } else if (this.inventory[0] != null)
			// {
			// LogHelper.log(Level.INFO, "itemstack == " + itemstack);
			// InventoryQuiver inv = new InventoryQuiver(this.inventory[0]);
			// inv.inventory[slot - 1] = this.inventory[slot];
			// inv.onInventoryChanged();
		}

		this.onInventoryChanged();
	}

	@Override
	public String getInvName()
	{
		return name;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return name.length() > 0;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return Settings.limitStackSize;
	}

	@Override
	public void onInventoryChanged()
	{
		LogHelper.log(Level.INFO, "onInventoryChanged");
		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			LogHelper.log(Level.INFO, "Index " + i + " : " + this.inventory[i] + " : "
					+ this.invOld[i]);
			if (this.inventory[i] != null && this.inventory[i].stackSize == 0)
				this.inventory[i] = null;

			if (this.inventory[i] != this.invOld[i])
			{
				if (this.inventory[SLOT_QUIVER] == null) this.inventory[i] = null;
				else if (i > 0)
				{
					ItemStack quiver = this.inventory[SLOT_QUIVER];
					InventoryQuiver invQuiver = new InventoryQuiver(quiver);
					LogHelper.log(Level.INFO, quiver + " : " + this.inventory[i] + " : "
							+ invQuiver.inventory[i - 1]);
					invQuiver.inventory[i - 1] = this.inventory[i];
				}
			}

			this.invOld[i] = this.inventory[i];
			if (this.inventory[SLOT_QUIVER] != null)
			{
				LogHelper.log(Level.INFO, "Quiver changed");
				ItemStack quiver = this.inventory[SLOT_QUIVER];
				InventoryQuiver invQuiver = new InventoryQuiver(quiver);
				invQuiver.onInventoryChanged();
			}
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return true;
	}

	@Override
	public void openChest()
	{}

	@Override
	public void closeChest()
	{}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		if (!Settings.debug) return false;
		if (slot == SLOT_QUIVER && itemstack.itemID == Ids.quiver) return true;
		else if (slot != SLOT_QUIVER && itemstack.itemID == Item.arrow.itemID)
		{
			if (getStackInSlot(SLOT_QUIVER) == null) return false;
			else return true;
		}
		return false;
	}

	public void writeToNBT(NBTTagCompound tagcompound)
	{
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); ++i)
		{
			if (getStackInSlot(i) != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}

		// We're storing our items in a custom tag list using our 'tagName' from
		// above to prevent potential conflict
		tagcompound.setTag(tagName, items);
	}

	public void readFromNBT(NBTTagCompound tagcompound)
	{
		NBTTagList items = tagcompound.getTagList(tagName);

		for (int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound item = (NBTTagCompound) items.tagAt(i);
			byte slot = item.getByte("Slot");

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}
}
