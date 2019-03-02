public class RBTree extends NPC2 implements Interactable, Givetoable {
	public RBTree() {
		String[] ts2s = new String[0];

		init("Treedle Dee",46,86,5,ts2s,8,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = {
					"1. What's with all the snails?",
					"2. Can I have one of your berries?"};
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="I just love them. They're fun to watch.";
				break;
			case 2:
				retval="Well they're not so easy to give away, they're like a part of me! Maybe if you bring me another snail.";
		}

		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void get(DSpiresSocket c) {
		if (c.inhand>=c.map.items+9&&c.inhand<=c.map.items+16) {
			c.pSend("("+name+": Wow! Another snail! Thank you! You may have one of my berries.\n(* You pluck a red berry from the tree.");
			c.setHands(255);
			return;
		}

		c.pSend("("+name+": I only like snails.");
	}
}