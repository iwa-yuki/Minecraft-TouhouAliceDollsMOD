package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import mods.touhou_alice_dolls.EntityAliceDoll;

/**
 * ランダムに周りを見る
 */
public class EntityDollAILookIdle extends EntityDollAIBase
{
    private double lookX;
    private double lookZ;
    private int idleTime = 0;

    public EntityDollAILookIdle(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(2);
    }

    public boolean shouldExecute()
    {
        return this.theDoll.getRNG().nextFloat() < 0.02F;
    }

    public boolean continueExecuting()
    {
        return this.idleTime >= 0;
    }

    public void startExecuting()
    {
        double var1 = (Math.PI * 2D) * this.theDoll.getRNG().nextDouble();
        this.lookX = Math.cos(var1);
        this.lookZ = Math.sin(var1);
        this.idleTime = 20 + this.theDoll.getRNG().nextInt(20);
    }

    public void updateTask()
    {
        --this.idleTime;
        this.theDoll.getLookHelper().setLookPosition(
            this.theDoll.posX + this.lookX,
            this.theDoll.posY + (double)this.theDoll.getEyeHeight(),
            this.theDoll.posZ + this.lookZ,
            10.0F, (float)this.theDoll.getVerticalFaceSpeed());
    }
}
