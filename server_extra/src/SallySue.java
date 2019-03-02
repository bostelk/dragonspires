public class SallySue extends NPC2 implements Interactable, Resetable, Givetoable {
	byte state=0;

	public SallySue() {
		String[] ts2s = {
				"*sniffle*",
			};

		init("Sally Sue",28,30,3,ts2s,24,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		if (state==0) {
			String[] blah = new String[2];
			blah[0] = "She's a little girl and she looks very upset.;What do you want to say?; ";
			blah[1] = "1. What's wrong? Why are you crying?";
			return blah;
		}
		else {
			String[] blah = new String[2];
			blah[0] = "She's a little girl happily eating some ice cream.;What do you want to say?; ";
			blah[1] = "1. Hi there!";
			return blah;
		}
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		if (state==0) {
			switch (opt) {
				case 1:
					retval="A mean ol' dog came and grabbed my ice cream cone from me!";
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
		if (c.inhand==195) {
			if (state==0) {
				c.pSend("("+name+": Oh thank you, "+c.name+"! Here, I drew a picture for you on the back of this paper I found in my dad's trasure chest!");
				c.pSend("("+name+" gives you the light blue scroll!");
				c.setHands(99);
				state=1;
				return;
			}
			else {
				c.pSend("("+name+": Yay! More ice cream! =D");
				c.setHands(0);
				return;
			}
		}

		c.pSend("("+name+": I only like vanilla ice cream!");
	}
}