public class Lev05 extends MainMapWithCode {
	public Lev05() {
		mapname = "lev05";
		init("lev05.dsmap",this);

		mapnumber = 4;
		diemap = 2;

		allowcombat=true;
		attacktiles = new int[5];
		attacktiles[0] = 1;
		attacktiles[1] = 9;
		attacktiles[2] = 32;
		attacktiles[3] = 15;
		attacktiles[4] = 45;
	}
	public void start() {

		parent.npc2base.npc2s.addElement(new Hermit());

		//hosp
		String[] ts2s = new String[3];
		ts2s[0] = "Welcome to the Animal Hospital.";
		ts2s[1] = "Poor animals. Some people can be so cruel.";
		ts2s[2] = "We always fix animals up so they're good as new!";
		parent.npc2base.npc2s.addElement(new NPC2("Kitten",41,27,4,ts2s,this));


		shops = new Shop[1];

		int[][][] tmenus = new int[3][7][4];

		tmenus[1][1][0] = 19;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 500;

		tmenus[1][2][0] = 32;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 110;

		tmenus[1][3][0] = 47;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 200;

		tmenus[1][4][0] = 50;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 475;

		tmenus[2][1][0] = 82;
		tmenus[2][1][1] = items+1;
		tmenus[2][1][2] = 1;

		tmenus[2][2][0] = 83;
		tmenus[2][2][1] = items+9;
		tmenus[2][2][2] = 1;

		tmenus[2][3][0] = 85;
		tmenus[2][3][1] = items+17;
		tmenus[2][3][2] = 1;

		tmenus[2][4][0] = 84;
		tmenus[2][4][1] = items+33;
		tmenus[2][4][2] = 1;


		shops[0] = new Shop(0,"~ The Animal Hospital ~",tmenus,
					5,1,39,26,42,31,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
}
