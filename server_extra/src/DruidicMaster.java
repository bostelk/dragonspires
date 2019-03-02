public class DruidicMaster extends NPC3 implements Interactable, Resetable {

	byte state=0;

	public DruidicMaster(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Druidic Master",",**%",18,13,1,3,p);
		talk = 0;
		attributes|=NPC3_INTERACT;
	}
	public void trigger() {
		if (++talk==8) {
			int say = (int)Math.round(Math.random() * 2);
			switch (say) {
				case 0:	say("We don't get many visitors at the temple these days. Unless you count looters.");
						break;
				case 1:	say("Not many people realize I've switched the real scrolls with a few reproductions.");
						break;
				case 2:	say("If the real power of the ten scrolls is realized.. we druids can kiss our staffs goodbye.");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public String[] getOptions(DSpiresSocket c) {
		if (state==0) {
			String[] opts = new String[3];
			opts[0] = "1. You look tired.";
			opts[1] = "2. How much magic does a druidic master like you need?";
			opts[2] = "3. I have some berries for you!";
			return opts;
		}
		return (new String[0]);
	}
	public void interact(int opt, DSpiresSocket c) {
		String retval=null;
		if (state==0) {
			switch (opt) {
				case 1:
					c.pSend("("+name+": Yes, fending off looters all day takes all my strength. I wish I had thought to stock up on some more violet berries.");
					break;
				case 2:
					c.pSend("("+name+": Oh, about five berries worth to get back to my old self.");
					break;
				case 3:
					int berrycount=-1;
					switch (c.inhand) {
						case 0:
							berrycount=0;
							break;
						case 256:
							berrycount=1;
							break;
						default:
							retval="What's that in your hands? It doesn't look like a berry.";
					}

					if (berrycount!=-1) {
						for (int i=0; i < c.inventory.length&&berrycount<5; i++) {
							if (c.inventory[i]==256)
								berrycount++;
						}

						if (berrycount==5) {
							if (c.inhand==256) {
								c.setHands(0);
								berrycount=4;
							}
							for (int i=0;i<c.inventory.length&&berrycount>0;i++) {
								if (c.inventory[i]==256) {
									berrycount--;
									c.inventory[i]=0;
									c.pSend("i"+((char)(i+32))+"  ");
								}
							}

							retval="Ah! Thank you! Here, have this pink scroll, I can heal wounds on my own now thanks to you.";
							state=1;
							c.setHands(100);
						}	
						else
							retval="No you don't.";
					}
			}
		}
		if (retval!=null)
			c.pSend("("+name+": "+retval);
	}
	public void reset() {
		state=0;
	}
}
