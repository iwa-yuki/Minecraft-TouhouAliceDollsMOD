package mods.touhou_alice_core;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDollCore extends Item {

	private boolean isUsing;
	private int chargeCounter;

	public ItemDollCore() {
		
		this.maxStackSize = 16;
        this.setMaxDamage(0);
		setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("dollcore");
        
        isUsing = false;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		return 20;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn,
			EntityPlayer playerIn) {
		
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        chargeCounter = 0;
        isUsing = true;
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn,
			EntityPlayer playerIn, int timeLeft) {

		isUsing = false;
		
		super.onPlayerStoppedUsing(stack, worldIn, playerIn, timeLeft);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {
		
		if(!worldIn.isRemote && isUsing)
		{
			++chargeCounter;

            if(chargeCounter == 19)
            {
                onCharged(worldIn, entityIn);
            }
        }
		
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	/**
	 * 人形をテレポートさせる
	 * @param worldIn worldオブジェクト
	 * @param entityIn 召喚元のプレイヤー
	 */
	private void onCharged(World worldIn, Entity entityIn) {
        List<EntityAliceDoll> dolls = worldIn.func_175647_a(
                EntityAliceDoll.class, entityIn.getEntityBoundingBox().expand(
                    128.0D, 128.0D, 128.0D),
                new DollSelector((EntityPlayer)entityIn));
            int size = dolls.size();
            if(size != 0)
            {
                Random rand = new Random();
                int index = rand.nextInt(size);

                EntityAliceDoll d = dolls.get(index);
                
                d.teleportToEntity(d.getOwnerEntity(), 2.0D);
                entityIn.mountEntity(null);
                d.setRideonMode();
            }

	}
	
	
}
