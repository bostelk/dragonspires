/*

Java DragonSpires Client
Copyright (c) 1997-2001, Adam Maloy
All rights reserved.

LICENSE (BSD, revised)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
/*
   Adam Maloy (Mech) would like to give special thanks to: 
          Dr. Cat and 'Manda (Dragon's Eye Productions),
          C.H. Wolf (Motorhed),
          the players and persistant fans of DragonSpires,
          and everyone else who has supported the development of DragonSpires.
*/

import java.awt.*;
import java.net.URL;
import java.io.InputStream;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.CropImageFilter;

public class DSMapCanvas extends Canvas {

	DragonSpiresPanel parent;

	Image buffImg,tileImg;

	int xpos,ypos,isrepaint,currx, curry, xs, xe, ys, ye,oe,tilecount,x,y,
		inshop = -1,
		drawok = -1,
		drawTiles = 1,
		nitems=0,
		omdisplaymode=0,
		dirtycount=0,myass,
		downloadx,downloady,decount=0;

	final short mwidth = 52,
		  mheight = 100;

	private short[][] itemmap;
	private byte[][] tilemap,playermap;
	short[][] spellmap;
	int [][] shopitems;
	/*final byte colorpal[][] = {{0,0,55},{0,0,(byte)175},{75,(byte)127,43},{83,83,119},
			{(byte)175,0,0},
			{(byte)147,0,(byte)147},
			{(byte)203,(byte)147,79},
			{(byte)179,(byte)207,(byte)235},
			{(byte)55,(byte)55,(byte)83},
			{(byte)103,(byte)103,(byte)219},
			{(byte)107,(byte)167,(byte)19},
			{(byte)99,(byte)147,(byte)159},			{(byte)215,(byte)99,(byte)99},
			{(byte)191,(byte)71,(byte)207},
			{(byte)231,(byte)219,(byte)79},
			{(byte)255,(byte)239,(byte)235}};

*/
	final short enecoords[] = {
1,1,23,17,
25,1,23,17,
49,1,24,16,
74,1,23,16,
98,1,23,17,
122,1,23,17,
146,1,24,16,
171,1,23,16,
195,1,25,17,
221,1,25,17,
247,1,24,17,
272,1,25,17,
298,1,23,17,
322,1,23,17,
346,1,23,17,
370,1,23,17,
1,19,41,35,
43,19,44,44,
88,19,47,36,
136,19,41,45,
178,19,37,36,
216,19,40,31,
257,19,44,37,
302,19,41,32,
344,19,30,56,
1,65,30,57,
32,65,32,56,
65,65,32,57,
98,65,28,54,
127,65,28,54,
156,65,32,56,
189,65,33,56,
223,65,23,51,
247,65,26,53,
274,65,23,50,
298,65,28,53,
327,76,25,52,
353,76,25,53,
1,130,28,53,
30,130,24,54,
55,130,47,49,
103,130,50,52,
154,130,37,50,
192,130,38,52,
231,130,46,52,
278,130,48,52,
327,130,38,52,
1,185,36,52,
38,185,38,56,
77,185,42,55,
120,185,38,56,
159,185,44,55,
204,185,40,56,
245,185,43,52,
289,185,39,56,
329,185,44,52,
1,242,43,55,
45,242,39,56,
85,242,42,55,
128,242,42,55,
171,242,42,54,
214,242,40,55,
255,242,43,55,
299,242,43,55,
343,242,40,58,
1,301,39,57,
41,301,39,57,
81,301,40,58,
122,301,40,58,
163,301,39,57,
203,301,40,57,
244,301,41,58,
286,301,34,50,
321,301,32,53,
354,301,35,52,
1,360,40,51,
42,360,29,51,
72,360,31,55,
104,360,32,56,
137,360,33,52,
171,360,44,55,
216,360,38,56,
255,360,38,57,
294,360,38,55,
333,360,38,55,
1,418,37,55,
39,418,46,55,
86,418,39,55,
126,418,42,56,
169,418,42,56,
212,418,41,56,
254,418,41,56,
296,418,42,56,
339,418,42,56,
1,475,40,56,
42,475,40,56,
83,475,28,56,
112,475,24,56,
137,475,25,56,
163,475,28,56,
192,475,28,56,
221,475,26,56,
248,475,26,56,
275,475,27,55,
303,475,42,55,
346,475,37,55,
1,532,40,55,
42,532,40,55,
83,532,40,54,
124,532,38,55,
163,532,41,55,
205,532,41,55,
247,532,30,54,
278,532,29,55,
308,532,29,55,
338,532,29,54,
368,532,28,53,
1,588,25,54,
27,588,25,54,
53,588,26,53,
80,588,29,59,
110,588,31,58,
142,588,27,59,
170,588,31,58,
202,588,31,59,
234,588,33,58,
268,588,28,59,
297,588,33,58,
331,588,31,59,
363,588,34,59,
1,648,31,59,
33,648,33,59,
67,648,35,59,
103,648,33,59,
137,648,35,59,
173,648,33,59,
207,648,29,52,
237,648,28,55,
266,648,29,52,
296,648,28,55,
325,648,30,53,
356,648,29,55,
1,708,30,53,
32,708,29,55,
62,708,37,58,
100,708,34,58,
135,708,37,58,
173,708,34,58,
208,708,36,57,
245,708,39,57,
285,708,36,57,
322,708,39,57,
362,708,31,55,
1,767,28,55,
30,767,31,55,
62,767,28,55,
91,767,30,56,
122,767,29,56,
152,767,30,56,
183,767,29,56,
213,767,28,60,
242,767,28,59,
271,767,28,59,
300,767,28,59,
329,767,28,59,
358,767,28,59,
1,823,28,59,
30,823,28,59
	};

	int[] tempmap;
	Graphics buffImgG,tileImgG,g;
	Image[] floors,items;
	ImageProducer[] player;
	ImageProducer itemsrc,flrsrc,enesrc;
	String[][] colorstrings;
	String buffer_string;
	String[] buffer_string_array;

