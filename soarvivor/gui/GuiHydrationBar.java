package soarvivor.gui;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import soarvivor.entity.ExtendedPlayer;
import soarvivor.lib.LogHelper;

public class GuiHydrationBar extends Gui
{
	private Minecraft						mc;
	/*
	 * (my added notes:) ResourceLocation takes 2 arguments: your mod id and the
	 * path to your texture file, starting from the folder 'textures/' from
	 * '/src/minecraft/assets/yourmodid/', or you can write it all as a single
	 * argument.
	 * 
	 * The texture file must be 256x256 (or multiples thereof)
	 * 
	 * If you want a texture to test out the tutorial with, I've uploaded the
	 * mana_bar.png to my github page:
	 * https://github.com/coolAlias/Forge_Tutorials/tree/master/textures/gui
	 */
	private static final ResourceLocation	HYDRATION	= new ResourceLocation("soarvivor",
																"textures/gui/hydration.png");

	// private static final ResourceLocation ICONS = new ResourceLocation(
	// "textures/gui/icons.png");

	public GuiHydrationBar(Minecraft mc)
	{
		super();
		// We need this to invoke the render engine.
		this.mc = mc;
	}

	//
	// This event is called by GuiIngameForge during each frame by
	// GuiIngameForge.pre() and GuiIngameForce.post().
	//
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderAirBar(RenderGameOverlayEvent event)
	{
		// Draw the hydration bar before the air bubbles so we can move the air
		// bubbles up in the gui
		if (!event.isCancelable() || event.type != ElementType.AIR) return;

		// Get our extended player properties and assign it locally so we can
		// easily access it
		ExtendedPlayer props = ExtendedPlayer.get(this.mc.thePlayer);

		if (props == null) return;

		// Calculate starting position for the hydration bar and length of the
		int left = event.resolution.getScaledWidth() / 2 + 91;
		int top = event.resolution.getScaledHeight() - GuiIngameForge.right_height;
		int wet_level = props.getCurrentHydration();
		int ice_level = props.getMaxIce() - props.getCurrentIce();

		//LogHelper.log(Level.INFO, "Level : " + wet_level);

		// Get the texture for the hydration bar
		this.mc.getTextureManager().bindTexture(HYDRATION);

		int x, idx, icon_u, icon_v;
		for (int i = 0; i < 10; ++i)
		{
			idx = i * 2 + 1;
			x = left - i * 8 - 9;
			icon_u = icon_v = 0;
			// Calculate wet level
			if (idx < wet_level)
				icon_u = 18;
			else if (idx == wet_level) icon_u = 9;
			// Calculate ice level
			if (idx > ice_level)
				icon_v = 18;
			else if (idx == ice_level) icon_v = 9;
			// Draw bottles
			drawTexturedModalRect(x, top, icon_u, icon_v, 9, 9);
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

		// Restore texture to render air bubbles and move position up
		this.mc.getTextureManager().bindTexture(icons);
		GuiIngameForge.right_height += 10;
	}
}
