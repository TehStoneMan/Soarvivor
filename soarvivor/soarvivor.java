/*
 * Soarvivor mod for Minecraft
 * 
 * A mod to implement the rules from Paul Soars Jr's "Man Vs Minecraft" series
 * of YouTube videos.
 * 
 * Features:
 * 
 * Quiver - Craftable from a pair of leather boots and two string.
 * 
 * ================
 * 
 * To Do:
 * 
 * Limit max item/block stack to 16 (player inventory only).
 * 
 * Limit player inventory space to hotbar and one row of inventory.
 * 
 * ================
 */

package soarvivor;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import soarvivor.gui.GuiHydrationBar;
import soarvivor.items.Items;
import soarvivor.items.Recipies;
import soarvivor.lib.LogHelper;
import soarvivor.lib.ModInfo;
import soarvivor.lib.SvrEventHandler;
import soarvivor.lib.SvrKeyHandler;
import soarvivor.lib.config.ConfigHandler;
import soarvivor.proxies.CommonProxy;
import soarvivor.util.DebugInfo;
import soarvivor.util.PacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

// Setup mod info
@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
// Setup mod network channel
@NetworkMod(channels = ModInfo.CHANNEL, clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class)
/**
 * 
 * @author TehStoneMan
 */
public class soarvivor {
    // Create an instance of this mod
    @Instance(value = ModInfo.ID)
    public static soarvivor instance;

    // Define proxies
    @SidedProxy(clientSide = ModInfo.PROXY_LOCATION + "ClientProxy", serverSide = ModInfo.PROXY_LOCATION
	    + "CommonProxy")
    public static CommonProxy proxy;

    // This is used to keep track of GUIs that we make
    private static int modGuiIndex = 0;

    // Set our custom inventory Gui index to the next available Gui index
    public static final int GUI_QUIVER_INV = modGuiIndex++;

    /** Custom GUI indices: */
    public static final int GUI_LIMITED_INV = modGuiIndex++;

    // Perform pre-initialisation operations
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
	LogHelper.init();
	DebugInfo.init();

	LogHelper.log(Level.INFO, "Initialising proxies.");
	proxy.initRenderers();
	proxy.initSounds();

	LogHelper.log(Level.INFO, "Loading configuration.");
	ConfigHandler.init(event.getSuggestedConfigurationFile());

	// register CommonProxy as our GuiHandler
	NetworkRegistry.instance().registerGuiHandler(this, new CommonProxy());
    }

    // Initialise mod
    @EventHandler
    public void Init(FMLInitializationEvent event) {
	LogHelper.log(Level.INFO, "Preparing items.");
	Items.init();
	Items.addNames();
	LogHelper.log(Level.INFO, "Items loaded.");

	// Blocks.init();
	// Blocks.addNames();

	LogHelper.log(Level.INFO, "Preparing recipies.");
	Recipies.init();
	LogHelper.log(Level.INFO, "Recipies loaded.");
    }

    // Perform post-initialisation operations
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
	LogHelper.log(Level.INFO, "Registering event handlers.");
	// Register event handler
	MinecraftForge.EVENT_BUS.register(new SvrEventHandler());

	// Register GUI handler
	NetworkRegistry.instance().registerGuiHandler(this, new CommonProxy());

	if (FMLCommonHandler.instance().getEffectiveSide().isClient())
	    MinecraftForge.EVENT_BUS.register(new GuiHydrationBar(Minecraft
		    .getMinecraft()));

	// Register KeyHandler
	if (FMLCommonHandler.instance().getEffectiveSide().isClient())
	    SvrKeyHandler.init();

	// Register tick handler
	proxy.registerServerTickHandler();
	LogHelper.log(Level.INFO, "Event handlers registered.");
    }
}
