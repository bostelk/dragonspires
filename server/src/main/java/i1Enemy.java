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

public class i1Enemy extends Enemy {

	int previtem;

	public i1Enemy() {}

	public i1Enemy(int eie, int x, int y, boolean nwd, DSMapServer m) {
		if (m.enemybase.enemies.size() >= m.MAX_ENEMIES)
			return;

		init(eie,x,y,nwd,m);
	}
	public void init(int eie, int x, int y, boolean nwd, DSMapServer m) {
		this.x = map.parent.toDSChar(x);
		this.y = map.parent.toDSChar(y);
		ox = this.x;
		oy = this.y;
		nullwhendie = nwd;
		me = m.parent.enemyIndex[eie];
		hp = me.maxhp;
		currshape = 2;
		mstate = 1;
		tilltrig = me.maxtilltrig;
		map = m;
		ie = eie;
		previtem=m.itemmap[x][y];
		m.placeItemAt(me.enemy_item,x,y,m);
	}
	public void trigger() {
		if (killed==0) {
			try {
				int move = getNearestPlayerDir(x-32,y-32,map);

				if (move == -1)
					move = map.parent.shortfaceconv[(int)Math.round(Math.random() * 3)];

				move(move);

				if (me.projectile&&map.canWalk(map.parent.nextx(x-32,y-32,facing),map.parent.nexty(y-32,facing),map)) {
					new DSBullet(me.projectile_item,x-32,y-32,facing,me.projectile_length,map,this,map.parent);
					return;
				}

				int tx = map.parent.nextx(x-32,y-32,facing);
				int ty = map.parent.nexty(y-32,facing);

				if (map.playermap[tx][ty] != 0)
					attack(tx,ty);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			if (--killed==0) {
				previtem=map.itemmap[x-32][y-32];
				map.placeItemAt(me.enemy_item,x-32,y-32,map);
			}
		}
	}
	public void move(int dir) {
		int tx = map.parent.nextx(x-32,y-32,dir);
		int ty = map.parent.nexty(y-32,dir);

		facing=dir;

		if (map.canWalk(tx,ty,map) && map.playermap[tx][ty]==0) {
			char bx=x,by=y;
			x=map.parent.toDSChar(tx);			
			y=map.parent.toDSChar(ty);
			map.broadcast(">"+x+""+y+""+map.parent.encode(me.enemy_item)+""+bx+""+by+""+map.parent.encode(previtem),map);
			map.itemmap[bx-32][by-32]=previtem;
			if (map.itemmap[tx][ty]!=me.enemy_item) {
				previtem=map.itemmap[tx][ty];
				map.itemmap[tx][ty]=me.enemy_item;
			}
		}
	}
	public void die() {
		if (special_die)
			whendie();

		map.placeItemAt(previtem,x-32,y-32,map);

		BagOStuff b=map.addBag(x-32,y-32,me.gold,new int[0],map);
		for (int i=0;i<me.drop.length;i++) {
			if (me.drop[i]!=0 && (int)Math.round(Math.random() * 2) <= 1)
				b.addItem(me.drop[i],b,map);
		}

		if (nullwhendie) {
			map.removeEnemy(this);
			return;
		}

		x = ox;
		y = oy;
		hp=me.maxhp;
		killed=5;
	}
}