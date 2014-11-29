////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.*;
import net.minecraftforge.fml.common.FMLLog;

import java.util.Random;
import java.util.List;
import java.util.Iterator;

import mods.touhou_alice_core.dolls.*;
import mods.touhou_alice_core.gui.GuiAliceDollInventory;
import mods.touhou_alice_core.AI.*;
import mods.touhou_alice_core.chunkloader.*;

/**
 * 人形のエンティティクラス
 */
public class EntityAliceDoll extends EntityLiving implements IInventory
{
    private int localDollID;
    private boolean isInitialized;
    private boolean isSlowFall;
    private boolean isHover;
    protected ItemStack mainHeldItem;
    protected Entity targetEntity;
    protected ChunkCoordIntPair currentChunk;
    private boolean isChunkLoad;
    public Ticket ticket;

    ////////////////////////////////////////////////////////////////////////////
    // 初期化
    
    public EntityAliceDoll(World world)
    {
        super(world);

        localDollID = -1;
        isInitialized = false;
        isSlowFall = true;
        isHover = false;
        targetEntity = null;
        isChunkLoad = false;
        ((PathNavigateGround)getNavigator()).func_179690_a(true);

        inventory = new ItemStack[getSizeInventory()];
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        initDataWatcher();
    }

    /** 人形のIDが変更されたときに呼ばれる */
    protected void onChangeDollID()
    {
        if(!isInitialized)
        {
            // 人形のステータスをDollRegistryから取得して設定
            mainHeldItem = DollRegistry.getHeldItem(localDollID);
            setSize(DollRegistry.getWidth(localDollID),
                    DollRegistry.getHeight(localDollID));
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .setBaseValue(DollRegistry.getHealth(localDollID));
            this.setHealth(this.getMaxHealth());
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
                .setBaseValue(DollRegistry.getSpeed(localDollID));
            isSlowFall = DollRegistry.isSlowFall(localDollID);
            isHover = DollRegistry.isHover(localDollID);

            // AIを初期化
            DollRegistry.onInitializeAI(localDollID, this);

            // 内部インベントリのサイズを設定
            // サイズが小さくなる場合、あふれたアイテムは消滅する
            ItemStack[] newInventory = new ItemStack[DollRegistry.getSizeInventory(localDollID)];
            int len = inventory.length > newInventory.length ? newInventory.length : inventory.length;
            for(int i=0;i<len;++i)
            {
                newInventory[i] = inventory[i];
            }
            inventory = newInventory;

            isInitialized = true;
            FMLLog.info("%s : Doll(%s[%s]) is initialized.",
                        (worldObj.isRemote?"R":"S"),
                        DollRegistry.getDollName(localDollID),
                        hashCode());
        }
    }

    /** AIを登録する */
    public void addAI(int index, EntityDollAIBase ai)
    {
        this.tasks.addTask(index, ai);
    }

    ////////////////////////////////////////////////////////////////////////////
    // 動作
    
    @Override
    public void onLivingUpdate()
    {
        // 腕を振る処理
        this.updateArmSwingProgress();
        
        super.onLivingUpdate();

        if(localDollID != getDollID())
        {
            // IDが異なる場合には変更処理を行う
            localDollID = getDollID();
            onChangeDollID();
        }

        // 何らかの理由でライドオン状態が解除されたときに
        // 内部状態との整合性を保つための処理
        if(!worldObj.isRemote && isRideonMode() && !isOwner(ridingEntity))
        {
            setStandbyMode();
        }

        // ライドオンモード時は自動回復
        if(!worldObj.isRemote && isRideonMode())
        {
            heal(1F);
        }

        // アイテム回収
        pickupItem();
        
        if(isHover) // 浮遊する
        {
        	updateHoveringState();
        }
        else if(isSlowFall) // ふわふわと落下
        {
            updateFallingState();
        }

        // チャンクローダー処理
        if(enableChunkLoad())
        {
            updateChunkLoad();
        }
        else
        {
            releaseChunkLoad();
        }
    }

    /** 落下時の処理 */
    
