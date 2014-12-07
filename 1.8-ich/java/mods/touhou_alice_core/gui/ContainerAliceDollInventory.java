package mods.touhou_alice_core.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import mods.touhou_alice_core.EntityAliceDoll;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerAliceDollInventory extends Container {

	private final EntityAliceDoll theDoll;
    private int numRows;
	
	public ContainerAliceDollInventory(EntityPlayer player, World world,
			EntityAliceDoll doll) {
		theDoll = doll;
        numRows = doll.getSizeInventory() / 9;
        
		//theDoll.openInventory();
		
        int i = (this.numRows - 4) * 18;
        int j;
        int k;

        for (j = 0; j < this.numRows-1; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(theDoll, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }            
        for (k = 0; k < 5; ++k)
        {
            this.addSlotToContainer(new Slot(theDoll, k + (this.numRows-1) * 9, 8 + k * 18, 18 + (this.numRows-1) * 18));
        }
        for (k = 5; k < 9; ++k)
        {
        	final int armorIndex = 8 - k;
            this.addSlotToContainer(new Slot(theDoll, k + (this.numRows-1) * 9, 8 + k * 18, 18 + (this.numRows-1) * 18)
            {
                @SideOnly(Side.CLIENT)
                public String func_178171_c()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[armorIndex];
                }
            });
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 161 + i));
        }
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return theDoll.isUseableByPlayer(var1);
	}
	
	@Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
        //this.theDoll.closeInventory();
    }
	
	
}