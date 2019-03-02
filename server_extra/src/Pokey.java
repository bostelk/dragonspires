public class Pokey extends NPC3 implements Interactable {

	public Pokey(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Pokey|the|Druid","  -%",24,36,1,4,p);
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
				case 0:	say("Cookie.");
						break;
				case 1:	say("Only Superman can save us now!");
						break;
				case 2:	say("Gasp! Italians!!");
						break;
				case 3:	send("("+name+" stands there and looks at you, smiling.");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		say("I remain unaffected by your attempts to harm me!");
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"Pokey the Druid is a short, fat little man in a;black and white robe. He has a distant look;in his eyes and a deviant little smile.;What do you want to say?; ","1. What are you doing?","2. Help me, Pokey! I am drowning!","3. Ask for some Arctic Circle Candy","4. Ask for money for beer and cigarettes."};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("("+name+": I have poked you in the eye.. with Democracy!");
				break;
			case 2:
				c.pSend("("+name+": Smeat will do that to you.");
				break;
			case 3:
				c.pSend("("+name+": Of course, I have a seemingly limitless supply of Arctic Circle Candy.. And yet it is gone...");
				break;
			case 4:
				c.pSend("("+name+": You get.. GUN!\n("+name+" shoots "+c.name+" in the leg!");
		}
	}
}
