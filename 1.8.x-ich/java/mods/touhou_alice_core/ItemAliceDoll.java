////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import mods.touhou_alice_core.dolls.*;

/**
 * ドールアイテム
 */
public class ItemAliceDoll extends Item
{
    @SideOnly(Side.CLIENT)
    private IIcon[] iconList;

    public ItemAliceDoll()
    {
        super();
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabTools);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        setUnlocalizedName("alicedoll");
        setTextureName("touhou_alice_common:alicedoll");
    }

    @Override
    /**
     * アイテムを使ったときに呼ばれる
     */
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
                             World world, int i, int j, int k, int side,
                             float vecX, float vecY, float vecZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            Block block = world.getBlock(i, j, k);
            i += Facing.offsetsXForSide[side];
            j += Facing.offsetsYForSide[side];
            k += Facing.offsetsZForSide[side];
            double offsetY = 0.0D;

            if (side == 1 && block != null && block.getRenderType() == 11)
            {
                offsetY = 0.5D;
            }

            Entity entity = spawnCreature(world, itemstack.getItemDamage(), (double)i + 0.5D, (double)j + offsetY, (double)k + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && itemstack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(itemstack.getDisplayName());
                }
                if (entity instanceof EntityAliceDoll)
                {
                    ((EntityAliceDoll)entity).setOwner(entityplayer);
                }
                if (!entityplayer.capabilities.isCreativeMode)
                {
                    --itemstack.stackSize;
                }
            }

            return true;
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
                entity.onSpawnWithEgg(null);
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
     * ダメージ値からアイコンを選択<br />染料アイテムと同じ
     * @param damage ダメージ値
     * @return アイコン
     */
    public IIcon getIconFromDamage(int damage)
    {
        return iconList[damage];
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

    @SideOnly(Side.CLIENT)
    @Override
    /**
     * アイコンの登録
     * @param par1IconRegister 登録に使うIconRegister
     */
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.iconList = new IIcon[DollRegistry.getDollListLength()];

        for (int i = 0; i < DollRegistry.getDollListLength(); ++i)
        {
            String name = DollRegistry.getIconName(i);
            if(name != null && name != "")
            {
                this.iconList[i] = par1IconRegister.registerIcon(
                    this.getIconString() + "_" + name);
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
