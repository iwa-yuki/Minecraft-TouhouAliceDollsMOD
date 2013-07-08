////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package iwa_yuki.touhou_alice_dolls;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * 持ち主が自分で、PatrolModeまたはFollowModeである人形を選択するクラス<br />
 * ドールコアで呼び出す際の条件として使用する
 */
public class DollSelector implements IEntitySelector
{
    public DollSelector(Entity entityplayer)
    {
        player = entityplayer;
    }
    
    /**
     * 選択条件を定義する
     * @param enttity 対象のエンティティ
     * @return 条件に当てはまればtrue
     */
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

