package soarvivor.lib;

import java.util.Random;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.inventory.InventoryQuiver;
import soarvivor.items.Items;

public class SvrEventHandler
{
	protected Random	rand;

	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event)
	{
		/*
		 * Be sure to check if the entity being constructed is the correct type
		 * for the extended properties you're about to add! The null check may
		 * not be necessary - I only use it to make sure properties are only
		 * registered once per entity
		 */
		if (event.entity instanceof EntityPlayer
				&& ExtendedPlayer.get((EntityPlayer) event.entity) == null)

		// This is how extended properties are registered using our convenient
		// method from earlier
			ExtendedPlayer.register((EntityPlayer) event.entity);
		// That will call the constructor as well as cause the init() method
		// to be called automatically
	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		// Only need to synchronise when the world is remote (i.e. we're on the
		// server side) and only for player entities, as that's what we need for
		// the GuiHydrationBar
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
			ExtendedPlayer.get((EntityPlayer) event.entity).sync();
	}

	@ForgeSubscribe
	public void onArrowNockEvent(ArrowNockEvent event)
	{
		// Variables from event
		EntityPlayer player = event.entityPlayer;
		ItemStack result = event.result;
		int quiverSlot = -1;

		// Replace vanilla code with our own

		// Check for creative mode
		boolean infinateAmmo = player.capabilities.isCreativeMode;

		// Check for arrows in equipped quiver
		// ** Quiver armour slot yet implemented **
		boolean hasAmmo = infinateAmmo;

		// Check for arrows loose in player inventory
		hasAmmo = (hasAmmo || player.inventory.hasItem(Item.arrow.itemID));

		// Check inventory for quivers
		if (!hasAmmo && player.inventory.hasItem(Items.quiver.itemID))
		{
			int i = 0;
			while (!hasAmmo && i < player.inventory.getSizeInventory())
			{
				if (player.inventory.getStackInSlot(i).itemID == Items.quiver.itemID)
				{
					quiverSlot = i;
					ItemStack test = player.inventory.getStackInSlot(quiverSlot);
					InventoryQuiver inv = new InventoryQuiver(test);
					hasAmmo = inv.hasItem(Item.arrow.itemID);
				}
				++i;
			}
		}

		if (infinateAmmo || hasAmmo) player.setItemInUse(result, result.getMaxItemUseDuration());

		// Cancel event to prevent vanilla processing.
		event.result = result;
		if (event.isCancelable()) event.setCanceled(true);
	}

	@ForgeSubscribe
	public void onArrowLooseEvent(ArrowLooseEvent event)
	{
		// Variables from event
		ItemStack bow = event.bow;
		EntityPlayer player = event.entityPlayer;
		int charge = event.charge;
		World world = player.worldObj;
		int quiverSlot = -1;

		// Replace vanilla code with our own

		// Check for creative mode or infinity enchantment
		boolean infinateAmmo = player.capabilities.isCreativeMode
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) > 0;

		// Check for arrows in equipped quiver
		// ** Quiver armour slot yet implemented **
		boolean hasAmmo = infinateAmmo;

		// Check for arrows loose in player inventory
		hasAmmo = (hasAmmo || player.inventory.hasItem(Item.arrow.itemID));

		// Check inventory for quivers
		if (!hasAmmo && player.inventory.hasItem(Items.quiver.itemID))
		{
			int i = 0;
			while (!hasAmmo && i < player.inventory.getSizeInventory())
			{
				if (player.inventory.getStackInSlot(i).itemID == Items.quiver.itemID)
				{
					quiverSlot = i;
					ItemStack test = player.inventory.getStackInSlot(quiverSlot);
					InventoryQuiver inv = new InventoryQuiver(test);
					hasAmmo = inv.hasItem(Item.arrow.itemID);
				}
				++i;
			}
		}

		if (hasAmmo)
		{
			float f = (float) charge / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double) f < 0.1D) { return; }

			if (f > 1.0F)
			{
				f = 1.0F;
			}

			EntityArrow entityarrow = new EntityArrow(world, player, f * 2.0F);

			if (f == 1.0F)
			{
				entityarrow.setIsCritical(true);
			}

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);

			if (k > 0)
			{
				entityarrow.setDamage(entityarrow.getDamage() + (double) k * 0.5D + 0.5D);
			}

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);

			if (l > 0)
			{
				entityarrow.setKnockbackStrength(l);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0)
			{
				entityarrow.setFire(100);
			}

			bow.damageItem(1, player);
			// world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F
			// / (itemRand * 0.4F + 1.2F) + f * 0.5F);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F);

			if (infinateAmmo)
			{
				entityarrow.canBePickedUp = 2;
			} else
			{
				if (quiverSlot >= 0)
				{
					ItemStack test = player.inventory.getStackInSlot(quiverSlot);
					InventoryQuiver inv = new InventoryQuiver(test);
					inv.consumeInventoryItem(Item.arrow.itemID);
				} else player.inventory.consumeInventoryItem(Item.arrow.itemID);
			}

			if (!world.isRemote)
			{
				world.spawnEntityInWorld(entityarrow);
			}
		}

		// Cancel event to prevent vanilla processing.
		if (event.isCancelable()) event.setCanceled(true);
	}

	@ForgeSubscribe
	public void onItemPickupEvent(EntityItemPickupEvent event)
	{
		this.rand = new Random();

		EntityPlayer player = event.entityPlayer;
		ExtendedPlayer props = ExtendedPlayer.get(player);

		if (props == null) return;

		EntityItem entityitem = event.item;

		ItemStack itemstack = entityitem.getEntityItem();
		int stacksize = itemstack.stackSize;

		if (entityitem.delayBeforeCanPickup <= 0
				&& (stacksize <= 0 || props.addItemStackToInventory(itemstack)))
		{
			if (itemstack.itemID == Block.wood.blockID)
				player.triggerAchievement(AchievementList.mineWood);

			if (itemstack.itemID == Item.leather.itemID)
				player.triggerAchievement(AchievementList.killCow);

			if (itemstack.itemID == Item.diamond.itemID)
				player.triggerAchievement(AchievementList.diamonds);

			if (itemstack.itemID == Item.blazeRod.itemID)
				player.triggerAchievement(AchievementList.blazeRod);

			GameRegistry.onPickupNotification(player, entityitem);

			entityitem.playSound("random.pop", 0.2F,
					((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			player.onItemPickup(entityitem, stacksize);

			if (itemstack.stackSize <= 0) entityitem.setDead();
		}

		// Cancel event to prevent vanilla processing.
		if (event.isCancelable()) event.setCanceled(true);
	}
}
