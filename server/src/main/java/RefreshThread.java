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

public class RefreshThread extends Thread {
	final static int NPC_RESET = 10,
						MAP_RESET=13;

	int npc_reset_count=NPC_RESET,
		map_reset_count=MAP_RESET;


	DSpiresServer parent;

	public RefreshThread(DSpiresServer p) {
		parent=p;
		this.setPriority(1);
	}
	public void run() {
		while (true) {
			try {
				sleep(3600000);
				parent.uptime++;
				parent.logStats();
			}
			catch(InterruptedException e) {
			}

			refreshHolders();

			if (--npc_reset_count==0)
				resetNPCs();

			if (--map_reset_count==0)
				resetMaps();

		}
	}
	public void refreshHolders() {
		for (int m = 0; m < parent.maps.length; m++) {
			if (parent.maps[m].holders != null) {
				for (int i = 0; i < parent.maps[m].holders.length; i+=4) {
					try {
						if (parent.maps[m].holders[i+3] <= 0) {
							parent.maps[m].holders[i+3] = 1;
							if (parent.maps[m].itemmap[parent.maps[m].holders[i]][parent.maps[m].holders[i+1]] != 15)
								parent.maps[m].placeItemAt(15,parent.maps[m].holders[i],parent.maps[m].holders[i+1],parent.maps[m]);
						}
					}
					catch (Exception e) {
						System.out.println("Holders error on map "+(m+1));
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void resetNPCs() {
		Enumeration e;
		NPC2 npc2;
		NPC3 npc3;

		e=parent.npc2base.npc2s.elements();
		while (e.hasMoreElements()) {
			npc2=(NPC2)e.nextElement();
			if (npc2 instanceof Resetable)
				((Resetable)npc2).reset();
		}

		DSMapServer map;
		for (int m = 0; m < parent.maps.length; m++) {
			map = parent.maps[m];
			if (map.npc3s != null) {
				for (int i = 0; i < map.npc3s.size(); i++) {
					npc3 = (NPC3)map.npc3s.elementAt(i);
					if (npc3 instanceof Resetable)
						((Resetable)npc3).reset();
				}
			}
		}
		npc_reset_count=NPC_RESET;
	}
	public void resetMaps() {
		DSMapServer m;

		for (int i=0;i<parent.maps.length;i++) {
			int x,y;
			m=parent.maps[i];
			new MapUpdateThread(m,m.mapname+".dsmap",m.mapname+".ini");
			try {
				sleep(200);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		map_reset_count=MAP_RESET+(int)(Math.round(Math.random()*5)-2);
	}
}