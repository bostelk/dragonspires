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

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.MediaTracker;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Panel;
import java.awt.Frame;
import java.awt.Rectangle;

import java.awt.image.*;

//import java.applet.*;

import java.net.Socket;
import java.net.URL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

import java.util.Vector;
import java.util.StringTokenizer;
//import audio.*;

public class DragonSpiresPanel extends Panel implements Runnable {

	final char version[] = {'V','0','0','2','6'};
	private final static String[] habl = {"localhost"};

	final static String itemnames[]= {"nothing","some paper",
                                                                "light armor",
                                                                "something",
                                                                "a bottle",
                                                                "a heavy sword",
                                                                "a small pouch",
                                                                "a white mark",
                                                                "an evil-vampire-happy-funball",
                                                                "a table",
                                                                "a smelly fish",
                                                                "a chalice",
                                                                "a sword",
                                                                null,null,null,
                                                                "a 'pop'",
                                                                null,null,
                                                                "the magic doggie biscuit",
                                                                "a little fish",
                                                                null,null,
                                                                "a statue of RazorHawk",
                                                                null,null,null,
                                                                "Patrick",
                                                                null,
                                                                "a chair",
                                                                "a chair",
                                                                null,
                                                                "a green tonic",
                                                                "a portrait of an old man in underpants",
                                                                "a portrait of a young girl riding a llama",
                                                                null,null,
                                                                "a window",
                                                                "a window",
                                                                "a Big Mech",
                                                                "a Jumbo Mo",
                                                                "an innocent-looking little flower",
                                                                "a staircase",
                                                                "a pile of rotted bones",
                                                                "a goblet of blood",
                                                                "a bottle of hot sauce",
                                                                "a shrunken head",
                                                                "an herb",
                                                                "a crabby crab",
                                                                "a medicinal plant",
                                                                "a bottle of perfume",
                                                                "a bottle of blueberry jam",
                                                                "a bottle of ketchup",
                                                                "salmon extract",
                                                                "Jolt Cola(tm)",
                                                                "a bottle of ale",
                                                                "bloody knives",
                                                                "a green scroll",
                                                                "a dark blue scroll",
                                                                "a yellow scroll",
                                                                "a fountain",
                                                                null,
                                                                "a lizard egg",
                                                                null,null,null,null,null,null,null,
                                                                "a torch",
                                                                "a torch",
                                                                "a torch",
                                                                "a torch",
                                                                null,null,
                                                                "a rock",
                                                                "a shattered rock",
                                                                "a few stones",
                                                                null,null,null,
                                                                "a dead bunny",
                                                                "a squashed snail",
                                                                "a dead squirrel",
                                                                "a dead lizard",
                                                                "a fish skeleton",
                                                                "a stalagmite",
                                                                "a Golden Slayer",
                                                                "a Demon Slayer",
                                                                "a fake light saber",
                                                                "a club",
                                                                "silver coins",
                                                                null,null,null,
                                                                "a 6-pack of Grog Lite",
                                                                "a red scroll",
                                                                "a purple scroll",
                                                                "a light blue scroll",
                                                                "a pink scroll",
                                                                "a grey scroll",
                                                                "a silver scroll",
                                                                "a brown scroll",
                                                                "a Water Robe",
                                                                "Fruit Garb",
                                                                "a pea cap",
                                                                null,null,null,null,null,null,
                                                                "a walnut",
                                                                "a war hammer",
                                                                null,null,
                                                                "a shadow",
                                                                "venus fly traps",
                                                                "a book",
                                                                "a worm",
                                                                "a yellow potion",
                                                                null,null,
                                                                "*KRINK*",
                                                                null,
                                                                "Royal Armor",
                                                                null,
                                                                "a turtle shell",
                                                                "something you don't want to mess with",
                                                                "gloves",
                                                                "a Life Rune",
                                                                "a Spirit Rune",
                                                                "a Mind Rune",
                                                                "a Flesh Rune",
                                                                "a belt",
                                                                "a vase",
                                                                "a pillow",
                                                                "ashes",
                                                                "a bird nest",
                                                                "a crystal ball",
                                                                null,null,
                                                                "an evil-vampire-happy-funballoon",
                                                                "a flatty",
                                                                "the green pole and flag",
                                                                "the yellow pole and flag",
                                                                null,null,
                                                                "the green flag",
                                                                "the yellow flag",
                                                                null,null,
                                                                "a water bowl",
                                                                "poop",
                                                                "Seashell Armor",
                                                                "Turtleshell Armor",
                                                                "Wooden Armor",
                                                                "a Royal Sword",
                                                                "a Battle Axe",
                                                                "a Steel Mace",
                                                                "a Spiked Mace",
                                                                "a branch",
                                                                "a shovel",
                                                                "a red potion",
                                                                "a blue potion",
                                                                null,
                                                                "a jewel",
                                                                "a jewel",
                                                                "a jewel",
                                                                "a jewel",
                                                                null,null,null,null,
                                                                "a blue dodgeball",
                                                                "a green dodgeball",
                                                                "a red dogeball",
                                                                "a broken larva",
                                                                null,null,null,null,null,null,null,null,
                                                                "a scorpion tail",
                                                                null,null,null,null,null,null,null,
                                                                "ice cream",
                                                                "something scary.",
                                                                "mushrooms",
                                                                "something in the ground",
                                                                "a turnip",
                                                                "a gold key",
                                                                "a sack",
                                                                "a nest",
                                                                "a nest",
                                                                "a hermit crab",
                                                                null,
                                                                "a bow",
                                                                "a sling",
                                                                "a puddle",
                                                                "an old pile of bones with a necklace",
                                                                "an unfinished potion",
                                                                "a necklace",
                                                                "a crossbow",
                                                                "iron balls",
                                                                "a staff",
                                                                "a magical staff",
                                                                "a necklace",
                                                                "an Official Adventurer's Badge",
                                                                null,null,null,null,null,null,null,null,null,null,null,null,
                                                                "the Dead Stone",
                                                                null,
                                                                "the Dead Stone",
                                                                null,null,null,null,
                                                                "a scythe",
                                                                "a pole blade",
                                                                "a green potion",
                                                                "a purple potion",
                                                                null,
                                                                "the blue pole and flag",
                                                                null,
                                                                "the red pole and flag",
                                                                null,
                                                                "BeKaR's head",
                                                                "three arrows",
                                                                "a grey potion",
                                                                "a steel key",
                                                                "hands! Weird..",
                                                                "two arrows",
                                                                "an arrow",
                                                                "a Veggie Head",
                                                                "a Veggie Head",
                                                                "a red berry",
                                                                "a purple berry",
                                                                "an ant hill",
                                                                "an escaping skeleton",
                                                                null,
                                                                "a hole",
                                                                null,null,null,null,null,null,null,
                                                                "a lagbert",
                                                                null,null,null,null,null,null,null,null,null,null,null,
                                                                "a ghost",
                                                                "a well-kept plant",
                                                                "vines",
                                                                "an orange-shelled snail",
                                                                "an pile of snow",
                                                                "a blue-shelled snail",
                                                                "an explosion",
                                                                "metal spikes",
                                                                "lightning",
                                                                "a swirl of fire",
                                                                "mist",
                                                                "an explosed skeleton",
                                                                "exploding earth",
                                                                "food",
                                                                null,null,null,null,
                                                                "food",
                                                                "a spider necklace",
                                                                "a little dagger",
                                                                "a heart",
                                                                null,null,null,
                                                                "a web",
                                                                "a sword in a stone",
                                                                "a stump",
                                                                "a brown potion",
                                                                "a water spout",
                                                                null,
                                                                "some carpet",
                                                                "a secret entrance",
                                                                "a chrome cross",
                                                                "a quiver",
                                                                "a DragonSlayer",
                                                                null,null,null,
                                                                "some carpet",
                                                                null,
                                                                "a huge toadstool",
                                                                null,
                                                                "the snail mastah",
                                                                null,null,null,
                                                                "some really bad water",
                                                                "a poltergheist",
                                                                null,null,null,null,null,null,null,null,
                                                                "some petrified globlings",
																null,
																"the Foz",
                                                                null,
                                                                "a carpet",
                                                                "a carpet",
                                                                "a carpet",
                                                                "a carpet",
                                                                "a black cross",
                                                                "a gold cross"};
	final static String animnames[] = {"a bunny",
									"a snail",
									"a lizard",
									"a fish",
									"a squirrel",
									"a crow"};

	DragonSpiresFrame parent;
	DragonSpiresApplet dsapplet;
	DSMapCanvas map;
	DSLoginFrame dslf;
	PortraitEditorPanel pep;

	Font f,fs;
	FontMetrics fm,fms;

	private DataInputStream i;
	private PrintStream o;
	private Socket s;
	PrintStream logstream;
	Thread listen;

	Vector motdlines,/*spells,*/ignores;
	TextField namebox, passbox;
	Button login,help;
	TextArea desc;
	Choice[] asp;
	Choice sex;
	List mapfiles;

	Image marbled,preview,end,wood,typebuffimg,compass,titlei,gnb,ad;//,night;
	ImageProducer gnbp;
	Graphics ttg;

	boolean keyok,amapplet,focus,atmoveplace,mousemoveclick=false,didtt=false,log=false,showad=false,showperc=false;

	Color dsbg,dstext,dsbg2,darkbloo,greeny,bgcolors[];

	Toolkit toolkit;

	stuffThread2 sthread;

	String[] classwords = {"Armor","Shield","Plume",
				"Armor","Shield","Hair",
				"Armor","Shield","Horns/Boots",
				"Vest","Pants","Hair",
				"Robe","Detail","Tunic"};

	Color[] colorarray;

	String[] TextArray;
	private int[] lineColors;
	final String aligntexts[] = {"Demonic","Menacing","Bad","Neutral","Nice","Honorable","Heroic"};
	String line1,line2,line3,path,colorstring,connstat,name,name2draw="",tttext,mapupload="lev01.dsmap",replies[];

	int[] inventory;
	int weapon,armour,gold,keyhold,afeet,ahands,invsel,
		ledin = -3,
		facing = 1,
		hp = 20,maxhp=20,
		stam = 116,
		alignment = 3,
		versionok = -1,
		scrollpos = 8,
		mp = 30, maxmp = 30,
		//str,def,agi,dex,wiz=1,
		xdiff,ydiff,
		dirpoint=0,
		ttlx=0,ttly=0,
		server=0,
		replypos=-2,
		downloading=0;

	float currperc=0;

	int fx,fy;
	int wm,wx=1,am,ax=1;

	static final int	HAIR = 14,
						ARMOR = 12,
						ACC = 7;

