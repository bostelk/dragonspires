public class Lev02 extends MainMapWithCode {
	public Lev02() {
		mapname = "lev02";
		init("lev02.dsmap",this);

		mapnumber = 1;
	}
	public void start() {
		shops = new Shop[1];

		//Wizard
		String[] ts2s = new String[4];
		ts2s[0] = "Hocus Pocus puddin' and pie!";
		ts2s[1] = "Alakazam, Alakazat!";
		ts2s[2] = "Can I interest you in a magic potion?";
		ts2s[3] = "You look like someone who needs a protection spell!";
		parent.npc2base.npc2s.addElement(new NPC2("Merlin Jr.",31,46,4,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 32;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 110;

		tmenus[1][2][0] = 47;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 241;

		tmenus[1][3][0] = 140;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 250;

		tmenus[1][4][0] = 50;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 475;

		tmenus[1][5][0] = 54;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 250;

		tmenus[2][1][0] = 62;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 20;

		tmenus[2][2][0] = 113;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 25;

		tmenus[2][3][0] = 53;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 40;

		tmenus[2][4][0] = 140;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 100;

		tmenus[2][5][0] = 97;
		tmenus[2][5][1] = 25;
		tmenus[2][5][2] = 1000;

		tmenus[2][6][0] = 58;
		tmenus[2][6][1] = 25;
		tmenus[2][6][2] = 1111;

		shops[0] = new Shop(0,"(*) Merlin Jr.'s Magic Shop (*)",tmenus,
					41,41,30,44,34,50,5,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		//Andy
		parent.npc2base.npc2s.addElement(new Andy());
	}
}
