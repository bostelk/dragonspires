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
import java.io.*;

public class DSMapServer {

	static DSpiresServer parent;
	DSMapServer parentmap;
	final static int mwidth=52,mheight=100,items=346,MAX_ENEMIES = 40;

	static String[] graffitti;

	Vector sockets,pdos,holes,bags;

	String[] readables;
	String mapname,entrytext,owner;

	int[][] tilemap,itemmap,oitemmap,playermap;
	int[] holders = null,exits={-2,-2,-2,-2},attacktiles={0,0,0,0,0};
	int stamval=-1,maxUploads=0;

	short mapnumber,xstart=26,ystart=41,diemap=0,fountainx=-1,fountainy=-1;

	boolean safemap=false, walktrig=false, allowupload=false,allowcombat=false,removingenemy=false;;

	Shop[] shops;
	//Enemy[] enemies;
	Vector npc3s;
	Portal[] portals;
	MapEnemyThread enemybase;
	Vector laptops;

	public DSMapServer() {}
	public DSMapServer(String mname, int mnumber) {
		mapname = mname;
		mapnumber = (short)mnumber;
		init(mapname+".dsmap",this);
	}
	public DSMapServer(String mname, String oname, int mnumber) {
		mapname = mname;
		mapnumber = (short)mnumber;
		init(mapname+".dsmap",oname,this);
	}

	protected static void init(String mapfile, DSMapServer m) {
		m.sockets = new Vector();
		m.pdos = new Vector();

		m.tilemap = new int[mwidth][mheight+2];
		m.itemmap = new int[mwidth][mheight+2];
		m.playermap = new int[mwidth][mheight+2];
		m.oitemmap = new int[mwidth][mheight+2];

		getMapData("../maps/"+mapfile,m);
	}
	protected static void init(String mapfile, String oimapfile, DSMapServer m) {
		init(oimapfile,m);
		getIMapData("../maps/"+mapfile,m);
	}

	public void walkTrigger(DSpiresSocket c){}

