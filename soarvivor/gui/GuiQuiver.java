package soarvivor.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import soarvivor.entity.ExtendedPlayer;
import soarvivor.inventory.ContainerQuiver;
import soarvivor.inventory.InventoryLimitedPlayer;
import soarvivor.inventory.InventoryQuiver;
import soarvivor.lib.ModInfo;

public class GuiQuiver extends GuiContainer {
    /**
     * ResourceLocation takes 2 parameters: ModId, path to texture at the
     * location: "src/minecraft/assets/modid/"
     * 
     * If you want a working texture to test out the tutorial with, I've
     * uploaded one to my github page:
     * https://github.com/coolAlias/Forge_Tutorials/tree/master/textures/gui
     */
    private static final ResourceLocation guiLocation = new ResourceLocation(
	    ModInfo.ID.toLowerCase(), "textures/gui/quiver.png");
    private static final ResourceLocation iconLocation = new ResourceLocation(
	    ModInfo.ID.toLowerCase(), "textures/gui/inventory.png");

    /** The inventory to render on screen */
    private final InventoryQuiver inventory;
    private final InventoryLimitedPlayer invLtd;

    public GuiQuiver(ContainerQuiver ContainerQuiver, EntityPlayer player) {
	super(ContainerQuiver);
	this.inventory = ContainerQuiver.inventory;
	ExtendedPlayer props = ExtendedPlayer.get(player);
	invLtd = props.ltdInventory;
    }

    /**
     * Draws the screen and all the components in it.
     * 
     * <pre>
     * public void drawScreen(int par1, int par2, float par3) {
     *     super.drawScreen(par1, par2, par3);
     *     this.xSize_lo = (float) par1;
     *     this.ySize_lo = (float) par2;
     * }
     * </pre>
     */

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     * 
     * In this case, it is just the labels.
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
	this.fontRenderer.drawString(
		this.inventory.isInvNameLocalized() ? this.inventory
			.getInvName() : I18n.getString(this.inventory
			.getInvName()), 8, 6, 4210752);
	this.fontRenderer.drawString(I18n.getString("container.inventory"), 8,
		this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
	    int par3) {
	// Draw overall GUI background
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	this.mc.getTextureManager().bindTexture(guiLocation);
	int xPos = this.guiLeft;
	int yPos = this.guiTop;
	this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);

	// Draw background of quiver equip slots
	this.mc.getTextureManager().bindTexture(iconLocation);
	if (invLtd.getStackInSlot(0) == null) {
	    // x = 8, y = 84
	    this.drawTexturedModalRect(xPos + 7, yPos + 83, 162, 234, 18, 18);
	    this.drawTexturedModalRect(xPos + 25, yPos + 83, 144, 234, 18, 18);
	    this.drawTexturedModalRect(xPos + 43, yPos + 83, 144, 234, 18, 18);
	} else {
	    // x = 8, y = 84
	    if (invLtd.getStackInSlot(1) == null)
		this.drawTexturedModalRect(xPos + 25, yPos + 83, 180, 234, 18,
			18);
	    if (invLtd.getStackInSlot(2) == null)
		this.drawTexturedModalRect(xPos + 43, yPos + 83, 180, 234, 18,
			18);
	}
    }
}
