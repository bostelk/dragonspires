public class Lev23 extends MainMapWithCode {
	public Lev23() {
		mapname = "lev23";
		init("lev23.dsmap",this);

		mapnumber = 22;
		diemap = 22;
		walktrig=true;
	}
	public void start() {
		shops = new Shop[1];

		//WCGS
		String[] ts2s = {"Zzzzzz.",};
		parent.npc2base.npc2s.addElement(new NPC2("CTF Shopkeeper",45,80,15,ts2s,this));

		int[][][] tmenus = new int[3][7][4];
		tmenus[1][1][0] = 145;
		tmenus[1][1][1] = 25;
		tmenus[1][1][2] = 10;

		tmenus[1][2][0] = 146;
		tmenus[1][2][1] = 25;
		tmenus[1][2][2] = 10;

		tmenus[2][1][0] = 145;
		tmenus[2][1][1] = 25;
		tmenus[2][1][2] = 10;

		tmenus[2][2][0] = 146;
		tmenus[2][2][1] = 25;
		tmenus[2][2][2] = 10;

		shops[0] = new Shop(0,"~| The CTF Outlet |~",tmenus,
					6,6,43,79,47,83,-1,true,
					(NPC2)parent.npc2base.npc2s.elementAt(parent.npc2base.npc2s.size()-1));
	}
	public void walkTrigger(DSpiresSocket c) {
		if (c.colorstring.charAt(3)==' ')
			return;
		if (c.cx-32 ==  40 && c.cy-32 == 80)
			stepOnGreen(c);
		if (c.cx-32 ==  43 && c.cy-32 == 75)
			stepOnYellow(c);
	}
	public void stepOnGreen(DSpiresSocket c) {
		if (c.facing == 3)
			c.colorstring = c.ocolorstring;
		else
			c.colorstring = "#"+c.colorstring.substring(1);
	}
	public void stepOnYellow(DSpiresSocket c) {
		if (c.facing == 3)
			c.colorstring = c.ocolorstring;
		else
			c.colorstring = "+"+c.colorstring.substring(1);
	}
}
