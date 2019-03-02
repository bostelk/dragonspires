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

import java.util.*;
import java.io.*;

public class stuffThread extends Thread {
	DSpiresServer parent;
	int hpreg,mincheck,lasthour=0,shopoc,stamreg;//,irc;
	ParadePath[] parade = new ParadePath[3];
	boolean paradeon[] = {false,false,false};

	public stuffThread(DSpiresServer p) {
		setPriority(Thread.MAX_PRIORITY);
		parent = p;
	}
	public void run() {
		DSpiresSocket c;
		while (true) {
			try {
				for (int i=0;i<parent.SOCKETS;i++) {
					if (parent.socketbase[i]!=null) {
						if (parent.socketbase[i].loggedin) {
							c=parent.socketbase[i];								

							if (hpreg==0)
								doHPRegen(c);

							if (stamreg==2) {
								if (c.stam<c.MAX_STAM)
									doStamRegen(c);
							}

							c.speechlimit=0;
							c.restCount=0;
							c.moveCount=0;
						}
					}
				}

				if (++hpreg == 15) hpreg = 0;
	
				if (++mincheck>=20) {
					paradeTimeCheck();
					mincheck = 0;
				}

				if (++stamreg > 2) stamreg = 0;

				if (++shopoc >= 20) shopStateChange();

				for (int i = 0; i < paradeon.length; i++) {
					if (paradeon[i]) {
						yield();
						paradeon[i] = parade[i].continueParade(parade[i]);
						if (!paradeon[i]) {
							if (!paradeon[0] && !paradeon[1] && !paradeon[2]) {
								parade[0] = null;
								parade[1] = null;
								parade[2] = null;
							}
						}
					}
				}
				sleep(3000);
			}
			catch (Exception e) {
				e.printStackTrace();
				//parent.log(e.toString());
			}
			c=null;
		}
	}
	public void shopStateChange() {
		for (int m = 0; m < parent.maps.length; m++) {
			if (parent.maps[m].shops != null) {
				for (int s = 0; s < parent.maps[m].shops.length; s++) {
					if (parent.maps[m].shops[s].statetime == -1) continue;

					if (--parent.maps[m].shops[s].statetime <= 0) {
						parent.maps[m].shops[s].changeState(parent.maps[m].shops[s]);
						parent.maps[m].shops[s].statetime = parent.maps[m].shops[s].ostatetime;
						if (parent.maps[m].shops[s].host.name.startsWith("Faerie")) {
							int toplace = 0;
							if (parent.maps[m].shops[s].open)
								toplace = 125;
							parent.maps[m].placeItemAt(toplace,8,73,parent.maps[m]);
						}
					}
				}
			}
		}
		shopoc = 0;
	}
	public void doStamRegen(DSpiresSocket c) {
		int updateval = 7;
		switch (c.inhand) {
			case 32:
				updateval+=8;
				break;
			case 131:
			case 132:
				updateval+=10;
		}
		if (c.bitIsMarked(c.stateCheck,c.ST_REST))
			updateval+=10;
		switch (c.map.itemmap[c.cx-32][c.cy-32]) {
			case 29:
			case 30:
			case 137:
				updateval+=8;
		}

		c.updateStam(updateval);
	}
	public void doHPRegen(DSpiresSocket c) {
		try {
			c.setPriority((int)Math.round(Math.random()*3)+5);
		}
		catch (Exception e) {
			System.err.println("Caught:");
			e.printStackTrace();
		}
		if (c.hp < 20) {
			c.hp++;
			c.pSend("$H"+c.hp);
		}
		if (c.mp < 30) {
			if ((c.mp+=5) > 30) c.mp = 30;
				c.pSend("$M"+parent.toDSChar(c.mp));
		}
	}
	public void initParade() {

		int pi[] = {143};

		if (!paradeon[0]) {
			int[] tpath = new int[65+2];
			for (int i = 0; i < 27; i++) {
				tpath[i] = 3;
			}
			tpath[27]=0;
			tpath[28]=0;
			for (int i = 0; i < 38; i++) {
				tpath[29+i] = 1;
			}
			String tcss[] = {"    ",
							"!!!!",
							" ,#$",
							"!!!#",
							"!!!$",
							"%!\"!",
							"    "};
			parade[0] = new ParadePath(tpath,tcss,pi,12,13,parent.maps[0],parent);
		}

		if (!paradeon[1]) {
			int[] tpath2 = new int[67];
			for (int i = 0; i < 28; i++) {
				tpath2[i] = 3;
			}
			for (int i = 0; i < 39; i++) {
				tpath2[28+i] = 1;
			}

			String tcss2[] = {"    ",
								" &&%",
								"###\"",
								"\"-*$",
								"###$",
								" *-!",
								"    "};
			parade[1] = new ParadePath(tpath2,tcss2,pi,12,12,parent.maps[0],parent);
		}

		if (!paradeon[2]) {
			int[] tpath3 = new int[63+4];
			for (int i = 0; i < 26; i++) {
				tpath3[i] = 3;
			}
			tpath3[26]=0;
			tpath3[27]=0;
			tpath3[28]=0;
			tpath3[29]=0;
				for (int i = 0; i < 37; i++) {
				tpath3[30+i] = 1;
			}

			String tcss3[] = {"    ",
								"%#'!",
								"(((\"",
								"(((#",
								"%%*$",
								"(((%",
								"    "};
			parade[2] = new ParadePath(tpath3,tcss3,pi,11,14,parent.maps[0],parent);
		}

		parent.globalBroadcast("[\'(*)-(*) It's the DragonSpires Parade! (*)-(*)");
		parent.globalBroadcast("[\'(*)-(*) Type '-goparade' within the next 30 seconds to join in!");

		paradeon[0]=true;
		paradeon[1]=true;
		paradeon[2]=true;

		new GoParadeThread(parent);
	}
	public void paradeTimeCheck() {
		Calendar c = Calendar.getInstance();
		int i = c.get(Calendar.HOUR_OF_DAY);
		if (i!=lasthour) {
			lasthour=i;
			switch (lasthour) {
				case 1:
				case 8:
				case 16:
					initParade();
			}
		}
	}
}