	//map ints;
	final byte colorindexes[] = {0,3,8 //black
				,8,9,1 // darkblue
				,1,7,9 //blue
				,3,10,2 //olive
				,2,14,10 //lime
				,8,9,3 //grey
				,8,12,4 //deep red
				,4,6,12 //light red
				,8,13,5 //purple
				,5,7,13 //lavender
				,12,14,6 //bronze
				,6,15,14 //gold
				,9,15,7 //silver
				,7,15,15 //white
				};
	Hashtable playercache;

	boolean showstats = false,indentline,drawcompass = true;

	byte itall[]=null;
	final byte ptall[]={ 0, 32,32,32,32,32, 32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32,32};
	final byte ppush[]={15,  0, 0, 0, 0, 0,  9,15,17, 6, 8, 0,12,13, 8, 0,16, 2,14,10,11,16,11,14,12};

	short itemcoords[]=null;
	short justmoved=3;

	Color[] scolors = {Color.black,Color.yellow};

	public DSMapCanvas(DragonSpiresPanel p) {
		setBackground(new Color(179,207,235));
		tilemap = new byte[mwidth][mheight];
		itemmap = new short[mwidth][mheight];
		playermap = new byte[mwidth][mheight];
		spellmap = new short[mwidth][mheight];
		tempmap = new int[132];

		/*for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				tilemap[x][y] = 0;
				itemmap[x][y] = 0;
				playermap[x][y] = 0;
			}
		}*/
		parent = p;

		floors = new Image[90];

		shopitems = new int[7][3];

		playercache = new Hashtable();
		colorstrings = new String[mwidth][mheight];

	}
	//public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		//System.out.print("!"+flags+"!");

