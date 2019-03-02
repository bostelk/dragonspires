/*

Dodgeball.java (for the Java DragonSpires Server)
Copyright (c) 2000, Adam Maloy
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

public class Dodgeball extends Thread {
	int x,y,spaces,dir,toshoot;
	Object owner;
	DSMapServer map;
	static DSpiresServer parent;
	long speed=150;

	public Dodgeball(int ts, int ox, int oy, int d, int s, DSMapServer m, Object o, DSpiresServer p) {
		toshoot=ts;
		x=ox;
		y=oy;
		dir=d;
		spaces=s;
		map=m;
		owner=o;
		parent=p;
		//setPriority(2);
		switch (toshoot) {
			case 177:
				speed=170;
				break;
			case 176:
				speed=160;
		}
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
			
			if (map.playermap[tx][ty]==0&&(map.tilemap[tx][ty]==41||map.tilemap[tx][ty]==4)) {
				//map.limitedBroadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toshoot)+""+parent.toDSChar(xf)+""+parent.toDSChar(yf)+""+parent.encode(map.itemmap[xf][yf]),tx,ty,map);
				map.broadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toshoot)+""+parent.toDSChar(xf)+""+parent.toDSChar(yf)+""+parent.encode(map.itemmap[xf][yf]),map);
				xf=tx;
				yf=ty;
			}
			else
				break;

			try {
				sleep(speed);
			}
			catch (InterruptedException e) {}
		}
		try {
			int i = map.itemmap[xf][yf];
			map.placeItemAt(toshoot,xf,yf,map);
			if (i!=0)
				((Lev24)map).randomDodgeballPlace(i);
			DSpiresSocket c=((DSpiresSocket)owner).findSocketAtPos(parent.nextx(xf,yf,((DSpiresSocket)owner).facing),parent.nexty(yf,((DSpiresSocket)owner).facing),map,1);
			if (c!=null)
				playerIsHit(xf,yf,(DSpiresSocket)owner,c);
		} catch (Exception e) {}
	}
	public void playerIsHit(int tx, int ty, DSpiresSocket o, DSpiresSocket c) {
		if (o.colorstring.charAt(0)!=c.colorstring.charAt(0)) {
			c.map.broadcast("["+(c.colorstring.charAt(0)=='*'?"#":"&")+"{D} "+c.name+" has been hit by "+o.name+"!",map);
			switch (c.inhand) {
				case 175:
				case 176:
				case 177:
					if (map.itemmap[tx][ty]==0)
						map.placeItemAt(c.inhand,tx,ty,map);
					else
						((Lev24)map).randomDodgeballPlace(c.inhand);
							c.setHands(0);											
			}
			c.movePlayerIntoStartArea();
			((Lev24)map).addScore(o.colorstring.charAt(0));
		}
	}
}