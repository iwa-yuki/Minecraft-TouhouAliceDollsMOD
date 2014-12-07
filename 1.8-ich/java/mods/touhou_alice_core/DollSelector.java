package mods.touhou_alice_core;

import com.google.common.base.Predicate;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * パトロールモード、フォローモードの人形を抽出するためのセレクタ
 */ 
public class DollSelector implements Predicate
{
    public DollSelector(Entity entityplayer)
    {
        player = entityplayer;
    }

	@Override
	public boolean apply(Object input)
	{
        if(input instanceof EntityAliceDoll)
        {
            EntityAliceDoll doll = (EntityAliceDoll)input;

            return doll.isOwner(player) && (doll.isPatrolMode() || doll.isFollowMode());
        }

        return false;
	}
	
    private Entity player;

}

