public class VBTree extends NPC2 implements Interactable, Givetoable {
	public VBTree() {
		String[] ts2s = new String[0];

		init("Treedle Dum",48,85,5,ts2s,8,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = {
					"1. What's with all the birds?",
					"2. Can I have one of your berries?"};
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="I just love them. They're fun to listen to.";
				break;
			case 2:
				retval="Well they're not so easy to give away, they're like a part of me! Maybe if you bring me another bird. I love crows the most.";
		}

		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void get(DSpiresSocket c) {
		if (c.inhand>=c.map.items+33&&c.inhand<=c.map.items+48) {
			c.pSend("("+name+": Wow! Another crow! Thank you! You may have one of my berries.\n(* You pluck a violet berry from the tree.");
			c.setHands(256);
			return;
		}

		c.pSend("("+name+": I only like birds. Crows are my favorite.");
	}
}