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

public class ParadePath {
	static DSpiresServer parent;
	//String[] colorstrings;
	//int[][] positions;
	PlayerDataObject[] pdos;
	int[] posonpath;


	int[] path,paradeitems;
	int startcount,fincount,x,y,mstate;
	DSMapServer map;

	public ParadePath(int[] tpath, String[] tcss, int[] pi, int tx, int ty, DSMapServer m, DSpiresServer p) {
		x = tx;
		y = ty;
		paradeitems = pi;
		parent = p;
		path = tpath;
		//colorstrings = tcss;
		pdos = new PlayerDataObject[tcss.length];
		for (int i = 0;i<tcss.length; i++)
			pdos[i]=new PlayerDataObject(' ',' ',tcss[i]);
		map = m;
		//positions = new int[colorstrings.length][3];
		posonpath = new int[tcss.length];
		mstate = 1;
		reset(this);
	}
	static void reset(ParadePath p) {
		for (int i = 0; i < p.pdos.length; i++) {
			//p.positions[i][0] = p.x;
			//p.positions[i][1] = p.y;
			//p.positions[i][2] = 0;
			p.pdos[i].x=(char)(p.x+32);
			p.pdos[i].y=(char)(p.y+32);
			p.posonpath[i]=0;
		}
		p.startcount = 0;
		p.fincount = 0;
	}
	static boolean continueParade(ParadePath p) {
		p.map.broadcast("~",p.map);
		if (p.startcount < p.pdos.length) {
			p.map.pdoInit(p.pdos[p.startcount],parent.shortShapeStart[parent.shapestartpointers[p.pdos[p.startcount].colorstring.charAt(3)-32]][parent.faceconv[p.path[p.posonpath[p.startcount]]]]+p.mstate,p.map);
			p.startcount++;
		}

		for (int i = p.fincount; i < p.startcount; i++) {
			//if (p.positions[i][2] <= p.path.length) {
				//System.out.println("pos:"+p.positions[i][2]+" plen:"+p.path.length);
				if (p.posonpath[i] == p.path.length) {
					p.map.pdoPlace(p.pdos[i],0,p.pdos[i].colorstring,p.pdos[i].x,p.pdos[i].y,p.map);
					p.fincount++;
				}
				/*else if (p.positions[i][2]==0) {
					parent.maps[p.mapnumber].pdoInit(parent.shortShapeStart[parent.shapestartpointers[p.colorstrings[i].charAt(3)-32]][parent.faceconv[p.path[p.positions[i][2]]]]+p.mstate,p.colorstrings[i],parent.toDSChar(p.positions[i][0]),parent.toDSChar(p.positions[i][1]),parent.maps[p.mapnumber]);
					p.positions[i][2]++;
				}*/
				else {
					//int[] tempos = parent.maps[p.mapnumber].findNextFloor(p.path[p.positions[i][2]],p.positions[i][0],p.positions[i][1]);
					if (p.path[p.posonpath[i]]==0) {
						p.posonpath[i]++;
						if (i==0)
							break;
					}

						
					int tx = parent.nextx(p.pdos[i].x-32,p.pdos[i].y-32,p.path[p.posonpath[i]]);
					int ty = parent.nexty(p.pdos[i].y-32,p.path[p.posonpath[i]]);
					boolean oktomove = p.map.canWalk(tx,ty,p.map);
					if (!oktomove) {
						DSpiresSocket c = null;
						if (p.map.sockets.size() > 0)
							c = ((DSpiresSocket)p.map.sockets.elementAt(0)).findSocketAtPos(tx,ty,p.map,1);
						if (c != null) {
							c.transformPlayerToItem(144);
							oktomove = true;
						}
						else if (p.map.itemmap[tx][ty]==151||p.map.itemmap[tx][ty]==23)
							oktomove=true;
						else
							p.map.pdoPlace(p.pdos[i],parent.shortShapeStart[parent.shapestartpointers[p.pdos[i].colorstring.charAt(3)-32]][parent.faceconv[p.path[p.posonpath[i]]]]+p.mstate,p.pdos[i].colorstring,p.pdos[i].x,p.pdos[i].y,p.map);
					}
					if (oktomove) {
						p.map.pdoMove(p.pdos[i],parent.shortShapeStart[parent.shapestartpointers[p.pdos[i].colorstring.charAt(3)-32]][parent.faceconv[p.path[p.posonpath[i]]]]+p.mstate,p.pdos[i].colorstring,parent.toDSChar(tx),parent.toDSChar(ty),p.pdos[i].x,p.pdos[i].y,p.map);

						if (p.map.itemmap[tx][ty] == 0) {
							int a = (int)Math.round(Math.random() * 9);
							if (a == 0)
								p.map.placeItemAt(p.paradeitems[((int)Math.round(Math.random() * (p.paradeitems.length-1)))],tx,ty,p.map);
						}
						//p.pdos[i].x = parent.toDSChar(tx);
						//p.pdos[i].y = parent.toDSChar(ty);
						p.posonpath[i]++;
					}
				}
			//}
		}

		p.map.broadcast("=",p.map);
		//System.out.println("fincount:"+p.fincount);
		if (p.fincount == p.pdos.length) {
			reset(p);
			return false;
		}

		p.mstate*=-1;

		return true;
	}
}