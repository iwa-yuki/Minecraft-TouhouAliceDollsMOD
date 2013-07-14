////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import mods.touhou_alice_dolls.dolls.*;

/**
 * ドールアイテム
 */
public class ItemAliceDoll extends Item
{
    @SideOnly(Side.CLIENT)
    private Icon[] iconList;

    public ItemAliceDoll(int id)
    {
        super(id);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabTools);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        setUnlocalizedName("alicedoll");
        func_111206_d("alicedoll");
    }

    @Override
    /**
     * アイテムを使ったときに呼ばれる
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par3World.isRemote)
        {
            return true;
        }
        else
        {
            int i1 = par3World.getBlockId(par4, par5, par6);
            par4 += Facing.offsetsXForSide[par7];
            par5 += Facing.offsetsYForSide[par7];
            par6 += Facing.offsetsZForSide[par7];
            double d0 = 0.0D;

            if (par7 == 1 && Block.blocksList[i1] != null && Block.blocksList[i1].getRenderType() == 11)
            {
                d0 = 0.5D;
            }

            Entity entity = spawnCreature(par3World, par1ItemStack.getItemDamage(), (double)par4 + 0.5D, (double)par5 + d0, (double)par6 + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(par1ItemStack.getDisplayName());
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode)
                {
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * 人形をスポーンさせる
     * 
     */
    public static Entity spawnCreature(World par0World, int id, double x, double y, double z)
    {
        if (!DollRegistry.isExist(id))
        {
            return null;
        }
        else
        {
            Entity entity = null;

            for (int j = 0; j < 1; ++j)
            {
                entity = new EntityAliceDoll(par0World);

                if (entity != null && entity instanceof EntityLivingBase)
                {
                    EntityAliceDoll entityDoll = (EntityAliceDoll)entity;
                    entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
                    entityDoll.rotationYawHead = entityDoll.rotationYaw;
                    entityDoll.renderYawOffset = entityDoll.rotationYaw;
                    entityDoll.func_110161_a((EntityLivingData)null);
                    entityDoll.setDollID(id);
                    par0World.spawnEntityInWorld(entity);
                    entityDoll.playLivingSound();
                }
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
    public Icon getIconFromDamage(int damage)
    {
        return iconList[damage];
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    /**
     * 一つのアイテムIDに複数のアイテムを登録する
     * @param par1 アイテムID
     * @param par2CreativeTabs CreativeTabs(使い方不明)
     * @param par3List 登録するItemStackのリスト
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < DollRegistry.getDollListLength(); ++j)
        {
            if(DollRegistry.isExist(j))
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
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconList = new Icon[DollRegistry.getDollListLength()];

        for (int i = 0; i < DollRegistry.getDollListLength(); ++i)
        {
            String name = DollRegistry.getDollName(i);
            if(name != null)
            {
                this.iconList[i] = par1IconRegister.registerIcon(
                    this.func_111208_A() + "_" + name);
            }
        }
    }
    
    /**
     * アイテムの内部名
     * @param par1ItemStack ダメージ値取得用のItemStack
     * @return 内部名
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "."
            + DollRegistry.getDollName(par1ItemStack.getItemDamage());
    }
}
