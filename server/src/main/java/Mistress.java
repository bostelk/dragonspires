public class Mistress extends NPC2 implements Interactable, Resetable, Givetoable {
	byte state=0;

	public Mistress() {
		String[] ts2s = new String[0];
		init("Faerie Mistress",8,73,10,ts2s,8,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		if (state==0) {
			String[] blah = new String[2];
			blah[0] = "1. Where'd you get that gem on your belt?";
			blah[1] = "2. Can I trade you something for that gem?";
			return blah;
		}
		else {
			String[] blah = new String[1];
			blah[0] = "1. Hi there!";
			return blah;
		}
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		if (state==0) {
			switch (opt) {
				case 1:
					retval="An adventurer found it and gave it to me in hopes of winning my affections. What a silly notion!";
					break;
				case 2:
					retval="Well, I would be willing to part with it, maybe for a WaterFreak potion, it gets kind of lonely out here in the woods.";
			}
		}
		else {
			switch (opt) {
				case 1:
					retval="Hiya!";
			}
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void reset() {
		state=0;
	}
	public void get(DSpiresSocket c) {
		if (state==0) {
			if (c.inhand==165) {
				c.pSend("("+name+": Oh! Thank you! Just what a girl like me needs! No more lonely days and nights with nobody to talk to! I don't know how I can repay you.. oh yes, my gem, here, take it!");
				c.setHands(168);
				state=1;
				return;
			}
		}

		c.pSend("("+name+": No thank you.");
	}
}