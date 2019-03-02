public class Barkeep extends NPC2 implements Interactable {
	public Barkeep() {
		String[] ts2s = {
			"w00t",
			"What can I get for you today? Press Ctrl-S to shop.",
			"We've got the finest ale in the city!",
			"What I really want to do is act.",
			"Gin and tonic, gin and tonic.. What goes into that again??",
			"I used to have a twin brother, I wonder what ever happened to him..",
			"Green Tonic: The Drink that Refreshes!",
			"I hear that Billy's Butcher Shop down east past the river pays good money for snails.",
			"If you don't like the way you look, you should take a friend and head to the Magic Color Change Shop.",
			"Did you know that there's Dodgeball and Capture the Flag here? Yeah! Just go to Killoseum and take the portals.",
			"I can't wait for the next parade! They're great for business.",
			"Feeling a little beat up? Get some healing items at Herb's Herbs'N'More, just down the eastern street.",
			"My pal Simon's got some pretty good swords at his shop. It's just just west of Razorhawk's Temple behind me."
			};

		init("Barkeep",19,36,3,ts2s,0,this);
	}
	public String[] getOptions(DSpiresSocket c) {
		String[] blah = {
			"1. Say: Blah"
			};
		return blah;
	}
	public void interact(int opt, DSpiresSocket c) {
		c.pSend("("+name+": BAAAAAAAA. Heh. I'm no help at all.");
	}
}