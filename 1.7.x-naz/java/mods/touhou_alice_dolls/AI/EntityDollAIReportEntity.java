package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;

import mods.touhou_alice_core.AI.EntityDollAIBase;
import mods.touhou_alice_core.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

/**
 * Entityを探知してチャットに出力する
 */
public class EntityDollAIReportEntity extends EntityDollAIBase
{
    private int counter;
    public static double searchRange;
    public static double searchHeight;
    public static String searchEntityRegex;
    
    public EntityDollAIReportEntity(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(4);
    }

    public boolean shouldExecute()
    {
        return theDoll.isEnable();
    }

    public void startExecuting()
    {
    	super.startExecuting();
    	
        counter = 0;
    }
    
    public boolean continueExecuting()
    {
        return theDoll.isEnable();
    }
    
    public void updateTask()
    {        
        if(counter == 0)
        {
            List<EntityLivingBase> targetList =
                (List<EntityLivingBase>)(theWorld.getEntitiesWithinAABB(EntityLivingBase.class, theDoll.boundingBox.expand(searchRange, searchHeight, searchRange)));
            Pattern searchPattern = Pattern.compile(searchEntityRegex);
            Matcher searchMatcher;
            TreeMap<String, Integer> entityCount = new TreeMap<String, Integer>();

            for(EntityLivingBase e : targetList)
            {
                String name = EntityList.getEntityString(e);
                if(name == null || name == "")
                {
                    continue;
                }

                //探知対象ならリストに追加
                searchMatcher = searchPattern.matcher(name);
                if(searchMatcher.find())
                {
                    if(entityCount.containsKey(name))
                    {
                        int c = entityCount.get(name).intValue() + 1;
                        entityCount.put(name, new Integer(c));
                    }
                    else
                    {
                        entityCount.put(name, new Integer(1));
                    }
                }
            }

            // 出力文字列の作成
            StringBuffer msg=new StringBuffer(theDoll.getDollName() + " : ");
        
            if(entityCount.isEmpty())
            {
                msg.append("No target");
                theDoll.chatMessage(msg.toString(),2);
            }
            else
            {
                Iterator it = entityCount.keySet().iterator();
                while(it.hasNext())
                {
                    String s = (String)it.next();
                    int v = entityCount.get(s).intValue();
                    msg.append(s);
                    msg.append("[");
                    msg.append(v);
                    msg.append("] ");
                }
                theDoll.chatMessage(msg.toString(),1);
            }

            //ターゲットに設定
            // theDoll.setAttackTarget(theTarget);
        }
        counter = (counter + 1)%20;
    }
}
