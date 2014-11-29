////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import mods.touhou_alice_core.dolls.*;

/**
 * ドールアイテム
 */
public class ItemAliceDoll extends Item
{

    public ItemAliceDoll()
    {
        super();
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabTools);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        setUnlocalizedName("alicedoll");
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();

        return s;
    }

    @Override
    /**
     * アイテムを使ったときに呼ばれる
     */
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
                             World world, BlockPos pos, EnumFacing side,
                             float vecX, float vecY, float vecZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else if (!entityplayer.func_175151_a(pos.offset(side), side, itemstack))
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = world.getBlockState(pos);

            pos = pos.offset(side);
            double d0 = 0.0D;

            if (side == EnumFacing.UP && iblockstate instanceof BlockFence)
            {
                d0 = 0.5D;
            }

            Entity entity = spawnCreature(world, itemstack.getMetadata(), (double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && itemstack.hasDisplayName())
                {
                    entity.setCustomNameTag(itemstack.getDisplayName());
                }

                if (!entityplayer.capabilities.isCreativeMode)
                {
                    --itemstack.stackSize;
                }
            }

            return true;
        }
    }
    
    @Override
    /**
     * アイテムを持った状態で右クリックしたときに呼ばれる
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (worldIn.isRemote)
        {
            return itemStackIn;
        }
        else
        {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);

            if (movingobjectposition == null)
            {
                return itemStackIn;
            }
            else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    BlockPos blockpos = movingobjectposition.func_178782_a();

                    if (!worldIn.isBlockModifiable(playerIn, blockpos))
                    {
                        return itemStackIn;
                    }

                    if (!playerIn.func_175151_a(blockpos, movingobjectposition.field_178784_b, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    if (worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid)
                    {
                        Entity entity = spawnCreature(worldIn, itemStackIn.getMetadata(), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);

                        if (entity != null)
                        {
                            if (entity instanceof EntityLivingBase && itemStackIn.hasDisplayName())
                            {
                                ((EntityLiving)entity).setCustomNameTag(itemStackIn.getDisplayName());
                            }

                            if (!playerIn.capabilities.isCreativeMode)
                            {
                                --itemStackIn.stackSize;
                            }

                            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        }
                    }
                }

                return itemStackIn;
            }
        }
    }

    /**
     * 人形をスポーンさせる
     * @param world Worldオブジェクト
     * @param id 人形ID
     * @param x スポーンさせるX座標
     * @param y スポーンさせるY座標
     * @param z スポーンさせるZ座標
     * @return 人形のEntity
     */
    public static Entity spawnCreature(World world, int id, double x, double y, double z)
    {
        if (!DollRegistry.isExist(id))
        {
            return null;
        }
        else
        {
            EntityAliceDoll entity = null;

            entity = new EntityAliceDoll(world);

            if (entity != null)
            {
                entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                entity.rotationYawHead = entity.rotationYaw;
                entity.renderYawOffset = entity.rotationYaw;
                entity.func_180482_a(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
                entity.setDollID(id);
                world.spawnEntityInWorld(entity);
                entity.playLivingSound();
            }

            return entity;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    /**
     * 一つのアイテムIDに複数のアイテムを登録する
     * @param par1 アイテム
     * @param par2CreativeTabs CreativeTabs(使い方不明)
     * @param par3List 登録するItemStackのリスト
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < DollRegistry.getDollListLength(); ++j)
        {
            if(DollRegistry.isExist(j) && (!DollRegistry.isSecret(j)))
            {
                par3List.add(new ItemStack(par1, 1, j));
            }
        }
    }
    
    /**
     * アイテムの内部名
     * @param itemstack ダメージ値取得用のItemStack
     * @return 内部名
     */
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return super.getUnlocalizedName() + "."
            + DollRegistry.getDollName(itemstack.getItemDamage());
    }
}
