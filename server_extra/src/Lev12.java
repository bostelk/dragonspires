public class Lev12 extends MainMapWithCode {
	public Lev12() {
		mapname = "lev12";
		init("lev12.dsmap",this);

		mapnumber = 11;
		xstart = 18;
		ystart = 56;
		diemap = 11;

		allowcombat=true;
		attacktiles = new int[5];
		attacktiles[0] = 1;
		attacktiles[1] = 31;
		attacktiles[2] = 34;
		attacktiles[3] = 36;
		attacktiles[4] = 44;
	}
	public void start() {
		shops = new Shop[1];

		String[] ts2s = new String[6];
		ts2s[0] = "Man, I gots me alot of pelts!";
		ts2s[1] = "Can I interest you in some antlers? Good for Antler stew!";
		ts2s[2] = "Maybe I would be more successful if I actually set traps.";
 		ts2s[3] = "Brr. I'm chilly.";
 		ts2s[4] = "I only have use for furry things.";
		ts2s[5] = "I need to find some good Furcadian pelts.";
		parent.npc2base.npc2s.addElement(new NPC2("Furrier",1,53,3,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 104;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 3900;

		tmenus[1][2][0] = 105;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 3100;

		tmenus[2][1][0] = 82;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 14;

		tmenus[2][2][0] = 84;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 13;

		tmenus[2][3][0] = 85;
		tmenus[2][3][1] = 25;
		tmenus[2][3][2] = 16;

		shops[0] = new Shop(0,"Furrier's Pelts",tmenus,
					5,5,0,50,2,56,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));

	}
}
