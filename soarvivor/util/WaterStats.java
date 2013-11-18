package soarvivor.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.DamageSource;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.lib.ModInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WaterStats // extends FoodStats
{
	/** The player's water and ice levels. */
	private int		wetLevel				= ExtendedPlayer.MAX_HYDRATION / 2;
	private int		iceLevel				= 0;

	/** The player's water saturation. */
	private float	waterSaturationLevel	= 5.0F;

	/** The player's water exhaustion. */
	private float	waterExhaustionLevel;

	/** The player's water timer value. */
	private int		waterTimer;
	private int		prevWetLevel			= wetLevel;
	private int		prevIceLevel			= iceLevel;

	public void addStats(int wet, int ice, float saturation)
	{
		this.wetLevel = Math.min(wet + this.wetLevel, ExtendedPlayer.MAX_HYDRATION);
		this.iceLevel = Math.min(ice + this.iceLevel, ExtendedPlayer.MAX_FROZEN);

		this.waterSaturationLevel = Math.min(this.waterSaturationLevel + (float)wet * saturation
				* 2.0F, (float)this.wetLevel);
	}

	/**
	 * Eat some food.
	 * 
	 * <pre>
	 * public void addStats(ItemFood par1ItemFood)
	 * {
	 * 	this.addStats(par1ItemFood.getHealAmount(), par1ItemFood.getSaturationModifier());
	 * }
	 * </pre>
	 */

	/**
	 * Handles the food game logic.
	 */
	public void onUpdate(EntityPlayer player)
	{
		int difficulty = player.worldObj.difficultySetting;
		this.prevWetLevel = this.wetLevel;
		this.prevIceLevel = this.iceLevel;

		/**
		 * <pre>
		 * if (this.foodExhaustionLevel &gt; 4.0F)
		 * {
		 * 	this.foodExhaustionLevel -= 4.0F;
		 * 
		 * 	if (this.foodSaturationLevel &gt; 0.0F)
		 * 	{
		 * 		this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
		 * 	} else if (i &gt; 0)
		 * 	{
		 * 		this.foodLevel = Math.max(this.foodLevel - 1, 0);
		 * 	}
		 * }
		 * </pre>
		 */

		if (player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration")
				&& this.wetLevel >= 18 && player.shouldHeal())
		{
			++this.waterTimer;

			if (this.waterTimer >= 80)
			{
				player.heal(0.5F);
				this.waterTimer = 0;
			}
		} else if (this.wetLevel <= 0)
		{
			++this.waterTimer;

			if (this.waterTimer >= 80)
			{
				if (player.getHealth() > 10.0F || difficulty >= 3 || player.getHealth() > 1.0F
						&& difficulty >= 2) player.attackEntityFrom(DamageSource.starve, 1.0F);

				this.waterTimer = 0;
			}
		} else this.waterTimer = 0;
	}

	/**
	 * Reads water stats from an NBT object.
	 */
	public void readNBT(NBTTagCompound par1NBTTagCompound)
	{
		if (par1NBTTagCompound.hasKey("wetLevel"))
		{
			this.wetLevel = par1NBTTagCompound.getInteger("wetLevel");
			this.iceLevel = par1NBTTagCompound.getInteger("iceLevel");
			this.waterTimer = par1NBTTagCompound.getInteger("waterTickTimer");
		}
	}

	/**
	 * Writes water stats to an NBT object.
	 */
	public void writeNBT(NBTTagCompound par1NBTTagCompound)
	{
		par1NBTTagCompound.setInteger("wetLevel", this.wetLevel);
		par1NBTTagCompound.setInteger("iceLevel", this.iceLevel);
		par1NBTTagCompound.setInteger("waterTickTimer", this.waterTimer);
	}

	/**
	 * Get the player's water level.
	 */
	public int getWaterLevel()
	{
		return this.wetLevel;
	}

	@SideOnly(Side.CLIENT)
	public int getPrevWaterLevel()
	{
		return this.prevWetLevel;
	}

	/**
	 * Get the player's ice level.
	 */
	public int getIceLevel()
	{
		return this.iceLevel;
	}

	@SideOnly(Side.CLIENT)
	public int getPrevIceLevel()
	{
		return this.prevIceLevel;
	}

	/**
	 * If waterLevel is not max.
	 */
	public boolean needWater()
	{
		return this.wetLevel < ExtendedPlayer.MAX_HYDRATION;
	}

	/**
	 * If iceLevel is not 0.
	 */
	public boolean needHeat()
	{
		return this.iceLevel > 0;
	}

	@SideOnly(Side.CLIENT)
	public void setWaterLevel(int wet)
	{
		this.wetLevel = Math.min(wet, ExtendedPlayer.MAX_HYDRATION);
	}

	@SideOnly(Side.CLIENT)
	public void setIceLevel(int ice)
	{
		this.iceLevel = Math.min(ice, ExtendedPlayer.MAX_FROZEN);
	}
}
