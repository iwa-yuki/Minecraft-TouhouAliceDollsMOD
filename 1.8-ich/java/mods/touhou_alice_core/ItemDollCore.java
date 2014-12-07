package mods.touhou_alice_core;

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
        // setUnlocalizedName(TouhouAliceCore.MODID + ":dollcore");
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
		// TODO Auto-generated method stub
		
	}
	
	
}
