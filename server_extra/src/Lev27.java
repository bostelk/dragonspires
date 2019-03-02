class Lev27 extends MainMapWithCode {
	public Lev27() {
		mapname = "lev27";
		init("lev27.dsmap",this);

		mapnumber = 26;
		diemap = 25;
		walktrig=true;
	}
	public void start() {
		shops = new Shop[1];

		//Wizard
		String[] ts2s = new String[3];
		ts2s[0] = "Business is tough on this hidden island of legend.";
		ts2s[1] = "What can I strap on for you today? *teehee* Damn I'm bored...";
		ts2s[2] = "*sigh* Please buy something.";
		parent.npc2base.npc2s.addElement(new NPC2("Rhonda",9,23,3,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 50;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 475;

		tmenus[1][2][0] = 162;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 25;

		tmenus[1][3][0] = 163;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 500;

		tmenus[1][4][0] = 140;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 200;

		tmenus[1][5][0] = 165;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 5000;

		tmenus[1][6][0] = 164;
		tmenus[1][6][1] = 25;
		tmenus[1][6][2] = 5000;

		tmenus[2][1][0] = 153;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 251;

		tmenus[2][2][0] = 57;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 1234;

		tmenus[2][3][0] = 135;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 600;

		shops[0] = new Shop(0,"? Rhonda's Junk Shop ?",tmenus,
					41,41,7,24,12,32,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		ts2s = new String[2];
		ts2s[0] = "Zzzzz.";
		ts2s[1] = "I got majical stuff.";
		parent.npc2base.npc2s.addElement(new NPC2("Marlyn The Majikan",23,26,3,ts2s,this));

		/*tmenus = new int[3][7][4];
		tmenus[1][1][0] = 19;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 200;

		tmenus[1][2][0] = 57;
		tmenus[1][2][1] = 59;
		tmenus[1][2][2] = 1;

		tmenus[1][3][0] = 59;
		tmenus[1][3][1] = 57;
		tmenus[1][3][2] = 1;

		tmenus[1][4][0] = 58;
		tmenus[1][4][1] = 103;
		tmenus[1][4][2] = 1;

		tmenus[1][5][0] = 143;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 500;

		tmenus[2][1][0] = 98;
		tmenus[2][1][1] = 97;
		tmenus[2][1][2] = 1;

		tmenus[2][2][0] = 99;
		tmenus[2][2][1] = 97;
		tmenus[2][2][2] = 1;

		tmenus[2][3][0] = 102;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 3000;

		tmenus[2][4][0] = 187;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 200;

		shops[1] = new Shop(1,"Marlyn's Maaaaaaajical Store",tmenus,
					7,6,22,23,25,29,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));*/
	}
	public void walkTrigger(DSpiresSocket c) {
		if (c.colorstring.charAt(3)==' ')
			return;
		if (tilemap[c.cx-32][c.cy-32]==2) {
			boolean send = false;
			if (c.cx-32==9&&c.cy-32==60) {
				c.colorstring=c.colorstring.substring(0,3)+'!';
				c.pstring="!"+c.pstring.substring(1);
				send=true;
			}
			else if (c.cx-32==11&&c.cy-32==64) {
				c.colorstring=c.colorstring.substring(0,3)+'"';
				c.pstring=" "+c.pstring.substring(1);
				send=true;
			}
			else if (c.cx-32==10&&c.cy-32==68) {
				c.colorstring=c.colorstring.substring(0,3)+'#';
				c.pstring="!"+c.pstring.substring(1);
				send=true;
			}
			else if (c.cx-32==8&&c.cy-32==67) {
				c.colorstring=c.colorstring.substring(0,3)+'$';
				c.pstring=" "+c.pstring.substring(1);
				send=true;
			}
			else if (c.cx-32==7&&c.cy-32==62) {
				c.colorstring=c.colorstring.substring(0,3)+'%';
				c.pstring=(Math.round(Math.random())==0 ? " ":"!")+c.pstring.substring(1);
				send=true;
			}

			if (send) {
				c.ocolorstring=c.colorstring;
				limitedBroadcast("<"+c.cx+""+c.cy+""+parent.toDSChar(c.visishape)+""+c.colorstring,c.cx-32,c.cy-32,this);
				c.pSend("color "+c.colorstring+"\nPY"+c.pstring);
			}
		}
	}
}