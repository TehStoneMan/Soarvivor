package soarvivor.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.util.DebugInfo;

/**
 * Draw the custom Soarvivor mod GUI elements on the screen
 * 
 * @author TehStoneMan
 */
public class GuiHydrationBar extends Gui
{
	// Get the current Minecraft session
	private Minecraft						mc;
	/*
	 * Get the texture file for the GUI elements
	 * 
	 * The texture file must be 256x256 (or multiples thereof)
	 */
	private static final ResourceLocation	HYDRATION	= new ResourceLocation("soarvivor",
																"textures/gui/hydration.png");

	/**
	 * Hydration GUI constructor
	 * 
	 * @param mc
	 */
	public GuiHydrationBar(Minecraft mc)
	{
		super();
		// We need this to invoke the render engine.
		this.mc = mc;
	}

	/**
	 * Draw the player's hydration bar on the screen
	 * 
	 * @param event
	 */
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderAirBar(RenderGameOverlayEvent event)
	{
		// Draw the hydration bar before the air bubbles so we can move the air
		// bubbles up in the gui
		if (!event.isCancelable() || event.type != ElementType.AIR) return;

		// Get our extended player properties and assign it locally so we can
		// easily access it
		EntityPlayer player = this.mc.thePlayer;
		ExtendedPlayer props = ExtendedPlayer.get(player);

		if (props == null) return;

		// Calculate starting position and length of for the hydration bar
		int left = event.resolution.getScaledWidth() / 2 + 91;
		int top = event.resolution.getScaledHeight() - GuiIngameForge.right_height;
		int wet_level = props.getCurrentHydration();
		int ice_level = props.getMaxIce() - props.getCurrentIce();

		// Get the texture for the hydration bar
		this.mc.getTextureManager().bindTexture(HYDRATION);

		/**
		 * Draw our hydration bar on the GUI
		 * 
		 * <pre>
		 * x = icon position on screen
		 * idx = value to compare with water stats
		 * icon_u = icon position in texture file (water lavel)
		 * icon_v = icon position in texture file (ice level)
		 * </pre>
		 */
		int x, y, idx, icon_u, icon_v;
		for (int i = 0; i < 10; ++i)
		{
			idx = i * 2 + 1;
			x = left - i * 8 - 9;
			y = top;
			icon_u = icon_v = 0;

			// Calculate water level icon
			if (idx < wet_level)
				icon_u = 18;
			else if (idx == wet_level) icon_u = 9;

			// Calculate ice level icon
			if (idx > ice_level)
				icon_v = 18;
			else if (idx == ice_level) icon_v = 9;

			// if (props.getSaturationLevel() <= 0.0F && updateCounter % (level
			// * 3 + 1) == 0)
			// {
			// y = top + (rand.nextInt(3) - 1);
			// }

			// Draw water bottle icons in GUI
			drawTexturedModalRect(x, y, icon_u, icon_v, 9, 9);
		}

		/**
		 * The parameters for drawTexturedModalRect are as follows:
		 * 
		 * <pre>
		 * 	drawTexturedModalRect(int x, int y, int u, int v, int width, int height);
		 * </pre>
		 * 
		 * x and y are the on-screen position at which to render. u and v are
		 * the coordinates of the most upper-left pixel in your texture file
		 * from which to start drawing. width and height are how many pixels to
		 * render from the start point (u, v)
		 */

		// draw debug info
		//FontRenderer fontRender = mc.fontRenderer;
		//DebugInfo.display(fontRender);

		// Restore texture to render air bubbles and move position up
		this.mc.getTextureManager().bindTexture(icons);
		GuiIngameForge.right_height += 10;

	}
}
