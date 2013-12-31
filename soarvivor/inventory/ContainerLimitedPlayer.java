package soarvivor.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import soarvivor.items.Quiver;

/**
 * 
 * @author TehStoneMan
 * 
 */
public class ContainerLimitedPlayer extends LimitedContainer
{
	/**
	 * Avoid magic numbers! This will greatly reduce the chance of you making
	 * errors in 'transferStackInSlot' method
	 */
	private static final int		CRAFT_RESULT	= 0;
	private static final int		CRAFT_START		= CRAFT_RESULT + 1;
	private static final int		CRAFT_END		= CRAFT_START + 3;

	private static final int		QUIVER_START	= CRAFT_END + 1;
	private static final int		QUIVER_END		= QUIVER_START
															+ InventoryLimitedPlayer.INV_SIZE - 1;

	private static final int		ARMOR_START		= QUIVER_END + 1;
	private static final int		ARMOR_END		= ARMOR_START + 3;
	private static final int		INV_START		= ARMOR_END + 1;
	// Limited inventory size
	private static final int		INV_END			= INV_START + 8;
	private static final int		HOTBAR_START	= INV_END + 1;
	private static final int		HOTBAR_END		= HOTBAR_START + 8;

	/** The crafting matrix inventory. */
	public InventoryCrafting		craftMatrix		= new InventoryCrafting(this, 2, 2);
	public IInventory				craftResult		= new InventoryCraftResult();

	protected final EntityPlayer	thePlayer;

	public ContainerLimitedPlayer(EntityPlayer player, InventoryPlayer inventoryPlayer,
			InventoryLimitedPlayer inventoryCustom)
	{
		// Get the player that this inventory belongs to
		this.thePlayer = player;

		// Add crafting slots
		this.addSlotToContainer(new SlotCrafting(inventoryPlayer.player, this.craftMatrix,
				this.craftResult, 0, 144, 36));
		int i;
		int j;

		for (i = 0; i < 2; ++i)
		{
			for (j = 0; j < 2; ++j)
			{
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18,
						26 + i * 18));
			}
		}

		// Add CUSTOM slots - we'll just add two for now, both of the same type.
		// Make a new Slot class for each different item type you want to add
		for (i = 0; i < 3; ++i)
			if (i == 0) this.addSlotToContainer(new SlotQuiver(inventoryCustom, i, 8 + i * 18, 84));
			else this.addSlotToContainer(new SlotArrow(inventoryCustom, i, 8 + i * 18, 84));

		// Add ARMOR slots; note you need to make a public version of SlotArmor
		// just copy and paste the vanilla code into a new class and change what
		// you need
		for (i = 0; i < 4; ++i)
			this.addSlotToContainer(new SlotArmor(player, inventoryPlayer, inventoryPlayer
					.getSizeInventory() - 1 - i, 8, 8 + i * 18, i));

		// Add vanilla PLAYER INVENTORY - Limited to one row
		i = 2;
		for (j = 0; j < 9; ++j)
			this.addSlotToContainer(new SlotLimited(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18,
					84 + i * 18));

		// Add ACTION BAR - just copied/pasted from vanilla classes
		for (i = 0; i < 9; ++i)
			this.addSlotToContainer(new SlotLimited(inventoryPlayer, i, 8 + i * 18, 142));

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory par1IInventory)
	{
		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance()
				.findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
	}

	/**
	 * This should always return true, since custom inventory can be accessed
	 * from anywhere
	 */
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
	{
		ItemStack stackTarget = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackSource = slot.getStack();
			stackTarget = stackSource.copy();

			if (fromSlot == CRAFT_RESULT)
			{
				/*
				 * Transfer from craft result to inventory/hotbar
				 */
				if (!this.mergeItemStack(stackSource, INV_START, HOTBAR_END, true)) return null;
				slot.onSlotChange(stackTarget, stackSource);
			} else if (fromSlot >= CRAFT_START && fromSlot <= CRAFT_END)
			{
				/*
				 * Transfer from craft matrix to inventory/hotbar
				 */
				if (!this.mergeItemStack(stackSource, INV_START, HOTBAR_END, false)) return null;
				slot.onSlotChange(stackTarget, stackSource);
			} else if (fromSlot == QUIVER_START)
			{
				/*
				 * Transfer from quiver slot to inventory/hotbar
				 */
				if (!this.mergeItemStack(stackSource, INV_START, HOTBAR_END, false)) return null;

				slot.onSlotChange(stackSource, stackTarget);
			} else if (stackSource.getItem() instanceof Quiver
					&& !((Slot) this.inventorySlots.get(QUIVER_START)).getHasStack())
			{
				/*
				 * Handle our custom quiver item
				 */
				if (!this.mergeItemStack(stackSource, QUIVER_START, QUIVER_START + 1, false))
					return null;
			} else if (fromSlot >= ARMOR_START && fromSlot <= ARMOR_END)
			{
				/*
				 * Transfer from armour slot to inventory/hotbar
				 */
				if (!this.mergeItemStack(stackSource, INV_START, HOTBAR_END, false)) return null;

				slot.onSlotChange(stackSource, stackTarget);
			} else if (stackSource.getItem() instanceof ItemArmor
					&& !((Slot) this.inventorySlots.get(ARMOR_START
							+ ((ItemArmor) stackSource.getItem()).armorType)).getHasStack())
			{
				/*
				 * Handle armor item
				 */
				int type = ((ItemArmor) stackSource.getItem()).armorType;
				if (!this.mergeItemStack(stackSource, ARMOR_START + type, ARMOR_START + type + 1,
						false)) return null;
			} else if (fromSlot >= INV_START && fromSlot <= INV_END)
			{
				/*
				 * item in player's inventory - place in hot bar
				 */
				if (!this.mergeItemStack(stackSource, HOTBAR_START, HOTBAR_END, false))
					return null;
			} else if (fromSlot >= HOTBAR_START && fromSlot <= HOTBAR_END)
			{
				/*
				 * item in hot bar - place in player inventory
				 */
				if (!this.mergeItemStack(stackSource, INV_START, INV_END, false)) return null;
			}

			if (stackSource.stackSize == 0) slot.putStack((ItemStack) null);
			else slot.onSlotChanged();

			if (stackSource.stackSize == stackTarget.stackSize) return null;

			slot.onPickupFromSlot(player, stackSource);
		}

		return stackTarget;
	}
}