    @Override
    public void fall(float par1, float damageMultiplier)
    {
        if(isSlowFall || isHover)
        {
            // 落下ダメージなし
            return;
        }
        
        super.fall(par1, damageMultiplier);
    }
    
    public float field_70886_e;
    public float destPos;
    public float field_70884_g;
    public float field_70888_h;
    public float field_70889_i = 1.0F;
    protected void updateFallingState()
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
     * 浮遊動作(うまく動かないため使わない)
     */
    protected void updateHoveringState()
    {
    	// 参考：EntityBlaze
    	if (getTargetEntity() != null)
    	{
    		double targetPosY = getTargetEntity().posY + getTargetEntity().getEyeHeight();
    		if(targetPosY > this.posY + getEyeHeight())
    		{
    			this.motionY = this.motionY + (0.3D - this.motionY) * 0.3D;
    		}
    		else if(targetPosY < this.posY + getEyeHeight() - 0.5D)
    		{
        		this.motionY *= 0.6D;
    		}
    		else
    		{
    			this.motionY = 0.0D;
    		}
    	}
    	else if ((!this.onGround) && (this.motionY < 0.0D))
    	{
    		this.motionY *= 0.6D;
    	}
    }

    /** 近くにあるアイテムを回収する */
    protected void pickupItem()
    {
        if (!this.worldObj.isRemote && !this.dead)
        {
            List list = this.worldObj.getEntitiesWithinAABB(
                EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityItem entityitem = (EntityItem)iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null)
                {
                    ItemStack itemstack = entityitem.getEntityItem();

                    if(this.addItemStackToInventory(itemstack))
                    {
                        this.playSound("random.pop", 0.2F,
                                       ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        if(itemstack.stackSize <= 0)
                        {
                            entityitem.setDead();
                        }
                    }
                }
            }
            markDirty();
        }
    }

    /** 右クリックされたときに呼ばれる */
    @Override
    protected boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        
        if(itemstack != null && itemstack.getItem() != null)System.out.println(itemstack.getItem());

