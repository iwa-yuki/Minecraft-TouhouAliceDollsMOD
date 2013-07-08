////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package iwa_yuki.touhou_alice_dolls;

import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import java.util.List;
import java.util.Random;

/**
 * ドールコアアイテム
 */
public class ItemDollCore extends Item
{
    public ItemDollCore(int id)
    {
        super(id);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabTools);
        isUsing = false;
    }

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		return EnumAction.bow;
	}
    
	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i)
	{
        isUsing = false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		return 20;
	}

    @Override
    public ItemStack onItemRightClick(
        ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        entityplayer.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
        chargeCounter = 0;
        isUsing = true;
        
        return itemstack;
    }

    @Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
	{
		if(!world.isRemote && isUsing)
		{
			++chargeCounter;

            if(chargeCounter == 19)
            {
                onCharged(world, entity);
            }
        }
	}

    public void onCharged(World world, Entity entity)
    {
        List<EntityAliceDoll> dolls = world.selectEntitiesWithinAABB(
            EntityAliceDoll.class,
            entity.boundingBox.expand(64.0D, 64.0D, 64.0D),
            new DollSelector((EntityPlayer)entity));
        int size = dolls.size();
        if(size != 0)
        {
            Random rand = new Random();
            int index = rand.nextInt(size);

            EntityAliceDoll d = dolls.get(index);
            
            d.teleportToEntity(d.getOwnerEntity(), 2.0D);
            d.setRideonMode();
        }
    }

    private int chargeCounter;
    private boolean isUsing;
}
