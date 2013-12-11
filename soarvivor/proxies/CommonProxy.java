package soarvivor.proxies;

import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.gui.GuiLimitedInventory;
import soarvivor.gui.GuiQuiver;
import soarvivor.inventory.ContainerLimitedPlayer;
import soarvivor.inventory.ContainerQuiver;
import soarvivor.inventory.InventoryQuiver;
import soarvivor.lib.LogHelper;
import soarvivor.lib.PlayerTickHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler
{
	public void initRenderers()
	{}

	public void initSounds()
	{}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y,
			int z)
	{
		// Hooray, no 'magic' numbers - we know exactly which Gui this refers to
		if (guiId == soarvivor.soarvivor.InventoryQuiverGuiIndex)
		{
			// Use the player's held item to create the inventory
			return new ContainerQuiver(player, player.inventory, new InventoryQuiver(
					player.getHeldItem()));
		}
		// if (guiId == soarvivor.soarvivor.GUI_CUSTOM_INV)
		// return new ContainerLimitedPlayer(player, player.inventory,
		// ExtendedPlayer.get(player).inventory);
		return null;
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y,
			int z)
	{
		if (guiId == soarvivor.soarvivor.InventoryQuiverGuiIndex)
		{
			// We have to cast the new container as our custom class
			// and pass in currently held item for the inventory
			return new GuiQuiver((ContainerQuiver)new ContainerQuiver(player, player.inventory,
					new InventoryQuiver(player.getHeldItem())));
		}
		// if (guiId == soarvivor.soarvivor.GUI_CUSTOM_INV)
		// return new ContainerLimitedPlayer(player, player.inventory,
		// ExtendedPlayer.get(player).inventory);
		return null;
	}

	public void registerServerTickHandler()
	{
		LogHelper.log(Level.INFO, "Tick Test!");
		TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.SERVER);
	}
}
