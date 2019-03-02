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

public class Kalamar extends NPC3 implements Interactable {

	String thestring;

	public Kalamar(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Kalamar"," &!%",36,63,1,4,p);
		attributes|=NPC3_INTERACT;
		thestring = "[\'"+name+" chants softly then thrusts his arms into the air!";
	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random() * 2);
			switch (say) {
				case 0:	say("Hmmmmm.");
						break;
				case 1:	say("I can teleport you anywhere you want to go!");
						break;
				case 2:	send("("+name+" looks around suspiciously.");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"Kalamar is a travelling Wizard.",
				"He asks what he can help you with...",
				"1. Restore my magic points!",
				"Teleport me to...",
				"2. Water City (9000 gold)",
				"3. Catacombs (30000)",
				"4. Guild Hall (2000)",
				"5. Magog Village (5000)",
				"6. Admin Land (5000000)"};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.mp=30;
				c.pSend("[\'"+name+" touches your head gently and suddenly you feel the magical power within you restored.\n$M"+c.parent.toDSChar(c.mp));
				break;
			case 2:
				if (c.gold >= 9000) {
					c.pSend(thestring);
					c.updateGold(-9000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[14],0,0,0);
				}
				break;
			case 3:
				if (c.gold >= 30000) {
					c.pSend(thestring);
					c.updateGold(-30000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[27],0,0,0);
				}
				break;
			case 4:
				if (c.gold >= 2000) {
					c.pSend(thestring);
					c.updateGold(-2000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[6],0,0,0);
				}
				break;
			case 5:
				if (c.gold >= 5000) {
					c.pSend(thestring);
					c.updateGold(-5000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[11],0,0,0);
				}
				break;
			case 6:
				if (c.gold >= 5000000) {
					c.pSend(thestring);
					c.updateGold(-5000000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[45],0,0,0);
				}
				break;
		}
	}
}
