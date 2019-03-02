public class Lev03 extends MainMapWithCode {
	public Lev03() {
		mapname = "lev03";
		init("lev03.dsmap","lev03.old",this);

		mapnumber = 2;
		//xstart = 7;
		//ystart = 42;
		diemap = 2;
	}
	public void start() {
		shops = new Shop[3];

		//Butcher
		String[] ts2s = new String[4];
		ts2s[0] = "It's so hard to find good squirrel these days.";
		ts2s[1] = "Squashed snails make a lovely marinade.";
		ts2s[2] = "Would you like to try my meat today?";
		ts2s[3] = "Mmm.. Lizard.";
		parent.npc2base.npc2s.addElement(new NPC2("Billy the Butcher",4,41,5,ts2s,this));

		int[][][] tmenus = new int[3][7][4];

		tmenus[1][1][0] = 39;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 15;

		tmenus[1][2][0] = 40;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 15;

		tmenus[1][3][0] = 54;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 300;

		tmenus[2][1][0] = 82;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 9;

		tmenus[2][2][0] = 83;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 20;

		tmenus[2][3][0] = 84;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 11;

		tmenus[2][4][0] = 85;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 17;

		shops[0] = new Shop(0,"* Billy's Butcher Shop *",tmenus,
					41,41,1,40,4,46,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		//ted
		ts2s = new String[6];
		ts2s[0] = "A weakling like yourself needs some quality armor.";
		ts2s[1] = "These swords are too much to handle for a pansy like you.";
		ts2s[2] = "If you see something you like, I'll just put it on your tab.";
		ts2s[3] = "Don't skip town with my weapons, or I'll skin you.";
		ts2s[4] = "My horns kick ass.";
		ts2s[5] = "Star Wars rocks. I wish I had one of those light sabers.";
		parent.npc2base.npc2s.addElement(new NPC2("Ted the Armorer",14,79,4,ts2s,this));

		tmenus = new int[3][7][4];

		tmenus[1][1][0] = 114;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 7000;

		tmenus[1][2][0] = 104;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 4000;

		tmenus[1][3][0] = 88;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 13000;

		tmenus[1][4][0] = 2;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 499;

		tmenus[1][5][0] = 249;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 70;

		tmenus[1][6][0] = 157;
		tmenus[1][6][1] = 25;
		tmenus[1][6][2] = 1000000;

		tmenus[2][1][0] = 88;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 6000;

		tmenus[2][2][0] = 114;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 2000;

		tmenus[2][3][0] = 104;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 2000;

		tmenus[2][4][0] = 105;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 800;

		shops[1] = new Shop(1,"<> Ted's Armory <>",tmenus,
					4,4,13,77,17,83,3,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		//ted
		ts2s = new String[3];
		ts2s[0] = "I am continuously working on new archery weapons.";
		ts2s[1] = "It is illogical to rely on melee weapons alone.";
		ts2s[2] = "My arrows are the strongest in the land.";
		parent.npc2base.npc2s.addElement(new NPC2("Tuvok",42,43,5,ts2s,this));

		tmenus = new int[3][7][4];

		tmenus[1][1][0] = 247;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 600;

		tmenus[1][2][0] = 251;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 400;

		tmenus[1][3][0] = 252;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 200;

		tmenus[1][4][0] = 212;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 500000;

		tmenus[1][5][0] = 206;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 2000000;

		tmenus[2][1][0] = 247;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 600;

		tmenus[2][2][0] = 251;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 400;

		tmenus[2][3][0] = 252;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 200;

		tmenus[2][4][0] = 212;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 90000;

		tmenus[2][5][0] = 206;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 200000;

		shops[2] = new Shop(2,">>>--Archery Range--->",tmenus,
					1,4,41,41,42,46,2,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
}
