public class Lev29 extends MainMapWithCode {
	public Lev29() {
		mapname = "lev29";
		init("lev29.dsmap","lev29.old",this);

		mapnumber = 28;
		diemap = 2;

	}
	public void start() {
		parent.npc2base.npc2s.addElement(new Guybrush());
	}
}
