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

public class DSSpellShoot extends Thread {
	int x,y,spaces,dir;
	int toshoot;
	Object owner;
	DSMapServer map;
	static DSpiresServer parent;
	Weapon theweapon;

	public DSSpellShoot(int ts, int ox, int oy, int d, int s, Weapon w, DSMapServer m, Object o, DSpiresServer p) {
		toshoot=ts;
		x=ox;
		y=oy;
		dir=d;
		spaces=s;
		map=m;
		owner=o;
		parent=p;
		theweapon=w;
		//setPriority(Thread.MIN_PRIORITY);
		start();
	}
	public void run() {
		int xf = x;
		int yf = y;
		int tx = parent.nextx(x,y,dir);
		int ty = parent.nexty(y,dir);

		for (int i=0;i<spaces;i++) {
			tx = parent.nextx(xf,yf,dir);
			ty = parent.nexty(yf,dir);
			
			map.limitedBroadcast(parent.assembleSpellString(toshoot,tx,ty),tx,ty,map);
			if (map.canWalk(tx,ty,map)) {
				//map.limitedBroadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toshoot)+""+parent.toDSChar(xf)+""+parent.toDSChar(yf)+""+parent.encode(map.itemmap[xf][yf]),tx,ty,map);
				xf=tx;
				yf=ty;
			}
			else {
				//if (!(xf==x&&yf==y))
				//	map.limitedBroadcast(">"+parent.toDSChar(xf)+""+parent.toDSChar(yf)+""+parent.encode(map.itemmap[xf][yf]),xf,yf,map);

				if (owner instanceof DSpiresSocket) {
					DSpiresSocket socketowner=(DSpiresSocket)owner;
					if (map.allowcombat) {
						if (socketowner.okToAttack(tx,ty,map)) {
							DSpiresSocket c = socketowner.findSocketAtPosInSight(tx,ty,1);
							if (c != null) {
								if (c.colorstring.charAt(3) == ' ') return;

								if (parent.dice(1,4)==1) {
									socketowner.doPlayerHit(tx,ty,c);
								}		
								return;
							}
						}
					}

					Enemy c = map.findEnemyAtPos(tx,ty,map);
					if (c != null) {
						Weapon abitch = socketowner.weaponO;
						socketowner.weaponO=theweapon;
						c.damage(socketowner,true);
						socketowner.weaponO=abitch;
						return;
					}
				}
				else if (owner instanceof Enemy) {
					if (((Enemy)owner).map.playermap[tx][ty] != 0)
						((Enemy)owner).attack(tx,ty);
				}
				return;
			}

			try {
				sleep(170);
			}
			catch (InterruptedException e) {}
		}
		//if (!(xf==x&&yf==y))
		//	map.limitedBroadcast(">"+parent.toDSChar(xf)+""+parent.toDSChar(yf)+""+parent.encode(map.itemmap[xf][yf]),xf,yf,map);
	}
}