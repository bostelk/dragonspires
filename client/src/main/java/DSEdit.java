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

import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;

//import java.applet.*;

import java.net.Socket;
import java.net.URL;

import java.io.*;

import java.util.Vector;
import java.util.StringTokenizer;
//import audio.*;

public class DSEdit extends Panel {

	Object parent;
	DSEMapCanvas map;

	Font f;
	FontMetrics fm;

	private DataInputStream i;
	private PrintStream o;
	private Socket s;
	Thread listen;

	Vector ignores;

	Image[] hps;
	Image marbled,preview,end,typebuffimg,compass;//,night;
	Graphics ttg;

	boolean keyok,amapplet,saved=true;

	Color dsbg,dstext;

	Toolkit toolkit;

	private String[] TextArray;
	String line1="",line2="",path,colorstring,connstat,name;

	int[] inventory;
	int keyhold,t,
		ledin = -3,
		scrollpos = 6;
	
	Frame fr;

	//Dialog stuff.
	boolean dialog=false;
	int dialognum=-1; // 0=save when quit, 1=save when change map, 2=open spec map, 3=make new
	String dialogdata,dialogtext;

	public static void main(String args[]) {
		Frame frame = new Frame("LOADING . . .");

		DSEdit p = new DSEdit(frame);
		frame.add("Center", p);
		frame.resize(586,441);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.move(d.width/2-586/2,d.height/2-441/2);
		frame.setIconImage(p.toolkit.getImage("edit1.gif"));
		frame.show();

		p.start();

		frame.setTitle("DragonSpires Map Editor");
	}
	public DSEdit(Frame frt) {
		fr=frt;

		System.out.println("\n\nIt's okay to hide me! =)");
		setLayout(null);

		toolkit = Toolkit.getDefaultToolkit();

		keyok = false;

		line1=line2=connstat="";

		//setBackground(new Color(179,207,235));
		dsbg = new Color(179,207,235);
		dstext = new Color(0,0,55);

		TextArray = new String[30];
		for (int i = 0; i < TextArray.length; i++) {
			TextArray[i] = "";
		}

		end = toolkit.getImage("end.gif");
		marbled = toolkit.getImage("marbled.gif");
		f = new Font("TimesRoman", Font.BOLD, 12);
		fm = getFontMetrics(f);
		map = new DSEMapCanvas(this);

		map.move(183,11);
		map.resize(385,256);
		map.hide();

		add(map);
	}
	public void start() {
		doTracker();
		typebuffimg = createImage(406,26);
		ttg = typebuffimg.getGraphics();
		ledin = 1;
		setupNewMap("lev01.dsmap");
		map.show();
		repaint();
		resumeMapDraw();
		append("Welcome to the DragonSpires Editor. Press Z for help.");
		//Graphics g = getGraphics();
		//g.setColor(new Color(49,49,82));
		//g.fillRect(0,0,size().width,size().height);
		//repaint();
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void paint(Graphics g) {
		if (map.drawok >= 0) {
			g.drawImage(marbled,0,0,this);

			g.setColor(dstext);

			for (int spm = 0; spm < 7; spm++) {
				g.drawString(TextArray[scrollpos-spm],163,279+(12*(spm+1)));
			}
			drawTypeText();

			//if (ledin < 1)
			//	preGamePaint(g);

		}
		//if (map.drawok < 0)
		//	drawTitle(g);
	}
	/*public void drawTitle(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0,386,size().width,20);
		//g.drawImage(toolkit.getImage("title256.gif"),-30,-20,this);
		//g.drawImage(java.awt.Toolkit.getDefaultToolkit().getImage("title256.gif"),0,0,this);
		g.setFont(f);
		g.setColor(Color.yellow);
		g.drawString(connstat,size().width/2-fm.stringWidth(connstat)/2,392);
	}*/
	private void updatePos(int x, int y) {
		map.xpos = x;
		map.ypos = y;
		map.drawTiles = 1;
		dataString();
		drawTypeText();
		map.repaint();
	}
	public void incomingText(String incoming) {

		for (int i = 0; i < ignores.size(); i++) {
			//System.out.println(ignores.elementAt(i).toString());
			
			if (incoming.toLowerCase().startsWith(ignores.elementAt(i).toString()))
				return;
		}

		append(incoming);
	}
	public void haltMapDraw() {
		keyok = false;
		map.drawok = 0;
		map.repaint();
	}
	public void resumeMapDraw() {
		keyok = true;
		map.drawok = 1;
		map.repaint();
	}
	public void setupNewMap(String name) {
		File testfile = new File(name);
		if (testfile.exists()) {
			if (!saved) {
				initDialog(1,"This map isn't saved. Save it? (Y/N)",name);
				return;
			}
			map.getMapData(name);
			map.initMap();
			map.repaint();
			saved=true;
		}
		else
			initDialog(3,name+" doesn't exist. Would you like to create it? (Y/N)",name);
	}
	public boolean mouseDown(Event e, int x, int y) {
		return false;
	}
	public boolean keyUp(Event e, int key) {
		keyhold = 0;
		return true;
	}
	public boolean keyDown(Event e, int key) {
		if (keyok) {
				if (dialog) {
					dialogInput(key);
					return true;
				}
				switch (key) {
					
					case '+':
						if (map.currseltype==1) {
							if (++map.curritemsel>map.nitems)
								map.curritemsel=map.nitems;
							map.itemmap[map.xpos][map.ypos]=map.curritemsel;
						}
						else if (map.currseltype==2) {
							map.drawTiles=1;
							if (++map.currfloorsel>map.floors.length)
								map.currfloorsel=map.floors.length;
							map.tilemap[map.xpos][map.ypos]=map.currfloorsel;
						}
						dataString();
						drawTypeText();
						map.repaint();
						return true;
					case '-':
						if (map.currseltype==1) {
							if (--map.curritemsel<0)
								map.curritemsel=0;
							map.itemmap[map.xpos][map.ypos]=map.curritemsel;
						}
						else if (map.currseltype==2) {
							map.drawTiles=1;
							if (--map.currfloorsel<0)
								map.curritemsel=0;
							map.tilemap[map.xpos][map.ypos]=map.currfloorsel;
						}
					
						dataString();
						drawTypeText();
						map.repaint();
						return true;
					case '.':
						if (map.currseltype==1) {
							if ((map.curritemsel+=10)>map.nitems)
								map.curritemsel=map.nitems;
							map.itemmap[map.xpos][map.ypos]=map.curritemsel;
						}
						else if (map.currseltype==2) {
							map.drawTiles=1;
							if ((map.currfloorsel+=10)>map.floors.length)
								map.currfloorsel=map.floors.length;
							map.tilemap[map.xpos][map.ypos]=map.currfloorsel;
						}
						dataString();
						drawTypeText();
						map.repaint();
						return true;
					case ',':
						if (map.currseltype==1) {
							if ((map.curritemsel-=10)<0)
								map.curritemsel=0;
							map.itemmap[map.xpos][map.ypos]=map.curritemsel;
						}
						else if (map.currseltype==2) {
							map.drawTiles=1;
							if ((map.currfloorsel-=10)<0)
								map.curritemsel=0;
							map.tilemap[map.xpos][map.ypos]=map.currfloorsel;
						}
					
						dataString();
						drawTypeText();
						map.repaint();
						return true;
					case '1':
						map.currseltype=1;

						map.tilemap[map.xpos][map.ypos]=t;
						t=map.itemmap[map.xpos][map.ypos];
						map.itemmap[map.xpos][map.ypos]=map.curritemsel;

						map.drawTiles=1;
						dataString();
						drawTypeText();
						map.repaint();
						return true;
					case '2':
						map.currseltype=2;

						map.itemmap[map.xpos][map.ypos]=t;
						t=map.tilemap[map.xpos][map.ypos];
						map.tilemap[map.xpos][map.ypos]=map.currfloorsel;

						map.drawTiles=1;
						dataString();
						drawTypeText();
						map.repaint();
						return true;						
					case 'c':
					case 'C':
						if (map.currseltype==1) {
							for (int x=0;x<map.mwidth;x++) {
								for (int y=0;y<map.mheight;y++) {
									map.itemmap[x][y]=map.curritemsel;
								}
							}
						}
						else if (map.currseltype==2) {
							for (int x=0;x<map.mwidth;x++) {
								for (int y=0;y<map.mheight;y++) {
									map.tilemap[x][y]=map.currfloorsel;
								}
							}
						}
						return true;
					case 's':
					case 'S':
						map.saveMap();
						return true;
					/*case 'n':
					case 'N':
						char[] mn = map.mapname.toCharArray();
						if (++mn[4]>'9') {
							mn[4]='0';
							mn[3]++;
						}
						setupNewMap(new String(mn));
						return true;
					case 'p':
					case 'P':
						char[] mn2 = map.mapname.toCharArray();
						if (--mn2[4]<'0') {
							mn2[4]='9';
							mn2[3]--;
						}
						setupNewMap(new String(mn2));
						return true;
					*/case 'o':
					case 'O':
						OBclear();
						initDialog(2,"Type the map filename you want to open below then press Enter.","");
						return true;
					case 'z':
					case 'Z':
						append("Press 1 to select items, 2 to select floors. +/- to select.");
						append("Press Enter to place. Press Space to toggle drag.");
						append("Press S to save map. Press O to open a map.");
						//append("Press N for next map. P for previous map.");
						append("Press ESC to exit.");

						return true;
					//case '\b':
					//	backspace();
					//	return true;
					case '\n':
						enterPress();
						return true;
					case ' ':
						map.drag = !map.drag;
						if (!map.drag)
							enterPress();
						return true;
					case Event.ESCAPE:
						if (!saved) {
							initDialog(0,"This map isn't saved. Save it? (Y/N/C)","");
							return true;
						}
						fr.dispose();
						System.exit(0);
						return true;
					case Event.UP:
						if (upPress(e))
							return true;
					case Event.PGUP:
						if (map.drag)
							enterPress();
						goThatWay(9);
						return true;

					case Event.DOWN:
						if (downPress(e))
							return true;
					case Event.END:
						if (map.drag)
							enterPress();
						goThatWay(1);
						return true;

					case Event.LEFT:
						if (leftPress(e))
							return true;
					case Event.HOME:
						if (map.drag)
							enterPress();
						goThatWay(7);
						return true;

					case Event.RIGHT:
						if (rightPress(e))
							return true;
					case Event.PGDN:
						if (map.drag)
							enterPress();
						goThatWay(3);
						return true;
					case Event.TAB:
						return true;
					default:
						if (e.controlDown()) {
							controlKeyDown(key);
							return true;
						}

				}
		}
		//else if (ledin == 0) {
		//	if (key > 31 && key < 127 && c != '\n') {
		//		if ((e.target == namebox && namebox.getText().length() <= 14) || (e.target == passbox && passbox.getText().length() <= 14))
		//			retval = false;	
		//		else if (e.target == desc && desc.getText().length() <= 300)
		//			retval = false;	
		//	}
		//	else
		//		retval = false;	
		//}
		return false;
	}
	public void enterPress() {
		if (saved)
			saved=false;

		if (map.currseltype==1)
			t=map.curritemsel;
		else if (map.currseltype==2)
			t=map.currfloorsel;
	}
	private void controlKeyDown(int key) {
	}
	public boolean action(Event e, Object arg) {
		return false;
	}
	public int nextx(int x, int y, int dir) {
		int nx = x;
		switch (dir) {
			case 9:
			case 3: if (y%2==0) nx++; break;
			case 7:
			case 1: if (y%2==1) nx--;
		}
		if (nx < 0)
			nx = x;
		else if (nx >= map.mwidth) nx=x;

		return nx;
	}
	public int nexty(int x, int y, int dir) {
		int ny = y;
		switch (dir) {
			case 7:
			case 9: ny--; break;
			case 1:
			case 3: ny++;
		}
		if (ny < 0)
			ny = y;
		else if (ny >= map.mheight) ny=y;

		return ny;
	}
	public void doTracker() {

		//Load source images
		String[] ttt = {"floors","items"};
		String[] tttn = {"floor.gif","item256.gif"};
		Image[] ttti = new Image[tttn.length];
		MediaTracker tracker = new MediaTracker(this);

		for (int i = 0; i < tttn.length; i++) {
			connstat = "Now loading "+ttt[i]+"...";
			repaint(0,370,size().width,size().height);
			ttti[i] = toolkit.getImage(tttn[i]);
			tracker.addImage(ttti[i],i);
			try {
				tracker.waitForID(i);
				while(!(tracker.checkID(i))){}
			}
			catch (InterruptedException e) {
			}
		}

		connstat = "Processing...";
		repaint(0,370,size().width,size().height);

		map.flrsrc = ttti[0].getSource();

		ItemImgSep iis = new ItemImgSep(map,null,toolkit);
		iis.doEditor(map);
		map.itemsrc = ttti[1].getSource();
	}
	public void append(String text) {
		String templine = "";
		String tempword = "";
		char[] charys = text.toCharArray();
		String[] words = new String[0];
		for (int i = 0; i < charys.length; i++) {
			if (charys[i] == ' ' || i == charys.length-1) {
				String[] tempwords = new String[words.length+1];
				for (int n = 0; n < words.length; n++) {
					tempwords[n] = words[n];
				}
				tempwords[words.length] = tempword+charys[i];
				words = tempwords;
				tempword = "";
			}
			else {
				tempword+=charys[i];
			}
		}
		for (int i = 0; i < words.length; i++) {
			tempword = words[i];
			if (fm.stringWidth(templine+tempword) < 406) {
				//if (templine.equals(""))
				//	templine = tempword;
				//else
					templine+=tempword;
			}
			else {
				addLine(templine);
				templine = tempword;
				while (fm.stringWidth(templine) > 406) {
					String[] tempstuff = trimLongLine(templine);
					addLine(tempstuff[0]);
					templine = tempstuff[1];
				}
			}
		}
		addLine(templine);
		repaint(162,279,406,90);
	}
	public String[] trimLongLine(String text) {
		String retval[] = {"",""};
		char[] charys = text.toCharArray();
		for (int i = 0; i < charys.length; i++) {
			if (fm.stringWidth(retval[0]+charys[i]) > 406) {
				for (int n = i; n < charys.length; n++) {
					retval[1]+=charys[i];
				}
				return retval;
			}
			else
				retval[0]+=charys[i];
		}
		return null;
	}
	public void addLine(String text) {
		text = text.replace('|',' ');
		for (int i = TextArray.length-1; i > 0; i--) {
			TextArray[i] = TextArray[i-1];
			//System.out.println(TextArray[i] + "-"+TextArray[i-1]);
		}
		TextArray[0] = text;
		if (scrollpos > 6) {
			scrollpos++;
			if (scrollpos > TextArray.length - 1)
				scrollpos = TextArray.length-1;
		}			
	}
	public void scrollUp() {
		if (scrollpos < TextArray.length - 1) {
			scrollpos++;
			repaint(162,279,406,90);
		}
	}
	public void scrollDown() {
		if (scrollpos > 6) {
			scrollpos--;
			repaint(162,279,406,90);
		}
	}
	public void append(char c) {
		if (line1 != "" && fm.stringWidth(line1 + c + "|") <= 406 && line2.equals("")) {
			line1 = line1.substring(0, line1.length()-1) + c + "|";
		}
		else if (fm.stringWidth(line1 + c + "|") <= 406 && line2.equals("")) {
			line1 = line1 + c + "|";
		}
		else if (line2 != "" && fm.stringWidth(line2 + c + "|") <= 406) {
			line2 = line2.substring(0, line2.length()-1) + c + "|";
		}
		else if (fm.stringWidth(line2 + c + "|") <= 406) {
			line1 = line1.substring(0, line1.length()-1);
			line2 = line2 + c + "|";
		}
		//repaint(162,372,360,26);
		drawTypeText();
	}
	public void backspace() {
		if (line2 != "") {
			try {
				line2 = line2.substring(0,line2.length()-2) + "|";
				if (line2.equals("|"))
					line2 = "";
			}
			catch(StringIndexOutOfBoundsException e) {
				line2 = "";
			}
			//repaint(162,372,360,26);
		}
		else {
			try {
				line1 = line1.substring(0,line1.length()-2) + "|";
				if (line1.equals("|"))
					line1 = "";
			}
			catch(StringIndexOutOfBoundsException e) {
				line1 = "";
			}
			//repaint(162,372,360,26);
			//drawTypeText();
		}
		drawTypeText();
	}
	public void OBclear() {
		line1 = "";
		line2 = "";
		//repaint(162,372,360,26);
		drawTypeText();
	}
	public void drawTypeText() {
		ttg.setColor(dsbg);
		ttg.fillRect(0,0,406,26);
		ttg.setColor(dstext);
		ttg.setFont(f);
		ttg.drawString(line1,1,10);
		ttg.drawString(line2,1,22);
		getGraphics().drawImage(typebuffimg,162,372,this);
	}
	public boolean lostFocus(Event e, Object what) {
		/*if (e.target == this && ledin == 1)
			if (tryfocus)
				requestFocus();
			else
				tryfocus = true;*/
		return true;
	}
	private boolean upPress(Event e) {
		if (e.shiftDown()) {
			scrollUp();
			return true;
		}
		return false;
	}
	private boolean downPress(Event e) {
		if (e.shiftDown()) {
			scrollDown();
			return true;
		}
		return false;
	}
	private boolean leftPress(Event e) {
		return false;
	}
	private boolean rightPress(Event e) {
		return false;
	}
/*	public boolean dsPinR(int x, int y) {
		return (map.ypos % 2 == 0) ? even_dsPinR(x,y) : odd_dsPinR(x,y);
	}
	public boolean even_dsPinR(int x, int y) {
		return (x >= map.xpos-3 &&
			x <= map.xpos+3 &&
			y >= map.ypos-8 &&
			y <= map.ypos+8 &&
			!(x == map.xpos-3 && y%2 == 1));
	}
	public boolean odd_dsPinR(int x, int y) {
		return (x >= map.xpos-3 &&
			x <= map.xpos+3 &&
			y >= map.ypos-8 &&
			y <= map.ypos+8 &&
			!(x == map.xpos+3 && y%2 == 0));
	}
*/
	public void dialogInput(int c) {
		switch (dialognum) {
			case 0:
				switch (c) {
					case 'y':
					case 'Y':
						map.saveMap();
					case 'n':
					case 'N':
						fr.dispose();
						System.exit(0);				
						break;
					case 'c':
					case 'C':
						closeDialog();
				}
				break;
			case 1:
				switch (c) {
					case 'y':
					case 'Y':
						map.saveMap();
					case 'n':
					case 'N':
						saved=true;
						setupNewMap(dialogdata);
						closeDialog();
				}
				break;
			case 2:
				switch (c) {
					case '\n':
						String tempo = line1;
						if (line2.equals(""))
							tempo=tempo.substring(0,tempo.length()-1);
						else
							tempo+=line2.substring(0,line2.length()-1);
						closeDialog();
						setupNewMap(tempo);
						OBclear();
						break;
					case '\b':
						backspace();
						break;
					default:
						if (c > 31 && c < 127)
							append ((char)c);
				}
				break;
			case 3:
				switch (c) {
					case 'y':
					case 'Y':
						createNewMap(dialogdata);						
						setupNewMap(dialogdata);
					case 'n':
					case 'N':
						closeDialog();
				}
				break;
		}
	}
	public void initDialog(int num, String text, String data) {
		dialog=true;
		dialognum=num;
		dialogtext=text;
		dialogdata=data;
		append(text);
	}
	public void closeDialog() {
		dialog=false;
		dialognum=-1;
		dialogtext=null;
		dialogdata=null;
	}
	public void createNewMap(String thename) {
		append("Creating '"+thename+"'...");
		try {
		RandomAccessFile o = new RandomAccessFile(thename,"rw");
		for (int x=0;x<map.mwidth;x++) {
			for (int y=0;y<map.mheight;y++) {
				o.writeBytes(map.encode(0));
			}
		}
		for (int x=0;x<map.mwidth;x++) {
			for (int y=0;y<map.mheight;y++) {
				o.writeBytes(map.encode(5));
			}
		}
		o.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	public void dataString() {
		line1="["+map.mapname+"] X="+map.xpos+",Y="+map.ypos+((map.currseltype==1) ? (" item="+map.curritemsel) : (" floor="+map.currfloorsel));
	}
	public void goThatWay(int dir) {
		if (keyhold > 0) {
			keyhold--;
			return;
		}
		keyhold=3;
		int nx=nextx(map.xpos,map.ypos,dir);
		int ny=nexty(map.xpos,map.ypos,dir);

		switch (map.currseltype) {
			case 1:
				map.itemmap[map.xpos][map.ypos]=t;
				t=map.itemmap[nx][ny];
				map.itemmap[nx][ny]=map.curritemsel;
				break;
			case 2:
				map.tilemap[map.xpos][map.ypos]=t;
				t=map.tilemap[nx][ny];
				map.tilemap[nx][ny]=map.currfloorsel;
		}

		updatePos(nx,ny);
	}	
}