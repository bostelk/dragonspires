public class Cricket extends NPC3 implements Interactable {

	public Cricket(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Cricket","!-!%",24,9,1,5,p);
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
				case 0:	send("("+name+" looks down at the ground at a tiny flower that is struggling to grow. He kneels down, scoops it out of the dirt, and places it in his pouch, saying: \"Don't worry, I'll find you a good home in the sunlight.\"");
						break;
				case 1:	send("("+name+" puts his hands up in the air and closes his eyes. A calm breeze rustles his hair.");
						break;
				case 3:	send("("+name+" rubs his forearms together, looks dissapointed, and says, \"I guess being a human is going to take some getting used to.\"");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		send("(Cricket dodges your swing. Before you can react he has taken your sword!!\n(Cricket grins and hands you sword back to you.\n(Cricket: Never underestimate a druid.");
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"You see a handsome guy in a long druidic;robe. He looks around at everything;as if he's amazed at how tall he is.; ","1. Hi, whatcha doin'?","2. Who are you?","3. Don't you ever give a streight answer?","4. Can I help you with anything?"};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("("+name+": Not much. I make sure that I do as little as possible whenever I have the time to.");
				break;
			case 2:
				c.pSend("("+name+": Who are any of us?");
				c.updateGold(-25);
				break;
			case 3:
				c.pSend("("+name+": Only if I'm asked a straight question.");
				break;
			case 4:
				c.pSend("("+name+": Not really. Everyone asks me that. I suppose they assume I'm part of some silly \"quest\" or something.");
		}
	}
}
