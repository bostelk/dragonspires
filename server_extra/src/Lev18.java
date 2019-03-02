public class Lev18 extends DSMapServer { //ruin
	public Lev18() {
		mapname = "lev18";
		init("lev18.dsmap",this);

		mapnumber = 17;
		diemap = 0;

		allowcombat=true;
		attacktiles = new int[5];
		attacktiles[0] = -1;
		attacktiles[1] = 0;
		attacktiles[2] = 0;
		attacktiles[3] = 51;
		attacktiles[4] = 99;
	}
}
