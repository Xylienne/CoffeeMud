package com.planet_ink.coffee_mud.Abilities.Spells;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;



import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class Spell_Scatter extends Spell
{
	public String ID() { return "Spell_Scatter"; }
	public String name(){return "Scatter";}
	protected int canTargetCode(){return CAN_MOBS|CAN_ITEMS;}
	public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto, int asLevel)
	{
	    Vector areas=new Vector();
	    if(commands.size()==0)
	        areas.addElement(mob.location().getArea());
	    else
	    if(((String)commands.lastElement()).equalsIgnoreCase("far"))
	    {
	        commands.removeElementAt(commands.size()-1);
	        for(Enumeration e=CMLib.map().areas();e.hasMoreElements();)
	            areas.addElement(e.nextElement());
	    }
	    else
	    if(((String)commands.lastElement()).equalsIgnoreCase("near"))
	    {
	        commands.removeElementAt(commands.size()-1);
	        areas.addElement(mob.location().getArea());
	    }
	    else
	        areas.addElement(mob.location().getArea());
		MOB mobTarget=getTarget(mob,commands,givenTarget,true,false);
		Item target=null;
		if(mobTarget!=null)
		{
			Vector goodPossibilities=new Vector();
			Vector possibilities=new Vector();
			for(int i=0;i<mobTarget.inventorySize();i++)
			{
				Item item=mobTarget.fetchInventory(i);
				if(item!=null)
				{
					if(item.amWearingAt(Item.IN_INVENTORY))
						possibilities.addElement(item);
					else
						goodPossibilities.addElement(item);
				}
				if(goodPossibilities.size()>0)
					target=(Item)goodPossibilities.elementAt(CMLib.dice().roll(1,goodPossibilities.size(),-1));
				else
				if(possibilities.size()>0)
					target=(Item)possibilities.elementAt(CMLib.dice().roll(1,possibilities.size(),-1));
			}
			if(target==null)
				return maliciousFizzle(mob,mobTarget,"<S-NAME> attempt(s) a scattering spell at <T-NAMESELF>, but nothing happens.");
		}

		Vector targets=new Vector();
		if(target!=null)
		    targets.addElement(target);
		else
		{
			targets=CMLib.english().fetchItemList(mob,mob,null,commands,Item.WORNREQ_ANY,true);
			if(targets.size()==0)
				mob.tell("You don't seem to be carrying that.");
		}

		if((targets==null)||(targets.size()==0)) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
		    String str=null;
		    if(mobTarget==null)
			    str=auto?"<S-NAME> is enveloped in a scattering field!":"^S<S-NAME> utter(s) a scattering spell!^?";
		    else
			    str=auto?"<T-NAME> is enveloped in a scattering field!":"^S<S-NAME> utter(s) a scattering spell, causing <T-NAMESELF> to resonate.^?";
			CMMsg msg=CMClass.getMsg(mob,mobTarget,this,verbalCastCode(mob,target,auto),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					for(int i=0;i<targets.size();i++)
					{
					    target=(Item)targets.elementAt(i);
						msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
						Room room=((Area)areas.elementAt(CMLib.dice().roll(1,areas.size(),-1))).getRandomMetroRoom();
						if(mob.location().okMessage(mob,msg))
						{
							mob.location().send(mob,msg);
							if(msg.value()<=0)
							{
							    target.unWear();
							    if(target.owner() instanceof MOB)
							    {
							        MOB owner=(MOB)target.owner();
									mob.location().show(owner,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,"<O-NAME> vanishes from <S-YOUPOSS> inventory!");
									room.showOthers(owner,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,"<O-NAME> appears from out of nowhere!");
							    }
							    else
							    {
									mob.location().show(mob,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,"<O-NAME> vanishes!");
									room.showOthers(mob,room,target,CMMsg.MASK_ALWAYS|CMMsg.MSG_THROW,"<O-NAME> appears from out of nowhere!");
							    }
								if(!room.isContent(target))
								    room.bringItemHere(target,Item.REFUSE_PLAYER_DROP,true);
								room.recoverRoomStats();
							}
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,mobTarget,"<S-NAME> attempt(s) a scattering spell, but nothing happens.");


		// return whether it worked
		return success;
	}
}

