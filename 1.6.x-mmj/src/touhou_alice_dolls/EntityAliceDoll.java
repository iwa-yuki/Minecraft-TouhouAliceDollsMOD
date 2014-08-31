////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

import net.minecraftforge.common.ForgeHooks;

import cpw.mods.fml.common.FMLLog;

import java.util.Random;
import java.util.List;
import java.util.Iterator;

import mods.touhou_alice_dolls.dolls.*;
import mods.touhou_alice_dolls.AI.*;

public class EntityAliceDoll extends EntityLiving implements IInventory
{
    private int localDollID;
    private boolean isInitialized;
    protected ItemStack mainHeldItem;

    public int targetX;
    public int targetY;
    public int targetZ;
    public boolean isTargetLockon; 

    ////////////////////////////////////////////////////////////////////////////
    // 初期化
    
    public EntityAliceDoll(World world)
    {
        super(world);

        localDollID = -1;
        isInitialized = false;
        getNavigator().setAvoidsWater(true);

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
            mainHeldItem = DollRegistry.getHeldItem(localDollID);
            setSize(DollRegistry.getWidth(localDollID),
                    DollRegistry.getHeight(localDollID));
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(
                DollRegistry.getHealth(localDollID));
            this.setEntityHealth(this.func_110138_aP());
            this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(
                DollRegistry.getSpeed(localDollID));

            DollRegistry.onInitializeAI(localDollID, this);
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
                        entityId);
        }
    }

    public void addAI(int index, EntityDollAIBase ai)
    {
        this.tasks.addTask(index, ai);
    }

    ////////////////////////////////////////////////////////////////////////////
    // 動作
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if(localDollID != getDollID())
        {
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
        
        // ふわふわと落下
        updateFallingState();
    }

    /** 落下時の処理 */
    @Override
    protected void fall(float par1)
    {
        // 落下ダメージなし
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

    /** 近くにあるアイテムを回収する */
    protected void pickupItem()
    {
        if (!this.worldObj.isRemote && !this.dead)
        {
            List list = this.worldObj.getEntitiesWithinAABB(
                EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
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
            onInventoryChanged();
        }
    }

    /** 右クリックされたときに呼ばれる */
    @Override
    protected boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

        if(isOwner(par1EntityPlayer))
        {
            if(itemstack == null)
            {
                toggleMode();
                return true;
            }
            else
            {
                if (itemstack.itemID == Item.field_111212_ci.itemID)
                {
                    // NameTagが使えるようにfalseを返す
                    return false;
                }
                else if(itemstack.itemID ==
                        TouhouAliceDolls.instance.itemDollCore.itemID)
                {
                    // ドールコアで右クリックされたら機能のON/Offを切り替える
                    toggleEnable();
                    return true;
                }
                else if(itemstack.itemID == Item.book.itemID)
                {
                    // 本で右クリックされたらチャットの出力レベルを切り替える
                    toggleChatLevel();
                    return true;
                }
                else if(itemstack.itemID == Block.chest.blockID)
                {
                    // チェストで右クリックされたらインベントリを開く
                    par1EntityPlayer.displayGUIChest(this);
                    return true;
                }
                else
                {
                    toggleMode();
                    return true;
                }
            }
        }
        else
        {
            // 人形の持ち主でない場合、ドールコアを消費してアクティベーション可能
            if(itemstack != null && itemstack.itemID ==
               TouhouAliceDolls.instance.itemDollCore.itemID)
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
                    spawnParticle("smoke");
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
                    par1EntityPlayer.addChatMessage(getDollName() + " : You are not my owner!");
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
    @Override
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
        return this.height;
	}
    
    /** ライドオン時の位置調整 */
	@Override
	public double getYOffset()
	{
        if(ridingEntity != null)
        {
            if(ridingEntity instanceof EntityPlayer)
            {
                return (double)(yOffset - 1.1F);
            }
        }

		return super.getYOffset();
	}

    @Override
    public boolean isRiding()
    {
        return false;
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
            if(currentItem != null && currentItem.itemID == TouhouAliceDolls.instance.itemDollCore.itemID)
            {
                // ドールコアで攻撃されたので、アイテム化する
                setDead();                
                // インベントリのアイテムをドロップ
                dropAllItems();
                // 人形アイテムをドロップ
                dropItemStack(new ItemStack(TouhouAliceDolls.instance.itemAliceDoll, 1,
                                            getDollID()));
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
        dropItemStack(new ItemStack(TouhouAliceDolls.instance.itemAliceDoll, 1,
                                    getDollID()));

        this.worldObj.setEntityState(this, (byte)3);
    }

    ////////////////////////////////////////////////////////////////////////////
    // データ管理

    /** データ登録 */
    private void initDataWatcher()
    {
        this.dataWatcher.addObject(16, Integer.valueOf(0x00000210)); // 状態
        this.dataWatcher.addObject(17, ""); // オーナーの名前
        this.dataWatcher.addObject(18, Integer.valueOf(-1)); // Doll ID
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
        return this.hasCustomNameTag() ? this.getCustomNameTag() : DollRegistry.getDollName(getDollID());
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
            this.dataWatcher.updateObject(17, entity.username);
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
            return entityplayer.username.equalsIgnoreCase(getOwnerName());
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
        spawnParticle("note");
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
        spawnParticle("heart");
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
    public int getSizeInventory()
    {
        return DollRegistry.getSizeInventory(getDollID());
    }

    /** インベントリの指定スロットのアイテムスタック */
    public ItemStack getStackInSlot(int i)
    {
        return inventory[i];
    }

    /** 指定スロットのアイテム数を減らす */
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
        int i = itemstack.itemID;
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
                    this.inventory[k] = new ItemStack(i, 0, itemstack.getItemDamage());

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
                this.inventory[i].itemID == itemstack.itemID &&
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
    public String getInvName()
    {
        return getDollName();
    }

    /** ローカライズされているかどうか */
    public boolean isInvNameLocalized()
    {
        return true;
    }

    /** スタック数の限界 */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /** インベントリが変更されたときに呼ばれる */
    public void onInventoryChanged()
    {
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
                    Block block = Block.blocksList[itemstack.itemID];
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
    }

    /** インベントリが利用可能かどうか */
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return (!this.isDead) && (entityplayer.getDistanceSqToEntity(this) <= 64.0D)
            && isOwner(entityplayer);
    }

    /** GUIを開けた時に呼ばれる */
    public void openChest()
    {
        setGUIOpened(true);
    }

    /** GUIを閉じた時に呼ばれる */
    public void closeChest()
    {
        setGUIOpened(false);
    }

    /** 自動搬入するかどうか */
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return true;
    }

    /** インベントリをロード */
    protected void readInventoryFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        this.inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inventory.length)
            {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
                // System.out.println("read" + this.inventory[j]);
            }
        }
        onInventoryChanged();
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
                new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));
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
			if(entityplayer != null && getChatLevel()==3)
			{
				System.out.println("  "+msg);
			}
			if(!lastMsg.equalsIgnoreCase(msg))
			{
				entityplayer.addChatMessage(msg);
				lastMsg = msg;
			}
		}
    }

    /**
     * パーティクルの出力
     * @param type パーティクルの種類
     */
    public void spawnParticle(String type)
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
}
