public class Lev16 extends MainMapWithCode {
	public Lev16() {
		mapname = "lev16";
		init("lev16.dsmap","lev16.old",this);

		mapnumber = 15;
		diemap = 14;

	}
	public void start() {
		parent.npc2base.npc2s.addElement(new Red());

		/*int[][][] tmenus = new int[3][7][4];

		tmenus[1][1][0] = 97;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 5000;

		tmenus[1][2][0] = 98;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 5000;

		tmenus[1][3][0] = 99;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 6000;

		tmenus[1][4][0] = 100;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 5000;

		tmenus[1][5][0] = 101;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 4000;

		tmenus[1][6][0] = 102;
		tmenus[1][6][1] = 25;
		tmenus[1][6][2] = 6900;

		tmenus[2][1][0] = 97;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 5000;

		tmenus[2][2][0] = 98;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 3000;

		tmenus[2][3][0] = 99;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 2000;

		tmenus[2][4][0] = 100;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 1000;

		tmenus[2][5][0] = 101;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 4000;

		tmenus[2][5][0] = 102;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 3900;

		shops[0] = new Shop(0,"Red's Inventory",tmenus,
					62,62,7,19,9,21,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));*/

	}
}
