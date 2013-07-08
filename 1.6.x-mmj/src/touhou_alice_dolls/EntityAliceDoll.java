////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package iwa_yuki.touhou_alice_dolls;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.inventory.IInventory;

/**
 * 人形のエンティティクラス
 */
public class EntityAliceDoll extends EntityLiving
{
    public EntityAliceDoll(World world)
    {
        super(world);
    }

    /**
     * エンティティの初期化
     */
    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Integer.valueOf(0x00000210)); // 状態
        this.dataWatcher.addObject(17, ""); // オーナーの名前
        this.dataWatcher.addObject(18, Integer.valueOf(-1)); // Doll ID
        this.dataWatcher.addObject(19, Integer.valueOf(-1)); // Sub Item ID
        this.dataWatcher.addObject(20, Integer.valueOf(-1)); // Sub Item Damage
    }


    ////////////////////////////////////////////////////////////////////////////
    // 持ち主の設定・取得
    
    /**
     * 人形の持ち主かどうか
     * @param entity 判定対象のエンティティ
     * @return 持ち主ならばtrue
     */
    public boolean isOwner(Entity entity)
    {
        // TODO
        return true;
    }
    
    /**
     * 持ち主を設定
     * @param player 持ち主のエンティティ
     */
    public void setOwner(EntityPlayer player)
    {
        setOwnerName(player.username);
    }

    /**
     * 持ち主の名前を設定
     * @param name 持ち主の名前
     */
    public void setOwnerName(String name)
    {
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(17, name);
        }
    }
    
    /**
     * 持ち主の名前を取得
     * @return 持ち主の名前
     */
    public String getOwnerName()
    {
        return this.dataWatcher.getWatchableObjectString(17);
    }

    /**
     * 持ち主のエンティティを取得
     * @return 持ち主のエンティティ
     */
    public EntityPlayer getOwnerEntity()
    {
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }

    ////////////////////////////////////////////////////////////////////////////
    // 動作状態の設定・取得
    
    /**
     * PatrolModeかどうか
     * @return PatrolModeならばtrue
     */
    public boolean isPatrolMode()
    {
        // TODO
        return true;
    }

    /**
     * FollowModeかどうか
     * @return FollowModeならばtrue
     */
    public boolean isFollowMode()
    {
        // TODO
        return true;
    }

    /**
     * StandbyModeかどうか
     * @return StandbyModeならばtrue
     */
    public boolean isStandbyMode()
    {
        // TODO
        return true;
    }

    /**
     * RideonModeかどうか
     * @return RideonModeならばtrue
     */
    public boolean isRideonMode()
    {
        // TODO
        return true;
    }

    /**
     * RideonModeに設定
     */
    public void setRideonMode()
    {
        // TODO
    }

    ////////////////////////////////////////////////////////////////////////////
    // 動作

    /** 落下時の処理用<br />EntityChickenから流用 */
    public float field_70886_e;
    /** 落下時の処理用<br />EntityChickenから流用 */
    public float destPos;
    /** 落下時の処理用<br />EntityChickenから流用 */
    public float field_70884_g;
    /** 落下時の処理用<br />EntityChickenから流用 */
    public float field_70888_h;
    /** 落下時の処理用<br />EntityChickenから流用 */
    public float field_70889_i = 1.0F;

    /**
     * 落下中の処理<br />EntityChickenから流用
     */
    private void updateFallingState()
    {
        this.field_70888_h = this.field_70886_e;
        this.field_70884_g = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);

        if (this.destPos < 0.0F)
        {
            this.destPos = 0.0F;
        }

        if (this.destPos > 1.0F)
        {
            this.destPos = 1.0F;
        }

        if (!this.onGround && this.field_70889_i < 1.0F)
        {
            this.field_70889_i = 1.0F;
        }

        this.field_70889_i = (float)((double)this.field_70889_i * 0.9D);

        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }

        this.field_70886_e += this.field_70889_i * 2.0F;
    }

    /**
     * 落下中に呼ばれる
     */
    protected void fall(float par1)
    {
        // 落下ダメージ無効
    }
    
    /**
     * エンティティの近くへテレポートする
     * @param entity ターゲットとなるエンティティ
     * @param r エンティティからテレポート先までの距離
     */
    public void teleportToEntity(Entity entity, double r)
    {
    }
}
