package soarvivor.lib;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class RegisterKeyBindings
{
	/** Key index for easy handling */
	public static final int						CUSTOM_INV	= 0;

	/** Key descriptions */
	private static final String[]				desc		= { "Limited Inventory" };

	/** Default key values */
	private static final int[]					keyValues	= { Keyboard.KEY_O };

	/**
	 * Maps Keyboard values to Tutorial KeyBinding index values (e.g. CUSTOM_INV
	 * returns KEY_O)
	 */
	public static final Map< Integer, Integer >	svrKeyMap	= new HashMap< Integer, Integer >();

	/**
	 * This will initialise all key bindings; I like to pass in a Configuration
	 * object, but I won't be showing that here. Check the tutorial provided in
	 * the prerequisites section for more advanced KeyBinding methods,
	 * specifically my posts therein.
	 */
	public static void init()
	{
		KeyBinding[] key = new KeyBinding[desc.length];
		boolean[] repeat = new boolean[desc.length];

		for (int i = 0; i < desc.length; ++i)
		{
			key[i] = new KeyBinding(desc[i], keyValues[i]);
			repeat[i] = false;
			svrKeyMap.put(key[i].keyCode, i);
		}

		KeyBindingRegistry.registerKeyBinding(new SvrKeyHandler(key, repeat));
	}
}