	//	if (flags == 32)
	//			repaint();
		/*switch (flags) {
			case 128:
				System.out.println("imageUpdate ABORT flag");
				break;
			case 64:
				System.out.println("imageUpdate ERROR flag");
				break;
			case 32:*/
		//}
	//	return true;
	//}

//OLD PAINT
/*	public void repaint() {
		//try {
		if (drawok == 1 && isrepaint == 0) {

			//System.out.println("Pstart");
			isrepaint = 1;
			//g = getGraphics();

			xs = xpos-3;
			ys = ypos-8;

			xe = xpos+3;
			ye = ypos+8;

			oe = ypos%2;

			if (drawTiles == 1)
				drawTileGraphics();

			buffImgG.drawImage(tileImg,0,0,this);
			curry = -16;
			indentline = false;

			if (oe==0) {
				for (int y = ys; y <= ye; y++) {
					currx = ((indentline) ? 1 : -31);
					for (int x = xs; x <= xe; x++) {
						if (x==xs && Math.abs(y%2)==1)
							x++;
						try {
							if (itemmap[x][y] != 0) {
								if (itemmap[x][y] < items.length)
									buffImgG.drawImage(getItem(itemmap[x][y]),currx,curry-itall[itemmap[x][y]],this);
							}
							if (playermap[x][y] != 0) {
								int t = colorstrings[x][y].charAt(3)-32;
								buffImgG.drawImage( ((PICO)playercache.get(colorstrings[x][y])) .images[playermap[x][y]-1],currx+ppush[t],curry-ptall[t],this);
								//drawPlayer(playermap[x][y]-1,colorstrings[x][y]);
							}
						}
						catch (Exception e) {
							if (e instanceof ArrayIndexOutOfBoundsException) {
								int tallness = 32;
								if (y == mheight || y == mheight+1)
									tallness = 0;
								buffImgG.drawImage(parent.end,currx,curry-tallness,this);
								//buffImgG.drawImage(floors[0],currx,curry,this);
							}
						}
						//if (parent.spells.size() > 0)
						//	drawSpells(x,y);
						currx+=64;
					}
					curry+=16;
					indentline = !indentline;
				}
			}
			else {
				for (int y = ys; y <= ye; y++) {
					currx = ((indentline) ? 1 : -31);
					for (int x = xs; x <= xe; x++) {
						if (x == xe && y%2 == 0)
							break;
						try {
							if (itemmap[x][y] != 0) {
								if (itemmap[x][y] < items.length)
									buffImgG.drawImage(getItem(itemmap[x][y]),currx,curry-itall[itemmap[x][y]],this);
							}
							if (playermap[x][y] != 0) {
								int t = colorstrings[x][y].charAt(3)-32;
								buffImgG.drawImage( ((PICO)playercache.get(colorstrings[x][y])) .images[playermap[x][y]-1],currx+ppush[t],curry-ptall[t],this);
								//drawPlayer(playermap[x][y]-1,colorstrings[x][y]);
							}
						}
						catch (Exception e) {
							if (e instanceof ArrayIndexOutOfBoundsException) {
								int tallness = 32;
								if (y == mheight || y == mheight+1)
									tallness = 0;
								buffImgG.drawImage(parent.end,currx,curry-tallness,this);
								//buffImgG.drawImage(floors[0],currx,curry,this);
							}
						}
						//if (parent.spells.size() > 0)
						//	drawSpells(x,y);
						currx+=64;
					}
					curry+=16;
					indentline = !indentline;
				}
			}
			//buffImgG.drawImage(parent.night,0,0,this);
			
			if (omdisplaymode!=0)
				drawOMDisplay(buffImgG,false);

			if (drawcompass)
				buffImgG.drawImage(parent.compass,333,1,this);

			g.drawImage(buffImg,0,0,this);

			isrepaint = 0;
		}
		//}
		//catch(Exception e) {
		//	e.printStackTrace();
		//}
		//System.out.print("#repaint#");
			//System.out.println("Pend");
	}
	public void drawTileGraphics() {
		tilecount = 0;

		curry = -16;
		indentline = false;

		if (oe==0) {
		for (int y = ys; y <= ye; y++) {

			currx = ((indentline) ? 1 : -31);

			for (int x = xs; x <= xe; x++) {


					if (x==xs && Math.abs(y%2)==1)
						x++;

				try {
					if (tilemap[x][y] != tempmap[tilecount]) {
						if (floors[tilemap[x][y]] == null)
							fetchFloorImg(tilemap[x][y]);

						tileImgG.drawImage(floors[tilemap[x][y]],currx,curry,this);
						tempmap[tilecount] = tilemap[x][y];
					}
				}
				catch (Exception e) {
					//if (tilecount <= 111)
						tempmap[tilecount] = -1;
				}
				currx+=64;
				tilecount++;
			}
			curry+=16;
			indentline = !indentline;
		}

		}
		else {
		for (int y = ys; y <= ye; y++) {

			currx = ((indentline) ? 1 : -31);

			for (int x = xs; x <= xe; x++) {


				if (x == xe && y%2 == 0)
						break;

				try {
					if (tilemap[x][y] != tempmap[tilecount]) {
						if (floors[tilemap[x][y]] == null)
							fetchFloorImg(tilemap[x][y]);

						tileImgG.drawImage(floors[tilemap[x][y]],currx,curry,this);
						tempmap[tilecount] = tilemap[x][y];
					}
				}
				catch (Exception e) {
					//if (tilecount <= 111)
						tempmap[tilecount] = -1;
				}
				currx+=64;
				tilecount++;
			}
			curry+=16;
			indentline = !indentline;
		}
		}
		drawTiles = 0;
	}*/
	/*public void drawPlayer(int shape, String colorstring) {
		if (colorstring.charAt(3) ==' ')
			buffImgG.drawImage(playerextra[shape],currx+15,curry,this);
		else if (colorstring.charAt(3) >= '&')
			buffImgG.drawImage(playerextra[16+((colorstring.charAt(3)-38)*12)+shape],currx+15,curry-32,this);
		else {
			if (playercache.containsKey(colorstring))
				//buffImgG.drawImage(((Image[])(((Vector)playercache.get(colorstring)).elementAt(0)))[shape],currx,curry-32,this);
				buffImgG.drawImage( ((PICO)playercache.get(colorstring)) .images[shape],currx,curry-32,this);

			//buffImgG.drawImage(createImage(new FilteredImageSource(player[playermap[x][y]-1],new KnightFilter(colorstrings[x][y].toCharArray(),this))),currx,curry-32,this);
			//buffImgG.drawImage(createImage(parent.ips[15+playermap[x][y]]),currx,curry-32,this);
		}
	}*/
	/*public void drawSpells() {//int x, int y) {
		try {
		Spell tspell;
		for (int i = 0; i < parent.spells.size(); i++) {
			//tspell = parent.spells.elementAt(i).toString();
			tspell = (Spell)parent.spells.elementAt(i);
			//if (tspell.charAt(0)-32 == x) {
				//if (tspell.charAt(1)-32 == y) {
			if (tspell.x == x) {
				if (tspell.y == y) {
					//buffImgG.drawImage(spells[tspell.charAt(2)-32],currx,curry-32,this);
					buffImgG.drawImage(spells[tspell.spell],currx,curry-32,this);
					break;
				}
			}
		}
		}
		catch (Exception e) {
		}
	}*/
	public void drawShop(Graphics g) {
		//Color[] scolors = new Color[2];
		//scolors[0] = Color.gray;
		//scolors[1] = Color.black;
		g.drawImage(parent.wood,5,5,this);
		g.setFont(parent.fs);
		if (inshop == 0) {
			for (int n = 0; n < 2; n++) {
				g.setColor(scolors[n]);
				g.drawLine(7-n,23-n,254-n,23-n);
				g.drawString(buffer_string,10-n,20-n);
				g.drawString("1. Buy",10-n,38-n);
				g.drawString("2. Sell",10-n,58-n);
				g.drawString("Type the number of your selection.",10-n,88-n);
			}
		}
		else {
			for (int n = 0; n < 2; n++) {
				g.setColor(scolors[n]);
				g.drawLine(7-n,23-n,254-n,23-n);
				if (inshop == 1) {
					g.drawString("Item to Buy",10-n,20-n);
					g.drawString("Cost",188-n,20-n);
				}
				else {
					g.drawString("Item to Sell",10-n,20-n);
					g.drawString("Value",185-n,20-n);
				}
				g.drawLine(178-n,10-n,178-n,152-n);
				g.drawLine(222-n,10-n,222-n,152-n);
			}
			try {
				for (int i = 0; i < 7; i++) {
					if (shopitems[i][0] > -1) {
						g.drawImage(getItem(shopitems[i][0]),5,20+(20*i),this);
						g.drawImage(getItem(shopitems[i][1]),170,20+(20*i),this);
						for (int n = 0; n < 2; n++) {
							g.setColor(scolors[n]);
							g.drawString((i+1)+".",10-n,38+(20*i)-n);
							g.drawString(""+shopitems[i][2],182-n,43+(20*i)-n);
							String tname = parent.itemnames[shopitems[i][0]];//.substring(0,parent.itemnames[shopitems[i][0]].length()-1);
							if (tname.startsWith("a"))
								tname = tname.substring(tname.indexOf(" ")+1,tname.length());
							g.drawString(tname,55-n,38+(20*i)-n);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		g.setColor(Color.black);
		g.drawString("Press Ctrl-S to close",79,158);
		g.setColor(Color.green);
		g.drawString("Press Ctrl-S to close",78,157);
	}
	public void drawStats(Graphics g) {
		//Color[] scolors = new Color[2];
		//scolors[0] = Color.gray;
		//scolors[1] = Color.black;
		g.drawImage(parent.wood,5,5,this);
		g.setFont(parent.fs);
		for (int n = 0; n < 2; n++) {
			g.setColor(scolors[n]);
			g.drawLine(7-n,23-n,254-n,23-n);
			g.drawString("Your Statistics",10-n,20-n);
			g.drawString("[ "+parent.name+" ]",(132-parent.fms.stringWidth("[ "+parent.name+" ]")/2)-n,38-n);

			g.drawString("HP:",50-n,58-n);
			g.drawString("MP:",48-n,78-n);
			g.drawString("Stamina:",21-n,98-n);
			g.drawString("Gold:",41-n,118-n);
			g.drawString("Alignment:",10-n,138-n);

			g.drawString(parent.hp+"/"+parent.maxhp,75-n,58-n);
			g.drawString(parent.mp+"/"+parent.maxmp,75-n,78-n);
			g.drawString((int)(((float)parent.stam/(float)116)*100)+"%",75-n,98-n);
			g.drawString(parent.gold+"",75-n,118-n);
			g.drawString(parent.aligntexts[parent.alignment],75-n,138-n);
			

			g.drawString("* Weapon",145-n,58-n);
			g.drawString("["+parent.itemnames[parent.weapon]+"]",145-n,72-n);
			g.drawString("Damage: "+parent.wm+" - "+parent.wx,145-n,86-n);

			g.drawString("* Armor",145-n,110-n);
			g.drawString("["+parent.itemnames[parent.armour]+"]",145-n,124-n);
			g.drawString("Protect: "+parent.am+" - "+parent.ax,145-n,138-n);

			//g.drawString(parent.str+"",230-n,58-n);
			//g.drawString(parent.def+"",230-n,78-n);
			//g.drawString(parent.agi+"",230-n,98-n);
			//g.drawString(parent.dex+"",230-n,118-n);
			//g.drawString(parent.wiz+"",230-n,138-n);

			//g.drawString("Alignment:",10-n,58-n);
			//g.drawString("HP:",10-n,78-n);
			//g.drawString("Stamina:",10-n,98-n);

		}
	}
	public void paint(Graphics g) {
		try {
			//drawImage(buffImg,0,0,this);
		//if (!parent.movemouseclick)
			repaint();	
		//repaint(-99,-99);
		}
		catch (NullPointerException e) {}
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void initMap() {
		g.drawString("2 - Clearing buffers...",5,55);
		if (buffImg == null) {
			//buffImg = this.createImage(500,300);
			buffImg = this.createImage(384,256);
			buffImgG = buffImg.getGraphics();
			//tileImg = this.createImage(500,300);
			tileImg = this.createImage(384,256);
			tileImgG = tileImg.getGraphics();
			//dirtyBitch = this.createImage(62,96);
			//dBG = dirtyBitch.getGraphics();
			//drawok = 1;
			//g = getGraphics();
		}		
		clearPlayerMap();
		for (int i = 0; i < tempmap.length; i++) {
			tempmap[i] = -1;
		}
		drawTiles = 1;
		g.drawString("1 - Assembling map... (this may take a moment)",5,75);
		isrepaint = 0;
		omdisplaymode=99;
		new Patience(this);
	}
	public void getMapData(String mapname) {
		try {
			//int head,tack;
			parent.append("* Loading map. Please wait. *",6);
			//if (mapname.equals("download.dsmap"))
			//	g.drawString("4 - Downloading map from server...",5,15);
			//else
				g.drawString("4 - Reading map...",5,15);
			//InputStream i = new URL(parent.path+mapname).openConnection().getInputStream();
			InputStream i = parent.dsOpenFile(mapname);
			//System.out.println(theurl.toString());
			g.drawString("3 - Decoding map data...",5,35);
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					tilemap[x][y]=(byte)decode(i.read(),i.read());
					//tilemap[x][y] = i.read();
					//System.out.print(""+tilemap[x][y]);
					//i.skip(1);
				}
			}
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					itemmap[x][y]=(short)decode(i.read(),i.read());
					//itemmap[x][y] = i.read();
					//i.skip(1);
				}
			}
			//if (!mapname.equals("download.dsmap"))
			//	i.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void downloadMapData(String data) {
		try {
			int datalen=data.length();
			char carray[] = data.toCharArray();

			switch (parent.downloading) {
				case 1:
					for (int i = 0; i < datalen; i+=2) {
						tilemap[downloadx][downloady]=(byte)decode(carray[i]-32,carray[i+1]-32);
						downloady++;
						if (downloady==mheight) {
							downloady=0;
							downloadx++;
							if (downloadx==mwidth) {
								parent.downloading=2;
								downloadx=0;
								downloady=0;
								downloadMapData(data.substring(i+2));
								return;
							}
						}
					}
					break;
				case 2:
					for (int i = 0; i < datalen; i+=2) {
						itemmap[downloadx][downloady]=(short)decode(carray[i]-32,carray[i+1]-32);
						downloady++;
						if (downloady==mheight) {
							downloady=0;
							downloadx++;
						}
					}
					
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int decode(int head, int tack) {
		return (head*95+tack);
	}
	public void createCacheEntry(String colorstring) {
		try {
		char[] cs = colorstring.toCharArray();
		Image[] retimg=null;

		if (cs[3]==' ') {
			int t;
			retimg = new Image[16];
			for (int i=0;i<16;i++) {
				t=i*4;
				retimg[i]=doImageCrop(enesrc,enecoords[t],enecoords[t+1],enecoords[t+2],enecoords[t+3]);
			}
		}
		else if (cs[3] < '&') {
			retimg = new Image[22];
			for (int i = 0; i < 22; i++) {
				retimg[i] = parent.getRemap(cs,player[(cs[3]-33)*22+i]);
			}
		}
		else {
			int t=64+((cs[3]-'&')*32);
			retimg = new Image[12];
			for (int i=0;i<12;i++) {
				switch (i) {
					case 1:
					case 4:
					case 7:
					case 10:
						continue;
				}
				retimg[i]=doImageCrop(enesrc,enecoords[t],enecoords[t+1],enecoords[t+2],enecoords[t+3]);
				t+=4;
			}
		}

		PICO pico = new PICO(retimg);
		playercache.put(colorstring, pico);//newentry);

		/*MediaTracker mt = new MediaTracker(this);
		for (int i=0;i<retimg.length;i++)
			mt.addImage(retimg[i],0);
		try {
			mt.waitForAll();
		}
		catch (Exception e) {}*/

		//System.out.println("Added. "+playercache.size());
		//Runtime r = Runtime.getRuntime();
		//System.out.println(""+((float)((float)r.freeMemory()/(float)r.totalMemory()))*100);
		}
		catch (Exception e) {}
	}
	public void removeCacheEntry(String colorstring) {
		//for (int i = 0; i < playercache.size(); i++) {
		//	if (((Vector)playercache.elementAt(i)).elementAt(0).toString().equals(colorstring)) {
		//		playercache.removeElementAt(i);
		//		break;
		//	}
		//}
		//for (int ic = 0; ic < color   Still need to create checking.
		if (colorstring.equals(parent.colorstring)) return;

		playercache.remove(colorstring);
		System.gc();
		//System.out.println("Removed. "+playercache.size());
		//Runtime r = Runtime.getRuntime();
		//System.out.println(""+((float)((float)r.freeMemory()/(float)r.totalMemory()))*100);
	}
	public void fetchItemImg(int numnum) {
		numnum--;
		items[numnum] = doImageCrop(itemsrc,itemcoords[numnum*4],itemcoords[numnum*4+1],itemcoords[numnum*4+2],itemcoords[numnum*4+3]);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(items[numnum],0);
		try {
			mt.waitForID(0);
		}
		catch (Exception e) {}
	}
	public Image getItem2(int item) {
		if (items[item-1] == null)
			fetchItemImg2(item);
		return items[item-1];
	}
	public void fetchItemImg2(int numnum) {
		numnum--;
		items[numnum] = doImageCrop(itemsrc,itemcoords[numnum*4],itemcoords[numnum*4+1],itemcoords[numnum*4+2],itemcoords[numnum*4+3]);
	}
	public void fetchFloorImg(int numnum) {
		int[] tempo = getFloorCoords(numnum);
		floors[numnum] = doImageCrop(flrsrc,tempo[0],tempo[1],62,32);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(floors[numnum],0);
		try {
			mt.waitForID(0);
		}
		catch (Exception e) {}
	}
	public Image doImageCrop(ImageProducer ip, int x, int y, int w, int h) {
		return parent.toolkit.createImage(new FilteredImageSource(ip, new CropImageFilter(x,y,w,h)));
	}
	public Image getItem(int item) {
		if (items[item-1] == null)
			fetchItemImg(item);
		return items[item-1];
	}
	public Image getPlayerImage(int x, int y) {
		PICO p = ((PICO)playercache.get(colorstrings[x][y]));
		int sh = playermap[x][y]-1;
		if (p.notloaded[sh]) {
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(p.images[sh],0);
			try {
				mt.waitForID(0);
			}
			catch (Exception e) {}
			p.notloaded[sh]=false;
		}
		return p.images[sh];
	}
	public int[] getFloorCoords(int floor) {
		int[] retval = new int[2];
		int column = 0;
		int row = 0;
		for (int r = 1; r <= 10; r++) {
			for (int c = 1; c <= 9; c++) {
				if (floor == ((r - 1)*9)+(c-1)) {
					column = c;
					row = r;
					break;
				}
			}
		}
		retval[0] = column+((column-1)*62);
		retval[1] = row+((row-1)*32);
		return retval;
	}
	public void clearPlayerMap() {
		for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				playermap[x][y] = 0;
				colorstrings[x][y] = null;
				spellmap[x][y] = 0;
			}
		}
	}
	public void drawInv(Graphics g) {
		//Color[] scolors = new Color[2];
		//scolors[0] = Color.gray;
		//scolors[1] = Color.black;
		g.drawImage(parent.wood,5,5,this);
		g.setFont(parent.fs);
		for (int n = 0; n < 2; n++) {
			g.setColor(scolors[n]);
			g.drawLine(7-n,23-n,254-n,23-n);
			g.drawString("Inventory - Ctrl-V to close. Ctrl-C to swap.",10-n,20-n);
		}

		int x = -5;
		int y = 23;
		int drawx=0,drawy=0,drawi=0;

		for (int i = 0; i < parent.inventory.length; i+=5) {
			for (int n = 0; n < 5; n++) {
				if (i+n >= parent.inventory.length) break;
				if (i+n == parent.invsel) {
					drawx=x;
					drawy=y;
					drawi=i+n;
				}
				if (parent.inventory[i+n] > 0)
					g.drawImage(getItem2(parent.inventory[i+n]),x,y-itall[parent.inventory[i+n]],this);
				y+=25;
			}
			x+=35;
			y = 23;
		}

		g.setColor(Color.green);
		g.drawOval(drawx+16,drawy,28,28);
		//g.setColor(Color.red);
		//g.drawString(parent.itemnames[parent.inventory[i+n]],drawx+40,drawy+32);

		if (parent.inventory[drawi]!=0) {
			String thestring = "";
			if (parent.inventory[drawi]>nitems)
				thestring=parent.animnames[((parent.inventory[drawi]+1-nitems)-2)/8];
			else
				thestring=parent.itemnames[parent.inventory[drawi]];
			g.setFont(parent.fs);
			g.setColor(parent.dsbg2);
			g.fillRect(drawx+34,drawy+20,parent.fms.stringWidth(thestring)+10,15);
			g.setColor(Color.black);
			g.drawRect(drawx+34,drawy+20,parent.fms.stringWidth(thestring)+10,15);
			g.setColor(parent.dsbg);
			g.drawString(thestring,drawx+39,drawy+32);
		}
	}
	/*public boolean lookit() {
		int x = parent.nextx(xpos,ypos,parent.facing);
		int y = parent.nexty(xpos,ypos,parent.facing);

		int item = itemmap[x][y];

		if (playermap[x][y] != 0) item = 21;

		switch (item) {
			case 21:
			case 24:
			case 25:
			case 35:
			case 36:
			case 81:
			case 94:
			case 95:
			case 185:
			//NPCs
			case 61:
			case 63:
			case 64:
			case 65:
			case 66:
			case 67:
			case 68:
			case 69:
			case 75:
			case 93:
			case 122:
			case 123:
			case 125:
			case 171:
			case 184:
			case 189:
			case 205:
			case 221:
			case 222:
			case 227:
			case 228:
			case 229:
			case 234:
			case 235:
				return true;
			case 0:
				parent.append("You see "+parent.floornames[parent.floornamepointers[tilemap[x][y]]]+".",10);
				break;
			default:
				if (item < nitems)
					parent.append("You see "+parent.floornames[parent.floornamepointers[tilemap[x][y]]]+" and "+parent.itemnames[itemmap[x][y]]+".",10);
				else
					parent.append("You see "+parent.floornames[parent.floornamepointers[tilemap[x][y]]]+" and "+parent.animnames[((itemmap[x][y]+1-nitems)-2)/8]+".",10);
		}

		if (omdisplaymode==4)
			return true;
		return false;
	}*/
	public void placePlayer(String incoming) {
		// <xyscccc
		// <xys
		// <xysccccxys

		char[] ichar = incoming.toCharArray();
		int x,y;

		if (ichar.length == 4) {
			blankOutPlayer((ichar[1]-=32),(ichar[2]-=32));
			repaint();
			return;
		}
		else {
			String tcs = ichar[4]+""+ichar[5]+""+ichar[6]+""+ichar[7];
			if (ichar[3] != ' ') {
				x=ichar[1]-32;
				y=ichar[2]-32;
				playermap[x][y] = (byte)(ichar[3]-32);
				colorstrings[x][y] = tcs;

			}
			else {
				blankOutPlayer2(ichar,tcs);
				repaint();
				return;
			}

			//map.playermap[ichar[1]-32][ichar[2]-32] = ichar[3]-32;

			if (ichar.length == 11) { //if blank out added on
				ichar[8]-=32;
				ichar[9]-=32;
				blankOutPlayer(ichar[8],ichar[9]);
				/*if (justmoved) {
					repaint();
					//if (x==xpos&&y==ypos)
						justmoved=false;
				}
				else*/
					drawDoubleDirty(xpos,ypos,x,y,ichar[8],ichar[9]);
			}
			else {
				if (!playercache.containsKey(tcs))
					createCacheEntry(tcs);
				drawDirty(x,y);
			}
		}
	}
	public void blankOutPlayer(char x, char y) {
		//x-=32;
		//y-=32;
		playermap[x][y] = 0;
		colorstrings[x][y] = null;
		//if (!justmoved)
		//	drawDirty(x,y);
	}
	public void blankOutPlayer2(char[] ichar,String tcs) {
		blankOutPlayer((ichar[1]-=32),(ichar[2]-=32));
		removeCacheEntry(tcs);
		repaint();
	}
	/*public void blankOutPlayer3(char x, char y) {
		x-=32;
		y-=32;
		playermap[x][y] = 0;
		colorstrings[x][y] = null;
		drawDirty(x,y);
	}*/
	public void placeItem(String incoming) { // <xyiixyii
		int x1,x2=0,y1,y2=0;
		if (incoming.length() == 9) {
			x2=incoming.charAt(5)-32;
			y2=incoming.charAt(6)-32;
			itemmap[x2][y2] = (short)decode(incoming.charAt(7)-32,incoming.charAt(8)-32);
		}
		x1=incoming.charAt(1)-32;
		y1=incoming.charAt(2)-32;
		itemmap[x1][y1] = (short)decode(incoming.charAt(3)-32,incoming.charAt(4)-32);
		if (parent.dsPinR(x1,y1)) {
			if (incoming.length()==9)
				drawDoubleDirty(xpos,ypos,x1,y1,x2,y2);
			else
				drawDirty(x1,y1);
		}
	}
	public void cacheCleanup() {
		int xs = xpos-3;
		int xe = xpos+3;

		int ys = ypos-8;
		int ye = ypos+8;

		Vector s2s = new Vector();
		int[] items2save = new int[nitems];
		int[] floors2save = new int[floors.length];


		int item = 0,moreass;
		for (int i = 0; i < 4; i++) {
			switch (i) {
				case 0: item = parent.weapon;
					break;
				case 1: item = parent.armour;
					break;
				case 2: item = parent.afeet;
					break;
				case 3: item = parent.ahands;
			}
			if (item > 0 && item < items2save.length)
				items2save[item-1] = 1;
		}

		if (parent.map.omdisplaymode == 2) {
			for (int i = 0; i < parent.inventory.length; i++){
      				if (parent.inventory[i] > 0 && parent.inventory[i] < items2save.length)
					items2save[parent.inventory[i]-1] = 1;
			}
		}

		for (int y = ys; y <= ye; y++) {
			for (int x = xs; x <= xe; x++) {
				try {
					if (colorstrings[x][y]!=null)
						s2s.addElement(parent.map.colorstrings[x][y]);
					moreass=itemmap[x][y];
					if (moreass > 0 && moreass < nitems)
						items2save[moreass-1] = 1;
					floors2save[tilemap[x][y]] = 1;
					//parent.sthread.yield();
				}
				catch (Exception e) {}
			}
		}

		for (Enumeration e = parent.map.playercache.keys(); e.hasMoreElements();) {
			String tcs = e.nextElement().toString();
			boolean delit = true;
			for (int i = 0; i < s2s.size(); i++) {
				if (s2s.elementAt(i).toString().equals(tcs)) {
					delit = false;
					break;
				}
			}
			if (delit)
				removeCacheEntry(tcs);
		}

		for (int i = 0; i < items2save.length; i++) {
			if (items2save[i] == 0) {
				//if (items[i] != null)
					items[i] = null;
			}
		}

		for (int i = 0; i < floors2save.length; i++) {
			if (floors2save[i] == 0) {
				floors[i] = null;
			}
		}

		//System.gc();

		parent.sthread.cachecheck = 0;
	}
	public boolean mouseDown(Event e, int x, int y) {
		if (parent.keyok)
			parent.mapMouseDown(x,y);
		return false;
	}
	public boolean mouseMove(Event e, int x, int y) {
			if (y < 103) {
				if (x < 192) {
					if (parent.dirpoint!=3) {
						int t=parent.dirpoint;
						parent.repaintDIRPoint(t);
						parent.dirpoint=3;
						parent.repaintDIRPoint(3);
					}
				}
				else {
					if (parent.dirpoint!=4) {
						int t=parent.dirpoint;
						parent.repaintDIRPoint(t);
						parent.dirpoint=4;
						parent.repaintDIRPoint(4);
					}
				}
			}
			else if (y > 153) {
				if (x < 192) {
					if (parent.dirpoint!=1) {
						int t=parent.dirpoint;
						parent.repaintDIRPoint(t);
						parent.dirpoint=1;
						parent.repaintDIRPoint(1);
					}
				}
				else {
					if (parent.dirpoint!=2) {
						int t=parent.dirpoint;
						parent.repaintDIRPoint(t);
						parent.dirpoint=2;
						parent.repaintDIRPoint(2);
					}				
				}
			}
			else {
				if (x < 192) {
					if (parent.dirpoint!=5) {
						int t=parent.dirpoint;
						parent.repaintDIRPoint(t);
						parent.dirpoint=5;
						parent.repaintDIRPoint(5);
					}
				}
				else {
					if (parent.dirpoint!=6) {
						int t=parent.dirpoint;
						parent.repaintDIRPoint(t);
						parent.dirpoint=6;
						parent.repaintDIRPoint(6);
					}				
				}
			}
		return true;
	}
	public boolean mouseExit(Event e, int x, int y) {
		if (parent.dirpoint!=0) {
			int t = parent.dirpoint;
			parent.dirpoint=0;
			parent.repaintDIRPoint(t);
		}
		return true;
	}

	/*****
	<Over-map display
	*****/

	/************
	Display Table
	0 - No display
	1 - Stats
	2 - Inventory
	3 - Shop
	4 - Interact
	************/

	public void requestOMDisplay(int mode) {

		//if (mode<-1 || mode>4) //Available modes.
		//	return;

		switch(omdisplaymode) {
			case 3:
				parent.clearShop(mode);
				break;
			case 4:
				buffer_string=null;
				buffer_string_array=null;
				break;
			//case 0:
			case 1:
				if (mode==1)
					mode=0;
				break;
			//case 2:
			//default:
			//	break;
		}

		if (mode==-1)
			omdisplaymode=0;
		else
			omdisplaymode=mode;

		switch(mode) {
			case 3:
			case 4:
				break;
			default:
				drawOMDisplay(getGraphics(),true);
		}
	}

	public void drawOMDisplay(Graphics g,boolean repaint) {
		if (repaint)
			repaint();
		switch (omdisplaymode) {
			case 0:
				repaint();
				break;
			case 1:
				drawStats(g);
				break;
			case 2:
				drawInv(g);
				break;
			case 3:
				//if (inshop != -1)
				drawShop(g);
				break;
			case 4:
				drawInteraction(g);
		}
	}

	public void drawInteraction(Graphics g) {
		g.drawImage(parent.wood,5,5,this);
		g.setFont(parent.fs);

		//int y;

		for (int n = 0; n < 2; n++) {
			g.setColor(scolors[n]);
			g.drawLine(7-n,23-n,254-n,23-n);
			g.drawString(buffer_string,10-n,20-n);
			for (int i=0,y=35;buffer_string_array[i]!=null;i++,y+=11)
				g.drawString(buffer_string_array[i],10-n,y-n);
		}

		g.setColor(Color.black);
		g.drawString("Press Ctrl-L to close",79,158);
		g.setColor(Color.green);
		g.drawString("Press Ctrl-L to close",78,157);
	}

	/*****
	</Over-map display>
	*****/


	// NEO KICK ASS PAINT

	public void repaint() {
		if (drawok == 1 && isrepaint == 0) {

			isrepaint = 1;

			oe = ypos%2;

			xs = xpos-3;
			ys = ypos-8;

			xe = xpos+3;
			ye = ypos+8;

			if (drawTiles == 1)
				drawTileGraphics();

			buffImgG.drawImage(tileImg,0,0,this);
			curry = -16;
			indentline = false;

			ye+=2;

			if (oe==0)
				drawIPEven();
			else
				drawIPOdd();

			if (omdisplaymode!=0)
				drawOMDisplay(buffImgG,false);

			if (drawcompass)
				buffImgG.drawImage(parent.compass,333,1,this);

			g.drawImage(buffImg,0,0,this);

			isrepaint = 0;
		}
	}

	public void drawDirty(int x, int y) {
		if (justmoved!=0) {
			justmoved--;
			repaint();
			return;
		}
		new dirtyThread(/*xpos,ypos,*/x,y,this);
		/*try {
		if (omdisplaymode!=0||(++dirtycount)==3) {
			dirtycount=0;
			repaint();
			return;
		}

		if (drawok==1) {
			int xx=160-((xpos-x)*64);
			if (ypos%2!=y%2) {
				if (oe==0)
					xx-=32;
				else
					xx+=32;
			}
			int yy=48-((ypos-y)*16);
			
			g.drawImage(getDirtyImage(x,y),xx,yy,this);

			if (drawcompass&&x>=271&&y<=49)
				g.drawImage(parent.compass,333,1,this);
		}
		}
		catch (Exception e) {}*/
	}

	public void drawDoubleDirty(int xpos, int ypos, int x, int y, int x2, int y2) {
		if (justmoved!=0) {
			justmoved--;
			repaint();
			return;
		}
		//new dirtyThread2(x,y,x2,y2,this);

		try {
		if (omdisplaymode!=0||(++dirtycount)==3) {
			dirtycount=0;
			repaint();
			return;
		}

		if (drawok==1) {
			int xx=160-((xpos-x)*64);
			if (ypos%2!=y%2) {
				if (oe==0)
					xx-=32;
				else
					xx+=32;
			}
			int yy=48-((ypos-y)*16);

			int xx2=160-((xpos-x2)*64);
			if (ypos%2!=y2%2) {
				if (oe==0)
					xx2-=32;
				else
					xx2+=32;
			}
			int yy2=48-((ypos-y2)*16);

			Image i = getDirtyImage(x,y);
			Image i2 = getDirtyImage(x2,y2);

			//if (this.xpos==xpos && this.ypos==ypos) {
				g.drawImage(i2,xx2,yy2,this);
				g.drawImage(i,xx,yy,this);
			
				if (drawcompass)
					g.drawImage(parent.compass,333,1,this);
			//}
		}
		}
		catch (Exception e) {}	
	}

	public Image getDirtyImage(int x, int y) {
		Image i = createImage(62,96);
		Graphics g = i.getGraphics();
		int dxs,dxe,dys=y-5,dye=y+2,currx,curry=-16;
		boolean indentline=false;

		if (y%2==0) {
			dxs=x;
			dxe=x+2;

			// drawTileEven2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);

				for (x = dxs; x < dxe; x++) {
					drawTileSimple2(x,y,currx,curry,g);
					currx+=64;
				}

				if (y%2!=0)
					drawTileSimple2(x,y,currx,curry,g);

				curry+=16;
				indentline = !indentline;
			}

			dye+=4;
			indentline=false;
			curry=-16;

			// drawIPEven2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);
				for (x = dxs; x < dxe; x++) {
					drawIPSimple2(x,y,currx,curry,g);
					currx+=64;
				}

				if (y%2!=0)
					drawIPSimple2(x,y,currx,curry,g);
					
				curry+=16;
				indentline = !indentline;
			}
		}
		else {
			dxs=x-1;
			dxe=x+1;

			// drawTileOdd2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);
				x=dxs;
				if (y%2!=0)
					x++;
				for (;x <= dxe; x++) {
					drawTileSimple2(x,y,currx,curry,g);
					currx+=64;
				}
				curry+=16;
				indentline = !indentline;
			}

			dye+=4;
			indentline=false;
			curry=-16;

			// drawIPOdd2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);
				x=dxs;
				if (y%2!=0)
					x++;

				for (; x <= dxe; x++) {
					drawIPSimple2(x,y,currx,curry,g);
					currx+=64;
				}
				curry+=16;
				indentline = !indentline;
			}
		}

		//g.drawRect(0,0,61,95);

		return i;
	}