        if(isOwner(par1EntityPlayer))
        {
            if(itemstack == null)
            {
                toggleMode();
                return true;
            }
            else
            {
                if (itemstack.getItem() == Items.name_tag)
                {
                    // NameTagが使えるようにfalseを返す
                    return false;
                }
                else if(itemstack.getItem() == TouhouAliceCore.instance.itemDollCore)
                {
                    // ドールコアで右クリックされたら機能のON/Offを切り替える
                    toggleEnable();
                    return true;
                }
                else if(itemstack.getItem() == Items.book)
                {
                    // 本で右クリックされたらチャットの出力レベルを切り替える
                    toggleChatLevel();
                    return true;
                }
                else if(itemstack.getItem() == Item.getItemFromBlock(Blocks.chest))
                {
                    // チェストで右クリックされたらインベントリを開く
                    //par1EntityPlayer.displayGUIChest(this);
                	par1EntityPlayer.openGui(TouhouAliceCore.instance, GuiAliceDollInventory.GuiID, this.worldObj, this.getEntityId(), 0, 0);
                    return true;
                }
                else
                {
                    // それ以外ならモードを切り替える
                    toggleMode();
                    return true;
                }
            }
        }
        else
        {
            // 人形の持ち主でない場合、ドールコアを消費してアクティベーション可能
            if(itemstack != null && itemstack.getItem() == TouhouAliceCore.instance.itemDollCore)
            {
                setOwner(par1EntityPlayer);
                chatMessage(getDollName() + " : Activation successful!", 2);
                if(!worldObj.isRemote)
                {
                    mountEntity(null);
                    setStateBits((getStateBits() & 0xfffffff0) | 0x00000000);
                }
                for(int i=0; i<7; ++i)
                {
                    spawnParticle(EnumParticleTypes.SMOKE_NORMAL);
                }
                playSound("fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
                if (!par1EntityPlayer.capabilities.isCreativeMode
                    && --itemstack.stackSize <= 0)
                {
                    par1EntityPlayer.inventory.setInventorySlotContents(
                        par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                }
                return true;
            }
            else
            {
                if(!worldObj.isRemote)
                {
                	chatMessage(getDollName() + " : You are not my owner!", 1);
                }
            }
        }

        return true;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // 設定

    /** デスポーンするかどうか */
	@Override
	protected boolean canDespawn()
	{
		return false;
	}

    /** AIを使うかどうか */
    protected boolean isAIEnabled()
    {
        return true;
    }
    
    /** 手持ちアイテム */
    @Override
    public ItemStack getHeldItem()
    {
        if(isEnable())
        {
            return mainHeldItem;
        }

        return super.getHeldItem();
    }

    /** 目の高さ */
    @Override
	public float getEyeHeight()
	{
        return this.height * 0.9F;
	}
    
    /** ライドオン時の位置調整 */
	@Override
	public double getYOffset()
	{
        if(ridingEntity != null)
        {
            if(ridingEntity instanceof EntityPlayer)
            {
                float yOffset = 0.0f;
				return (double)(yOffset  - 1.1F);
            }
        }

		return super.getYOffset();
	}

    // @Override
    // public boolean isRiding()
    // {
    //     return false;
    // }

	/**
	 * 攻撃対象のEntityを設定する
	 * @param target 攻撃対象のentity
	 */
	public void setTargetEntity(Entity target)
	{
		this.targetEntity = target;
	}
	
	/**
	 * 攻撃対象のEntityを取得する
	 * @return 攻撃対象のentity
	 */
	public Entity getTargetEntity()
	{
		return this.targetEntity;
	}
    
    /** 攻撃されたときに呼ばれる */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        Entity entitySource = par1DamageSource.getEntity();
        
        if(entitySource != null && entitySource instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entitySource;
            ItemStack currentItem = player.getHeldItem();
            if(currentItem != null && currentItem.getUnlocalizedName().equals("item.dollcore"))
            {
                // ドールコアで攻撃されたので、アイテム化する
                setDead();
                
                // インベントリのアイテムをドロップ
                dropAllItems();
                
                // 人形アイテムをドロップ
                dropItemStack(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                                            getDollID()));
                
                // チャンクロード解除
                if(isChunkLoad)
                {
                    chatMessage(getDollName() + " : Disable ChunkLoader.", 2);
                    releaseChunkLoad();
                }
                
                this.playSound("mob.chicken.plop", 1.0F,
                               (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                return false;
            }
        }
        if(isRideonMode())
        {
            if(par1DamageSource instanceof EntityDamageSourceIndirect)
            {
                if(this.isOwner(entitySource))
                {
                    return false;
                }
            }
        }
        
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    @Override
    public void onDeath(DamageSource par1DamageSource)
    {
        if (ForgeHooks.onLivingDeath(this, par1DamageSource))
        {
            return;
        }
        
        Entity entity = par1DamageSource.getEntity();
        EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (this.scoreValue >= 0 && entitylivingbase != null)
        {
            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }

        if (entity != null)
        {
            entity.onKillEntity(this);
        }

        this.dead = true;

        // インベントリのアイテムをドロップ
        dropAllItems();

        // 人形アイテムをドロップ
        dropItemStack(new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1,
                                    getDollID()));

        // チャンクロード解除
        if(isChunkLoad)
        {
            chatMessage(getDollName() + " : Disable ChunkLoader.", 2);
            releaseChunkLoad();
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    ////////////////////////////////////////////////////////////////////////////
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
        writeInventoryToNBT(par1NBTTagCompound);
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
        readInventoryFromNBT(par1NBTTagCompound);
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
            if(isOpened)
            {
                setStateBits((state & 0xfffffff7) | 0x00000008);
            }
            else
            {
                setStateBits((state & 0xfffffff7) | 0x00000000);
            }
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
    // テレポート

    /** Entityの近くへテレポート */
    public void teleportToEntity(Entity entity, double r)
    {
        teleportToXYZ(entity.posX, entity.posY, entity.posZ, r);
    }

    /** 指定座標の近くへテレポート */
    public void teleportToXYZ(double x, double y, double z, double r)
    {
        double d = Math.sqrt((posX-x)*(posX-x) + (posY-y)*(posY-y) + (posZ-z)*(posZ-z));
        
        double newX = x + (posX - x) / d * r;
        double newY = y + (posY - y) / d * r;
        double newZ = z + (posZ - z) / d * r;

        this.setPosition(newX, newY, newZ);
        this.playSound("mob.endermen.portal", 0.5F, 1.0F);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // インベントリ

    protected ItemStack[] inventory;

    /** インベントリのサイズ */
    @Override
    public int getSizeInventory()
    {
        return DollRegistry.getSizeInventory(getDollID());
    }

    /** インベントリの指定スロットのアイテムスタック */
    @Override
    public ItemStack getStackInSlot(int i)
    {
        return inventory[i];
    }

    /** 指定スロットのアイテム数を減らす */
    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if (this.inventory[i] != null)
        {
            ItemStack itemstack;

            if (this.inventory[i].stackSize <= j)
            {
                itemstack = this.inventory[i];
                this.inventory[i] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.inventory[i].splitStack(j);

                if (this.inventory[i].stackSize == 0)
                {
                    this.inventory[i] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /** GUIを閉じたときにドロップするアイテム<br />chest系はたぶん呼ばれない */
    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        if (this.inventory[i] != null)
        {
            ItemStack itemstack = this.inventory[i];
            this.inventory[i] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /** インベントリの空きスロットにアイテムを追加 */
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        this.inventory[i] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }

    /** インベントリのスロットにアイテムを追加 */
    public boolean addItemStackToInventory(ItemStack itemstack)
    {
        if (itemstack == null)
        {
            return false;
        }
        else if (itemstack.stackSize == 0)
        {
            return false;
        }
        else
        {
            int i;

            if (itemstack.isItemDamaged())
            {
                i = this.getFirstEmptyStack();

                if (i >= 0)
                {
                    this.inventory[i] = ItemStack.copyItemStack(itemstack);
                    itemstack.stackSize = 0;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                do
                {
                    i = itemstack.stackSize;
                    itemstack.stackSize = this.storePartialItemStack(itemstack);
                }
                while (itemstack.stackSize > 0 && itemstack.stackSize < i);

                return itemstack.stackSize < i;
            }
        }
    }

    /** 最初の空きスロットを取得 */
    public int getFirstEmptyStack()
    {
        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    /** 指定したアイテムをインベントリに追加 */
    private int storePartialItemStack(ItemStack itemstack)
    {
        Item item = itemstack.getItem();
        int j = itemstack.stackSize;
        int k;

        if (itemstack.getMaxStackSize() == 1)
        {
            k = this.getFirstEmptyStack();

            if (k < 0)
            {
                return j;
            }
            else
            {
                if (this.inventory[k] == null)
                {
                    this.inventory[k] = ItemStack.copyItemStack(itemstack);
                }

                return 0;
            }
        }
        else
        {
            k = this.storeItemStack(itemstack);

            if (k < 0)
            {
                k = this.getFirstEmptyStack();
            }

            if (k < 0)
            {
                return j;
            }
            else
            {
                if (this.inventory[k] == null)
                {
                    this.inventory[k] = new ItemStack(item, 0, itemstack.getItemDamage());

                    if (itemstack.hasTagCompound())
                    {
                        this.inventory[k].setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                    }
                }

                int l = j;

                if (j > this.inventory[k].getMaxStackSize() - this.inventory[k].stackSize)
                {
                    l = this.inventory[k].getMaxStackSize() - this.inventory[k].stackSize;
                }

                if (l > this.getInventoryStackLimit() - this.inventory[k].stackSize)
                {
                    l = this.getInventoryStackLimit() - this.inventory[k].stackSize;
                }

                if (l == 0)
                {
                    return j;
                }
                else
                {
                    j -= l;
                    this.inventory[k].stackSize += l;
                    return j;
                }
            }
        }
    }

    /** 同じアイテムのスロットを取得 */
    private int storeItemStack(ItemStack itemstack)
    {
        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null &&
                this.inventory[i].getUnlocalizedName().equals(itemstack.getUnlocalizedName()) &&
                this.inventory[i].isStackable() &&
                this.inventory[i].stackSize < this.inventory[i].getMaxStackSize() &&
                this.inventory[i].stackSize < this.getInventoryStackLimit() &&
                (!this.inventory[i].getHasSubtypes() ||
                 this.inventory[i].getItemDamage() == itemstack.getItemDamage()) &&
                ItemStack.areItemStackTagsEqual(this.inventory[i], itemstack))
            {
                return i;
            }
        }

        return -1;
    }

    /** インベントリの名前 */
    @Override
    public String getName()
    {
        return getDollName();
    }


    /** スタック数の限界 */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /** インベントリが変更されたときに呼ばれる */
    public void markDirty()
    {
        // インベントリの後ろから4スロットを防具スロットと共有化するための処理
        for(int i=0; i<4; ++i)
        {
            ItemStack itemstack = inventory[getSizeInventory()-1-i];
            if(itemstack != null)
            {
                Item item = itemstack.getItem();
                if(item instanceof ItemArmor)
                {
                    ItemArmor armor = (ItemArmor)item;
                    if(armor.armorType == i)
                    {
                        setCurrentItemOrArmor(4-i, itemstack);
                    }
                    else
                    {
                        setCurrentItemOrArmor(4-i, null);
                    }
                }
                else if(item instanceof ItemSkull)
                {
                    setCurrentItemOrArmor(4-i, itemstack);
                }
                else if(item instanceof ItemBlock)
                {
                    Block block = ((ItemBlock)item).getBlock();
                    if(block instanceof BlockPumpkin)
                    {
                        setCurrentItemOrArmor(4-i, itemstack);
                    }
                    else
                    {
                        setCurrentItemOrArmor(4-i, null);
                    }
                }
                else
                {
                    setCurrentItemOrArmor(4-i, null);
                }
            }
            else
            {
                setCurrentItemOrArmor(4-i, null);
            }
        }
        setCurrentItemOrArmor(0, inventory[0]);

        // チャンクロードするかどうかを設定する処理
        if(!worldObj.isRemote)
        {
            boolean flag = false;
            for(int i = getSizeInventory() - 1; i >= 0; i--)
            {
                if(inventory[i] != null && inventory[i].getUnlocalizedName().equals("item.clock"))
                {
                    flag = true;
                    break;
                }
            }
            
            if(isChunkLoad == true && flag == false)
            {
                releaseChunkLoad();
                isChunkLoad = false;
                chatMessage(getDollName() + " : Disable ChunkLoader.", 2);
            }
            else if(isChunkLoad == false && flag == true)
            {
                isChunkLoad = true;
                chatMessage(getDollName() + " : Enable ChunkLoader.", 2);
            }
        }
    }

    /** インベントリが利用可能かどうか */
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return (!this.isDead) && (entityplayer.getDistanceSqToEntity(this) <= 64.0D)
            && isOwner(entityplayer);
    }

    /** GUIを開けた時に呼ばれる */
    @Override
    public void openInventory(EntityPlayer playerIn)
    {
        setGUIOpened(true);
    }

    /** GUIを閉じた時に呼ばれる */
    @Override
    public void closeInventory(EntityPlayer playerIn)
    {
        setGUIOpened(false);
    }

    /** 自動搬入するかどうか */
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return true;
    }

    /** インベントリをロード */
    protected void readInventoryFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inventory.length)
            {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
                // System.out.println("read" + this.inventory[j]);
            }
        }
        markDirty();
    }

    /** インベントリをセーブ */
    protected void writeInventoryToNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
                // System.out.println("write" + this.inventory[i]);
            }
        }
        par1NBTTagCompound.setTag("Items", nbttaglist);
    }
    
