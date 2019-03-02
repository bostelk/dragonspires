public class Alchemist extends NPC2 implements Interactable, Resetable {
	byte state=0;

	public Alchemist() {
		String[] ts2s = {
			"I invoke the mystical forces of evil to cure my acne!",
			"I curse this land with eternal darkness!... after the parade..",
			"Damn, I need some silver coins."
			};

		init("Alchemist",44,17,4,ts2s,6,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah;
		if (state==0) {
			blah = new String[2];
			blah[0] = "1. You look troubled, what's the matter?";
			blah[1] = "2. Can I have that large stone of yours;   over there?";
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
					retval="Woman troubles. I want to cast a love spell on Succubus, but I don't have enough stinky fish for the potion to work. I need fifteen, but I only have five.";
					break;
				case 2:
					int fishcount=-1;
					switch (c.inhand) {
						case 0:
							fishcount=0;
							break;
						case 10:
							fishcount=1;
							break;
						default:
							retval="Well how can I give it to you if your hands are full?";
					}

					if (fishcount!=-1) {
						for (int i=0; i < c.inventory.length&&fishcount<10; i++) {
							if (c.inventory[i]==10)
								fishcount++;
						}

						if (fishcount==10) {
							if (c.inhand==10) {
								c.setHands(0);
								fishcount=9;
							}
							for (int i=0;i<c.inventory.length&&fishcount>0;i++) {
								if (c.inventory[i]==10) {
									fishcount--;
									c.inventory[i]=0;
									c.pSend("i"+((char)(i+32))+"  ");
								}
							}

							retval="Oh. Yes. You have the fish! Here, take the stone.";
							c.map.placeItemAt(231,43,17,c.map);
							state=1;
							c.setHands(232);
						}	
						else
							retval="Well.. I suppose I could part with it if you could get me 10 stinky fish. I really need them! *twitch*";
					}
			}
			if (retval!=null)
				c.pSend("("+name+": "+retval);
		}
	}
	public void reset() {
		state=0;
		server.maps[6].placeItemAt(230,43,17,server.maps[6]);
	}
}