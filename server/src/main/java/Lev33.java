public class Lev33 extends MainMapWithCode {
	public Lev33() {
		mapname = "lev33";
		init("lev33.dsmap",this);

		mapnumber = 32;
		diemap = 32;
	}
	public void start() {
		parent.npc2base.npc2s.addElement(new SirJ());
		parent.npc2base.npc2s.addElement(new SilentOlaf());
	}
}
