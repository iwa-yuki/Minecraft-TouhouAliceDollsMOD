////////////////////////////////////////////////////////////////////////////////
// アリスの人形MOD

package mods.touhou_alice_dolls.client;

import net.minecraft.client.model.ModelBiped;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.touhou_alice_dolls.*;

@SideOnly(Side.CLIENT)
public class ModelAliceDoll extends ModelBiped
{
    public ModelAliceDoll()
    {
        super(0.0F);
    }

    public ModelAliceDoll(float par1)
    {
        super(par1);
    }
}
