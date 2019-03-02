public class Lev07 extends MainMapWithCode {
	public Lev07() {
		mapname = "lev07";
		init("lev07.dsmap","lev07.old",this);

		mapnumber = 6;
		diemap = 6;

	}
	public void start() {
		//alch
		parent.npc2base.npc2s.addElement(new Alchemist());

		shops = new Shop[2];

		int[][][] tmenus = new int[3][7][4];

		tmenus[1][1][0] = 89;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 9666;

		tmenus[1][2][0] = 44;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 50;

		tmenus[1][3][0] = 46;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 260;

		tmenus[1][4][0] = 249;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 60;

		tmenus[2][1][0] = 46;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 125;

		tmenus[2][2][0] = 89;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 6669;

		tmenus[2][3][0] = 92;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 70;

		tmenus[2][4][0] = 98;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 1000;

		shops[0] = new Shop(0,"Roy's House of Evil Knicknacks",tmenus,
					62,62,43,18,46,23,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		String[] ts2s = new String[1];
		ts2s[0] = "Contrary to popular belief, being a Succubus *doesn't* suck.";
		parent.npc2base.npc2s.addElement(new NPC2("Succubus",44,52,6,ts2s,this));

		tmenus = new int[3][7][4];

		tmenus[1][1][0] = 46;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 258;

		tmenus[1][2][0] = 50;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 475;

		tmenus[1][3][0] = 8;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 10;

		tmenus[2][1][0] = 46;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 125;

		tmenus[2][2][0] = 89;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 7000;

		tmenus[2][3][0] = 50;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 70;

		tmenus[2][4][0] = 8;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 10;

		shops[1] = new Shop(1,"Succubus' Chamber",tmenus,
					2,1,42,46,47,56,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
}
