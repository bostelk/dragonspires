public class Hag extends NPC2 implements Interactable, Resetable {
	byte state=0;

	public Hag() {
		String[] ts2s = {
			"Eek. A swampie!",
			};

		init("Swamp Hag",4,10,5,ts2s,7,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = new String[((state==1) ? 1 : 3)];
		blah[0] = "1. Who are you?";
		if (state==0) {
			blah[1] = "2. You look like you're missing something.";
			blah[2] = ((c.inhand==232||c.findInInv(232)!=-1) ? "3. I've found your Dead Stone!" : null);
		}
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		switch (opt) {
			case 1:
				retval="I am the Swamp Hag. I live here.";
				break;
			default:
				if (state==0) {
					switch (opt) {
						case 2:
							retval="Someone stole my Dead Stone. It's my magical rock that protects me from the creatures that inhabit this swamp. Without it I'm completely powerless!!";
							break;
						case 3:
							if (c.inhand==232||c.findInInv(232)!=-1) {
								if (c.inhand==232) {
									c.map.placeItemAt(228,4,10,c.map);
									retval="Thank you so much! Have this green scroll!\n("+name+" gives you a green scroll!";
									c.setHands(57);
									state=1;
								}
								else
									retval="Where? You're not holding it.";
							}
					}
				}
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void reset() {
		state=0;
		server.maps[7].placeItemAt(228,4,10,server.maps[7]);
	}
}