	Graphics g4buff,g4panel;
	Image portrait=null,elbuff;
	ImageProducer[][] ppics = new ImageProducer[2][HAIR+ARMOR+1];
	ImageProducer[] accppics = new ImageProducer[ACC*2];
	char[] parray={' ',' ',' ',' ','!','!','!'},myparray={' ',' ',' ',' ','!','!','!'};
	//String[] dpstrings = {"!'+!"," &,#","!)#+"," \"!%","!,''"};

	int[] baseColorIndex;

	public DragonSpiresPanel(DragonSpiresFrame pa, String p, boolean ama, int serv, DragonSpiresApplet a) {


		//System.out.println("\n\nIt's okay to hide me but not close me! =)");
		setLayout(null);
		server=serv;

		toolkit = Toolkit.getDefaultToolkit();

		if (p.endsWith("."))
			p = p.substring(0,p.length()-1);

		path = p;

		amapplet = ama;
		if (ama)
			dsapplet = a;
		else {
			try {
				logstream = new PrintStream(new FileOutputStream("dslog.txt"),true);
				log=true;
			}
			catch (Exception e) {
			}
		}
			
		parent = pa;

		keyok = false;

		line1=line2=line3=connstat="";

		inventory = new int[35];
		replies = new String[5];
		for (int i=0;i<replies.length;i++)
			replies[i]="";

		//setBackground(new Color(179,207,235));
		bgcolors = new Color[3];
		bgcolors[0] = new Color(179,207,235);
		bgcolors[1] = new Color(255,255,200);
		bgcolors[2] = new Color(49,115,140);
		dsbg=bgcolors[0];
		dstext = new Color(0,0,55);
		dsbg2 = new Color(49,49,82);
		darkbloo = new Color(0,0,210);
		greeny = new Color(0,210,0);

		colorarray=new Color[11];
		colorarray[0]=dstext;
		colorarray[1]=new Color(0,0,234);
		colorarray[2]=new Color(234,0,0);
		colorarray[3]=new Color(240,120,0);
		colorarray[4]=new Color(128,128,0);
		colorarray[5]=new Color(0,128,0);
		colorarray[6]=new Color(0,128,255);
		colorarray[7]=Color.magenta;
		colorarray[8]=new Color(205,108,172);
		colorarray[9]=Color.gray;
		colorarray[10]=Color.darkGray;

		//spells = new Vector();
		ignores = new Vector();

		lineColors= new int[35];
		TextArray = new String[35];
		for (int i = 0; i < TextArray.length; i++)
			TextArray[i] = "";

		asp = new Choice[3];
		for (int i = 0; i < 3; i++) {
			asp[i] = new Choice();
			asp[i].addItem("Black");
			asp[i].addItem("Dark Blue");
			asp[i].addItem("Blue");
			asp[i].addItem("Olive");
			asp[i].addItem("Lime");
			asp[i].addItem("Grey");
			asp[i].addItem("Deep Red");
			asp[i].addItem("Light Red");
			asp[i].addItem("Purple");
			asp[i].addItem("Lavender");
			asp[i].addItem("Bronze");
			asp[i].addItem("Gold");
			asp[i].addItem("Silver");
			asp[i].addItem("White");
		}

		sex = new Choice();
		sex.addItem("Knight (m)");
		sex.addItem("Valkyrie (f)");
		sex.addItem("Warlord (m)");
		sex.addItem("Rogue (f)");
		sex.addItem("Druid");
		
		motdlines = new Vector();

		//end = toolkit.getImage(dsGetCodeBase("end.gif"));
		//marbled = toolkit.getImage(dsGetCodeBase("moborder.gif"));
		//wood = toolkit.getImage(dsGetCodeBase("wood.gif"));
		//compass = toolkit.getImage(dsGetCodeBase("compass.gif"));
		//night = toolkit.getImage(dsGetCodeBase("night.gif"));

		end = dsGetImage("end.gif");
		marbled = dsGetImage("moborder.gif");
		titlei = dsGetImage("title256.gif");
		wood = dsGetImage("wood.gif");
		compass = dsGetImage("compass.gif");
		gnb = dsGetImage("gnb.gif");
		gnbp = gnb.getSource();

		namebox = new TextField();
		passbox = new TextField();
		passbox.setEchoCharacter('*');
		login = new Button("OK");
		help = new Button("Help! I can't type in my name!");
		f = new Font("TimesRoman", Font.BOLD, pa.normalfont);
		fm = getFontMetrics(f);
		fs = new Font("TimesRoman", Font.BOLD, pa.smallfont);
		fms = getFontMetrics(fs);
		map = new DSMapCanvas(this);
		desc = new TextArea();

		map.move(233,21);

		map.resize(384,256);

		map.hide();
		//namebox.hide();
		//passbox.hide();
		//login.hide();
		//sex.hide();
		//desc.hide();

		add(map);
	}

	/**********************
	<Main Process Routines>
	**********************/

	public void start() {
		//if (!connectDS()) return;
		listen = new Thread(this);
		listen.start();
		Rectangle r = parent.bounds();
		fx=r.x;
		fy=r.y;
		g4panel=getGraphics();
		elbuff=this.createImage(640,480);
		g4buff=elbuff.getGraphics();
		map.g = map.getGraphics();
	}

