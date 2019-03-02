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

public class Balloon extends Animal {

	public Balloon(int tx, int ty, DSMapServer m, AnimalThread p) {
		balloonInit(tx,ty,m,p,this);
	}
	protected static void balloonInit(int tx, int ty, DSMapServer m, AnimalThread p, Balloon c) {
		parent = p;
		c.cx = parent.parent.toDSChar(tx);
		c.cy = parent.parent.toDSChar(ty);
		c.ox = c.cx;
		c.oy = c.cy;
		c.name = "";
		c.tilltrig = 2;
		c.maxtilltrig = 2;
		c.map = m;
		m.itemmap[tx][ty] = 143;
	}
	public void trigger() {
		balloonMove(this);
	}
	protected static void balloonMove(Balloon c) {
		int tx = c.cx-32;
		int ty = c.cy-32;

		int dir = (int)Math.round(Math.random() * 3);

		int dir2 = 0;
		switch (dir) {
			case 1:	dir2 = 7;
					break;
			case 2:	dir2 = 9;
					break;
			case 3:	dir2 = 1;
					break;
			case 4:	dir2 = 3;
		}
		
		//int[] tempo = parent.parent.maps[c.mapnumber].findNextFloor(dir2,tx,ty);
		tx = parent.parent.nextx(tx,ty,dir2);
		ty = parent.parent.nexty(ty,dir2);

		if (c.map.itemmap[tx][ty] == 0 && c.map.canWalk(tx,ty,c.map)) {

			c.facing = dir;

			char bx = c.cx;
			char by = c.cy;
			c.cx = parent.parent.toDSChar(tx);
			c.cy = parent.parent.toDSChar(ty);
			c.map.broadcast(">"+c.cx+""+c.cy+""+parent.parent.encode(143)+""+bx+""+by+"  ",c.map);
			c.map.itemmap[c.cx-32][c.cy-32] = 143;
			c.map.itemmap[bx-32][by-32] = 0;
		}
	}
}
