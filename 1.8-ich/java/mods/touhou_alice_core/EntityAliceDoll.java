package mods.touhou_alice_core;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.dolls.DollRegistry;
import mods.touhou_alice_core.gui.GuiAliceDollInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLLog;

/**
 * 人形エンティティクラス
 * @author iwa_yuki
 *
 */
public class EntityAliceDoll extends EntityLiving implements IInventory, ISidedInventory {
	
	private int localDollID = -1;
	private ItemStack mainHeldItem = null;
	private boolean isHover = false;
	private boolean isSlowFall = true;
	private Entity targetEntity = null;

	public EntityAliceDoll(World worldIn) {
		super(worldIn);
		
		setDollID(0);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// 初期化
    
    @Override
    protected void entityInit()
    {
        super.entityInit();

        initDataWatcher();
    }
    
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
    	
    	// 人形の体力を設定
        getEntityAttribute(SharedMonsterAttributes.maxHealth)
        	.setBaseValue(DollRegistry.getHealth(localDollID));
        setHealth(this.getMaxHealth());
        
        // 人形の移動速度を設定
        getEntityAttribute(SharedMonsterAttributes.movementSpeed)
        	.setBaseValue(DollRegistry.getSpeed(localDollID));
    }
    
    /** 人形IDが変更されたときに呼ばれる */
    private void onChangeDollID() {
    	
    	// インベントリの初期化・サイズ変更
    	if(inventory == null) {
    		inventory = new ItemStack[getSizeInventory()];
    	}
    	else if(inventory.length < getSizeInventory()) {
    		ItemStack[] newInventory = new ItemStack[getSizeInventory()];
    		for(int i=0; i < inventory.length; ++i) {
    			newInventory[i] = inventory[i];
    			inventory[i] = null;
    		}
    		inventory = newInventory;
    	}
    	else if(inventory.length > getSizeInventory()) {
    		// インベントリが小さくなる場合はあふれたアイテムが消失する
    		ItemStack[] newInventory = new ItemStack[getSizeInventory()];
    		for(int i=0; i < getSizeInventory(); ++i) {
    			newInventory[i] = inventory[i];
    			inventory[i] = null;
    		}
    		inventory = newInventory;
    	}
    	
    	// 手持ちアイテムの設定
    	mainHeldItem = DollRegistry.getHeldItem(localDollID);
    	
    	// 人形の大きさを設定
    	setSize(DollRegistry.getWidth(localDollID), DollRegistry.getHeight(localDollID));
        
        // 人形の落下動作を設定
        isSlowFall = DollRegistry.isSlowFall(localDollID);
        isHover = DollRegistry.isHover(localDollID);
        
        // AIを初期化
        clearAI();
        DollRegistry.onInitializeAI(localDollID, this);
        
        // 座標ずれ対策
        posX = Math.floor(posX) + 0.5D;
        posZ = Math.floor(posZ) + 0.5D;
        
        FMLLog.info("%s : Initialized Doll(%s) with entity id.",
                (worldObj.isRemote?"R":"S"),
                DollRegistry.getDollName(localDollID),
                getEntityId());
	}   

    ///////////////////////////////////////////////////////////////////////////
	// 動作
    
