public class ElderlyWoman extends NPC3 implements Interactable, Resetable {
	int state=0;

	public ElderlyWoman(NPC3Thread p, DSMapServer m) {
		map=m;
		init("An elderly woman","   8",30,49,2,7,p);
		talk = 0;
		attributes|=NPC3_INTERACT;
		attributes|=NPC3_HIT;
	}
	public void trigger() {
		if (++talk==3) {
			int say = (int)Math.round(Math.random() * 3);
			switch (say) {
				case 0:	say("Is this the way home? Wait.. maybe this way..");
						break;
				case 1:	say("Oh, my. Where was I going? To the store?");
						break;
				case 3:	send("("+name+" stops a moment and thinks.");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		c.pSend("(The elderly woman screams bloody murder! Angry villagers come out of nowhere and surround you!! Before you know it, you're locked up in prison!!");
		c.jail();
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"This woman looks lost.; ","1. Say: Hello.","2. Ask: Are you lost?",(state==0?"3. Ask: Where do you live?":"")};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("("+name+": OH! You scared me!");
				break;
			case 2:
				c.pSend("("+name+": I can't remember if I'm lost or not..");
				break;
			case 3:
				if (state==0) {
					c.pSend("("+name+": I live with my daughter just outside of town.. Wait! I remember now.. Thank you, I would never have remembered if you didn't ask!\n("+name+" gives you 10 gold and a kiss on the cheek!");
					c.updateGold(10);
					state=1;
				}
		}
	}
	public void reset() {
		state=0;
	}
}