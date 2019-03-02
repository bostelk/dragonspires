public class Lev32 extends MainMapWithCode {
	public Lev32() {
		mapname = "lev32";
		init("lev32.dsmap",this);

		mapnumber = 31;
		diemap = 31;
	}
	public void start() {
		String[] ts2s = new String[4];
		ts2s[0] = "Well look at you. Ain't you cute.";
		ts2s[1] = "What did you do THIS time?";
		ts2s[2] = "Bail is 1000 gold. Just walk up to your cell door and '-dropgold 1000'.";
		ts2s[3] = "Man you stink..";
		parent.npc2base.npc2s.addElement(new NPC2("The Warden",30,54,5,ts2s,this));
	}
}
