public class Red extends NPC2 implements Interactable, Resetable, Givetoable {
	byte state=0;

	public Red() {
		String[] ts2s = {"If I had all the scrolls, I would flood the city with molten rock!"};
		init("Red",9,19,11,ts2s,15,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		if (state==0) {
			String[] blah = new String[2];
			blah[0] = "1. What's a Fire Nymph like you doing in a;place like this?";
			blah[1] = "2. What do you need to obtain your goal?";
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
					retval="I'm hanging around your realm, trying to figure out the means of bottling living souls. Like the man called \"Nezerath\" does with Warlocks.";
					break;
				case 2:
					retval="Ideally, I need unfinished potion so I can figure out the secret of the transfer.";
			}
		}
		else {
			switch (opt) {
				case 1:
					retval="?";
			}
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void reset() {
		state=0;
		server.neztable=0;
	}
	public void get(DSpiresSocket c) {
		if (state==0) {
			if (c.inhand==210) {
				c.pSend("("+name+": Yes! I will find the secret thanks to you. A reward for you help, have this purple scroll.\n("+name+" gives you a purple scroll!");
				c.setHands(98);
				state=1;
				return;
			}
		}

		c.pSend("("+name+": It isn't what I need!");
	}
}