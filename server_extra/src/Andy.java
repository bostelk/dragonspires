public class Andy extends NPC2 implements Interactable, Givetoable {
	byte state=0;

	public Andy() {
		String[] ts2s = {
				"Blub!",
				"I invented this suit this morning! I call it Stupa Gear!",
				"Woo! Hey! Eel!!",
				"Fish are so much better and nicer than people..",
				".... AIR! AIR! *gasp!* ... air..... Puuhh!... Oh.. was standing on the hose again.."
			};

		init("Andy!",39,13,4,ts2s,1,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = new String[4];
		blah[0] = "What do you want to say?;";
		blah[1] = "1. Who are you?";
		blah[2] = "2. What are you doing in there?";
		blah[3] = "3. What kinds of animals you have?";
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="I'm Andy, of Andy's Amazing Aquarium!";
				break;
			case 2:
				retval="I invented a way to breathe under water. The only problem is I can't lift this heavy suit to get out.";
				break;
			case 3:
				retval="Just about everything. I also collect Blue Crabs, people seem to leave them out to dry an awful lot. It's a real shame.";
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void get(DSpiresSocket c) {
		if (c.inhand==48) {
			c.pSend("("+name+": Oh, this crab is just clinging to life!! I will nurse him back to health! Thank you, please take this Hermit Crab, they breath air, so it will make you a lovely pet.");
			c.setHands(204);
			return;
		}

		c.pSend("("+name+": Hmm. Interesting.");
	}
}