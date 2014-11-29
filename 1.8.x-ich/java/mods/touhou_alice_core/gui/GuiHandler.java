package mods.touhou_alice_core.gui;

import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		
		switch(ID)
		{
		case GuiAliceDollInventory.GuiID:
			Entity e = world.getEntityByID(x);
			EntityAliceDoll doll;
			if(e != null && e instanceof EntityAliceDoll){
				doll = (EntityAliceDoll)e;
				return new ContainerAliceDollInventory(player,world,doll);
			}
			break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		
		switch(ID)
		{
		case GuiAliceDollInventory.GuiID:
			Entity e = world.getEntityByID(x);
			EntityAliceDoll doll;
			if(e != null && e instanceof EntityAliceDoll){
				doll = (EntityAliceDoll)e;
				return new GuiAliceDollInventory(player,world,doll);
			}
			break;
		}
		return null;
	}

}
