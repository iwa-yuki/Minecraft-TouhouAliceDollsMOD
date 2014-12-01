package mods.touhou_alice_core;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import mods.touhou_alice_core.ai.EntityDollAIBase;
import mods.touhou_alice_core.doll.DollRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityAliceDoll extends EntityLiving {
	
	private int localDollID = -1;

	public EntityAliceDoll(World worldIn) {
		super(worldIn);
		
		setDollID(0);
	}
	
    @Override
    protected void entityInit()
    {
        super.entityInit();

        initDataWatcher();
    }

    
	///////////////////////////////////////////////////////////////////////////
	// 初期化
    
    /** 人形IDが変更されたときに呼ばれる */
    private void onChangeDollID() {
		// TODO Auto-generated method stub
	
	}
    
	///////////////////////////////////////////////////////////////////////////
	// AI
    private List localTasks = Lists.newArrayList();
    
    /** AIを登録する */
    public void addAI(int index, EntityDollAIBase ai)
    {
        this.tasks.addTask(index, ai);
        localTasks.add(ai);
    }
    
    /** AIをすべて削除する */
    public void clearAI() {
    	for (Iterator i = localTasks.iterator(); i.hasNext();) {
    		this.tasks.removeTask((EntityAIBase)(i.next()));
    	}
    }
    
	///////////////////////////////////////////////////////////////////////////
	// データ管理
	
    /** データ登録 */
    private void initDataWatcher()
    {
        this.dataWatcher.addObject(16, Integer.valueOf(0x00000210)); // 状態
        this.dataWatcher.addObject(17, ""); // オーナーの名前
        this.dataWatcher.addObject(18, Integer.valueOf(0)); // Doll ID
    }
    
    /** データのセーブ */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("DollID", this.getDollID());
        par1NBTTagCompound.setString("Owner", this.getOwnerName());
        par1NBTTagCompound.setInteger("State", this.getStateBits());
        
        // インベントリ内のアイテムのセーブ
        //writeInventoryToNBT(par1NBTTagCompound);
    }

    /** データのロード */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setDollID(par1NBTTagCompound.getInteger("DollID"));
        this.setOwnerName(par1NBTTagCompound.getString("Owner"));
        this.setStateBits(par1NBTTagCompound.getInteger("State"));
        
        // インベントリ内のアイテムのロード
        //readInventoryFromNBT(par1NBTTagCompound);
    }

    /** 人形のIDを取得 */
	public int getDollID() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	/** 人形のIDを設定 */
	public void setDollID(int id) {
		if(id == localDollID) {
			return;
		}
		localDollID = id;
		onChangeDollID();
		
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(18, Integer.valueOf(id));
        }
	}

	/** 人形の名前を取得 <br /> NameTagで名前が付けられている場合はそちらを優先する */
    public String getDollName()
    {
        return this.hasCustomName() ? this.getCustomNameTag() : DollRegistry.getDollName(getDollID());
    }

    /** 持ち主の名前を設定 */
    public void setOwnerName(String name)
    {
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(17, name);
        }
    }

    /** 持ち主を設定 */
    public void setOwner(EntityPlayer entity)
    {
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(17, entity.getDisplayName());
        }
    }
    
    /** 持ち主の名前を取得 */
    public String getOwnerName()
    {
        return this.dataWatcher.getWatchableObjectString(17);
    }

    /** 持ち主のエンティティを取得 */
    public EntityPlayer getOwnerEntity()
    {
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }

    /** 持ち主かどうか */
	public boolean isOwner(Entity entity)
	{
        if(entity == null)
        {
            return false;
        }
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            return entityplayer.getDisplayNameString().equalsIgnoreCase(getOwnerName());
        }
        return false;
	}
    
    /** 状態の取得 */
    public int getStateBits()
    {
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    /** 状態の設定 */
 	public void setStateBits(int state)
	{
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(16, state);
        }
	}
    
    /** 動作モード変更 */
    public void toggleMode()
    {
        if(isStandbyMode())
        {
            setPatrolMode();
        }
        else if(isPatrolMode())
        {
            setFollowMode();
        }
        else if(isFollowMode())
        {
            setRideonMode();
        }
        else if(isRideonMode())
        {
            setStandbyMode();
        }
    }

    /** スタンバイモードかどうか */
    public boolean isStandbyMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000000;
    }

    /** スタンバイモードに設定 */
    public void setStandbyMode()
    {
        if(!worldObj.isRemote)
        {
            mountEntity(null);
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000000);
            chatMessage(getDollName()+" : Standby mode", 2);
            playSound("random.click", 0.3F, 0.6F);
        }
    }

    /** パトロールモードかどうか */
    public boolean isPatrolMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000001;
    }

    /** パトロールモードに設定 */
    public void setPatrolMode()
    {
        spawnParticle(EnumParticleTypes.NOTE);
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000001);
            chatMessage(getDollName()+" : Patrol mode", 2);
            playSound("random.click", 0.3F, 0.6F);
        }        
    }

    /** フォローモードかどうか */
    public boolean isFollowMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000002;
    }

    /** フォローモードに設定 */
    public void setFollowMode()
    {
        spawnParticle(EnumParticleTypes.HEART);
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000002);
            chatMessage(getDollName()+" : Follow mode", 2);
            playSound("random.click", 0.3F, 0.6F);
        }
    }

    /** ライドオンモードかどうか */
    public boolean isRideonMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000003;
    }

    /** ライドオンモードに設定 */
    public void setRideonMode()
    {
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000003);
            if(!isOwner(ridingEntity))
            {
                EntityPlayer owner = getOwnerEntity();
                if(owner != null && owner.riddenByEntity != null)
                {
                    owner.riddenByEntity.mountEntity(null);
                }
                this.mountEntity(owner);
            }
            chatMessage(getDollName()+" : Rideon mode", 2);
            playSound("random.click", 0.3F, 0.6F);
        }
    }

    /** インベントリが開かれているか取得する */
    public boolean isGUIOpened()
    {
        return (getStateBits() & 0x00000008) == 0x00000008;
    }

    /** インベントリが開かれているか設定する */
    public void setGUIOpened(boolean isOpened)
    {
        int state = getStateBits();
        setStateBits((state & 0xfffffff7) | (isOpened ? 0x00000008 : 0x00000000));
    }

    /** チャット出力レベルの変更 */
    public void toggleChatLevel()
    {
        setChatLevel((getChatLevel() + 1) % 4);
    }

    /** チャット出力レベルを取得 */
    public int getChatLevel()
    {
        int state = getStateBits();
        return (int)((state & 0x00000f00) >> 8);
    }

    /** チャット出力レベルの設定 */
    public void setChatLevel(int level)
    {
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffff0ff) | (level << 8));
            chatMessage(getDollName()+" : setChatLevel("+level+")", 0);
            playSound("random.click", 0.3F, 0.6F);
        }
    }
    
    /** 機能のオンオフ切り替え */
    public void toggleEnable()
    {
        setEnable(!isEnable());
    }

    /** 機能のオンオフを設定 */
    public void setEnable(boolean enable)
    {
        if(!worldObj.isRemote)
        {
            if(enable)
            {
                int state = getStateBits();
                setStateBits((state & 0xffffff8f) | 0x00000010);
                chatMessage(getDollName()+" : Work AI ON", 2);
                playSound("note.harp", 1.0F, (float)Math.pow(2D, (double)(23 - 12) / 12D));
            }
            else
            {
                int state = getStateBits();
                setStateBits(state & 0xffffff8f);
                chatMessage(getDollName()+" : Work AI OFF", 2);
                playSound("note.harp", 1.0F, (float)Math.pow(2D, (double)(11 - 12) / 12D));
            }
        }
    }

    /** 機能がオンかどうか */
    public boolean isEnable()
    {
        return ((getStateBits() & 0x00000070) == 0x00000010);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // チャット・ログ出力

    private String lastMsg = "";
    public void chatMessage(String msg, int level)
    {
        // デバッグ用
        if(level == -1)
        {
			if(!lastMsg.equalsIgnoreCase(msg))
			{
				System.out.println("  "+msg);
				lastMsg = msg;
			}
            return;
        }
        
        EntityPlayer entityplayer = getOwnerEntity();
        
        if(entityplayer != null && getChatLevel()>=level)
		{
//			if(entityplayer != null && getChatLevel()==3)
//			{
//				System.out.println("  "+msg);
//			}
			if(!lastMsg.equalsIgnoreCase(msg))
			{
				entityplayer.addChatComponentMessage(new ChatComponentText(msg));
				lastMsg = msg;
			}
		}
    }

    /**
     * パーティクルの出力
     * @param type パーティクルの種類
     */
    public void spawnParticle(EnumParticleTypes type)
    {
        Random r = this.getRNG();
        double x = this.posX + (double)(r.nextFloat() * this.width * 2.0F) - (double)this.width;
        double y = this.posY + 0.5D + (double)(r.nextFloat() * this.height);
        double z = this.posZ + (double)(r.nextFloat() * this.width * 2.0F) - (double)this.width;
        double dx = r.nextGaussian() * 0.02D;
        double dy = r.nextGaussian() * 0.02D;
        double dz = r.nextGaussian() * 0.02D;
        this.worldObj.spawnParticle(type, x, y, z, dx, dy, dz);
    }


}
