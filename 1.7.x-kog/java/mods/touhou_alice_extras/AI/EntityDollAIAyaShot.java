package mods.touhou_alice_extras.AI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;
import mods.touhou_alice_core.EntityAliceDoll;
import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.packet.MessageAyaShot;
import mods.touhou_alice_core.packet.PacketHandler;

public class EntityDollAIAyaShot  extends EntityDollAIBase{
    
	private int counter;
	private int interval;
	
	public EntityDollAIAyaShot(EntityAliceDoll doll) {
		super(doll);
		
		counter = 0;
		interval = 20*30;
		
		setMutexBits(0);
	}

	@Override
	public boolean shouldExecute() {
		return theDoll.isEnable();
	}

	@Override
	public void updateTask() {

		if(counter < interval) {
			counter++;
			return;
		}
		
		if(!theWorld.isRemote) {
			EntityPlayer player = theDoll.getOwnerEntity();
			if(player != null && player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				PacketHandler.INSTANCE.sendTo(new MessageAyaShot(theDoll), playerMP);
			}
		}
		
		counter = 0;
	}

}
