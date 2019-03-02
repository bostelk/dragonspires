public class Kaylamar extends NPC3 implements Interactable {

	String thestring;

	public Kaylamar(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Kaylamar","#!(%",26,76,1,4,p);
		attributes|=NPC3_INTERACT;
		thestring="[\'"+name+" chants softly then kicks you in the butt!";
	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random() * 2);
			switch (say) {
				case 0:	say("There's a glass ceiling, I tell you. I get stuck all the way out here while that pig Kalamar gets to work the main town.");
						break;
				case 1:	say("Where you wanna go? I can take you there.");
						break;
				case 2:	send("("+name+"'s beaming confidence makes you feel she is extremely arrogant if you are male and instills feelings of pride if you are female.");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"Kaylamar is a travelling Wizard.",
				"She asks what she can help you with...",
				"1. Restore my magic points!",
				"Teleport me to...",
				"2. Main (5000 gold)",
				"3. Desert Peninsula (100000)",
				"4. Ye Olde Hotel (4000)",
				"5. The Swamp (20000)",
				"6. The Beach (5000000)"};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.mp=30;
				c.pSend("[\'"+name+" pokes your forehead and suddenly you feel the magical power within you restored.\n$M"+c.parent.toDSChar(c.mp));
				break;
			case 2:
				if (c.gold >= 5000) {
					c.pSend(thestring);
					c.updateGold(-5000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[0],0,0,0);
				}
				break;
			case 3:
				if (c.gold >= 100000) {
					c.pSend(thestring);
					c.updateGold(-100000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[25],0,0,0);
				}
				break;
			case 4:
				if (c.gold >= 4000) {
					c.pSend(thestring);
					c.updateGold(-4000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[2],0,0,0);
				}
				break;
			case 5:
				if (c.gold >= 20000) {
					c.pSend(thestring);
					c.updateGold(-20000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[7],0,0,0);
				}
				break;
			case 6:
				if (c.gold >= 5000000) {
					c.pSend(thestring);
					c.updateGold(-5000000);
					//c.doCheckBeforeMove(c);
					c.changeMap(map.parent.maps[20],0,0,0);
				}
				break;
		}
	}
}
