public class Steve extends NPC2 implements Interactable, Givetoable, Resetable {
	byte state=-1;
	int bx=0, by=0;

	public Steve() {
		String[] ts2s = {
		 		"Give me a bounty and I'll hunt it.",
		 		"Pay no attention to the movement in the bag.",
 				"A lot of people need to be taught a lesson. Call me Professor Steve."};


		init("Steve the Bounty Hunter",19,58,12,ts2s,14,this);
		reset();
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = new String[3];
		blah[0] = "What do you want to say?;";
		blah[1] = "1. Who are you?";
		blah[2] = "2. Any bounties on your list?";
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="I'm Steve. I'm a bounty Hunter.";
				break;
			case 2:
				if (state==0)
					retval="Eh! The greatest thief of DragonSpires; Bekar. I've been searching for him for awhile now. One of these days he's going to run into the right person, hopefully me, and get what he deserves. I would reward well anyone who brings me his head.";
				else
					retval="There's this guy named Telkandore. I can't kill him because of his damn macros! It peepeees me off!";
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void get(DSpiresSocket c) {
		if (c.inhand==246) {
			c.pSend("("+name+": Heheheh. You got Bekar! That bastard. Here's your bounty!\n("+name+" gives you a red scroll!");
			c.setHands(97);
			state=1;
			return;
		}

		c.pSend("("+name+": Wtf?");
	}
	public void reset() {
		if (state!=-1)
			server.maps[23].placeItemAt(261,24,22,server.maps[23]);
		state=0;
	}
}