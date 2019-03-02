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
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.CropImageFilter;

public class DSEMapCanvas extends Canvas {

	DSEdit parent;

	Image buffImg,tileImg;

	int xpos=26,ypos=41,isrepaint,currx, curry, xs, xe, ys, ye,oe,tilecount,x,y,
		inshop = -1,
		drawok = -1,
		drawTiles = 1,
		nitems=0,curritemsel=2,currfloorsel=2,currseltype=1,t;

	final short mwidth = 52,
		  mheight = 100;

	String mapname;

	int[][] tilemap,itemmap;

	int[] tempmap;
	Graphics buffImgG,tileImgG,g;
	Image[] floors,items,playerextra;
	ImageProducer[] player;
	ImageProducer itemsrc,flrsrc;
	//map ints;
	/*final byte colorindexes[] = {0,3,8 //black
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
				};*/
	Hashtable playercache;

	boolean indentline,drag;

	byte itall[]=null;

	short itemcoords[]=null;

	public DSEMapCanvas(DSEdit p) {
		setBackground(new Color(179,207,235));
		tilemap = new int[mwidth][mheight];
		itemmap = new int[mwidth][mheight];
		tempmap = new int[132];

		parent = p;

		floors = new Image[90];

	}
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		//System.out.print("!"+flags+"!");

