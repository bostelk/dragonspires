public class Lev08 extends MainMapWithCode {
	public Lev08() {
		mapname = "lev08";
		init("lev08.dsmap",this);

		mapnumber = 7;
	}
	public void start() {
		parent.npc2base.npc2s.addElement(new Hag());
	}
}