	public void run() {
		connstat = "Trying to connect...";
		repaint(43,325,219,27);
		try {

			s = new Socket(habl[server], 7734);
			try {
				s.setSoLinger(true,0);
			}
			catch (Exception e) {}

			i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			o = new PrintStream(new BufferedOutputStream(s.getOutputStream()), true);

			connstat = "Connected.";
			repaint(43,325,219,27);

			doTracker();

			connstat = "Click screen to continue!";
			repaint(43,325,219,27);

			ledin = -2;
			//return true;
		}
		catch (Exception e) {
			//e.printStackTrace();
			connstat = "Can't connect. That sucks.";
			repaint(43,325,219,27);
			return;
		}

		try {
			String incoming;
			while ((incoming=i.readLine())!=null) {

				//String incoming = i.readLine();
				//System.out.println(incoming);

				if (ledin==1) {
					switch (incoming.charAt(0)) {
						case '<':
							map.placePlayer(incoming);
							continue;
						case '@':
							//updatePos(incoming.charAt(1)-32,incoming.charAt(2)-32);
							map.xpos = incoming.charAt(1)-32;
							map.ypos = incoming.charAt(2)-32;
							map.drawTiles = 1;
							map.justmoved=3;
							continue;
						case '#':
							updateStam(incoming.charAt(1)-32);
							continue;
						case '!':
							if (amapplet) {
								if (dsapplet.sound.ps.getState())
									dsapplet.sound.play(Integer.parseInt(incoming.substring(1,incoming.length())));
							}
							continue;
						case '>':
							map.placeItem(incoming);
							continue;
						case '%':
							updateFeet(map.decode(incoming.charAt(1)-32,incoming.charAt(2)-32));
							continue;
					}
				}

				try {
					switch (incoming.charAt(0)) {
						case '(':
						case '[':
							incomingText(incoming);
							continue;
					}
				}
				catch (Exception e) {}

				switch (ledin) {
					case 1:
						secondaryIncoming(incoming);
						break;
					case 0:
						checkPreLogin(incoming);
						break;
					default:
						checkIntro(incoming);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Got null, apparently.");
		stop();
	}

	public void secondaryIncoming(String incoming) {
		switch (incoming.charAt(0)) {
			case '$':
				statInput(incoming);
				break;
			case '*':
				addSpell(incoming);
				break;
			case '^':
				updateHands(map.decode(incoming.charAt(1)-32,incoming.charAt(2)-32));
				break;
			case 'i':
				updateInv(incoming.charAt(1)-32,map.decode(incoming.charAt(2)-32,incoming.charAt(3)-32));
				break;
			case 'e':
				updateEQ(incoming.charAt(1)-32,map.decode(incoming.charAt(2)-32,incoming.charAt(3)-32),incoming.charAt(4)-32,incoming.charAt(5)-32);
				break;
			case 's':
				switch (incoming.charAt(2)) {
					case ' ':
						showShop(incoming);
						break;
					case 'n':
						map.buffer_string = incoming.substring(3,incoming.length());
				}
				break;
			case 'c':
				switch (incoming.charAt(1)) {
					case 'd':
						map.requestOMDisplay(0);
						break;
					case 'i':
						inventory=new int[35];
						break;
					case 'o':
						colorstring = incoming.substring(6);
						updateGNB();
						char[] tarray = colorstring.toCharArray();
						System.arraycopy(tarray,0,myparray,0,3);
						parray=(char[])myparray.clone();
						//myparray=(colorstring.substring(0,3)+dpstrings[colorstring.charAt(3)-33]).toCharArray();
						portrait=assemblePortrait(parray);
						name2draw=name;
						//repaint(23,198,127,80);
						repaint(24,174,131,83);
				}
				break;
			case 'C':
				map.clearPlayerMap();
				break;
			case 'G':
				updateGold(incoming);
				break;
			case '~':
				haltMapDraw();
				break;
			case '=':
				resumeMapDraw();
				break;
			case 'a':
				if (incoming.startsWith("ad:")) {
					if (incoming.equals("ad:off")) {
						showad=false;
					}
					else {
						try {
							ad=toolkit.getImage(new URL(incoming.substring(3)));
							showad=true;
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					repaint();
				}
				else {
					alignment = incoming.charAt(2)-32;
					if (map.omdisplaymode==1)
						map.repaint();
				}
				break;
			case ']':
				if (incoming.equals("]uploadit"))
					uploadMap();
				else if (incoming.equals("]download")) {
					setupNewDownloadedMap();
					downloading=1;
					append("* Downloading map. Please wait. *",6);
					map.downloadx=0;
					map.downloady=0;
					currperc=0;
					showperc=true;
					repaint();
				}
				else if (incoming.equals("]fin")) {
					downloading=0;
					showperc=false;
					repaint();
					map.initMap();
				}
				else if (incoming.startsWith("]data")) {
					map.downloadMapData(incoming.substring(5));
					currperc=(float)map.downloadx/(float)map.mheight;
					if (downloading==2) currperc+=.5f;
					repaint(24,174,131,83);
				}
				else
					setupNewMap(incoming.substring(1));
				break;
			case  'P':
				if (incoming.charAt(1)=='Y') {
					myparray = (colorstring.substring(0,3)+incoming.substring(2)).toCharArray();
					updateGNB();
					parray=(char[])myparray.clone();
					name2draw=name;
					portrait=assemblePortrait(myparray);
					name2draw=name;
					repaint(24,174,131,83);
				}
				else {
					name2draw=incoming.substring(2,incoming.indexOf(' ',2)).replace('|',' ');
					portrait=assemblePortrait(incoming.substring(incoming.indexOf(' ',2)+1,incoming.length()).toCharArray());
					repaint(24,174,131,83);
				}
				break;
			case 'I':
				showInteraction(incoming.substring(1));
				break;
			case 'p':
				sendIt("pong");
				//System.out.println("Ping-Pong!");
			//default:
			//else if (incoming.startsWith("!") && ps.getState())
			//	aus[Integer.parseInt(incoming.substring(1,incoming.length()))].play();
		}
	}

	public void stop() {
		try {
			append("* Lost connection to DragonSpires.",2);
		}
		catch (Exception e) {}
		try {
			o.println("quit");
			o.flush();
		}
		catch (Exception e) {}
		if (listen != null) {
			listen.stop();
			listen = null;
		}
		if (sthread != null) {
			sthread.stop();
			sthread = null;
		}

		try {
			o.close();
			i.close();
			s.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (logstream!=null) logstream.close();
	}

	/***********************
	</Main Process Routines>
	***********************/

	/*************************
	<Primary Display Routines>
	*************************/

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (map.drawok >= 0) {
			if (mousemoveclick) {
				mousemoveclick=false;
				return;
			}
			g4buff.drawImage(marbled,0,0,this);
			if (showad) {
				map.g.drawImage(ad,0,0,this);
			}
			g4buff.drawImage(gnb,21,307,this);

			if (afeet > 0)
				g4buff.drawImage(map.getItem(afeet),54,431-map.itall[afeet],this);
			if (ahands > 0)
				g4buff.drawImage(map.getItem(ahands),70,296-map.itall[ahands],this);

			if (weapon > 0)
				g4buff.drawImage(map.getItem(weapon),36,296,this);
			if (armour > 0)
				g4buff.drawImage(map.getItem(armour),54,341,this);

			g4buff.setColor(dsbg);
			g4buff.fillRect(156,289,463,119);
			g4buff.setColor(dstext);
			g4buff.setFont(f);
			for (int spm = 0; spm < 9; spm++) {
				g4buff.setColor(colorarray[lineColors[scrollpos-spm]]);
				g4buff.drawString(TextArray[scrollpos-spm],157,287+(13*(spm+1)));
			}
			if (scrollpos > 8) {
				g4panel.setColor(colorarray[2]);
				g4panel.drawRect(154,287,466,122);		
			}

			drawStam(g4buff);
			drawHP(g4buff);

			drawGold(g4buff);

			drawMP(g4buff);

			drawTypeText(g4buff);

			if (portrait!=null)
				g4buff.drawImage(portrait,27,195,this);

			if (showperc) {
				g4buff.setColor(Color.green);
				g4buff.fillRect(27,176,(int)(127*currperc),12);
				g4buff.drawString("0%",20,173);
				g4buff.drawString("100%",146,173);
			}
			else {
				if (name!=null) {
					int strwid = fms.stringWidth(name2draw);
					int strtnme = 90-(strwid/2);
					g4buff.setColor(dsbg2);
					g4buff.fillRect(strtnme+1,176,strwid-2,11);
					g4buff.setColor(Color.yellow);
					g4buff.drawString(name2draw,strtnme,186);
				}
			}

			if (dirpoint!=0)
				drawDIRPoint(dirpoint,g4buff);

			if (didtt)
				drawttLike(g4buff);

			if (ledin==1) {
				if (!focus) {
					g4buff.setColor(Color.yellow);
					g4buff.drawString("*Warning* The window is not in focus. Click to regain focus.",3,14);
				}
			}
			else if (ledin < 1)
				preGamePaint(g4buff);				
		}
		else if (map.drawok!=-10)
			drawTitle(g4buff);

		g.drawImage(elbuff,0,0,this);

		if (atmoveplace) {
			g.setColor(Color.yellow);
			g.drawRect(0,0,639,479);
			g.drawRect(1,1,637,477);
		}
	}

	public void drawStam(Graphics g) {
		g.setColor(dsbg2);

		g.fillRect(187,19,13,116-stam);

		if (stam < 30)
			g.setColor(Color.red);
		else if (stam < 60)
			g.setColor(Color.yellow);
		else
			g.setColor(darkbloo);

		g.fillRect(187,19+(116-stam),13,stam);
	}

	public void drawHP(Graphics g) {
		float blah=(float)hp/(float)maxhp;
		int wobbly = (int)(116*blah);

		g.setColor(dsbg2);
		g.fillRect(211,19,13,116-wobbly);

		if (wobbly < 39)
			g.setColor(Color.red);
		else if (wobbly < 79)
			g.setColor(Color.yellow);
		else
			g.setColor(greeny);

		g.fillRect(211,19+(116-wobbly),13,wobbly);
	}

	public void drawGold(Graphics g) {
		g.setFont(fs);
		//g.setColor(dsbg2);
		//g.fillRect(160,256,39,11);
		g.setColor(new Color(130,130,0));
		String mybitch=""+gold;
		int acalc = 194-fms.stringWidth(mybitch)/2;
		//g.drawString(""+gold,196-fms.stringWidth(""+gold)/2,274);
		//g.drawString(""+gold,192-fms.stringWidth(""+gold)/2,272);
		g.fillRoundRect(acalc-1,262,fms.stringWidth(mybitch)+1,11,4,4);
		g.setColor(Color.black);
		g.drawString(mybitch,acalc+1,274);
		g.setColor(Color.green);
		g.drawString(mybitch,acalc,273);
	}

	public void drawMP(Graphics g) {
		String mybitch=mp+"/"+maxmp;
		int acalc=194-fms.stringWidth(mp+"/"+maxmp)/2;

		g.setFont(fs);
		//g.setColor(dsbg2);
		//g.fillRect(160,226,39,11);
		g.setColor(new Color(90,110,195));
		//g.drawString(mp+"/"+maxmp,196-fms.stringWidth(mp+"/"+maxmp)/2,247);
		//g.drawString(mp+"/"+maxmp,192-fms.stringWidth(mp+"/"+maxmp)/2,245);
		g.fillRoundRect(acalc-1,236,fms.stringWidth(mybitch)+1,11,4,4);

		//g.setColor(new Color(170,200,235));
		g.setColor(Color.black);
		g.drawString(mybitch,acalc+1,247);
		g.setColor(Color.green);
		g.drawString(mybitch,acalc,246);
	}

	public void drawTypeText(Graphics g) {
		ttg.setColor(dsbg);
		ttg.fillRect(0,0,463,43);
		ttg.setColor(dstext);
		ttg.setFont(f);
		ttg.drawString(line1,1,13);
		ttg.drawString(line2,1,27);
		ttg.drawString(line3,1,41);
		g.drawImage(typebuffimg,156,411,this);
	}

	public void preGamePaint(Graphics g) {
		switch (ledin) {
			case -1:
				int y = 33;
				g.setColor(dsbg);
				g.fillRect(233,21,384,256);
				g.setColor(dstext);
				g.setFont(new Font("Courier", Font.BOLD, parent.smallfont));
				for (int i = 0; i < motdlines.size(); i++) {
					g.drawString(motdlines.elementAt(i).toString(),237,y);
					y+=12;
				}
				break;
			case 0:
				g.setColor(Color.cyan);
				if (namebox.isShowing()) {
					g.drawString("Name:",290,65);
					g.drawString("Password:",270,95);
				}
				else if (asp[0].isShowing()) {
					g.drawString(classwords[sex.getSelectedIndex()*3]+":",331-fm.stringWidth(classwords[sex.getSelectedIndex()*3]+":"),52);
					g.drawString(classwords[sex.getSelectedIndex()*3+1]+":",331-fm.stringWidth(classwords[sex.getSelectedIndex()*3+1]+":"),89);
					g.drawString(classwords[sex.getSelectedIndex()*3+2]+":",331-fm.stringWidth(classwords[sex.getSelectedIndex()*3+2]+":"),129);
					g.drawString("Description:",256,165);
					try {
						g.drawImage(preview,460,45,this);
					}
					catch (NullPointerException e) {
					}
				}
		}
	}

	public void drawTitle(Graphics g) {
		try {
			g.setColor(Color.black);
			g.fillRect(43,325,219,27);
			//if (titlei==null)
			//	titlei=dsGetImage("title256.gif");
			g.drawImage(titlei,0,0,this);
			//g.drawImage(java.awt.Toolkit.getDefaultToolkit().getImage(dsGetCodeBase("title256.gif")),0,0,this);
			g.setFont(f);
			int x = 153-fm.stringWidth(connstat)/2;
			g.setColor(dsbg);
			g.drawString(connstat,x,342);
			//g.setColor(dsbg);
			//g.drawRect(x-5,422,fm.stringWidth(connstat)+10,19);
		}
		catch (NullPointerException e) {}
	}

	public void clearMoveBorder() {
		atmoveplace=false;
		g4panel.setColor(Color.black);
		g4panel.drawRect(0,0,639,479);
		g4panel.drawRect(1,1,637,477);
	}

	/**************************
	</Primary Display Routines>
	**************************/

	/*******************
	<Game Routines, etc>
	*******************/

	public InputStream dsOpenFile(String name) {
		//if (name.equals("download.dsmap"))
		//	return i;
		try {
			if (amapplet)
				return (dsGetCodeBase(name).openStream());
			else
				return (new FileInputStream(name));
		}
		catch (Exception e) {}
		return null;
	}

	public Image dsGetImage(String name) {
		if (amapplet)
			return (toolkit.getImage(dsGetCodeBase(name)));
		else
			return (toolkit.getImage(name));
	}

	public URL dsGetCodeBase(String file) {
		
		try {
			return new URL(path+file);
	 	}
		catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	public Image getRemap(char[] cs, ImageProducer i) {
		return createImage(new FilteredImageSource(i,new KnightFilter3(cs,baseColorIndex,map.colorindexes)));
	}

	private void sendIt(String message) {
		o.println(message);
		//o.flush();
	}

	private void versionCheckTime() {
		motdlines = null;
		repaint();
		if (versionok == 1) {
			namebox.move(335,40);
			passbox.move(335,75);
			login.move(335,110);
			help.move(290,140);
			namebox.setFont(f);
			passbox.setFont(f);
			login.setFont(fs);
			help.setFont(fs);
			namebox.resize(150,30);
			passbox.resize(150,30);
			login.resize(150,20);
			help.resize(235,20);
			add(namebox);
			add(passbox);
			add(login);
			add(help);
			namebox.show();
			passbox.show();
			login.show();
			help.show();
			namebox.requestFocus();
			ledin = 0;
			append("* DragonSpires Login",6);
			append("Enter your name and password.",0);
		}
		else {
			ledin = -100;
			append("The version of DragonSpires you're using is out of date.",3);
			append("Get the update at: http://stuff2do.systs.net/dspire",3);
			append("",0);
			append("If you're getting this message in your web browser, clear your browser's cache and restart your web browser.",3);
			append("",0);
		}
		versionok = 0;
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
		String[] ttt = {"floors","player images","enemies","items","animals","portraits","portraits","some more"};
		String[] tttn = {"floor.gif","player.gif","enemies.gif","item256.gif","animals.gif","fportrait.gif","mportrait.gif"};
		Image[] ttti = new Image[tttn.length];
		MediaTracker tracker = new MediaTracker(this);

		int i;
		for (i = 0; i < tttn.length; i++) {
			connstat = "Now loading "+ttt[i]+"...";
			repaint(43,325,219,27);
			ttti[i] = dsGetImage(tttn[i]);
			tracker.addImage(ttti[i],i);
			try {
				tracker.waitForID(i);
				while(!(tracker.checkID(i))){}
			}
			catch (InterruptedException e) {
			}
		}


		// Load some last stuff...
		tracker = new MediaTracker(this);
		connstat = "Now loading even more junk...";
		repaint(43,325,219,27);

		// Marbled
		tracker.addImage(marbled,0);
		tracker.addImage(end,1);
		tracker.addImage(wood,1);
		tracker.addImage(compass,1);
		try {
			tracker.waitForAll();
		}
		catch (InterruptedException e) {}


		connstat = "Processing...";
		repaint(43,325,219,27);

		map.flrsrc = ttti[0].getSource();

		PlayerImgSep pis = new PlayerImgSep(ttti[1], toolkit);
		map.enesrc = ttti[2].getSource();
		map.player = pis.playerSeperate();

		try {
			baseColorIndex = new int[16];
			Image ifff=createImage(new FilteredImageSource(map.player[1],new IndexCaptureFilter(baseColorIndex)));
			g4buff.drawImage(ifff,0,0,this);
			//for (int n=0;n<baseColorIndex.length;n++)
			//	System.out.println("["+n+"]: "+baseColorIndex[n]);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		ItemImgSep iis = new ItemImgSep(map,ttti[4],toolkit);
		map.itemsrc = ttti[3].getSource();

		loadPPics(ttti[5],ttti[6]);

		//pre-load floors
		/*for (int i = 0; i < map.floors.length; i++) {
			tracker.addImage(map.floors[i],i+tttn.length);
		}
		try {
			tracker.waitForAll();
			while(!(tracker.checkAll())){}
		}
		catch (InterruptedException e) {
		}*/

		//pre-load items
		/*for (int i = 0; i < map.items.length; i++) {
			tracker.addImage(map.items[i],i+tttn.length);
		}
		try {
			tracker.waitForAll();
			while(!(tracker.checkAll())){}
		}
		catch (InterruptedException e) {
		}*/
	}

	public void incomingText(String incoming) {

		int col=0;

		switch (incoming.charAt(0)) {
			case '(':
				incoming=incoming.substring(1);
				break;
			case '[':
				if (incoming.startsWith("[(~ ")) {
					if (incoming.endsWith(" to you.")) {
						String it=incoming.substring(4,incoming.indexOf(" whispers"));
						if (!replies[0].equals(it)) {
							System.arraycopy(replies,0,replies,1,4);
							replies[0]=it;
							for (int i=1;i<replies.length&&replies[i]!=null;i++) {
								if (replies[i].equals(it)) {
									for (int n=i;n<replies.length-1;n++)
										replies[n]=replies[n+1];
									replies[replies.length-1]="";
									break;
								}
							}
						}
					}
				}
				col=incoming.charAt(1)-32;
				incoming=incoming.substring(2);
		}

		for (int i = 0; i < ignores.size(); i++) {
			//System.out.println(ignores.elementAt(i).toString());
			
			if (incoming.toLowerCase().startsWith(ignores.elementAt(i).toString()))
				return;
		}

		append(incoming,col);
	}

	/*private void updatePos(int x, int y) {
		map.xpos = x;
		map.ypos = y;
		map.drawTiles = 1;
		map.justmoved=true;
	}*/

	public void updateStam(int value) {
		stam=value;
		drawStam(g4panel);
	}

	public void updateFeet(int item) {
		afeet = item;
		repaint(54,397,62,64);
	}

	public void statInput(String incoming) {
		switch (incoming.charAt(1)) {
			case 'H':
				updateHP(incoming);
				if (map.omdisplaymode==1)
					map.repaint();
				break;
			case 'M':
				mp = incoming.charAt(2)-32;
				//drawMP(g4panel);
				magicGoldRepaint();
				if (map.omdisplaymode==1)
					map.repaint();
				break;
			/*case 'S':
				str = Integer.parseInt(incoming.substring(2,incoming.length()))+1;
				break;
			case 'D':
				def = Integer.parseInt(incoming.substring(2,incoming.length()))+1;
				break;
			case 'A':
				agi = Integer.parseInt(incoming.substring(2,incoming.length()))+1;
				break;
			case 'X':
				dex = Integer.parseInt(incoming.substring(2,incoming.length()))+1;
				break;
			case 'W':
				wiz = Integer.parseInt(incoming.substring(2,incoming.length()))+1;
				break;*/
		}
	}

	public void updateHP(String incoming) {
		hp = Integer.parseInt(incoming.substring(2,incoming.length()));
		drawHP(g4panel);
		//repaint(158,14,21,103);
	}

	public void addSpell(String incoming) {
		//spells.addElement(incoming.charAt(1)+""+incoming.charAt(2)+""+incoming.charAt(3));
		//spells.addElement(new Spell(incoming.charAt(1)-32,incoming.charAt(2)-32,incoming.charAt(3)-32));
		//int x=incoming.charAt(1)-32,y=incoming.charAt(2)-32,s=incoming.charAt(3)-32;
		//spells.addElement(new Spell(x,y,0));
		//map.spellmap[x][y]=(byte)(s+1);
		//map.drawDirty(x,y);
		//map.repaint();
		new Spell(incoming.charAt(1)-32,incoming.charAt(2)-32,map.decode(incoming.charAt(3)-32,incoming.charAt(4)-32),map);
	}

	public void updateHands(int item) {
		ahands = item;
		//repaint(73,273,43,86);
		equipmentRepaint();
	}
	public void updateInv(int place, int item) {
		inventory[place] = item;
		if (map.omdisplaymode == 2)
			map.repaint();
		//repaint(14,343,131,65);
	}
	public void updateEQ(int type, int item, int m, int x) {
		switch (type) {
			case 0:
				weapon = item;
				wm=m;
				wx=x;
				//repaint(19,298,32,32);
				//equipmentRepaint();
				break;
			case 1:
				armour = item;
				am=m;
				ax=x;
				//repaint(127,298,32,32);
				//equipmentRepaint();
		}
		equipmentRepaint();
		if (map.omdisplaymode==1)
			map.repaint();
	}

	public void showShop(String incoming) {
		incoming = incoming.substring(3,incoming.length());
		StringTokenizer st = new StringTokenizer(incoming, "\t");

		if (map.inshop==-1)
			map.requestOMDisplay(3);

		map.inshop = Integer.parseInt(st.nextToken());
		if (map.inshop > 0) {
			for (int i = 0; i < 7; i++) {
				if (st.hasMoreTokens()) {
					String tempo = st.nextToken();
					map.shopitems[i][0] = map.decode(tempo.charAt(0)-32,tempo.charAt(1)-32);
					map.shopitems[i][1] = map.decode(tempo.charAt(2)-32,tempo.charAt(3)-32);
					map.shopitems[i][2] = Integer.parseInt(tempo.substring(4,tempo.length()));
				}
				else {
					map.shopitems[i][0] = -1;
					map.shopitems[i][1] = -1;
				}
			}
		}
		map.drawOMDisplay(map.getGraphics(),true);
	}

	public void clearShop(int blah) {
		map.inshop = -1;
		map.buffer_string = null;
		if (blah!=0)
			sendIt("csh");
	}
	public void updateGold(String incoming) {
		gold = Integer.parseInt(incoming.substring(1,incoming.length()));
		//drawGold(g4panel);
		magicGoldRepaint();
		if (map.omdisplaymode==1)
			map.repaint();
	}

	public void haltMapDraw() {
		//keyok = false;
		map.drawok = 0;
		map.repaint();
	}
	public void resumeMapDraw() {
		//keyok = true;
		map.drawok = 1;
		map.drawTiles=1;
		map.repaint();
	}
	public void setupNewMap(String name) {
		keyok=true;
		map.g.drawImage(marbled,-233,-21,map);
		map.g.setColor(colorarray[6]);
		map.g.setFont(fs);
		map.getMapData(name+".dsmap");
		//Object thisplayer = ((Vector)map.playercache.get(colorstring)).clone();
		//map.playercache.clear();
		//map.playercache.put(colorstring,thisplayer);
		System.gc();
		map.initMap();
	}

	public void setupNewDownloadedMap() {
		keyok=true;
		map.g.drawImage(marbled,-233,-21,map);
		map.g.setColor(colorarray[6]);
		map.g.setFont(fs);
	}

	public void checkPreLogin(String incoming) {
		if (incoming.equals("&")) {

			if (dslf!=null) {
				dslf.dispose();
				dslf = null;
			}
			ledin = 1;
			map.show();
			append("Welcome to DragonSpires!  * Press Ctrl-Z for help. *",1);
			remove(login);
			login = null;
			remove(namebox);
			namebox = null;
			remove(passbox);
			passbox = null;
			remove(help);
			help = null;
			try {
				for (int i = 0; i < 3; i++) {
					remove(asp[i]);
					//asp[i] = null;
				}
				asp = null;
				remove(sex);
				sex = null;
				remove(desc);
				desc = null;
				//preview.flush();
				preview = null;
			}
			catch(NullPointerException e) {
			}
			classwords = null;
			connstat = null;
			titlei.flush();
			titlei = null;
			System.gc();
			sthread = new stuffThread2(this);
			sthread.start();
			requestFocus();
		}
		else if (incoming.startsWith("color")) {
			colorstring = incoming.substring(6);
			//char[] tarray = colorstring.toCharArray();
			//System.arraycopy(tarray,0,myparray,0,3);
			//parray=(char[])myparray.clone();
			//portrait=assemblePortrait(parray);
			//repaint(23,217,38,61);
		}
		else if (incoming.equals("cs")) {
			//login.hide();
			namebox.hide();
			passbox.hide();
			if (dslf!=null) {
				dslf.dispose();
				dslf=null;
				add(login);
			}
			for (int i = 0; i < 3; i++) {
				asp[i].resize(100,20);
				asp[i].setFont(fs);
				add(asp[i]);
				asp[i].show();
				asp[i].move(340,35+40*i);
			}
			sex.move(460,115);
			sex.setFont(fs);
			desc.move(340,155);
			sex.resize(125,20);
			desc.resize(245,85);
			add(sex);
			desc.setFont(fs);
			add(desc);
			sex.show();
			desc.show();
			help.setLabel("Help! I can't type in my description!");
			login.resize(100,20);
			help.resize(225,20);
			help.move(365,243);
			login.move(255,243);
			login.enable();
			namebox.enable();
			passbox.enable();
			help.enable();
			append("* Character Creation",6);
			append("Choose your colors and enter your description.",0);
			//random colors
			asp[0].select((int)Math.round(Math.random() * (asp[0].countItems()-1)));
			asp[1].select((int)Math.round(Math.random() * (asp[1].countItems()-1)));
			asp[2].select((int)Math.round(Math.random() * (asp[2].countItems()-1)));
			sex.select((int)Math.round(Math.random() * (sex.countItems()-1)));
			action(new Event(sex,Event.ACTION_EVENT,sex),sex);
		}
		else if (incoming.startsWith("N")) {
			append(incoming.substring(1),3);
			login.enable();
			namebox.enable();
			passbox.enable();
			help.enable();
			if (dslf!=null)
				dslf.requestFocus();
		}
		else if (incoming.startsWith("p"))
			sendIt("pong");
	}

	private void checkIntro(String incoming) {
		if (incoming.equals("ping")) {
			sendIt("pong");
			return;
		}
		if (!(incoming.endsWith("Dragonroar!") || incoming.startsWith("V"))) {
			motdlines.addElement(incoming);
			System.out.println(incoming);
			if (ledin == -1)
				repaint();
		}
		else if (incoming.startsWith("V0")) {
			if (incoming.equals(new String(version)))
				versionok = 1;
			else
				versionok = 0;
		}
	}

	public boolean dsPinR(int x, int y) {
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

	public void doIgnore(String ignoreit) {
		ignoreit = ignoreit.substring(8,ignoreit.length()).toLowerCase();
		if (ignoreit.endsWith("|"))
			ignoreit = ignoreit.substring(0,ignoreit.length()-1);

		if (ignoreit.equals("off")) {
			ignores.removeAllElements();
			append("All ignores off.",4);
			return;
		}

		for (int i = 0; i < ignores.size(); i++) {
			if (ignoreit.equals(ignores.elementAt(i).toString())) {
				ignores.removeElementAt(i);
				append("You are no longer ignoring lines starting with \""+ignoreit+"\".",4);
				return;
			}
		}

		ignores.addElement(ignoreit);
		append("You are now ignoring lines starting with \""+ignoreit+"\".",4);
	}

	public void loadPPics(Image fi, Image mi) {
		int total=HAIR+ARMOR+1+ACC,part;
		int[] coords={
					//F
					1,1,35,61,
					37,1,23,22,
					61,1,24,19,
					86,1,21,18,
					108,1,26,24,
					135,1,24,21,
					160,1,28,22,
					189,1,21,24,
					211,1,23,22,
					235,1,29,22,
					265,1,22,26,
					288,1,23,32,
					312,1,24,22,
					337,1,23,22,
					361,1,27,19,
					37,26,35,61,
					73,26,35,61,
					109,26,35,61,
					145,26,35,61,
					181,26,35,61,
					217,26,35,61,
					253,34,35,61,
					289,34,35,61,
					325,34,35,61,
					361,34,35,61,
					1,63,35,61,
					37,88,35,61,
					73,88,31,44,
					105,88,31,41,
					137,88,32,44,
					170,88,33,41,
					204,88,28,31,
					233,96,30,52,
					264,96,35,52,

					//M
					1,1,32,57,
					34,1,23,22,
					58,1,22,24,
					81,1,22,24,
					104,1,21,18,
					126,1,21,25,
					148,1,21,33,
					170,1,24,19,
					195,1,23,24,
					219,1,33,24,
					253,1,23,24,
					277,1,23,25,
					301,1,23,25,
					325,1,26,19,
					352,1,26,20,
					34,27,32,57,
					67,27,32,57,
					100,27,32,57,
					170,27,32,57,
					203,27,32,57,
					236,27,32,57,
					269,27,32,57,
					302,27,32,57,
					335,27,34,57,
					1,59,32,57,
					133,59,32,57,
					34,85,32,57,
					67,85,36,44,
					170,85,35,61,
					206,85,30,44,
					237,85,30,41,
					268,85,28,31,
					297,85,34,52,
					332,85,30,58
		};
		
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(fi,0);
		mt.addImage(mi,1);
		try {
			mt.waitForAll();
		}
		catch (Exception e) {}

		ImageProducer[] ips = {fi.getSource(),mi.getSource()};

		for (int p=0;p<2;p++) {
			for(int i=0;i<total-ACC;i++) {
				part=(p*total*4)+(4*i);
				ppics[p][i]=doCropIP(ips[p],coords[part],coords[part+1],coords[part+2],coords[part+3]);
			}
		}
		for (int p=0;p<2;p++) {
			for(int i=total-ACC,c=0;i<total;i++,c++) {
				part=(p*total*4)+(4*i);
				accppics[c+(p*ACC)]=doCropIP(ips[p],coords[part],coords[part+1],coords[part+2],coords[part+3]);
			}
		}
	}

	public ImageProducer doCropIP(ImageProducer ip, int x, int y, int w, int h) {
		return new FilteredImageSource(ip, new CropImageFilter(x,y,w,h));
	}

	public Image assemblePortrait(char[] pstring) {
		Graphics g;
		Image[] stuff = new Image[4];
		Image portrait;
		int tint=pstring[3]-32;

		portrait = createImage(36,61);
		g = portrait.getGraphics();
		g.setColor(bgcolors[2]);
		g.fillRect(0,0,36,61);

		stuff[0] = getRemap(pstring,ppics[tint][0]);
		if (pstring[4]!=' ')
			stuff[1] = getRemap(pstring,ppics[tint][1+pstring[4]-33]);
		if (pstring[5]!=' ')
			stuff[2] = getRemap(pstring,ppics[tint][1+HAIR+pstring[5]-33]);
		if (pstring[6]!=' ')
			stuff[3] = getRemap(pstring,accppics[pstring[6]-33]);

		MediaTracker mt = new MediaTracker(this);

		for (int i=0;i<4;i++) {
			if (stuff[i]!=null)
				mt.addImage(stuff[i],i);
		}

		try { mt.waitForAll(); } catch (Exception e) {}

		if (stuff[0]!=null)
			g.drawImage(stuff[0],0,0,this);
		if (stuff[2]!=null)
			g.drawImage(stuff[2],0,0,this);
		if (stuff[1]!=null)
			g.drawImage(stuff[1],0,0,this);
		if (stuff[3]!=null)
			g.drawImage(stuff[3],0,0,this);

		//for (int i=0;i<4;i++) {
		//	if (stuff[i]!=null)
		//		g.drawImage(stuff[i],0,0,this);
		//}

		//portrait = getRemap(parray,portrait.getSource());
		//portrait = createImage(new FilteredImageSource(portrait.getSource(),new PowerKnightFilter(parray)));
		//repaint();
		return portrait;
	}

	public void drawDIRPoint(int type, Graphics g) {
		g.setColor(new Color(200,0,0));
		switch (type) {
			case 1: //LL
				for (int x=232, l=18;x>=229;x--,l+=2)
					g.drawLine(x,280,x,280-l);
				for (int y=277, l=14;y<=280;y++,l+=2)
					g.drawLine(233,y,233+l,y);
				break;
			case 2: //LR
				for (int x=617, l=18;x<=620;x++,l+=2)
					g.drawLine(x,280,x,280-l);
				for (int y=277, l=14;y<=280;y++,l+=2)
					g.drawLine(616,y,616-l,y);
				break;
			case 3: //UL
				for (int x=232, l=18;x>=229;x--,l+=2)
					g.drawLine(x,17,x,17+l);
				for (int y=20, l=14;y>=17;y--,l+=2)
					g.drawLine(233,y,233+l,y);
				break;
			case 4: //UR
				for (int x=617, l=18;x<=620;x++,l+=2)
					g.drawLine(x,17,x,17+l);
				for (int y=20, l=14;y>=17;y--,l+=2)
					g.drawLine(616,y,616-l,y);
				break;
			case 5:
				g.fillRect(229,122,4,50);
				break;
			case 6:
				g.fillRect(617,122,4,50);
		}
	}

	public void repaintDIRPoint(int type) {
		switch (type) {
			case 1:
				repaint(229,257,24,24);
				break;
			case 2:
				repaint(597,257,24,24);
				break;
			case 3:
				repaint(229,17,24,24);
				break;
			case 4:
				repaint(597,17,24,24);
				break;
			case 5:
				repaint(229,103,232,153);
				break;
			case 6:
				repaint(618,103,621,153);
		}
	}

	public void mapMouseDown(int x, int y) {
		mousemoveclick=true;
		/*if (x < 192) {
			if (y < 128) {
				sendIt("m 7");
				facing = 7;
			}
			else {
				sendIt("m 1");
				facing = 1;
			}
		}
		else {
			if (y < 128) {
				sendIt("m 9");
				facing = 9;
			}
			else {
				sendIt("m 3");
				facing = 3;
			}
		}*/
		switch (dirpoint) {
			case 1:
				sendIt("m 1");
				facing=1;
				break;
			case 2:
				sendIt("m 3");
				facing=3;
				break;
			case 3:
				sendIt("m 7");
				facing=7;
				break;
			case 4:
				sendIt("m 9");
				facing=9;
				break;
			case 5:
				insPress();
				break;
			case 6:
				delPress();
		}
	}

	public void closePep() {
		remove(pep);
		pep=null;
		sendIt("P"+myparray[3]+myparray[4]+myparray[5]+myparray[6]);
		portrait=assemblePortrait(myparray);
		name2draw=name;
		//repaint(23,198,127,80);
		requestFocus();
	}

	public void showInteraction(String data) {
		map.requestOMDisplay(4);
		StringTokenizer st = new StringTokenizer(data,";");
		map.buffer_string = st.nextToken();
		map.buffer_string_array = new String[25];
		for (int i=0; st.hasMoreTokens(); i++)
			map.buffer_string_array[i] = st.nextToken();
		map.drawOMDisplay(map.getGraphics(),true);
	}

	/********************
	</Game Routines, etc>
	********************/

	/**********************
	<System Event Routines>
	**********************/

	public boolean keyUp(Event e, int key) {
		keyhold = 0;
		return true;
	}

	public boolean keyDown(Event e, int key) {
		//append(""+key,0);
		//boolean retval = true;

		//if (map.omdisplaymode == 1)
		//	map.requestOMDisplay(0);

		if (keyok) {
			char c = (char) e.key;
			if (replypos != -2) {
				if (c == '/'&&replies[0].length()!=0) {
					replypos++;
					if (replypos>=replies.length || replies[replypos].length()==0)
						replypos = 0;
					line1="/"+replies[replypos]+" |";
					drawTypeText(g4panel);
					return true;
				}
				else
					replypos = -2;
			}

			if (key > 31 && key < 127) {
				append(c);
				return true;
			}
			else {
				switch (c) {
					//case '\b':
					case 8:
					case 65288:
						backspace();
						return true;
					case '\n':
						enterPress();
						return true;
					case Event.UP:
						if (upPress(e))
							return true;
					case Event.PGUP:
						if (!checkStamAndKeyHold())
							return true;
						sendIt("m 9");
						facing = 9;
						return true;

					case Event.DOWN:
						if (downPress(e))
							return true;
					case Event.END:
						if (!checkStamAndKeyHold())
							return true;
						sendIt("m 1");
						facing = 1;
						return true;

					case Event.LEFT:
						if (leftPress(e))
							return true;
					case Event.HOME:
						if (!checkStamAndKeyHold())
							return true;
						sendIt("m 7");
						facing = 7;
						return true;

					case Event.RIGHT:
						if (rightPress(e))
							return true;
					case Event.PGDN:
						if (!checkStamAndKeyHold())
							return true;
						sendIt("m 3");
						facing = 3;
						return true;
					case Event.INSERT:
						if (!checkStamAndKeyHold())
							return true;
						insPress();
						return true;
					//case Event.DELETE:
					case 127:
					case 65535:
						if (!checkStamAndKeyHold())
							return true;
						delPress();
						return true;
					case Event.TAB:
						if (!checkStamAndKeyHold())
							return true;
						sendIt("sw");//ing");
						return true;
					default:
						if (e.controlDown()) {
							controlKeyDown(key);
							return true;
						}

				}
			}				
		}
		else if (ledin == -1 && versionok != -1) {
			versionCheckTime();
			return true;
		}
		if (e.controlDown()) {
			if (key==17)
				parent.haltIt();
			//else if (key==6)
			//	parent.switchWindow();				
			return true;
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

	public void append(char c) {
		if (line3.equals("")) {
			if (line2.equals("")) {
				if (line1.equals("")) {
					if (c == '/')// {
						replypos=-1;
						//append("replypos set to -1",0);
					//}
					line1 = line1 + c + "|";
				}
				else if (fm.stringWidth(line1 + c + "|") <= 463)
					line1 = line1.substring(0, line1.length()-1) + c + "|";
				else {
					line1 = line1.substring(0, line1.length()-1);
					line2 = line2 + c + "|";
				}
			}
			else if (fm.stringWidth(line2 + c + "|") <= 463)
				line2 = line2.substring(0, line2.length()-1) + c + "|";
			else {
				line2 = line2.substring(0, line2.length()-1);
				line3 = line3 + c + "|";
			}
		}
		else if (fm.stringWidth(line3 + c + "|") <= 463)
			line3 = line3.substring(0, line3.length()-1) + c + "|";




		/*if (line1 != "" && fm.stringWidth(line1 + c + "|") <= 463 && line2.equals("")) {
			line1 = line1.substring(0, line1.length()-1) + c + "|";
		}
		else if (fm.stringWidth(line1 + c + "|") <= 463 && line2.equals("")) {
			line1 = line1 + c + "|";
		}
		else if (line2 != "" && fm.stringWidth(line2 + c + "|") <= 463) {
			line2 = line2.substring(0, line2.length()-1) + c + "|";
		}
		else if (fm.stringWidth(line2 + c + "|") <= 463) {
			line1 = line1.substring(0, line1.length()-1);
			line2 = line2 + c + "|";
		}*/
		drawTypeText(g4panel);
	}

	public void backspace() {
		if (line3 != "") {
			try {
				line3 = line3.substring(0,line3.length()-2) + "|";
				if (line3.equals("|"))
					line3 = "";
			}
			catch(StringIndexOutOfBoundsException e) {
				line3 = "";
			}
			//repaint(162,372,360,26);
		}
		else if (line2 != "") {
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
		drawTypeText(g4panel);
	}

	private void enterPress() {
		if (line1.startsWith("-")) {
			if (checkLocalExtendedCommands()) {
				OBclear();
				return;
			}
		}

		if (ledin == 1 && line1.trim() != "") {
			if (line2.equals(""))
				line1 = line1.substring(0,line1.length()-1);
			else if (line3.equals(""))
				line2 = line2.substring(0,line2.length()-1);
			else
				line3 = line3.substring(0,line3.length()-1);

			sendIt('"'+line1+line2+line3);
		}
		OBclear();
	}

	private boolean checkLocalExtendedCommands() {
		String tochk=line1.toLowerCase().substring(1,line1.length()-1);
		if (tochk.equals("stats"))
			map.requestOMDisplay(1);
		else if (tochk.startsWith("ignore"))
			doIgnore(line1);
		else if (tochk.startsWith("ping"))
			new DSPing(this,habl);
		else if (tochk.equals("help"))
			new DSHelpFrame(this);
		else if (tochk.equals("quit"))
			parent.haltIt();
		else if (tochk.equals("who"))
			sendIt("who");
		else if (tochk.equals("look")) {
			//if (map.lookit())
					sendIt("look");
		}
		else if (tochk.equals("map"))
			initMapBox();
		else if (tochk.equals("get"))
			sendIt("get");
		else if (tochk.equals("arm"))
			sendIt("wr");
		else if (tochk.equals("wep"))
			sendIt("eq");
		else if (tochk.equals("inv")) {
			if (map.omdisplaymode != 2)
				map.requestOMDisplay(2);
			else
				map.requestOMDisplay(0);
		}
		else if (tochk.equals("use")) {
			if (ahands != 0)
				sendIt("u");
		}
		else if (tochk.equals("throw")) {
			if (ahands != 0)
				sendIt("ti");
		}
		else if (tochk.equals("rest"))
			sendIt("rest");
		else if (tochk.equals("swp"))
			sendIt("is"+(char)(invsel+32));
		else if (tochk.equals("compass")) {
			map.drawcompass = !map.drawcompass;
			map.drawTiles=1;
			map.repaint();
		}
		else if (tochk.equals("shop")) {
			if (map.omdisplaymode != 3)
				sendIt("sh");
			else
				map.requestOMDisplay(-1);
		}
		else if (tochk.equals("clear"))
			OBclear();
		else if (tochk.equals("pedit")) {
			if (pep==null) {
				pep = new PortraitEditorPanel(this);
				pep.move(36,77);
				pep.resize(107,93);
				add(pep);
			}
		}
		else if (tochk.equals("snaptext"))
			new DSTextFrame(this);
		else if (tochk.equals("cls")) {
			for (int i=0;i<TextArray.length;i++) {
				TextArray[i]="";
			}
			repaint(156,289,463,119);
		}
		else
			return false;
		return true;
	}

	public void OBclear() {
		line1 = "";
		line2 = "";
		line3 = "";
		//repaint(162,372,360,26);
		drawTypeText(g4panel);
	}

	private boolean checkStamAndKeyHold() {
		if (keyhold > 0) {
			keyhold--;
			return false;
		}
		if (stam <= 0) return false;
		keyhold = 3;
		drawStam(g4panel);
		return true;
	}

	private boolean upPress(Event e) {
		if (e.shiftDown()) {
			scrollUp();
			return true;
		}
		else if (map.omdisplaymode == 2) {
			//sendIt("is"+(char)(invsel+32));
			invsel--;
			for (int i = -1; i < inventory.length; i+=5) {
				if (invsel == i) {
					invsel+=5;
					break;
				}
			}
			map.repaint();
			return true;
		}
		return false;
	}

	private boolean downPress(Event e) {
		if (e.shiftDown()) {
			scrollDown();
			return true;
		}
		else if (map.omdisplaymode == 2) {
			//sendIt("is"+(char)(invsel+32));
			invsel++;
			for (int i = inventory.length; i > 0; i-=5) {
				if (invsel == i) {
					invsel-=5;
					break;
				}
			}
			map.repaint();
			return true;
		}
		return false;
	}

	private boolean leftPress(Event e) {
		if (map.omdisplaymode == 2) {
			invsel-=5;
			if (invsel < 0)
				invsel = inventory.length+invsel;
			//repaint(14,343,131,65);
			map.repaint();
			return true;
		}
		return false;
	}

	private boolean rightPress(Event e) {
		if (map.omdisplaymode == 2) {
			invsel+=5;
			if (invsel >= inventory.length)
				invsel = 0+(invsel-inventory.length);
			//repaint(14,343,131,65);
			map.repaint();
			return true;
		}
		return false;
	}

	private void insPress() {
		sendIt("<");
		switch (facing) {
			case 7:	facing = 1;
					break;
			case 9:	facing = 7;
					break;
			case 1:	facing = 3;
					break;
			case 3:	facing = 9;
		}
	}

	private void delPress() {
		sendIt(">");
		switch (facing) {
			case 7:	facing = 9;
					break;
			case 9:	facing = 3;
					break;
			case 1:	facing = 7;
					break;
			case 3:	facing = 1;
		}
	}

	private void controlKeyDown(int key) {
		switch (key) {
			case 26:	new DSHelpFrame(this);
					break;
			case 17:	parent.haltIt();
					break;
			case 16:	sendIt("who");
					break;
			case 12:	//if (map.lookit())
							sendIt("look");
					break;
			case 13:
				initMapBox();
				break;
			case 7:
					sendIt("get");
					break;
			case 1:	sendIt("wr");
					break;
			case 23:	sendIt("eq");
					break;
			case 22:
					if (map.omdisplaymode != 2)
						map.requestOMDisplay(2);
					else
						map.requestOMDisplay(0);
					//repaint(14,343,131,65);
					break;
			case 21:	if (ahands != 0)
						sendIt("u");
					break;
			case 20:	if (ahands != 0)
					sendIt("ti");
					break;
			case 18:	sendIt("rest");
					break;
			/*case 13:
				Runtime r = Runtime.getRuntime();
				System.out.println(""+((float)((float)r.freeMemory()/(float)r.totalMemory()))*100);
				break;*/
			case 3:
				if (map.omdisplaymode == 2)
					sendIt("is"+(char)(invsel+32));
				else {
					map.drawcompass = !map.drawcompass;
					map.drawTiles=1;
					map.repaint();
				}
				break;
			case 19:
					if (map.omdisplaymode != 3)
						sendIt("sh");
					else
						map.requestOMDisplay(-1);
					break;
			case 24:
				OBclear();
				break;					
			case 5:
				if (pep==null) {
					pep = new PortraitEditorPanel(this);
					pep.move(36,77);
					pep.resize(107,93);
					add(pep);
				}
				//else {
				//	remove(pep);
				//	pep=null;
				//}
				break;
		}
	}

	public boolean mouseDown(Event e, int x, int y) {
		if (ledin == -2) {
			map.drawok = 0;
			typebuffimg = createImage(463,43);
			ttg = typebuffimg.getGraphics();
			ledin = -1;
			g4panel.setColor(new Color(49,49,82));
			g4panel.fillRect(0,0,size().width,size().height);
			repaint();
			//colors.show();
			//ps.show();
			return true;
		}
		else if (ledin == -1 && versionok != -1)
			versionCheckTime();
		else if (x>=71&&x<=154&&y>=195&&y<=256) { // 71,195|154,256
			if (x>=74&&x<=109&&y>=196&&y<=208) { //Background
				if (dsbg==bgcolors[0])
					dsbg=bgcolors[1];
				else if (dsbg==bgcolors[1])
					dsbg=bgcolors[2];
				else
					dsbg=bgcolors[0];
				repaint();
			}
			else if (x>=74&&x<=109&&y>=211&&y<=223) { //Snap
				new DSTextFrame(this);
			}
			else if (x>=115&&x<=148&&y>=226&&y<=238) { //Stats
				map.requestOMDisplay(1);
			}
			else if (x>=115&&x<=148&&y>=241&&y<=253) { //Help
				new DSHelpFrame(this);
			}
			else if (x>=74&&x<=109&&y>=226&&y<=238) { //cls
				for (int i=0;i<TextArray.length;i++) {
					TextArray[i]="";
				}
				repaint(156,289,463,119);
			}
			else if (x>=115&&x<=148&&y>=211&&y<=223) { //brb
				sendIt("\"-brb");
			}
		}
		else if (x>=135&&x<=152&&y>=382&&y<=391) {
			scrollUp();
		}
		else if (x>=135&&x<=152&&y>=394&&y<=403) {
			scrollDown();
		}
		else
				requestFocus();

		xdiff=x;
		ydiff=y;

		return false;
	}

	public boolean mouseMove(Event e, int x, int y) {
		if (x<11||y<11||x>629||y>469) {
			if (!atmoveplace) {
				atmoveplace=true;
				g4panel.setColor(Color.yellow);
				g4panel.drawRect(0,0,639,479);
				g4panel.drawRect(1,1,637,477);
			}
		}
		else if (x>=51 && y>=296 && x<=83 && y<=328)
			ttLike(itemnames[weapon],x,y);
		else if (x>=85 && y>=296 && x<=117 && y<=328)
			ttLike(itemnames[ahands],x,y);
		else if (x>=69 && y>=341 && x<=101 && y<=373)
			ttLike(itemnames[armour],x,y);
		else if (x>=57 && y>=431 && x<=116 && y<=461)
			ttLike(itemnames[afeet],x,y);
		else if (x>=24 && y>=12 && x<=144 && y<=171)
			ttLike("http://stuff2do.systs.net/dspire",10,82);
		else if (x>=210 && y>=18 && x<=223 && y<=134)
			ttLike("HP: "+hp+"/"+maxhp,x-50,y);
		else if (x>=187 && y>=19 && x<=201 && y<=136)
			ttLike("STAM: "+(int)(((float)stam/(float)116)*100)+"%",x-80,y);
		else if (x>=25 && y>=193 && x<=66 && y<=256) {
			if (name != null) {
				if (name.equals(name2draw))
					ttLike("Your portrait",x,y);
				else
					ttLike(name2draw+"'s portrait",x,y);
			}
		}
		else //if (x>=71&&x<=154&&y>=195&&y<=256) { // 71,195|154,256
			if (x>=74&&x<=109&&y>=196&&y<=208) { //Background
				ttLike("Toggle background color",x,y);
			}
			else if (x>=74&&x<=109&&y>=211&&y<=223) { //Snap
				ttLike("Snap text",x,y);
			}
			else if (x>=115&&x<=148&&y>=226&&y<=238) { //Stats
				ttLike("Your stats",x,y);
			}
			else if (x>=115&&x<=148&&y>=241&&y<=253) { //Help
				ttLike("Help!",x,y);
			}
			else if (x>=74&&x<=109&&y>=226&&y<=238) { //cls
				ttLike("Clear the text box",x,y);
			}
			else if (x>=115&&x<=148&&y>=211&&y<=223) { //brb
				ttLike("Go BRB/AFK",x,y);
			}
		//}
		else {
			if (atmoveplace)
				clearMoveBorder();
			else if (didtt) {
				didtt=false;
				repaint(ttlx-5, ttly-15, fms.stringWidth(tttext)+11, 16);
				tttext=null;
			}
		}
		return true;
	}

	public void ttLike(String s, int x, int y) {
		if (s==null||didtt)
			return;
		didtt=true;
		ttlx=x;
		ttly=y;
		tttext=s;
		repaint(ttlx-5, ttly-15, fms.stringWidth(s)+11, 16);
	}
	public void drawttLike(Graphics g) {
		//Graphics g = getGraphics();
		g.setFont(fs);
		g.setColor(dsbg2);
		g.fillRect(ttlx-5,ttly-15,fms.stringWidth(tttext)+10,15);
		g.setColor(Color.black);
		g.drawRect(ttlx-5,ttly-15,fms.stringWidth(tttext)+10,15);
		g.setColor(dsbg);
		g.drawString(tttext,ttlx,ttly-3);
	}

	public boolean mouseDrag(Event e, int x, int y) {
		if (atmoveplace) {
			fx+=(x-xdiff);
			fy+=(y-ydiff);
			parent.move(fx,fy);
			if (parent.fullscreen)
				parent.w.move(fx,fy);
		}
		return true;
	}

	public boolean mouseExit(Event e, int x, int y) {
		if (atmoveplace)
			clearMoveBorder();
		return true;
	}

	public boolean action(Event e, Object arg) {
		//append(e.toString(),0);
		if (e.target == login) {
			if (login.getLabel().equals("Upload!")) {
				mapupload=mapfiles.getSelectedItem();
				removeMapBox();
				sendIt("]iwannaupload");
				return true;				
			}
			else if (namebox.isShowing()) {
				login.disable();
				namebox.disable();
				passbox.disable();
				help.disable();
				name = namebox.getText().replace(' ','|');
				name2draw=name.replace('|',' ');
				sendIt("connect " + name + " " + passbox.getText().replace(' ','|')+"\t");
			}
			else if (asp[0].isShowing()) {
				if (desc.getText().equals(""))
					append("You need to enter a description.",3);
				else {
					login.disable();
					namebox.disable();
					passbox.disable();
					help.disable();
					colorstring = (char)(asp[0].getSelectedIndex()+32)+""+(char)(asp[1].getSelectedIndex()+32)+""+(char)(asp[2].getSelectedIndex()+32)+""+(char)(sex.getSelectedIndex()+33);
					//remakePlayerImages();
					sendIt("color "+colorstring+"\ndesc "+desc.getText().replace('\n',' '));
					char[] tarray = colorstring.toCharArray();
					System.arraycopy(tarray,0,myparray,0,3);
					//parray=(colorstring.substring(0,3)+dpstrings[colorstring.charAt(3)-33]).toCharArray();
					//myparray=(colorstring.substring(0,3)+dpstrings[colorstring.charAt(3)-33]).toCharArray();
					//portrait=assemblePortrait(parray);
					//repaint(23,217,38,61);
				}
			}
			return true;
		}
		else if (e.target == help) {
			if (help.getLabel().equals("Cancel")) {
				removeMapBox();
			}
			else if (namebox.isShowing()) {
				if (dslf==null)  {
					dslf = new DSLoginFrame(this,namebox,passbox,login);
					dslf.repaint();
				}
				else
					dslf.requestFocus();
			}
			else {
				if (dslf==null)  {
					dslf = new DSLoginFrame(this,desc,login);
					dslf.repaint();
				}
				else
					dslf.requestFocus();
			}
		}
		else if (e.target == asp[0] || e.target == asp[1] || e.target == asp[2] || e.target == sex) {
			char cs[] = {(char)(asp[0].getSelectedIndex()+32),(char)(asp[1].getSelectedIndex()+32),(char)(asp[2].getSelectedIndex()+32)};
			preview = getRemap(cs,map.player[(sex.getSelectedIndex()*22)+3]);
			repaint();
			return true;
		}
		return false;
	}

	public boolean lostFocus(Event e, Object what) {
		if (focus&&e.target==this) {
			focus=false;
			repaint();
		}

		//return true;
		return true;
	}
	public boolean gotFocus(Event e, Object what) {
		if (!focus&&e.target==this) {
			focus=true;
			repaint();
		}
		return true;
	}

	/***********************
	</System Event Routines>
	***********************/

	/*******************
	<Input Box Routines>
	*******************/


	public void append(String text, int col) {
		String line="",word="";
		int startpos=0,spacepos;
		int spaceavailable=462,wordlen=0;

		if (log)
			logstream.println(text);

		while (true) {
			spacepos=text.indexOf(" ",startpos);

			if (spacepos==-1) {
				word=text.substring(startpos,text.length());
				if (word.length()==0) {
					addLine(line,col);
					break;
				}
			}
			else
				word=text.substring(startpos,spacepos+1);

			wordlen = fm.stringWidth(word);

			if (wordlen > spaceavailable) {
				if (line.length()>0)
					addLine(line,col);
				if (wordlen > 462) {
					//startpos+=line.length();
					//line="";
					int s=getLongLineCutOff(text,startpos);
					addLine(text.substring(startpos,s),col);
					startpos=s;
					//continue;
				}
				line="";
				spaceavailable=462;
			}
			else {
				startpos+=word.length();
				line+=word;
				spaceavailable-=wordlen;
			}
		}
		repaint(156,289,463,119);
	}
	public int getLongLineCutOff(String text, int startpos) {
		char[] chars = text.toCharArray();
		int thelen=0;
		//String newline="";
		//int spaceavailable=462;
		
		for (int i=startpos;i<chars.length;i++) {
			thelen+=fm.stringWidth(""+chars[i]);
			if (thelen > 462)
				return (i-1);
			//else
			//	newline+=chars[i];
		}
		return -1;
	}

/*	public void append(String text, int col) {
		if (log)
			logstream.println(text);
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
			if (fm.stringWidth(templine+tempword) < 463) {
				//if (templine.equals(""))
				//	templine = tempword;
				//else
					templine+=tempword;
			}
			else {
				addLine(templine, col);
				templine = tempword;
				while (fm.stringWidth(templine) > 463) {
					String[] tempstuff = trimLongLine(templine);
					addLine(tempstuff[0],col);
					templine = tempstuff[1];
				}
			}
		}
		addLine(templine,col);
		repaint(156,289,463,119);
	}

	public String[] trimLongLine(String text) {
		String retval[] = {"",""};
		char[] charys = text.toCharArray();
		for (int i = 0; i < charys.length; i++) {
			if (fm.stringWidth(retval[0]+charys[i]) > 463) {
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
*/
	public void addLine(String text, int col) {
		text = text.replace('|',' ');
		for (int i = TextArray.length-1; i > 0; i--) {
			TextArray[i] = TextArray[i-1];
			lineColors[i] = lineColors[i-1];
			//System.out.println(TextArray[i] + "-"+TextArray[i-1]);
		}
		TextArray[0] = text;
		lineColors[0]=col;
		if (scrollpos > 8) {
			scrollpos++;
			if (scrollpos > TextArray.length - 1)
				scrollpos = TextArray.length-1;
		}			
	}

	public void scrollUp() {
		if (scrollpos < TextArray.length - 1) {
			scrollpos++;
			repaint(156,289,463,119);
			g4panel.setColor(colorarray[2]);
			g4panel.drawRect(154,287,466,122);
		}
	}

	public void scrollDown() {
		if (scrollpos > 8) {
			scrollpos--;
			repaint(156,289,463,119);
			if (scrollpos==8) {
				g4panel.setColor(dsbg);
				g4panel.drawRect(154,287,466,122);
			}
		}
	}

	/********************
	</Input Box Routines>
	********************/

	/*public boolean connectDS() {
		connstat = "Trying to connect to DragonSpires...";
		repaint(43,325,219,27);
		try {
			//Socket s = new Socket("adum", 7734);
			//Socket s = new Socket("207.227.238.122", 7734);
			//Socket s = new Socket("mechanixx.dyndns.org", 7734);
			i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			o = new PrintStream(new BufferedOutputStream(s.getOutputStream()));
			connstat = "Connected.";
			repaint(43,325,219,27);
			doTracker();
			connstat = "Click screen to continue!";
			repaint(43,325,219,27);
			ledin = -2;
			return true;
		}
		catch (Exception e) {
			/try {
				String cod = dsGetCodeBase("").toString();
				Socket s = new Socket(cod.substring(7, cod.indexOf("/", 8)), 7734);
				i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
				o = new PrintStream(new BufferedOutputStream(s.getOutputStream()));
				connstat = "Connected.";
				repaint(43,325,219,27);
				doTracker();
				connstat = "Click screen to continue.";
				ledin = -2;
			}
			catch (Exception ex) {/
				connstat = "Can't connect to DragonSpires.";
				repaint(43,325,219,27);
			//}
		}
		return false;
	}*/
	/*public int[] findNextFloor(int dir, int tx, int ty) {

		char xc = 0;
		char yc = 0;
		int oe = ty % 2;

		switch (dir) {
			case 7:
				if (oe == 1) xc = '-';
				yc = '-';
				break;
			case 9:
				if (oe == 0) xc = '+';
				yc = '-';
				break;
			case 1:
				if (oe == 1) xc = '-';
				yc = '+';
				break;
			case 3:
				if (oe == 0) xc = '+';
				yc = '+';
		}

		switch (xc) {
			case '+':
				tx++;
				if (tx >= map.mwidth)
					tx = map.mwidth-1;
				break;
			case '-':
				tx--;
				if (tx < 0)
					tx = 0;
		}

		switch (yc) {
			case '+':
				ty++;
				if (ty >= map.mheight)
					ty = map.mheight-1;
				break;
			case '-':
				ty--;
				if (ty < 0)
					ty = 0;
		}

		int[] retval = new int[2];
		retval[0] = tx;
		retval[1] = ty;
		return retval;
	}*/
	/*public void remakePlayerImages() {
		char[] cs = colorstring.toCharArray();
		Image[] retimg = new Image[22];
		MediaTracker tracker = new MediaTracker(this);
		for (int i = 0; i < 22; i++) {
			retimg[i] =getRemap(cs,map.player[(cs[3]-33)+i]);
		}
		Vector newentry = new Vector();
		newentry.addElement(retimg);
		playercache.put(colorstring, newentry);
	}*/
	/*public boolean dsPinR(int x, int y) {
		return (x >= map.xpos-3 && x <= map.xpos+3 && y >= map.ypos-8 && y <= map.ypos+8);
		//	return true;
		//return false;
	}*/

	public void uploadMap() {
		try {
			append("Uploading map...",2);
			InputStream i = dsOpenFile(mapupload);
			int c=0;
			int cycleinterval=100;
			int m=((map.mwidth*2)*(map.mheight*2));///cycleinterval;
			int n;
			Graphics g = getGraphics();
			g.setFont(f);
			g.setColor(Color.yellow);

			showperc=true;
			currperc=0;
			repaint();
			for (c=0;c<m;c++) {
				o.print((char)(i.read()+32));
				if (c%1000==0) {
					currperc=(float)c/(float)m;
					repaint(24,174,131,83);
				}
			}
			currperc=1.0f;
			repaint(24,174,131,83);
			showperc=false;
			repaint();

			//for (c=0;c<m;c++) {
			//	for (n=0;n<cycleinterval;n++)
			//		o.print((char)(i.read()+32));
			//	prevperc=currperc;
			//	currperc=(int)(( ((float)((float)c/(float)m)) *100));
			//	if (prevperc!=currperc) {
			//		repaint();
			//		g.drawString(currperc+"%",10,20);
			//	}
			//}
			i.close();
			uploadScript();
		}
		catch (Exception e) {
			append(e.toString(),0);
			e.printStackTrace();
		}
	}
	public void uploadScript() {
		String mapscript=mapupload.substring(0,mapupload.length()-3)+"ini";
		try {

			InputStream it = dsOpenFile(mapscript);
			if (it==null) {
				sendIt("]scriptno");
				return;
			}
			sendIt("]scriptyes");
			DataInputStream i = new DataInputStream(it);
			String line;
			append("Uploading script...",2);
			while ((line=i.readLine())!=null)
				sendIt(line);
			i.close();
			sendIt("[end]\nEOF");
		}
		catch (Exception e) {
			append(e.toString(),0);
			e.printStackTrace();
		}
	}
	void removeMapBox() {
		remove(mapfiles);
		mapfiles=null;
		remove(login);
		login=null;
		remove(help);
		help=null;
		requestFocus();
	}
	void equipmentRepaint() {
		repaint(14,247,137,201);
	}
	void magicGoldRepaint() {
		repaint(154,232,74,48);
	}
	void initMapBox() {
		mapfiles = new List();
		mapfiles.move(19,18);
		mapfiles.resize(182,147);
		mapfiles.setBackground(dsbg2);
		mapfiles.setForeground(Color.yellow);
		mapfiles.setFont(f);
		login=new Button("Upload!");
		login.move(19,165);
		login.resize(92,20);
		login.setBackground(dsbg2);
		login.setForeground(Color.yellow);
		login.setFont(f);
		help=new Button("Cancel");
		help.move(111,165);
		help.resize(91,20);
		help.setBackground(dsbg2);
		help.setForeground(Color.yellow);
		help.setFont(f);
		add(mapfiles);
		add(login);
		add(help);
		File f = new File(".");
		String[] dirlist = f.list();
		for (int i=0;i<dirlist.length;i++) {
			if (dirlist[i].toLowerCase().endsWith(".dpm"))
				mapfiles.addItem(dirlist[i]);
		}
		append("** Select a map file from the box at the left then click Upload!",5);
	}
	void updateGNB() {
		gnb = getRemap(colorstring.toCharArray(),gnbp);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(gnb,0);
		try {
			mt.waitForAll();
		}
		catch (Exception e) {}

		equipmentRepaint();
	}
}
class DSHelpFrame extends Frame {
	Button closeit;
	TextArea message;

	public DSHelpFrame(DragonSpiresPanel p) {
		super("DragonSpires Help");
		closeit = new Button("Close Help");

		message = new TextArea();

		StringBuffer sbuff = new StringBuffer();
		try {
			InputStream i = p.dsOpenFile("help.txt");
			int c=i.read();
			for (;c!=-1;c=i.read())
				sbuff.append((char)c);
			message.append(sbuff.toString());
		}
		catch(Exception e) {
			message.append("Error: Unable to open 'help.txt'");
		}

		add("South",closeit);
		add("Center",message);
		resize(326,350);
		show();
	}
	public boolean action(Event e, Object arg) {
		if (e.target == closeit)
			this.dispose();
		return true;
	}
}
class DSTextFrame extends Frame {
	Button closeit;
	TextArea message;
	static int count=0;

	public DSTextFrame(DragonSpiresPanel p) {
		super("DS Text Snapshot ("+count+")");
		closeit = new Button("Close");

		message = new TextArea();

		for (int i=p.TextArray.length-1;i>-1;i--) {
			if (p.TextArray[i].length()>0)
				message.append(p.TextArray[i]+"\n");
		}

		add("South",closeit);
		add("Center",message);
		resize(326,400);
		show();
	}
	public boolean action(Event e, Object arg) {
		if (e.target == closeit)
			this.dispose();
		return true;
	}
}
class DSLoginFrame extends Frame {
	TextField name,pass;
	TextArea desc;
	Button butt;
	Panel holdem;
	DragonSpiresPanel parent;

	public DSLoginFrame(DragonSpiresPanel pa, TextField n, TextField p, Button b) {
		super("DragonSpires Login");
		
		parent=pa;
		name=n;
		pass=p;
		butt=b;

		resize(245,120);

		holdem = new Panel();
		holdem.setLayout(null);
		holdem.setBackground(parent.dsbg);

		add("Center",holdem);

		holdem.add(name);
		holdem.add(pass);
		holdem.add(butt);

		name.move(70,10);
		pass.move(70,37);
		butt.move(69,64);

		show();

		name.requestFocus();
	}
	public DSLoginFrame(DragonSpiresPanel pa, TextArea d, Button b) {
		super("Character Description");
		
		parent=pa;
		desc=d;
		butt=b;

		resize(265,180);

		holdem = new Panel();
		holdem.setLayout(null);
		holdem.setBackground(parent.dsbg);

		add("Center",holdem);

		holdem.add(desc);
		holdem.add(butt);

		desc.move(5,25);
		butt.move(75,112);

		show();

		desc.requestFocus();
	}
	public void paint(Graphics g) {
		g = holdem.getGraphics();
		g.setFont(parent.f);
		g.setColor(parent.dstext);

		if (desc!=null) {
			g.drawString("Description:",5,15);
		}
		else {
			g.drawString("Name:",26,25);
			g.drawString("Password:",5,55);
		}
	}
	public boolean action(Event e, Object what) {
		return parent.action(e,what);
	}
}
