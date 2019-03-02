public class Lev15 extends MainMapWithCode {
	public Lev15() {
		mapname = "lev15";
		init("lev15.dsmap",this);

		mapnumber = 14;
		diemap = 14;
	}
	public void start() {
		shops = new Shop[1];

		//WCGS
		String[] ts2s = {"Tralala."};
		parent.npc2base.npc2s.addElement(new NPC2("Shop Owner",20,20,14,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 104;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 2000;

		tmenus[1][2][0] = 135;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 2000;

		tmenus[1][3][0] = 114;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 6998;

		tmenus[1][4][0] = 105;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 2000;

		tmenus[1][5][0] = 47;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 190;

		tmenus[2][1][0] = 104;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 1000;

		tmenus[2][2][0] = 135;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 1000;

		tmenus[2][3][0] = 114;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 300;

		tmenus[2][4][0] = 59;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 1000;

		tmenus[2][5][0] = 99;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 1000;

		shops[0] = new Shop(0,"[] Water City General Store []",tmenus,
					41,41,18,18,22,25,6,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		parent.npc2base.npc2s.addElement(new Steve());
	}
}
