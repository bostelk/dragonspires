public class Lev22 extends MainMapWithCode {
	public Lev22() {
		mapname = "lev22";
		init("lev22.dsmap",this);

		mapnumber = 21;
		diemap = 21;

		allowcombat=true;
		attacktiles = new int[5];
		attacktiles[0] = 6;
		attacktiles[1] = 0;
		attacktiles[2] = 0;
		attacktiles[3] = 51;
		attacktiles[4] = 99;

	}
	public void start() {
		shops = new Shop[1];

		//WCGS
		String[] ts2s = {"Go in there. Kick some ass. Then buy stuff from me.",
					"My prices are IIINNNNSANE.. muahaha.",
					"Don''t walk all the way back to town to buy your stuff!",
					"Hey, pal. I got some quality merchandise!",
					"Man this is a prime location!",
					"What''re you looking at? Go kick some ass!"};
		parent.npc2base.npc2s.addElement(new NPC2("Killoseum Owner",33,65,3,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 32;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 500;

		tmenus[1][2][0] = 54;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 300;

		tmenus[1][3][0] = 47;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 400;

		tmenus[1][4][0] = 140;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 700;

		tmenus[1][5][0] = 46;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 700;
		
		tmenus[1][6][0] = 19;
		tmenus[1][6][1] = 25;
		tmenus[1][6][2] = 7000;

		tmenus[2][1][0] = 32;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 5;

		tmenus[2][2][0] = 54;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 10;

		tmenus[2][3][0] = 47;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 2;

		tmenus[2][4][0] = 140;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 6;

		tmenus[2][5][0] = 46;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 6;
		
		tmenus[2][6][0] = 19;
		tmenus[2][6][1] = 25;
		tmenus[2][6][2] = 1;

		shops[0] = new Shop(0,"][ KILLOSEUM ][",tmenus,
					4,1,30,66,33,72,6,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
}
