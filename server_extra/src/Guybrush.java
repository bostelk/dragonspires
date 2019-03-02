public class Guybrush extends NPC2 implements Interactable, Resetable {
	byte state=0;

	public Guybrush() {
		String[] ts2s = new String[0];

		init("a statue of Guybrush",42,3,14,ts2s,28,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah;
		if (state==0) {
			boolean check = (c.inhand==168||c.findInInv(168)!=-1);
			blah = new String[((check) ? 4 : 3)];
			blah[0] = "What do you want to do?; ";
			blah[1] = "1. Examine the statue.";
			blah[2] = "2. Try to pull out a pink gem from the helmet.";
			if (check)
				blah[3] = "3. Place your pink gem in empty slot on the helmet.";
		}
		else {
			blah = new String[1];
			blah[0] = "1. Say nothing.";
		}
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		if (state==0) {
			switch (opt) {
				case 1:
					retval="The statue seems to be missing something. A pink gem is gone from its helmet.";
					break;
				case 2:
					retval="You tug at a pink gem, but it wont budge! There seems to be a magical force at work here.";
					break;
				case 3:
					if (c.inhand==168) {
						c.pSend("(* You place the pink gem into the empty slot.\n(* The statue of Guybrush magically transforms into Guybrush himself!");
						c.setHands(0);
						server.maps[28].placeItemAt(122,42,3,server.maps[28]);
						name="Guybrush";
						c.pSend("("+name+": WHEW! Finally! Thanks for helping me out! I've been stuck like that hours. Here, take this before I get stuck again. And be careful with it!");
						c.pSend("("+name+" gives you the yellow scroll!");
						c.setHands(59);
						state=1;
						return;
					}
					else if (c.findInInv(168)!=-1)
						c.pSend("(* You would, but the gem is in your inventory.");
			}
			if (retval!=null)
				c.pSend("("+name+": "+retval);
		}
	}
	public void reset() {
		state=0;
		name="a statue of Guybrush";
		server.maps[28].placeItemAt(61,42,3,server.maps[28]);
	}
}