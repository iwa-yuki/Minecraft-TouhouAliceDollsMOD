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

public class EntityDollAISearchAndReportTarget extends EntityDollAIBase
{
    private int counter;
    public static double searchRange;
    public static double searchHeight;
    public static String searchEntityRegex;
    public static String targetEntityRegex;
    
    public EntityDollAISearchAndReportTarget(EntityAliceDoll doll)
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
            Pattern searchPattern = Pattern.compile(searchEntityRegex);
            Pattern targetPattern = Pattern.compile(targetEntityRegex);
            Matcher searchMatcher, targetMatcher;
            TreeMap<String, Integer> entityCount = new TreeMap<String, Integer>();
            EntityLiving theTarget = null;
            EntityLiving stealTarget = null;

            theTarget = null;
            for(EntityLiving e : targetList)
            {
                String name = EntityList.getEntityString(e);
                if(name == null)
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

                //攻撃対象設定
                if(theDoll.isPatrolMode())
                {
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

                if(theDoll.isFollowMode())
                {
                    Entity tt = null;
                    if(e instanceof EntityCreature)
                    {
                        tt = ((EntityCreature)e).getEntityToAttack();
                    }
                    else
                    {
                        tt = e.getAttackTarget();
                    }
                    if(theDoll.isOwner(tt))
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

                //蒐集
                if(theDoll.isPatrolMode())
                {
                    if(e instanceof EntitySheep)
                    {
                        EntitySheep sheep = (EntitySheep)e;

                        if(!sheep.getSheared() && !sheep.isChild())
                        {
                            if(stealTarget == null)
                            {
                                stealTarget = e;
                            }
                            else
                            {
                                if(theDoll.getDistanceSqToEntity(stealTarget)
                                   > theDoll.getDistanceSqToEntity(e))
                                {
                                    stealTarget = e;
                                }
                            }
                        }
                    }
                    else if(e instanceof EntityChicken ||
                            e instanceof EntityMooshroom ||
                            e instanceof EntityIronGolem)
                    {
                        if(stealTarget == null)
                        {
                            stealTarget = e;
                        }
                        else
                        {
                            if(theDoll.getDistanceSqToEntity(stealTarget)
                               > theDoll.getDistanceSqToEntity(e))
                            {
                                stealTarget = e;
                            }
                        }
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
            theDoll.setAttackTarget(theTarget == null ? stealTarget : theTarget);
        }
        counter = (counter + 1)%20;
    }
}
