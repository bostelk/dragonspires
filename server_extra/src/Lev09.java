public class Lev09 extends MainMapWithCode {
	public Lev09() {
		mapname = "lev09";
		init("lev09.dsmap",this);

		mapnumber = 8;
		diemap = 0;

	}
	public void start() {
		shops = new Shop[1];

		//Faerie Mistress
		parent.npc2base.npc2s.addElement(new RBTree());
		parent.npc2base.npc2s.addElement(new VBTree());
		parent.npc2base.npc2s.addElement(new Mistress());

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 19;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 200;

		tmenus[1][2][0] = 137;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 500;

		tmenus[1][3][0] = 50;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 475;

		tmenus[2][1][0] = 140;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 200;

		tmenus[2][2][0] = 100;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 3000;

		tmenus[2][3][0] = 50;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 50;

		shops[0] = new Shop(0,"Enasni Garden",tmenus,
					5,8,5,70,10,74,8,false,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
}
