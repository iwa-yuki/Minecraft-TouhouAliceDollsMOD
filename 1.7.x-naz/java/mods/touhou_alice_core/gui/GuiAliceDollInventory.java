package mods.touhou_alice_core.gui;

import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class GuiAliceDollInventory extends GuiContainer {

	public static final int GuiID = 1;
	
	public GuiAliceDollInventory(Container par1Container) {
		super(par1Container);
		// TODO Auto-generated constructor stub
	}

	public GuiAliceDollInventory(EntityPlayer player, World world,
			EntityAliceDoll doll) {
		// TODO Auto-generated constructor stub
		super(new ContainerAliceDollInventory(player, world, doll));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		// TODO Auto-generated method stub

	}

}
