package soarvivor.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.IExtendedEntityProperties;
import soarvivor.inventory.InventoryLimitedPlayer;
import soarvivor.lib.LogHelper;
import soarvivor.lib.ModInfo;
import soarvivor.lib.config.Settings;
import soarvivor.util.DebugInfo;
import soarvivor.util.PacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * Adds a set of extended properties to track player hydration
 * 
 * @author TehStoneMan
 * 
 */
public class ExtendedPlayer implements IExtendedEntityProperties {
    /*
     * Create a constant to store the unique name for this instance of
     * IExtendedEntityProperties
     */
    public final static String EXT_PROP_NAME = "SRVRExtendedPlayer";

    private static final int HOTBAR_START = 0;
    private static final int HOTBAR_END = HOTBAR_START + 8;
    private static final int INV_START = HOTBAR_END + 19;
    private static final int INV_END = INV_START + 8;

    // Store a reference to the entity to which the properties belong for easy
    // access. It's final because we won't be changing which player it is
    private final EntityPlayer player;

    // Define the maximum hydration (WET) and ice levels.
    public static final int MAX_WET_LEVEL = 20;
    public static final int MAX_ICE_LEVEL = 20;

    // The player's water and ice levels.
    private int wetLevel = ExtendedPlayer.MAX_WET_LEVEL;
    private int iceLevel = 0;

    // The player's water saturation.
    private float waterSaturationLevel = 5.0F;

    // The player's water exhaustion.
    private float waterExhaustionLevel;
    private float prevFoodExhaustionLevel;

    // The player's water timer value.
    private int waterTimer, iceTimer;
    private int prevWetLevel = wetLevel;
    private int prevIceLevel = iceLevel;

    // Timer for calculating environmental effects
    private int envTimer;

    /** The player's water stats. (See class WaterStats) */
    // protected WaterStats waterStats = new WaterStats();

    /** Custom inventory slots will be stored here - be sure to save to NBT! */
    public final InventoryLimitedPlayer ltdInventory = new InventoryLimitedPlayer();

    /**
     * The default constructor takes no arguments, but I put in the Entity so I
     * can initialise the above variable 'player'
     * 
     * Also, it's best to initialise any other variables you may have added,
     * just like in any constructor.
     * 
     * @param player
     */
    public ExtendedPlayer(EntityPlayer player) {
	this.player = player;
	// Start with max hydration and zero ice.
	// this.currentWater = MAX_WET_LEVEL / 2;
	// this.currentIce = 0;

	// this.player.getDataWatcher().addObject(WET_WATCHER, MAX_HYDRATION);
	// this.player.getDataWatcher().addObject(ICE_WATCHER, 0);
    }

