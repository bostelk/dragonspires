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

import java.util.Enumeration;
import java.util.Vector;

public class MapUpdateThread extends Thread {
	DSMapServer m;
	String mapname,scriptname;

	public MapUpdateThread(DSMapServer map, String mn, String sn) {
		setPriority(Thread.MAX_PRIORITY);
		m=map;
		mapname=mn;
		scriptname=sn;
		start();
	}
	public void run() {
		int x,y;

		m.getIMapData("../maps/"+mapname,m);
		if (scriptname!=null)
			m.readMapScript(scriptname,m);
		else {
			if (m.portals!=null) {
				for (int i=0;i<m.portals.length;i++) {
					if (m.portals[i]!=null) {
						if (m.portals[i].active) {
							if (m.portals[i].dest_map instanceof BasicMap) {
								m.placeItemAt(151,m.portals[i].orig_x,m.portals[i].orig_y,m);
	
							}
						}
					}
				}
			}
		}

		m.broadcast("[&* This map has been updated.",m);

		//synchronized (m.sockets) {
			Enumeration e = ((Vector)(m.sockets.clone())).elements();
			DSpiresSocket s;
			while (e.hasMoreElements()) {
				try {
					s = (DSpiresSocket)e.nextElement();
					//x=s.cx-32;
					//y=s.cy-32;
					s.changeMap(s.map,2,0,0);
					//s.movePlayer(x,y,s);

					sleep(700);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		//}

		//stop();
	}
}