    @Override
    public void onLivingUpdate()
    {
        // 腕を振る処理
        this.updateArmSwingProgress();

        if(isHover) // 浮遊する
        {
        	updateHoveringState();
        }
        else if(isSlowFall) // ふわふわと落下
        {
            updateFallingState();
        }
        
        super.onLivingUpdate();

        if(localDollID != getDollID())
        {
            // IDが異なる場合には変更処理を行う
            setDollID(getDollID());
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
    public void fall(float distance, float damageMultiplier)
    {
        if(isSlowFall || isHover)
        {
            // 落下ダメージなし
            return;
        }
        
        super.fall(distance, damageMultiplier);
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
     * 浮遊動作(参考：Wither)
     */
    protected void updateHoveringState()
    {
        this.motionY *= 0.6000000238418579D;

        if (!this.worldObj.isRemote && this.targetEntity != null)
        {
            if (this.posY < targetEntity.posY)
            {
                if (this.motionY < 0.0D)
                {
                    this.motionY = 0.0D;
                }

                this.motionY += (0.5D - this.motionY) * 0.6000000238418579D;
            }

            double diffX = targetEntity.posX - this.posX;
            double diffZ = targetEntity.posZ - this.posZ;
            double diffSq = diffX * diffX + diffZ * diffZ;
            double diff;

            if (diffSq > 9.0D)
            {
                diff = (double)MathHelper.sqrt_double(diffSq);
                this.motionX += (diffX / diff * 0.5D - this.motionX) * 0.6000000238418579D;
                this.motionZ += (diffZ / diff * 0.5D - this.motionZ) * 0.6000000238418579D;
            }
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05000000074505806D)
        {
            this.rotationYaw = (float)Math.atan2(this.motionZ, this.motionX) * (180F / (float)Math.PI) - 90.0F;
        }
    }


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
                else if(itemstack.getItem() == TouhouAliceCore.itemDollCore)
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

                if(!worldObj.isRemote)
                {
                    chatMessage(getName() + " : Activation successful!", 2);
                    
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
                	chatMessage(par1EntityPlayer, getName() + " : You are not my owner!", 0);
                }
            }
        }

        return true;
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
            if(currentItem != null && currentItem.getItem() instanceof ItemDollCore)
            {
                // ドールコアで攻撃されたので、アイテム化する
                setDead();
                
                // インベントリのアイテムをドロップ
                dropAllItems();
                
                // 人形アイテムをドロップ
                dropItemStack(new ItemStack(TouhouAliceCore.itemAliceDoll, 1, getDollID()));
                
                // チャンクロード解除
                if(isChunkLoad)
                {
                    chatMessage(getName() + " : Disable ChunkLoader.", 2);
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
            else if(par1DamageSource.getDamageType().equalsIgnoreCase("inWall"))
            {
            	return false;
            }
        }
        
        return super.attackEntityFrom(par1DamageSource, par2);
    }
	
    /** ライフがゼロになった時に呼ばれる */
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
        ItemStack dropItem = new ItemStack(TouhouAliceCore.instance.itemAliceDoll, 1, getDollID());
        if(hasCustomName()) {
        	dropItem.setStackDisplayName(getCustomNameTag());
        }
        dropItemStack(dropItem);

        // チャンクロード解除
        if(isChunkLoad)
        {
            chatMessage(getName() + " : Disable ChunkLoader.", 2);
            releaseChunkLoad();
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

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
	// 設定
    
    /** デスポーンするかどうか */
	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
	/** 人形の位置調整 */
	@Override
	public double getYOffset()
	{
        if(ridingEntity != null)
        {
            if(ridingEntity instanceof EntityPlayer)
            {
            	// プレイヤーの頭に乗せるために上へずらす
				return super.getYOffset() + 0.52D;
            }
        }

		return super.getYOffset();
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
	@Override
    public String getName()
    {        
		if (this.hasCustomName())
		{
			return this.getCustomNameTag();
		}
		else
		{
			String s = DollRegistry.getDollName(getDollID());

			return StatCollector.translateToLocal("entity.alicedoll." + s + ".name");
		}
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
        this.dataWatcher.updateObject(17, entity.getDisplayNameString());
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
            chatMessage(getName()+" : Standby mode", 2);
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
            chatMessage(getName()+" : Patrol mode", 2);
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
            chatMessage(getName()+" : Follow mode", 2);
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
            chatMessage(getName()+" : Rideon mode", 2);
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
            chatMessage(getName()+" : setChatLevel("+level+")", 0);
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
                chatMessage(getName()+" : Work AI ON", 2);
                playSound("note.harp", 1.0F, (float)Math.pow(2D, (double)(23 - 12) / 12D));
            }
            else
            {
                int state = getStateBits();
                setStateBits(state & 0xffffff8f);
                chatMessage(getName()+" : Work AI OFF", 2);
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
    	chatMessage(getOwnerEntity(), msg, level);
    }
    public void chatMessage(EntityPlayer entityplayer, String msg, int level)
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
    
    /** インベントリのスロット数を取得する */
	@Override
	public int getSizeInventory() {
		return DollRegistry.getSizeInventory(localDollID);
	}

	/** 指定したスロットのアイテムを取得する
	 * @param slotIn スロット番号
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory[index];
	}

	/**
	 * 指定したスロットのアイテムを減らす
	 * @param index 
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		
        if (this.inventory[index] != null)
        {
            ItemStack itemstack;

            if (this.inventory[index].stackSize <= count)
            {
                itemstack = this.inventory[index];
                this.inventory[index] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.inventory[index].splitStack(count);

                if (this.inventory[index].stackSize == 0)
                {
                    this.inventory[index] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}
	
	/**
	 * GUIを閉じたときにドロップするアイテムを取得する<br />chest系はたぶん呼ばれない
	 * @param index スロット坂東
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		
		if (this.inventory[index] != null)
        {
            ItemStack itemstack = this.inventory[index];
            this.inventory[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	/**
	 * 指定したスロットにアイテムを追加する
	 * @param index スロット番号 
	 * @param itemstack アイテム
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack) {
		
		this.inventory[index] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
	}

	/**
	 * アイテムスタックの最大値を取得する<br />通常は64でよさげ
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/** 
	 * インベントリが変更されたときに呼ばれる
	 */
	@Override
	public void markDirty() {
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
                chatMessage(getName() + " : Disable ChunkLoader.", 2);
            }
            else if(isChunkLoad == false && flag == true)
            {
                isChunkLoad = true;
                chatMessage(getName() + " : Enable ChunkLoader.", 2);
            }
        }

	}

	/**
	 * プレイヤーがGUIを開くことができるかどうか
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer playerIn) {
		// 人形が生存、直線距離8ブロック以内、プレイヤーがオーナー
        return (!this.isDead) && (playerIn.getDistanceSqToEntity(this) <= 64.0D) && isOwner(playerIn);
	}

	/** GUIを開けた時に呼ばれる */
	@Override
	public void openInventory(EntityPlayer playerIn) {
		setGUIOpened(true);
	}

	/** GUIを閉じた時に呼ばれる */
	@Override
	public void closeInventory(EntityPlayer playerIn) {
		setGUIOpened(false);
	}

	/** 自動搬入するかどうか */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		
		// 手持ちアイテム、防具との共有スロット以外は自動搬入可
		return (index > 0) && (index < getSizeInventory() - 4);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] aint = new int[getSizeInventory() - 5];
		for(int i=1; i<getSizeInventory()-4; ++i) {
			aint[i-1] = i;
		}
		return aint;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			EnumFacing direction) {
		return (index > 0) && (index < getSizeInventory() - 4);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			EnumFacing direction) {
		return (index > 0) && (index < getSizeInventory() - 4);
	}

	/**
	 * GUIで必要なデータを取得する<br />かまどのプログレスバーの値など
	 */
	@Override
	public int getField(int id) {
		return 0;
	}
	
	/**
	 * GUIで必要なデータを設定する<br />かまどのプログレスバーの値など
	 */
	@Override
	public void setField(int id, int value) {
		
	}
	
	/**
	 * GUIで必要なデータの個数を取得する
	 */
	@Override
	public int getFieldCount() {
		// 使用しないので0
		return 0;
	}

	/**
	 * インベントリをすべて空にする
	 */
	@Override
	public void clearInventory() {
		
        for (int i = 0; i < inventory.length; ++i)
        {
            inventory[i] = null;
        }
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
    

    ////////////////////////////////////////////////////////////////////////////
    // チャンクロード処理

	private boolean isChunkLoad = false;
    protected ChunkCoordIntPair currentChunk = null;
    public Ticket ticket;

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
