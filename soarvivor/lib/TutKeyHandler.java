package soarvivor.lib;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import soarvivor.inventory.ContainerLimitedPlayer;
import soarvivor.util.PacketHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TutKeyHandler class
@SideOnly(Side.CLIENT)
public class TutKeyHandler extends KeyHandler
{
	/** Not really important. I use it to store/find keys in the config file */
	public static final String	label	= "Tutorial Key";

	public TutKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings)
	{
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel()
	{
		return this.label;
	}

	@Override
	public void keyDown(EnumSet< TickType > types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (tickEnd && RegisterKeyBindings.tutKeyMap.containsKey(kb.keyCode))
		{
			EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;

			switch (RegisterKeyBindings.tutKeyMap.get(kb.keyCode))
			{
				case RegisterKeyBindings.CUSTOM_INV:
					// If the custom inventory screen is open, close it
					if (player.openContainer != null
							&& player.openContainer instanceof ContainerLimitedPlayer)
						player.closeScreen();

					// Otherwise, open the screen. Here you will need to send a
					// packet to the server telling it
					// to open the corresponding server gui element, or your
					// inventory won't function
					else if (FMLClientHandler.instance().getClient().inGameHasFocus)
					{
						// Send a packet to the server using a method we'll
						// create in the next step
						PacketHandler.sendOpenGuiPacket(soarvivor.soarvivor.GUI_CUSTOM_INV);
						// opening the gui server side automatically opens the
						// client side as well,
						// so we don't need to do anything else
					}
					break;
			}
		}
	}

	@Override
	public void keyUp(EnumSet< TickType > types, KeyBinding kb, boolean tickEnd)
	{
		// Don't need to do anything here!
	}

	@Override
	public EnumSet< TickType > ticks()
	{
		// We're only interested in player ticks, as that's when the keyboard
		// will fire
		return EnumSet.of(TickType.PLAYER);
	}
}