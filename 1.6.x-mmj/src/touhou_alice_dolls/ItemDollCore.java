////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls;

import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;

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
        setUnlocalizedName("dollcore");
        func_111206_d("dollcore");
    }
}
