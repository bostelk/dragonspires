public class Lev19 extends MainMapWithCode {
	public Lev19() {
		mapname = "lev19";
		init("lev19.dsmap",this);

		mapnumber = 18;
		diemap = 24;

	}
	public void start() {
		shops = new Shop[1];

		String[] ts2s = new String[5];
 		ts2s[0] = "If Ted calls me 'babe' one more time, WHACK! Off comes his head.";
 		ts2s[1] = "Sometimes I wonder why men run from me. Maybe I should bathe.. nah.";
 		ts2s[2] = "Stop looking at me.";
 		ts2s[3] = "I hate when I get swamp dweller all over my sword.";
 		ts2s[4] = "Warlocks are the ugliest bunch of invisible freaks I've ever not seen.";
		parent.npc2base.npc2s.addElement(new NPC2("Warlette",28,50,6,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 158;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 99999999;

		tmenus[1][2][0] = 126;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 20000000;

		tmenus[1][3][0] = 159;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 1200000;

		tmenus[1][4][0] = 160;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 900000;

		tmenus[1][5][0] = 161;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 1000000;

		tmenus[2][1][0] = 158;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 200000;

		tmenus[2][2][0] = 126;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 50000;

		tmenus[2][3][0] = 159;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 300000;

		tmenus[2][4][0] = 160;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 100000;

		tmenus[2][5][0] = 161;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 60000;

		shops[0] = new Shop(0,"Warlette's Weapon Emporium",tmenus,
					1,1,27,46,30,52,10,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
}
