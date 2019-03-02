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

public class Sweeper extends NPC3 {

	public Sweeper(int x, int y, NPC3Thread p, DSMapServer m) {
		map=m;
		init("a|street|sweeper","&$!%",x,y,1,5,p);
		talk = 0;
	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random() * 2);
			switch (say) {
				case 0:	say("What's that over there?");
						break;
				case 1:	send("(Flies buzz around Street Sweeper.");
						break;
				case 2:	say("Hey. What's this?");
			}
			talk = 0;
		}

		int move=getGetItemDir(x-32,y-32);
		if (move==-1)
			move=parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)];
		move=decideDir(move,this);
		move(move);

		if (map.itemmap[x-32][y-32]!=0) {
			try {
				send("(Street|Sweeper sweeps up "+parent.parent.itemnames[map.itemmap[x-32][y-32]]+".");
			}
			catch (Exception e) {
				send("(Street|Sweeper sweeps something up.");
			}
			map.placeItemAt(0,x-32,y-32,map);
		}
	}
	public int getGetItemDir(int tx, int ty) {

		int xs = tx-3;
		int xe = tx+3;

		if (xs < 13)
			xs = 13;
		else if (xe > 27)
			xe = 27;

		int ys = ty-8;
		int ye = ty+8;

		if (ys < 8)
			ys = 8;
		else if (ye > 66)
			ye = 66;

		int currnearx = 0;
		int currneary = 0;
		int canget=0;
		for (int x = xs; x < xe; x++) {
			for (int y = ys; y < ye; y++) {
				if (map.itemmap[x][y]!=0) {
					try {
						canget=map.parent.itemdefs[map.itemmap[x][y]][1];
					}
					catch (Exception e) {
						canget=0;
					}
					if (canget!=1&&map.parent.floorwalk[map.tilemap[x][y]]!=1) {
						if ((Math.abs(tx-x) < Math.abs(tx-currnearx)) || (Math.abs(ty-y) < Math.abs(ty-currneary))) {
							currnearx = x;
							currneary = y;
						}
					}
				}
			}
		}

		if (ty%2==0) {
			if (currnearx > tx) {
				if (currneary <= ty)
					return 9;
				else
					return 3;
			}
			else {
				if (currneary <= ty)
					return 7;
				else
					return 1;
			}
		}
		else {
			if (currnearx < tx) {
				if (currneary <= ty)
					return 7;
				else
					return 1;
			}
			else {
				if (currneary <= ty)
					return 9;
				else
					return 3;
			}
		}
	}
	protected static int decideDir(int d, NPC3 e) {
		int x, y, t,tx=e.x-32,ty=e.y-32,tempval;
		x=parent.parent.nextx(tx,ty,d);
		y=parent.parent.nexty(ty,d);
		if (e.map.tilemap[x][y]==6)
			return d;
		
		t=parent.parent.intRotateLeft(d);
		x=parent.parent.nextx(tx,ty,t);
		y=parent.parent.nexty(ty,t);
		if (e.map.tilemap[x][y]==6)
			return t;

		t=parent.parent.intRotateRight(d);
		x=parent.parent.nextx(tx,ty,t);
		y=parent.parent.nexty(ty,t);
		if (e.map.tilemap[x][y]==6)
			return t;
		
		t=parent.parent.intRotateRight(t);
		x=parent.parent.nextx(tx,ty,t);
		y=parent.parent.nexty(ty,t);
		if (e.map.tilemap[x][y]==6)
			return t;

		return d;
	}
}
