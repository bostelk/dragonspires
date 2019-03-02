public class Butch extends NPC3 implements Interactable, Resetable, Givetoable {

	byte state=0;

	public Butch(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Billy's dog Butch","    ",2,45,0,6,p);
		talk = 0;
		attributes|=NPC3_INTERACT;
		attributes|=NPC3_HIT;
	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random());
			switch (say) {
				case 0:	say("*slurp*");
						break;
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		say("BARK BARK BARK BARK BARK!!!!!!\n!3");
	}
	public String[] getOptions(DSpiresSocket c) {
		if (state==0) {
			String[] opts = {
			"He's a dog deeply involved in devouring an ice;cream cone.;What do you want to do?; ",
			"1. Take the ice cream cone."};
			return opts;
		}
		else {
			String[] opts = {
			"He's a dog deeply involved in playing with a funball;What do you want to do?; ",
			"1. Take the funball."};
			return opts;
		}
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;

		if (state==0) {
			switch (opt) {
				case 1:
					retval="GRRRRRRR BARK BARK BARK BARK BARK!!!!\n!3\n(* Maybe you should give him something he likes more than the ice cream.";
			}
		}
		else {
			switch (opt) {
				case 1:
					retval="GRRRRRRR BARK BARK BARK BARK BARK!!!!\n!3";
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
			if (c.inhand==8) {
				c.pSend("("+name+" barks happily with his tail wagging. You put the funball down and he bounces it around with excitement.\n(* You carefully pickup the ice cream cone while he isn't looking.");
				c.setHands(195);
				state=1;
				return;
			}
		}
		c.pSend("("+name+" cocks his head and looks at you.");
	}
}
