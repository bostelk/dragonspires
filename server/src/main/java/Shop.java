/*

Java DragonSpires Server
Copyright (c) 1997-2001, Adam Maloy
All rights reserved.

LICENSE (BSD, revised)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
/*
   Adam Maloy (Mech) would like to give special thanks to: 
          Dr. Cat and 'Manda (Dragon's Eye Productions),
          C.H. Wolf (Motorhed),
          the players and persistant fans of DragonSpires,
          and everyone else who has supported the development of DragonSpires.
*/

public class Shop {

	static DSpiresServer parent;
	int[][][] menus;
	static String texts[] = {"do","buy","sell"};
	static String accepts[] = {"There you go.","Thank you!"};
	static String denies[] = {"Hmmm. You don't seem to be able to pay for that.","Uhm. You don't seem to be holding that."};
	NPC2 host;
	int shopnum,ostatetime,statetime;
	int[] rect,tiles;
	String title;
	boolean open = true,msgopen = true;

	public Shop(int snum, String tt, int[][][] m, int t0, int t1, int r0, int r1, int r2, int r3, int st, boolean msgo, NPC2 h) {
		shopnum = snum;
		title = tt;
		statetime = st;
		ostatetime = st;
		msgopen = msgo;
		menus = m;
		menus[0][0][0] = -1;
		menus[0][0][1] = -1;
		menus[0][1][0] = -1;
		menus[0][1][1] = -1;
		menus[0][1][3] = -1;
		menus[0][2][0] = -1;
		menus[0][2][1] = -1;
		menus[0][2][3] = -2;
		tiles = new int[2];
		tiles[0] = t0;
		tiles[1] = t1;
		rect = new int[4];
		rect[0] = r0;
		rect[1] = r1;
		rect[2] = r2;
		rect[3] = r3;
		host = h;
	}
	protected static boolean sendMenu(int mnum, Shop s, DSpiresSocket c) {

		if (!s.open) {
			if (s.msgopen)
				c.pSend("[&"+s.host.name+": I'm re-stocking. I'll be finished in about "+s.statetime+" minutes.");
			return false;
		}

		String line = ""+mnum;

		if (mnum == 0)
			c.pSend("shn"+s.title);

		for (int i = 1; i < 7; i++) {
			if ((char)s.menus[mnum][i][0] > 0) {
			//try {
				line+="\t"+parent.encode(s.menus[mnum][i][0])+""+parent.encode(s.menus[mnum][i][1])+""+s.menus[mnum][i][2];
			//}
			}
			//else
			//catch (NullPointerException e) {
			//	System.out.println("Got nully");
			//	break;
			//}
		}
		c.inshop = s.shopnum;
		c.smenu = mnum;
		c.pSend("[&"+s.host.name+": What would you like to "+texts[mnum]+"?");
		c.pSend("sh "+line);

		return true;
	}
	protected static void initCommand(int cnum, Shop s, DSpiresSocket c) {
		//if ((char)s.menus[c.smenu][cnum][2] != null) {
		try {
			if (c.smenu > 0) {
			//try {
				if (s.menus[c.smenu][cnum][1-(c.smenu-1)] == 25 && c.gold >= s.menus[c.smenu][cnum][2]) {
					int i;
					for (i=0;i<c.inventory.length&&c.inventory[i]!=0;i++);

					if (i < c.inventory.length) {

						int theitem = s.menus[c.smenu][cnum][(c.smenu-1)];

						if (c.invweight+parent.itemdefs[theitem][5] > parent.maxweight) {
							c.pSend("[&"+s.host.name+": Hmm. You can't seem to hold anymore weight in your inventory.");
							return;
						}
						else {
							c.invweight+=parent.itemdefs[theitem][5];
							c.setInvPos(theitem,i);
							c.updateGold(s.menus[c.smenu][cnum][2]*-1);
							c.pSend("[&"+s.host.name+": "+accepts[c.smenu-1]);
							c.pSend("(* You put "+parent.itemnames[theitem]+" in your inventory.");
						}
					}
					else {
						c.pSend("[&"+s.host.name+": Heh. Your inventory is full!");
						return;
					}
				}
				else if (c.inhand == s.menus[c.smenu][cnum][1-(c.smenu-1)]) {
					if (s.menus[c.smenu][cnum][(c.smenu-1)] == 25) {
						c.updateGold(s.menus[c.smenu][cnum][2]);
						c.setHands(0);
					}
					else
						c.setHands(s.menus[c.smenu][cnum][(c.smenu-1)]);
					c.pSend("[&"+s.host.name+": "+accepts[c.smenu-1]);
				}
				else //if (c.smenu > 0)
					c.pSend("[&"+s.host.name+": "+denies[c.smenu-1]);
			//}
			}
			//catch (NullPointerException e) {
			//	System.out.println("Got nully2");
			//}
			//c.pSend("(It is done!",c);
			sendMenu(s.menus[c.smenu][cnum][3]*-1,s,c);
		}
		//}
		catch (NullPointerException e) {
				System.err.println("Got nully3");
		}
	}
	protected static void changeState(Shop s) {
		s.open = !s.open;
		if (s.open) {
			if (s.msgopen)
				s.host.map.limitedBroadcast("[&"+s.host.name+": Whew. I'm finally finished re-stocking.",s.host.x, s.host.y,s.host.map);
		}
		else if (!s.open) {
			if (s.msgopen)
				s.host.map.limitedBroadcast("[&"+s.host.name+": Ah, new merchandise. Time to re-stock.",s.host.x, s.host.y,s.host.map);
		}
	}
}