	protected static void getMapData(String mapname, DSMapServer m) {
		try {
			FileInputStream i = new FileInputStream(new File(mapname));
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					m.tilemap[x][y] = parent.decode(i.read(),i.read());
					if (m.tilemap[x][y]>parent.floorwalk.length||m.tilemap[x][y]<0)
						m.tilemap[x][y]=0;
				}
			}
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					m.itemmap[x][y] = parent.decode(i.read(),i.read());
					if (m.itemmap[x][y]>items||m.itemmap[x][y]<0)
						m.itemmap[x][y]=0;
					m.oitemmap[x][y] = m.itemmap[x][y];
				}
			}
			i.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected static Exception read_map_from_reader(Reader i, DSMapServer m, DSpiresSocket c) {
		try {
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					m.tilemap[x][y] = parent.decode(i.read()-32,i.read()-32);
					if (m.tilemap[x][y]>parent.floorwalk.length||m.tilemap[x][y]<0)
						m.tilemap[x][y]=0;
				}
			}
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					m.itemmap[x][y] = parent.decode(i.read()-32,i.read()-32);
					if (m.itemmap[x][y]>items||m.itemmap[x][y]<0)
						m.itemmap[x][y]=0;
					m.oitemmap[x][y] = m.itemmap[x][y];
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return e;
		}
		return null;
	}
	protected static void getIMapData(String mapname, DSMapServer m) {
		try {
			FileInputStream i = new FileInputStream(new File(mapname));
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					i.skip(2);
				}
			}
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					m.itemmap[x][y] = parent.decode(i.read(),i.read());
					if (m.itemmap[x][y]>items||m.itemmap[x][y]<0)
						m.itemmap[x][y]=0;
				}
			}
			i.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected static boolean canWalk(int x, int y, DSMapServer m) {
		try {
			if (m.playermap[x][y] != 0 
					|| parent.itemdefs[m.itemmap[x][y]][0] != 0
					|| parent.floorwalk[m.tilemap[x][y]] != 0)
				return false;
		}
		catch (Exception e) {}
		return true;
	}
	protected static boolean dsPinR2(int cx, int cy, int x, int y) {
		return (cy % 2 == 0) ? even_dsPinR2(cx,cy,x,y) : odd_dsPinR2(cx,cy,x,y);
	}
	protected static boolean even_dsPinR2(int cx, int cy, int x, int y) {
		return (x >= cx-3 &&
			x <= cx+3 &&
			y >= cy-8 &&
			y <= cy+8 &&
			!(x == cx-3 && y%2 == 1));
	}
	protected static boolean odd_dsPinR2(int cx, int cy, int x, int y) {
		return (x >= cx-3 &&
			x <= cx+3 &&
			y >= cy-8 &&
			y <= cy+8 &&
			!(x == cx+3 && y%2 == 0));
	}
	protected static void playerMoveBroadcast(int shape, String colorstring, char x, char y, char x2, char y2, DSpiresSocket s, DSMapServer m) {
		boolean inrectshape,inrectblank;

		String tosend = "<"+x+""+y+""+parent.toDSChar(shape)+""+colorstring+""+x2+""+y2+" ";

		x=(char)s.x;
		y=(char)s.y;
		x2-=32;
		y2-=32;

		m.playermap[x][y] = shape;
		//m.colorstrings[x][y] = colorstring;
		m.playermap[x2][y2] = 0;
		//m.colorstrings[x2][y2] = "";

		DSpiresSocket c;
		//synchronized(m.sockets) {
			Enumeration e = m.sockets.elements();
			while (e.hasMoreElements()) {
			//for (int i = 0; i < m.sockets.size(); i++) {
				c = (DSpiresSocket)e.nextElement();
				//c = (DSpiresSocket)m.sockets.elementAt(i);
				inrectshape = dsPinR2(c.x,c.y,x,y);
				inrectblank = dsPinR2(c.x,c.y,x2,y2);
				if (inrectshape && inrectblank){
					//c.myplayermap[x-32][y-32] = shape;
					//c.myplayermap[x2-32][y2-32] = 0;
					c.pSend(tosend);
				}
				else if (!inrectshape && inrectblank) {
					//c.myplayermap[x2-32][y2-32] = 0;
					c.pSend("<"+(char)(x2+32)+""+(char)(y2+32)+" ");
						c.sight.removeElement(s);
					//if (s != null)
						s.pSend("<"+c.cx+""+c.cy+" ");
						s.sight.removeElement(c);
				}
				else if (inrectshape && !inrectblank) {
					//c.myplayermap[x-32][y-32] = shape;
					c.pSend("<"+(char)(x+32)+""+(char)(y+32)+""+parent.toDSChar(shape)+""+colorstring);
					c.sight.addElement(s);
					//if (s != null)
						s.pSend("<"+c.cx+""+c.cy+""+parent.toDSChar(c.visishape)+""+c.colorstring);
					s.sight.addElement(c);
				}
			}
		//}
		if (m.pdos.size() > 0)
			pdoForPlayerMove(x,y,x2,y2,s);
	}
	protected static  void playerPlaceBroadcast(int shape, String colorstring, char x, char y, DSpiresSocket s, DSMapServer m) {
		m.playermap[s.x][s.y] = shape;
		//m.colorstrings[x-32][y-32] = colorstring;

		String tosend = "<"+x+""+y+""+parent.toDSChar(shape)+""+colorstring;

		if (shape==0) {
			if (!colorstring.equals(""))
				s.sight.removeElement(s);
		}

		DSpiresSocket c;
		//synchronized(s.sight) {
			Enumeration e = s.sight.elements();
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();
				c.pSend(tosend);

			}
		//}
		if (shape==0) {
			if (!colorstring.equals(""))
				clearSight(s);
		}
	}
	protected static void clearSight(DSpiresSocket s) {
		DSpiresSocket c;
		//synchronized(s.sight) {
			Enumeration e = s.sight.elements();
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();
				c.sight.removeElement(s);
			}
		//}
		s.sight.removeAllElements();
	}
	protected static void itemRecover(int item, DSMapServer m) {
		boolean found = false;
		if (item<=m.items) {
		for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				if (m.oitemmap[x][y] == item && m.itemmap[x][y] == 0) {
					found = true;
					placeItemAt(item,x,y,m);
					break;
				}
			}
		}
		}
		if (!found) {
			int x = (int)Math.round(Math.random() * (mwidth-1));
			int y = (int)Math.round(Math.random() * (mheight-1));
			while (!canWalk(x,y,m) || m.itemmap[x][y] != 0) {
				x = (int)Math.round(Math.random() * (mwidth-1));
				y = (int)Math.round(Math.random() * (mheight-1));
			}
			if (item<=m.items)
				placeItemAt(item,x,y,m);
			else
				addAnimal(item,x,y,m);
		}
	}
	protected static void broadcast(String message, DSMapServer m) {
		//synchronized(m.sockets) {
			Enumeration e = m.sockets.elements();
			DSpiresSocket c;
			while (e.hasMoreElements()) {
			//for (int i = 0; i < m.sockets.size(); i++) {
				try {
					c = (DSpiresSocket)e.nextElement();
					//c = (DSpiresSocket)m.sockets.elementAt(i);
					c.pSend(message);
				}
				catch (NoSuchElementException ex) {
				}
			}
		//}
	}
	protected static void placeItemAt(int item, int x, int y, DSMapServer m) {
		m.itemmap[x][y] = item;
		broadcast(">"+parent.toDSChar(x)+""+parent.toDSChar(y)+""+parent.encode(item),m);
		if (item > items || item == 143)
			addAnimal(item,x,y,m);
	}
	/*protected static void itemTimedRecover(int item, int map) {
		parent.sthread.irecover.addElement(parent.toDSChar(item)+"3"+(char)(map+32));
	}*/
	/*protected static int[] findNextFloor(int dir, int tx, int ty) {
		char xc = 0;
		char yc = 0;
		int oe = ty % 2;

		switch (dir) {
			case 7:
				if (oe == 1) xc = '-';
				yc = '-';
				break;
			case 9:
				if (oe == 0) xc = '+';
				yc = '-';
				break;
			case 1:
				if (oe == 1) xc = '-';
				yc = '+';
				break;
			case 3:
				if (oe == 0) xc = '+';
				yc = '+';
		}

		switch (xc) {
			case '+':
				tx++;
				if (tx > parent.exitpoints[0])
					tx = parent.exitpoints[0];
				break;
			case '-':
				tx--;
				if (tx < parent.exitpoints[1])
					tx = parent.exitpoints[1];
		}

		switch (yc) {
			case '+':
				ty++;
				if (ty > parent.exitpoints[3])
					ty = parent.exitpoints[3];
				break;
			case '-':
				ty--;
				hapemodifier[3] = 0;
				c.colorstr					ty = parent.exitpoints[2];
		}

		int[] retval = new int[2];
		retval[0] = tx;
		retval[1] = ty;
		return retval;
	}*/
	protected static void limitedBroadcast(String message,int x, int y, DSMapServer m) {
		//synchronized(m.sockets) {
			Enumeration e = m.sockets.elements();
			DSpiresSocket c;
			while (e.hasMoreElements()) {
			//for (int i = 0; i < m.sockets.size(); i++) {
				c = (DSpiresSocket)e.nextElement();
				//c = (DSpiresSocket)m.sockets.elementAt(i);
				if (dsPinR2(c.cx-32,c.cy-32,x,y))
					c.pSend(message);
			}
		//}
	}
	protected static void doBall(int dir, int x, int y, int item, DSMapServer m) {
		//int[] tempo = findNextFloor(dir,x,y);
		int tx = parent.nextx(x,y,dir);
		int ty = parent.nexty(y,dir);
		boolean ok = false;
		for (int i = 0; i < 4; i++) {
			if (!canWalk(tx,ty,m) || m.itemmap[tx][ty] != 0) {
				dir-=2;
				if (dir < 1)
					dir = 9;
				else if (dir == 5)
					dir = 3;
				tx = parent.nextx(x,y,dir);
				ty = parent.nexty(y,dir);
			}
			else {
				//tx = tempo[0];
				//ty = tempo[1];
				ok = true;
				break;
			}
		}
		if (ok) {
			broadcast(">"+(char)(tx+32)+""+(char)(ty+32)+""+parent.encode(item)+""+(char)(x+32)+""+(char)(y+32)+"  ",m);
			m.itemmap[tx][ty] = item;
			m.itemmap[x][y] = 0;
			limitedBroadcast("!1",x,y,m);
		}
		else {
			if (item == 8) {
				limitedBroadcast(parent.assembleSpellString(16,x,y),x,y,m);
				placeItemAt(0,x,y,m);
			}
		}
	}
	/*protected static void pop(int x, int y, int item, DSMapServer m) {
		broadcast("!0",m);
		broadcast(">"+parent.toDSChar(x)+""+parent.toDSChar(y)+""+parent.encode(item),m);
		parent.sthread.pops.addElement(parent.toDSChar(x)+""+parent.toDSChar(y)+""+parent.toDSChar(m.mapnumber));
		m.itemmap[x][y] = 0;
	}*/
	protected static void makeBalls(DSMapServer m) {
		for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				if (m.tilemap[x][y] == 2 && m.itemmap[x][y] == 0)
					placeItemAt(8,x,y,m);
			}
		}
		broadcast("!2",m);
	}
	protected static void doWhisper(String incoming, DSpiresSocket s) {

		try {
			String thename = incoming.substring(2,incoming.indexOf(" "));
			String message = incoming.substring(incoming.indexOf(" ")+1);
			DSpiresSocket c = s.getSocketByNearestString(thename.toLowerCase());
			if (c != null) {
				if (message.startsWith(":")&&message.length()>2)
					message=s.name+((Character.isLetterOrDigit(message.charAt(1))) ? " " : "")+message.substring(1);

				c.pSend("[(~ "+s.name+" whispers, "+'"'+message+'"'+" to you.");
				if (c.invis)
					s.pSend("(* There's no one on DragonSpires with the name '"+thename+"'.");
				else
					s.pSend("[(You whisper, "+'"'+message+'"'+" to "+c.name+((c.bitIsMarked(c.stateCheck,c.ST_BRB)) ? ", but "+c.name+" is in BRB mode." : "."));
			}
		}
		catch(Exception e) {
			s.pSend("(Whispers must have a name and a message.");
		}
	}
	protected static void addAnimal(int animal, int x, int y,DSMapServer map) {
		if (items<animal && animal<=items+8)
			parent.animalbase.animals.addElement(new Bunny(x,y,map,parent.animalbase));
		else if (items+8<animal && animal<=items+16)
			parent.animalbase.animals.addElement(new Snail(x,y,map,parent.animalbase));
		else if (items+16<animal && animal<=items+24)
			parent.animalbase.animals.addElement(new Lizard(x,y,map,parent.animalbase));
		else if (items+32<animal && animal<=items+40)
			parent.animalbase.animals.addElement(new Squirrel(x,y,map,parent.animalbase));
		else if (items+32<animal && animal<=items+48)
			parent.animalbase.animals.addElement(new Crow(x,y,map,parent.animalbase));
		else if (animal == 143)
			parent.animalbase.animals.addElement(new Balloon(x,y,map,parent.animalbase));
	}
	public static Enemy findEnemyAtPos(int x, int y, DSMapServer m) {
		if (m.enemybase != null) {
			Enumeration e = m.enemybase.enemies.elements();
			Enemy c;
			while (e.hasMoreElements()) {
				c = (Enemy)e.nextElement();
				if (c.killed==0) {
					if (c.x-32 == x && c.y-32 == y) {
						return c;
					}
				}
			}
		}
		/*if (m.enemies != null) {
			Enemy c;
			for (int i = 0; i < m.enemies.length;i++) {
				c = m.enemies[i];
				if (c.x-32 == x && c.y-32 == y && c.killed == -1) {
					return c;
				}
			}
		}*/
		return null;
	}
	public synchronized void addEnemy(Enemy e) {
		if (this instanceof BasicMap)
			return;
		else if (enemybase.enemies.size() >= MAX_ENEMIES)
			return;
		/*if (m.enemies == null) {
			m.enemies = new Enemy[1];
			m.enemies[0] = e;
		}
		else {
			if (m.enemies.length < MAX_ENEMIES) {
				Enemy[] newenemies = new Enemy[m.enemies.length+1];
				for (int i = 0; i < m.enemies.length; i++) {
					newenemies[i] = m.enemies[i];
				}
				newenemies[m.enemies.length] = e;
				m.enemies = newenemies;
			}
			else
				e = null;
		}*/
		enemybase.enemies.addElement(e);
		boolean ass = enemybase.isAlive();
		//System.out.println(mapnumber+":"+ass);
		try {
			if (!ass) {
				MapEnemyThread ho = new MapEnemyThread(this);
				ho.enemies=enemybase.enemies;
				enemybase = ho;
				enemybase.start();
			}
		}
		catch (IllegalThreadStateException ex) {
			System.err.println("");
			System.err.println("Map="+(mapnumber+1));
			System.err.println("ass="+ass);
			System.err.println("enemycount="+enemybase.enemies.size());
			ex.printStackTrace();
			System.err.println("");
		}
	}
	public void addEnemy(int type, int x, int y, boolean nwd) {
		Enemy e;
		//switch (type) {
			//case 15:
				//e = new Nezerath(x,y,nwd,this);
			//	break;
			//case 54:
			//	e = new MystTree(x,y,nwd,this);
			//	break;
			//case 36:
			//	e = new ScorpionNest(x,y,nwd,m);
			//	break;
			//default:
				if (parent.enemyIndex[type].is_item)
					e = new i1Enemy(type,x,y,nwd,this);
				else
					e = new Enemy(type,x,y,nwd,this);
		//}
		addEnemy(e);
	}
	public synchronized void removeEnemy(Enemy e) {
		/*try {
			while (m.removingenemy);
			m.removingenemy=true;
			Enemy[] newenemies = new Enemy[m.enemies.length-1];
			int ecount = 0;
			for (int i = 0; i < m.enemies.length; i++) {
				if (m.enemies[i] == e)
					m.enemies[i]=e=null;
				else {
					newenemies[ecount] = m.enemies[i];
					ecount++;
				}
			}
			e=null;
			m.enemies = newenemies;
			//System.gc();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		m.removingenemy=false;*/
		enemybase.enemies.removeElement(e);
	}
	protected static void pdoPlace(PlayerDataObject pdo, int shape, String colorstring, char x, char y, DSMapServer m) {
		if (shape == 0) {
			PlayerDataObject tpdo;
			//synchronized (m.pdos) {
				Enumeration e = m.pdos.elements();
				while (e.hasMoreElements()) {
					tpdo = (PlayerDataObject)e.nextElement();
					if (tpdo==pdo) {
						m.pdos.removeElement(pdo);
						break;
					}
				}
			//}
		}

		//System.out.println("Shape: "+shape+" ; pdo: "+m.pdos.size());
		
		pdoPlaceBroadcast(shape,colorstring,x,y,m);
	}
	protected static  void pdoMove(PlayerDataObject pdo, int shape, String colorstring, char x, char y, char x2, char y2, DSMapServer m) {
		//PlayerDataObject pdo;
		boolean inrectshape,inrectblank;
		String tosend = "<"+x+""+y+""+parent.toDSChar(shape)+""+colorstring+""+x2+""+y2+" ";
		DSpiresSocket c;
		Enumeration e;

		m.playermap[x-32][y-32] = shape;
		//m.colorstrings[x-32][y-32] = colorstring;
		m.playermap[x2-32][y2-32] = 0;
		//m.colorstrings[x2-32][y2-32] = "";

		//synchronized (m.pdos) {
		//	e = m.pdos.elements();
		//	while (e.hasMoreElements()) {
		//		pdo = (PlayerDataObject)e.nextElement();
		//		if (pdo.x == x2 && pdo.y == y2) {
					pdo.x=x;
					pdo.y=y;
					//playerMoveBroadcast(shape,colorstring,x,y,x2,y2,null,m);
		//			break;
		//		}
		//	}
		//}

		//synchronized(m.sockets) {
			e = m.sockets.elements();
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();

				inrectshape = dsPinR2(c.cx-32,c.cy-32,x-32,y-32);
				inrectblank = dsPinR2(c.cx-32,c.cy-32,x2-32,y2-32);

				if (inrectshape && inrectblank)
					c.pSend(tosend);
				else if (!inrectshape && inrectblank)
					c.pSend("<"+x2+""+y2+" ");
				else if (inrectshape && !inrectblank)
					c.pSend("<"+x+""+y+""+parent.toDSChar(shape)+""+colorstring);
			}
		//}
	}
	protected static  void pdoForPlayerMove(int x, int y, int x2, int y2, DSpiresSocket s) {
		PlayerDataObject pdo;
		boolean inrectshape,inrectblank;
		int somex, somey;

		//synchronized (s.map.pdos) {
			Enumeration e = s.map.pdos.elements();
			while (e.hasMoreElements()) {
				//System.out.print("Searching");
				pdo = (PlayerDataObject)e.nextElement();
				somex=pdo.x-32;
				somey=pdo.y-32;

				inrectshape = dsPinR2(x,y,somex,somey);
				inrectblank = dsPinR2(x2,y2,somex,somey);

				if (!inrectshape && inrectblank)
					s.pSend("<"+pdo.x+""+pdo.y+" ");
				else if (inrectshape && !inrectblank)
					s.pSend("<"+pdo.x+""+pdo.y+""+parent.toDSChar(s.map.playermap[somex][somey])+""+pdo.colorstring);
			}
		//}
	}
	protected static  void playerPlaceBroadcastWithRefresh(int shape, String colorstring, int x, int y, DSpiresSocket s, DSMapServer m) {
		DSpiresSocket c;
		Enumeration e;
		String tosend = "<"+s.cx+""+s.cy+""+parent.toDSChar(shape)+""+colorstring;

		m.playermap[x][y] = shape;
		//m.colorstrings[x][y] = colorstring;

		s.sight.removeAllElements();

		//synchronized(m.sockets) {
			e = m.sockets.elements();
			while (e.hasMoreElements()) {
				try {
					c = (DSpiresSocket)e.nextElement();

					if (dsPinR2(c.x,c.y,x,y)) {
						if (s!=c)
							c.sight.addElement(s);
						c.pSend(tosend);
						s.sight.addElement(c);
						s.pSend("<"+c.cx+""+c.cy+""+parent.toDSChar(c.visishape)+""+c.colorstring);
					}
				}
				catch (NoSuchElementException ex) {
					//break;
				}
			}
		//}

		if (m.pdos.size() > 0) {
			PlayerDataObject pdo;
			//synchronized (m.pdos) {
				e = m.pdos.elements();
				while (e.hasMoreElements()) {
					pdo = (PlayerDataObject)e.nextElement();
	
					if (dsPinR2(x,y,pdo.x-32,pdo.y-32))
						s.pSend("<"+pdo.x+""+pdo.y+""+parent.toDSChar(m.playermap[pdo.x-32][pdo.y-32])+""+pdo.colorstring);
				}
			//}
		}
	}
	protected static void pdoInit(PlayerDataObject pdo, int shape, DSMapServer m) {
		m.pdos.addElement(pdo);		
		pdoPlaceBroadcast(shape,pdo.colorstring,pdo.x,pdo.y,m);
	}
	protected static void pdoPlaceBroadcast(int shape, String colorstring, char x, char y, DSMapServer m) {

		String tosend = "<"+x+""+y+""+parent.toDSChar(shape)+""+colorstring;

		x-=32;
		y-=32;

		m.playermap[x][y] = shape;
		//m.colorstrings[x][y] = colorstring;

		DSpiresSocket c;
		//synchronized(m.sockets) {
			Enumeration e = m.sockets.elements();
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();
				if (dsPinR2(c.cx-32,c.cy-32,x,y))
					c.pSend(tosend);
			}
		//}
	}
	protected static void addHole(DSpiresSocket c) {
		if (c.inhand==0) {
			c.pSend("(* You have nothing to put in the hole.");
			return;
		}

		if (c.weapon!=163) {
			c.pSend("(* You'd put your item in the hole, but you have nothing to use to fill it in.");
			return;
		}

		if (c.inhand==19) {
			c.pSend("(* The bone doesn't want to go in the hole");
			return;
		}

		if (c.map.holes==null)
			c.map.holes = new Vector();

		c.map.holes.addElement(c.cx+""+c.cy+parent.encode(c.inhand));

		c.setHands(0);
		placeItemAt(0,c.cx-32,c.cy-32,c.map);

		c.pSend("(* You bury your item in the hole.");
	}
	protected static void readMapScript(String fn, DSMapServer m) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("../maps/"+fn));
			m.readMapScriptFromReader(in,fn,m);
			in.close();
		}
		catch (Exception e) {}
	}
	public synchronized Exception readMapScriptFromReader(BufferedReader in, String fn, DSMapServer m) {
		/*if (m.enemies!=null) {
			for (int i=0;i<m.enemies.length;i++) {
				m.pdoPlace((PlayerDataObject)m.enemies[i],0,"",m.enemies[i].x,m.enemies[i].y,m);
				//m.removeEnemy(m.enemies[i],m);
			}
			m.enemies=null;

		}*/
		
		if (enemybase==null)
			enemybase = new MapEnemyThread(this);

		Vector temp = (Vector)(m.enemybase.enemies.clone());
		m.enemybase.enemies.removeAllElements();
		Enumeration e = temp.elements();
		Enemy c;
		while (e.hasMoreElements()) {
			c = (Enemy)e.nextElement();
			m.pdoPlace((PlayerDataObject)c,0,"",c.x,c.y,m);
		}
		temp.removeAllElements();

		if (m.bags!=null)
			m.bags.removeAllElements();									

		try {
			String line;
			Portal[] oldPortals=null;

			while ((line=in.readLine())!=null) {
				//if (m instanceof BasicMap)
				//	System.out.println(line);
				if (line.equals("EOF"))
					break;
				if (line.startsWith("#"));				
				else if (line.startsWith("[")) {
					line=line.toLowerCase();

					if (line.equals("[portals]")) {
						Vector fun = grabMapScriptSectionData(in);
						if (m.portals!=null)
							oldPortals=m.portals;
						m.portals=new Portal[fun.size()+m.maxUploads];
						for (int i=0;i<fun.size();i++) {
							line=fun.elementAt(i).toString().trim();
							StringTokenizer st = new StringTokenizer(line,",");
							m.portals[i]=new Portal();
							m.portals[i].orig_x=Integer.parseInt(st.nextToken());
							m.portals[i].orig_y=Integer.parseInt(st.nextToken());
							if (m instanceof BasicMap)
								m.portals[i].dest_map=m;
							else
								m.portals[i].dest_map=parent.maps[Integer.parseInt(st.nextToken())-1];
							try {
								m.portals[i].dest_x=Integer.parseInt(st.nextToken());
								m.portals[i].dest_y=Integer.parseInt(st.nextToken());
							}
							catch (Exception ex) {}
						}

						if (oldPortals!=null) {
							int pc=fun.size();
							for (int i=0;i<oldPortals.length;i++) {
								if (oldPortals[i]!=null) {
									if (oldPortals[i].dest_map instanceof BasicMap) {
										if (pc<m.portals.length) {
											m.portals[pc]=oldPortals[i];
											m.placeItemAt(151,oldPortals[i].orig_x,oldPortals[i].orig_y,m);
											pc++;
										}
										else
											break;
									}
								}
							}
						}
					}
					else if (line.equals("[readables]")) {
						Vector fun = grabMapScriptSectionData(in);
						m.readables=new String[fun.size()];
						for (int i=0;i<fun.size();i++)
							m.readables[i]=fun.elementAt(i).toString().replace(';','\n');
					}
					else if (line.equals("[holders]")) {
						Vector fun = grabMapScriptSectionData(in);
						m.holders=new int[fun.size()*4];
						for (int i=0;i<fun.size();i++) {
							line=fun.elementAt(i).toString().trim();
							StringTokenizer st = new StringTokenizer(line,",");
							if (st.countTokens()==3) {
								for (int n=0;n<3;n++)
									m.holders[(i*4)+n]=(short)Integer.parseInt(st.nextToken());
								m.holders[(i*4)+3]=1;
								try {
									if (m instanceof BasicMap && (parent.findIntInArray(m.holders[(i*4)+2],parent.itemsdisallowed) || parent.itemdefs[m.holders[(i*4)+2]][0]!=0 || parent.itemdefs[m.holders[(i*4)+2]][1]!=0)) {
										m.holders[(i*4)+2]=0;
										m.holders[(i*4)+3]=0;
									}
								}
								catch (Exception ex) {
								}
							}
						}
					}
					else if (line.equals("[enemies]")&& !(m instanceof BasicMap)) {
						Vector fun = grabMapScriptSectionData(in);
						//m.enemies=new Enemy[fun.size()];
						for (int i=0;i<fun.size();i++) {
							line=fun.elementAt(i).toString().trim();
							StringTokenizer st = new StringTokenizer(line,",");
							//m.enemies[i]=new Enemy(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()), false,m);
							m.addEnemy(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),false);
						}
					}
				}
				else if (line.indexOf('=')!=-1) { // Simple variables
					String var = line.toLowerCase().substring(0,line.indexOf('='));
					String val=line.substring(line.indexOf('=')+1);;
					if (var.equals("start")) {
						val=val.trim();
						int i=val.indexOf(',');
						m.xstart=(short)Integer.parseInt(val.substring(0,i));
						m.ystart=(short)Integer.parseInt(val.substring(i+1));
					}
					else if (var.equals("exits")&& !(m instanceof BasicMap)) {
						val=val.trim();
						StringTokenizer st = new StringTokenizer(val,",");
						for (int i =0; i<4;i++)
							m.exits[i]=(Integer.parseInt(st.nextToken()))-1;
					}
					else if (var.equals("combatarea")) {
						val=val.trim();
						try {
							StringTokenizer st = new StringTokenizer(val,",");
							for (int i =0; i<5;i++)
								m.attacktiles[i]=(Integer.parseInt(st.nextToken()));
						}
						catch (Exception ex) {}
					}
					else if (var.equals("stamval")) {
						val=val.trim();
						try {
							m.stamval=Integer.parseInt(val);
							if (m.stamval<-5||m.stamval>-1)
								m.stamval=-1;
						}
						catch (Exception ex) {
							m.stamval=-5;
						}
					}
					else if (var.equals("allowcombat")) {
						if (val.toLowerCase().equals("true"))
							m.allowcombat=true;
						else
							m.allowcombat=false;
					}
					else if (var.equals("safemap")&& !(m instanceof BasicMap)) {
						if (val.toLowerCase().equals("true"))
							m.safemap=true;
						else
							m.safemap=false;
					}
					else if (var.equals("entrytext")) {
						if (val.length() > 0) {
							if (m instanceof BasicMap)
								m.entrytext="[%"+val;
							else
								m.entrytext=val;
						}
					}
					else if (var.equals("diemap")&& !(m instanceof BasicMap)) {
						val=val.trim();
						m.diemap=(short)(Integer.parseInt(val)-1);
					}
					else if (var.equals("allowupload")&&!(m instanceof BasicMap)) {
						if (val.toLowerCase().equals("true"))
							m.allowupload=true;
						else
							m.allowupload=false;
					}
					else if (var.equals("maxuploads")&&!(m instanceof BasicMap)) {
						val=val.trim();
						m.maxUploads=Integer.parseInt(val)-1;
					}
					else if (var.equals("fountain")) {
						val=val.trim();
						int i=val.indexOf(',');
						m.fountainx=(short)Integer.parseInt(val.substring(0,i));
						m.fountainy=(short)Integer.parseInt(val.substring(i+1));
					}
				}
			}
		}
		catch (Exception ex) {
			System.err.println("Error in: "+fn+".ini");
			ex.printStackTrace();
			//System.exit(1);
			return ex;
		}
		return null;
	}
	protected static Vector grabMapScriptSectionData(BufferedReader in) {
		Vector storage=new Vector();
		String line;
		try {
			while ((line=in.readLine())!=null) {
				if (line.startsWith("#"));
				else if (line.startsWith("[")) {
					if (line.equals("[end]"))
						break;
				}
				else if (line.length()!=0)
					storage.addElement(line);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return storage;
	}
	protected static BagOStuff addBag(int x, int y, int gold, int[] items, DSMapServer m) {
		BagOStuff b;
		if (m.itemmap[x][y]==201&&m.bags!=null) {
			Enumeration e = m.bags.elements();
			while (e.hasMoreElements()) {
				b=(BagOStuff)e.nextElement();
				if (b.x==x&&b.y==y) {
					b.gold+=gold;
					if (b.items.length==0&&items.length!=0)
						m.placeItemAt(201,x,y,m);
					int[] newitems = new int[items.length+b.items.length];
					System.arraycopy(b.items,0,newitems,0,b.items.length);
					System.arraycopy(items,0,newitems,b.items.length,items.length);
					return b;
				}
			}
		}

		if (m.bags==null)
			m.bags=new Vector();

		b=new BagOStuff();
		b.x=x;
		b.y=y;
		b.gold=gold;
		b.items=items;
		m.bags.addElement(b);
		//if (m.itemmap[x][y]!=0)
		//	b.addItem(m.itemmap[x][y],b);
		b.below=m.itemmap[x][y];
		b.below=parent.checkForTrans(b.below,x,y,m);
		if (b.items.length==0)		
			m.placeItemAt(6,x,y,m);
		else
			m.placeItemAt(201,x,y,m);
		return b;
	}
	protected static void destroyMap(DSMapServer m) {
		if (m.owner==null)
			return;
		Portal theportal;
		for (int i=0;i<m.parentmap.portals.length;i++) {
			theportal=m.parentmap.portals[i];
			if (theportal!=null) {
				if (theportal.dest_map==m) {
					m.parentmap.placeItemAt(0,theportal.orig_x,theportal.orig_y,m.parentmap);
					m.broadcast("[&This map has been destroyed.",m);
					//Vector mysockets = (Vector)m.sockets.clone();
					//synchronized (m.sockets) {
						Enumeration e = ((Vector)(m.sockets.clone())).elements();
						while (e.hasMoreElements()) {
							DSpiresSocket s = (DSpiresSocket)e.nextElement();
							s.changeMap(m.parentmap,0,0,0);
						}
					//}
					if (m.parentmap.portals[i]==theportal)
						m.parentmap.portals[i]=null;
					return;
				}
			}
		}
	}
	public void dualItemBroadcast(int x1, int y1, int i1, int x2, int y2, int i2) {
		Enumeration e = sockets.elements();
		DSpiresSocket c;
		String si1 = parent.toDSChar(x1)+""+parent.toDSChar(y1)+""+parent.encode(i1);
		String si2 = parent.toDSChar(x2)+""+parent.toDSChar(y2)+""+parent.encode(i2);
		boolean inrectshape,inrectblank;
		while (e.hasMoreElements()) {
			c = (DSpiresSocket)e.nextElement();
			inrectshape = dsPinR2(c.cx-32,c.cy-32,x1,y1);
			inrectblank = dsPinR2(c.cx-32,c.cy-32,x2,y2);
			if (inrectshape) {
				if (inrectblank)
					c.pSend(">"+si1+si2);
				else
					c.pSend(">"+si1);
			}
			else if (inrectblank)
				c.pSend(">"+si2);
		}
	}
}
