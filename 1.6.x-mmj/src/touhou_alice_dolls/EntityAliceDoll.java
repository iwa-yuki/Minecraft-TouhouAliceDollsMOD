////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLLog;

import mods.touhou_alice_dolls.dolls.*;

public class EntityAliceDoll extends EntityLiving
{
    private int localDollID;
    private boolean isAIInitialized;
    
    public EntityAliceDoll(World world)
    {
        super(world);

        localDollID = -1;
        isAIInitialized = false;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        this.dataWatcher.addObject(18, Integer.valueOf(-1)); // Doll ID
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if(localDollID != getDollID())
        {
            localDollID = getDollID();
            onChangeDollID();
        }
    }

    protected void onChangeDollID()
    {
        if(!isAIInitialized)
        {
            DollRegistry.onInitializeAI(getDollID());
            isAIInitialized = true;
            FMLLog.info("%s : Doll(%s[%s]).AI is initialized.",
                        (worldObj.isRemote?"R":"S"),
                        DollRegistry.getDollName(getDollID()),
                        entityId);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // データ管理
    
    /** データのセーブ */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("DollID", this.getDollID());
    }

    /** データのロード */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setDollID(par1NBTTagCompound.getInteger("DollID"));
    }

    /** 人形のIDを取得 */
    public int getDollID()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /** 人形のIDを設定 */
    public void setDollID(int id)
    {
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(18, Integer.valueOf(id));
        }
    }
}
