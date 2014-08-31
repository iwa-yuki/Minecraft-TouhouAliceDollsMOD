package mods.touhou_alice_core.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import mods.touhou_alice_core.EntityAliceDoll;

/**
 * 近くのモブを見る
 */
public class EntityDollAIWatchClosest extends EntityDollAIBase
{
    protected Entity closestEntity;
    private float range;
    private int lookTime;
    private float probability;
    private Class watchedClass;

    public EntityDollAIWatchClosest(EntityAliceDoll doll)
    {
        super(doll);

        this.watchedClass = EntityLiving.class;
        this.range = 8.0F;
        this.probability = 0.02F;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.theDoll.getRNG().nextFloat() >= this.probability)
        {
            return false;
        }

        this.closestEntity = this.theWorld.findNearestEntityWithinAABB(
            this.watchedClass, this.theDoll.boundingBox.expand(
                (double)this.range, 3.0D, (double)this.range), this.theDoll);

        return this.closestEntity != null;
    }

    @Override
    public boolean continueExecuting()
    {
        return !this.closestEntity.isEntityAlive() ? false :
            (this.theDoll.getDistanceSqToEntity(this.closestEntity) > (double)(this.range * this.range) ? false : this.lookTime > 0);
    }

    @Override
    public void startExecuting()
    {
        this.lookTime = 40 + this.theDoll.getRNG().nextInt(40);
    }

    @Override
    public void resetTask()
    {
        this.closestEntity = null;
    }

    @Override
    public void updateTask()
    {
        float offset = 0.0f;
        if(theDoll.isRideonMode() && theDoll.isOwner(this.closestEntity))
        {
            offset = -1.1f;
        }

        this.theDoll.getLookHelper().setLookPosition(
            this.closestEntity.posX,
            this.closestEntity.posY + (double)this.closestEntity.getEyeHeight() + offset,
            this.closestEntity.posZ,
            10.0F, (float)this.theDoll.getVerticalFaceSpeed());
        --this.lookTime;
    }

}
