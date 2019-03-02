public class SilentOlaf extends NPC2 implements Interactable, Givetoable {

	public SilentOlaf() {
		String[] ts2s = new String[0];
		init("Silent Olaf",43,49,30,ts2s,32,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah={"Silent Olaf is, to put it mildly, a large man.;He has a goatee, and a shirt with a picture of a;medicinal plant under his jacket.; ",
						"1. Ask: Who are you?",
						"2. Ask: What'cha doin'?",
						"3. Poke Silent Olaf in the tummy."};
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("(Sir J. leans over and replies: \"That's Silent Olaf, dude. He never says anything, so when he finally does speak, it just seems more meaningful.\"");
				break;
			case 2:
				c.pSend("(Silent Olaf stands there and looks at you like you're an idiot.");
				break;
			case 3:
				c.pSend("(You poke Silent Olaf in the tummy. As you do, Olaf hauls off and socks you in the face.\n(Sir J.: He's not the Pilsbury Dough Boy, dude.");
		}
	}
	public void get(DSpiresSocket c) {
		if (c.inhand==49) {
			c.pSend("(Silent Olaf takes out his glasses and looks at the plant. Satisfied, Olaf pockets it and gives you 50 gold.");
			c.setHands(0);
			c.updateGold(50);
		}
	}
}