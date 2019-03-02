public class Lev20 extends MainMapWithCode {
	public Lev20() {
		mapname = "lev20";
		init("lev20.dsmap","lev20.old",this);

		mapnumber = 19;
		diemap = 24;

	}
	public void start() {
		String[] ts2s = new String[4];
		ts2s[0] = "I have such a good life. A 3 by 3 room, a chair to sit in, a table to sleep and write on.. yeah, I've finally made it!";
		ts2s[1] = "My whole life has been packed into this sack.. to tell the truth, it's mostly porn.";
		ts2s[2] = "If you happen to get the chance to see my landscaping work.. DON'T TOUCH ANYTHING!";
		ts2s[3] = "Oh, hell, someone stole my blowup doll.";
		parent.npc2base.npc2s.addElement(new NPC2("Grounds Keeper William",34,30,1,ts2s,this));
		parent.npc2base.npc2s.addElement(new Maggie());
	}
}