    /**
     * Used to register these extended properties for the player during
     * EntityConstructing event This method is for convenience only; it will
     * make your code look nicer
     * 
     * @param player
     */
    public static final void register(EntityPlayer player) {
	player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME,
		new ExtendedPlayer(player));
    }

    /**
     * Returns ExtendedPlayer properties for player This method is for
     * convenience only; it will make your code look nicer
     * 
     * @param player
     * @return ExtendedPlayer
     */
    public static final ExtendedPlayer get(EntityPlayer player) {
	return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
    }

    /*
     * I personally have yet to find a use for this method. If you know of any,
     * please let me know and I'll add it in!
     */
    @Override
    public void init(Entity entity, World world) {
    }

    /**
     * Sends a packet to the client containing information stored on the server
     * for ExtendedPlayer
     */
    public final void sync() {

	// We only want to send from the server to the client
	if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	    DataOutputStream outputStream = new DataOutputStream(bos);

	    try {
		outputStream.write(PacketHandler.EXTENDED_PROPERTIES);
		outputStream.writeInt(this.getCurrentHydration());
		outputStream.writeInt(this.getCurrentIce());
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }

	    Packet250CustomPayload packet = new Packet250CustomPayload(
		    ModInfo.CHANNEL, bos.toByteArray());

	    EntityPlayerMP player1 = (EntityPlayerMP) player;
	    PacketDispatcher.sendPacketToPlayer(packet, (Player) player1);
	}
    }

    // Handle water stats
    public int getCurrentHydration() {
	// return this.currentWater;
	return this.wetLevel;
    }

    public int getCurrentIce() {
	return this.iceLevel;
    }

    public int getMaxHydration() {
	return MAX_WET_LEVEL;
    }

    public int getMaxIce() {
	return MAX_ICE_LEVEL;
    }

    public float getSaturationLevel() {
	return waterSaturationLevel;
    }

    /**
     * Sets current hydration to amount or MAX_HYDRATION, whichever is lesser
     */
    public void setCurrentHydration(int amount) {
	this.wetLevel = Math.min(amount, MAX_WET_LEVEL);
	DebugInfo.wetLevel = wetLevel;
	this.sync();
    }

    /**
     * Sets current ice to amount or MAX_FROZEN, whichever is lesser
     */
    public void setCurrentIce(int amount) {
	this.iceLevel = Math.min(amount, MAX_ICE_LEVEL);
	DebugInfo.iceLevel = iceLevel;
	this.sync();
    }

    public void addWaterStats(int wet, int ice, float saturation) {
	wetLevel = Math.max(
		Math.min(wet + wetLevel, ExtendedPlayer.MAX_WET_LEVEL), 0);
	iceLevel = Math.max(
		Math.min(ice + iceLevel, ExtendedPlayer.MAX_ICE_LEVEL), 0);

	waterSaturationLevel = Math.min(waterSaturationLevel + (float) wet
		* saturation * 2.0F, (float) wetLevel);

	// update debug info
	DebugInfo.wetLevel = wetLevel;
	DebugInfo.iceLevel = iceLevel;
	DebugInfo.waterSaturationLevel = waterSaturationLevel;

	sync();
    }

    /**
     * Increases exhaustion level by supplied amount
     */
    public void addWaterExhaustion(float amount) {
	if (!this.player.capabilities.disableDamage) {
	    if (!this.player.worldObj.isRemote) {
		this.waterExhaustionLevel = Math.min(this.waterExhaustionLevel
			+ amount, 40.0F);

		// Update debug info
		DebugInfo.waterExhaustionLevel = this.waterExhaustionLevel;
	    }
	}
    }

    /**
     * Handles the hydration game logic. Mostly copied from vanilla FoodStats.
     */
    public void onUpdate() {
	int difficulty = player.worldObj.difficultySetting;
	this.prevWetLevel = this.wetLevel;
	this.prevIceLevel = this.iceLevel;
	World world = player.worldObj;

	/*
	 * Get food exhaustion level and calculate difference from previous
	 * recorded level. This is a private value, so we have to use reflection
	 * to read it.
	 */
	float foodDif = 0f;
	FoodStats stats = player.getFoodStats();
	Class c = stats.getClass();

	// Function name depends on weather we are in a development
	// (deobfuscated) or compiled (obfuscated) environment
	String f_name;
	if (FMLForgePlugin.RUNTIME_DEOBF) {
	    f_name = "field_75126_c";
	} else {
	    f_name = "foodExhaustionLevel";
	}
	;
	Field f = ReflectionHelper.findField(stats.getClass(), f_name);
	try {
	    float fExh = f.getFloat(stats);
	    DebugInfo.foodExhuastion = fExh;
	    foodDif = Math.max(fExh - prevFoodExhaustionLevel, 0f);
	    prevFoodExhaustionLevel = fExh;
	    DebugInfo.misc = foodDif;
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	}
	// Add difference to water exhaustion level
	this.addWaterExhaustion(foodDif);

	if (++envTimer >= 100) {
	    // Get world and biome info
	    BiomeGenBase biome = world.getBiomeGenForCoords(
		    (int) player.lastTickPosX, (int) player.lastTickPosZ);
	    float temperature = biome.getFloatTemperature();

	    // Scan surounding blocks for heat sources
	    float heat = 0f;
	    int foundBlock;
	    float distance = 1f;
	    for (int y = -5; y < 5; y++) {
		for (int z = -5; z < 5; z++) {
		    for (int x = -5; x < 5; x++) {
			int findX = (int) player.lastTickPosX + x;
			int findY = (int) player.lastTickPosY + y;
			int findZ = (int) player.lastTickPosZ + z;
			foundBlock = world.getBlockId(findX, findY, findZ);
			float foundDistance = 1f - (getDistance(x, y, z) / 9f);
			if (foundBlock == Block.fire.blockID) {
			    heat += 2f * foundDistance;
			    if (foundDistance < distance)
				distance = foundDistance;
			}
			if (foundBlock == Block.lavaMoving.blockID) {
			    heat += foundDistance;
			    if (foundDistance < distance)
				distance = foundDistance;
			}
			if (foundBlock == Block.lavaStill.blockID) {
			    heat += foundDistance;
			    if (foundDistance < distance)
				distance = foundDistance;
			}
		    }
		}
	    }
	    heat = Math.min(heat, 2f);

	    // Update debug info
	    DebugInfo.biomeTemperature = temperature;
	    DebugInfo.heat = heat;
	    DebugInfo.distance = distance;
	    // DebugInfo.distance = getDistance(5, 5, 5) / 9f;

	    // Test for hot biome
	    if (Math.max(temperature, heat) >= 1.0) {
		this.addWaterExhaustion(Math.max(temperature, heat));
		addWaterStats(0, -1, 0);
	    }

	    // Test for cold biome
	    if (biome.getEnableSnow()) {
		if (++iceTimer > 2) {
		    if (heat <= 0)
			addWaterStats(0, 1, 0);
		    if (heat >= 1)
			addWaterStats(0, -1, 0);
		    iceTimer = 0;
		}
	    } else {
		addWaterStats(0, -1, 0);
		iceTimer = 0;
	    }

	    // Reset timer
	    envTimer = 0;
	}

	if (this.waterExhaustionLevel > 4.0F) {
	    this.waterExhaustionLevel -= 4.0F;

	    if (this.waterSaturationLevel > 0.0F) {
		this.waterSaturationLevel = Math.max(
			this.waterSaturationLevel - 0.5F, 0.0F);
	    } else if (difficulty > 0) {
		this.wetLevel = Math.max(this.wetLevel - 1, 0);

		sync();
	    }
	}

	// Check game rules to see if auto healing is enabled
	if (world.getGameRules().getGameRuleBooleanValue("naturalRegeneration")
		&& this.wetLevel >= 18 && player.shouldHeal()
		&& this.iceLevel <= 2) {
	    // Test to heal player
	    ++this.waterTimer;

	    if (this.waterTimer >= 80) {
		player.heal(0.5F);
		this.addWaterExhaustion(3.0F);
		this.waterTimer = 0;
	    }
	} else if (this.wetLevel <= 0 || this.iceLevel >= 18) {
	    // Test to hurt player
	    ++this.waterTimer;

	    if (this.waterTimer >= 80) {
		if (player.getHealth() > 10.0F || difficulty >= 3
			|| player.getHealth() > 1.0F && difficulty >= 2) {
		    if (this.wetLevel <= 0)
			player.attackEntityFrom(DamageSource.starve, 0.5F);
		    if (this.iceLevel >= 18)
			player.attackEntityFrom(DamageSource.starve, 2.0F);
		}

		this.waterTimer = 0;
	    }
	} else
	    this.waterTimer = 0;

	// update debug info
	DebugInfo.waterTimer = waterTimer;
	DebugInfo.waterSaturationLevel = waterSaturationLevel;
	DebugInfo.envTimer = envTimer;
	DebugInfo.waterExhaustionLevel = this.waterExhaustionLevel;
	DebugInfo.wetLevel = wetLevel;
	DebugInfo.iceLevel = iceLevel;
    }

    /**
     * Gets the distance to an offset.
     */
    public float getDistance(double dx, double dy, double dz) {
	return MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    public boolean addItemStackToInventory(ItemStack stackSource) {
	// Check if player is in creative mode, and do vanilla processing if so
	if (this.player.capabilities.isCreativeMode)
	    return player.inventory.addItemStackToInventory(stackSource);

	if (stackSource == null)
	    return false;
	else if (stackSource.stackSize == 0)
	    return false;
	else {
	    // Loop through inventory, adding items to available slots
	    int startSize = stackSource.stackSize;
	    int maxStackSize = Math.min(Settings.limitStackSize,
		    stackSource.getMaxStackSize());
	    for (int count = 0; count < player.inventory.mainInventory.length; ++count) {
		// Ignore inventory slots outside our "Limited Inventory" space
		if (stackSource.stackSize > 0
			&& (count <= HOTBAR_END || count >= INV_START)) {
		    ItemStack stackTarget = player.inventory.mainInventory[count];

		    if (stackTarget == null) {
			// Target slot is empty - Move as many items into it as
			// it can hold
			player.inventory.mainInventory[count] = ItemStack
				.copyItemStack(stackSource);
			player.inventory.mainInventory[count].stackSize = Math
				.min(maxStackSize, stackSource.stackSize);
			player.inventory.mainInventory[count].animationsToGo = 5;
			stackSource.stackSize -= player.inventory.mainInventory[count].stackSize;
		    } else if (stackTarget.itemID == stackSource.itemID
			    && stackTarget.isStackable()
			    && stackTarget.stackSize < maxStackSize
			    && (stackTarget.getHasSubtypes() || stackTarget
				    .getItemDamage() == stackSource
				    .getItemDamage())
			    && ItemStack.areItemStackTagsEqual(stackTarget,
				    stackSource)) {
			// Target slot holds the same item as we are picking up
			if (stackTarget.stackSize + stackSource.stackSize <= maxStackSize) {
			    stackTarget.stackSize += stackSource.stackSize;
			    player.inventory.mainInventory[count].animationsToGo = 5;
			    stackSource.stackSize = 0;
			} else {
			    int diff = maxStackSize - stackTarget.stackSize;
			    stackTarget.stackSize = maxStackSize;
			    player.inventory.mainInventory[count].animationsToGo = 5;
			    stackSource.stackSize -= diff;
			}
		    }
		}
	    }
	    return stackSource.stackSize < startSize;
	}
    }

    // Save any custom data that needs saving here
    @Override
    public void saveNBTData(NBTTagCompound compound) {
	// We need to create a new tag compound that will save everything for
	// our Extended Properties
	NBTTagCompound properties = new NBTTagCompound();

	// We only have 2 variables currently; save them both to the new tag
	properties.setInteger("currentHydration", this.getCurrentHydration());
	properties.setInteger("currentIce", this.getCurrentIce());

	properties.setFloat("waterSaturationLevel", this.waterSaturationLevel);

	// The player's water exhaustion.
	properties.setFloat("waterExhaustionLevel", this.waterExhaustionLevel);
	properties.setFloat("prevFoodExhaustionLevel",
		this.prevFoodExhaustionLevel);

	// The player's water timer value.
	properties.setInteger("waterTimer", this.waterTimer);
	properties.setInteger("iceTimer", this.iceTimer);
	properties.setInteger("prevWetLevel", this.prevWetLevel);
	properties.setInteger("prevIceLevel", this.prevIceLevel);

	// Timer for calculating environmental effects
	properties.setInteger("envTimer", this.envTimer);

	// Save custom inventory from NBT
	this.ltdInventory.saveNBTData(properties);

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
    public void loadNBTData(NBTTagCompound compound) {
	// Here we fetch the unique tag compound we set for this class of
	// Extended Properties
	NBTTagCompound properties = (NBTTagCompound) compound
		.getTag(EXT_PROP_NAME);

	// Get our data from the custom tag compound
	this.wetLevel = properties.getInteger("currentHydration");
	this.iceLevel = properties.getInteger("currentIce");

	// The player's water exhaustion.
	this.waterExhaustionLevel = properties.getFloat("waterExhaustionLevel");
	this.prevFoodExhaustionLevel = properties
		.getFloat("prevFoodExhaustionLevel");

	// The player's water timer value.
	this.waterTimer = properties.getInteger("waterTimer");
	this.iceTimer = properties.getInteger("iceTimer");
	this.prevWetLevel = properties.getInteger("prevWetLevel");
	this.prevIceLevel = properties.getInteger("prevIceLevel");

	// Timer for calculating environmental effects
	this.envTimer = properties.getInteger("envTimer");

	// Write custom inventory to NBT
	this.ltdInventory.loadNBTData(properties);

	sync();
    }
}