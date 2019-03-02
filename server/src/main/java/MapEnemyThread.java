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

public class MapEnemyThread extends Thread {
	DSMapServer parentMap;
	Vector enemies;
	//boolean running=false;
	int yieldcount;

	public MapEnemyThread(DSMapServer p) {
		parentMap = p;
		enemies = new Vector();
		setPriority(Thread.MIN_PRIORITY);
	}
	public void run() {
		//running=true;
		Enemy e;
		Enumeration enum;
		while (enemies.size()>0) {
			try {
				if (parentMap.sockets.size() > 0) {
					enum = enemies.elements();
					while (enum.hasMoreElements()) {
						if (++yieldcount > 10) {
							yieldcount = 0;
							yield();
						}
						e = (Enemy)enum.nextElement();
						try {
							if (e.isReady()) {
								if (e.special_trig)
									e.mytrigger();
								else if (e instanceof i1Enemy)
									((i1Enemy)e).trigger();
								else
									e.trigger();
							}
						}
						catch (Exception exx) {
							System.err.println("Enemy removed because of:");
							exx.printStackTrace();
							enemies.removeElement(e);
						}
					}
					Thread.sleep(parentMap.parent.sleeptime+( (int)Math.round(Math.random()*1000) ));
				}
				else
					Thread.sleep(41000+( (int)Math.round(Math.random()*1000) ));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}		
			yield();
		}
		//running=false;
	}
}
