package mods.touhou_alice_core.packet;

import mods.touhou_alice_core.EntityAliceDoll;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageAyaShot implements IMessage {
	
    public int entityID;
    
    public MessageAyaShot(){}
 
    public MessageAyaShot(EntityAliceDoll doll) {
        this.entityID = doll.getEntityId();
    }
    
	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID= buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.entityID);
	}

}