    /** インベントリ内のすべてのアイテムをドロップする */
    public void dropAllItems()
    {
        for (int i = 0; i < this.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.getStackInSlot(i);

            if (itemstack != null)
            {
                dropItemStack(itemstack);
            }
        }
    }

    /** アイテムをドロップする */
    protected void dropItemStack(ItemStack itemstack)
    {        
        if(worldObj.isRemote)
        {
            return;
        }

        float f = this.rand.nextFloat() * 0.2F + 0.1F;
        float f1 = this.rand.nextFloat() * 0.2F + 0.1F;
        float f2 = this.rand.nextFloat() * 0.2F + 0.1F;

        while (itemstack.stackSize > 0)
        {
            int j = this.rand.nextInt(21) + 10;

            if (j > itemstack.stackSize)
            {
                j = itemstack.stackSize;
            }

            itemstack.stackSize -= j;
            EntityItem entityitem = new EntityItem(
                this.worldObj, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f2,
                new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
            if (itemstack.hasTagCompound())
            {
                entityitem.getEntityItem().setTagCompound(
                    (NBTTagCompound)itemstack.getTagCompound().copy());
            }
            float f3 = 0.05F;
            entityitem.motionX = (double)((float)this.rand.nextGaussian() * f3);
            entityitem.motionY = (double)((float)this.rand.nextGaussian() * f3 + 0.2F);
            entityitem.motionZ = (double)((float)this.rand.nextGaussian() * f3);

            this.worldObj.spawnEntityInWorld(entityitem);
        }
    }
    
    /**
     * アイテムを回収できるかどうか
     * @param itemstack
     * @return
     */
    public boolean canPickupItem(ItemStack itemstack)
    {
    	// 空きスロットがあれば回収可能
    	if(getFirstEmptyStack() >= 0)
    	{
    		return true;
    	}
    	// 空きスロットがない場合、スタック可能であれば回収可能
    	if(storeItemStack(itemstack) >= 0)
    	{
    		return true;
    	}
    	return false;
    }


	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clearInventory() {
		// TODO Auto-generated method stub
		
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
        Random r = this.rand;
        double x = this.posX + (double)(r.nextFloat() * this.width * 2.0F) - (double)this.width;
        double y = this.posY + 0.5D + (double)(r.nextFloat() * this.height);
        double z = this.posZ + (double)(r.nextFloat() * this.width * 2.0F) - (double)this.width;
        double dx = r.nextGaussian() * 0.02D;
        double dy = r.nextGaussian() * 0.02D;
        double dz = r.nextGaussian() * 0.02D;
        this.worldObj.spawnParticle(type, x, y, z, dx, dy, dz);
    }

    ////////////////////////////////////////////////////////////////////////////
    // チャンクロード処理

    public boolean enableChunkLoad()
    {
        return isChunkLoad;
    }
    
    public void updateChunkLoad()
    {
        if(worldObj.isRemote)
        {
            return;
        }
        
        ChunkCoordIntPair chunk = new ChunkCoordIntPair((int)(posX)/16, (int)(posZ)/16);

        if(currentChunk == null)
        {
            currentChunk = chunk;
            TouhouAliceCore.instance.chunkloader.requestTicket(this);
            for(int j = -1; j <= 1; j++)
            {
                for(int i = -1; i <= 1; i++)
                {
                    TouhouAliceCore.instance.chunkloader.addChunk(
                        this, new ChunkCoordIntPair(chunk.chunkXPos + i, chunk.chunkZPos + j));
                }
            }
        }
        else if(!chunk.equals(currentChunk))
        {
            for(int j = -1; j <= 1; j++)
            {
                for(int i = -1; i <= 1; i++)
                {
                    TouhouAliceCore.instance.chunkloader.removeChunk(
                        this, new ChunkCoordIntPair(currentChunk.chunkXPos + i, currentChunk.chunkZPos + j));
                }
            }
            currentChunk = chunk;
            for(int j = -1; j <= 1; j++)
            {
                for(int i = -1; i <= 1; i++)
                {
                    TouhouAliceCore.instance.chunkloader.addChunk(
                        this, new ChunkCoordIntPair(chunk.chunkXPos + i, chunk.chunkZPos + j));
                }
            }
        }
    }

    public void releaseChunkLoad()
    {
        if(worldObj.isRemote)
        {
            return;
        }
        TouhouAliceCore.instance.chunkloader.releaseTicket(this);
    }
}
