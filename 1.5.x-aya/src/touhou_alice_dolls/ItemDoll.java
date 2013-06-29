package mods.touhou_alice_dolls;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Facing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;

public class ItemDoll extends Item
{
    public ItemDoll(int id)
    {
        super(id);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean onItemUse(
        ItemStack itemStack, EntityPlayer entityplayer,
        World world, int i, int j, int k, int l, float x, float y, float z)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            int blockID = world.getBlockId(i, j, k);
            i += Facing.offsetsXForSide[l];
            j += Facing.offsetsYForSide[l];
            k += Facing.offsetsZForSide[l];
            double offset = 0.0D;

            if (l == 1
                && Block.blocksList[blockID] != null
                && Block.blocksList[blockID].getRenderType() == 11)
            {
                offset = 0.5D;
            }

            if (spawnDoll(world, entityplayer, (double)i + 0.5D, (double)j + offset,
                          (double)k + 0.5D, this.itemID) != null
                && !entityplayer.capabilities.isCreativeMode)
            {
                --itemStack.stackSize;
            }

            return true;
        }
    }

    public static EntityAliceDoll spawnDoll(
        World world, EntityPlayer entityplayer, double x, double y, double z, int dollID)
    {
        if(!DollRegistry.isDollRegistered(dollID))
        {
            return null;
        }
        
        Entity entity = null;
        EntityAliceDoll doll = null;

        entity = EntityList.createEntityByID(
            TouhouAliceDolls.instance.entityAliceDollID, world);

        if (entity != null)
        {
            doll = (EntityAliceDoll)entity;
            doll.setDollID(dollID);
            doll.setOwner(entityplayer);
            doll.setLocationAndAngles(
                x, y, z,MathHelper.wrapAngleTo180_float(
                    world.rand.nextFloat() * 360.0F), 0.0F);
            doll.rotationYawHead = doll.rotationYaw;
            doll.renderYawOffset = doll.rotationYaw;
            doll.initCreature();
            world.spawnEntityInWorld(doll);
        }

        return doll;
    }

}
