package soarvivor.lib;

import java.util.EnumSet;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import soarvivor.inventory.ContainerLimitedPlayer;
import soarvivor.util.PacketHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Key handler for Soarvivor mod
 * 
 * @author TehStoneMan
 */
@SideOnly(Side.CLIENT)
public class SvrKeyHandler extends KeyHandler
{
	private EnumSet						tickTypes		= EnumSet.of(TickType.CLIENT);

	public static final byte			KEY_INVENTORY	= 0;

	public static final String[]		desc			=
														{ "Limited Inventory" };

	private static final int[]			keyValues		=
														{ Keyboard.KEY_I };

	public static final KeyBinding[]	keys			= new KeyBinding[desc.length];

	public static final void init()
	{
		boolean[] repeat = new boolean[desc.length];
		for (int i = 0; i < desc.length; ++i)
		{
			keys[i] = new KeyBinding(desc[i], keyValues[i]);
			repeat[i] = false;
		}

		KeyBindingRegistry.registerKeyBinding(new SvrKeyHandler(keys, repeat));
	}

	public SvrKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings)
	{
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel()
	{
		return "Soarvivor Key";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (tickEnd)
		{
			EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;

			if (kb.keyCode == keys[KEY_INVENTORY].keyCode)
			{
				// If the custom inventory screen is open, close it
				if (player.openContainer != null
						&& player.openContainer instanceof ContainerLimitedPlayer) player
						.closeScreen();

				// Otherwise, open the screen. Here you will need to send a
				// packet to the server telling it
				// to open the corresponding server gui element, or your
				// inventory won't function
				else if (FMLClientHandler.instance().getClient().inGameHasFocus)
				{
					// Send a packet to the server using a method we'll
					// create in the next step
					PacketHandler.sendOpenGuiPacket(soarvivor.soarvivor.GUI_LIMITED_INV);
					// opening the gui server side automatically opens the
					// client side as well,
					// so we don't need to do anything else
				}
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
	{
		// Don't need to do anything here!
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return tickTypes;
	}
}
