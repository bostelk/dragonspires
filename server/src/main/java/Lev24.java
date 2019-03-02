public class Lev24 extends DSMapServer {
	int guards=0,scorps=0;

	public Lev24() {
		mapname = "lev24";
		init("lev24.dsmap",this);

		mapnumber = 23;
		xstart = 2;
		ystart = 88;
		diemap = 23;
		walktrig=true;
	}
	public void walkTrigger(DSpiresSocket c) {
		int x = c.cx-32;
		int y = c.cy-32;
		if (x == 15 && y == 64)
			stepOnBlue(c);
		else if (x == 20 && y == 73)
			stepOnBronze(c);
		else {
			switch (tilemap[x][y]) {
				case 4:
				case 41:
					if (parent.PointinRect(x,y,15,54,25,74)) {
						if ((tilemap[x][y]==41&&c.colorstring.charAt(0)!='*') || (tilemap[x][y]==4&&c.colorstring.charAt(0)!='%')) {
							c.pSend("(You've either stepped on the other team's side or you haven't chosen Blue or Bronze as your team. Bad dog!");
							switch (c.inhand) {
								case 175:
								case 176:
								case 177:
									if (c.map.itemmap[x][y]==0)
										c.map.placeItemAt(c.inhand,x,y,c.map);
									else
										randomDodgeballPlace(c.inhand);

									c.setHands(0);											
							}
							c.movePlayerIntoStartArea();
						}
					}						
			}
		}
	}
	public void stepOnBlue(DSpiresSocket c) {
		if (c.colorstring.charAt(3)==' ')
			return;

		if (c.facing == 1) {
			c.colorstring = c.ocolorstring;
			c.pSend("(You leave The Royal Guards.");
			switch (c.inhand) {
				case 175:
				case 176:
				case 177:
					randomDodgeballPlace(c.inhand);
					c.setHands(0);
			}
			/*for (int i=0;i<c.inventory.length;i++) {
				switch (c.inventory[i]) {
					case 175:
					case 176:
					case 177:
						randomDodgeballPlace(c.inventory[i]);
						c.inventory[i]=0;
						c.pSend("i"+parent.toDSChar(i)+"  ");
				}
			}*/
		}
		else {
			c.colorstring = "%"+c.colorstring.substring(1);
			c.map.playerPlaceBroadcast(c.visishape,c.colorstring,c.cx,c.cy,c,c.map);
			c.pSend("(You join The Royal Guards.");
		}
	}
	public void stepOnBronze(DSpiresSocket c) {
		if (c.colorstring.charAt(3)==' ')
			return;

		if (c.facing == 1) {
			c.colorstring = c.ocolorstring;
			c.pSend("(You leave The Scorpions.");
			switch (c.inhand) {
				case 175:
				case 176:
				case 177:
					randomDodgeballPlace(c.inhand);
					c.setHands(0);	
			}
			/*for (int i=0;i<c.inventory.length;i++) {
				switch (c.inventory[i]) {
					case 175:
					case 176:
					case 177:
						randomDodgeballPlace(c.inventory[i]);
						c.inventory[i]=0;
						c.pSend("i"+parent.toDSChar(i)+"  ");
				}
			}*/
		}
		else {
			c.colorstring = "*"+c.colorstring.substring(1);
			c.map.playerPlaceBroadcast(c.visishape,c.colorstring,c.cx,c.cy,c,c.map);
			c.pSend("(You join The Scorpions.");
		}
	}
	public void randomDodgeballPlace(int ball) {
		//15,54,25,74
		//0,0,10,20
		int x, y;

		do {
			x = parent.dice(1,10)+15;
			y = parent.dice(1,20)+54;
		}
		while (!canWalk(x,y,this) || !(tilemap[x][y]==41||tilemap[x][y]==4) || !(itemmap[x][y]==0));

		placeItemAt(ball,x,y,this);
	}
	public void addScore(char team) {
		switch (team) {
			case '%':
				guards++;
				break;
			case '*':
				scorps++;
		}
		broadcast("[\'Royal Guards="+guards+" | Scorpions="+scorps, this);
		if (guards==5)
			broadcast("[&The Royal Guards win!",this);
		else if (scorps==5)
			broadcast("[#The Scorpions win!",this);
		else return;

		scorps=0;
		guards=0;
		broadcast("[\'New game started.",this);
	}
}
