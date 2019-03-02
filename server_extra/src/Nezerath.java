public class Nezerath extends i1Enemy {
	int talk=0;

	public Nezerath(int tx, int ty, boolean nwd, DSMapServer m) {
		special_trig=true;
		special_die=true;
		init(15,tx,ty,nwd,m);
	}
	public void mytrigger() {

		if (++talk==6) {
			int dir;//,x,y;

			switch (facing) {
				case 1:
					dir=9;
					break;
				case 9:
					dir=1;
					break;
				case 7:
					dir=3;
					break;
				default:
					dir=7;
			}

			//x = parent.parent.nextx(this.x-32,this.y-32,dir);
			//y = parent.parent.nexty(this.x-32,this.y-32,dir);

			map.addEnemy(new Enemy(5,map.parent.nextx(this.x-32,this.y-32,dir),map.parent.nexty(this.y-32,dir),true,map));

			String tosay="";
			switch ((int)Math.round(Math.random()*3)) {
				case 0:
					tosay="Nezerath: This city will perish under my vengeful heel!";
					break;
				case 1:
					tosay="Nezerath: Lowly worms, you cannot hope to defeat a master of the Runes.";
					break;
				case 2:
					tosay="Nezerath: You are all pitiful, you have no hope of controlling the secret of the scrolls!";
					break;
				case 3:
					tosay="Nezerath: Sunshine, lollipops, and rainbows!";
			}

			map.limitedBroadcast("[\""+tosay,this.x-32,this.y-32,map);

			talk=0;
		}

		trigger();
		trigger();
	}
	public void whendie() {
		map.broadcast("(The rune of darkness that Nezerath wears around his neck shatters into small shards! He lets out a primal howl as electricity punches through him, vaporizing his body into a haze. He is gone for now, but shall always loom over the city like a black cloud of doom.",map);
		map.parent.nez=false;
	}
}