		if (flags == 32)
				repaint();
		/*switch (flags) {
			case 128:
				System.out.println("imageUpdate ABORT flag");
				break;
			case 64:
				System.out.println("imageUpdate ERROR flag");
				break;
			case 32:*/
		//}
		return true;
	}
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
							t=itemmap[x][y];
							if (currseltype==1) {
								if (x==xpos&&y==ypos)
									t=curritemsel;
							}
							if (t != 0) {
								if (t < items.length)
									buffImgG.drawImage(getItem(t),currx,curry-itall[t],this);
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
							t=itemmap[x][y];
							if (currseltype==1) {
								if (x==xpos&&y==ypos)
									t=curritemsel;
							}
							if (t != 0) {
								if (t < items.length)
									buffImgG.drawImage(getItem(t),currx,curry-itall[t],this);
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
						currx+=64;
					}
					curry+=16;
					indentline = !indentline;
				}
			}
			//buffImgG.drawImage(parent.night,0,0,this);
			
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
					t=tilemap[x][y];
					if (currseltype==2) {
						if (x==xpos&&y==ypos)
							t=currfloorsel;
					}
					if (t != tempmap[tilecount]) {
						if (floors[t] == null)
							fetchFloorImg(t);

						tileImgG.drawImage(floors[t],currx,curry,this);
						tempmap[tilecount] = t;
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
					t=tilemap[x][y];
					if (currseltype==2) {
						if (x==xpos&&y==ypos)
							t=currfloorsel;
					}
					if (t != tempmap[tilecount]) {
						if (floors[t] == null)
							fetchFloorImg(t);

						tileImgG.drawImage(floors[t],currx,curry,this);
						tempmap[tilecount] = t;
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
	} */
	public void paint(Graphics g) {
		g.drawImage(buffImg,0,0,this);
		//repaint(-99,-99);
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void initMap() {
		if (buffImg == null) {
			//buffImg = this.createImage(500,300);
			buffImg = this.createImage(385,256);
			buffImgG = buffImg.getGraphics();
			//tileImg = this.createImage(500,300);
			tileImg = this.createImage(385,256);
			tileImgG = tileImg.getGraphics();
			//drawok = 1;
			g = getGraphics();
		}		
		for (int i = 0; i < tempmap.length; i++) {
			tempmap[i] = -1;
		}
		drawTiles = 1;
		isrepaint = 0;
		switch (currseltype) {
			case 1:
				parent.t=itemmap[xpos][ypos];
				itemmap[xpos][ypos]=curritemsel;
				break;
			case 2:
				parent.t=tilemap[xpos][ypos];
				tilemap[xpos][ypos]=currfloorsel;
				//break;
		}
	}
	public void getMapData(String mapname) {
		this.mapname=mapname;
		try {
			//int head,tack;
			parent.append("* Loading map. Please wait. *");
			FileInputStream i = new FileInputStream(mapname);
			//System.out.println(theurl.toString());
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					tilemap[x][y]=decode(i.read(),i.read());
					//tilemap[x][y] = i.read();
					//System.out.print(""+tilemap[x][y]);
					//i.skip(1);
				}
			}
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					itemmap[x][y]=decode(i.read(),i.read());
					//itemmap[x][y] = i.read();
					//i.skip(1);
				}
			}
			i.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int decode(int head, int tack) {
		return (head*95+tack);
	}
	public void fetchItemImg(int numnum) {
		//System.out.print("fII");
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
	/*public void placeItem(String incoming) { // <xyiixyii
		if (incoming.length() == 9)
			itemmap[incoming.charAt(5)-32][incoming.charAt(6)-32] = decode(incoming.charAt(7)-32,incoming.charAt(8)-32);
		itemmap[incoming.charAt(1)-32][incoming.charAt(2)-32] = decode(incoming.charAt(3)-32,incoming.charAt(4)-32);
		if (parent.dsPinR(incoming.charAt(1)-32,incoming.charAt(2)-32))
			repaint();
	}*/
/*	public void cacheCleanup() {
		int xs = xpos-3;
		int xe = xpos+3;

		int ys = ypos-8;
		int ye = ypos+8;

		Vector s2s = new Vector();
		int[] items2save = new int[nitems];
		int[] floors2save = new int[floors.length];


		int item = 0;
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

		for (int y = ys; y <= ye; y++) {
			for (int x = xs; x <= xe; x++) {
				try {
					if (!colorstrings[x][y].equals(""))
						s2s.addElement(parent.map.colorstrings[x][y]);
					if (itemmap[x][y] > 0 && itemmap[x][y] < nitems)
						items2save[itemmap[x][y]-1] = 1;
					floors2save[tilemap[x][y]] = 1;
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

		System.gc();

		parent.sthread.cachecheck = 0;
	}*/
	public void saveMap() {
		switch (currseltype) {
			case 1:
				itemmap[xpos][ypos]=parent.t;
				break;
			case 2:
				tilemap[xpos][ypos]=parent.t;
		}
		try {
		RandomAccessFile o = new RandomAccessFile(mapname,"rw");
		for (int x=0;x<mwidth;x++) {
			for (int y=0;y<mheight;y++) {
				o.writeBytes(encode(tilemap[x][y]));
			}
		}
		for (int x=0;x<mwidth;x++) {
			for (int y=0;y<mheight;y++) {
				o.writeBytes(encode(itemmap[x][y]));
			}
		}
		o.close();
		parent.append("Map saved.");
		parent.saved=true;
		}
		catch (Exception e) { e.printStackTrace(); }
		switch (currseltype) {
			case 1:
				itemmap[xpos][ypos]=curritemsel;
				break;
			case 2:
				tilemap[xpos][ypos]=currfloorsel;
		}
	}
	public String encode(int val) {
		return (char)(val/95)+""+(char)(val%95);
	}
	// NEO KICK ASS PAINT

	public void repaint() {
		if (drawok == 1 && isrepaint == 0) {

			isrepaint = 1;

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

			if (oe==0)
				drawIPEven();
			else
				drawIPOdd();
			
			//if (omdisplaymode!=0)
			//	drawOMDisplay(buffImgG);

			//if (drawcompass)
			//	buffImgG.drawImage(parent.compass,333,1,this);

			g.drawImage(buffImg,0,0,this);

			isrepaint = 0;
		}
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
			if (itemmap[x][y] != 0) {
				if (itemmap[x][y] < items.length)
					buffImgG.drawImage(getItem(itemmap[x][y]),currx,curry-itall[itemmap[x][y]],this);
			}
			/*if (playermap[x][y] != 0) {
				int t = colorstrings[x][y].charAt(3)-32;
				buffImgG.drawImage( ((PICO)playercache.get(colorstrings[x][y])) .images[playermap[x][y]-1],currx+ppush[t],curry-ptall[t],this);
				//drawPlayer(playermap[x][y]-1,colorstrings[x][y]);
			}*/
		}
		catch (Exception e) {
			if (e instanceof ArrayIndexOutOfBoundsException)
				buffImgG.drawImage(parent.end,currx,curry-((y > mheight-1) ? 0 : 32),this); //y == mheight || y == mheight+1
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

		drawTiles = 0;
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
			if (tilemap[x][y] != tempmap[tilecount]) {
				if (floors[tilemap[x][y]] == null)
					fetchFloorImg(tilemap[x][y]);

				tileImgG.drawImage(floors[tilemap[x][y]],currx,curry,this);
				tempmap[tilecount] = tilemap[x][y];
			}
		}
		catch (Exception e) {
			tempmap[tilecount] = -1;
		}
		tilecount++;
	}
}