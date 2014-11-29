package mods.touhou_alice_core.gui;

import org.lwjgl.opengl.GL11;

import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.TouhouAliceCore;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiAliceDollInventory extends GuiContainer {

	public static final int GuiID = 1;
    protected static final ResourceLocation inventoryTexture =
    		new ResourceLocation(TouhouAliceCore.MODID,"textures/gui/inventory.png");
    private IInventory dollInventory;
    private IInventory playerInventory;
    private int inventoryRows;

	public GuiAliceDollInventory(EntityPlayer player, World world,
			EntityAliceDoll doll) {
		super(new ContainerAliceDollInventory(player, world, doll));
		dollInventory = doll;
		playerInventory = player.inventory;
        this.allowUserInput = false;
        short short1 = 222;
        int i = short1 - 108;
        this.inventoryRows = dollInventory.getSizeInventory() / 9;
        this.ySize = i + this.inventoryRows * 18;
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRendererObj.drawString(this.dollInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
    
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(inventoryTexture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(k, l + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);

	}

}
