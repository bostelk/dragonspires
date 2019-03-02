public class Lev01 extends MainMapWithCode {
	public Lev01(DSpiresServer p) {
		parent = p;
		diemap=(short)p.START_MAP;
		mapname = "lev01";
		init("lev01.dsmap","lev01.old",this);

		mapnumber = 0;
		walktrig=true;

		allowcombat=true;
		attacktiles = new int[5];
		attacktiles[0] = 6;
		attacktiles[1] = 31;
		attacktiles[2] = 34;
		attacktiles[3] = 36;
		attacktiles[4] = 44;

		graffitti = new String[5];
		graffitti[0] = "*Bob Dobbs* Get Slack!";
		graffitti[1] = "";
		graffitti[2] = "";
		graffitti[3] = "";
		graffitti[4] = "";

	}
	public void start() {
		shops = new Shop[5];

		//Fishmonger
		String[] ts2s = new String[5];
		ts2s[0] = "Wholesale fishes on sale today!";
		ts2s[1] = "Finest Trout and Seabass in the city!";
		ts2s[2] = "Fish heads, fish heads, roley poley fish heads!";
		ts2s[3] = "Phew.";
		ts2s[4] = "Hey, man, my fish. They're live savers. Buy one and see for yourself!";
		parent.npc2base.npc2s.addElement(new NPC2("Somebody Fishmonger",23,55,5,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 10;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 30;
		tmenus[1][2][0] = 53;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 50;

		tmenus[2][1][0] = 48;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 20;

		shops[1] = new Shop(1,"><\\\\\\o> Somebody Fishmonger's",tmenus,
					1,1,21,51,25,58,6,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
		shops[1].parent = parent;

		//barkeep
	
		parent.npc2base.npc2s.addElement(new Barkeep());

		tmenus = new int[3][7][4];
		tmenus[1][1][0] = 32;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 120;

		tmenus[1][2][0] = 55;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 20;

		tmenus[1][3][0] = 54;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 200;

		tmenus[1][4][0] = 45;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 70;

		tmenus[1][5][0] = 4;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 5;

		tmenus[1][6][0] = 40;
		tmenus[1][6][1] = 25;
		tmenus[1][6][2] = 10;

		tmenus[2][1][0] = 32;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 50;

		tmenus[2][2][0] = 54;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 80;

		tmenus[2][3][0] = 45;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 30;

		shops[0] = new Shop(0,"* Monkey Boy's Saloon *",tmenus,
					41,1,17,34,21,41,8,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
		
		//herb
		ts2s = new String[2];
		ts2s[0] = "Welcome to Herb's Herbs'N'More.";
		ts2s[1] = "I have the finest selection of healing accessories in this part of the world!";
	
		parent.npc2base.npc2s.addElement(new NPC2("Herb",33,71,4,ts2s,this));

		tmenus = new int[3][7][4];
		tmenus[1][1][0] = 47;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 230;

		tmenus[1][2][0] = 49;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 60;

		tmenus[1][3][0] = 54;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 195;

		tmenus[1][4][0] = 45;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 69;

		tmenus[2][1][0] = 47;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 100;

		tmenus[2][2][0] = 49;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 20;

		tmenus[2][3][0] = 54;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 100;

		tmenus[2][4][0] = 45;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 21;

		shops[2] = new Shop(2,"~ Herb's Herbs'N'More ~",tmenus,
					41,41,30,69,33,73,2,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		//Simon
		ts2s = new String[3];
		ts2s[0] = "We've got some good armor here.";
		ts2s[1] = "Please don't touch any of the display models.";
		ts2s[2] = "I really admire Ted, he's got some good weapons. I bought my war hammer from him.";
	
		parent.npc2base.npc2s.addElement(new NPC2("Simon",20,15,5,ts2s,this));

		tmenus = new int[3][7][4];
		tmenus[1][1][0] = 2;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 550;

		tmenus[1][2][0] = 5;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 200;

		tmenus[1][3][0] = 12;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 100;

		tmenus[1][4][0] = 91;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 1400;

		tmenus[1][5][0] = 249;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 60;

		tmenus[2][1][0] = 2;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 225;

		tmenus[2][2][0] = 5;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 100;

		tmenus[2][3][0] = 12;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 50;

		tmenus[2][4][0] = 91;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 400;

		shops[3] = new Shop(3,"[ The Armor Shop ]",tmenus,
					41,41,18,14,20,19,2,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		//rh
		ts2s = new String[1];
		ts2s[0] = "Yeehaw!";
	
		parent.npc2base.npc2s.addElement(new NPC2("RazorHawk",28,19,15,ts2s,this));

		tmenus = new int[3][7][4];
		tmenus[1][1][0] = 23;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 654321;

		tmenus[1][2][0] = 3;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 5;

		tmenus[1][3][0] = 7;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 25;

		tmenus[1][4][0] = 8;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 20;

		tmenus[2][1][0] = 23;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 2;

		tmenus[2][2][0] = 3;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 5;

		tmenus[2][3][0] = 7;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 25;

		tmenus[2][4][0] = 8;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 20;

		shops[4] = new Shop(4,") The Temple of RazorHawk (",tmenus,
					1,7,26,17,27,26,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

	}
	public void walkTrigger(DSpiresSocket c) {
		switch (tilemap[c.cx-32][c.cy-32]) {
			case 0:
				if (playermap[19][69] > 0)
					doColorChangeShop(c);
				break;
			case 7:
				makeBalls(parent.maps[0]);
		}

		/*if (c.cx-32 == 27 && c.cy-32 == 20)
			makeBalls(parent.maps[0]);
		else if (tilemap[c.cx-32][c.cy-32] == 0) {
			if (playermap[19][69] > 0)
				doColorChangeShop(c);
		}*/
	}
	public void doColorChangeShop(DSpiresSocket s) {
		int x = s.cx-32;
		int y = s.cy-32;
		DSpiresSocket c = s.findSocketAtPos(19,69,this,1);
		if (c != null) {
			if (c.colorstring.charAt(3)==' ')
				return;
			String tcs = c.colorstring;
			char[] ncs = tcs.toCharArray();
			if (x == 15 && y == 72) {
				if (--ncs[0] < ' ') ncs[0] = '-';
			}				
			else if (x == 16 && y == 70) {
				if (++ncs[0] > '-') ncs[0] = ' ';
			}
			else if (x == 16 && y == 74) {
				if (--ncs[1] < ' ') ncs[1] = '-';
			}
			else if (x == 17 && y == 72) {
				if (++ncs[1] > '-') ncs[1] = ' ';
			}
			else if (x == 17 && y == 76) {
				if (--ncs[2] < ' ') ncs[2] = '-';
			}
			else if (x == 18 && y == 74) {
				if (++ncs[2] > '-') ncs[2] = ' ';
			}
			c.colorstring = new String(ncs);
			c.ocolorstring = c.colorstring;
			//System.out.println("1 "+tcs);
			//System.out.println("2 "+c.colorstring);
			parent.maps[0].broadcast("<"+c.cx+""+c.cy+" "+tcs,parent.maps[0]);
			parent.maps[0].limitedBroadcast("<"+c.cx+""+c.cy+""+parent.toDSChar(parent.maps[0].playermap[19][69])+""+c.colorstring,19,69,parent.maps[0]);
			c.pSend("color "+c.colorstring);
		}
	}
}
