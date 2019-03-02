public class Lev25 extends MainMapWithCode {
	public Lev25() {
		mapname = "lev25";
		init("lev25.dsmap","lev25.old",this);

		mapnumber = 24;
		diemap = 24;
	}
	public void start() {
		shops = new Shop[1];

		String[] ts2s = new String[1];
		ts2s[0] = "Just because I jog around DragonSpires late at night doesn't mean the Royal Guards have the right to hassle me.";
		parent.npc2base.npc2s.addElement(new NPC2("Somebody Else Fishmonger",34,26,4,ts2s,this));

		ts2s = new String[2];
		ts2s[0] = "Welcome to the Dragon's Gullet. Eat your damn food.";
		ts2s[1] = "Thanks for choosing the Dragon's Gullet. Can I get you anything or are you going to get off your ass and get it yourself?";
		parent.npc2base.npc2s.addElement(new NPC2("P.Krab",38,34,4,ts2s,this));


		ts2s = new String[4];
		ts2s[0] = "Yes, it's naturally red.";
		ts2s[1] = "Yes, they're real.";
 		ts2s[2] = "Behold the healing powers of my magical staff!";
 		ts2s[3] = "Care to join me for a roll in the daisies?";
		parent.npc2base.npc2s.addElement(new NPC2("Healer",29,16,5,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 47;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 200;

		tmenus[1][2][0] = 49;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 50;

		tmenus[1][3][0] = 32;
		tmenus[1][3][1] = 25;
		tmenus[1][3][2] = 100;

		tmenus[1][4][0] = 54;
		tmenus[1][4][1] = 25;
		tmenus[1][4][2] = 190;

		tmenus[1][5][0] = 131;
		tmenus[1][5][1] = 25;
		tmenus[1][5][2] = 69696;

		tmenus[2][1][0] = 132;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 1500;

		tmenus[2][2][0] = 133;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 1500;

		tmenus[2][3][0] = 134;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 1500;

		tmenus[2][4][0] = 100;
		tmenus[2][4][1] = 25;
		tmenus[2][4][2] = 1000;

		shops[0] = new Shop(0,"H E A L E R ' S",tmenus,
					41,4,27,13,30,20,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

		parent.npc2base.npc2s.addElement(new SallySue());
	}
}
