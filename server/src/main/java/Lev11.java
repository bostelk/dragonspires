public class Lev11 extends DSMapServer {
	public Lev11() {
		mapname = "lev11";
		init("lev11.dsmap","lev11.old",this);

		mapnumber = 10;
		diemap = 9;

		allowcombat=true;
		attacktiles = new int[5];
		attacktiles[0] = -1;
		attacktiles[1] = 0;
		attacktiles[2] = 0;
		attacktiles[3] = 51;
		attacktiles[4] = 99;
	}
}
