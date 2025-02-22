package com.planet_ink.coffee_mud.Abilities.Languages;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2020-2023 Bo Zimmerman

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
public class Krakenspeak extends Aquan
{
	@Override
	public String ID()
	{
		return "Krakenspeak";
	}

	private final static String localizedName = CMLib.lang().L("Krakenspeak");

	@Override
	public String name()
	{
		return localizedName;
	}

	public Krakenspeak()
	{
		super();
	}

	public String tup(final String msg)
	{
		if(msg==null)
			return msg;
		return msg.toUpperCase();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((beingSpoken(ID()))
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.sourceMessage()!=null)
		&&(msg.tool()==null)
		&&((msg.sourceMinor()==CMMsg.TYP_SPEAK)
		   ||(msg.sourceMinor()==CMMsg.TYP_TELL)
		   ||(CMath.bset(msg.sourceMajor(),CMMsg.MASK_CHANNEL))))
		{
			msg.modify(msg.source(),msg.target(),msg.tool(),
					   msg.sourceCode(),tup(msg.sourceMessage()),
					   msg.targetCode(),tup(msg.targetMessage()),
					   msg.othersCode(),tup(msg.othersMessage()));
		}
		return super.okMessage(myHost,msg);
	}
}
