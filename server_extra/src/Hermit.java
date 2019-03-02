public class Hermit extends NPC2 implements Interactable, Resetable, Givetoable {
	byte state=0;

	public Hermit() {
		String[] ts2s = {
				"What?",
				"Leave me alone!",
				"Go away.",
				"You look like a clown, you know that?",
				"Don't trample my flowers."
			};

		init("Turtle Hermit",35,64,5,ts2s,4,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = new String[4];
		blah[0] = "What do you want to say?;";
		blah[1] = "1. Who are you?";
		blah[2] = "2. Why do you live out here?";
		blah[3] = "3. Aren't you lonely?";
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="I'm just an old hermit.";
				break;
			case 2:
				retval="I don't like to be around people. They all smell like poop.";
				break;
			case 3:
				retval="I could use a pet, I guess, to keep me company, but I don't know what kind would be good for my type of personality.";
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void reset() {
		state=0;
	}
	public void get(DSpiresSocket c) {
		if (c.inhand==204) {
			if (state==0) {
				c.pSend("("+name+": Oh, look at how he hides in his shell when I poke him! He's perfect! Thank you so much, here, have this blue scroll.\n("+name+" gives you a blue scroll!");
				c.setHands(58);
				state=1;
			}
			else
				c.pSend("("+name+": No thanks, I'm happy with the one I have! I hope he doesn't run off.");
			return;
		}

		c.pSend("("+name+": I don't want that! What is that? Where has it been??");
	}
}