package mods.touhou_alice_dolls.AI;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.EntityLiving;
import mods.touhou_alice_dolls.EntityAliceDoll;

import java.util.*;
import java.util.regex.*;

public class EntityDollAISearchTarget extends EntityDollAIBase
{
    private int counter;
    public static double searchRange;
    public static double searchHeight;
    public static String targetEntityRegex;
    
    public EntityDollAISearchTarget(EntityAliceDoll doll)
    {
        super(doll);
        this.setMutexBits(8);
    }

    public boolean shouldExecute()
    {
        if(!theDoll.isEnable())
        {
            return false;
        }
        return true;
    }

    public void startExecuting()
    {
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
            List<EntityLiving> targetList =
                (List<EntityLiving>)(theWorld.getEntitiesWithinAABB(EntityLiving.class, theDoll.boundingBox.expand(searchRange, searchHeight, searchRange)));
            Pattern targetPattern = Pattern.compile(targetEntityRegex);
            Matcher targetMatcher;

            EntityLiving theTarget = null;
            for(EntityLiving e : targetList)
            {
                String name = EntityList.getEntityString(e);
                if(name == null)
                {
                    continue;
                }

                //攻撃対象設定
                targetMatcher = targetPattern.matcher(name);
                if(targetMatcher.find())
                {
                    if(theTarget == null)
                    {
                        theTarget = e;
                    }
                    else
                    {
                        if(theDoll.getDistanceSqToEntity(theTarget)
                           > theDoll.getDistanceSqToEntity(e))
                        {
                            theTarget = e;
                        }
                    }
                }
            }
            //ターゲットに設定
            theDoll.setAttackTarget(theTarget);
        }
        counter = (counter + 1)%20;
    }
}
