package soarvivor.util;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.lib.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WaterStats // extends FoodStats
{
	/** The player's water and ice levels. */
	private int		wetLevel				= ExtendedPlayer.MAX_WET_LEVEL;
	private int		iceLevel				= 0;

	/** The player's water saturation. */
	private float	waterSaturationLevel	= 5.0F;

	/** The player's water exhaustion. */
	private float	waterExhaustionLevel;

	/** The player's water timer value. */
	private int		waterTimer;
	private int		prevWetLevel			= wetLevel;
	private int		prevIceLevel			= iceLevel;

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
			this.waterSaturationLevel = par1NBTTagCompound.getFloat("waterSaturationLevel");
			this.waterExhaustionLevel = par1NBTTagCompound.getFloat("waterExhaustionLevel");
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
		par1NBTTagCompound.setFloat("waterSaturationLevel", this.waterSaturationLevel);
		par1NBTTagCompound.setFloat("waterExhaustionLevel", this.waterExhaustionLevel);
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
		return this.wetLevel < ExtendedPlayer.MAX_WET_LEVEL;
	}

	/**
	 * If iceLevel is not 0.
	 */
	public boolean needHeat()
	{
		return this.iceLevel > 0;
	}
}
