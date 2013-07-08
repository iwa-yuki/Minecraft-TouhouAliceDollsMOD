////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package iwa_yuki.touhou_alice_dolls;

import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;

/**
 * 人形アイテム
 */
public class ItemAliceDoll extends Item
{
    public ItemAliceDoll(int id)
    {
        super(id);
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabTools);
    }
}
