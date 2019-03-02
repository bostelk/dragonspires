/*

Java DragonSpires Client
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

import java.util.Vector;
import java.util.Enumeration;

public class stuffThread2 extends Thread {

	DragonSpiresPanel parent;
	int cachecheck;

	public stuffThread2 (DragonSpiresPanel p) {
		setPriority(Thread.MIN_PRIORITY);
		parent = p;
	}
	public void run() {
		while (true) {
			try {

				this.sleep(72000);
				//if (++cachecheck >= 18) //4000
					parent.map.cacheCleanup();

				/*if (parent.spells.size() > 0) {
					synchronized (parent.spells) {
						Enumeration e=parent.spells.elements();
						Spell s;
						while (e.hasMoreElements()) {
							s=(Spell)e.nextElement();
							parent.map.spellmap[s.x][s.y]=0;
						}
						parent.spells.removeAllElements();
						parent.map.repaint();
					}
				}*/

			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*public void regenStam() {
		stamreg = 2;
		parent.stam+=8;
		if (parent.map.playermap[parent.map.xpos][parent.map.ypos] == 22) {
			parent.stam+=10;
			if (parent.map.itemmap[parent.map.xpos][parent.map.ypos] == 137)
				parent.stam+=8;
		}

		switch (parent.ahands) {
			case 132:
				parent.stam+=10;
				break;
			case 32:
				parent.stam+=8;
		}

		if (parent.stam > 116)
			parent.stam = 116;

		parent.drawStam(parent.getGraphics());
	}*/
}