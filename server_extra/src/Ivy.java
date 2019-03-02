public class Ivy extends NPC3 implements Interactable {

	public Ivy(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Ivy","#)&$",34,72,1,4,p);
		talk = 0;
		attributes|=NPC3_INTERACT;
		attributes|=NPC3_WANDER;
		attributes|=NPC3_RANDOM_SPEAK;
		attributes|=NPC3_HIT;

	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random() * 3);
			switch (say) {
				case 0:	say("Honey, when I touch you, the itch never goes away.");
						break;
				case 1:	say("I hope those two herbheads, Sir J. and Silent Olaf don't find me here. You sell them a medicinal plant ONCE and they think that you'll score 'em for them all the time.");
						break;
				case 3:	send("("+name+" stops a moment to streighten her stockings. She catches you staring and winks at you!");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		c.pSend("(Ivy smiles at you. As she does you feel dizzy, like you're about to pass out. The world becomes fuzzy and you can't quite remember what you were about to do.");
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"You see a beautiful young woman with long,;flowing red hair.; ",
						"1. Say Hi.",
						"2. Ask, \"What's a nice girl like you doing in an inn like this?\"",
						"3. Ask Ivy if she needs help with anything.",
						"4. Poke Ivy in the tummy."
						};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("("+name+": Hi, yourself, stranger.");
				break;
			case 2:
				c.pSend("("+name+" smiles: I'd tell you, but I don't think you could handle it.");
				break;
			case 3:
				c.pSend("("+name+": I doubt you could help. I usually require at least five stout men.");
				break;
			case 4:
				c.pSend("(Ivy arches an eyebrow at you. You have successfully freaked her out.");
		}
	}
}
