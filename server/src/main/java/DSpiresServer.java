/*

Java DragonSpires Server
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

import java.net.*;
import java.io.*;
import java.util.*;

public class DSpiresServer extends Thread {

	static final int SOCKETS = 100;
                 int PORT;
	static final int MAPS = 1;
	static       int START_MAP = 0;
	static int MAX_STAM = 210;
	static int mwidth=52,mheight=100;
	boolean LOG_COMMANDS=false;
	long sleeptime=2500;
	Sweeper SWEEPER;
	int maxweight = 55;
	final int exitpoints[] = {51,0,0,99};
	final static String[] dpstrings = {"!'+!"," &,#","!)#+"," \"!%","!,''"};

	ServerSocket server;

	DSMapServer[] maps = new DSMapServer[MAPS];
	DSpiresSocket[] socketbase = new DSpiresSocket[SOCKETS];

	EIE[] enemyIndex;
	Weapon[] weaponIndex;
	Armor[] armorIndex;

	stuffThread sthread;
	NPC2Thread npc2base;
	NPC3Thread npc3base;
	AnimalThread animalbase;
	//static EnemyThread enemybase;
	RefreshThread rthread;
	Vector admins;
	Vector ads;
	DSPCleanupThread dspct;
	RandomInfoThread rit;
	Enemy masterEnemy;
	PrintStream commandLog;

	final int COMBAT_CHANNEL=4, INFO_CHANNEL=7;
	final char channelchars[] = {'S','Q','H','G','C','B','T','I'};
	final char channelcolors[] = {'&','\'','#','%','\"','$','*',')'};
	final String channelnames[] = {"Shout","Quest","Help","Gossip","Combat","Bitch","Trade","Info"};
	boolean channelmutes[] = {false,false,false,false,false,false,false,false};
	Vector channels[] = new Vector[channelchars.length];

	final String aligntexts[] = {"Demonic","Menacing","Bad","Neutral","Nice","Honorable","Heroic"};
	final String dogsay[] = {"Yip yip yip!","Ruff. Rowf!","Wouf wouf!","*pant* Woof!","Rrrrrruff!","Yip yap!","Does anyone have any toilet paper?"};

	final static byte shortShapeStart[][] = {{2,6,10,14},{2,7,12,17},{2,5,8,11}};
	final static byte longShapeStart[][] = {{2,2,6,10,10,6,10,14,14},{2,2,7,12,12,7,12,17,17},{2,2,5,8,8,5,8,11,11}};

	final byte shapestartpointers[] = {0,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
	final byte faceconv[] = {0,0,0,1,0,0,0,2,0,3};
	final byte shortfaceconv[] = {1,3,7,9};
	final byte faceopp[] = {0,9,0,7,0,0,0,3,0,1};

	String multiconns[] = {"127.0.0.1"};

	String motd = "laf",rectime="",starttime="",randomInfos[]=new String[0],domainBans[]=new String[0],nameBans[]=new String[0],servername="DragonSpires Server";

	boolean goparade=false,nez=false,creation=true,saving=true;
	int uptime,record,logins,socktotal,created,neztable=0,currTotal,sooperadmins,maxconn,logtime=2,defaultadmins;

	public static void main(String args[]) {
		if (args.length == 1)
			new DSpiresServer(Integer.parseInt(args[0]));
		else
			new DSpiresServer(7734);
	}

	public DSpiresServer(int port) {
		PORT=port;
		setPriority(3);
		start();
	}

	public void run() {
		Socket client;
		int i;

		try {
			startServer();
			server = new ServerSocket(PORT,1);//,InetAddress.getByName("stuff2do"));

			while (true) {
				client = server.accept();
				socktotal++;
				//try {
				//client.setKeepAlive(true);
				//}
				//catch (Throwable e) {
				//}
				//client.setSoTimeout(60000);
				//client.setSoLinger(true,1000);
				client.setTcpNoDelay(true);

				log("Connected: "+client.getInetAddress());

				if (!checkDomainBans(client))
					basicSocketCloseMessage(client,"Banned","Your IP or domain is currently banned.\nIf you have a problem with this e-mail somebody.\nPress Ctrl-Q to close.");
				else if(tooManySockets(client))
					basicSocketCloseMessage(client,"Too many conns","You currently have too many connections to DragonSpires. Sorry.\nPress Ctrl-Q to close.");
				else {
					i = findFreeSocket();

					if (i==-1)
						client.close();
					else
						socketbase[i]=new DSpiresSocket(i,client,this);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void basicSocketCloseMessage(Socket s, String reason, String message) throws IOException {
		PrintWriter o = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));
		log("Disonnected:  ("+s.getInetAddress()+") {"+reason+"}");
		o.println(message);
		o.flush();
		o.close();
		//s.shutdownOutput();
		s.close();
		s=null;
	}

	public void startServer() throws Exception {

		try {
			int savelog=3;
			File afile,bfile;

			// Cycle server log save
			for (int i=savelog-1;i>0;i--) {
				afile=new File("../log/server.log_"+i);
				if (afile.exists()) {
					bfile=new File("../log/server.log_"+(i+1));
					afile.renameTo(bfile);
				}
			}
			afile=new File("../log/server.log");
			bfile=new File("../log/server.log_1");
			afile.renameTo(bfile);

			// Cycle error log save
			for (int i=savelog-1;i>0;i--) {
				afile=new File("../log/error.log_"+i);
				if (afile.exists()) {
					bfile=new File("../log/error.log_"+(i+1));
					afile.renameTo(bfile);
				}
			}
			afile=new File("../log/error.log");
			bfile=new File("../log/error.log_1");
			afile.renameTo(bfile);

			FileOutputStream fos = new FileOutputStream(new File("../log/server.log"));
			FileOutputStream fos2 = new FileOutputStream(new File("../log/error.log"));
			if (LOG_COMMANDS)
				commandLog = new PrintStream(new FileOutputStream(new File("../log/commands.log")));
			System.setErr(new PrintStream(fos2));
			System.setOut(new PrintStream(fos));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		new DSPingServer();
		new DSQueryServer(this);
		dspct = new DSPCleanupThread(this);

		admins = new Vector();
		admins.addElement("Mech");
		admins.addElement("Motorhed");

		sooperadmins = admins.size();

		admins.addElement("Turnip");
		admins.addElement("Brandy");
		admins.addElement("sylvyr");

		defaultadmins = admins.size();

		readServerConfig();
		readEnemyConfig();
		readWeaponConfig();
		readArmorConfig();

		loadDomainBans();
		loadNameBans();

		ads = new Vector();
		loadAds();

		npc2base = new NPC2Thread(this);
		npc3base = new NPC3Thread(this);
		animalbase = new AnimalThread(this);
		//enemybase = new EnemyThread(this);
		rthread = new RefreshThread(this);
		rit = new RandomInfoThread(this);

		masterEnemy = new Enemy(this);

		DSMapServer.parent=this;
		
		//not old stuff (comment this next line if using "accessories"
		maps[0] = new DSMapServer("blank",0);
		//old stuff
		// ** Uncomment ** IF using "accessories"
		/*maps = new DSMapServer[50];
		maps[0] = new Lev01(this);
		maps[1] = new Lev02();
		maps[2] = new Lev03();
		maps[3] = new DSMapServer("lev04",3);
		maps[4] = new Lev05();
		maps[5] = new DSMapServer("lev06",5);
		maps[6] = new Lev07();
		maps[7] = new Lev08();
		maps[8] = new Lev09();
		maps[9] = new DSMapServer("lev10",9);
		maps[10] = new Lev11();
		maps[11] = new Lev12();
		maps[12] = new DSMapServer("lev13",12);
		maps[13] = new DSMapServer("lev14",13);
		maps[14] = new Lev15();
		maps[15] = new Lev16();
		maps[16] = new DSMapServer("lev17",16);
		maps[17] = new Lev18();
		maps[18] = new Lev19();
		maps[19] = new Lev20();
		maps[20] = new DSMapServer("lev21",20);
		maps[21] = new Lev22();
		maps[22] = new Lev23();
		maps[23] = new Lev24();
		maps[24] = new Lev25();
		maps[25] = new DSMapServer("lev26",25);
		maps[26] = new Lev27();
		maps[27] = new DSMapServer("lev28",27);
		maps[28] = new Lev29();
		maps[29] = new DSMapServer("lev30",29);
		maps[30] = new DSMapServer("lev31",30);
		maps[31] = new Lev32();
		maps[32] = new Lev33();
		maps[33] = new DSMapServer("lev34",33);
		maps[34] = new DSMapServer("lev35",34);
		maps[35] = new DSMapServer("lev36",35);
		maps[36] = new DSMapServer("lev37",36);
		maps[37] = new DSMapServer("lev38",37);
		maps[38] = new DSMapServer("lev39",38);
		maps[39] = new DSMapServer("lev40",39);
		maps[40] = new DSMapServer("lev41",40);
		maps[41] = new DSMapServer("lev42",41);
		maps[42] = new DSMapServer("lev43",42);
		maps[43] = new DSMapServer("lev44",43);
		maps[44] = new DSMapServer("lev45",44);
		maps[45] = new DSMapServer("lev46",45);
		maps[46] = new DSMapServer("lev47",46);
		maps[47] = new DSMapServer("lev48",47);
		maps[48] = new DSMapServer("lev49",48);
		maps[49] = new DSMapServer("lev50",49);*/

		for (int i=0;i<maps.length;i++) {
			if (maps[i] instanceof MainMapWithCode)
				((MainMapWithCode)maps[i]).start();
			maps[i].readMapScript(maps[i].mapname+".ini",maps[i]);
			//if (maps[i].enemybase.enemies.size()>0)
			//	maps[i].enemybase.start();
		}

		mwidth = 52;//maps[0].mwidth;
		mheight = 100;//maps[0].mheight;

		sthread = new stuffThread(this);

		animalbase.animals.addElement(new Bunny(26,82,maps[0],animalbase));
		animalbase.animals.addElement(new Bunny(25,85,maps[0],animalbase));
		animalbase.animals.addElement(new Bunny(25,82,maps[0],animalbase));
		animalbase.animals.addElement(new Bunny(25,83,maps[0],animalbase));
		animalbase.animals.addElement(new Snail(6,26,maps[0],animalbase));
		animalbase.animals.addElement(new Snail(7,28,maps[0],animalbase));
		animalbase.animals.addElement(new Lizard(26,21,maps[0],animalbase));

		// ** Uncomment ** IF using "accessories"
		/*animalbase.animals.addElement(new Squirrel(3,27,maps[1],animalbase));
		animalbase.animals.addElement(new Lizard(36,25,maps[1],animalbase));
		animalbase.animals.addElement(new Fish(38,11,maps[1],animalbase));
		animalbase.animals.addElement(new Fish(40,11,maps[1],animalbase));
		animalbase.animals.addElement(new Bunny(32,14,maps[1],animalbase));

		animalbase.animals.addElement(new Squirrel(5,28,maps[2],animalbase));
		animalbase.animals.addElement(new Squirrel(20,71,maps[2],animalbase));

		animalbase.animals.addElement(new Bunny(13,33,maps[3],animalbase));

		animalbase.animals.addElement(new Bunny(32,62,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(28,13,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(28,16,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(30,9,maps[4],animalbase));
		animalbase.animals.addElement(new Squirrel(27,7,maps[4],animalbase));
		animalbase.animals.addElement(new Squirrel(28,13,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(27,13,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(32,34,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(33,30,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(36,35,maps[4],animalbase));
		animalbase.animals.addElement(new Squirrel(37,39,maps[4],animalbase));
		animalbase.animals.addElement(new Squirrel(38,40,maps[4],animalbase));
		animalbase.animals.addElement(new Snail(37,45,maps[4],animalbase));
		animalbase.animals.addElement(new Fish(37,47,maps[4],animalbase));
		animalbase.animals.addElement(new Snail(35,44,maps[4],animalbase));
		animalbase.animals.addElement(new Bunny(33,26,maps[4],animalbase));
		animalbase.animals.addElement(new Crow(31,30,maps[4],animalbase));

		animalbase.animals.addElement(new Snail(24,53,maps[6],animalbase));
		animalbase.animals.addElement(new Fish(31,80,maps[6],animalbase));
		animalbase.animals.addElement(new Fish(30,75,maps[6],animalbase));

		animalbase.animals.addElement(new Lizard(5,31,maps[13],animalbase));
		animalbase.animals.addElement(new Lizard(22,18,maps[13],animalbase));
		animalbase.animals.addElement(new Lizard(32,6,maps[13],animalbase));
		animalbase.animals.addElement(new Lizard(45,21,maps[13],animalbase));
		*/
		
		String[] s2s;

		maps[0].npc3s = new Vector();
		//SWEEPER = new Sweeper(25,30,npc3base,maps[0]);
		//maps[0].npc3s.addElement(SWEEPER);
		maps[0].npc3s.addElement(new Kalamar(npc3base,maps[0]));


		// ** Uncomment ** IF using "accessories"
		//maps[2].npc3s = new Vector();
		//maps[2].npc3s.addElement(new Butch(npc3base,maps[2]));
		//maps[2].npc3s.addElement(new ElderlyWoman(npc3base,maps[2]));
		//maps[2].npc3s.addElement(new Ivy(npc3base,maps[2]));

		//maps[5].npc3s = new Vector();
		//maps[5].npc3s.addElement(new Cricket(npc3base,maps[5]));

		//maps[13].npc3s = new Vector();
		//maps[13].npc3s.addElement(new Elmer(npc3base,maps[13]));

		//maps[14].npc3s = new Vector();
		//maps[14].npc3s.addElement(new Kaylamar(npc3base,maps[14]));

		//maps[17].npc3s = new Vector();
		//maps[17].npc3s.addElement(new DruidicMaster(npc3base,maps[17]));

		//maps[24].npc3s = new Vector();
		//maps[24].npc3s.addElement(new Pokey(npc3base,maps[24]));
		//maps[24].npc3s.addElement(new Batrade(npc3base,maps[24]));

		//maps[26].npc3s = new Vector();
		//maps[26].npc3s.addElement(new Rodney(npc3base,maps[26]));

		readNPC3Config();

		sthread.start();

		NPC2.server=this;
		//((NPC2)npc2base.npc2s.elementAt(0)).server=this;
		npc2base.start();

		npc3base.start();
		animalbase.start();
		//enemybase.start();
		rthread.start();
		dspct.start();
		rit.start();

		//maps[0].npcs = new Vector();
		//maps[0].npcs.addElement(new Wanderer(this,0));
		//maps[0].npcs.addElement(new Dog(this,0));

		motd = getMOTD();

		for (int i = 0; i < channels.length; i++) {
			channels[i] = new Vector();
		}


		Calendar c = Calendar.getInstance();
		starttime = "";//[DragonSpires server started on " + c.getTime() + "]";

		log("Server started.");
	}

	public int findFreeSocket() {
		for (int i = 0; i<SOCKETS; i++) {
			if(socketbase[i]==null)
				return i;
		}
		return -1;
	}






	public char toDSChar (int i) {
		return (char)(i+32);
	}
	public int getRandomStartX(DSMapServer m) {
		int retval=((int)Math.round(Math.random() * 5)-3) + m.xstart;
		if (retval>=m.mwidth)
			retval=m.mwidth-1;
		else if (retval<0)
			retval=0;
		return retval;
	}
	public int getRandomStartY(DSMapServer m) {
		int retval=((int)Math.round(Math.random() * 5)-3) + m.ystart;
		if (retval>=m.mheight)
			retval=m.mheight-1;
		else if (retval<0)
			retval=0;
		return retval;
	}
	public String badCharReplace(String name) {
		char[] chars=name.toCharArray();
		for (int i=0;i<chars.length;i++) {
			if (chars[i]<32||chars[i]>126)
				chars[i]='|';
		}
		return new String(chars);
	}
	public File getPlayerFileForInput(String name) {
		String fname = toDSFilename(name);
		if (fname.equals("")) {
			fname = getWackyPlayerName(name);
			if (fname!=null)
				return (new File(fname));
			else {
				File f = new File("../players/"+name+".dsp");
				if (!f.exists())
					return null;
				else fname = "!";
			}
		}

		if (fname.length()>=2)
			fname = "../players/"+fname.charAt(0)+"/"+fname.charAt(1)+"/"+fname+".dsp";
		else
			fname = "../players/"+fname.charAt(0)+"/"+fname+".dsp";

		File f = new File(fname);
		if (f.exists())
			return f;

		return null;
	}
	public String getPlayerFilenameForOutput(String name) {
		String fname = toDSFilename(name);
		if (fname.equals("")) {
			fname = getWackyPlayerName(name);
			if (fname==null)
				fname = createWackyEntry(name);

			return (fname);
		}
		else {
			if (fname.length()>=2)
				fname = "../players/"+fname.charAt(0)+"/"+fname.charAt(1)+"/"+fname+".dsp";
			else
				fname = "../players/"+fname.charAt(0)+"/"+fname+".dsp";

			return fname;
		}
	}
	public String encode(int val) {
		//if (resultArray.length<2)
		//	resultArray=new Char[2];

		//resultArray[0]=val/95;
		//resultArray[1]=val%95;

		return (char)(val/95+32)+""+(char)(val%95+32);
	}
	public String mapencode(int val) {
		return (char)(val/95)+""+(char)(val%95);
	}
	public int decode(int head, int tack) {
		return (head*95+tack);
	}
	public Weapon getWeapon(int item) {
		for (int i=0;i<weaponIndex.length;i++) {
			if (weaponIndex[i].item==item)
				return weaponIndex[i];
		}
		return null;
	}
	public Armor getArmor(int item) {
		for (int i=0;i<armorIndex.length;i++) {
			if (armorIndex[i].item==item)
				return armorIndex[i];
		}
		return null;
	}
	public int nextx(int x, int y, int dir) {
		int nx = x;
		switch (dir) {
			case 9:
			case 3: if (y%2==0) nx++; break;
			//case 7:
			//case 1:
			default: if (y%2==1) nx--;
		}
		if (nx < 0)
			nx = x;
		else if (nx >= mwidth) nx=x;

		return nx;
	}
	public int nexty(int y, int dir) {
		int ny = y;
		switch (dir) {
			case 7:
			case 9: ny--; break;
			//case 1:
			//case 3:
			default: ny++;
		}
		if (ny < 0)
			ny = y;
		else if (ny >= mheight) ny=y;

		return ny;
	}
	public int dice(int rolls, int sides) {
		int retval = 0;
		for (;rolls > 0;rolls--) {
			retval+=Math.round(Math.random() * (sides-1));
		}
		return retval+rolls; //+rolls to compensate for the rolls being >= 0
	}
	public int intRotateRight(int d) {
		switch (d) {
			case 7:
					return 9;
			case 9:
					return 3;
			case 1:
					return 7;
			default:
					return 1;
		}
	}
	public int intRotateLeft(int d) {
		switch(d) {
			case 7:
					return 1;
			case 9:
					return 7;
			case 1:
					return 3;
			default:
					return 9;
		}
	}
	public void channelBroadcast(String message, int channel) {
		//if (message.startsWith("("))
		//	message="["+channelcolors[channel]+message.substring(1);
		message="["+channelcolors[channel]+"["+channelchars[channel]+"] "+message;
		DSpiresSocket c;
		message=filterString(message);
		//synchronized(channels[channel]) {
			Enumeration e = channels[channel].elements();
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();
				c.pSend(message);
			}
		//}
	}
	public String assembleSpellString(int num, int x, int y) {
		return "*"+toDSChar(x)+""+toDSChar(y)+""+encode(num);
	}
	public void logStats() {
		try {
		//open file
		BufferedWriter o = new BufferedWriter(new FileWriter("../log/stats.log"));

		o.write(servername+"\n\n");

		//write when started
		o.write(starttime+"\n\n");

		o.write("The server has been up for "+uptime+" hours.\n\n");

		o.write("New characters this session: "+created+"\n\n");

		//write total logins
		o.write("Connection Totals\n");
		o.write("-----------------\n");
		o.write("Logins: "+logins+"\n");
		o.write("Sockets: "+socktotal+"\n");
		o.write("\nL/S ratio: "+Math.round(((float)logins/(float)socktotal)*100)+"%\n");
		o.write("\nGreatest number of players(GNP) on at once this session: "+record+"\n");
		o.write("Time for GNP: "+rectime+"\n");

		o.close();
		}
		catch (Exception e) {
			log(e.toString());
		}
	}
	public void globalBroadcast(String message) {
		DSpiresSocket c;
		//for (int m = 0; m < maps.length; m++) {
		for (int i=0;i<SOCKETS;i++) {
			c=socketbase[i];
			//synchronized(maps[m].sockets) {
			if (c!=null) {
				//Enumeration e = maps[m].sockets.elements();
				//while (e.hasMoreElements()) {
				//for (int i = 0; i < maps[m].sockets.size(); i++) {
				if (c.loggedin) //{
					//c = (DSpiresSocket)e.nextElement();
					//c = (DSpiresSocket)maps[m].sockets.elementAt(i);
					c.pSend(message);
				//}
			}
		}
	}
	public boolean findIntInArray(int value, int[] thearray) {
		for (int i = 0; i < thearray.length; i++) {
			if (thearray[i] == value)
				return true;
		}
		return false;
	}
	public void log(String txt) { //Print System Event
		//try {
		//	RandomAccessFile raf = new RandomAccessFile(new File("../log/server.log"), "rw");
			Calendar c = Calendar.getInstance();
		//	raf.seek(raf.length());
		//	raf.writeBytes("[" + c.getTime() + "] " + txt + "\n");
			System.out.println("[" + c.getTime() + "] " + txt);
		//	raf.close();
		
		//}
		//catch (IOException e) {
		//}
	}
	public void logCommands(String txt, DSpiresSocket s) {
		switch (txt.charAt(0)) {
			case 'm':
				return;
			default:
				if (txt.startsWith("sw"))
					return;
		}
		commandLog.println(s.name+": "+txt);
		//commandLog.flush();
	}
	public int checkForTrans(int anum, int x, int y, DSMapServer map) {
		if (anum==225 || (anum==151 && !portalAtPos(x,y,map)))
			return 0;
		//synchronized(map.sockets) {
			Enumeration e = map.sockets.elements();
			DSpiresSocket s;
			while (e.hasMoreElements()) {
				s = (DSpiresSocket)e.nextElement();
				if (s.x == x && s.y == y) {
					if (s.bitIsMarked(s.stateCheck, 1)||s.bitIsMarked(s.stateCheck, 8)) {
						if (!findIntInArray(map.oitemmap[x][y],itemsdisallowed) && !findIntInArray(map.oitemmap[x][y],othernorespawns))
							anum=map.oitemmap[x][y];
						else
							anum=0;
						return anum;							
					}
				}
			}
		//}
		return anum;
	}

	public int checkItemReplace(int anum, int x, int y, DSMapServer map) {
		switch (anum) {
			case 151:
				if (!portalAtPos(x,y,map))
					anum=0;
				break;
			case 143:
			case 225:
				anum=0;
				break;

			// Transformations
			case 14:
			case 23:
			case 61:
			case 87:
			case 117:
			case 181:
			case 144:
			case 323:
				if (map.oitemmap[x][y] != anum) {
					if (itemdefs[map.oitemmap[x][y]][0]==1 || itemdefs[map.oitemmap[x][y]][1]==1) {
						anum=map.oitemmap[x][y];
					}
					else
						anum=0;
				}
				break;
			default:
				if (anum>maps[0].items)
					anum=0;
		}

		return anum;
	}

	public String filterString(String s) {
		int t;

		for (int i = 0; i < filters; i++) {
			while ((t=s.toLowerCase().indexOf(filterstrings[i][0]))!=-1)
				s = s.substring(0,t)+filterstrings[i][1]+s.substring(t+filterstrings[i][0].length());
		}
		return s;
	}
	public String createWackyEntry(String name) {
		try {
			// Get current count and update
			RandomAccessFile raf = new RandomAccessFile("../players/wacky/wnp.count","rw");
			raf.seek(0);
			int n=Integer.parseInt(raf.readLine());
			raf.seek(0);
			raf.writeBytes((n+1)+"\n");
			raf.close();

			// Update list file
			raf = new RandomAccessFile("../players/wacky/wnp.dat","rw");
			raf.seek(raf.length());
			raf.writeBytes(name+"\t"+n+"\n");
			raf.close();

			return "../players/wacky/wacky"+n+".wnp";
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String getWackyPlayerName(String name) {
		String line,result=null;
		try {
			BufferedReader i = new BufferedReader(new FileReader("../players/wacky/wnp.dat"));

			while ((line=i.readLine())!=null) {
				if (line.startsWith(name)) {
					result="../players/wacky/wacky"+line.substring(line.indexOf('\t')+1)+".wnp";
					break;
				}
			}

			i.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	public String toDSFilename(String name) {
		char[] data=name.toLowerCase().toCharArray();
		String result="";
		for (int i=0;i<data.length;i++) {
			if (Character.isLetterOrDigit(data[i]))
				result+=data[i];
		}
		return result;
	}
	public boolean PointinRect(int x, int y, int rx1, int ry1, int rx2, int ry2) {
		return (x >= rx1 && x <= rx2 && y >= ry1 && y <= ry2);
		//	return true;
		//return false;
	}
	public boolean alreadyHasAMap(String name) {
		DSMapServer map;
		for (int m=0;m<maps.length;m++) {
			map=maps[m];
			if (map.allowupload) {
				for (int i=0;i<map.portals.length;i++) {
					if (map.portals[i]!=null) {
						if (map.portals[i].dest_map instanceof BasicMap) {
							if (map.portals[i].dest_map.owner.equals(name))
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	public void winCTF(DSpiresSocket c) {
		c.map.broadcast("({CTF} "+c.name+" places the "+((c.colorstring.charAt(0) == '+') ? "green" : "yellow")+" flag on the "+((c.colorstring.charAt(0) == '#') ? "green" : "yellow")+" pole! "+((c.colorstring.charAt(0) == '#') ? "Green" : "Yellow")+" wins!",c.map);

		c.setHands(0);

		for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				switch (c.map.itemmap[x][y]) {
					case 149:
					case 150:
					case 145:
					case 146:
					case 147:
					case 148:
						c.map.placeItemAt(0,x,y,c.map);
				}
			}
		}
			//synchronized (c.map.sockets) {
				Enumeration e = c.map.sockets.elements();
				DSpiresSocket c2;
				while (e.hasMoreElements()) {
					c2 = (DSpiresSocket)e.nextElement();
					if (c2.inhand == 149 || c2.inhand == 150)
						c2.setHands(0);
				}
			//}
	}
	public void recoverFlag(DSpiresSocket c) {
		int item2find = c.inhand-2;
		for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				if (c.map.itemmap[x][y] == item2find) {
					c.map.placeItemAt(item2find-2,x,y,c.map);
					break;
				}
			}
		}
		c.map.broadcast("({CTF} The "+((item2find == 147) ? "green" : "yellow")+" flag has been recovered.",c.map);
	}
	public void placePole(int pole, String color, DSpiresSocket c) {
		if (c.map.mapnumber != 22) {
			c.setHands(0);
			return;
		}
		for (int x = 0; x < mwidth; x++) {
			for (int y = 0; y < mheight; y++) {
				if (c.map.itemmap[x][y] == pole || c.map.itemmap[x][y] == pole+2) {
					c.pSend("(The "+color+" pole has already been placed. You may want to sell yours back.");
					return;
				}
			}
		}
		int tx4 = nextx(c.cx-32,c.cy-32,c.facing);
		int ty4 = nexty(c.cy-32,c.facing);
		if (PointinRect(tx4,ty4,39,74,51,99))
			c.pSend("(You can't place a pole so close to the start area!");
		else if (c.map.itemmap[tx4][ty4] == 0 && c.map.playermap[tx4][ty4] == 0) {
			c.setHands(0);
			c.map.placeItemAt(pole,tx4,ty4,c.map);
			c.map.broadcast("({CTF} The "+color+" pole has been placed!",c.map);
		}
		else
			c.pSend("(There's something there already!");
	}
	protected String getWho() {
		String retval = "";
		int count = 0;
		DSpiresSocket c;
		/*for (int i = 0; i < maps.length; i++) {
			synchronized(maps[i].sockets) {
				Enumeration e = maps[i].sockets.elements();
				while (e.hasMoreElements()) {
					c = (DSpiresSocket)e.nextElement();
					if (retval.equals(""))
						retval+=c.name;
					else
						retval+=", " + c.name;					
					count++;
				}
			}
		}*/

		for (int i=0;i<SOCKETS;i++) {
			if (socketbase[i]!=null) {
				if (socketbase[i].loggedin&&!socketbase[i].invis) {
					c = socketbase[i];
					if (retval.equals(""))
						retval+=c.name;
					else
						retval+=", " + c.name;					
					count++;					
				}
			}
		}
		retval+=".\n(Total players: "+count;//currTotal;
		if (count != currTotal)
			currTotal = count;
		return retval;
	}
	public String getMOTD() {
		String motdc = "";
		try {
			File thefile = new File("../conf/motd.txt");
			RandomAccessFile ras = new RandomAccessFile(thefile, "r");
			String line;

			while ((line = ras.readLine()) != null) {
				//motdc = motdc + line.substring(0, line.length() - 1) + "\n";
				motdc+=line + "\n";
			}
		}
		catch (IOException e) {
			log(e.toString());
			System.err.println("Error reading MOTD: " + e);
		}
		return motdc;
	}
	public void saveAllCharsNow() {
			for (int i = 0; i < SOCKETS; i++) {
				if (socketbase[i]!=null) {
					if (socketbase[i].loggedin)
						socketbase[i].savePlayerData();
				}
			}
	}
	public void killServer(int n) {
		saveAllCharsNow();
		switch (n) {
			case 0:
				globalBroadcast("[&Server going down...");
				try {
					BufferedWriter o = new BufferedWriter(new FileWriter("../kill"));
					o.close();
				}
				catch (IOException e) { e.printStackTrace(); }
				break;
			case 1:
				globalBroadcast("[&Server restarting! Please come right back! =)");
		}
		System.exit(0);
	}
	public void loadDomainBans() {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/bans.domain"));
			String line;
			Vector collector=new Vector();

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					collector.addElement(line);
				}
			}
			in.close();

			domainBans=new String[collector.size()];
			for (int i=0;i<collector.size();i++)
				domainBans[i]=collector.elementAt(i).toString().toLowerCase();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean checkDomainBans(Socket s) {
		String daip = s.getInetAddress().toString().toLowerCase();;
		for (int i = 0; i < domainBans.length; i++) {
			if (daip.indexOf(domainBans[i])!=-1)
				return false;
		}
		return true;
	}
	public void loadNameBans() {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/bans.name"));
			String line;
			Vector collector=new Vector();

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					collector.addElement(line);
					//System.out.println(line);
				}
			}
			in.close();

			nameBans=new String[collector.size()];
			for (int i=0;i<collector.size();i++)
				nameBans[i]=collector.elementAt(i).toString().toLowerCase();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean checkNameBans(String s) {
		s=s.toLowerCase();
		for (int i = 0; i < nameBans.length; i++) {
			//System.out.println(s+":"+nameBans[i]);
			if (s.indexOf(nameBans[i])!=-1)
				return false;
		}
		return true;
	}
	public void addNameBan(String name) {
		genericFileAppend(name+"\n","../conf/bans.name");
		loadNameBans();
	}
	public void genericFileAppend(String text, String filename) {
		try {
			RandomAccessFile raf = new RandomAccessFile(filename,"rw");
			raf.seek(raf.length());
			raf.writeBytes(text);
			raf.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	public void addDomainBan(DSpiresSocket c) {
		genericFileAppend(c.my_socket.getInetAddress().getHostName()+"\n","../conf/bans.domain");
		loadDomainBans();
	}
	public void readWeaponConfig() {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/wpn.conf"));
			String line;
			boolean inTag=false;
			Weapon tempentry=null;
			Vector collector=new Vector();

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					if (!line.startsWith("#")) {
						if (inTag) {
							String lowling=line.toLowerCase().trim();
							String posval1=line.substring(line.indexOf("=")+2, line.length()-1);
							try {
								if (lowling.startsWith("item"))
									tempentry.item=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("mindam"))
									tempentry.mindam=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("maxdam"))
									tempentry.maxdam=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("projectile="))
									tempentry.projectile=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("projectile_length")) {
									tempentry.projectile_length=Integer.parseInt(posval1.trim());
									if (tempentry.projectile_length>10)
										tempentry.projectile_length=10;
								}
								else if (lowling.startsWith("stam"))
									tempentry.stam=-Integer.parseInt(posval1.trim());
								else if (line.startsWith("</")) {
									inTag=false;
									if (tempentry!=null) {
										collector.addElement(tempentry);
										tempentry=null;
									}
								}
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							line=line.trim();
							if (line.startsWith("<")) {
								inTag=true;
								tempentry = new Weapon(0,0,1,0,2);
							}
						}
					}
				}
			}
			in.close();
			weaponIndex=new Weapon[collector.size()+1];
			weaponIndex[0]=NO_WEAPON;
			for (int i=1;i<weaponIndex.length;i++)
				weaponIndex[i]=(Weapon)collector.elementAt(i-1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void readArmorConfig() {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/arm.conf"));
			String line;
			boolean inTag=false;
			Armor tempentry=null;
			Vector collector=new Vector();

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					if (!line.startsWith("#")) {
						if (inTag) {
							String lowling=line.toLowerCase().trim();
							String posval1=line.substring(line.indexOf("=")+2, line.length()-1);
							try {
								if (lowling.startsWith("item"))
									tempentry.item=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("mindam"))
									tempentry.mindam=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("maxdam"))
									tempentry.maxdam=Integer.parseInt(posval1.trim());
								else if (line.startsWith("</")) {
									inTag=false;
									if (tempentry!=null) {
										collector.addElement(tempentry);
										tempentry=null;
									}
								}
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							line=line.trim();
							if (line.startsWith("<")) {
								inTag=true;
								tempentry = new Armor(0,0,1);
							}
						}
					}
				}
			}
			in.close();
			armorIndex=new Armor[collector.size()+1];
			armorIndex[0]=new Armor(0,0,1);
			for (int i=1;i<armorIndex.length;i++)
				armorIndex[i]=(Armor)collector.elementAt(i-1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void readEnemyConfig() {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/nme.conf"));
			String line;
			boolean inTag=false;
			EIE tempentry=null;
			Vector collector=new Vector();

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					if (!line.startsWith("#")) {
						if (inTag) {
							String lowling=line.toLowerCase().trim();
							String posval1=line.substring(line.indexOf("=")+2, line.length()-1);
							try {
								if (lowling.startsWith("name"))
									tempentry.name=posval1;
								else if (lowling.startsWith("color"))
									tempentry.colorstring=posval1;
								else if (lowling.startsWith("attack"))
									tempentry.attack=posval1;
								else if (lowling.startsWith("drop")) {
									StringTokenizer st=new StringTokenizer(posval1,",");
									Vector bitch=new Vector();
									int[] slut;
									while (st.hasMoreTokens())
										bitch.addElement(st.nextToken());
									slut=new int[bitch.size()];
									for (int i=0;i<slut.length;i++)
										slut[i]=Integer.parseInt(((String)bitch.elementAt(i)).trim());
									tempentry.drop=slut;
								}
								else if (lowling.startsWith("gold"))
									tempentry.gold=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("hp"))
									tempentry.maxhp=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("movewait"))
									tempentry.maxtilltrig=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("spell"))
									tempentry.spell=(short)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("weaponmin"))
									tempentry.weapon.mindam=(byte)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("weaponmax"))
									tempentry.weapon.maxdam=(byte)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("armormin"))
									tempentry.armor.mindam=(byte)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("armormax"))
									tempentry.armor.maxdam=(byte)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("shapecat"))
									tempentry.shapecat=(byte)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("aligngive"))
									tempentry.align=(short)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("projectile_item"))
									tempentry.projectile_item=Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("projectile_length"))
									tempentry.projectile_length=(short)Integer.parseInt(posval1.trim());
								else if (lowling.startsWith("projectile=")) {
									if (posval1.trim().toLowerCase().equals("true"))
										tempentry.projectile=true;
									else
										tempentry.projectile=false;
									//System.out.println("projectile set to: "+tempentry.projectile);
								}
								else if (lowling.startsWith("enemy_item")) {
									int tempint = Integer.parseInt(posval1.trim());
									if (itemdefs[tempint][1] == 0)
										tempint=22;
									tempentry.enemy_item=tempint;
									itemdefs[tempint][2] = 2;
								}
								else if (lowling.startsWith("is_item")) {
									if (posval1.trim().toLowerCase().equals("true"))
										tempentry.is_item=true;
									else
										tempentry.is_item=false;
								}
								else if (line.startsWith("</")) {
									inTag=false;
									if (tempentry!=null) {
										collector.addElement(tempentry);
										tempentry=null;
									}
								}
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							line=line.trim();
							if (line.startsWith("<")) {
								inTag=true;
								tempentry = new EIE();
								tempentry.weapon=new Weapon(0,0,1,0,1);
								tempentry.armor=new Armor(0,0,1);
							}
						}
					}
				}
			}
			in.close();
			enemyIndex=new EIE[collector.size()];
			for (int i=0;i<enemyIndex.length;i++)
				enemyIndex[i]=(EIE)collector.elementAt(i);
			//System.out.println("Enemy index size="+enemyIndex.length);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void readServerConfig() {		
		BufferedReader in=null;
		try {
			in = new BufferedReader(new FileReader("../conf/server.conf"));
			String line;

			while ((line=in.readLine())!=null) {
				if (line.startsWith("#"));				
				else if (line.startsWith("[")) {
					line=line.toLowerCase();

					if (line.equals("[multiconns]")) {
						Vector fun = grabScriptSectionData(in);
						multiconns = new String[fun.size()];
						for (int i=0;i<multiconns.length;i++)
							multiconns[i]=fun.elementAt(i).toString();
					}
				}
				else if (line.indexOf('=')!=-1) { // Simple variables
					String var = line.toLowerCase().substring(0,line.indexOf('='));
					String val=line.substring(line.indexOf('=')+1);;
					if (var.equals("start_map"))
						START_MAP=Integer.parseInt(val);
					else if (var.equals("max_connections"))
						maxconn=Integer.parseInt(val);
					else if (var.equals("allow_new_chars")) {
						if (val.toLowerCase().equals("true"))
							creation=true;
						else
							creation=false;
					}
					else if (var.equals("allow_saving")) {
						if (val.toLowerCase().equals("true"))
							saving=true;
						else
							saving=false;
					}
					else if (var.equals("max_stam")) // WORKS ON RESTART ONLY
						MAX_STAM=Integer.parseInt(val);
					else if (var.equals("max_weight"))
						maxweight=Integer.parseInt(val);
					else if (var.equals("max_connections"))
						maxconn=Integer.parseInt(val);
					else if (var.equals("pong_wait")) {
						if (dspct!=null)
							dspct.pong_wait=(long)Integer.parseInt(val);
					}
					else if (var.equals("ping_interval")) {
						if (dspct!=null)
							dspct.ping_interval=(long)Integer.parseInt(val);
					}
					else if (var.equals("servername"))
						servername=val;
				}
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	public Vector grabScriptSectionData(BufferedReader in) {
		Vector storage=new Vector();
		String line;
		try {
			while ((line=in.readLine())!=null) {
				if (line.startsWith("#"));
				else if (line.startsWith("[")) {
					if (line.equals("[end]"))
						break;
				}
				else if (line.length()!=0)
					storage.addElement(line);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return storage;
	}
	public  boolean tooManySockets(Socket s) {
		String addy=s.getInetAddress().getHostAddress();
		for (int i=0;i<multiconns.length;i++) {
			if (addy.equals(multiconns[i]))
				return false;
		}
		int i=0;
		for (int n=0; n<SOCKETS;n++) {
			if (socketbase[n]!=null) {
				if (addy.equals(socketbase[n].my_socket.getInetAddress().getHostAddress()))
					i++;
			}
		}

		return (i>=maxconn);
	}
	public void userLoggedIn(DSpiresSocket s) {
		logins++;
		currTotal++;
		if (currTotal > record) {
			record = currTotal;
			Calendar c = Calendar.getInstance();
			rectime = "[" + c.getTime() + "]";
		}
		if (++logtime==3) {
			logtime=0;
			logStats();
		}
		if (!saving)
			s.pSend("[\"Warning: character saving is disabled.");
	}
	public void userLoggedOut() {
		currTotal--;
		if (currTotal<0) {
			System.err.println("NEGATIVE TOTAL.");
		}
	}
	public void readNPC3Config() {
		String line="not started";
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/npc3.conf"));
			boolean inTag=false;
			NPC3 tempentry=null;
			Vector collector;

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					if (!line.startsWith("#")) {
						if (inTag) {
							if (line.startsWith("[")) {
								if (line.equals("[random_speaks]")) {
									if (tempentry.bitIsMarked(tempentry.NPC3_RANDOM_SPEAK)) {
										collector=grabScriptSectionData(in);
										tempentry.random_speaks = new String[collector.size()];
										for (int i=0;i<collector.size();i++)
											tempentry.random_speaks[i]=tempentry.assembleNPC3String(collector.elementAt(i).toString().replace(';','\n'),tempentry.name,"");
									}
								}
								else if (line.equals("[hits]")) {
									if (tempentry.bitIsMarked(tempentry.NPC3_HIT)) {
										collector=grabScriptSectionData(in);
										tempentry.hits = new String[collector.size()];
										for (int i=0;i<collector.size();i++)
											tempentry.hits[i]=collector.elementAt(i).toString().replace(';','\n');
									}
								}
								else if (line.equals("[interactions]")) {
									if (tempentry.bitIsMarked(tempentry.NPC3_INTERACT)) {
										collector=grabScriptSectionData(in);
										tempentry.num_interact = collector.size()/2;
										tempentry.interactions = new String[tempentry.num_interact][2];
										for (int i=0;i<tempentry.num_interact;i++) {
											tempentry.interactions[i][0]=collector.elementAt(i*2).toString().replace(';','\n');
											tempentry.interactions[i][1]=collector.elementAt((i*2)+1).toString().replace(';','\n');
										}
									}
								}
							}
							else if (line.startsWith("</")) {
								inTag=false;
								if (tempentry!=null) {
									if (tempentry.map.npc3s==null)
										tempentry.map.npc3s = new Vector();
									tempentry.map.npc3s.addElement(tempentry);
									tempentry.init(tempentry);
									tempentry=null;
								}
							}
							else {
								String var = line.toLowerCase().substring(0,line.indexOf('='));
								String val=line.substring(line.indexOf('=')+1);

								if (var.equals("name")) {
									tempentry.name=val;
								}
								else if (var.equals("color")) {
									if (val.length() != 4)
										val="!!!!";
									tempentry.colorstring=val;
									if (val.charAt(3)==' ')
										tempentry.shapecat=0;
									else if (val.charAt(3)>'%')
										tempentry.shapecat=2;
									else
										tempentry.shapecat=1;
								}
								else if (var.equals("start")) {
									int i=val.indexOf(',');
									tempentry.x=toDSChar(Integer.parseInt(val.substring(0,i)));
									tempentry.y=toDSChar(Integer.parseInt(val.substring(i+1)));
								}
								else if (var.equals("wander")) {
									if (val.toLowerCase().equals("true"))
										tempentry.attributes|=tempentry.NPC3_WANDER;
									else if (tempentry.bitIsMarked(tempentry.NPC3_WANDER))
										tempentry.attributes^=tempentry.NPC3_WANDER;
								}
								else if (var.equals("face")) {
									switch (val.toLowerCase().charAt(0)) {
										case 'n':
											tempentry.facing=9;
											break;
										case 's':
											tempentry.facing=1;
											break;
										case 'w':
											tempentry.facing=7;
											break;
										default:
											tempentry.facing=3;
											break;
									}
								}
								else if (var.equals("map")) {
									tempentry.map = maps[Integer.parseInt(val)-1];
								}
								else if (var.equals("trigger_interval")) {
									tempentry.trigwait=Integer.parseInt(val);
									tempentry.trigtime=tempentry.trigwait;
								}
								else if (var.equals("random_speak")) {
									if (val.toLowerCase().equals("true"))
										tempentry.attributes|=tempentry.NPC3_RANDOM_SPEAK;
									else if (tempentry.bitIsMarked(tempentry.NPC3_RANDOM_SPEAK))
										tempentry.attributes^=tempentry.NPC3_RANDOM_SPEAK;
								}
								else if (var.equals("canhit")) {
									if (val.toLowerCase().equals("true"))
										tempentry.attributes|=tempentry.NPC3_HIT;
									else if (tempentry.bitIsMarked(tempentry.NPC3_HIT))
										tempentry.attributes^=tempentry.NPC3_HIT;
								}
								else if (var.equals("interactable")) {
									if (val.toLowerCase().equals("true"))
										tempentry.attributes|=tempentry.NPC3_INTERACT;
									else if (tempentry.bitIsMarked(tempentry.NPC3_INTERACT))
										tempentry.attributes^=tempentry.NPC3_INTERACT;
								}
								else if (var.equals("interact_title")) {
									if (tempentry.bitIsMarked(tempentry.NPC3_INTERACT))
										tempentry.interact_title=val;
								}

							}
						}
						else {
							line=line.trim();
							if (line.startsWith("<")) {
								inTag=true;
								tempentry = new NPC3();
								tempentry.parent = npc3base;
							}
						}
					}
				}
			}
			in.close();
		}
		catch (Exception e) {
			System.err.println("Line: "+line);
			e.printStackTrace();
		}
	}
	public boolean portalAtPos(int x, int y, DSMapServer map) {
		boolean retval=false;
		if (map.portals!=null) {
			for (int i=0;i<map.portals.length;i++) {
				if (map.portals[i]==null)
					continue;
				if (!map.portals[i].active)
					continue;
				if (map.portals[i].orig_x==x&&map.portals[i].orig_y==y) {
					retval=true;
					break;
				}
			}
		}
		return retval;
	}

	public String getItemName(int i) {
		if (i <= maps[0].items)
			return itemnames[i];
		else
			return animnames[((i+1-maps[0].items)-2)/8];
	}

	public void loadAds() {
		ads.removeAllElements();

		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/banners.txt"));
			String line;

			while ((line=in.readLine())!=null) {
				if (line.length()>0&&!line.startsWith("#")) {
					ads.addElement(line);
				}
			}
			in.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		/*try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/banners.mo"));
			String line;

			while ((line=in.readLine())!=null) {
				if (line.length()>0&&!line.startsWith("#")) {
					ads.addElement(line);
				}
			}
			in.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	//nowalk,noget,actwhenswing,actwhenget,actwhenstep,weight

	final byte itemdefs[][] = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,10},{0,0,0,0,1,2},{0,0,0,0,0,1},
					{0,0,0,0,0,7},{0,0,0,1,0,3},{0,1,0,0,1,1},{0,0,1,0,1,0},{1,1,0,0,0,15}, //5
					{0,0,0,0,0,4},{0,0,0,1,0,3},{0,0,0,0,0,5},{1,1,0,0,0,13},{1,1,0,0,0,15}, //10
					{1,1,1,0,0,15},{0,1,0,0,0,0},{1,1,0,0,0,15},{1,1,0,0,0,15},{0,0,0,1,0,2}, //15
					{0,0,0,0,0,3},{1,1,0,0,0,15},{1,1,0,0,0,15},{1,1,1,0,0,15},{1,1,0,0,0,15}, //20
					{0,0,0,1,0,5},{1,1,1,0,0,15},{0,0,1,0,0,10},{1,1,0,0,0,15},{0,0,0,0,0,10}, //25
					{0,0,0,0,0,10},{1,1,1,0,0,15},{0,0,0,0,0,3},{0,1,0,0,0,8},{0,1,0,0,0,8}, //30
					{1,1,0,0,0,15},{1,1,0,0,0,15},{0,1,0,0,0,14},{0,1,0,0,0,14},{0,0,0,0,0,3}, //35
					{0,0,0,0,0,2},{0,0,0,0,0,1},{1,1,0,0,1,15},{0,0,0,0,0,5},{0,0,0,0,0,6}, //40
					{0,0,0,0,0,2},{0,0,0,0,0,3},{0,0,0,0,0,0},{0,0,0,0,0,2},{0,0,0,0,0,1}, //45
					{0,0,0,0,0,2},{0,0,0,0,0,2},{0,0,0,0,0,2},{0,0,0,0,0,2},{0,0,0,0,0,2}, //50
					{0,0,0,0,0,2},{0,0,0,0,0,10},{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,0,1}, //55
					{1,1,1,0,0,15},{1,1,0,0,0,15},{0,0,0,0,0,1},{1,1,1,0,0,15},{1,1,1,0,0,15}, //60
					{1,1,1,0,0,15},{1,1,1,0,0,15},{1,1,1,0,0,15},{1,1,1,0,0,15},{1,1,0,0,0,15}, //65
					{0,0,1,0,0,7},{0,0,1,0,0,7},{0,0,1,0,0,7},{0,0,1,0,0,7},{1,1,0,0,0,15}, //70
					{1,1,0,0,0,10},{0,0,0,0,0,7},{0,0,0,0,0,5},{0,0,0,0,0,3},{1,1,0,0,0,15}, //75
					{1,1,0,0,0,15},{1,1,0,0,0,15},{0,0,0,0,0,3},{0,0,0,0,0,1},{0,0,1,0,0,2}, //80
					{0,0,1,0,0,2},{0,0,0,0,0,1},{1,1,0,0,0,15},{0,0,0,0,0,7},{0,0,0,0,0,7}, //85
					{0,0,0,0,0,5},{0,0,0,0,0,9},{0,0,0,0,0,4},{1,1,0,0,0,13},{0,1,0,0,0,7}, //90
					{0,1,0,0,0,7},{0,0,0,0,0,5},{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,0,1}, //95
					{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,0,4}, //100
					{0,0,0,0,0,4},{0,0,0,0,0,3},{0,0,0,1,0,1},{0,0,0,1,0,3},{0,0,0,1,0,5}, //105
					{0,0,0,1,0,7},{0,0,0,1,0,8},{1,1,0,0,0,4},{0,0,0,0,0,1},{0,0,0,0,0,10}, //110
					{1,1,0,0,0,7},{1,1,0,0,0,7},{0,1,0,0,0,0},{0,0,0,0,0,2},{0,0,0,1,0,4}, //115
					{0,0,0,0,0,1},{1,1,1,0,0,9},{1,1,0,0,0,15},{1,1,0,0,0,15},{0,1,0,0,0,8}, //120
					{1,1,0,0,0,5},{0,0,0,0,0,8},{1,1,0,0,0,15},{0,0,0,0,0,5},{1,1,0,0,0,14}, //125
					{0,0,0,0,0,2},{0,0,0,0,0,6},{0,0,0,1,0,2},{0,0,0,1,0,2},{0,0,0,1,0,2}, //130
					{0,0,0,0,0,2},{1,1,1,0,0,12},{0,0,0,0,0,3},{0,0,0,0,0,0},{0,0,1,0,0,3}, //135
					{0,0,0,0,0,2},{1,1,1,0,0,12},{1,1,1,0,0,12},{0,1,1,0,0,1},{0,1,0,0,0,3}, //140
					{1,1,1,0,0,8},{1,1,1,0,0,8},{1,1,0,0,0,7},{1,1,0,0,0,7},{0,0,0,1,0,2}, //145
					{0,0,0,1,0,2},{1,1,0,0,1,0},{1,1,0,0,0,15},{0,0,0,0,0,3},{0,0,0,0,1,3}, //150
					{0,0,0,0,0,8},{0,0,0,0,0,10},{0,0,0,0,0,10},{0,0,0,0,0,7},{0,0,0,0,0,9}, //155
					{0,0,0,0,0,8},{0,0,0,0,0,7},{0,0,0,0,0,5},{0,0,0,0,0,7},{1,1,1,0,0,0}, //160
					{1,1,1,0,0,0},{1,1,0,0,0,15},{0,0,0,0,0,2},{0,0,0,0,0,2},{0,0,0,0,0,2}, //165
					{0,0,0,0,0,2},{1,1,0,0,0,15},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//170
					{0,0,0,0,0,4},{0,0,0,0,0,4},{0,0,0,0,0,4},{0,0,0,0,0,4},{1,1,1,0,0,10},		//175
					{1,1,0,0,0,15},{1,1,0,0,0,10},{1,1,1,0,0,15},{1,1,1,0,0,15},{1,1,1,0,0,0},	//180
					{1,1,0,0,0,0},{1,1,0,0,0,0},{0,0,0,0,0,7},{1,1,0,0,1,0},{1,1,1,0,0,0},		//185
					{1,1,1,0,0,0},{1,1,1,0,0,0},{1,1,1,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//190
					{0,0,0,0,0,2},{0,1,0,0,1,0},{0,1,0,0,0,0},{0,0,0,1,0,0},{0,0,0,0,0,3},		//195
					{0,0,0,0,0,3},{0,0,0,1,0,8},{1,1,1,0,0,0},{1,1,1,0,0,0},{0,0,0,0,0,3},		//200
					{1,1,1,0,0,0},{0,0,0,0,0,4},{0,0,0,0,0,5},{0,1,0,0,1,6},{0,1,1,0,0,0},		//205
					{0,0,0,0,0,6},{0,0,0,0,0,4},{0,0,0,0,0,7},{0,0,0,0,0,15},{0,0,0,0,0,7},		//210
					{0,0,0,0,0,7},{0,0,0,0,0,5},{0,0,0,0,0,1},{1,1,1,0,0,0},{1,1,0,0,0,0},		//215
					{1,1,1,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//220
					{1,1,1,0,0,0},{1,1,0,0,0,0},{1,1,1,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//225
					{1,1,0,0,0,0},{1,1,0,0,0,0},{0,0,0,0,0,7},{1,1,0,0,1,0},{1,1,1,0,0,0},		//230
					{1,1,1,0,0,0},{1,1,0,0,0,0},{0,0,0,0,0,9},{0,0,0,0,0,8},{1,1,1,0,0,15},		//235
					{1,1,1,0,0,15},{1,1,0,0,0,0},{1,1,1,0,0,14},{1,1,1,0,0,14},{1,1,1,0,0,0},	//240
					{1,1,0,0,0,0},{0,0,0,0,0,2},{0,0,0,0,0,2},{1,1,1,0,0,14},{0,0,0,0,0,2},		//245
					{0,0,0,0,0,6},{0,0,0,0,0,6},{0,0,0,0,0,6},{0,1,1,0,0,2},{0,1,1,0,0,2},		//250
					{0,0,0,0,0,2},{0,0,0,0,0,2},{0,1,0,0,0,0},{0,1,0,0,0,0},{1,1,0,0,1,0},		//255
					{0,0,0,1,0,0},{1,1,1,0,0,0},{1,1,1,0,0,0},{1,1,1,0,0,0},{1,1,1,0,0,0},		//260
					{1,1,1,0,0,0},{1,1,1,0,0,0},{1,1,0,0,0,0},{0,1,0,0,0,8},{1,1,0,0,0,0},		//265
					{1,1,0,0,0,1},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//270
					{1,1,0,0,0,0},{1,1,0,0,1,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,1,0},		//275
					{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},{0,0,0,0,0,2},{0,1,0,0,0,0},		//280
					{0,0,0,0,0,2},{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},		//285
					{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},{1,1,0,0,0,0},		//290
					{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{0,1,0,0,0,0},{0,0,0,0,0,4},		//295
					{0,0,0,0,0,1},{0,0,0,0,0,5},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//300
					{0,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,1,0,0,14},{0,1,0,0,0,0},		//305
					{1,1,0,0,0,0},{0,1,0,0,0,0},{1,1,0,0,1,0},{0,0,0,0,0,7},{0,0,0,0,0,7},		//310
					{0,1,0,0,0,7},{1,1,1,0,0,0},{1,1,1,0,0,0},{1,1,0,0,0,0},{0,1,0,0,0,0},		//315
					{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//320
					{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{0,1,0,0,0,0},{1,1,0,0,0,0},		//325
					{1,1,0,0,0,0},{1,1,1,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},{1,1,0,0,0,0},		//330
					{1,1,0,0,0,0},{1,1,0,0,0,0},{0,1,0,0,0,0},{1,1,0,0,1,0},{1,1,0,0,0,0},		//335
					{1,1,1,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},{0,1,0,0,0,0},		//340
					{0,0,0,0,0,0},{0,1,0,0,0,0},
					
					
					//nowalk,noget,actwhenswing,actwhenget,actwhenstep,weight

					{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},
					{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},
					{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},
					{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},
					{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},{0,0,1,1,0,4},
					{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4},{0,0,0,1,0,4}
				};

	final byte floorwalk[] = {
		0,0,0,0,0,0,0,0,0,
		0,0,1,1,1,1,1,1,1,
		1,1,1,1,1,1,1,1,1,
		1,1,1,1,1,1,1,1,1,
		1,1,1,1,1,0,1,1,1,
		1,1,1,1,1,1,1,1,1,
		1,1,1,1,1,1,1,1,0,
		0,1,0,0,1,0,1,1,1,
		1,0,0,0,1,0,1,1,1,
		1,1,1,0,0,0,1,1,0};

	final static byte floornamepointers[] = {
							 0, 0, 1, 0, 0, 2, 3, 0, 4,
							 5, 0, 6, 6, 7, 7, 7, 7, 7,
							 6, 6, 6, 7, 7, 7, 7, 7, 7,
							 7, 7, 7, 7, 7, 7, 7, 7, 6,
							 6, 7, 7, 7, 7, 8, 6, 9, 9,
							 9, 9, 9, 9, 9, 9, 9,10,10,
							10,10,11,11,11,10,10,11,12,
							13,14,14,14,15,13,14,14,14,
							14,16,16,16,17,18,19,19,19,
							19,19,19,20,21,22, 7, 7,23
							};

	final static String floornames[] = {
		"the floor",
					"a Dragon tile",
					"grass",
					"sand",
					"dirt",
					"some flowers",
					"a wall",
					"a corner",
					"a wooden floor",
					"water",
					"a wall of dirt and rock",
					"..ewww, a worm on the wall",
					"a dark, dirt path",
					"a rickety wooden bridge",
					"some marsh",
					"some marsh with lillypads",
					"the catwalk",
					"lava",
					"mud",
					"a wall of stone",
					"leaves",
					"sand and grass",
					"dirt and grass",
					"stones"
					};

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
                                                                "a bush",
                                                                "a tree",
                                                                "a treasure chest",
                                                                "a 'pop'",
                                                                "a decorative orb",
                                                                "a decorative orb",
                                                                "the magic doggie biscuit",
                                                                "a little fish",
                                                                null,//sign
                                                                "a pillar.",
                                                                "a statue of RazorHawk",
                                                                null,//mior
                                                                "100 gold",
                                                                "a table with Mech's laptop on it",
                                                                "Darryl's Furcadia character",
                                                                "a fancy table",
                                                                "a chair",
                                                                "a chair",
                                                                "an open treasure chest",
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
                                                                "a table with food on it",
                                                                "Scare Crow",
                                                                "a rock",
                                                                "a shattered rock",
                                                                "a few stones",
                                                                "dead trees",
                                                                "a big tree",
                                                                null, //gopy
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
                                                                "a warlord onna stick! Mmm",
                                                                null,null,
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
                                                                "1 gold",
                                                                "5 gold",
                                                                "10 gold",
                                                                "25 gold",
                                                                "50 gold",
                                                                "a sapling",
                                                                "a walnut",
                                                                "a war hammer",
                                                                "a cool cactus",
                                                                "blooming cacti",
                                                                "a shadow",
                                                                "venus fly traps",
                                                                "a book",
                                                                "a worm",
                                                                "a yellow potion",
                                                                null,null,
                                                                "*KRINK*",
                                                                null,
                                                                "Royal Armor",
                                                                "a stone golem",
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
                                                                "an unlit pan",
                                                                "a lit, firey pan",
                                                                "an evil-vampire-happy-funballoon",
                                                                "a flatty",
                                                                "the green pole and flag",
                                                                "the yellow pole and flag",
                                                                "the green pole",
                                                                "the yellow pole",
                                                                "the green flag",
                                                                "the yellow flag",
                                                                "a portal",
                                                                "a stone wall",
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
                                                                "Lenny the Jeweler",
                                                                "a jewel",
                                                                "a jewel",
                                                                "a jewel",
                                                                "a jewel",
                                                                null,
                                                                "a flying arrow",
                                                                "a flying arrow",
                                                                "a flying ball",
                                                                "a blue dodgeball",
                                                                "a green dodgeball",
                                                                "a red dogeball",
                                                                "a broken larva",
                                                                "a treasure chest",
                                                                "a unicorn",
                                                                "a BRB sign",
                                                                "a Royal Guard",
                                                                "a Royal Guard",
                                                                null,null,
                                                                "a broken stole golem",
                                                                "a scorpion tail",
                                                                "a door",
                                                                null,
                                                                "a serpant head",
                                                                "a gargoyle statue",
                                                                "a serpant head",
                                                                "a wall",
                                                                "a wall",
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
                                                                "a cabinet",
                                                                "King Swamp",
                                                                "a giant chemistry table",
                                                                null,
                                                                null,
                                                                "a table",
                                                                "a table",
                                                                "Nezerath",
                                                                "Mo's Furcadia character",
                                                                null,null,null,
                                                                "the Dead Stone",
                                                                "a pedestal",
                                                                "the Dead Stone",
                                                                "a door",
                                                                null,null,
                                                                "silly Killoseum guys, Chester-Jester and Hornbeard!",
                                                                "a scythe",
                                                                "a pole blade",
                                                                "a green potion",
                                                                "a purple potion",
                                                                "an open coffin... sans dead person",
                                                                "the blue pole and flag",
                                                                "the blue pole",
                                                                "the red pole and flag",
                                                                "the red pole",
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
                                                                "stairs going down",
                                                                "a hole",
                                                                "the head of a dragon",
                                                                "the body of a dragon",
                                                                "the body of a dragon",
                                                                "the tail of a dragon",
                                                                "the leg of a dragon",
                                                                "the leg of a dragon",
                                                                "a bed",
                                                                "a lagbert",
                                                                "a fairy girl",
                                                                "a bed",
                                                                "a person in a spider web",
                                                                "a Queen Scorpion",
                                                                "a round map of the Earth? Crazy!",
                                                                "a rock wall",
                                                                "a metal gate",
                                                                "a door",
                                                                "a rock wall",
                                                                "a metal gate",
                                                                "a door",
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
                                                                "Spidra",
                                                                "a Sorceress",
                                                                "a candle stand",
                                                                "a flower stand",
                                                                "food",
                                                                "a spider necklace",
                                                                "a little dagger",
                                                                "a heart",
                                                                "a fence",
                                                                "a fence",
                                                                "part of a submerged pole",
                                                                "a web",
                                                                "a sword in a stone",
                                                                "a stump",
                                                                "a brown potion",
                                                                "a water spout",
                                                                "a throne o'bones",
                                                                "some carpet",
                                                                "a secret entrance",
                                                                "a chrome cross",
                                                                "a quiver",
                                                                "a DragonSlayer",
                                                                "a bookcase",
                                                                "a fountain",
                                                                "the Warden",
                                                                "some carpet",
                                                                "a prince snail",
                                                                "a huge toadstool",
                                                                "a throne o'bones",
                                                                "the snail mastah",
                                                                "a dead tree",
                                                                "a Succubus",
                                                                "Aahh! SATAN! Run!",
                                                                "some really bad water",
                                                                "a poltergheist",
                                                                "a bed",
                                                                "a bed",
                                                                "a shelf",
                                                                "a bed",
                                                                "a stone altar",
                                                                "a bed",
                                                                "a Doom Blossom",
                                                                "a Doom Blossom",
                                                                "some petrified globlings",
																"a staircase",
                                                                "the Foz",
                                                                "a shelf",
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

	int itemsdisallowed[] = {2,5,10,11,12,19,25,
					32,44,45,46,47,48,49,50,
					53,54,57,58,59,62,82,83,
					84,85,88,89,90,91,92,97,
					98,99,100,101,102,103,104,105,
					107,108,109,110,111,113,114,118,
					119,120,126,128,130,131,132,133,
					134,135,137,139,140,143,149,150,153,
					155,156,157,158,159,160,161,163,
					167,168,169,170,175,176,177,187,
					195,200,204,206,207,209,210,211,
					212,213,214,215,216,217,232,237,
					238,246,247,249,251,252,255,256,
					283,285,293,298,299,300,301,313,
					314,315,320,345,346};

	final int balloonpops[] = {0,3,8,8,11,19,20,25,25,41,46,47,76,107,108,109,110,111,140,143,200,247};
	final int slotsitems[] = {2,5,8,10,11,11,11,11,12,19,25,32,41,46,47,47,47,47,50,50,50,58,91,131,154,126,163,163,163,163,163,196,249};
	final int nonsaves[] = {19,149,150};
	final int losewhendie[] = {19,57,58,59,97,98,99,100,101,102,103,131};
	final int nogive[] = {11,19,57,59,98,101,132,133,134,175,176,177};
	final int jailcoords[][] = {{33,49},{31,46},{34,52}};
	final int othernorespawns[] = {164,165,121,182,183,191,239,240,248,308};


	final short filters=8;
	final String[][] filterstrings = {{"fuck","sniff"},
										{"shit","jello"},
										{"piss","peepee"},
										{"this sucks","I'm having a wonderful time!"},
										{"this|sucks","I'm a moron"},
										{"fag","sexy thang"},
										{"f4g","wonderful person"},
										{"bitch","cute puppy"}};

	static Weapon RED_SCROLL=new Weapon(97,3,7,0,0);
	static Weapon SILVER_SCROLL=new Weapon(102,3,6,0,0);
	static Weapon BASH=new Weapon(-1,10,13,0,0);
	static Weapon CHROME_CROSS=new Weapon(313,4,6,0,0);
	static Weapon BLACK_CROSS=new Weapon(345,4,6,0,0);
	static Weapon NO_WEAPON=new Weapon(0,0,1,0,2);
}
