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

public class NPC2Thread extends Thread {
	DSpiresServer parent;
	Vector npc2s;
	//int offset;

	public NPC2Thread(DSpiresServer p) {
		setPriority(Thread.MIN_PRIORITY);
		npc2s = new Vector();
		parent = p;
		//offset = ((int)Math.round(Math.random() * 6))*1500;
	}
	public void run() {
		while (true) {
			for (int i = 0; i < npc2s.size(); i++) {
				NPC2 t = (NPC2)npc2s.elementAt(i);
				if (t.map.sockets.size() > 0) {
					if (t.timed == 0) {
						if (t.s2s.length > 0)
							t.map.limitedBroadcast("("+t.name+": "+t.s2s[(int)Math.round(Math.random() * (t.s2s.length-1))],t.x,t.y,t.map);
						t.timed = t.otimed;
					}
					else
						t.timed--;
				}
			}
			try {
				sleep(10000);
			}
			catch (InterruptedException e) {
			}
			yield();
		}
	}
	public NPC2 findNPC2(int x, int y, DSMapServer m) {
		//synchronized (npc2s) {
			NPC2 c;
			Enumeration e = npc2s.elements();
			while (e.hasMoreElements()) {
				c = (NPC2)e.nextElement();
				if (c.map==m) {
					if (c.x==x&&c.y==y)
						return c;
				}
			}
		//}
		return null;
	}
}
