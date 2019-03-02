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
import java.util.NoSuchElementException;

public class Enemy extends PlayerDataObject {

	static byte shapestart[][];

	DSMapServer map;
	int currshape,facing,mstate,hp,tilltrig,ie,move,killed=0;
	char ox,oy;
	boolean nullwhendie,special_trig=false,special_die=false;
	EIE me;

	public Enemy() {}

	public Enemy(DSpiresServer s) {
		shapestart = s.longShapeStart;
		//index = s.enemyIndex;
	}
	public Enemy(int eie, int x, int y, boolean nwd, DSMapServer m) {
		try {
			if (m.enemybase.enemies.size() >= m.MAX_ENEMIES)
				return;
			else if (m==null)
				throw new NullPointerException("null map");

			init(eie,x,y,nwd,m);
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	public void init(int eie, int x, int y, boolean nwd, DSMapServer m) {
		map = m;

		this.x = m.parent.toDSChar(x);
		this.y = m.parent.toDSChar(y);
		ox = this.x;
		oy = this.y;

		me=m.parent.enemyIndex[eie];
		nullwhendie = nwd;		
		hp = me.maxhp;
		currshape = 2;
		mstate = 1;
		ie = eie;
		tilltrig = me.maxtilltrig;

		//pdo
		colorstring=me.colorstring;
		map.pdoInit(this,2+mstate,m);
	}

	public void mytrigger() {}

	public synchronized boolean isReady() {
		if (--tilltrig<=0) {
			tilltrig = me.maxtilltrig;
			return true;
		}
		
		return false;
	}

	public synchronized void trigger() {
		if (killed==0) {
				move = getNearestPlayerDir(x-32,y-32,map);

				boolean domore=true;

				if (move == -1)
					move = map.parent.shortfaceconv[(int)Math.round(Math.random() * 3)];

				int tx = map.parent.nextx(x-32,y-32,move);
				int ty = map.parent.nexty(y-32,move);

				if (me.projectile&&map.canWalk(tx,ty,map)) {
					move(move);
					new DSBullet(me.projectile_item,x-32,y-32,facing,me.projectile_length,map,this,map.parent);
					return;
				}

				if (map.playermap[tx][ty] != 0) {
					int f=attack(tx,ty);
					if (f==1) {
						domore=false;
						move(move);
						//move(move);
					}
					else if (f==2)
						return;
				}

				if (domore) {
					move=decideDir(move);
					tx = map.parent.nextx(x-32,y-32,move);
					ty = map.parent.nexty(y-32,move);
					if (map.playermap[tx][ty] != 0) {
						int f=attack(tx,ty);
						if (f==1) {
							domore=false;
							move(move);
						}
					}

					move(move);

					if (domore) {
						tx = map.parent.nextx(x-32,y-32,move);
						ty = map.parent.nexty(y-32,move);
						if (map.playermap[tx][ty] != 0) {
							attack(tx,ty);
						}
					}

				}
		}
		else {
			if (--killed==0) {
				mstate = 1;
				map.pdoInit(this,2+mstate,map);
				map.pdoPlace(this,3,colorstring,x,y,map);
			}
		}
	}
	public void move(int dir) {
		facing=dir;
		mstate*=-1;
		currshape=shapestart[me.shapecat][facing-1]+mstate;

		int tx = map.parent.nextx(x-32,y-32,facing);
		int ty = map.parent.nexty(y-32,facing);

		if (!map.canWalk(tx,ty,map) || map.playermap[tx][ty] != 0)
			map.pdoPlace(this,currshape,colorstring,x,y,map);
		else
			map.pdoMove(this,currshape,colorstring,(char)(tx+32),(char)(ty+32),x,y,map);
	}
	public int getNearestPlayerDir(int x, int y, DSMapServer m) {

		//try {
			if (map.sockets.size()==0)
				return -1;
		//}
		/*catch (Exception e) {
			Systemap.err.println();
			Systemap.err.println("name="+me.name);
			if (map==null)
				System.err.println("map="+map);
			else {
				System.err.println("map="+map);
				System.err.println("map.mapnumber="+map.mapnumber);
				System.err.println("map.sockets="+map.sockets);
			}
			e.printStackTrace();
			Systemap.err.println();
			return -1;
		}*/

		int currnearx=0,currneary = 0;
		int tx=-1,ty;

		Enumeration e = map.sockets.elements();
		DSpiresSocket s;
		while (e.hasMoreElements()) {
			try {
				s = (DSpiresSocket)e.nextElement();
				if (!s.bitIsMarked(s.stateCheck,s.ST_TRANS)) {
					tx=s.x;
					ty=s.y;
					if ((Math.abs(x-tx)+Math.abs(y-ty)) < (Math.abs(x-currnearx)+Math.abs(y-currneary))) {
						currnearx = tx;
						currneary = ty;
					}
				}
			}
			catch (NoSuchElementException exc) {
			}
		}

		if (tx==-1)
			return -1;

		if (y%2==0) {
			if (currnearx > x) {
				if (currneary <= y)
					return 9;
				else
					return 3;
			}
			else {
				if (currneary <= y)
					return 7;
				else
					return 1;
			}
		}
		else {
			if (currnearx < x) {
				if (currneary <= y)
					return 7;
				else
					return 1;
			}
			else {
				if (currneary <= y)
					return 9;
				else
					return 3;
			}
		}
	}
	public int decideDir(int d) {
		int x, y, t,tx=this.x-32,ty=this.y-32;
		x=map.parent.nextx(tx,ty,d);
		y=map.parent.nexty(ty,d);
		if (map.canWalk(x,y,map)||map.playermap[x][y]!=0)
			return d;
		
		t=map.parent.intRotateLeft(d);
		x=map.parent.nextx(tx,ty,t);
		y=map.parent.nexty(ty,t);
		if (map.canWalk(x,y,map)||map.playermap[x][y]!=0)
			return t;

		t=map.parent.intRotateRight(d);
		x=map.parent.nextx(tx,ty,t);
		y=map.parent.nexty(ty,t);
		if (map.canWalk(x,y,map)||map.playermap[x][y]!=0)
			return t;
		
		t=map.parent.intRotateRight(t);
		x=map.parent.nextx(tx,ty,t);
		y=map.parent.nexty(ty,t);
		if (map.canWalk(x,y,map)||map.playermap[x][y]!=0)
			return t;

		return d;
	}
	public static String assembleAttackString(String emessage, String ename, String pname) {
		String retval = "";
		char[] buffer = emessage.toCharArray();
		for (int i=0;i<buffer.length;i++) {
			if (buffer[i]=='%') {
				switch (buffer[i+1]) {
					case '1':
						retval+=ename;
						i++;
						break;
					case '2':
						retval+=pname;
						i++;
				}
			}
			else retval+=buffer[i];
		}
		return retval;
	}

	public void attack(int dir) {
		attack(map.parent.nextx(x-32,y-32,facing),map.parent.nexty(y-32,facing));
	}

	public int attack(int x, int y) {
		DSpiresSocket c = findSocketAtPosToAttack(map.parent.toDSChar(x),map.parent.toDSChar(y),map);
		if (c != null) {
			if (me.spell > -1)
				c.sightSend(c.parent.assembleSpellString(me.spell,x,y));

			String emessage = assembleAttackString(me.attack,me.name,c.name);
			if (me.weapon.mindam == -1) {
				c.sightSendAttacks("[#"+emessage,null);
				return 1;
			}
			else {
				//int damage = c.map.parent.dice(1,me.str+1)-c.map.parent.dice(1,c.def);
				int damage = c.W2A(me.weapon,c.armorO);//c.doDamage(me.str,c.def);
				if (damage<1) {
					if (c.map.parent.dice(1,6)==1) {
						emessage = "[#While dodging, "+c.name+" slips and falls!";
						damage=1;
					}
					else
						emessage="[#"+c.name+" dodges "+me.name+"'s pathetic attack!";
				}
				else
					emessage="[\""+emessage;

				if (!c.doDamageToPlayer(damage)) {
					c.sightSendAttacks(emessage+" ("+damage+")",null);
					c.pSend("$H"+c.hp);
				}
				else {
					c.sightSendAttacks("[\""+me.name+" did slay "+c.name+"!",null);
					c.map.parent.channelBroadcast(me.name+" did slay "+c.name+"!",4);
					c.killMe(true);
				}
				return 1;
			}
		}
		return 0;
	}
	public synchronized void die() {
		if (special_die)
			whendie();

		map.pdoPlace(this,0,"",x,y,map);

		BagOStuff b=map.addBag(x-32,y-32,me.gold,new int[0],map);
		for (int i=0;i<me.drop.length;i++) {
			if (me.drop[i]!=0 && (int)Math.round(Math.random() * 2) <= 1)
				b.addItem(me.drop[i],b,map);
		}

		if (nullwhendie||ie==36) {
			map.removeEnemy(this);
			return;
		}

		x = ox;
		y = oy;
		hp=me.maxhp;
		killed=5;
	}

	public void whendie() {}

	public boolean doDamageToEnemy(int damage) {
		if ((hp-=damage) <= 0)
			return true;
		return false;

	}

	public void damage(DSpiresSocket s, boolean attackbydir) {
		int damage=s.W2A(s.weaponO,me.armor)+s.extraDamage(x-32,y-32);

		if (damage>0) {
			String funstring;;
			if (damage > 12)
				funstring = "0wns";
			else if (damage > 9)
				funstring = "massacres";
			else if (damage > 6)
				funstring = "bashes";
			else if (damage > 2)
				funstring = "did smite";
			else
				funstring = "pricks";

			s.sightSendAttacks("[\""+s.name+" "+funstring+" "+me.name+"! ("+damage+")",null);
		}
		else {
			if (map.parent.dice(1,5)==1) {
				s.sightSendAttacks("[$While dodging, "+me.name+" slips and falls! (1)",null);
				damage=1;
			}
			else
				s.sightSendAttacks("[$"+me.name+" dodges "+s.name+"'s pathetic attack! (0)",null);
		}
			
		if (doDamageToEnemy(damage)) {
			s.updateAlign(me.align);
			s.sightSendAttacks("!4",null);
			s.sightSendAttacks("[\""+s.name+" did slay "+me.name+"!",null);
			if (this instanceof i1Enemy)
				((i1Enemy)this).die();
			else
				die();
			return;
		}
			
		if (attackbydir) {
			if (this instanceof i1Enemy) {
				((i1Enemy)this).move(map.parent.faceopp[s.facing]);
				if (me.projectile&&map.canWalk(map.parent.nextx(x-32,y-32,facing),map.parent.nexty(y-32,facing),map)) {
					new DSBullet(me.projectile_item,x-32,y-32,facing,me.projectile_length,map,this,map.parent);
					return;
				}
			}
			else {
				if (Math.round(Math.random())==0)
					move(map.parent.shortfaceconv[(int)Math.round(Math.random()*3)]);
				else {
					move(map.parent.faceopp[s.facing]);
					if (me.projectile&&map.canWalk(map.parent.nextx(x-32,y-32,facing),map.parent.nexty(y-32,facing),map)) {
						new DSBullet(me.projectile_item,x-32,y-32,facing,me.projectile_length,map,this,map.parent);
						return;
					}
				}
			}
			attack(map.parent.faceopp[s.facing]);
		}
		else
			attack(s.x,s.y);
	}
	public DSpiresSocket findSocketAtPosToAttack(char x, char y, DSMapServer m) {
		DSpiresSocket c;
		Enumeration e = m.sockets.elements();
		while (e.hasMoreElements()) {
			c = (DSpiresSocket)e.nextElement();
			if (c.cx == x && c.cy == y) {
				if (!c.bitIsMarked(c.stateCheck,c.ST_TRANS)) {
					if (!(c.bitIsMarked(c.stateCheck,c.ST_BRB) && c.map.safemap)) {
						if (c.colorstring.charAt(3)!=' ')
							return c;
					}
				}
			}
		}
		return null;
	}
}