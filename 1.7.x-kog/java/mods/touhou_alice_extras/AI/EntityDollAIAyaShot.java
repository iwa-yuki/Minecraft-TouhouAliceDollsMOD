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
    
	public static int shotInterval;
	public static boolean emergencyShot;
	
	private int counter;
	private float prevHealth;
	
	public EntityDollAIAyaShot(EntityAliceDoll doll) {
		super(doll);
		
		counter = 0;
		
		setMutexBits(0);
	}

	@Override
	public boolean shouldExecute() {
		return theDoll.isEntityAlive() && theDoll.isEnable() && (shotInterval > 0);
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		
		prevHealth = theDoll.getHealth();
	}

	@Override
	public void updateTask() {
		
		if(emergencyShot && (prevHealth > theDoll.getHealth())) {
			sendMessageAyaShot();
			counter = 0;
		}
		prevHealth = theDoll.getHealth();

		if(counter < shotInterval * 20) {
			counter++;
			return;
		}
		sendMessageAyaShot();
		
		counter = 0;
	}

	private void sendMessageAyaShot() {

		if(!theWorld.isRemote) {
			EntityPlayer player = theDoll.getOwnerEntity();
			if(player != null && player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				PacketHandler.INSTANCE.sendTo(new MessageAyaShot(theDoll), playerMP);
			}
		}
	}
}
