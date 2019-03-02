public class Batrade extends NPC3 implements Interactable {

	public Batrade(NPC3Thread p, DSMapServer m) {
		map=m;
		init("Batrade","%(%\"",37,32,1,3,p);
		talk = 0;
		attributes|=NPC3_INTERACT;
		attributes|=NPC3_HIT;
	}
	public void trigger() {
		if (++talk==7) {
			int say = (int)Math.round(Math.random() * 3);
			switch (say) {
				case 0:	say("Another round, barkeep!");
						break;
				case 1:	say("Who's up for a drinking song? Anyone?? Ahh, you're no fun.");
						break;
				case 3:	send("("+name+" laughs heartily and takes a swig of her drink.");
			}
			talk = 0;
		}

		move(parent.parent.shortfaceconv[(int)Math.round(Math.random() * 3)]);
	}
	public void hit(DSpiresSocket c) {
		send("("+name+" nearly drops her drink. Batrade: Hey! You want me to crack your head, "+c.name+"?");
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] opts = {"Batrade is a largy, burly woman. Even in her;full body armor you can tell that she is thick;and muscle-bound. She is currently chugging;some kind of alcoholic beverage.;What do you want to do?; ","1. Who are you?","2. Can I buy you a drink?","3. I admire a stong woman.","4. Kiss Batrade."};
		return opts;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("("+name+": Who do I look like? I'm the baddest Valkyre this side of the other side of.. whatever it is that we're on one side of.");
				break;
			case 2:
				c.pSend("("+name+" puts her large arm around you, drags you to the bar, and makes you buy her a bottle of the most expensive ale!!");
				c.updateGold(-25);
				break;
			case 3:
				c.pSend("("+name+" slaps you on the back and laughs out loud.\n("+name+": Bwahaha! Never heard THAT one before, slim!");
				break;
			case 4:
				c.pSend("(As you lean in to kiss Batrade full on the lips, she makes a funny face and pushes you away with one hand. You fall backward onto the floor.\n("+name+": Hey! Who gave you permission to defile THESE lips?!");
		}
	}
}
