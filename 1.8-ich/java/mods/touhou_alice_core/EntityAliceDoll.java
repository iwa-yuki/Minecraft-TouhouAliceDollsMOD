package mods.touhou_alice_core;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import mods.touhou_alice_core.ai.EntityDollAIBase;
import mods.touhou_alice_core.doll.DollRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * 人形エンティティクラス
 * @author iwa_yuki
 *
 */
public class EntityAliceDoll extends EntityLiving implements IInventory {
	
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
                    par1EntityPlayer.displayGUIChest(this);
                	//par1EntityPlayer.openGui(TouhouAliceCore.instance, GuiAliceDollInventory.GuiID, this.worldObj, this.getEntityId(), 0, 0);
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

    
	///////////////////////////////////////////////////////////////////////////
	// 初期化
    
    /** 人形IDが変更されたときに呼ばれる */
    private void onChangeDollID() {
		// TODO Auto-generated method stub
    	inventory = new ItemStack[getSizeInventory()];
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

    ////////////////////////////////////////////////////////////////////////////
    // インベントリ
    
    protected ItemStack[] inventory;
    
    /** インベントリのスロット数を取得する */
	@Override
	public int getSizeInventory() {
		return 9;
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

        //TODO: チャンクロードするかどうかを設定する処理
//        if(!worldObj.isRemote)
//        {
//            boolean flag = false;
//            for(int i = getSizeInventory() - 1; i >= 0; i--)
//            {
//                if(inventory[i] != null && inventory[i].getUnlocalizedName().equals("item.clock"))
//                {
//                    flag = true;
//                    break;
//                }
//            }
//            
//            if(isChunkLoad == true && flag == false)
//            {
//                releaseChunkLoad();
//                isChunkLoad = false;
//                chatMessage(getDollName() + " : Disable ChunkLoader.", 2);
//            }
//            else if(isChunkLoad == false && flag == true)
//            {
//                isChunkLoad = true;
//                chatMessage(getDollName() + " : Enable ChunkLoader.", 2);
//            }
//        }

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
		// TODO Auto-generated method stub
		
	}

	/** 自動搬入するかどうか */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		
		// 手持ちアイテム、防具との共有スロット以外は自動搬入可
		return (index > 0) && (index < getSizeInventory() - 5);
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
}
