public class SirJ extends NPC2 implements Interactable, Givetoable {

	public SirJ() {
		String[] ts2s = {"Shoochith Boochieth!"};
		init("Sir J.",42,48,9,ts2s,32,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah={"You see a tall, skinny man in a long coat. He is;wearing;a metal skullcap with small horns,;backwards, on his head.; ",
						"1. Ask: Who are you?",
						"2. Ask: What's with all the herbs?",
						"3. Poke Sir Jay in the eye."};
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		switch (opt) {
			case 1:
				c.pSend("("+name+": I'm Sir J., This fatass next to me is Silent Olaf.");
				break;
			case 2:
				c.pSend("("+name+": Hey, they're medicinal! What are you, a Royal Guard or something? Stop hassling me, man!!");
				break;
			case 3:
				c.pSend("(You poke Sir J. in the eye. He blinks a few times. Don't abuse Herbs, kids!");
		}
	}
	public void get(DSpiresSocket c) {
		if (c.inhand==47) {
			c.pSend("(Sir J. looks around to see if anyone's watching and takes the herb. He tastes it and puts it in his pocket.\n(Sir J. gives you 70 gold!");
			c.setHands(0);
			c.updateGold(70);
		}
		else
			c.pSend("(Sir J.: Wts?");
	}
}