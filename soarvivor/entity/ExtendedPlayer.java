package soarvivor.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import soarvivor.inventory.InventoryLimitedPlayer;
import soarvivor.lib.ModInfo;
import soarvivor.util.WaterStats;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ExtendedPlayer implements IExtendedEntityProperties
{
	/*
	 * Here I create a constant EXT_PROP_NAME for this class of properties. You
	 * need a unique name for every instance of IExtendedEntityProperties you
	 * make, and doing it at the top of each class as a constant makes it very
	 * easy to organise and avoid typos. It's easiest to keep the same constant
	 * name in every class, as it will be distinguished by the class name:
	 * ExtendedPlayer.EXT_PROP_NAME vs. ExtendedEntity.EXT_PROP_NAME
	 */
	public final static String			EXT_PROP_NAME	= "PlayerHydration";

	// I always include the entity to which the properties belong for easy
	// access. It's final because we won't be changing which player it is
	private final EntityPlayer			player;

	// Declare other variables you want to add here

	// We're adding hydration to the player, so we'll need current and max
	// hydration
	public static final int				MAX_HYDRATION	= 20;
	public static final int				MAX_FROZEN		= 20;

	/** The player's water stats. (See class WaterStats) */
	protected WaterStats				waterStats		= new WaterStats();

	// This goes with all other variables declared at the beginning of the
	// class, such as currentMana and maxMana
	/** Custom inventory slots will be stored here - be sure to save to NBT! */
	public final InventoryLimitedPlayer	inventory		= new InventoryLimitedPlayer();

	/*
	 * The default constructor takes no arguments, but I put in the Entity so I
	 * can initialise the above variable 'player'
	 * 
	 * Also, it's best to initialise any other variables you may have added,
	 * just like in any constructor.
	 */
	public ExtendedPlayer(EntityPlayer player)
	{
		this.player = player;
		// Start with max hydration and zero ice.
		// this.player.getDataWatcher().addObject(WET_WATCHER, MAX_HYDRATION);
		// this.player.getDataWatcher().addObject(ICE_WATCHER, 0);
	}

	/**
	 * Used to register these extended properties for the player during
	 * EntityConstructing event This method is for convenience only; it will
	 * make your code look nicer
	 */
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
	}

	/**
	 * Returns ExtendedPlayer properties for player This method is for
	 * convenience only; it will make your code look nicer
	 */
	public static final ExtendedPlayer get(EntityPlayer player)
	{
		return (ExtendedPlayer)player.getExtendedProperties(EXT_PROP_NAME);
	}

	// Save any custom data that needs saving here
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		// We need to create a new tag compound that will save everything for
		// our Extended Properties
		NBTTagCompound properties = new NBTTagCompound();

		// We only have 2 variables currently; save them both to the new tag
		properties.setInteger("currentHydration", this.getCurrentHydration());
		properties.setInteger("iceHydration", this.getCurrentIce());

		// Read custom inventory from NBT
		this.inventory.readFromNBT(properties);

		/*
		 * Now add our custom tag to the player's tag with a unique name (our
		 * property's name). This will allow you to save multiple types of
		 * properties and distinguish between them. If you only have one type,
		 * it isn't as important, but it will still avoid conflicts between your
		 * tag names and vanilla tag names. For instance, if you add some
		 * "Items" tag, that will conflict with vanilla. Not good. So just use a
		 * unique tag name.
		 */
		compound.setTag(EXT_PROP_NAME, properties);
	}

	// Load whatever data you saved
	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		// Here we fetch the unique tag compound we set for this class of
		// Extended Properties
		NBTTagCompound properties = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);

		// Get our data from the custom tag compound
		this.waterStats.setWaterLevel(properties.getInteger("currentHydration"));
		this.waterStats.setIceLevel(properties.getInteger("iceHydration"));

		// Write custom inventory to NBT
		this.inventory.writeToNBT(properties);
	}

	/*
	 * I personally have yet to find a use for this method. If you know of any,
	 * please let me know and I'll add it in!
	 */
	@Override
	public void init(Entity entity, World world)
	{}

	public int getCurrentHydration()
	{
		return this.waterStats.getWaterLevel();
	}

	public int getCurrentIce()
	{
		return this.waterStats.getIceLevel();
	}

	public int getMaxHydration()
	{
		return MAX_HYDRATION;
	}

	public int getMaxIce()
	{
		return MAX_FROZEN;
	}

	/**
	 * Returns the player's FoodStats object.
	 */
	public WaterStats getWaterStats()
	{
		return this.waterStats;
	}

	public boolean canDrink(boolean par1)
	{
		return (par1 || this.waterStats.needWater()) && !this.player.capabilities.disableDamage;
	}

	/**
	 * Sets current hydration to amount or MAX_HYDRATION, whichever is lesser
	 */
	public void setCurrentHydration(int amount)
	{
		this.waterStats.setWaterLevel(amount);
		this.sync();
	}

	/**
	 * Sets current ice to amount or MAX_FROZEN, whichever is lesser
	 */
	public void setCurrentIce(int amount)
	{
		this.waterStats.setIceLevel(amount);
		this.sync();
	}

	/**
	 * Sends a packet to the client containing information stored on the server
	 * for ExtendedPlayer
	 */
	public final void sync()
	{
		// We only want to send from the server to the client
		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
			DataOutputStream outputStream = new DataOutputStream(bos);

			// We'll write max mana first so when we set current mana client
			// side, it doesn't get set to 0 (see methods below)
			try
			{
				outputStream.writeInt(this.getCurrentHydration());
				outputStream.writeInt(this.getCurrentIce());
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}

			Packet250CustomPayload packet = new Packet250CustomPayload(ModInfo.CHANNEL,
					bos.toByteArray());

			EntityPlayerMP player1 = (EntityPlayerMP)player;
			PacketDispatcher.sendPacketToPlayer(packet, (Player)player1);
		}
	}
}
