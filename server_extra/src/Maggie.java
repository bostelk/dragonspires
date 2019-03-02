public class Maggie extends NPC2 implements Interactable {
	byte state=0;

	public Maggie() {
		String[] ts2s = new String[0];

		init("Maggie",45,11,10,ts2s,19,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		boolean necklace=(c.inhand==211||c.findInInv(211)!=-1);
		String[] blah = new String[((necklace) ? 5 : 3)];
		blah[0] = "Do you dare disturb this weeping ghost?; ";
		blah[1] = "1. Who are you?";
		blah[2] = "2. What are you doing out here?";
		if (necklace) {
			blah[3] = "3. I have your necklace. Your husband died in;   Prince Lichder's dungeon, I took the necklace;   from his bones.";
			blah[4] = "4. I have your necklace. Your husband told me;   to return it to you, and that he will return;   to you as soon as he can.";
		};
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="My name is Maggie, who are you? Have you seen my husband?";
				break;
			case 2:
				retval="I'm waiting for my husband. I gave him my necklace for good luck, and he promised to return to me. I'm so worried, I just can't eat or sleep until he returns to me!!";
				break;
			default:
				if (c.inhand==211||c.findInInv(211)!=-1) {
					switch (opt) {
						case 3:
							c.pSend("("+name+" disappears with an echo of weeping.");
							c.map.placeItemAt(0,45,11,c.map);
							c.requestDialog(0);
							return;
						case 4:
							c.pSend("("+name+": Oh please, take this silver scroll, it's the only thing I have of any value. Thank you so much.");
							c.pSend("("+name+" gives you a silver scroll!");
							if (c.inhand==211)
								c.setHands(102);
							else {
								int p=c.findInInv(211);
								c.inventory[p]=102;
								c.pSend("i"+((char)(p+32))+""+server.encode(102));
								c.pSend("(* The silver scroll is placed in your inventory.");
							}
							c.pSend("("+name+" slowly disappears.");
							c.map.placeItemAt(0,45,11,c.map);
							c.requestDialog(0);
							return;
					}
				}
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void reset() {
		server.maps[19].placeItemAt(227,45,11,server.maps[19]);
	}
}