	public void drawIPEven() {
		for (y = ys; y <= ye; y++) {
			currx = ((indentline) ? 1 : -31);
			x=xs;
			if (y%2!=0)
				x++;

			for (; x <= xe; x++) {
				drawIPSimple();
				currx+=64;
			}
			curry+=16;
			indentline = !indentline;
		}
	}

	public void drawIPOdd() {
		for (y = ys; y <= ye; y++) {
			currx = ((indentline) ? 1 : -31);
			for (x = xs; x < xe; x++) {
				drawIPSimple();
				currx+=64;
			}

			if (y%2!=0)
				drawIPSimple();
				
			curry+=16;
			indentline = !indentline;
		}
	}

	public void drawIPSimple() {
		try {
			myass=itemmap[x][y];
			if (myass != 0) 
				buffImgG.drawImage(getItem(myass),currx,curry-itall[myass],this);
			if (playermap[x][y] != 0) {
				int t = colorstrings[x][y].charAt(3)-32;
				buffImgG.drawImage(getPlayerImage(x,y),currx+ppush[t],curry-ptall[t],this);
			}
			myass=spellmap[x][y];
			if (myass != 0)
				buffImgG.drawImage(getItem(myass),currx,curry-itall[myass],this);
		}
		catch (Exception e) {
			IPExceptionHandle(x,y,currx,curry,buffImgG,e);
		}
		//if (parent.spells.size() > 0)
		//	drawSpells();
	}

