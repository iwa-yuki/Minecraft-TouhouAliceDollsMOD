package mods.touhou_alice_dolls;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemArmor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.common.ForgeHooks;
import java.util.Random;
import java.util.List;
import java.util.Iterator;

public class EntityAliceDoll extends EntityLiving implements IInventory
{
    // 落下時の処理用
    public boolean field_70885_d = false;
    public float field_70886_e = 0.0F;
    public float destPos = 0.0F;
    public float field_70884_g;
    public float field_70888_h;
    public float field_70889_i = 1.0F;

    // チャット出力用
    protected String lastMsg;

    // 採掘用
    public int targetX;
    public int targetY;
    public int targetZ;
    public boolean isTargetLockon; 
    
    public EntityAliceDoll(World world)
    {
        super(world);
        if(!DollRegistry.isBipedModel)
        {
            this.setSize(0.3F, 0.7F);
        }
        this.jumpMovementFactor = 0.01F;
        this.inventory =  new ItemStack[36];
        this.lastMsg = "";

        this.getNavigator().setAvoidsWater(true);
        DollRegistry.initTasks(this);
    }

    // いろいろと初期化
    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Integer.valueOf(0x00000210)); // 状態
        this.dataWatcher.addObject(17, ""); // オーナーの名前
        this.dataWatcher.addObject(18, Integer.valueOf(-1)); // Doll ID
        this.dataWatcher.addObject(19, Integer.valueOf(-1)); // Sub Item ID
    }

    // テクスチャを取得
    @Override
    public String getTexture()
    {
        return DollRegistry.getTexture(getDollID());
    }

    // AIを使うかどうか
    @Override
    protected boolean isAIEnabled()
    {
        return true;
    }
    
    // 体力の設定
    @Override
    public int getMaxHealth()
    {
        if(DollRegistry.isBipedModel)
        {
            return 16;
        }
        else
        {
            return 8;
        }
    }

    // 音量の設定
    @Override
    protected float getSoundVolume()
	{
		return 0.0F;
	}

    // 移動速度の設定
    public float getSpeed()
    {
        if(DollRegistry.isBipedModel)
        {
            return 0.3f;
        }
        else
        {
            return 0.25f;
        }
    }

    // ライドオン時の位置調整
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
    
    // 視線の高さ
	@Override
	public float getEyeHeight()
	{
        return this.height;
	}
    
    // 落下時の処理
    @Override
    protected void fall(float par1)
    {
        // 落下ダメージなし
    }

    // デスポーンするかどうか
	@Override
	protected boolean canDespawn()
	{
		return false;
	}

    // 手持ちアイテムを取得
    @Override
    public ItemStack getHeldItem()
    {
        if(isEnable())
        {
            return super.getHeldItem();
        }
        return null;
    }

    // 1Tickごとに呼ばれる
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if(!worldObj.isRemote && isRideonMode() && !isOwner(ridingEntity))
        {
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000000);
        }
        if(!worldObj.isRemote && isRideonMode())
        {
            int h = getHealth();
            if(h < getMaxHealth())
            {
                setEntityHealth(h + 1);
            }
        }
        // if(worldObj.isRemote)
        // {
        //     chatMessage(String.format("S:%s.updateState(0x%08x)",getDollName(),getStateBits()), -1);
        // }
        // else
        // {
        //     chatMessage(String.format("C:%s.updateState(0x%08x)",getDollName(),getStateBits()), -1);
        // }

        // アイテム回収
        if (!this.worldObj.isRemote && !this.dead)
        {
            List list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityItem entityitem = (EntityItem)iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null)
                {
                    ItemStack itemstack = entityitem.getEntityItem();

                    if(this.addItemStackToInventory(itemstack))
                    {
                        this.playSE("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        if(itemstack.stackSize <= 0)
                        {
                            entityitem.setDead();
                        }
                    }
                }
            }
        }        

        // ふわふわと落下
        updateFallingState();
    }

    // 右クリックされたときに呼ばれる
    @Override
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack currentItem = par1EntityPlayer.inventory.getCurrentItem();

        if(!isOwner(par1EntityPlayer))
        {
            // ドールコアを持っていれば、消費してアクティベーションすることができる
            if(currentItem != null && currentItem.getItem() instanceof ItemDollCore)
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
                playSE("fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
                if (!par1EntityPlayer.capabilities.isCreativeMode
                    && --currentItem.stackSize <= 0)
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
        else
        {
            if(currentItem == null)
            {
                toggleMode();
                return true;
            }
            else if(currentItem.getItem() instanceof ItemDollCore)
            {
                toggleEnable();
                return true;
            }
            else if(currentItem.itemID == Item.book.itemID)
            {
                toggleChatLevel();
                return true;
            }
            else if(currentItem.itemID == Block.chest.blockID)
            {
                openInventoryGUI(par1EntityPlayer);
                return true;
            }
            else
            {
                toggleMode();
                return true;
            }
        }

        return super.interact(par1EntityPlayer);
    }

    // 攻撃されたときに呼ばれる
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if(isRideonMode())
        {
            if(par1DamageSource instanceof EntityDamageSourceIndirect)
            {
                Entity entitySource = par1DamageSource.getEntity();
                if(this.isOwner(entitySource))
                {
                    return false;
                }
            }
        }
        
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    // HPがゼロになった時に呼ばれる
    @Override
    public void onDeath(DamageSource par1DamageSource)
    {
        if (ForgeHooks.onLivingDeath(this, par1DamageSource))
        {
            return;
        }

        Entity var2 = par1DamageSource.getEntity();

        if (this.scoreValue >= 0 && var2 != null)
        {
            var2.addToPlayerScore(this, this.scoreValue);
        }

        if (var2 != null)
        {
            var2.onKillEntity(this);
        }

        this.dead = true;

        this.dropItemStack(new ItemStack(getDollID(), 1, 0));
        for (int slot = 0; slot < this.getSizeInventory(); ++slot)
        {
            ItemStack itemstack = this.getStackInSlot(slot);
            this.dropItemStack(itemstack);
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    // アイテムをドロップする
    protected void dropItemStack(ItemStack itemstack)
    {
        if (itemstack != null)
        {
            float var3 = this.rand.nextFloat() * 0.8F + 0.1F;
            float var4 = this.rand.nextFloat() * 0.8F + 0.1F;
            float var5 = this.rand.nextFloat() * 0.8F + 0.1F;

            while (itemstack.stackSize > 0)
            {
                int var6 = this.rand.nextInt(21) + 10;
                    
                if (var6 > itemstack.stackSize)
                {
                    var6 = itemstack.stackSize;
                }
                itemstack.stackSize -= var6;
                EntityItem var7 = new EntityItem(
                    this.worldObj, this.posX + (double)var3,
                    this.posY + (double)var4, this.posZ + (double)var5,
                    new ItemStack(itemstack.itemID, var6, itemstack.getItemDamage()));

                if (itemstack.hasTagCompound())
                {
                    var7.getEntityItem().setTagCompound(
                        (NBTTagCompound)itemstack.getTagCompound().copy());
                }

                float var8 = 0.05F;
                var7.motionX = (double)((float)this.rand.nextGaussian() * var8);
                var7.motionY = (double)((float)this.rand.nextGaussian() * var8 + 0.2F);
                var7.motionZ = (double)((float)this.rand.nextGaussian() * var8);
                
                
                if(!worldObj.isRemote)
                {
                    this.worldObj.spawnEntityInWorld(var7);
                }
            }
        }
    }

    // 装備品のドロップ
    @Override
    protected void dropEquipment(boolean par1, int par2)
    {
        // しない
    }

    ////////////////////////////////////////////////////////////////////////////
    // データ管理

    // セーブ
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("DollID", this.getDollID());
        par1NBTTagCompound.setString("Owner", this.getOwnerName());
        par1NBTTagCompound.setInteger("State", this.getStateBits());
        this.writeInventoryToNBT(par1NBTTagCompound);
    }

    // ロード
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setDollID(par1NBTTagCompound.getInteger("DollID"));
        this.setOwnerName(par1NBTTagCompound.getString("Owner"));
        this.setStateBits(par1NBTTagCompound.getInteger("State"));
        this.readInventoryFromNBT(par1NBTTagCompound);
    }

    // 人形のIDを取得
    public int getDollID()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    // 人形のIDを設定
    public void setDollID(int id)
    {
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(18, Integer.valueOf(id));
            this.setCurrentItemOrArmor(0, DollRegistry.getHeldItem(id));
        }
    }

    // 人形の名前を取得
    public String getDollName()
    {
        return DollRegistry.getName(getDollID());
    }

    // 持ち主を設定
    public void setOwner(EntityPlayer player)
    {
        setOwnerName(player.username);
    }
    
    // 持ち主の名前を設定
    public void setOwnerName(String name)
    {
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(17, name);
        }
    }

    // 持ち主の名前を取得
    public String getOwnerName()
    {
        return this.dataWatcher.getWatchableObjectString(17);
    }

    // 持ち主のエンティティを取得
    public EntityPlayer getOwnerEntity()
    {
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }

    // 持ち主かどうか
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

    // 状態の取得
    public int getStateBits()
    {
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    // 状態の設定
 	public void setStateBits(int state)
	{
        if(!worldObj.isRemote)
        {
            this.dataWatcher.updateObject(16, state);
        }
	}
	
    // 動作モード変更
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

    // スタンバイモードかどうか
    public boolean isStandbyMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000000;
    }

    // スタンバイモードに設定
    public void setStandbyMode()
    {
        if(!worldObj.isRemote)
        {
            mountEntity(null);
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000000);
            chatMessage(getDollName()+" : Standby mode", 2);
            playSE("random.click", 0.3F, 0.6F);
        }
    }

    // パトロールモードかどうか
    public boolean isPatrolMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000001;
    }

    // パトロールモードに設定
    public void setPatrolMode()
    {
        spawnParticle("note");
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000001);
            chatMessage(getDollName()+" : Patrol mode", 2);
            playSE("random.click", 0.3F, 0.6F);
        }        
    }

    // フォローモードかどうか
    public boolean isFollowMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000002;
    }

    // フォローモードに設定
    public void setFollowMode()
    {
        spawnParticle("heart");
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffffff8) | 0x00000002);
            chatMessage(getDollName()+" : Follow mode", 2);
            playSE("random.click", 0.3F, 0.6F);
        }
    }

    // ライドオンモードかどうか
    public boolean isRideonMode()
    {
        return (getStateBits() & 0x00000007) == 0x00000003;
    }

    // ライドオンモードに設定
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
                mountEntity(owner);
            }
            chatMessage(getDollName()+" : Rideon mode", 2);
            playSE("random.click", 0.3F, 0.6F);
        }
    }

    // インベントリGUIが開かれているか
    public boolean isGUIOpened()
    {
        return (getStateBits() & 0x00000008) == 0x00000008;
    }

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

    // チャット出力レベルの変更
    public void toggleChatLevel()
    {
        setChatLevel((getChatLevel() + 1) % 4);
    }

    // チャット出力レベルを取得
    public int getChatLevel()
    {
        int state = getStateBits();
        return (int)((state & 0x00000f00) >> 8);
    }

    // チャット出力レベルの設定
    public void setChatLevel(int level)
    {
        if(!worldObj.isRemote)
        {
            int state = getStateBits();
            setStateBits((state & 0xfffff0ff) | (level << 8));
            chatMessage(getDollName()+" : setChatLevel("+level+")", 0);
            playSE("random.click", 0.3F, 0.6F);
        }
    }
    
    // 機能のオンオフ切り替え
    public void toggleEnable()
    {
        setEnable(!isEnable());
    }

    // 機能のオンオフを設定
    public void setEnable(boolean enable)
    {
        if(!worldObj.isRemote)
        {
            if(enable)
            {
                int state = getStateBits();
                setStateBits((state & 0xffffff8f) | 0x00000010);
                chatMessage(getDollName()+" : Work AI ON", 2);
                playSE("note.harp", 1.0F, (float)Math.pow(2D, (double)(23 - 12) / 12D));
            }
            else
            {
                int state = getStateBits();
                setStateBits(state & 0xffffff8f);
                chatMessage(getDollName()+" : Work AI OFF", 2);
                playSE("note.harp", 1.0F, (float)Math.pow(2D, (double)(11 - 12) / 12D));
            }
        }
    }

    // 機能がオンかどうか
    public boolean isEnable()
    {
        return ((getStateBits() & 0x00000070) == 0x00000010);
    }

    // SubItemを取得
    public ItemStack getSubItem()
    {
        int id = this.dataWatcher.getWatchableObjectInt(19);
        if(id == -1)
        {
            return null;
        }
        ItemStack itemstack = new ItemStack(id & 0xffff, (id>>16) & 0xff, (id>>24) & 0xff);
        return itemstack;
    }

    // SubItemを設定
    public void setSubItem(ItemStack itemstack)
    {
        if(!worldObj.isRemote)
        {
            int id = itemstack == null ? -1 : ((itemstack.itemID & 0xffff)
                                               | ((itemstack.stackSize & 0xff) << 16)
                                               | ((itemstack.getItemDamage() & 0xff) << 24));
            this.dataWatcher.updateObject(19, Integer.valueOf(id));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // 動作
    
    // 落下中の動作
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

    // Entityの近くへテレポート
    public void teleportToEntity(Entity entity, double r)
    {
        teleportToXYZ(entity.posX, entity.posY, entity.posZ, r);
    }

    // 指定座標の近くへテレポート
    public void teleportToXYZ(double x, double y, double z, double r)
    {
        double d = Math.sqrt((posX-x)*(posX-x) + (posY-y)*(posY-y) + (posZ-z)*(posZ-z));
        
        double newX = x + (posX - x) / d * r;
        double newY = y + (posY - y) / d * r;
        double newZ = z + (posZ - z) / d * r;

        this.setPosition(newX, newY, newZ);
        this.playSE("mob.endermen.portal", 0.5F, 1.0F);
    }

    // パーティクルの出力
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

    // SEの出力
    public void playSE(String type, float vol, float pitch)
    {
		worldObj.playSoundEffect(posX, posY+getEyeHeight(), posZ, type, vol, pitch);
    }

    // チャット出力
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


    ////////////////////////////////////////////////////////////////////////////
    // インベントリ

    protected ItemStack[] inventory;

    // 人形のインベントリGUIを開く
    public void openInventoryGUI(EntityPlayer player)
    {
        if (this.getSizeInventory() > 0)
        {
            if (!this.worldObj.isRemote)
            {
                player.displayGUIChest(this);
            }
        }
    }

    // インベントリの保存
    protected void writeInventoryToNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (getSizeInventory() > 0)
        {
            NBTTagList var2 = new NBTTagList();

            for (int var3 = 0; var3 < this.inventory.length; ++var3)
            {
                if (this.inventory[var3] != null)
                {
                    NBTTagCompound var4 = new NBTTagCompound();
                    var4.setByte("Slot", (byte)var3);
                    this.inventory[var3].writeToNBT(var4);
                    var2.appendTag(var4);
                }
            }

            par1NBTTagCompound.setTag("Items", var2);
        }
    }
    
    // インベントリの読み出し
    protected void readInventoryFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (getSizeInventory() > 0)
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
            this.inventory = new ItemStack[36];

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
                int var5 = var4.getByte("Slot") & 255;

                if (var5 >= 0 && var5 < this.inventory.length)
                {
                    this.inventory[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }

        onInventoryChanged();
    }
    
    // インベントリのサイズ
    public int getSizeInventory()
    {
        return DollRegistry.getInventorySize(this.getDollID());
    }

    // インベントリのスロット
    public ItemStack getStackInSlot(int index)
    {
        if(this.inventory == null)
            return null;
        
        return this.inventory[index];
    }

    // スタックサイズを減らす
    public ItemStack decrStackSize(int index, int size)
    {
        if (this.inventory[index] != null)
        {
            ItemStack itemstack;

            if (this.inventory[index].stackSize <= size)
            {
                itemstack = this.inventory[index];
                this.inventory[index] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.inventory[index].splitStack(size);

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

    // よくわからない
    public ItemStack getStackInSlotOnClosing(int index)
    {
        if (this.inventory[index] != null)
        {
            ItemStack var2 = this.inventory[index];
            this.inventory[index] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    // インベントリにアイテムを入れる
    public void setInventorySlotContents(int index, ItemStack itemstack)
    {
        this.inventory[index] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }

    // インベントリの名前
    public String getInvName()
    {
        return getDollName();
    }

    // 最大スタック数
    public int getInventoryStackLimit()
    {
        return 64;
    }

    // インベントリが変更された時に呼ばれる
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
        setSubItem(inventory[0]);
    }

    // プレイヤーがインベントリにアクセスできるかどうか
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    // インベントリGUIが開かれたときに呼ばれる
    public void openChest()
    {
        setGUIOpened(true);
    }

    // インベントリGUIが閉じられた時に呼ばれる
    public void closeChest()
    {
        setGUIOpened(false);
    }

    // インベントリにアイテムを追加
    public boolean addItemStackToInventory(ItemStack itemstack)
    {
        int slot;

        if (itemstack.isItemDamaged())
        {
            slot = this.getFirstEmptyStack();

            if (slot >= 0)
            {
                this.inventory[slot] = ItemStack.copyItemStack(itemstack);
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
            int size;
            do
            {
                size = itemstack.stackSize;
                itemstack.stackSize = this.storePartialItemStack(itemstack);
            }
            while (itemstack.stackSize > 0 && itemstack.stackSize < size);

            return itemstack.stackSize < size;
        }
    }

    // 最初の空きスロットを取得
    public int getFirstEmptyStack()
    {
        for (int var1 = 0; var1 < this.getSizeInventory(); ++var1)
        {
            if (this.inventory[var1] == null)
            {
                return var1;
            }
        }

        return -1;
    }

    // インベントリにアイテムをマージ
    private int storePartialItemStack(ItemStack itemstack)
    {
        int itemID = itemstack.itemID;
        int size = itemstack.stackSize;
        int slot;

        if (itemstack.getMaxStackSize() == 1)
        {
            slot = this.getFirstEmptyStack();

            if (slot < 0)
            {
                return size;
            }
            else
            {
                if (this.inventory[slot] == null)
                {
                    this.inventory[slot] = ItemStack.copyItemStack(itemstack);
                }

                return 0;
            }
        }
        else
        {
            slot = this.storeItemStack(itemstack);

            if (slot < 0)
            {
                slot = this.getFirstEmptyStack();
            }

            if (slot < 0)
            {
                return size;
            }
            else
            {
                if (this.inventory[slot] == null)
                {
                    this.inventory[slot] = new ItemStack(itemID, 0, itemstack.getItemDamage());

                    if (itemstack.hasTagCompound())
                    {
                        this.inventory[slot].setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                    }
                }

                int var5 = size;

                if (size > this.inventory[slot].getMaxStackSize() - this.inventory[slot].stackSize)
                {
                    var5 = this.inventory[slot].getMaxStackSize() - this.inventory[slot].stackSize;
                }

                if (var5 > this.getInventoryStackLimit() - this.inventory[slot].stackSize)
                {
                    var5 = this.getInventoryStackLimit() - this.inventory[slot].stackSize;
                }

                if (var5 == 0)
                {
                    return size;
                }
                else
                {
                    size -= var5;
                    this.inventory[slot].stackSize += var5;
                    this.inventory[slot].animationsToGo = 5;
                    return size;
                }
            }
        }
    }

    private int storeItemStack(ItemStack itemstack)
    {
        for (int var2 = 0; var2 < this.getSizeInventory(); ++var2)
        {
            if (this.inventory[var2] != null
                && this.inventory[var2].itemID == itemstack.itemID
                && this.inventory[var2].isStackable()
                && this.inventory[var2].stackSize < this.inventory[var2].getMaxStackSize()
                && this.inventory[var2].stackSize < this.getInventoryStackLimit()
                && (!this.inventory[var2].getHasSubtypes() || this.inventory[var2].getItemDamage() == itemstack.getItemDamage())
                && ItemStack.areItemStackTagsEqual(this.inventory[var2], itemstack))
            {
                return var2;
            }
        }

        return -1;
    }

    public boolean func_94042_c()
    {
        return false;
    }
    
    public boolean func_94041_b(int i, ItemStack itemstack)
    {
        if(i == 0 || i >= this.getSizeInventory() - 4)
        {
            return false;
        }
        return true;
    }

}
