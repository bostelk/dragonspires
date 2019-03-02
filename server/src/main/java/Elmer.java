public class Elmer extends NPC3 implements Interactable, Resetable, Givetoable {

	byte state=0;

	public Elmer(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Elmer the Lost","!#'!",35,76,1,5,p);
		talk = 0;
		attributes|=NPC3_INTERACT;
		attributes|=NPC3_HIT;
	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random());
			switch (say) {
				case 0:	say("*cough* *pant* *wheeze*");
						break;
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		say("Ow! Come on, I'm dying already!");
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {
		"1. You look thirsty.",
		"2. Do you have any magic items?"};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;

		if (state==0) {
			switch (opt) {
				case 1:
					retval="Excellent observation, genius! I'm dying here!";
					break;
				case 2:
					retval="Yes, but what good is turning into a rock if you're about to die of thirst??";
			}
		}
		else {
			switch (opt) {
				case 1:
					retval="A little, but I'm okay.";
					break;
				case 2:
					retval="No anymore! I just traded my grey scroll for some water. Now I have to go find another one!";
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
			if (c.inhand==153) {
				c.pSend("("+name+": Thank you so much!!! *gulps down the water* Here, take this grey scroll, it will help protect you on your journeys.\n("+name+" gives you a grey scroll!");
				c.setHands(101);
				state=1;
			}
		}
		else {
			c.pSend("("+name+": No thanks, I don't need anything right now.");
		}
	}
}
