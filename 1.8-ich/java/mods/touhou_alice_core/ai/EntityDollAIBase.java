package mods.touhou_alice_core.ai;

import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityDollAIBase extends EntityAIBase {

	protected EntityAliceDoll theDoll;
    protected World theWorld;

    public EntityDollAIBase(EntityAliceDoll doll)
    {
        theDoll = doll;
        theWorld = doll.worldObj;
    }

	@Override
	public boolean shouldExecute() {
		return false;
	}
	
	@Override
	public void startExecuting() {
		theDoll.chatMessage(theDoll.getDollName()+" : "+getAIName()+".start", 3);
	}

	@Override
	public void resetTask() {
		theDoll.chatMessage(theDoll.getDollName()+" : "+getAIName()+".reset", 3);
	}
    
    public String getAIName() {
    	return this.getClass().getSimpleName();
    }

}
