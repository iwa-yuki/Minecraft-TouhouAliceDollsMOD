package mods.touhou_alice_dolls;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class DollSelector implements IEntitySelector
{
    public DollSelector(Entity entityplayer)
    {
        player = entityplayer;
    }
    
    public boolean isEntityApplicable(Entity entity)
    {
        if(entity instanceof EntityAliceDoll)
        {
            EntityAliceDoll doll = (EntityAliceDoll)entity;

            return doll.isOwner(player) && (doll.isPatrolMode() || doll.isFollowMode());
        }

        return false;
    }

    private Entity player;
}