	public void drawTileGraphics() {
		tilecount = 0;

		curry = -16;
		indentline = false;

		if (oe==0)
			drawTileEven();
		else
			drawTileOdd();

		drawTiles=0;
	}
	public void drawTileEven() {
		for (y = ys; y <= ye; y++) {
			currx = ((indentline) ? 1 : -31);
			x=xs;
			if (y%2!=0)
				x++;
			for (;x <= xe; x++) {
				drawTileSimple();
				currx+=64;
			}
			curry+=16;
			indentline = !indentline;
		}
	}

	public void drawTileOdd() {
		for (y = ys; y <= ye; y++) {
			currx = ((indentline) ? 1 : -31);

			for (x = xs; x < xe; x++) {
				drawTileSimple();
				currx+=64;
			}

			if (y%2!=0)
				drawTileSimple();

			curry+=16;
			indentline = !indentline;
		}
	}

	public void drawTileSimple() {
		try {
			myass=tilemap[x][y];
			if (myass != tempmap[tilecount]) {
				while (floors[myass] == null)
					fetchFloorImg(tilemap[x][y]);

				tileImgG.drawImage(floors[myass],currx,curry,this);
				tempmap[tilecount] = myass;
			}
		}
		catch (Exception e) {
			tempmap[tilecount] = -1;
		}
		tilecount++;
	}
	public void drawTileSimple2(int x, int y, int currx, int curry, Graphics g) {
		try {
			myass=tilemap[x][y];
			if (floors[myass] == null)
				fetchFloorImg(myass);

			g.drawImage(floors[myass],currx,curry,this);
		}
		catch (Exception e) {
		}
	}
	public void drawIPSimple2(int x, int y, int currx, int curry, Graphics g) {
		int myass;
		try {
			myass=itemmap[x][y];
			if (myass != 0)
					g.drawImage(getItem(myass),currx,curry-itall[myass],this);
			if (playermap[x][y] != 0) {
				int t = colorstrings[x][y].charAt(3)-32;
				g.drawImage( ((PICO)playercache.get(colorstrings[x][y])) .images[playermap[x][y]-1],currx+ppush[t],curry-ptall[t],this);
			}
			myass=spellmap[x][y];
			if (myass != 0)
				g.drawImage(getItem(myass),currx,curry-itall[myass],this);
		}
		catch (Exception e) {
			IPExceptionHandle(x,y,currx,curry,g,e);
		}
		//if (parent.spells.size() > 0)
		//	drawSpells();
	}
	public void IPExceptionHandle(int x, int y, int currx, int curry, Graphics g, Exception e) {
		if (e instanceof NullPointerException) {
			String cs=colorstrings[x][y];
			try {
				if (((PICO)playercache.get(cs)) .images[playermap[x][y]-1]==null) {
					if (cs.charAt(3)>' '&&cs.charAt(3)<'&') {
						removeCacheEntry(cs);
						createCacheEntry(cs);
						drawIPSimple2(x,y,currx,curry,g);
					}
				}
			}
			catch (NullPointerException exxx) {
				createCacheEntry(cs);
				return;
			}
		}

		g.drawImage(parent.end,currx,curry,this);
	}
}
