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

public class NPC3 extends PlayerDataObject implements Interactable {

	protected static NPC3Thread parent;

	// NPC3 bit mask stuff
	int attributes=0;

	final static int NPC3_WANDER		= 1;
	final static int NPC3_RANDOM_SPEAK	= 2;
	final static int NPC3_HIT			= 4;
	final static int NPC3_INTERACT		= 8;

	DSMapServer map;

	String name,interact_title;
	int currshape,facing=2,mstate,visishape,shapecat,trigtime,trigwait,talk,hitcount;

	String[] random_speaks,hits;

	int num_interact=0;
	String[][] interactions;

	final static short shapestart[][] = {{2,2,6,10,10,6,10,14,14},{2,2,7,12,12,7,12,17,17},{2,2,5,8,8,5,8,11,11}};

	public NPC3 () {}

	/*public NPC3 (String n, String cs, DSMapServer m, int x, int y, int sc, int tw, String[] s2s, String ht, NPC3Thread p) {
		stuff2say = s2s;
		hittext = ht;
		map = m;
		init (n,cs,x,y,sc,tw,p,this);
	}*/
	public void init(String n, String cs, int x, int y, int sc, int tw, NPC3Thread p) {
		parent = p;
		this.x = parent.parent.toDSChar(x);
		this.y = parent.parent.toDSChar(y);
		colorstring = cs;
		name = n;
		trigtime=tw;
		trigwait=tw;
		shapecat=sc;
		init(this);
	}
	protected static void init(NPC3 c) {
		c.mstate = 1;
		c.visishape=shapestart[c.shapecat][c.facing-1];
		c.currshape = c.visishape;
		c.map.pdoInit(c,c.visishape,c.map);
	}

	public void trigger() {
		if (bitIsMarked(NPC3_WANDER))
			move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);

		if (bitIsMarked(NPC3_RANDOM_SPEAK)) {
			//if (++talk==4) {
				send("("+random_speaks[(int)Math.round(Math.random()*(random_speaks.length-1))]);
				//talk = 0;
			//}
		}
	}
	public void hit(DSpiresSocket c) {
		if (++hitcount==4) {
			if (bitIsMarked(NPC3_HIT))
				c.pSend("("+assembleNPC3String(hits[(int)Math.round(Math.random()*(hits.length-1))],name,c.name));
			hitcount=0;
		}
	}

	public void move(int dir) {

		int tx = x-32;
		int ty = y-32;

		facing = dir;
		currshape=shapestart[shapecat][facing-1];

		tx = parent.parent.nextx(tx,ty,facing);
		ty = parent.parent.nexty(ty,facing);

		boolean didmove = map.canWalk(tx,ty,map);

		mstate*=-1;

		visishape=currshape+mstate;

		if (didmove) {
			map.pdoMove(this,visishape,colorstring,parent.parent.toDSChar(tx),parent.parent.toDSChar(ty),x,y,map);
		}
		else
			map.pdoPlace(this,visishape,colorstring,x,y,map);
	}
	public void say(String message) {
		map.limitedBroadcast("("+name+": "+message,x-32,y-32,map);
	}
	public void send(String message) {
		map.limitedBroadcast(message,x-32,y-32,map);
	}
	public boolean bitIsMarked(int condition) {
		return ((attributes & condition) == condition);
	}
	public String[] getOptions(DSpiresSocket c) {
		int thesize = num_interact;
		if (interact_title.length()>0)
			thesize++;
		String[] wee = new String[thesize];
		wee[0]=interact_title;
		for (int i=0;i<num_interact;i++)
			wee[i+1]=interactions[i][0];
		return wee;
	}
	public void interact(int opt, DSpiresSocket c) {
		if (opt > 0 && opt <= num_interact) {
			opt--;
			c.pSend("("+assembleNPC3String(interactions[opt][1],name,c.name));
		}
	}

	public static String assembleNPC3String(String emessage, String ename, String pname) {
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
}
