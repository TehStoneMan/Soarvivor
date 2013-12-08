package soarvivor.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Armor Slot:
public class SlotArmor extends Slot
{
	/**
	 * The armour type that can be placed on that slot, it uses the same values
	 * of armorType field on ItemArmor.
	 */
	final int			armorType;

	/**
	 * The parent class of this slot, ContainerPlayer, SlotArmor is a Anon inner
	 * class.
	 */
	final EntityPlayer	player;

	public SlotArmor(EntityPlayer player, IInventory inventory, int par3, int par4, int par5,
			int par6)
	{
		super(inventory, par3, par4, par5);
		this.player = player;
		this.armorType = par6;
	}

	/**
	 * Returns the maximum stack size for a given slot (usually the same as
	 * getInventoryStackLimit(), but 1 in the case of armour slots)
	 */
	public int getSlotStackLimit()
	{
		return 1;
	}

	/**
	 * Check if the stack is a valid item for this slot. Always true beside for
	 * the armour slots.
	 */
	public boolean isItemValid(ItemStack itemstack)
	{
		Item item = (itemstack == null ? null : itemstack.getItem());
		return item != null && item.isValidArmor(itemstack, armorType, player);
	}

	/**
	 * Returns the icon index on items.png that is used as background image of
	 * the slot.
	 */
	@SideOnly(Side.CLIENT)
	public Icon getBackgroundIconIndex()
	{
		return ItemArmor.func_94602_b(this.armorType);
	}
}
