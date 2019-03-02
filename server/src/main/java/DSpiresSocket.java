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

public class DSpiresSocket extends Thread {
	static DSpiresServer parent;
	static int MAX_STAM = DSpiresServer.MAX_STAM;
	static byte shapestart[][] = DSpiresServer.longShapeStart;

	 //stateChecker
		int stateCheck;

		final static int ST_TRANS  = 1;
		final static int ST_DIALOG = 2;
		final static int ST_GROUP_MEMBER = 4;
		final static int ST_BRB    = 8;
		final static int ST_TRADE  = 16;
		final static int ST_REST   = 32;
		final static int ST_FROZEN = 64;

	//Dialog
		int dialogID=0;

		final static int D_NONE     = 0;
		final static int D_SHOP     = 1;
		final static int D_INTERACT = 2;
		final static int D_TRADE    = 3;
		final static int D_MAP_ENTER= 4;

	Socket my_socket;
	int socket_base_num;
	BufferedReader in;
	PrintWriter out;

	String name="",colorstring="   !",ocolorstring="   !",desc="",pstring="!'+!",password="",email="Unspecified",homepage="Unspecified",title="the newbie";
	char cx='6',cy='J';
	int x,y,facing=1,mstate=1,currshape=2,visishape=2,shapecat=1,inhand,alignment=70,
	    mp=30,stam=MAX_STAM,gold,stamsend=116,pfooti,groupstat,currsword,pkill,loses,
		itemAtPosSave,hp=20,inshop=-1,smenu,speechlimit,restCount,invweight,gotPong=2,
		showadmin,yieldcount=0,mstate2=2,moveCount=0;
	static int mstateLoop[]={-1,0,1,0};
	Weapon weaponO=parent.NO_WEAPON;
	int weapon;
	Armor armorO=new Armor(0,0,1);
	int armour;
	int[] inventory;

	DSMapServer map;
	Vector sight;
	Group Group;
	Trade trade;
	Interactable interaction;
	finishSword swordthread;

	boolean loggedin=false,mute=false,seeattacks=false,channels[] = {true,false,false,false,false,false,false,true},
	        invis=false,follow=false,stopped=false,closed=false,nsync=false;

	public DSpiresSocket(int sbn, Socket s, DSpiresServer p) {
		socket_base_num=sbn;
		my_socket=s;
		parent=p;
		//map=p.maps[0];
		setPriority(Thread.MAX_PRIORITY);
		start();
	}

	public void run() {
		try {
			startUp();
			preLoginLoop();

			setPriority(6);
			// Start main loop
			String incoming;
			while (true) {
				if (++yieldcount > 10) {
					yieldcount = 0;
					yield();
				}
				//try {
					incoming = in.readLine();
				//}
				//catch (InterruptedIOException iie) {
				//	//iie.printStackTrace();
				//	continue;
				//}

				if (incoming==null)
					break;
				else if (incoming.length() > 350) {
					closeIt("Spam[2]");
					break;
				}

				protocol(incoming);
			}
		}
		catch (Exception e) {
			if (!(e instanceof java.net.SocketException
				|| e instanceof java.io.IOException)) {
				System.err.println("Name: "+name);
				e.printStackTrace();
			}

			closeIt(e.toString());
			endIt();
			return;
		}

		closeIt("Run finished");
		endIt();
	}

	public void closeIt(String reason) {
		if (closed)
			return;
		else
			closed=true;
		try {
			out.close();
			in.close();
			my_socket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (parent.socketbase[socket_base_num]==this)
			parent.socketbase[socket_base_num]=null;

		parent.log("Disonnected: "+name+" ("+my_socket.getInetAddress()+") {"+reason+"}");
	}

	public void endIt() {
		try {
			quit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startUp() throws Exception {
		out = new PrintWriter(new BufferedOutputStream(my_socket.getOutputStream()),true);
		in = new BufferedReader(new InputStreamReader(my_socket.getInputStream()));

		sight = new Vector();
		inventory = new int[35];

		pSend(parent.motd);
		pSend("Dragonroar!\nV0026");
	}

	public void pSend(String msg) {
		//This could be useful for counting bytes sent later on.
		out.println(msg);
	}

	public void preLoginLoop() throws Exception {
		String incoming;

		while (true) {
			if (++yieldcount > 10) {
				yieldcount = 0;
				yield();
			}
			//try {
				incoming = in.readLine();
			//}
			//catch (InterruptedIOException iie) {
			//	continue;
			//}

			if (incoming==null) {
				closeIt("preLogin:null");
				continue;
			}

			if (incoming.length() > 300 && !(incoming.startsWith("desc") && pfooti==2)) {
				closeIt("Spam[1]");
				continue;
			}

			if (incoming.startsWith("connect") && incoming.endsWith("\t") && pfooti==0) {
				StringTokenizer st = new StringTokenizer(incoming, " ");
				if (st.countTokens() != 3) {
					closeIt("Hack?[3]");
					continue;
				}
				incoming = st.nextToken();
				String tname = st.nextToken();
				String tpass = st.nextToken();
				tpass = tpass.substring(0,tpass.length()-1);

				int r = doLogin(tname,tpass);

				switch (r) {
					case 0: // Ok!
						loggedin=true;
						parent.userLoggedIn(this);
						addPlayerToMap(2,map);
						return;
					case 1:
						pfooti=1;
						break;
					case 2: // Bad pass.
						pSend("NName already taken. Please try again.");
						break;
				}					
			}
			else if (incoming.startsWith("color") && pfooti==1) {
				colorstring = incoming.substring(6);
				if (colorstring.length()!=4 || colorstring.charAt(3) > '%' || colorstring.charAt(3)==' ')
					colorstring="   !";
				ocolorstring = colorstring;
				pstring = parent.dpstrings[colorstring.charAt(3)-33];
				if (colorstring.charAt(3)-33==4)
					pstring=(Math.round(Math.random()*3)<2?" ":"!")+pstring.substring(1);
				pfooti=2;
			}
			else if (incoming.startsWith("desc") && pfooti==2) {
				desc = incoming.substring(5);
				if (desc.length()>500)
					desc=desc.substring(0,500);
				map = parent.maps[parent.START_MAP];

				savePlayerData();

				parent.log("Created: "+name+" ("+my_socket.getInetAddress()+")");
				parent.created++;
				parent.channelBroadcast(name+" has entered DragonSpires for the first time.",parent.INFO_CHANNEL);
				loggedin=true;
				parent.userLoggedIn(this);
				pSend("&");
				map = parent.maps[parent.START_MAP];
				for (int n = 0; n < channels.length; n++) {
					if (channels[n])
						parent.channels[n].addElement(this);
				}
				pSend("PY"+pstring);
				addPlayerToMap(0,map);
				return;
			}
			else if (incoming.equals("pong")) {
				//if (name.equals("Mech"))
				//	pSend("[#Got pong");
				if (gotPong++ > 5)
					closeIt("Hack?[2]");
			}
			else if (incoming.equals("quit"))
				closeIt("quit[2]");
			else
				closeIt("Hack?[1]");
		}
	}

	public int doLogin(String name, String password) {
		try {
			// Do necessary checks.

			if (!parent.checkNameBans(name)) {
				pSend("NThe name you entered has been banned. Heh.");
				return 3;
			}

			if (name.length() > 30) {
				pSend("NYour name is too long. 30 characters or less, please.");
				return 3;
			}

			name=parent.badCharReplace(name);

			switch (name.charAt(0)) {
				case '[':
				case '*':
				case '~':
					pSend("NYour name can't start with that character.");
					return 3;
			}


			// Passed checks.

			File fname = parent.getPlayerFileForInput(name);

			if (fname!=null) {
				String line;
				BufferedReader i = new BufferedReader(new FileReader(fname));

				// File version check
				String version = i.readLine();

				// IP skip.
				line = i.readLine();


				// Name Check
				line = i.readLine();
				if (!line.equals(name)) {
					i.close();
					return 2;
				}

				// Password Check
				line = i.readLine();
				if (!line.equals(password)) {
					i.close();
					return 2;
				}

				this.name=name;
				this.password=password;

				// Character already logged in?
				for (int n=0;n<parent.SOCKETS;n++) {
					if (parent.socketbase[n]!=null) {
						if (parent.socketbase[n]!=this) {
							if (parent.socketbase[n].name.toLowerCase().equals(name.toLowerCase())) {
								if (!name.toLowerCase().equals("mech") && !name.toLowerCase().equals("motorhed")) {
									i.close();
									pSend("NThat character is already logged in. Sorry.");
									return 3;

									//try {
									//	parent.socketbase[n].pSend("(* Someone else has logged onto this character.");
									//} catch (Exception e) {
									//	e.printStackTrace();
									//}

									//if (parent.socketbase[n]!=null)
									//	parent.socketbase[n].endIt("Someone else[1]",false);


									//i = new BufferedReader(new FileReader(fname));
									//for (int b=0;b<4;b++)
									//	line=i.readLine();
									//break;
								}
							}
						}
					}
				}

				// Character already logged in? [Part 2]
				/*for (int m = 0; m < maps.length; m++) {
					synchronized(maps[m].sockets) {
						Enumeration e = maps[m].sockets.elements();
						while (e.hasMoreElements()) {
							DSpiresSocket f = (DSpiresSocket)e.nextElement();
							if (f.name.equals(name)) {
								i.close();
								stopIt(f);
								try {
									f.pSend("(* Someone else has logged onto this character.",f);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								f.quit("Someone else[2]",f);
								for (int b=0;b<4;b++)
									line=i.readLine();
							}
						}
					}
				}*/


				// Colorstring
				colorstring = i.readLine();
				if (colorstring.charAt(3)==' ')
					colorstring=colorstring.substring(0,3)+"!";
				ocolorstring = colorstring;

				// Description
				desc = i.readLine();

				// Tell client its colorstring and that login was okay
				pSend("color "+colorstring);
				pSend("&");

				// Read in inventory
				char[] linechars = i.readLine().toCharArray();
				for (int n = 0,len=this.inventory.length*2; n < len; n+=2) {
					inventory[n/2] = parent.decode(linechars[n]-32,linechars[n+1]-32);
					if (inventory[n/2]==42)
						inventory[n/2]=0;
					if (inventory[n/2]!=0)
						pSend("i"+parent.toDSChar(n/2)+""+linechars[n]+""+linechars[n+1]);
				}

				// Read in equipment. weapon/armour/hands
				linechars = i.readLine().toCharArray();
				weapon = parent.decode(linechars[0]-32,linechars[1]-32);
				if (weapon!=0) {
					Weapon w=parent.getWeapon(weapon);
					if (w!=null)
						weaponO=w;
					sendEQStats(weaponO);
				}
				armour = parent.decode(linechars[2]-32,linechars[3]-32);
				if (armour!=0) {
					Armor a=parent.getArmor(armour);
					if (a!=null)
						armorO=a;
					sendEQStats(armorO);
				}
				inhand=parent.decode(linechars[4]-32,linechars[5]-32);
				if (inhand!=0)
					setHands(inhand);

				// Read in position (x,y) and map #
				line = i.readLine();
				map = parent.maps[line.charAt(0)-32];
				cx = line.charAt(1);
				cy = line.charAt(2);
				if (cx < ' ' || cy < ' ') {
					map = parent.maps[0];
					cx = '6';
					cy = 'J';
				}
				x=cx-32;
				y=cy-32;

				// Read in gold
				line = i.readLine();
				int g = Integer.parseInt(line);
				if (version.equals("DSP3.1")) {
					if (g > 100000000)
						g = 8008135;
				}
				updateGold(g);

				// Read in channel config
				line = i.readLine();
				for (int n=0;n<line.length();n++) {
					switch (line.charAt(n)) {
						case ' ':
							channels[n] = false;
							break;
						case '!':
							channels[n] = true;
					}
				}

				// Alignment
				line = i.readLine();
				alignment = -256;
				updateAlign((line.charAt(0)-32)+256);

				// Last HP
				line = i.readLine();
				hp = Integer.parseInt(line);
				pSend("$H"+hp);

				// Last stam
				line = i.readLine();
				stam = 0;
				updateStam(Integer.parseInt(line));

				// Mute
				line = i.readLine();
				if (line.startsWith("!"))
					mute=true;

				// Portrait
				pstring=i.readLine();

				// E-mail/web
				email=i.readLine();
				homepage=i.readLine();
				title=i.readLine();
				if (title==null)
					title="";
				else
					title=parent.filterString(title);

				i.close();

				parent.log("Logged in: "+name+" ("+my_socket.getInetAddress()+")");
				parent.channelBroadcast(name+" has entered DragonSpires.",parent.INFO_CHANNEL);

				for (int n = 0; n < channels.length; n++) {
					if (channels[n])
						parent.channels[n].addElement(this);
				}
				
				pSend("PY"+pstring);
				tempInitializeStats();
				return 0;
			}
			else {
				// New character
				if (parent.creation) {
					// Character already logged in?
					for (int n=0;n<parent.SOCKETS;n++) {
						if (parent.socketbase[n]!=null) {
							if (parent.socketbase[n]!=this) {
								if (parent.socketbase[n].name.toLowerCase().equals(name.toLowerCase())) {
									if (!name.toLowerCase().equals("mech") && !name.toLowerCase().equals("motorhed")) {
										//try {
										//	parent.socketbase[n].pSend("(* Someone else has logged onto this character.");
										//} catch (Exception e) {
										//	e.printStackTrace();
										//}
										parent.socketbase[n].closeIt("Someone else[2]");
									}
								}
							}
						}
					}

					this.name = name;
					this.password = password;
					pSend("cs");
					tempInitializeStats();
					return 1;
				}
				else {
					pSend("(DragonSpires character creation has been *temporarily* disabled. Sorry.");
					return 4;
				}
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public void savePlayerData() {
		if (!parent.saving)
			return;

		String fname = parent.getPlayerFilenameForOutput(name)+".temp";
		File oldfile = new File(fname.substring(0,fname.length()-5));
		File newfile = new File(fname);
		try{
			BufferedWriter o = new BufferedWriter(new FileWriter(fname));
			int x=cx-32,y=cy-32;


			o.write("DSP3.2\n");
			o.write(my_socket.getInetAddress()+"\n");
			o.write(name+"\n");
			o.write(password+"\n");
			o.write(ocolorstring+"\n");
			o.write(desc+"\n");

			int tinv;
			for (int i = 0; i < inventory.length; i++) {
				tinv = inventory[i];
				if (tinv > parent.maps[0].items)
						tinv = 0;
				else {
					for (int i2 = 0; i2 < parent.nonsaves.length; i2++) {
						if (tinv == parent.nonsaves[i2])
							tinv = 0;
					}
				}
				o.write(parent.encode(tinv));
			}

			tinv = inhand;
			for (int i2 = 0; i2 < parent.nonsaves.length; i2++) {
				if (tinv == parent.nonsaves[i2]) {
					tinv = 0;
					break;
				}	
			}

			o.write("\n"+parent.encode(weapon)+""+parent.encode(armour)+""+parent.encode(tinv)+"\n");
			if (cx < ' ' || cy < ' ') {
				map = parent.maps[0];
				cx = '6';
				cy = 'J';
			}

			if (map.mapnumber==map.diemap
				 && !(map instanceof BasicMap)
				 && (parent.itemdefs[map.itemmap[x][y]][0] == 0 || (map.mapnumber==31&&parent.itemdefs[map.oitemmap[x][y]][0] == 0))
				 && parent.floorwalk[map.tilemap[x][y]] == 0
				)
					o.write(parent.toDSChar(map.mapnumber)+""+cx+""+cy+"\n");
			else {
				int tx,ty;
				DSMapServer mm;
				if (map instanceof BasicMap)
					mm=parent.maps[map.parentmap.mapnumber];
				else
					mm=parent.maps[map.diemap];
				while (true) {
					tx = parent.getRandomStartX(mm);
					ty = parent.getRandomStartY(mm);
					
					if (mm.canWalk(tx,ty,mm))
						break;
				}
				o.write(parent.toDSChar(mm.mapnumber)+""+parent.toDSChar(tx)+""+parent.toDSChar(ty)+"\n");
			}

			String goldstring=""+gold;
			while (goldstring.length()<	9)
				goldstring="0"+goldstring;
			o.write(goldstring+"\n");
			for (int i = 0; i < channels.length; i++) {
				char blah = '!';
				if (!channels[i])
					blah = ' ';
				o.write(blah);
			}
			o.write('\n');
			o.write(parent.toDSChar(alignment)+"\n");
			o.write(hp+"\n");
			o.write(stam+"\n");

			if (mute)
				o.write("!\n");
			else
				o.write(" \n");

			o.write(pstring+"\n");
			o.write(email+"\n");
			o.write(homepage+"\n");
			o.write(title+"\n");

			o.close();

			oldfile.delete();
			newfile.renameTo(oldfile);
		}
		catch (IOException e) {
			System.err.println("Save(0) exception: "+e.getMessage());
			e.printStackTrace();
			try { newfile.delete(); } catch (Exception ex) {}
			if (e.getMessage().equals("No space left on device")) {
				parent.saving=false;
				parent.creation=false;
				parent.globalBroadcast("[\"Warning: character saving has been automatically disabled. Please tell an administrator.");
			}
			/*try {
				if (oldfile.exists()) {
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}*/
		}
	}

	public void protocol(String incoming) {
			//if (parent.LOG_COMMANDS)
			//	parent.logCommands(incoming, this);

			if (stateCheck!=0) {
				if (doStateChecker(incoming))
					return;
			}

			switch (incoming.charAt(0)) {
				case 'm':
					move(incoming);
					break;
				case 's':
					switch (incoming.charAt(1)) {
						case 'w': //swing
							doSwing();
							return;
						case 'h': //sh
							if (++restCount>8) return;
							requestDialog(D_SHOP);
							//return;
					}
					break;
				case '<':
					rotateLeft();
					break;
				case '>':
					rotateRight();
					break;
				default:
					secondaryProtocol(incoming);
			}
	}

	public boolean checkMoveCount() {
		if (++moveCount>20) {
			//System.err.println("Got a moveCount(move) of: "+moveCount);
			if (moveCount>90) {
				closeIt("Spam: Move");
				return false;
			}
			return true;
		}
		return false;
	}

	public void move(String incoming) {
		if (checkMoveCount())
			return;

		if (!updateStam(map.stamval))
			return;

		facing = incoming.charAt(2)-48;
		currshape=shapestart[shapecat][facing-1];

		//mstate*=-1;
		visishape=currshape+mstateLoop[mstate2];
		if (++mstate2==4)
			mstate2=0;


		int tx = parent.nextx(x,y,facing);
		int ty = parent.nexty(y,facing);

		boolean didmove = map.canWalk(tx,ty,map);

		if (didmove) {
	
			if (parent.itemdefs[map.itemmap[tx][ty]][4]==1) {
				if (stepOnItem(tx,ty,map.itemmap[tx][ty],0))
					return;
			}

			if (checkExits(tx,ty))
				return;

			char bx = cx;
			char by = cy;

			setPosition(tx,ty);
			//cx = parent.toDSChar(tx);
			//cy = parent.toDSChar(ty);
			//x=tx;
			//y=ty;

			if (groupstat==2)
				Group.moveGroup(bx,by,visishape);
			else {
				pSend("~");
				pSend("@"+cx+""+cy);
				map.playerMoveBroadcast(visishape,colorstring,cx,cy,bx,by,this,map);
				pSend("=");
			}

			if (map.itemmap[tx][ty] != pfooti)
				setFeet(map.itemmap[tx][ty]);

			if (map.walktrig)
				map.walkTrigger(this);

		}
		else {
			if (parent.itemdefs[map.itemmap[tx][ty]][4]==1) {
				if (stepOnItem(tx,ty,map.itemmap[tx][ty],1))
					return;
			}
			map.playerPlaceBroadcast(visishape,colorstring,cx,cy,this,map);
		}
	}

	public void doSwing() {
		if (!updateStam(weaponO.stam))
			return;
		map.playermap[x][y]=visishape;
		if (colorstring.charAt(3) != ' ') {
			if (weaponO.projectile!=0) {
				doProjectile();
				return;
			}
			int tx = parent.nextx(x,y,facing);
			int ty = parent.nexty(y,facing);
			if (currsword==2)
				currsword=3;
			else
				currsword=2;
			sightSend("<"+cx+""+cy+""+parent.toDSChar(currshape+currsword)+""+colorstring);
			sightSendAttacks("!3",null);
			if (map.playermap[tx][ty] != 0)
				checkHit(tx,ty);
			else if (parent.itemdefs[map.itemmap[tx][ty]][2] != 0)
				swingItemCheck(map.itemmap[tx][ty]);
		}
		else {
			sightSend("<"+cx+""+cy+""+parent.toDSChar(currshape+2)+""+colorstring);
			sightSend("!5");
		}
		
		if (swordthread!=null)
			swordthread.count=4;
		else {
			swordthread = new finishSword(this);
			swordthread.start();
		}

		//new finishSword(c);
		//sightSend("<"+cx+""+cy+""+parent.toDSChar(currshape)+""+colorstring,c);
		//spellsweeparent.maps[mapnumber].limitedBroadcast("*"+parent.toDSChar(tempo[0])+""+parent.toDSChar(tempo[1])+"!",tempo[0],tempo[1],parent.maps[mapnumber]);
	}

	public void secondaryProtocol(String incoming) {
		if (++restCount>8) {
			//if (name.equals("Mech"))
			//	pSend("(restCount="+restCount);
			return;
		}

		switch (incoming.charAt(0)) {
				case '"':
					doTextInput(incoming);
					break;
				case 'i': //is
					doInventorySwitch(incoming.charAt(2));
					break;
				case 'r':
					doRest();
					break;
				case 'e': //eq
					doEquip();
					break;
				case 'w':
					switch (incoming.charAt(1)) {
						case 'r': //wr
							doWear();
							return;
						case 'h': //who
							pSend("(Current players: "+parent.getWho());
							return;
					}
					break;
				case 't': //ti
					doThrow();
					break;
				case 'u':
					doUse(inhand);
					break;
				case 'g': //get
					doGet();
					break;
				case 'l': //look
					doLook();
					break;
				case 'P':
					pstring=incoming.substring(1);
					break;
				case 'c':
					requestDialog(D_NONE);
					break;
				case ']':
					int tx=parent.nextx(cx-32,cy-32,facing);
					int ty=parent.nexty(cy-32,facing);
					if (incoming.equals("]iwannaupload")) {
						//pSend("(Let's see what happens if uploads aren't allowed for awhile. Hmm.");
						//if (incoming.equals("]iwannaupload"))
						//	return;

						if (!map.allowupload) {
							pSend("(* Uploads aren't allowed on this map.");
							return;
						}
						if (!map.canWalk(tx,ty,map) || map.itemmap[tx][ty]!=0) {
						//if (map.playermap[tx][ty]!=0
						//	|| map.itemmap[tx][ty]!=0) {
							pSend("(* Can't upload. Something is in the way.");
							return;
						}

						int[] atestthing = {1,3,7,9};
						for (int i=0;i<4;i++) {
							int px=parent.nextx(tx,ty,atestthing[i]);
							int py=parent.nexty(ty,atestthing[i]);
							if (map.itemmap[px][py]==151) {
								pSend("(* You can't upload a portal where it touches another portal.");
								return;
							}
						}

						if (okToAttack(tx,ty,map) || okToAttack(x,y,map)) {
							pSend("(* This is a combat area. You shouldn't upload a map here.");
							return;
						}

						if (!(name.equals("Mech") || name.equals("Motorhed"))) {
							if (parent.alreadyHasAMap(name)) {
								pSend("(* You already have a map uploaded. You need to use '-destroymap' on your old map.");
								return;
							}
						}

						int i;
						for (i=0;i<map.portals.length;i++) {
							if (map.portals[i]==null)
								break;
						}
						if (i==map.portals.length) {
							for (i=0;i<map.portals.length;i++) {
								if (map.portals[i]==null)
									break;
								if (map.portals[i].dest_map.sockets.size()==0) {
									map.destroyMap(map.portals[i].dest_map);
									break;
								}
							}
							if (i==map.portals.length) {
								pSend("(* No open space for uploads on this map.");
								return;
							}
						}
						//if (map.portals[i]==null)
							map.portals[i]=new Portal(false);
						//else {
						//	pSend("(* Hmm.. try again, please.");
						//	return;
						//}
						BasicMap m = new BasicMap(null,parent);
						pSend("]uploadit");
						Exception e = m.read_map_from_reader(in,m,this);
						if (e!=null) {
							pSend("(* Error(1) with map upload, sorry.");
							map.portals[i]=null;
							return;
						}
						try {
							if (in.readLine().equals("]scriptyes")) {
								Exception e2 = m.readMapScriptFromReader(in,name+"'s_map",m);
								if (e2!=null) {
									pSend("(* Error(2) with map upload, sorry.");
									map.portals[i]=null;
									return;
								}
							}
						}
						catch (Exception e3) {}

						m.owner=name;
						m.parentmap=map;
						//map filter
						for (int x=0;x<m.mwidth;x++) {
							for (int y=0;y<m.mheight;y++) {
								if (parent.findIntInArray(m.itemmap[x][y],parent.itemsdisallowed))
									m.itemmap[x][y]=0;
							}
						}
						map.portals[i].dest_map=m;
						map.portals[i].orig_x=tx;
						map.portals[i].orig_y=ty;
						map.portals[i].active=true;
						//m.entrytext="[\'[ You enter "+name+"'s map. ]\n[&"+m.entrytext;
						map.placeItemAt(151,tx,ty,map);
						pSend("[%Upload successful!");
						return;
					}
					break;
				default:
					incoming = incoming.toLowerCase();
					if (incoming.equals("quit"))
						//quit("quit");
						closeIt("Quit");
					else if (incoming.equals("pong")) {
						gotPong++;
						//if (name.equals("Mech"))
						//	pSend("[#Got pong");
					}
					else
						closeIt("Hack?[4]");
						//pSend("[)[pong]");
					//	doPreLoginCheck(incoming);
		}
	}

	public void addPlayerToMap(int type, DSMapServer m) {
		
		map=m;

		m.sockets.addElement(this);

		//String buffer="";

		if (m instanceof BasicMap) {
			int pri = getPriority();
			setPriority(2);
			yield();
			pSend("]download");
			pSend("ad:"+parent.ads.elementAt((int)Math.round(Math.random() * (parent.ads.size()-1))));


					//out.print(parent.mapencode(m.tilemap[x][y]));
					//buffer+=parent.mapencode(m.tilemap[x][y]);

					//out.print(parent.mapencode(m.itemmap[x][y]));
					//buffer+=parent.mapencode(m.itemmap[x][y]);

				//out.print(buffer);
				//buffer="";

			int buffer_size = 1024,mapasscount=0;
			char[] buffer = new char[buffer_size];
			int[] mapass = new int[m.mwidth*m.mheight*2];
			String astring;

			for (int x=0;x<m.mwidth;x++) {
				for (int y=0;y<m.mheight;y++) {
					mapass[mapasscount]=m.tilemap[x][y];
					mapasscount++;
				}
			}
			for (int x=0;x<m.mwidth;x++) {
				for (int y=0;y<m.mheight;y++) {
					mapass[mapasscount]=m.itemmap[x][y];
					mapasscount++;
				}
			}

			for (int i=0, n=0;i<mapass.length;) {
				for (n=0;n<buffer_size&&i<mapass.length;n++,i++) {
					astring=parent.encode(mapass[i]);
					buffer[n++]=astring.charAt(0);
					buffer[n]=astring.charAt(1);
				}
				out.print("]data");
				out.write(buffer,0,n);
				out.println();
				try {
					sleep(600);
				}
				catch (Exception e) {
					e.printStackTrace();
				}	
			}

			pSend("ad:off");
			pSend("]fin");
			//buffer=null;
			setPriority(pri);
		}
		else
			pSend("]"+m.mapname);

		if (m.safemap)
			pSend("[(* This is a safe map; if you -brb here you won't be hurt by enemies.");

		if (m.allowupload)
			pSend("[#* You can upload maps on this map.");

		if (m instanceof BasicMap && type!=2) {
			m.broadcast("[\'[ "+name+" enters the map. ]",m);
			pSend("[\'[ You enter "+m.owner+"'s map. ]");
		}

		if (m.entrytext!=null)
			pSend(m.entrytext);

		int tx = x;
		int ty = y;
		if (type == 1) {
			int tx2 = parent.nextx(tx,ty,facing);
			int ty2 = parent.nexty(ty,facing);
			boolean didxmap = false;
			if (tx2 == parent.exitpoints[1]) {
				ty = y;
				tx = parent.exitpoints[0];
				didxmap = true;
			}
			else if (tx2 == parent.exitpoints[0]) {
				ty = y;
				tx = parent.exitpoints[1];
				didxmap = true;
			}
			if (!didxmap) {
				if (ty2 == parent.exitpoints[2]) {
					tx = tx2;
					ty = parent.exitpoints[3]-1;
				}
				else if (ty2 == parent.exitpoints[3]) {
					tx = tx2;
					ty = parent.exitpoints[2]+1;
				}
			}
			else {
				if (ty > 0 && ty < parent.exitpoints[3]) {
					if (facing == 7 || facing == 9)
						ty--;
					else
						ty++;
				}
			}
		}
		else if (type == 0) {
			int count=0;
			while (true) {
				tx=parent.getRandomStartX(m);
				ty=parent.getRandomStartY(m);
	
				if (m.canWalk(tx,ty,m))
					break;
				if (++count==50)
					break;
			}
		}

		//x = tx;
		//y = ty;

		setPosition(tx,ty);

		if (type==0) {
			facing = 1;
			currshape=shapestart[shapecat][facing-1];
			visishape=currshape;
		}
		pSend("@"+cx+""+cy);

		if (m.playermap[tx][ty] != 0) {
			DSpiresSocket ts = findSocketAtPos(tx,ty,m,1);
			if (ts != null) {
				if (ts.groupstat==0) {
					ts.transformPlayerToItem(144);
					sightSend(parent.assembleSpellString(152,x,y));
					m.limitedBroadcast("[#"+name+" plummets from the sky, smooshing "+ts.name+" flat.",tx,ty,m);
				}
			}
		}
		for (int x = 0; x < map.mwidth; x++) {
			for (int y = 0; y < map.mheight; y++) {
				if (m.itemmap[x][y] != m.oitemmap[x][y])
					pSend(">"+parent.toDSChar(x)+""+parent.toDSChar(y)+""+parent.encode(map.itemmap[x][y]));
			}
		}
		m.playerPlaceBroadcastWithRefresh(currshape,colorstring,x,y,this,m);

		setFeet(map.itemmap[tx][ty]);
		pSend("=");
	}
	public void sendEQStats(Object eq) {
		if (eq instanceof Weapon)
			pSend("e "+parent.encode(((Weapon)eq).item)+""+parent.toDSChar(((Weapon)eq).mindam)+""+parent.toDSChar(((Weapon)eq).maxdam));
		else //if (eq instanceof Armor)
			pSend("e!"+parent.encode(((Armor)eq).item)+""+parent.toDSChar(((Armor)eq).mindam)+""+parent.toDSChar(((Armor)eq).maxdam));
	}
	public void setHands(int item) {
		inhand = item;
		pSend("^"+parent.encode(item));
	}
	public void setFeet(int item) {
		pfooti = item;
		pSend("%"+parent.encode(item));
	}
	public void updateGold(int value) {
		gold+=value;
		if (gold<0)
			gold=0;
		else if (gold>888888888)
			gold=888888888;
		pSend("G"+gold);
	}
	public void updateAlign(int update) {
		if (update<256)
			pSend("(You "+((update>0) ? "gain "+update : "lose "+(update*-1))+" alignment points.");

		int ca = alignment/20;
		alignment+=update;
		if (alignment < 1)
			alignment = 1;
		else if (alignment > 124)
			alignment = 124;
		if (ca != alignment/20) {
			pSend("al"+parent.toDSChar(alignment/20));
			if (update<256)
				pSend("(You are now "+parent.aligntexts[alignment/20]+".");
		}
	}
	public boolean updateStam(int value) {
		if ((stam+=value)<0) {
			stam-=value;
			return false;
		}
		else if (stam > MAX_STAM)
			stam = MAX_STAM;

		int stamsend2=(int)(((float)stam/(float)MAX_STAM)*116);
		
		if (stamsend!=stamsend2) {
			stamsend=stamsend2;
			pSend("#"+parent.toDSChar(stamsend2));
		}

		return true;			
	}
	public void tempInitializeStats() {
		/*int neweq;

		if (weapon > 0) {
			neweq = parent.findWAIndex(weapon,parent.weapons);
			if (neweq != -1) {
				str+=parent.weapons[neweq+1];
				def+=parent.weapons[neweq+2];
				agi+=parent.weapons[neweq+3];
				dex+=parent.weapons[neweq+4];
				wiz+=parent.weapons[neweq+5];
			}
		}

		if (armour > 0) {
			neweq = parent.findWAIndex(armour,parent.armours);
			if (neweq != -1) {
				str+=parent.armours[neweq+1];
				def+=parent.armours[neweq+2];
				agi+=parent.armours[neweq+3];
				dex+=parent.armours[neweq+4];
				wiz+=parent.armours[neweq+5];
			}
		}

		pSend("$S"+str+"\n$D"+def+"\n$A"+agi+"\n$X"+dex+"\n$W"+wiz,c);*/

		for (int i = 0; i < inventory.length; i++) {
			invweight+=parent.itemdefs[inventory[i]][5];
		}
	}
	public void sightSend(String message) {
		DSpiresSocket c;
		//synchronized (sight) {
			Enumeration e = sight.elements();
			while (e.hasMoreElements()) {
				try {
					c = (DSpiresSocket)e.nextElement();
				}
				catch (NoSuchElementException ex) {
					break;
				}
				c.pSend(message);
			}
		//}
	}
	public void sightSendAttacks(String message, DSpiresSocket s2) {
		DSpiresSocket c;
		//synchronized (sight) {
			Enumeration e = sight.elements();
			while (e.hasMoreElements()) {
				try {
					c = (DSpiresSocket)e.nextElement();
					if (!c.seeattacks) {
						if (c!=this&&c!=s2)
							continue;
					}
					c.pSend(message);
				}
				catch (NoSuchElementException exc) {
				}
			}
		//}
	}
	public boolean doStateChecker(String incoming) {
		if (++restCount>19) return true;

		if (bitIsMarked(stateCheck,ST_TRANS)) {
			if ((!incoming.startsWith("\"") || incoming.startsWith("\"-")) && !incoming.startsWith("is") && !incoming.equals("pong")) {
				undoTransformPlayerToItem();
			}
		}
		if (bitIsMarked(stateCheck,ST_DIALOG)) {
			switch (incoming.charAt(0)) {
				case 'm':
				case '<':
				case '>':
				case 's':
					//pSend("(Hmm",c);
					requestDialog(D_NONE);
			}
		}
		if (bitIsMarked(stateCheck,ST_GROUP_MEMBER)) {
			if (incoming.charAt(0)=='m'&&!incoming.startsWith("mg1"))
				Group.removeMember(this);
		}
		if (bitIsMarked(stateCheck,ST_BRB)) {
			if (!incoming.equals("pong")&&!incoming.equals("mg"))
				doBRB();
		}
		if (bitIsMarked(stateCheck,ST_TRADE)) {
			if (incoming.startsWith("\"") && !incoming.startsWith("\"-"))
				return false;
			switch (incoming.charAt(0)) {
				case 'i':
				case 'g':
				case '\"':
				case 'm':
				case 'e':
				case 'w':
				case 't':
				case '<':
				case '>':
					trade.cancelTrade(0,trade);
			}
		}
		if (bitIsMarked(stateCheck,ST_REST)) {
			if ((incoming.startsWith("\"") && !incoming.startsWith("\"-")) || incoming.toLowerCase().startsWith("\"-brb"))
				return false;
			switch (incoming.charAt(0)) {
				case 'm':
				case '<':
				case '>':
				case 't':
				case 's':
				case 'g':
					pSend("(You're resting. You don't feel like doing that right now.");
					return true;
			}

		}
		if (bitIsMarked(stateCheck,ST_FROZEN)) {
			return true;
		}
		return false;
	}
	public boolean bitIsMarked(int tocheck, int condition) {
		return ((tocheck & condition) == condition);
	}
	public /*synchronized*/ void changeMap(DSMapServer newmap, int type,int x,int y) {
		if (nsync)
			return;

		nsync=true;
		boolean doit=true;

		if (!loggedin)
			doit=false;

		//if (++restCount>10)
		//	doit=false;

		if (doit) {
			if (stateCheck!=0)
				doStateChecker("mg"+type);
			if (dialogID!=D_NONE)
				requestDialog(D_NONE);
			if (groupstat==2) {
				if (type==1)
					Group.groupChangeMap(newmap);
				else
					Group.disbandGroup();
			}

			pSend("~");

			map.sockets.removeElement(this);
			map.playerPlaceBroadcast(0,colorstring,cx,cy,this,map);

			if (type==2&&x!=0) {
				//cx=parent.toDSChar(x);
				//cy=parent.toDSChar(y);
				//this.x=x;
				//this.y=y;
				setPosition(x,y);
			}

			map = newmap;

			addPlayerToMap(type,map);
		}

		nsync=false;
	}
	public /*synchronized*/ void transformPlayerToItem(int item) {
		if (nsync)
			return;

		nsync = true;

		//if (!loggedin) return;

		killFinishSword();
		itemAtPosSave = map.itemmap[x][y];
		visishape=0;

		stateCheck|=ST_TRANS;
		map.playerPlaceBroadcast(0,"",cx,cy,this,map);
		map.placeItemAt(item,x,y,map);
		sightSend("!6");

		nsync=false;
	}
	public DSpiresSocket findSocketAtPos(int x, int y, DSMapServer m, int statesensitive) {
		DSpiresSocket c;
		//synchronized(m.sockets) {
			Enumeration e = m.sockets.elements();
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();
				if (c.cx-32 == x && c.cy-32 == y && ((statesensitive==0)||(statesensitive==1 && !bitIsMarked(c.stateCheck,ST_TRANS))))
					return c;
			}
		//}
		return null;
	}
	public DSpiresSocket findSocketAtPosInSight(int x, int y, int statesensitive) {
		//synchronized(sight) {
			Enumeration e = sight.elements();
			DSpiresSocket c;
			while (e.hasMoreElements()) {
				c = (DSpiresSocket)e.nextElement();
				if (c.cx-32 == x && c.cy-32 == y && ((statesensitive==0)||(statesensitive==1 && !bitIsMarked(c.stateCheck,ST_TRANS))))
					return c;
			}
		//}
		return null;
	}
	public static DSpiresSocket getSocketByName(String name) {
		DSpiresSocket c;
		for (int i=0;i<parent.SOCKETS;i++) {
			if (parent.socketbase[i]!=null) {
				if (parent.socketbase[i].loggedin) {
					c = parent.socketbase[i];
					if (c.name.toLowerCase().equals(name.toLowerCase()))
						return c;
				}
			}
		}
		return null;
	}
	public DSpiresSocket getSocketByNearestString(String name) {
		DSpiresSocket tc = null,c;
		for (int i=0;i<parent.SOCKETS;i++) {
			if (parent.socketbase[i]!=null) {
				if (parent.socketbase[i].loggedin) {
					c = parent.socketbase[i];
					if (c.name.toLowerCase().equals(name.toLowerCase()))
						return c;
					else if (c.name.toLowerCase().startsWith(name.toLowerCase())) {
						if (tc == null)
							tc = c;
						else {
							pSend("(* There's more than one person on DragonSpires with a name starting with '"+name+"'.");
							return null;
						}
					}
				}
			}
		}
		if (tc == null)
			pSend("(* There's no one on DragonSpires with the name '"+name+"'.");
		return tc;
	}
	public static int W2A(Weapon weapon, Armor armor) {
		int retval = (parent.dice(1,weapon.maxdam-weapon.mindam-1)+weapon.mindam)-(parent.dice(1,armor.maxdam-armor.mindam-1)+armor.mindam);
		if (retval<0)
			return 0;
		return retval;
	}
	public /*synchronized*/ void killMe(boolean losestuff) {
		if (nsync)
			return;

		nsync=true;

		//if (!loggedin) return;

		restCount=0;

		map.playerPlaceBroadcast(0,colorstring,cx,cy,this,map);

		if (trade!=null)
			trade.cancelTrade(0,trade);
		if (groupstat==2)
			Group.disbandGroup();
		else if (groupstat==1)
			Group.removeMember(this);

		if (losestuff) {
			pSend("[(You've been killed!");
			//System.out.println("DEATH: "+name+" killed by an enemy.");

			// Old losegold
			//updateGold((int)-(gold*(float)((float)parent.dice(1,6)/(float)10)),c);
			//for (int i = 0; i < inventory.length; i++) {
			//	if (parent.findIntInArray(inventory[i],parent.losewhendie)) {
			//		if (parent.dice(1,10)<=6) {
			//			inventory[i] = 0;
			//			pSend("i"+\parent.toDSChar(i)+""+parent.encode(inventory[i]),c);
			//		}
			//	}
			//}

			int newgold=gold/2;
			updateGold(-newgold);

			//New BagOStuff
			BagOStuff b=map.addBag(x,y,newgold,new int[0],map);
			for (int i=0;i<inventory.length;i++) {
				if (inventory[i]!=0)
					b.addItem(inventory[i],b,map);
			}

			inventory=new int[35];
			invweight=0;
			pSend("cinv");

			if (weapon!=0) {
				//deEquipWeapon(weapon,c);
				b.addItem(weapon,b,map);
				weapon=0;
				//pSend("e    !",c);
				weaponO=parent.weaponIndex[0];
				sendEQStats(weaponO);
			}
			if (armour!=0) {
				//deEquipArmor(armour,c);
				b.addItem(armour,b,map);
				armour=0;
				//pSend("e!   !",c);
				armorO=parent.armorIndex[0];
				sendEQStats(armorO);
			}
			//sendStats(c);
			if (inhand!=0) {
				b.addItem(inhand,b,map);
				setHands(0);
			}
		}
		else {
			if (inhand != 0) {
				if (inhand==145||inhand==146) {}	
				else if (map.itemmap[x][y] == 0 || map.mapnumber == 22)
					map.placeItemAt(inhand,x,y,map);
				//else if (inhand == 149 || inhand == 150)
				//	parent.recoverFlag(c);
				//else if (!(inhand == 145 || inhand == 146))
				//	map.itemRecover(inhand,map);
				setHands(0);
			}
		}

        nsync=false;
		if (map.diemap != map.mapnumber)
			changeMap(parent.maps[map.diemap],0,0,0);
		else
			movePlayerIntoStartArea();
		nsync=true;
		hp = 20;
		pSend("$H20");

		nsync = false;
	}
	public boolean doDamageToPlayer(int damage) {
		if ((hp-=damage) <= 0)
			return true;
		pSend("$H"+hp);
		return false;
	}
	public int extraDamage(int x, int y) {
		int edamage=0;
		switch (inhand) {
			case 99:
				if (chargeMP(7,false)) {
					edamage=1;
					sightSendAttacks("[\'"+name+" summons lightning from the heavens!\n"+parent.assembleSpellString(288,x,y),null);
				}
				break;
			case 131:
			case 133:
				if (chargeMP(4,false)) {
					edamage=(int)Math.round(Math.random()*2)+1;
					sightSendAttacks("[\'"+name+" calls upon the power of the runes!\n"+parent.assembleSpellString(288,x,y),null);
				}
		}
		return edamage;
	}
	public boolean chargeMP(int value, boolean notify) {
		if (mp-value < 0) {
			if (notify)
				pSend("[\'You don't have enough energy to use this magic.");
			return false;
		}
		else {
			mp-=value;
			pSend("$M"+parent.toDSChar(mp));
			return true;
		}
	}
	public /*synchronized*/ void movePlayerIntoStartArea() {
		//if (!loggedin) return;
		
		if (nsync)
			return;

		nsync=true;

		if (stateCheck!=0)
			doStateChecker("m");
		if (groupstat==2)
			Group.disbandGroup();

		int x,y,count=0;

		while (true) {
			x = parent.getRandomStartX(map);
			y = parent.getRandomStartY(map);
			
			try {
				if (map.canWalk(x,y,map))
					break;
			} catch (Exception e) {}
			if (++count==50)
				break;
		}

		//sight.removeElement(c);
		//clearSight(c);
		map.playerPlaceBroadcast(0,colorstring,cx,cy,this,map);

		//cx = parent.toDSChar(x);
		//cy = parent.toDSChar(y);
		setPosition(x,y);
		pSend("~\nCP\n@"+cx+""+cy);
		map.playerPlaceBroadcastWithRefresh(currshape,colorstring,x,y,this,map);
		setFeet(map.itemmap[x][y]);
		pSend("=");

		nsync=false;
	}
	public void requestDialog(int id) {
		switch (dialogID) {
			case D_SHOP:
				if (id != 1) {
					inshop = -1;
					//if (groupstat!=1)
					//	checkBeforeMove=false;
				}
				else
					return;
				break;
			case D_INTERACT:
				interaction = null;
				//if (groupstat!=1)
				//	checkBeforeMove=false;
				break;
			case D_TRADE:
				if (trade!=null)
					trade.cancelTrade(1,trade);
				break;
			case D_MAP_ENTER:
				if (id==4)
					return;
				//if (groupstat!=1)
				//	checkBeforeMove=false;
				break;
			//case 0:
			//	break;
		}

		dialogID = id;

		switch (id) {
			case D_NONE:
				pSend("cd");
				break;
			case D_SHOP:
				if (!doShop())
					dialogID=0;
				break;
		}

		if (dialogID==D_NONE)
			stateCheck^=ST_DIALOG;
		else
			stateCheck|=ST_DIALOG;

	}
	public boolean doShop() {
		if (colorstring.charAt(3)==' ') {
			pSend("(You bark and beg for scraps for no reason.");
			return false;
		}
		if (map.shops != null) {
			for (int i = 0; i < map.shops.length; i++) {
				int[] tempo = map.shops[i].rect;
				if (x >= tempo[0] && x <= tempo[2] && y >= tempo[1] && y <= tempo[3]) {
					tempo = map.shops[i].tiles;
					if (map.tilemap[x][y] == tempo[0] || map.tilemap[x][y] == tempo[1]) {
						if (map.shops[i].sendMenu(0,map.shops[i],this)) {
							//checkBeforeMove=true;
							
							return true;
						}
						return false;
					}
				}
			}
		}
		pSend("(There's nowhere to shop here!");
		return false;
	}
	public void killFinishSword() {
		if (swordthread!=null) {
			swordthread.interrupt();
			swordthread=null;
		}
	}
	public void doBRB() {
		if (bitIsMarked(stateCheck,ST_BRB)) {
			if (colorstring.charAt(3) != ' ') {
				map.playerPlaceBroadcast(21,colorstring,cx,cy,this,map);
				visishape=21;
			}

			stateCheck^=ST_BRB;

			itemAtPosSave=parent.checkItemReplace(itemAtPosSave,x,y,map);

			map.placeItemAt(itemAtPosSave,cx-32,cy-32,map);
			itemAtPosSave=0;
		}
		else {
			//if (++restCount>10)
			//	return;

			killFinishSword();

			if (colorstring.charAt(3) != ' ') {
				map.playerPlaceBroadcast(22,colorstring,cx,cy,this,map);
				visishape=22;
			}

			itemAtPosSave = map.itemmap[cx-32][cy-32];
 
			stateCheck|=ST_BRB;
			map.placeItemAt(181,cx-32,cy-32,map);
			sightSend(parent.assembleSpellString(181,cx-32,cy-32));
		}
	}
	public void undoTransformPlayerToItem() {
		itemAtPosSave=parent.checkItemReplace(itemAtPosSave,x,y,map);
		map.placeItemAt(itemAtPosSave,x,y,map);
		itemAtPosSave=0;
		visishape=currshape;
		map.playerPlaceBroadcast(currshape,colorstring,cx,cy,this,map);
		stateCheck^=ST_TRANS;
	}
	public void swingItemCheck(int theitem) {
		if (++restCount>3) {
			if (theitem==60 || theitem==317)
				updateStam(-stam);
			return;
		}

		if (colorstring.charAt(3) == ' ') return;

		int tx = parent.nextx(cx-32,cy-32,facing);
		int ty = parent.nexty(cy-32,facing);

		if (parent.itemdefs[theitem][2]==2) {
			if (checkHit2(tx,ty))
				return;
		}

		switch (theitem) {
			case 8:	
					map.limitedBroadcast(parent.assembleSpellString(16,tx,ty),tx,ty,map);
					map.placeItemAt(0,tx,ty,map);
					break;
			case 15:
					if (checkHolders(tx,ty))
						map.placeItemAt(31,tx,ty,map);
					break;
			case 31:	map.placeItemAt(15,tx,ty,map);
					break;
			case 60:
			case 317:
				//if (++restCount>5)
				//	break;
					if (map.fountainx==tx&&map.fountainy==ty) {
						pSend("[&You are healed by the magical water of the fountain!");
						sightSend(parent.assembleSpellString(309,cx-32,cy-32));
						updateStam(MAX_STAM);
						hp=20;
						pSend("$H"+hp);
					}
					break;
			case 70:	map.placeItemAt(71,tx,ty,map);
					break;
			case 71:	map.placeItemAt(70,tx,ty,map);
					break;
			case 72:	map.placeItemAt(73,tx,ty,map);
					break;
			case 73:	map.placeItemAt(72,tx,ty,map);
					break;
			case 26:
					doLaptopHit(tx,ty);
					break;
			/*case 27:
					if (++restCount>2)
						break;
					if (map.mapnumber!=31) {
						//map.limitedBroadcast("(An innocent-looking, little flower did slay "+name+"!",cy-32,cy-32,map);
						//parent.channelBroadcast("Patrick boots "+name+" offline!",parent.COMBAT_CHANNEL);
						parent.channelBroadcast("Patrick did slay "+name+"!",parent.COMBAT_CHANNEL);
						updateAlign(-5);
						killMe(false);
						//closeIt("Patrick");

					}
					break;*/
			case 84:	map.placeItemAt(113,tx,ty,map);
					break;
			case 85:	map.placeItemAt(62,tx,ty,map);
					break;
			case 23:
				if (map.mapnumber==0) {
					if (parent.checkForTrans(-1,tx,ty,map)==-1 && !(tx==28&&ty==19)) {
						int f = (int)Math.round(Math.random()*10);
						if (f<4)
							map.placeItemAt(0,tx,ty,map);
					}
				}
				break;
			case 164:
			case 165:
			case 121:
			case 182:
			case 183:
			case 191:
			case 239:
			case 240:
			case 248:
			case 308:
					popPotion(map.itemmap[tx][ty],tx,ty);
					break;
			case 136:
			case 218:
			case 316:
			case 331:
			case 340:
					checkHolders(tx,ty);
					break;
			case 139:
					map.placeItemAt(map.items+41,tx,ty,map);
					break;
			case 141:	map.placeItemAt(142,tx,ty,map);
					break;
			case 142:	map.placeItemAt(141,tx,ty,map);
					break;
			case 145: //green flag pole
					if (map.mapnumber==22) {
					if (inhand == 0) {
						map.placeItemAt(147,tx,ty,map);
						setHands(149);
						map.broadcast("({"+((colorstring.charAt(0) == '#') ? "Green" : "Yellow")+"} "+name+" has taken the green flag from its pole!",map);
					}
					else
						pSend("(You try to grab the flag but your hands are full.");
					}
					break;
			case 146: // yellow flag pole
					if (map.mapnumber==22) {
					if (inhand == 0) {
						map.placeItemAt(148,tx,ty,map);
						setHands(150);
						map.broadcast("({"+((colorstring.charAt(0) == '#') ? "Green" : "Yellow")+"} "+name+" has taken the yellow flag from its pole!",map);
					}
					else
						pSend("(You try to grab the flag but your hands are full.");
					}
					break;
			case 202:
				map.placeItemAt(203,tx,ty,map);
				//map.addEnemy(36,tx,ty,true,map);
				if (!(map instanceof BasicMap))
					spawnScorp(tx,ty,map);
				break;
			case 203:
				map.placeItemAt(0,tx,ty,map);
				break;
			case 253:
					map.placeItemAt(254,tx,ty,map);
					if (map.readables != null) {
						for (int i = 0; i < map.readables.length; i++) {
							if (map.readables[i].startsWith(tx+","+ty)) {
								pSend("("+map.readables[i].substring(map.readables[i].indexOf(":")+1));
								break;
							}
						}
					}				
					break;
			case 254:
					map.placeItemAt(253,tx,ty,map);
					break;
/*			case 184:
					map.limitedBroadcast("(Healer: It's not very bright to try and harm someone who can heal themself.",cx-32,cy-32,map);
					break;
			case 189:
					map.limitedBroadcast("(Succubus: It's not nice to hit a woman.",cx-32,cy-32,map);
					break;
			case 205:
					map.limitedBroadcast("(Furrier: You know, I trap vermin like you for a living.",cx-32,cy-32,map);
					break;*/
			case 209:
					pSend("(* The bones shatter into dust leaving only the necklace behind.");
					map.placeItemAt(211,tx,ty,map);
					break;					
			case 220:
					if (map.mapnumber==47 && tx == 20 && ty == 68) {
						if (inhand==0) {
							if (parent.neztable==0) {
								pSend("(* You reach up and feel for something to grab. You find what seems to be an unfinished potion of some sort.");
								setHands(210);
								parent.neztable=1;
							}
						}
						else
							pSend("(* You feel around the table but just quit because your hands are full anyway.");
					}
					else
						checkHolders(tx,ty);
					break;
			/*case 227:
					map.limitedBroadcast("(*Pang!*\n(Warlette: I think I felt something. Must've been a misquito.",cx-32,cy-32,map);
					break;
			case 234:
					map.limitedBroadcast("(Steve writes \""+name+"\" on his hit list.",cx-32,cy-32,map);
					break;
			case 235:
					pSend("(Your sword becomes heavy as you begin to swing. As you try to strike, your muscles become weak and you cannot follow through.");
					break;*/
			default:	if (map.itemmap[tx][ty] > map.items || map.itemmap[tx][ty] == 143) {
						int leitem = map.itemmap[tx][ty];
						if (map.items<leitem && leitem<=map.items+8)
							map.placeItemAt(82,tx,ty,map);
						else if (map.items+8<leitem && leitem<=map.items+16)
							map.placeItemAt(83,tx,ty,map);
						else if (map.items+16<leitem && leitem<=map.items+24)
							map.placeItemAt(85,tx,ty,map);
						else if (map.items+32<leitem && leitem<=map.items+40)
							map.placeItemAt(84,tx,ty,map);
						else if (leitem == 143)
							map.placeItemAt(parent.balloonpops[(int)Math.round(Math.random() * (parent.balloonpops.length-1))],tx,ty,map);
						//else if (parent.maps[mapnumber].items+40<leitem && leitem<=parent.maps[mapnumber].items+48)
						//	parent.maps[mapnumber].placeItemAt(,tx,ty,parent.maps[mapnumber]);
						int nx = -1;
						int ny = -1;
						for (int i = 0; i < parent.animalbase.animals.size(); i++) {
							Animal a = (Animal)parent.animalbase.animals.elementAt(i);
							if (a.map == map && a.cx-32 == tx && a.cy-32 == ty) {
								nx = a.ox-32;
								ny = a.oy-32;
								parent.animalbase.animals.removeElementAt(i);
								break;
							}
						}
						if (nx != -1) {
							if (leitem != 143) {
								updateAlign(-2);
								map.addAnimal(leitem,nx,ny,map);
							}
						}
					}
					//else
					//		map.itemRecover(leitem,map);
					//}
		}
	}
	public static void spawnScorp(int x, int y, DSMapServer map) {
		int bx,by,tx,ty;

		bx=parent.nextx(x,y,1);
		by=parent.nexty(y,1);

		tx=parent.nextx(bx,by,7);
		ty=parent.nexty(by,7);

		if (map.canWalk(tx,ty,map))
			map.addEnemy(new Enemy(32,tx,ty,true,map));

		tx=parent.nextx(bx,by,3);
		ty=parent.nexty(by,3);

		if (map.canWalk(tx,ty,map))
			map.addEnemy(new Enemy(32,tx,ty,true,map));

		bx=parent.nextx(x,y,9);
		by=parent.nexty(y,9);

		tx=parent.nextx(bx,by,7);
		ty=parent.nexty(by,7);

		if (map.canWalk(tx,ty,map))
			map.addEnemy(new Enemy(32,tx,ty,true,map));

		tx=parent.nextx(bx,by,3);
		ty=parent.nexty(by,3);

		if (map.canWalk(tx,ty,map))
			map.addEnemy(new Enemy(32,tx,ty,true,map));
	}
	public boolean checkHolders(int x, int y) {
		if (map.holders==null) {
			pSend("(You find nothing inside.");
			return true;
		}

		int holdnum = -1;
		for (int i = 0; i < map.holders.length; i+=4) {
			if (x == map.holders[i] && y == map.holders[i+1]) {
				if (map.holders[i+3] > 0) {
					if (inhand == 0) {
						//parent.maps[mapnumber].holders[i+3]--;
						map.holders[i+3] = 0;
						//System.err.println(map.holders[i+2]);
						if (pickUpItemCheck(map.holders[i+2])==0)
							setHands(map.holders[i+2]);
						return true;
					}
					else {
						pSend("(There's something in the chest but your hands are full.");
						return false;
					}
				}
				else
					break;
			}
		}
		pSend("(You find nothing inside.");
		return true;
	}
	public int pickUpItemCheck(int item) {
		switch (item) {
			case 19:
				if (groupstat==1)
					Group.removeMember(this);
				else if (groupstat==2)
					Group.disbandGroup();
				//shapemodifier[1] = 1;
				//shapemodifier[2] = 2;
				//shapemodifier[3] = 3;
				shapecat=0;
				colorstring = "    ";
				sightSend("!6");
				//mstate = 1;
				mstate2=2;
				//if (currshape >= 17)
				//	currshape = 14;
				currshape=shapestart[shapecat][facing-1];
				visishape=currshape;
				map.playerPlaceBroadcast(visishape,colorstring,cx,cy,this,map);
				break;
			case 107:
				updateGold(1);
				pSend("(You find 1 gold.");
				//setHands(0);
				return 1;
			case 108:
				updateGold(5);
				pSend("(You find 5 gold.");
				//setHands(0);
				return 1;
			case 109:
				updateGold(10);
				pSend("(You find 10 gold.");
				//setHands(0);
				return 1;
			case 110:
				updateGold(25);
				pSend("(You find 25 gold.");
				//setHands(0);
				return 1;
			case 111:
				updateGold(50);
				pSend("(You find 50 gold.");
				//setHands(0);
				return 1;
			case 25:
				updateGold(100);
				pSend("(You find 100 gold.");
				//setHands(0);
				return 1;
			case 11:
				updateGold(350);
				pSend("(You find 350 gold.");
				//setHands(0);
				return 1;
			case 119:
				pSend("['* You pick up the book. As you open it, all the words inside fall off of the pages and blow away. You think, \"My, what and odd thing for words to do.\" You copy down the writing on your scrolls in the book.");
				break;
			case 149:
				map.broadcast("({"+((colorstring.charAt(0) == '#') ? "Green" : "Yellow")+"} "+name+" now has the green flag!",map);
				break;
			case 150:
				map.broadcast("({"+((colorstring.charAt(0) == '#') ? "Green" : "Yellow")+"} "+name+" now has the yellow flag!",map);
				break;
			case 132:
			case 133:
			case 134:
				int r1=-1,r2=-1;
				switch (item) {
					case 132:
						r1 = findInInv(133);
						r2 = findInInv(134);
						break;
					case 133:
						r1 = findInInv(132);
						r2 = findInInv(134);
						break;
					case 134:
						r1 = findInInv(133);
						r2 = findInInv(132);
				}
	
				if (r1 != -1 && r2 != -1) {
					pSend("(* The three runes rise above you and spin wildly, glowing bright. They thrust together in a flash of light and fall to your hands as a life rune.");
					map.itemmap[cx-32][cy-32] = 131;
					//map.buffint = 131;
					pSend("i"+parent.toDSChar(r1)+"  \ni"+parent.toDSChar(r2)+"  ");
					inventory[r1]=0;
					inventory[r2]=0;
				}

				break;

			case 201:
			case 6:
				if (map.bags!=null) {
					Enumeration e = map.bags.elements();
					BagOStuff b;
					int x=cx-32,y=cy-32;
					while (e.hasMoreElements()) {
						b=(BagOStuff)e.nextElement();
						if (b.x==x&&b.y==y) {
							if (b.gold>0) {
								pSend("(* The sack contained "+b.gold+" gold.");
								updateGold(b.gold);
							}
							if (b.items.length>0) {
								int r=0;
								boolean remove=true;
								for (int i=0;i<b.items.length;i++) {
									if (b.items[i]!=0) {
										switch (b.items[i]) {
											case 145:
											case 146:
											case 147:
											case 148:
											case 149:
											case 150:
											case 175:
											case 176:
											case 177:
												break;
											default:
												r=addToInv(b.items[i]);
										}
										
										if (r!=0) {
											b.gold=0;
											if (r==1) {
												if (remove) {
													remove=false;
													pSend("(* You can't hold everything from the sack, it's too much weight.");
												}
											}
											else if (r==2) {
												pSend("(* You get all you can from the sack, but your inventory is now full.");
												remove=false;
												break;
											}
										}
										else
											b.items[i]=0;
									}
								}	
								if (!remove)
									return 2;
								else
									pSend("(* You take all the items from the sack and put them into your inventory.");
							}
							map.placeItemAt(b.below,x,y,map);
							map.bags.removeElement(b);
							b=null;
							if (map.bags.size()==0)
								map.bags=null;
							break;
						}
					}
				}
				return 2;
			default:
				if (item > map.items) {
					for (int i = 0; i < parent.animalbase.animals.size(); i++) {
						Animal a = (Animal)parent.animalbase.animals.elementAt(i);
						if (a.cx == cx && a.cy == cy) {
							parent.animalbase.animals.removeElementAt(i);
							break;
						}
					}
				}
		}
		return 0;
	}
	public int findInInv(int item) {
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i]==item)
				return i;
		}
		return -1;
	}
	public int addToInv(int item) {
		int niw = invweight+parent.itemdefs[item][5];
		if (niw > parent.maxweight)
			return 1;
		invweight = niw;

		int invslot;
		for (invslot=0;invslot<inventory.length;invslot++) {
			if (inventory[invslot]==0)
				break;
		}

		if (invslot==inventory.length)
			return 2;

		inventory[invslot] = item;
		pSend("i"+(char)(invslot+32)+""+parent.encode(inventory[invslot]));		

		return 0;
	}
	public void checkHit(int x, int y) {
		if (map.allowcombat) {
			if (okToAttack(x,y,map)) {
				DSpiresSocket c = findSocketAtPosInSight(x,y,1);
				if (c != null) {
					if (c.colorstring.charAt(3) == ' ') return;
					if (parent.dice(1,4)==1) {
						doPlayerHit(x,y,c);
						return;
					}		
				}
			}
		}
		checkHit2(x,y);
	}
	public boolean checkHit2(int x, int y) {
		Enemy c = map.findEnemyAtPos(x,y,map);
		if (c != null) {
			c.damage(this,true);
			return true;
		}
		checkHit3(x,y);
		return false;
	}
	public void checkHit3(int x, int y) {
		if (map.npc3s != null) {
			NPC3 c;
			for (int i = 0;i < map.npc3s.size();i++) {
				c = (NPC3)map.npc3s.elementAt(i);
				if (c.x-32 == x && c.y-32 == y && c.bitIsMarked(c.NPC3_HIT)) {
					c.hit(this);
					//s.map.limitedBroadcast("("+c.name+": "+c.hitText,c.cx-32,c.cy-32,s.map);
					break;
				}
			}
		}
	}
	public void popPotion(int item, int x, int y) {
		if (map instanceof BasicMap)
			return;
		switch (item) {
			case 121:
				//map.addEnemy(new Warlock(x,y,map.mapnumber,true,parent.enemybase),map);
				int f = (int)Math.round(Math.random()*2);
				switch (f) {
					case 0:
						map.addEnemy(new Enemy(5,x,y,true,map));
						break;
					case 1:
						map.addEnemy(new Enemy(24,x,y,true,map));
						break;
					case 2:
						map.addEnemy(new Enemy(25,x,y,true,map));
						break;
				}
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
			case 164:
				map.addEnemy(new Enemy(1,x,y,true,map));
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
			case 165:
				map.addEnemy(new Enemy(13,x,y,true,map));
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
			case 182:
			case 183:
				map.placeItemAt(0,x,y,map);
				map.addEnemy(new Enemy(11,x,y,true,map));
				break;
			case 191:
				map.placeItemAt(0,x,y,map);
				map.addEnemy(new Enemy(8,x,y,true,map));
				break;
			case 239:
				map.addEnemy(new Enemy(9,x,y,true,map));
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
			case 240:
				map.addEnemy(new Enemy(12,x,y,true,map));
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
			case 248:
				map.addEnemy(new Enemy(10,x,y,true,map));
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
			case 308:
				map.addEnemy(new Enemy(29,x,y,true,map));
				map.limitedBroadcast(parent.assembleSpellString(124,x,y),x,y,map);
				map.placeItemAt(0,x,y,map);
				break;
		}

		int tx, ty;

		tx = parent.nextx(x,y,1);
		ty = parent.nexty(y,1);

		if (map.itemmap[tx][ty] == item)
			popPotion(item,tx,ty);

		tx = parent.nextx(x,y,3);
		ty = parent.nexty(y,3);

		if (map.itemmap[tx][ty] == item)
			popPotion(item,tx,ty);

		tx = parent.nextx(x,y,7);
		ty = parent.nexty(y,7);

		if (map.itemmap[tx][ty] == item)
			popPotion(item,tx,ty);

		tx = parent.nextx(x,y,9);
		ty = parent.nexty(y,9);

		if (map.itemmap[tx][ty] == item)
			popPotion(item,tx,ty);
	}
	public void doSlots() {
		if (gold > 0) {
			updateGold(-1);
			map.placeItemAt(16,25,48,map);
			map.placeItemAt(16,26,49,map);
			map.placeItemAt(16,26,50,map);

			//sightSend("("+name+" tries the slots!");
			int a = parent.slotsitems[(int)Math.round(Math.random() * (parent.slotsitems.length-1))];
			map.placeItemAt(a,25,48,map);
			int b = parent.slotsitems[(int)Math.round(Math.random() * (parent.slotsitems.length-1))];
			map.placeItemAt(b,26,49,map);
			int c2 = parent.slotsitems[(int)Math.round(Math.random() * (parent.slotsitems.length-1))];
			map.placeItemAt(c2,26,50,map);
			if (a == b && a == c2) {
				sightSend("("+name+" wins a prize!.");
				map.placeItemAt(a,24,52,map);
			}
			//else
			//	map.limitedBroadcast("(Awww, "+name+" doesn't win.",cx-32,cy-32,map);
		}
	}
	public void doSlots2() {
		if (gold > 0) {
			updateGold(-1);
			//sightSend("("+name+" tries the slots!");
			map.placeItemAt(16,18,19,map);
			map.placeItemAt(16,18,20,map);
			map.placeItemAt(16,19,21,map);

			int a = parent.slotsitems[(int)Math.round(Math.random() * (parent.slotsitems.length-1))];
			map.placeItemAt(a,18,19,map);
			int b = parent.slotsitems[(int)Math.round(Math.random() * (parent.slotsitems.length-1))];
			map.placeItemAt(b,18,20,map);
			int c2 = parent.slotsitems[(int)Math.round(Math.random() * (parent.slotsitems.length-1))];
			map.placeItemAt(c2,19,21,map);
			if (a == b && a == c2) {
				sightSend("("+name+" wins a prize!.");
				map.placeItemAt(a,17,23,map);
			}
			//else
			//	map.limitedBroadcast("(Awww, "+name+" doesn't win.",cx-32,cy-32,map);
		}
	}
	public static boolean okToAttack(int x, int y, DSMapServer m) {
		return (parent.PointinRect(x,y,
							m.attacktiles[1],
							m.attacktiles[2],
							m.attacktiles[3],
							m.attacktiles[4])
				&& (m.attacktiles[0] == m.tilemap[x][y] || m.attacktiles[0] == -1)
			);
	}
	public void doPlayerHit(int x, int y, DSpiresSocket c) {
		if (c.hp>13) {
			c.hp=13;
			c.doDamageToPlayer(c.hp-13);
			sightSendAttacks("[\""+name+" did smite "+c.name+"!\n!4",c);
		}
		else if (c.hp>6) {
			c.hp=6;
			c.doDamageToPlayer(c.hp-6);
			sightSendAttacks("[\""+name+" did smite "+c.name+"!\n!4",c);
		}
		else {
			c.hp=0;
			sightSendAttacks("("+name+" did slay "+c.name+"!\n!4",c);
			parent.channelBroadcast(name+" did slay "+c.name+"!",parent.COMBAT_CHANNEL);
			c.loses++;
			pkill++;
			c.killMe(false);
		}
	}
	public void doProjectile() {
		int proj=weaponO.projectile;
		if (weapon==212||weapon==206) {
			switch (inhand) {
				case 247:
					setHands(251);
					break;
				case 251:
					setHands(252);
					break;
				case 252:
					setHands(0);
					for (int i=0;i<inventory.length;i++) {
						switch (inventory[i]) {
							case 247:
							case 251:
							case 252:
								setHands(inventory[i]);
								setInvPos(0,i);
								i=inventory.length;
						}
					}
					break;
				default:
					if (++restCount>8) return;
					pSend("(* You're not holding arrows!");
					return;
			}
			proj=((facing==7||facing==3)?172:173);
			new DSBullet(proj,x,y,facing,(weapon==212 ? 5 : 7),map,this,parent);
		}
		else if (weapon==215) {
			new DSSpellShoot(289,x,y,facing,6,weaponO,map,this,parent);
		}
		else
			new DSBullet(proj,x,y,facing,weaponO.projectile_length,map,this,parent);
	}
	public boolean stepOnItem(int x, int y, int item, int type) {
		if (type!=parent.itemdefs[item][0]&&item!=7)
			return false;
		switch (item) {
			case 3:
			case 8:
				map.doBall(facing,x,y,item,map);
				break;
			case 188:
			case 233:
			case 259:
			case 151:
			case 276:
			case 279:
			case 312:
				//map.playermap[x][y] = 0;
				//map.colorstrings[x][y] = "";
				if (checkPortals(x,y))
					return true;
				break;
			case 154:
				pSend("[#You step in poo! It's stuck to your shoe! What the heck are you going to do?");
				map.placeItemAt(0,x,y,map);
				break;
			case 196:
				if (colorstring.charAt(3)==' ')
					break;
				if (doDamageToPlayer(1)) {
					//sightSend("("+s.name+" did slay "+name+"!\n!4",s);
					//parent.channelBroadcast("(["+parent.channelchars[4]+"] "+s.name+" did slay "+name+"!",4);
					pSend("(The floor did slay you!");
					killMe(true);
					return true;
				}
				else
					pSend("[\"The floor bites you!");
				break;
			case 42:
				if (facing==7) {
					if (upEWStairs(x,y))
						return true;
				}
				break;
			case 7:
				if (map.itemmap[x][y+2] == 42) {
					if (downEWStairs(x,y))
						return true;
				}
				else if (map.itemmap[x][y+2] == 338) {
					if (downNSStairs(x,y))
						return true;
				}
				break;
			case 338:
				if (facing==9) {
					if (upNSStairs(x,y))
						return true;
				}
				break;
			case 208:
				//if (++restCount>20) {
				//	closeIt("Spam[5]");
				//	return true;
				//}
				int xx=parent.nextx(x,y,facing);
				int yy=parent.nexty(y,facing);
				if (map.canWalk(xx,yy,map)) {
					x=parent.nextx(xx,yy,facing);
					y=parent.nexty(yy,facing);
					if (map.canWalk(x,y,map))
						movePlayer(x,y,true);
					else 
						movePlayer(xx,yy,true);
					pSend("[&You splash into the puddle... You slip and slide!");

					//if (map.itemmap[cx-32][cy-32]==208)
					//	stepOnItem(cx-32,cy-32,208,0);

					return true;
				}
				pSend("[&You splash into the puddle.");
		}
		return false;
	}
	public /*synchronized*/ void movePlayer(int x, int y, boolean ignoresplat) {
		if (nsync)
			return;

		nsync=true;

		//if (!loggedin) return;

		if (stateCheck!=0)
			doStateChecker("m");
		if (groupstat==2)
			Group.disbandGroup();

		map.playerPlaceBroadcast(0,colorstring,cx,cy,this,map);

		if (!ignoresplat) {
		if (map.playermap[x][y] != 0) {
			DSpiresSocket ts = findSocketAtPos(x,y,map,0);
			if (ts != null) {
				ts.transformPlayerToItem(144);
				sightSend(parent.assembleSpellString(152,x,y));
				map.limitedBroadcast("("+name+" plummets from the sky, smooshing "+ts.name+" flat.",x,y,map);
			}
		}
		}
		//cx = parent.toDSChar(x);
		//cy = parent.toDSChar(y);
		setPosition(x,y);
		pSend("~\nCP\n@"+cx+""+cy);
		map.playerPlaceBroadcastWithRefresh(currshape,colorstring,x,y,this,map);
		setFeet(map.itemmap[x][y]);
		pSend("=");

		nsync=false;
	}
	public /*synchronized*/ void movePlayer2(int x, int y, boolean ignoresplat) {
		if (nsync)
			return;

		nsync=true;

		//if (!loggedin) return;

		map.playerPlaceBroadcast(0,colorstring,cx,cy,this,map);

		if (!ignoresplat) {
		if (map.playermap[x][y] != 0) {
			DSpiresSocket ts = findSocketAtPos(x,y,map,0);
			if (ts != null) {
				ts.transformPlayerToItem(144);
				sightSend(parent.assembleSpellString(152,x,y));
				map.limitedBroadcast("("+name+" plummets from the sky, smooshing "+ts.name+" flat.",x,y,map);
			}
		}
		}
		//cx = parent.toDSChar(x);
		//cy = parent.toDSChar(y);
		setPosition(x,y);
		pSend("~\nCP\n@"+cx+""+cy);
		map.playerPlaceBroadcastWithRefresh(currshape,colorstring,x,y,this,map);
		setFeet(map.itemmap[x][y]);
		pSend("=");

		nsync=false;
	}
	public boolean upEWStairs(int x, int y) {
		if (facing == 7) {
			if (checkPortals(x,y))
				return true;
			int tx,ty;
			try {
				while (map.itemmap[x][y] == 42) {
					tx = x;
					ty = y;
					x = parent.nextx(tx,ty,facing);
					y = parent.nexty(ty,facing);
					y-=2;
				}

				if (map.playermap[x][y] == 0) {
					if (groupstat==2)
						moveGroup(x,y,Group);
					else
						movePlayer(x,y,true);
					return true;
				}
			}
			catch (Exception e) {}
		}
		return false;
	}
	public boolean downEWStairs(int x, int y) {
		if (facing == 3) {
			int tx,ty;
			try {
				while (map.itemmap[x][y+2] == 42) {
					tx = x;
					ty = y;
					x = parent.nextx(tx,ty,facing);
					y = parent.nexty(ty,facing);
					y+=2;
				}

				if (map.playermap[x][y] == 0) {
					if (groupstat==2)
						moveGroup(x,y,Group);
					else
						movePlayer(x,y,true);
					return true;
				}
			}
			catch (Exception e) {}
		}
		return false;
	}
	public boolean upNSStairs(int x, int y) {
		if (facing == 9) {
			if (checkPortals(x,y))
				return true;
			int tx,ty;
			try {
				while (map.itemmap[x][y] == 338) {
					tx = x;
					ty = y;
					x = parent.nextx(tx,ty,facing);
					y = parent.nexty(ty,facing);
					y-=2;
				}

				if (map.playermap[x][y] == 0) {
					if (groupstat==2)
						moveGroup(x,y,Group);
					else
						movePlayer(x,y,true);
					return true;
				}
			}
			catch (Exception e) {}
		}
		return false;
	}
	public boolean downNSStairs(int x, int y) {
		if (facing == 1) {
			int tx,ty;
			try {
				while (map.itemmap[x][y+2] == 338) {
					tx = x;
					ty = y;
					x = parent.nextx(tx,ty,facing);
					y = parent.nexty(ty,facing);
					y+=2;
				}

				if (map.playermap[x][y] == 0) {
					if (groupstat==2)
						moveGroup(x,y,Group);
					else
						movePlayer(x,y,true);
					return true;
				}
			}
			catch (Exception e) {}
		}
		return false;
	}
	public static void moveGroup(int x, int y, Group g) {
		g.groupSend("~");
		for (int i=0;i<g.MAX_GROUP_SIZE;i++) {
			if (g.members[i]!=null) {
				g.members[i].movePlayer2(x,y,true);
			}
		}
		g.leader.movePlayer2(x,y,true);
		g.groupSend("=");
	}
	public boolean checkPortals(int x, int y) {
		if (map.portals == null) return false;
		if (inhand>= 145 && inhand<=150) {
			pSend("['You can't take the CTF stuff through the portal.");
			return true;
		}
		/*for (int i = 0; i < map.portals.length; i+=3) {
			if (map.portals[i] == x && map.portals[i+1] == y) {
				if (real)
					pSend("['You enter the portal...");
				changeMap(parent.maps[map.portals[i+2]]);
				return true;
			}
		}*/
		if (map.portals!=null) {
			for (int i=0;i<map.portals.length;i++) {
				if (map.portals[i]==null)
					continue;
				if (!map.portals[i].active)
					continue;
				if (map.portals[i].orig_x==x&&map.portals[i].orig_y==y) {
					if (map.portals[i].dest_map instanceof BasicMap && map != map.portals[i].dest_map) {
						if (dialogID!=D_MAP_ENTER) {
							pSend("[\'* Do you want to enter "+map.portals[i].dest_map.owner+"'s map? (Y/N)");
							requestDialog(D_MAP_ENTER);
						}
						return false;
					}
					else {
						if (colorstring.charAt(3) == ' ' && map.itemmap[x][y]==151 && !(map.portals[i].dest_map instanceof BasicMap)) {
							pSend("['* The magical forces of the biscuit disrupt the portal's electro-magnetic field or something.");
							return true;
						}
						if (map.portals[i].dest_x==-1||map.portals[i].dest_y==-1) {
							changeMap(map.portals[i].dest_map,0,0,0);
						}
						else if (map == map.portals[i].dest_map) {
							if (groupstat==2)
								moveGroup(map.portals[i].dest_x,map.portals[i].dest_y,Group);
							else
								movePlayer(map.portals[i].dest_x,map.portals[i].dest_y,false);
						}
						else {
							changeMap(map.portals[i].dest_map,2,map.portals[i].dest_x,map.portals[i].dest_y);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean checkExits(int tx, int ty) {
		//int tempo[] = {tx,ty};
		//int oey = ty % 2;
		/*for (int t = 0; t <= 1; t++) {
			for (int e = t; e <= t+1; e++) {
				if (tempo[t] == parent.exitpoints[t+e]) {
					if (c.map.exits[t+e] != -2 && (t == 1 || oey == e)) {
						changeMap(parent.maps[c.map.exits[t+e]],c,1);
						return true;
					}
				}
			}
		}*/

		int check;

		switch (tx) {
			case 0:
				check=1;
				break;
			case 51:
				check=0;
				break;
			default:
				switch (ty) {
					case 0:
						check=2;
						break;
					case 99:
						check=3;
						break;
					default:
						return false;
				}
		}

		if (map.exits[check] != -2 && (check>1 || ty % 2 == check)) {
			changeMap(parent.maps[map.exits[check]],1,0,0);
			return true;
		}
		return false;
	}
	public void rotateLeft() {
		if (checkMoveCount())
			return;
		if (!updateStam(-2))
			return;
		switch(facing) {
			case 7:
					facing=1;
					break;
			case 9:
					facing=7;
					break;
			case 1:
					facing=3;
					break;
			case 3:
					facing=9;
		}
		currshape=shapestart[shapecat][facing-1];
		visishape=currshape;
		//mstate = 1;
		mstate2=2;
		map.playerPlaceBroadcast(visishape,colorstring,cx,cy,this,map);
	}
	public void rotateRight() {
		if (checkMoveCount())
			return;
		if (!updateStam(-2))
			return;
		switch (facing) {
			case 7:
					facing=9;
					break;
			case 9:
					facing=3;
					break;
			case 1:
					facing=7;
					break;
			case 3:
					facing=1;
		}
		currshape=shapestart[shapecat][facing-1];
		visishape=currshape;
		//mstate = 1;
		mstate2=2;
		map.playerPlaceBroadcast(visishape,colorstring,cx,cy,this,map);
	}
	public void doLook() {
		//if (++restCount>5)
		//	return;

		int tx = parent.nextx(cx-32,cy-32,facing);
		int ty = parent.nexty(cy-32,facing);

		if (map.playermap[tx][ty]!=0) {
			DSpiresSocket s = findSocketAtPosInSight(tx,ty,1);
			if (s != null) {
				pSend("P "+s.name+" "+s.ocolorstring.substring(0,3)+s.pstring);
				pSend("[*You see "+s.name+((s.title.length() > 0) ? " "+s.title : "")+".\n[&"+s.desc);
				int total = s.pkill+s.loses;
				pSend("[#Flags: ["+parent.aligntexts[s.alignment/20]+"]"+
									( (total > 0) ? " [PK:"+(int)(((float)(s.pkill)/total)*100)+"%]" : " [PK:N/A]")+
									(bitIsMarked(s.stateCheck,ST_BRB) ? " [BRB]" : ""));
				pSend("[%E-Mail: "+s.email);
				pSend("[\'Web site: "+s.homepage);
				s.pSend("[*"+name+" is looking you over.");
				return;
			}
			
			if (map.npc3s != null) {
				int tcx = tx+32;
				int tcy = ty+32;
				NPC3 npc;
				for (int i = 0; i < map.npc3s.size();i++) {
					npc = (NPC3)map.npc3s.elementAt(i);
					if (npc.x == tcx && npc.y == tcy) {
						if (npc.bitIsMarked(npc.NPC3_INTERACT))
							initInteraction((Interactable)npc);
						else
							pSend("(You see "+npc.name+".");
						return;
					}
				}
			}

			Enemy enemy = map.findEnemyAtPos(tx,ty,map);
			if (enemy != null)
				pSend("(You see "+enemy.me.name+".");
			return;
		}

		boolean notfound = true;
		if (map.readables != null) {
			for (int i = 0; i < map.readables.length; i++) {
				if (map.readables[i].startsWith(tx+","+ty)) {
					pSend("[!"+map.readables[i].substring(map.readables[i].indexOf(":")+1));
					notfound = false;
					break;
				}
			}
		}

		if (notfound) {
			notfound=true;
			NPC2 npc2 = parent.npc2base.findNPC2(parent.nextx(cx-32,cy-32,facing),parent.nexty(cy-32,facing),map);
			if (npc2!=null) {
				if (npc2 instanceof Interactable)
					initInteraction((Interactable)npc2);
				else
					pSend("(You see "+npc2.name+".");
				notfound=false;
			}

			if (notfound) {
				if (map == parent.maps[0] && tx == 33 && ty == 54) {
					char ch = '"';
					pSend("["+ch+"<*> The Magic Graffitti Sign! <*>");
					for (int i = map.graffitti.length-1; i >= 0 ; i--) {
						if (!(map.graffitti[i].equals(""))) {
							ch++;
							pSend("["+ch+""+map.graffitti[i]);
						}
					}
				}
				else if (map==parent.maps[1] && tx == 68 && ty == 21)
					pSend("(You see the grave of "+name+".");
				else if (map.itemmap[tx][ty]==24) {
					pSend("P "+name+" "+ocolorstring.substring(0,3)+pstring);
					pSend("[*You see yourself"+((title.length() > 0) ? " "+title : "")+".\n[&"+desc);
					int total = pkill+loses;
					pSend("[#Flags: ["+parent.aligntexts[alignment/20]+"]"+
									( (total > 0) ? " [PK:"+(int)(((float)(pkill)/total)*100)+"%]" : " [PK:N/A]")+
									(bitIsMarked(stateCheck,ST_BRB) ? " [BRB]" : ""));
					pSend("[%E-Mail: "+email);
					pSend("[\'Web site: "+homepage);
				}
				else {
					if (map.itemmap[tx][ty]==23||map.itemmap[tx][ty]==61) {
						DSpiresSocket ts = findSocketAtPos(tx,ty,map,0);
						if (ts != null) {
							pSend("[*You see a statue of "+ts.name+".");
							ts.pSend("[*"+name+" is looking you over.");
							return;
						}
					}

					if (map.itemmap[tx][ty]<=0)
						pSend("(You see "+parent.floornames[parent.floornamepointers[map.tilemap[tx][ty]]]+".");
					else {
						pSend("(You see "+parent.getItemName(map.itemmap[tx][ty])+".");
						//if (map.itemmap[tx][ty] <= map.items)
						//	pSend("(You see "+parent.itemnames[map.itemmap[tx][ty]]+".");
						//else
						//	pSend("(You see "+parent.animnames[((map.itemmap[tx][ty]+1-map.items)-2)/8]+".");
					}
				}
			}
		}
	}
	public void doGet() {
		//if (++restCount>15)
		//	return;

		if (colorstring.charAt(3) == ' '&&map.itemmap[cx-32][cy-32]!=0) {
			pSend("(Your little doggy paws cannot grasp that item to lift it.");
			return;
		}

		if (trade!=null)
			trade.cancelTrade(0,trade);

		if (!updateStam(-1))
			return;

		if (colorstring.charAt(3) != ' ')
			sightSend("<"+cx+""+cy+""+parent.toDSChar(22)+""+colorstring);

		if (map.itemmap[cx-32][cy-32]==260)
			map.addHole(this);
		else {

		switch (inhand) {
			case 145:
			case 146:
			case 149:
			case 150:
				break;
			default:
				if (map.itemmap[cx-32][cy-32] != inhand && parent.itemdefs[map.itemmap[cx-32][cy-32]][1] != 1) {
					int atpos = map.itemmap[cx-32][cy-32];
					int toplace = inhand;
					while (true) {
						int r=0;
						if (parent.itemdefs[atpos][3] == 1) {
							r=pickUpItemCheck(atpos);
							switch (r) {
								case 0:
									break;
								case 1:
									toplace=0;
									break;
								case 2:
									break;
							}
						}

						if (r==0)
							setHands(map.itemmap[cx-32][cy-32]);

						if (r==2)
							break;

						map.placeItemAt(toplace,cx-32,cy-32,map);
						setFeet(map.itemmap[cx-32][cy-32]);
						break;
					}
				}
		}

		switch (pfooti) {
			case 19:
				shapecat=1;
				//shapemodifier[1] = 0;
				//shapemodifier[2] = 0;
				//shapemodifier[3] = 0;
				colorstring = ocolorstring;
				currshape=shapestart[shapecat][facing-1];
				visishape=currshape;
				sightSend("!6");
				break;
		}

		}

		if (colorstring.charAt(3) != ' ')
			map.playerPlaceBroadcast(21,colorstring,cx,cy,this,map);
	}
	public void initInteraction(Interactable intr) {
		if (intr==interaction) {
			pSend("cd");
			requestDialog(D_NONE);
			return;
		}
		String buffer="";
		String opts[] = intr.getOptions(this);
		for (int i=0;i<opts.length;i++) {
			if (opts[i]!=null)
				buffer+=';'+opts[i];
		}

		if (intr instanceof NPC3)
			pSend("IYou see "+(((NPC3)intr).name.replace('|',' '))+"."+buffer);
		else if (intr instanceof NPC2)
			pSend("IYou see "+(((NPC2)intr).name.replace('|',' '))+"."+buffer);

		requestDialog(D_INTERACT);
		interaction = intr;
	}
	public void doUse(int item) {
		//if (++restCount>30) {
		//	closeIt("Spam[5]");
		//	return;
		//}
		switch(item) {
			case 3:	pSend("(You do something with the something and something happens.");
					break;
			case 4:	pSend("(You take a swig from the bottle. Tangy!");
					setHands(0);
					break;
			case 5:	pSend("(You feel that the sword is heavy and hard to use, but also that it can do a lot of damage.");
					break;
			case 10:	if (hp < 20) {
						hp++;
						pSend("$H"+hp+"\n(You feel better!");
					}
					else
						pSend("(You eat a the fish for no particular reason, save gluttony.");
					setHands(0);
					break;
			case 12:	pSend("(You feel that this sword is fairly easy to use and would make a good weapon.");
					break;
			case 27:	pSend("(You pet Patrick. He meows!");
					break;
			case 39:	pSend("(You eat a big Mech! You suddenly feel queasy..");
					setHands(0);
					break;
			case 40:	pSend("(You eat a Jumbo Mo! Ugh.. where's the bathroom?");
					setHands(0);
					break;
			case 41:	pSend("(The flower says to you, 'Don't mess with me, bub.'");
					break;
			case 43:	pSend("(You try to hold a meaningful conversation with the bones, but all they will do is lie there.");
					break;
			case 44:
					if (map.mapnumber!=31) {
						pSend("(You drink some blood from the goblet and die.");
						killMe(false);
					}
					break;
			case 45:	//stam+=25;
					if (!updateStam(25))
						return;
					pSend("(You feel saucy!");
					setHands(0);
					break;
			case 46:
					if (alignment/20 <= 3) {
						if ((mp+=10) > 30) mp = 30;
						pSend("$M"+parent.toDSChar(mp)+"\n(You gulp down the shrunken head. It's full of heady goodness.");
					}
					else
						pSend("[\"The shrunken head sticks its tongue out, mocks you, and rolls away.");

					setHands(0);
					break;
			case 47:	if (hp < 20)
						hp++;
					//stam = MAX_STAM;
					if (!updateStam(MAX_STAM))
						return;
					sightSend("[%"+name+" uses a herb and feels damn good now.");
					pSend("$H"+hp);
					setHands(0);
					break;
			case 49:	//stam+=15;
					//if (stam > MAX_STAM)
					//	stam = MAX_STAM;
					if (!updateStam(15))
						return;
					pSend("(You use the medicinal plant and feel more energized.");
					setHands(0);
					break;
			case 50:
					alignment = -256;
					updateAlign(80+256);
					pSend("(You smell nice now.");
					setHands(0);
					break;
			case 51:
					pSend("(You stuff your face with blueberry jam from the bottle.");
					setHands(0);
					break;
			case 52:	pSend("(You look around for a hamburger to put the ketchup on.");
					break;
			case 54:	//stam = MAX_STAM;
					if (!updateStam(MAX_STAM))
						return;
					sightSend("[("+name+" gulps down some Jolt Cola(tm) and starts zipping around.");
					setHands(0);
					break;
			case 55:
					sightSend("(\""+name+" guzzles down some ale. *burp*");
					setHands(0);
					break;
			case 57:
					if (chargeMP(3,true))
						transformPlayerToItem(14);
					break;
			case 58:
					if (map.mapnumber == 22) break;
					if (map.mapnumber!=31) {
						if (chargeMP(20,true))
							changeMap(parent.maps[map.diemap],0,0,0);
					}
					break;
			case 59:
					if (chargeMP(5,true)) {
						if (ocolorstring.charAt(3) == '"' || ocolorstring.charAt(3) == '$')
							transformPlayerToItem(23);
						else
							transformPlayerToItem(61);
					}
					break;
			case 76:	pSend("(You bash yourself in the head with the large rock as hard as humanly possible.");
					setHands(77);
					break;
			case 77:	pSend("(You stuff the shattered rock into your mouth, chewing it to small pebbles.\n(You have now broken all of your teeth. Congratulations.");
					setHands(78);
					break;
			case 78:	pSend("(You pop the small stones into your mouth and suck on them hoping to reach the creamy center.");
					if ((int)Math.round(Math.random()) == 1) {
						pSend("(You accidentally swallow the stones. Its not the first time, and probably won't be the last.");
						setHands(0);
					}
					break;
			case 82:	
			case 83:
			case 84:
			case 85:
					pSend("(You contemplate eating the dead animal raw, but its kind of icky.");
					break;
			case 96:	pSend("(You guzzled down each can then say suddenly, \"Grog Lite: The drink of pirates everywhere.\".\n#"+parent.toDSChar(20));
					setHands(0);
					break;
			case 97:
					if (chargeMP(13,true)) {
						new DSSpellShoot(289,cx-32,cy-32,facing,3,parent.RED_SCROLL,map,this,parent);
						int tx = parent.nextx(cx-32,cy-32,facing);
						int ty = parent.nexty(cy-32,facing);

						sightSend(parent.assembleSpellString(289,tx,ty));
						sightSendAttacks("[\""+name+" summons the elemental flame!",null);
						sightSendAttacks("!6",null);

						if (map.playermap[tx][ty] != 0) {
							Enemy e = map.findEnemyAtPos(tx,ty,map);
							if (e != null) {
								int damage=W2A(parent.RED_SCROLL,e.me.armor);
								sightSendAttacks("[\""+name+" did smite "+e.me.name+"! ("+damage+")",null);
								if (e.doDamageToEnemy(damage)) {
									updateAlign(e.me.align);
									sightSendAttacks("!4",null);
									sightSendAttacks("[\""+name+" did slay "+e.me.name+"!",null);
									if (e instanceof i1Enemy)
										((i1Enemy)e).die();
									else
										e.die();
									return;
								}
								e.attack(cx-32,cy-32);
							}
						}
					}
					break;
			case 98:
					if (chargeMP(4,true))
						transformPlayerToItem(117);
					break;
			case 100:
					if (chargeMP(16,true)) {
						if ((hp+=4) > 20)
							hp=20;
						pSend("$H"+hp);
						pSend("(You are healed by the power of the pink scroll.");
						sightSend(parent.assembleSpellString(309,cx-32,cy-32));
					}
					break;
			case 101:
					if (chargeMP(5,true))
						transformPlayerToItem(87);
					break;
			case 102:
					if (chargeMP(16,true)) {
						int tx[] = {parent.nextx(cx-32,cy-32,1),
									parent.nextx(cx-32,cy-32,3),
									parent.nextx(cx-32,cy-32,7),
									parent.nextx(cx-32,cy-32,9),
									0,
									0,
									0,
									0
									};
						int ty[] = {parent.nexty(cy-32,1),
									parent.nexty(cy-32,3),
									parent.nexty(cy-32,7),
									parent.nexty(cy-32,9),
									0,
									0,
									0,
									0
									};

						tx[4]=parent.nextx(tx[0],ty[0],3);
						ty[4]=parent.nexty(ty[0],3);
						tx[5]=parent.nextx(tx[1],ty[1],9);
						ty[5]=parent.nexty(ty[1],9);
						tx[6]=parent.nextx(tx[2],ty[2],1);
						ty[6]=parent.nexty(ty[2],1);
						tx[7]=parent.nextx(tx[3],ty[3],7);
						ty[7]=parent.nexty(ty[3],7);

						sightSendAttacks("[)"+name+" is surrounded by the silver mist!",null);
						sightSendAttacks("!6",null);

						for (int i = 0; i < tx.length; i++) {
							sightSend(parent.assembleSpellString(290,tx[i],ty[i]));

							if (map.playermap[tx[i]][ty[i]] != 0) {
								Enemy e = map.findEnemyAtPos(tx[i],ty[i],map);
								if (e != null) {
									int damage=W2A(parent.SILVER_SCROLL,e.me.armor);
									sightSendAttacks("[\""+name+" did smite "+e.me.name+"! ("+damage+")",null);
									if (e.doDamageToEnemy(damage)) {
										updateAlign(e.me.align);
										sightSendAttacks("!4",null);
										sightSendAttacks("[\""+name+" did slay "+e.me.name+"!",null);
										if (e instanceof i1Enemy)
											((i1Enemy)e).die();
										else
											e.die();
										return;
									}
									e.attack(cx-32,cy-32);
								}
							}
						}
					}
					break;
			case 131:
			case 134: //flesh rune
				int tx = parent.nextx(cx-32,cy-32,facing);
				int ty = parent.nexty(cy-32,facing);

				DSpiresSocket h = findSocketAtPosInSight(tx,ty,1);

				if (h == null)
					h = this;
				if (h.hp < 20) {
					if (chargeMP(12,true)) {
						h.hp+=3;
						if (h.hp>20) h.hp=20;
						h.pSend("$H"+h.hp);
						if (h!=this) {
							pSend("(You heal "+h.name+".");
							h.pSend("("+name+" heals you.");
							h.sightSend(parent.assembleSpellString(309,h.cx-32,h.cy-32));
						}
						else
							pSend("(You heal yourself.");
					}						
				}
				else {
					if (h!=this)
						pSend("("+h.name+" is fully healed.");
					else
						pSend("(You are fully healed.");
				}
				break;
			case 140:
					if (alignment/20 >= 3) {
						if ((mp+=10) > 30) mp = 30;
						pSend("$M"+parent.toDSChar(mp)+"\n(The crystal ball calls upon unseen forces to help you.");
					}
					else
						pSend("(The crystal ball smashes into powdered glass.");

					setHands(0);
					break;
			case 145:
					if (map.mapnumber==22)
						parent.placePole(145,"green",this);
					break;
			case 146:
					if (map.mapnumber==22)
						parent.placePole(146,"yellow",this);
					break;
			case 149:
					int tx2 = parent.nextx(cx-32,cy-32,facing);
					int ty2 = parent.nexty(cy-32,facing);
					switch (map.itemmap[tx2][ty2]) {
						case 146:
					 	case 148:
							if (colorstring.charAt(0) == '+') {
								setHands(0);
								parent.winCTF(this);
							}
							else
								pSend("(You can't put that there, you're on the wrong team!");
							break;
						case 147:
							setHands(0);
							map.placeItemAt(145,tx2,ty2,map);
					}
					break;
			case 150:
					int tx3 = parent.nextx(cx-32,cy-32,facing);
					int ty3 = parent.nexty(cy-32,facing);
					switch (map.itemmap[tx3][ty3]) {
						case 145:
					 	case 147:
							if (colorstring.charAt(0) == '#') {
								setHands(0);
								parent.winCTF(this);
							}
							else
								pSend("(You can't put that there, you're on the wrong team!");
							break;
						case 148:
							setHands(0);
							map.placeItemAt(146,tx3,ty3,map);
					}
					break;
			case 195:
					updateStam(10);
					sightSend("("+name+" saves some melting ice cream from liquid oblivion!");
					setHands(0);
					break;
			case 200:
					int tx5 = parent.nextx(cx-32,cy-32,facing);
					int ty5 = parent.nexty(cy-32,facing);
					switch (map.itemmap[tx5][ty5]) {
						case 233:
						case 188:
							map.placeItemAt(0,tx5,ty5,map);
							int breaktest = parent.dice(1,10);
							if (breaktest <= 2) {
								pSend("(ACK! Your key snaps in half!");
								setHands(0);
							}
					}
					break;
			case 204:
					pSend("(The Hermit crab pops back into its shell when you touch it!");
					break;
			/*case 212:
					//doShoot(c);
					//if (++restCount>10)
					//	return;
					new DSBullet(((facing==7||facing==3)?172:173),cx-32,cy-32,facing,5,map,c,parent);

					break;
			*/
			case 249:
					int tx4 = parent.nextx(cx-32,cy-32,facing);
					int ty4 = parent.nexty(cy-32,facing);
					switch (map.itemmap[tx4][ty4]) {
						case 233:
						case 188:
							map.placeItemAt(0,tx4,ty4,map);
							setHands(0);
					}
					break;
			case 255:
				if ((hp+=3) > 20)
					hp=20;
				pSend("$H"+hp);
				pSend("(You pop the crimson berry into your mouth! When the stinging stops, you feel pretty groovy!");
				setHands(0);
				break;
			case 256:
				//if ((mp+=30) > 30)
					mp=30;
				pSend("$M"+mp);
				pSend("(You pop the violet berry into your mouth! When the stinging stops, you feel pretty groovy!");
				setHands(0);
				break;
			case 283: // Blue snail shell
				//if (++restCount>20) {
				//	closeIt("Spam[5]");
				//	return;
				//}
				if (chargeMP(4,true)) {
					sightSend("[&"+name+" BECOMES A OMGWTF.");
					int ti;
					while (true) {
						ti = (int)Math.round(Math.random() * map.items);
						if (!parent.findIntInArray(ti,parent.itemsdisallowed) && ti != 42 && ti != 338)
							break;
					}
					transformPlayerToItem(14);
					map.broadcast(">"+cx+""+cy+""+parent.encode(ti),map);
				}
				break;
			case 285: // Orange snail shell
				//if (++restCount>20) {
				//	closeIt("Spam[5]");
				//	return;
				//}
				if (chargeMP(4,true)) {
					sightSend("[&"+name+" BECOMES A SNAIL MASTAH.");
					transformPlayerToItem(323);
				}
				break;
			case 301: // Heart
				if ((hp+=2) > 20)
					hp=20;
				pSend("$H"+hp);
				sightSend("[("+name+" heals him/herself with a heart.");
				setHands(0);
				break;
			case 313: // Chrome cross
				if (chargeMP(22,true)) {
					sightSendAttacks("[)"+name+" calls upon noble spirits for protection!",null);
					sightSendAttacks("!6",null);
					for (int i=0;i<4;i++)
						new DSSpellShoot(280,cx-32,cy-32,parent.shortfaceconv[i],2,parent.CHROME_CROSS,map,this,parent);
				}
				break;
			case 345: // Black cross				
				if (chargeMP(19,true)) {
					sightSendAttacks("[*"+name+" calls upon dark magic to raise the dead!",null);
					sightSendAttacks("!6",null);
					new DSSpellShoot(280,cx-32,cy-32,facing,4,parent.BLACK_CROSS,map,this,parent);
				}
				break;
			default:	pSend("(There doesn't seem to be any use for that.");
		}
	}
	public void doThrow() {
		if (trade!=null)
			trade.cancelTrade(0,trade);

		switch (inhand) {
			case 175:
			case 176:
			case 177:
				dodgeballThrow();
				return;
		}

		if (parent.findIntInArray(inhand,parent.nogive) || inhand == 149 || inhand == 150 || inhand == 145 || inhand == 146) {
			pSend("(* Magical forces prevent you from throwing it.");
			return;
		}

		int tinhand = inhand;
		int xf = -1;
		int yf = -1;
		int spaces=4;
		//int[] tempo = parent.maps[mapnumber].findNextFloor(facing,cx-32,cy-32);
		int tx = parent.nextx(cx-32,cy-32,facing);
		int ty = parent.nexty(cy-32,facing);
		for (int i = 0; i < spaces; i++) {
			if (map.itemmap[tx][ty] == 0 && map.canWalk(tx,ty,map)) {
				if (xf == -1)
					map.limitedBroadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(tinhand),tx,ty,map);
				else
					//map.limitedBroadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(tinhand)+""+parent.toDSChar(xf)+""+parent.toDSChar(yf)+"  ",tx,ty,map);
					map.dualItemBroadcast(tx,ty,tinhand,xf,yf,0);
				xf = tx;
				yf = ty;
				tx = parent.nextx(xf,yf,facing);
				ty = parent.nexty(yf,facing);
			}
			else
				break;
		}
		if (xf == -1) {
			if (map.itemmap[tx][ty]==151) {
				setHands(0);
				pSend("(You throw your item into the portal.");
			}
			else
				pSend("(You can't throw that, there's something in the way!");
		}
		else {
			setHands(0);
			if (map.itemmap[tx][ty]==151) {
				pSend("(You throw your item into the portal.");
				map.limitedBroadcast(">"+parent.toDSChar(xf)+""+parent.toDSChar(yf)+"  ",xf,yf,map);
			}
			else
				map.placeItemAt(tinhand,xf,yf,map);
		}
	}
	public void doEquip() {
		if (trade!=null)
			trade.cancelTrade(0,trade);
		if (inhand == weapon) return;

		/*switch (inhand) {
			case 0:
			case 5:
			case 10:
			case 12:
			case 88:
			case 89:
			case 90:
			case 91:
			case 114:
			case 163:
			case 158:
			case 159:
			case 160:
			case 161:
				break;
			default:
				pSend("(You can't use that as a weapon.");
				return;
		}*/

		int neweq = inhand;

		Weapon w = parent.getWeapon(neweq);

		if (w==null) {
			pSend("(* You can't equip that as a weapon.");
			return;
		}

		weaponO=w;

		setHands(weapon);

		weapon = neweq;

		//deEquipWeapon(inhand);
		//equipWeapon(weapon);
		//pSend("e "+parent.encode(weapon)+""+parent.toDSChar(w.mindam)+""+parent.toDSChar(w.maxdam));
		//sendStats(c);
		sendEQStats(w);
	}
	public void doWear() {
		if (trade!=null)
			trade.cancelTrade(0,trade);
		if (inhand == armour) return;

		/*switch (inhand) {
			case 0:
			case 2:
			case 104:
			case 105:
			case 126:
			case 135:
			case 156:
			case 157:
				break;
			default:
				pSend("(You can't wear that as armor.");
				return;
		}*/

		int neweq = inhand;

		Armor a = parent.getArmor(neweq);

		if (a==null) {
			pSend("(You can't wear that as armor.");
			return;
		}

		armorO=a;

		setHands(armour);

		armour = neweq;

		//deEquipArmor(inhand);
		//equipArmor(armour);
		//pSend("e!"+parent.encode(armorO)+""+parent.toDSChar(a.mindam)+""+parent.toDSChar(a.maxdam));
		//sendStats(c);
		sendEQStats(a);
	}
	public void dodgeballThrow() {

		int thex = x;
		int they = y;

		if (map.mapnumber!=23 || !(parent.PointinRect(thex,they,15,54,25,74) && (map.tilemap[thex][they]==41 || map.tilemap[thex][they]==4)))
			return;

		thex=parent.nextx(thex,they,facing);
		they=parent.nexty(they,facing);

		if (!map.canWalk(thex,they,map)&&map.playermap[thex][they]==0) {
			pSend("(You can't throw that, there's something in the way!");
			return;
		}

		int spaces=3;

		switch (inhand) {
			case 176:
				spaces=4;
				break;
			case 177:
				spaces=6;
		}
		

		thex=inhand;
		setHands(0);
		
		//new Dodgeball(thex,cx-32,cy-32,facing,spaces,map,this,parent);
	}
	public void doRest() {
		//if (++restCount>10)
		//	return;
		if (groupstat==1) {
			pSend("(* Don't rest while you're in a group! You never know when your group will start moving again.");
			return;
		}
		if (colorstring.charAt(3) == ' ') return;
		if (bitIsMarked(stateCheck, ST_REST)) {
			stateCheck^=ST_REST;
			visishape=21;
			map.playerPlaceBroadcast(21,colorstring,cx,cy,this,map);
			pSend("[$You stand, feeling more rested.");
		}
		else {
			killFinishSword();
			stateCheck|=ST_REST;
			visishape=22;
			map.playerPlaceBroadcast(22,colorstring,cx,cy,this,map);
			pSend("[$You settle down and begin resting.");
		}
	}
	public void doInventorySwitch(char invslot) {
		//if (++restCount>30)
		//	return;

		if (trade!=null)
			trade.cancelTrade(0,trade);
		switch (inhand) {
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 175:
			case 176:
			case 177:
				pSend("(You can't put that into your inventory.");
				return;
			/*default:
				if (inhand > parent.maps[0].items) {	
					pSend("(Animals don't like being in your inventory.");
					return;
				}*/
		}
		int niw = invweight-parent.itemdefs[inventory[invslot-32]][5]+parent.itemdefs[inhand][5];
		if (niw > parent.maxweight && inhand != 0) {
			pSend("(There's too much weight in your inventory!");
			return;
		}
		invweight = niw;
		//pSend("(iw:"+niw);
		int tinhand = inhand;
		inhand = inventory[invslot-32];
		inventory[invslot-32] = tinhand;
		pSend("^"+parent.encode(inhand)+"\ni"+invslot+""+parent.encode(inventory[invslot-32]));
	}
	public void setInvPos(int item, int index) {
		if (item==0)
			invweight-=parent.itemdefs[inventory[index]][5];
		inventory[index]=item;
		pSend("i"+parent.toDSChar(index)+""+parent.encode(item));
	}
	public void doTextInput(String incoming) {

		if (showadmin==1)
			showAdmin(name,incoming);

		switch (incoming.charAt(1)) {
			case ':':
				if (checkSpeechLimit())
					return;
				if (mute)
					return;
				doEmote(incoming);
				break;
			case '/':
				if (checkSpeechLimit())
					return;
				map.doWhisper(incoming,this);
				break;
			case '-':
				if (checkSpeechLimit())
					return;
				//parent.globalBroadcast("("+name+" shouts: "+incoming.substring(2));
				//pSend("(* The Shout channel shortcut has been disabled, perhaps temporarily, to get people to learn more about channels. Boohoo.");
				checkExtendedCommands(incoming);
				break;
				//incoming = "\"[S "+incoming.substring(2);
			case '[':
				if (checkSpeechLimit())
					return;
				if (mute)
					return;
				if (incoming.length() > 2)
					doChannelSend(incoming);
				break;

			case '@':
					for (int i = 0; i < parent.admins.size(); i++) {
						if (name.equals(parent.admins.elementAt(i).toString()))
							if (checkForAdminCommand(incoming.substring(2),i))
								return;
					}				
			default:					
				if (colorstring.charAt(3) == ' ') {
					if (checkSpeechLimit())
						return;
					if (mute)
						return;
					sightSend("[#"+name+": "+parent.dogsay[(int)Math.round(Math.random()*(parent.dogsay.length-1))]);
				}	
				else {
					if (mute)
						return;
					if (dialogID!=D_NONE) {
						//if (++restCount>40)
						//	return;
						if (checkDialog(incoming))
							return;
					}	

					if (checkSpeechLimit())
						return;

					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					if (map == parent.maps[0] && tx == 33 && ty == 54) 
						doGraffitti(incoming);
					else if (map.itemmap[tx][ty] == 24) {
						desc = incoming.substring(1);
						pSend("(You have changed your description.");
						sightSend(parent.assembleSpellString(94,cx-32,cy-32));
					}
					else
						sightSend("("+name+": "+parent.filterString(incoming.substring(1)));
				}
		}
	}
	public void doEmote(String incoming) {
		String finaly="("+name;

		if (incoming.length()>2)
			finaly+=parent.filterString(((Character.isLetterOrDigit(incoming.charAt(2))) ? " " : "")+incoming.substring(2));

		sightSend(finaly);
	}
	public void doGraffitti(String incoming) {
		for (int i = map.graffitti.length-1; i > 0; i--) {
			map.graffitti[i] = map.graffitti[i-1];
		}
		map.graffitti[0] = "*"+name+"* "+incoming.substring(1);
		map.broadcast("[&"+name+" has scrawled something on the sign.",map);
	}
	public void showChannels() {
		for (int i = 0; i < channels.length; i++) {
			String tempo = "listening";
			if (!channels[i])
				tempo = "not "+tempo;
			pSend("(["+parent.channelchars[i]+" - "+tempo);
		}
	}
	public void listChannels() {
		//pSend("([<channel letter> <message> to use. [<channel letter> to turn on/off.");
		pSend("[&[s - Shout channel. For general conversation. One map only.\n[\'[q - Quest channel. For quest information and dicussions.\n[#[h - Help channel. Use this to ask for help. Admins are always listening.\n[%[g - Gossip channel. For general conversation.\n[\"[c - Combat channel. Used for issuing challenges and other combat stuff.\n[$[b - Bitch channel. Use this for bitching. Keep bitching off other channels.\n[*[t - Trade channel. Use this channel if you're looking buy/sell/trade things.\n[)[i - Info channel. For hearing game information only, you can't talk on it.\n(Please only use channels for what they are for.");
	}
	public void sendPKStats(DSpiresSocket of) {
		pSend("(Player Kill Stats for "+of.name+" (session only)");
		pSend("(--------------------------------");
		pSend("(Player kills: "+of.pkill);
		pSend("(Player deaths: "+of.loses);
		int total = of.pkill+of.loses;
		if (total > 0)
			pSend("(Fighting prowess: "+(int)(((float)(of.pkill)/total)*100)+"%");
	}
	public boolean checkSpeechLimit() {
		boolean retval = (++speechlimit>2);
		if (retval) {
			if (speechlimit==6)
				//quit("Spamming",c);
				closeIt("Spam[2]");
			else
				pSend("(You're out of breath.");
		}
		return retval;
	}
	public boolean checkDialog(String incoming) {
		switch (dialogID) {
			case D_SHOP:
				if (inshop > -1 && Character.isDigit(incoming.charAt(1)) && incoming.length()==2) {
					if (incoming.charAt(1)>='1' && incoming.charAt(1)<='6') {
						map.shops[inshop].initCommand(incoming.charAt(1)-48,map.shops[inshop],this);
						return true;
					}
				}
				break;
			case D_INTERACT:
				if (Character.isDigit(incoming.charAt(1)) && incoming.length()==2) {
					interaction.interact(incoming.charAt(1)-48,this);
					return true;
				}
				break;
			case D_TRADE:
				String bitch = incoming.toLowerCase();
				if (bitch.equals("\"y")) {
					trade.trade(trade);
					return true;
				}
				else if (bitch.equals("\"n")) {
					trade.initiator.pSend("(* "+trade.recipient.name+" refused your offer.");
					trade.cancelTrade(0,trade);
					return true;
				}
				break;
			case D_MAP_ENTER:
				String bitchy = incoming.toLowerCase();
				if (bitchy.equals("\"y")) {
					int x=parent.nextx(cx-32,cy-32,facing);
					int y=parent.nexty(cy-32,facing);
					for (int i=0;i<map.portals.length;i++) {
						if (map.portals[i]==null)
							continue;
						if (map.portals[i].orig_x==x&&map.portals[i].orig_y==y) {
							if (((BasicMap)map.portals[i].dest_map).locked && !map.portals[i].dest_map.owner.equals(name)) {
								pSend("(* Sorry. The map is locked.");
							}
							else {
								if (map.portals[i].dest_x==-1||map.portals[i].dest_y==-1)
									changeMap(map.portals[i].dest_map,0,0,0);
								else
									changeMap(map.portals[i].dest_map,2,map.portals[i].dest_x,map.portals[i].dest_y);
							}
							break;
						}
					}
				}
				else if (bitchy.equals("\"n")) {
					int x=parent.nextx(parent.nextx(cx-32,cy-32,facing),parent.nexty(cy-32,facing),facing);
					int y=parent.nexty(parent.nexty(cy-32,facing),facing);
					if (map.canWalk(x,y,map)) {
						movePlayer(x,y,false);
					}
				}
				requestDialog(D_NONE);
				return true;
		}
		
		return false;
	}
	public boolean checkForAdminCommand(String incoming, int high) {
		// TEMP ADMIN DISALLOWED

		if (high < parent.sooperadmins) {
			if (incoming.toLowerCase().startsWith("boot ")) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(5));
				if (s != null)
					s.closeIt("Booted");
				return true;
			}
			/*else if (incoming.toLowerCase().startsWith("timeout ")) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(8));
				if (s != null) {
					try {
						s.my_socket.setSoTimeout(1);
					}
					catch (Exception e) {
						pSend("("+e.toString());
						e.printStackTrace();
					}
				}
				return true;
			}*/
			else if (incoming.toLowerCase().startsWith("interrupt ")) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(10));
				if (s != null)
					s.interrupt();
				return true;
			}
			else if (incoming.toLowerCase().equals("state")) {
				if (bitIsMarked(stateCheck,ST_TRANS))
					pSend("(ST_TRANS");
				if (bitIsMarked(stateCheck,ST_DIALOG))
					pSend("(ST_DIALOG");
				if (bitIsMarked(stateCheck,ST_GROUP_MEMBER))
					pSend("(ST_ST_GROUP_MEMBER");
				if (bitIsMarked(stateCheck,ST_BRB))
					pSend("(ST_BRB");
				if (bitIsMarked(stateCheck,ST_TRADE))
					pSend("(ST_TRADE");
				if (bitIsMarked(stateCheck,ST_REST))
					pSend("(ST_REST");
				if (bitIsMarked(stateCheck,ST_FROZEN))
					pSend("(ST_FROZEN");
				return true;
			}
			else if (incoming.toLowerCase().equals("updateenemies")) {
				parent.readEnemyConfig();
				pSend("(Enemies now available: "+parent.enemyIndex.length);
				return true;
			}
			else if (incoming.toLowerCase().equals("updateweapons")) {
				parent.readWeaponConfig();
				DSpiresSocket s;
				for (int i=0;i<parent.SOCKETS;i++) {
					s=parent.socketbase[i];
					if (s!=null) {
						if (s.loggedin) {
							Weapon w = parent.getWeapon(s.weapon);
							if (w==null)
								s.weaponO=parent.weaponIndex[0];
							else
								s.weaponO=w;
							sendEQStats(s.weaponO);
						}
					}
				}
				return true;
			}
			else if (incoming.toLowerCase().equals("updatearmors")) {
				parent.readArmorConfig();
				DSpiresSocket s;
				for (int i=0;i<parent.SOCKETS;i++) {
					s=parent.socketbase[i];
					if (s!=null) {
						if (s.loggedin) {
							Armor a = parent.getArmor(s.armour);
							if (a==null)
								s.armorO=parent.armorIndex[0];
							else
								s.armorO=a;
							sendEQStats(s.armorO);
						}
					}
				}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("maxconns ")) {
				try {
					int pee=Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));
					if (pee>10) pee=10;
					parent.maxconn=pee;
					pSend("(OK!");
				}
				catch (Exception e) {}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("mutechannel ")) {
				try {
					int pee=Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));
					if (pee<0||pee>=parent.channelnames.length) {
						pSend("(Bad channel #. Valid values are 0-"+(parent.channelnames.length-1)+". Type -channels channel order....");
						return true;
					}
					parent.channelmutes[pee]=!parent.channelmutes[pee];
					if (parent.channelmutes[pee])
						pSend("("+parent.channelnames[pee]+" muted.");
					else
						pSend("("+parent.channelnames[pee]+" unmuted.");
					pSend("(Hehe. It's done, you bastard.");
				}
				catch (Exception e) {}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("changedesc ")) {
				String therest = incoming.substring(incoming.indexOf(" ")+1);
				String name = therest.substring(0,therest.indexOf(" "));
				String text = therest.substring(therest.indexOf(" ")+1,therest.length());
				DSpiresSocket s = getSocketByNearestString(name);
				if (s != null)
					s.desc=text;
				return true;
			}
			else if (incoming.toLowerCase().startsWith("changepass ")) {
				String therest = incoming.substring(incoming.indexOf(" ")+1);
				String name = therest.substring(0,therest.indexOf(" "));
				String text = therest.substring(therest.indexOf(" ")+1,therest.length());
				DSpiresSocket s = getSocketByNearestString(name);
				if (s != null)
					s.password=text.replace(' ','|');
				return true;
			}
			/*else if (incoming.toLowerCase().startsWith("slit ")) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(incoming.indexOf(" ")+1));
				if (s != null) {
					pSend("[\""+name+" slits your sniffing throat.",s);
					new Slit(s);
				}
				return true;
			}*/
			/*else if (incoming.toLowerCase().startsWith("cleanup")) {
				parent.cleanup=!parent.cleanup;
				if (!parent.cleanup) {
					stopIt(parent.dspct);
					pSend("(Socket cleanup disabled.");
				}
				else {
					parent.dspct.start();
					pSend("(Socket cleanup enabled.");
				}
				return true;
			}*/
			/*else if (incoming.toLowerCase().startsWith("portalto ")) {
				String mapname=incoming.substring(9,incoming.length());

				File f = new File("../maps/"+mapname);
				if (!f.exists()) {
					pSend("(\"../maps/"+mapname+"\" doesn't exist.");
					return true;
				}

				int i;
				for (i=0;i<parent.portals.length;i++) {
					if (parent.portals[i]==null)
						break;			
				}

				if (i==parent.portals.length) {
					pSend("(No more space.");
					return true;
				}


				parent.portals[i]=new Portal();
				parent.portals[i].map = new BasicMap(mapname,parent);
				parent.portals[i].x=parent.nextx(cx-32,cy-32,facing);
				parent.portals[i].y=parent.nexty(cy-32,facing);

				map.placeItemAt(151,parent.portals[i].x,parent.portals[i].y,map);
				return true;
			}*/
			else if (incoming.toLowerCase().startsWith("enetime ")) {
				try {
					int sex = Integer.parseInt(incoming.substring(8));
					if (sex >= 500)
						parent.sleeptime = sex;
				}
				catch (Exception e) {
				}
				return true;
			}
			else if (incoming.toLowerCase().equals("pdos")) {
				pSend("(PDOs on this map: "+map.pdos.size());
				return true;
			}
			else if (incoming.toLowerCase().equals("nez")) {
				if (!parent.nez) {
					map.broadcast("(You feel a shift in the fiber of reality. The sky becomes dark with clouds, the Sun becomes tinged red. Birds nearby stop singing and scatter in every direction. Nezerath has returned from the mountains of DragonSpires and is attacking!",map);
					//map.addEnemy(new Nezerath(parent.nextx(cx-32,cy-32,facing),parent.nexty(cy-32,facing),true,map));
					parent.nez=true;
				}
				return true;
			}
			else if (incoming.toLowerCase().equals("love")) {
				int[] path = {7,9,3,3,1,1,7,7,7,9,9,9,3,3,3,3,1,1,1,1,7,7,7,7};
				int x=cx-32,y=cy-32;
				DSpiresSocket s=this;

				sightSend("[("+name+" spreads the love of DragonSpires!");

				map.limitedBroadcast("~",x,y,map);

				sightSend(parent.assembleSpellString(301,x,y));
				s.hp=20;
				pSend("$H"+s.hp);
				updateStam(MAX_STAM);
				mp=30;
				pSend("$M"+mp);
				map.limitedBroadcast("[("+name+" heals "+s.name+"!",x,y,map);

				for (int i=0;i<path.length;i++) {
					x=parent.nextx(x,y,path[i]);
					y=parent.nexty(y,path[i]);
					map.limitedBroadcast(parent.assembleSpellString(301,x,y),x,y,map);
					s=findSocketAtPos(x,y,map,0);
					if (s!=null) {
						s.hp=20;
						s.pSend("$H"+s.hp);
						s.updateStam(MAX_STAM);
						s.mp=30;
						s.pSend("$M"+mp);
						map.limitedBroadcast("[("+name+" heals "+s.name+"!",x,y,map);
					}
				}

				map.limitedBroadcast("=",x,y,map);

				return true;
			}
			else if (incoming.toLowerCase().equals("death")) {
				int[] path = {7,9,3,3,1,1,7,7,7,9,9,9,3,3,3,3,1,1,1,1,7,7,7,7};
				int x=cx-32,y=cy-32;
				DSpiresSocket s=this;

				sightSend("[("+name+" spreads the death of DragonSpires!");

				map.limitedBroadcast("~",x,y,map);

				for (int i=0;i<path.length;i++) {
					x=parent.nextx(x,y,path[i]);
					y=parent.nexty(y,path[i]);
					map.limitedBroadcast(parent.assembleSpellString(46,x,y),x,y,map);
					s=findSocketAtPos(x,y,map,0);
					if (s!=null) {
						map.limitedBroadcast("[("+name+" kills "+s.name+"!",x,y,map);
						s.killMe(false);
					}
				}

				map.limitedBroadcast("=",x,y,map);

				return true;
			}
			else if (incoming.toLowerCase().equals("dragon")) {
				int x=parent.nextx(cx-32,cy-32,facing), y=parent.nexty(cy-32,facing);
				
				map.broadcast("~",map);
				map.placeItemAt(261,x,y,map);
				x=parent.nextx(x,y,9);
				y=parent.nexty(y,9);
				map.placeItemAt(263,x,y,map);
				map.placeItemAt(266,parent.nextx(x,y,7),parent.nexty(y,7),map);
				map.placeItemAt(265,parent.nextx(x,y,3),parent.nexty(y,3),map);
				x=parent.nextx(x,y,9);
				y=parent.nexty(y,9);
				map.placeItemAt(262,x,y,map);
				x=parent.nextx(x,y,9);
				y=parent.nexty(y,9);
				map.placeItemAt(263,x,y,map);
				map.placeItemAt(266,parent.nextx(x,y,7),parent.nexty(y,7),map);
				map.placeItemAt(265,parent.nextx(x,y,3),parent.nexty(y,3),map);
				x=parent.nextx(x,y,9);
				y=parent.nexty(y,9);
				map.placeItemAt(264,x,y,map);				
				map.broadcast("=",map);
				return true;
			}
			else if (incoming.toLowerCase().startsWith("ban ")) {
				String dname = incoming.toLowerCase().substring(4);
				if (dname.equals("mech") || dname.equals("motorhed"))
					return true;
				DSpiresSocket s = getSocketByName(dname);
				if (s != null) {
					parent.addDomainBan(s);
					parent.addNameBan(s.name);
					//s.quit("Banned.",s);
					//stopIt(false,s);
					s.closeIt("Ban");
					pSend("(Player name and hostname added to ban lists.");
				}
				else {
					parent.addNameBan(dname);
					pSend("(Player not found online, adding '"+dname+"' to the name ban list.");
				}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("snoop ")) {
				String dname = incoming.toLowerCase().substring(6);
				DSpiresSocket s = getSocketByName(dname);
				if (s != null) {
					if (s.showadmin==0)
						s.showadmin=1;
					else
						s.showadmin=0;
				}
				return true;
			}
			else if (incoming.equals("updatebans")) {
				parent.loadDomainBans();
				parent.loadNameBans();
				return true;
			}
			else if (incoming.equals("updateads")) {
				parent.loadAds();
				return true;
			}
			else if (incoming.equals("creation")) {
				parent.creation=!parent.creation;
				if (parent.creation)
					pSend("(Character creation is enabled.");
				else
					pSend("(Character creation is disabled.");
				return true;
			}
			else if (incoming.toLowerCase().equals("killtheserver")) {
				parent.killServer(0);
				return true;
			}
			else if (incoming.toLowerCase().equals("restarttheserver")) {
				parent.killServer(1);
				return true;
			}
			/*else if (incoming.toLowerCase().equals("holes")) {
				if (map.holes != null) {
					synchronized (map.holes) {
						Enumeration e = map.holes.elements();
						while (e.hasMoreElements()) {
							pSend("("+e.nextElement().toString());
						}
					}
				}
				else
					pSend("(holes=null");

				return true;
			}*/
			else if (incoming.toLowerCase().startsWith("admin ")) {
				String daname = incoming.substring(incoming.indexOf(" ")+1);
				if (daname.length() > 0 && !daname.equals("Mech") && !daname.equals("Motorhed") && !daname.startsWith("*")) {
					for (int i = 0; i < parent.admins.size(); i++) {
						if (parent.admins.elementAt(i).toString().equals(daname)) {
							if (i >= parent.defaultadmins)
								parent.admins.removeElement(daname);
							return true;
						}
					}
					DSpiresSocket z = getSocketByNearestString(daname);
					if (z != null)
 {
						if (parent.admins.size()>=6)
							pSend("(The admin list is filled to capacity, sorry.");
						else
							parent.admins.addElement(daname);
					}
				}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("enemy ")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.enemyIndex.length) throw new Exception();

					map.addEnemy(toplace,tx,ty,true);
				}
				catch (Exception e) {
					pSend("(Bad number.");
				}
				return true;
				
			}
			else if (incoming.toLowerCase().startsWith("drop")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					for (int i=10;i>0;i-=2) {
						map.limitedBroadcast(parent.assembleSpellString(toplace,tx,ty-i),tx,ty-i,map);					
					}
					map.itemmap[tx][ty]=154;
					map.broadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toplace),map);
				}
				catch (Exception e) {
					pSend("(Bad item.");
				}
				return true;
				
			}
			else if (incoming.toLowerCase().startsWith("drop2")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					for (int i=10;i>0;i-=2) {
						map.limitedBroadcast(parent.assembleSpellString(toplace,tx,ty-i),tx,ty-i,map);					
						map.limitedBroadcast(parent.assembleSpellString(289,tx,ty-i),tx,ty-i,map);					
					}
					map.itemmap[tx][ty]=154;
					map.broadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toplace),map);
				}
				catch (Exception e) {
					pSend("(Bad item.");
				}
				return true;
				
			}
			else if (incoming.toLowerCase().startsWith("trans ")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					transformPlayerToItem(toplace);
				}
				catch (Exception e) {
					pSend("(Bad item.");
				}
				return true;
				
			}
			else if (incoming.toLowerCase().equals("invis")) {
				invis=!invis;
				if (invis)
					pSend("(Invis on.");
				else 
					pSend("(Invis off.");
				return true;
			}
			else if (incoming.toLowerCase().equals("saveallcharsnow")) {
				parent.saveAllCharsNow();
				return true;
			}
			else if (incoming.toLowerCase().equals("refreshtime")) {
				pSend("(Approximately "+parent.rthread.map_reset_count+" hours until the next map refresh.");
				return true;
			}
			else if (incoming.toLowerCase().equals("updatemotd")) {
				parent.motd = parent.getMOTD();
				return true;
			}
			else if (incoming.toLowerCase().equals("funkywalk")) {
				if (mstateLoop[0]==-1) {
					mstateLoop[0]=1;
					mstateLoop[1]=2;
					mstateLoop[2]=3;
					mstateLoop[3]=0;
					pSend("(Funk On");
				}
				else {
					mstateLoop[0]=-1;
					mstateLoop[1]=0;
					mstateLoop[2]=1;
					mstateLoop[3]=0;
					pSend("(Funk Off");
				}

				return true;
			}
			else if (incoming.toLowerCase().startsWith("updatemap ")) {
				int toplace,tokens;
				String newname,newscript=null;
				try {

					StringTokenizer st = new StringTokenizer(incoming, " ");
					tokens=st.countTokens();
					if (tokens!=4&&tokens!=3) {
						pSend("(Usage: @updatemap <number> <mapname> [<scriptfile>]");
						return true;
					}

					st.nextToken();

					toplace = Integer.parseInt(st.nextToken());

					if (toplace <= 0 || toplace > parent.maps.length) throw new Exception();
					toplace--;

					newname = st.nextToken();

					File f = new File("../maps/"+newname);
					if (!f.exists()) {
						pSend("(\"../maps/"+newname+"\" doesn't exist.");
						return true;
					}

					if (tokens==4) {
						newscript=st.nextToken();
						f = new File("../maps/"+newscript);
						if (!f.exists()) {
							pSend("(\"../maps/"+newscript+"\" doesn't exist.");
							return true;
						}
					}

					new MapUpdateThread(parent.maps[toplace],newname,newscript);
				}
				catch (Exception e) {
					pSend("(Bad map #.");
				}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("info ")) {
				parent.channelBroadcast(incoming.substring(5),parent.INFO_CHANNEL);
				return true;
			}
			else if (incoming.toLowerCase().equals("textdragon")) {
				sightSend("(           ,   ,");
				sightSend("(           $,  $,     ,");
				sightSend("(           \"ss.$ss. .s'");
				sightSend("(   ,     .ss$$$$$$$$$$s,");
				sightSend("(   $. s$$$$$$$$$$$$$$`$$Ss");
				sightSend("(   \"$$$$$$$$$$$$$$$$$$o$$$       ,");
				sightSend("(  s$$$$$$$$$$$$$$$$$$$$$$$$s,  ,s");
				sightSend("( s$$$$$$$$$\"$$$$$$\"\"\"\"$$$$$$\"$$$$$,");
				sightSend("( s$$$$$$$$$$s\"\"$$$$ssssss\"$$$$$$$$\"");
				sightSend("(s$$$$$$$$$$'         `\"\"\"ss\"$\"$s\"\"");
				sightSend("(s$$$$$$$$$$,              `\"\"\"\"\"$ ");
				sightSend("(s$$$$$$$$$$$$s,...               `");
				return true;
			}
			else if (incoming.toLowerCase().equals("updateinfos")) {
				parent.rit.loadRandomInfos();
				return true;
			}
			else if (incoming.toLowerCase().startsWith("forcesw ")) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(8));
				if (s != null)
					s.doSwing();
				return true;
			}
			else if (incoming.toLowerCase().startsWith("color ")) {
				colorstring=incoming.substring(6);
				ocolorstring=colorstring;
				return true;
			}
		}

		// TEMP ADMIN ALLOWED

		if (incoming.toLowerCase().startsWith("reset ")) {
			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(6));
			if (s != null)
				s.changeMap(parent.maps[0],0,0,0);
			return true;
		}
			else if (incoming.toLowerCase().startsWith("freeze ")) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(7));
				if (s != null) {
					//if (s.state != -999)
					//	s.state = -999;
					//else
					//	s.state = -1;
					if (bitIsMarked(s.stateCheck,ST_FROZEN)) {
						s.stateCheck^=ST_FROZEN;
						pSend("(Unfrozen");
					}
					else {
						s.stateCheck|=ST_FROZEN;
						pSend("(Frozen");
					}
					/*if (s.noAllowCommands)
						pSend("(A cold breeze blows over you. Your body is turned to living ice.",s);
					else
						pSend("(You're unfrozen, dumbass. Why are you still standing there?",s);*/
				}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("place ")) {
				int toplace;
				try {

					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace==-1) {
						map.playermap[tx][ty]=0;
						//map.colorstrings[tx][ty]=null;
						return true;
					}

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					if (high >= parent.sooperadmins) {
					if (map==parent.maps[0]) {
						switch (toplace) {
							case 0:
							case 1:
							case 10:
							case 20:
							case 22:
							case 60:
								break;
							default:
								pSend("(You can only place: 0,1,10,20,22,60");
								return true;
						}
					}
					else {
						pSend("(You can't place on this map.");
						return true;
						
					}
					}

					map.placeItemAt(toplace,tx,ty,map);
				}
				catch (Exception e) {
					pSend("(Bad item.");
				}
				return true;
			}
			else if (incoming.toLowerCase().startsWith("illusion ")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					map.itemmap[tx][ty]=154;
					map.broadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toplace),map);
				}
				catch (Exception e) {
					pSend("(Bad item.");
				}
				return true;
			}
		else if (incoming.toLowerCase().startsWith("ip ")) {
			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(3));
			if (s != null)
				pSend("("+s.my_socket.getInetAddress());
			return true;
		}
		else if (incoming.toLowerCase().startsWith("jail ")) {
			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(5));
			if (s != null)
				s.jail();
			return true;
		}
		else if (incoming.toLowerCase().startsWith("jail2 ")) {
			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(6));
			if (s != null)
				s.jail2();
			return true;
		}
		else if (incoming.toLowerCase().startsWith("chuck")) {
			new DSBullet(174,cx-32,cy-32,facing,5,map,this,parent);
			return true;
		}
		else if (incoming.toLowerCase().startsWith("whatshere")) {
			//int[] tempo = parent.maps[mapnumber].findNextFloor(facing,cx-32,cy-32);
			int tx = parent.nextx(cx-32,cy-32,facing);
			int ty = parent.nexty(cy-32,facing);
			pSend("(Player: "+map.playermap[tx][ty]);
			pSend("(Item: "+map.itemmap[tx][ty]);
			pSend("(Floor: "+map.tilemap[tx][ty]);
			return true;
		}
		/*else if (incoming.toLowerCase().equals("sweeper")) {
			char tx = parent.toDSChar(parent.nextx(cx-32,cy-32,facing));
			char ty = parent.toDSChar(parent.nexty(cy-32,facing));
			map.pdoMove((PlayerDataObject)parent.SWEEPER,parent.longShapeStart[parent.SWEEPER.shapecat][parent.SWEEPER.facing],parent.SWEEPER.colorstring,parent.SWEEPER.x,parent.SWEEPER.y,tx,ty,map);
			parent.SWEEPER.x=tx;
			parent.SWEEPER.y=ty;
			return true;
		}*/
		else if (incoming.toLowerCase().startsWith("echo")) {
			if (incoming.toLowerCase().startsWith("echo "))
				map.broadcast("("+incoming.substring(5),map);
			else if (incoming.toLowerCase().startsWith("echoall "))
				parent.globalBroadcast("("+incoming.substring(8));
			else if (incoming.toLowerCase().startsWith("echoc ")) {
				int peepee;
				incoming = incoming.substring(6);
				try {
					peepee = Integer.parseInt(incoming.substring(0,incoming.indexOf(" ")));
				}
				catch (Exception e) {
					pSend("(wtf??");
					return true;
				}
				if (peepee<=0||peepee>11) {
					pSend("(wtf??");
					return true;
				}
				map.broadcast("["+parent.toDSChar(peepee-1)+incoming.substring(incoming.indexOf(" ")+1,incoming.length()),map);
			}
			else if (incoming.toLowerCase().startsWith("echoallc ")) {
				int peepee;
				incoming = incoming.substring(9);
				try {
					peepee = Integer.parseInt(incoming.substring(0,incoming.indexOf(" ")));
				}
				catch (Exception e) {
					pSend("(wtf??");
					return true;
				}
				if (peepee<=0||peepee>11) {
					pSend("(wtf??");
					return true;
				}
				parent.globalBroadcast("["+parent.toDSChar(peepee-1)+incoming.substring(incoming.indexOf(" ")+1,incoming.length()));
			}
			else if (incoming.toLowerCase().startsWith("echos ")) {
				sightSend("("+incoming.substring(6));
			}
			else if (incoming.toLowerCase().startsWith("echosc ")) {
				int peepee;
				incoming = incoming.substring(7);
				try {
					peepee = Integer.parseInt(incoming.substring(0,incoming.indexOf(" ")));
				}
				catch (Exception e) {
					pSend("(wtf??");
					return true;
				}
				if (peepee<=0||peepee>11) {
					pSend("(wtf??");
					return true;
				}
				sightSend("["+parent.toDSChar(peepee-1)+incoming.substring(incoming.indexOf(" ")+1,incoming.length()));
			}
			return true;
		}
		else if (incoming.toLowerCase().equals("parade")) {
			//for (int i = 0; i < parent.sthread.paradeon.length; i++) {
			//	parent.sthread.paradeon[i] = true;
			//}
			parent.sthread.initParade();
			return true;
		}
		else if (incoming.toLowerCase().startsWith("goto ")) {
			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(5));
			if (s != null) {
				changeMap(s.map,0,0,0);
				movePlayer(s.cx-32,s.cy-32,false);
			}
			return true;
		}
		else if (incoming.toLowerCase().startsWith("gotomap ")) {
			try {
				int bitch=Integer.parseInt(incoming.substring(8));
				if (bitch<=0||bitch>parent.maps.length)
					throw new Exception();
				changeMap(parent.maps[bitch-1],0,0,0);
			}
			catch (Exception e) {
				pSend("(Bad map, bitch.");
			}
			return true;
		}
		else if (incoming.toLowerCase().equals("coords")) {
			pSend("(X="+parent.nextx(cx-32,cy-32,facing)+", Y="+parent.nexty(cy-32,facing));
			return true;
		}
		//else if (incoming.toLowerCase().equals("place -1")) {
		//	map.playermap[parent.nextx(cx-32,cy-32,facing)][parent.nexty(cy-32,facing)]=0;
		//	return true;
		//}
		else if (incoming.toLowerCase().startsWith("mute ")) {
			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(5));
			if (s != null) {
				if (s.name.equals("Mech") || s.name.equals("Motorhed"))
					return true;

				if (s.mute) {
					s.mute=false;
					pSend("(Unmuted");
				}
				else {
					s.mute=true;
					pSend("(Muted");
					s.pSend("[\"You suddenly feel short of breath as a red light begins to glow from inside your mouth.. your vocal chords have been melted!");
				}
			}
			return true;
		}
		else if (incoming.toLowerCase().equals("flood")) {
			int[] path = {7,9,3,7,3,1,7,9,1,7,1,3,7,9,3,1,3,1,3,9,7,1,3,7,7,9,9,9,3,1,3,7,1};
			int x=cx-32,y=cy-32;
			DSpiresSocket s=this;

			sightSend("[("+name+" floods for no reason at all.");

			for (int i=0;i<path.length;i++) {
				x=parent.nextx(x,y,path[i]);
				y=parent.nexty(y,path[i]);
				map.limitedBroadcast(parent.assembleSpellString(309,x,y),x,y,map);
			}
			return true;
		}
		else if (incoming.toLowerCase().equals("map")) {
			pSend("(This is map "+(map.mapnumber+1)+".");
			return true;
		}
		else if (incoming.toLowerCase().equals("destroymap")) {
			int tx = parent.nextx(cx-32,cy-32,facing);
			int ty = parent.nexty(cy-32,facing);
			if (map.itemmap[tx][ty]==151) {
				if (map.portals!=null) {
					for (int i=0;i<map.portals.length;i++) {
						if (map.portals[i]==null)
							continue;
						if (map.portals[i].orig_x==tx&&map.portals[i].orig_y==ty) {
							if (map.portals[i].dest_map instanceof BasicMap && map != map.portals[i].dest_map) {
								map.destroyMap(map.portals[i].dest_map);
							}
						}
					}
				}
			}
			else
				pSend("(You're supposed to be facing the portal to the map to destroy as an admin.");
			return true;
		}
			else if (incoming.toLowerCase().startsWith("enemy ")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > 9) throw new Exception();

					map.addEnemy(toplace,tx,ty,true);
				}
				catch (Exception e) {
					pSend("(Foo. You can only place enemies 0-9.");
				}
				return true;

			}
			else if (incoming.toLowerCase().startsWith("drop")) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					for (int i=10;i>0;i-=2) {
						map.limitedBroadcast(parent.assembleSpellString(toplace,tx,ty-i),tx,ty-i,map);
					}
					map.itemmap[tx][ty]=154;
					map.broadcast(">"+parent.toDSChar(tx)+""+parent.toDSChar(ty)+""+parent.encode(toplace),map);
				}
				catch (Exception e) {
					pSend("(Bad item.");
				}
				return true;

			}

		return false;
	}
	public void jail() {
		pSend("(* You've been thrown in jail for being a bad monkey.");
		changeMap(parent.maps[31],0,0,0);
		int t = (int)Math.round(Math.random()*2);
		movePlayer(parent.jailcoords[t][0],parent.jailcoords[t][1],false);
	}
	public void jail2() {
		pSend("(* You've been thrown in jail without bail for being a moron.");
		changeMap(parent.maps[31],0,0,0);
		movePlayer(29,42,false);
	}
	public void doChannelSend(String incoming) {
		if (incoming.length() > 3) {
			if (incoming.charAt(3) != ' ') {
				pSend("(* Channel usage: [<channel letter> <message>");
				return;
			}
		}

		char ch = Character.toUpperCase(incoming.charAt(2));
		int i;
		for (i = 0; i < parent.channelchars.length; i++) {
			if (parent.channelchars[i] == ch)
				break;
		}
		if (i==parent.channelchars.length) {
			pSend("(* That isn't a channel.");
			return;
		}

		if (parent.channelmutes[i]) {
			pSend("(* The "+parent.channelnames[i]+" channel is currently muted.");
			return;
		}

		if (!channels[i]) {
			channels[i] = true;
			parent.channels[i].addElement(this);
			pSend("(* You are now listening to the "+parent.channelnames[i]+" channel.");
		}
		else if (incoming.length() <= 4) {
			channels[i] = false;
			parent.channels[i].removeElement(this);
			pSend("(* You are no longer listening to the "+parent.channelnames[i]+" channel.");
			return;
		}
		if (incoming.length() > 4) {
			if (ch == 'I')
				pSend("(The Info channel is for listening only. You can't talk on it.");
			else if (ch == 'S') {
				//synchronized (parent.channels[i]) {
					incoming="["+parent.channelcolors[i]+"["+ch+"] "+name+": "+incoming.substring(4);
					incoming=parent.filterString(incoming);
					Enumeration e = parent.channels[i].elements();
					DSpiresSocket c4;
					while (e.hasMoreElements()) {
						c4 = (DSpiresSocket)e.nextElement();
						if (c4.map == map)
							c4.pSend(incoming);
					}
				//}
			}
			else
				parent.channelBroadcast(name+": "+incoming.substring(4),i);
		}
	}
	public void checkExtendedCommands(String incoming) {
		String tochk = incoming.substring(2).toLowerCase();

		if (groupstat>0) {
			if (tochk.equals("glist")) {
				pSend("(Group: "+Group.groupList());
				return;
			}
			else if (tochk.startsWith("gsay")) {
				pSend("[!Wee. Try using '-g' instead of '-gsay' now.");
			}
			else if (tochk.startsWith("g ")) {
				Group.groupSend("[!{G} "+name+": "+incoming.substring(4));
				return;
			}

			else if (groupstat==2) {
				if (tochk.equals("disband")) {
					Group.disbandGroup();
					return;
				}
				else if (tochk.equals("transpose") || tochk.equals("tp")) {
					//if (++restCount>5)
					//	return;
					Group.transpose();
					return;
				}
				else if (tochk.startsWith("kick ")) {
					Group.kickMember(incoming.substring(7));
					return;
				}
			}
		}

		if (inhand==119) {
			int scroll=0;
			if (tochk.equals("red"))
				scroll=97;
			else if (tochk.equals("yellow"))
				scroll=59;
			else if (tochk.equals("green"))
				scroll=57;
			else if (tochk.equals("light blue"))
				scroll=99;
			else if (tochk.equals("dark blue"))
				scroll=58;
			else if (tochk.equals("purple"))
				scroll=98;
			else if (tochk.equals("pink"))
				scroll=100;
			else if (tochk.equals("grey"))
				scroll=101;
			else if (tochk.equals("silver"))
				scroll=102;
			if (scroll!=0) {
				if (findInInv(scroll)!=-1)
					doUse(scroll);
			}
		}

		if (tochk.equals("join")) {
			if (colorstring.charAt(3)==' ')
				pSend("[#As a little puppy, you are destined to travel alone...");
			else if (map.mapnumber==23)
				pSend("(* There's no reason to join on this map. Heh.");
			else
				initJoin();
			return;
		}
		else if (tochk.equals("nogroup")) {
			follow=!follow;
			pSend("(You now "+((follow) ? "" : "do not ")+"allow other people to group with you.");
			return;
		}
		/*else if (tochk.equals("save")) {
			parent.savePlayerData(c);
			pSend("(You have been saved.");
		}*/
		else if (tochk.equals("channels"))
			listChannels();
		else if (tochk.equals("mychannels"))
			showChannels();
		else if (tochk.startsWith("gg "))
			giveGold(incoming);
		else if (tochk.equals("give"))
			doGive(incoming);
		else if (tochk.equals("sight"))
			pSend("("+sight.size());
		else if (tochk.equals("dig"))
			doDig();
		else if (tochk.startsWith("dropgold "))
			dropGold(incoming);
		else if (tochk.equals("adminlist")) {
			String blah = "Primary Administrators: ";
			String blah2 = "Secondary Administrators: ";

			for (int i = 0; i < parent.sooperadmins; i++) {
				blah+=parent.admins.elementAt(i).toString()+", ";
			}
			blah=blah.substring(0,blah.length()-2)+".";

			for (int i=parent.sooperadmins;i<parent.admins.size();i++) {
				if (i<parent.defaultadmins)
					blah2+="*";
				blah2+=parent.admins.elementAt(i).toString()+", ";
			}
			if (blah2.endsWith(", ")) {
				blah2=blah2.substring(0,blah2.length()-2)+".";
			}
			else
				blah2=blah2+" None.";

			pSend("[\'"+blah+"\n[&"+blah2+"\n[&   * = Default Secondary Administrator");
		}
		else if (tochk.equals("brb") || tochk.equals("afk"))
			doBRB();
		else if (tochk.equals("attacks")) {
			seeattacks=!seeattacks;
			if (seeattacks)
				pSend("(* You will now see all attacks.");
			else
				pSend("(* You will now only see attacks that involve you.");
		}
		else if (tochk.equals("pkstats")) {
			DSpiresSocket s = findSocketAtPosInSight(parent.nextx(cx-32,cy-32,facing),parent.nexty(cy-32,facing),1);
			if (s!=null)
				sendPKStats(s);
			else
				pSend("(There's no one there! To see your own pk stats type -mypkstats");
		}
		else if (tochk.equals("mypkstats"))
			sendPKStats(this);
		else if (tochk.equals("weight"))
			pSend("(Your inventory weight is at "+Math.round(((float)invweight/(float)parent.maxweight)*100)+"%.");
		else if (tochk.equals("myemail")) {
			email="";
			pSend("(Your e-mail has been cleared.");
		}
		else if (tochk.equals("mysite")) {
			homepage="";
			pSend("(Your web site has been cleared.");
		}
		else if (tochk.equals("mytitle")) {
			homepage="";
			pSend("(Your title has been cleared.");
		}
		else if (tochk.startsWith("myemail ")) {
			String it=incoming.substring(incoming.indexOf(" ")+1);
			if (it.length()>30)
				pSend("(Sorry, it can't be longer than 30 characters.");
			else {
				email=it;
				pSend("(You e-mail is now \""+it+"\".");
			}
		}
		else if (tochk.startsWith("mysite ")) {
			String it=incoming.substring(incoming.indexOf(" ")+1);
			if (it.length()>50)
				pSend("(Sorry, it can't be longer than 50 characters.");
			else {
				homepage=it;
				pSend("(You web site is now \""+it+"\".");
			}
		}
		else if (tochk.startsWith("mytitle ")) {
			String it=incoming.substring(incoming.indexOf(" ")+1);
			if (it.length()>30)
				pSend("(Sorry, it can't be longer than 30 characters.");
			else {
				title=parent.filterString(it);
				pSend("(You title is now \""+title+"\".");
			}
		}
		else if (tochk.equals("bash")) {
			pSend("(Hehe.");
		/*	int tx = parent.nextx(cx-32,cy-32,facing);
			int ty = parent.nexty(cy-32,facing);

			Enemy e = map.findEnemyAtPos(tx,ty,map);
			if (e != null) {
				int damage=W2A(parent.BASH,e.me.armor);
				sightSendAttacks("[\""+name+" lunges violently and bashes "+e.me.name+"! ("+damage+")",c,null);
				int pdamage=(int)Math.round(Math.random()*3)+4;
				sightSendAttacks("[\""+name+" takes "+pdamage+" damage!",c,null);
				if (e.doDamageToEnemy(damage,e)) {
					updateAlign(e.me.align);
					sightSendAttacks("!4",c,null);
					sightSendAttacks("[\""+name+" did slay "+e.me.name+"!",c,null);
					if (e instanceof i1Enemy)
						((i1Enemy)e).die((i1Enemy)e);
					else
						e.die(e);
				}

				if (doDamageToPlayer(pdamage)) {
					sightSendAttacks("[\""+name+" was wounded fatally...",c,null);
					killMe(c,true);
				}
				//e.attack(cx-32,cy-32,e);
			}
			else
				pSend("(You decide not to bash because there's no one there to bash.");*/
		}
		else if (tochk.equals("tradeforgold")) {
				pSend("(Do it like this: -tradeforgold <gold amount>");
		}
		else if (tochk.startsWith("tradeforgold ")) {
			int gold=-1;
			try {
				gold=Integer.parseInt(tochk.substring(tochk.indexOf(" ")+1));
			}
			catch (NumberFormatException e) {
				pSend("(Do it like this: -tradeforgold <gold amount>");
			}

			if (gold<0) {
				pSend("(Positive numbers puhleeeeze.");
				return;	
			}

			if (inhand==19 || inhand==175 || inhand==176 || inhand==177) {
				pSend("(You can't trade that.");
				return;	
			}

			Trade t = new Trade();
			if (!t.initTrade(0,gold,this))
				t=null;
		}
		else if (tochk.equals("goparade")) {
			if (parent.goparade && map.mapnumber!=31) {
				changeMap(parent.maps[0],0,0,0);
			}
		}
		else if (tochk.equals("exitmap")||tochk.equals("em")) {
			if (map instanceof BasicMap) {
				changeMap(map.parentmap,0,0,0);
			}				
		}
		else if (tochk.equals("canpk")) {
			pSend("(You can"+((map.allowcombat&&okToAttack(x,y,map)) ? "" : "'t")+" player-kill right here.");
		}
		else if (tochk.equals("destroymap")) {
			if (map.owner==null)
				return;
			if (!map.owner.equals(name))
				pSend("(* This is not your map.");
			else
				map.destroyMap(map);
		}
		else if (tochk.equals("destroyitem") || tochk.equals("di")) {
			switch (inhand) {
				case 0:
					pSend("[\"* There's nothing in your hands, smarty.");
					break;
				case 19:
				case 175:
				case 176:
				case 177:
				case 149:
				case 150:
					pSend("[\"* You should not destroy that.");
					break;
				default:
					setHands(0);
					pSend("[\"* You bash the item to bits, leaving nothing significant behind.");
			}
		}
		else if (tochk.equals("shove")) {
			int x=parent.nextx(cx-32,cy-32,facing);
			int y=parent.nexty(cy-32,facing);
			if (map.npc3s != null&&map.playermap[x][y]!=0) {
				NPC3 n;
				for (int i = 0;i < map.npc3s.size();i++) {
					n = (NPC3)map.npc3s.elementAt(i);
					if (n.x-32 == x && n.y-32 == y) {
						if (updateStam(-50)) {
							n.move(facing);
							pSend("(* You give "+n.name+" a good shove.");
						}
						return;
					}
				}
			}
			pSend("(* There's nothing here that you should shove, really...");
		}
		//else if (tochk.equals("map")) {
		//	uploadSimulation(c);
		//}
		else if (tochk.equals("mapwho")) {
			String retval="";
			if (map instanceof BasicMap) {
				DSpiresSocket s;
				//synchronized(map.sockets) {
					Enumeration e = map.sockets.elements();
					while (e.hasMoreElements()) {
						s = (DSpiresSocket)e.nextElement();
						if (retval.equals(""))
							retval+=s.name;
						else
							retval+=", " + s.name;
					}
				//}				
				retval+=".";
				pSend("[&Players on map: "+retval);
			}
		}
		else if (tochk.equals("lag")) {
			//if (++restCount>5)
			//	return;
			sightSend(parent.assembleSpellString(268,cx-32,cy-32)+"\n[\""+name+" lags...");
		}
		else if (tochk.equals("pose")) {
			if (colorstring.charAt(3)==' '||!updateStam(weaponO.stam))
				return;
			if (currsword==2)
				currsword=3;
			else
				currsword=2;
			visishape=currshape+currsword;
			sightSend("<"+cx+""+cy+""+parent.toDSChar(visishape)+""+colorstring);
		}
		else if (tochk.equals("scout")) {
			int nx=-99,ny=-99;

			switch (facing) {
				case 1:
					nx=x-2;
					ny=y+6;
					break;
				case 3:
					nx=x+2;
					ny=y+6;
					break;
				case 7:
					nx=x-2;
					ny=y-7;
					break;
				case 9:
					nx=x+2;
					ny=y-7;
					break;
			}

			if (nx!=-99) {
				pSend("@"+parent.toDSChar(nx)+parent.toDSChar(ny)+"\n=");
			}
		}
		else if (tochk.startsWith("boot ")) {
			if (name.equals(map.owner)) {
				DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(7));
				if (s != null) {
					if (s.map==map) {
						s.pSend("(* You have been booted from this map.");
						s.changeMap(map.parentmap,0,0,0);
						pSend("(* "+s.name+" has been booted from the map.");
					}
					else
						pSend("(* That person is not on your map.");
				}
			}
			else
				pSend("(* Sorry. This is not your map.");
		}
		else if (tochk.startsWith("place ")) {
			if (name.equals(map.owner)) {
				int toplace;
				try {
					int tx = parent.nextx(cx-32,cy-32,facing);
					int ty = parent.nexty(cy-32,facing);

					toplace = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));

					if (toplace < 0 || toplace > parent.maps[0].items) throw new Exception();

					
					if (parent.findIntInArray(toplace,parent.itemsdisallowed))
						throw new Exception();
					else
						map.placeItemAt(toplace,tx,ty,map);
				}
				catch (Exception e) {
					pSend("(* You can't place that item.");
				}
			}
			else
				pSend("(* Sorry. This is not your map.");
		}
		else if (tochk.startsWith("echo")) {
			if (!name.equals(map.owner)) {
				pSend("(* Sorry. This is not your map.");
				return;
			}

			if (tochk.startsWith("echo "))
				map.broadcast("("+incoming.substring(7),map);
			else if (tochk.startsWith("echoc ")) {
				int peepee;
				incoming = incoming.substring(8);
				try {
					peepee = Integer.parseInt(incoming.substring(0,incoming.indexOf(" ")));
				}
				catch (Exception e) {
					pSend("(* Do it like this: -echoc <color#> <message>");
					return;
				}
				if (peepee<=0||peepee>11) {
					pSend("(* Valid color numbers are 1 to 11.");
					return;
				}
				map.broadcast("["+parent.toDSChar(peepee-1)+incoming.substring(incoming.indexOf(" ")+1,incoming.length()),map);
			}
			else if (tochk.startsWith("echos ")) {
				sightSend("("+incoming.substring(8));
			}
			else if (tochk.startsWith("echosc ")) {
				int peepee;
				incoming = incoming.substring(9);
				try {
					peepee = Integer.parseInt(incoming.substring(0,incoming.indexOf(" ")));
				}
				catch (Exception e) {
					pSend("(* Do it like this: -echosc <color#> <message>");
					return;
				}
				if (peepee<=0||peepee>11) {
					pSend("(* Valid color numbers are 1 to 11.");
					return;
				}
				sightSend("["+parent.toDSChar(peepee-1)+incoming.substring(incoming.indexOf(" ")+1,incoming.length()));
			}
		}
		else if (tochk.startsWith("goto ")) {
			if (!name.equals(map.owner)) {
				pSend("(* Sorry. This is not your map.");
				return;
			}

			DSpiresSocket s = getSocketByNearestString(incoming.toLowerCase().substring(7));
			if (s != null) {
				if (s.map==map)
					movePlayer(s.cx-32,s.cy-32,false);
				else
					pSend("(* That person is not on your map.");
			}
			return;
		}
		else if (tochk.equals("lock")) {
			if (name.equals(map.owner)) {
				((BasicMap)map).locked=!((BasicMap)map).locked;
				if (((BasicMap)map).locked)
					pSend("(* The map is now locked.");
				else
					pSend("(* The map is now unlocked.");
			}
			else
				pSend("(* Sorry. This is not your map.");
		}
		else if (tochk.equals("uptime")) {
			pSend("[\"* The server has been up approximately "+parent.uptime+" hours.");
		}
		else if (tochk.equals("wpnstam")) {
			pSend("[\"* The weapon you're using takes away "+-weaponO.stam+" units of stamina.");
		}
		else if (tochk.startsWith("laptop:")) {
			int tx = parent.nextx(x,y,facing);
			int ty = parent.nexty(y,facing);
			if (map.itemmap[tx][ty]==26) {
				if (map.laptops==null) {
					map.laptops = new Vector();
					map.laptops.addElement(new Laptop(tx,ty));
				}

				Laptop l = getLaptopAtPos(tx,ty);
				if (l==null)
					pSend("(Hmm. This isn't right.");
				else
					doLaptopCommand(tochk,l);
			}
			else pSend("[\"There's no laptop here!");
		}
	}
	public void dropGold(String incoming) {
		//if (++restCount>10)
		//	return;
		if (map.itemmap[cx-32][cy-32]!=0) {
			pSend("(* There's something in the way!");
			return;
		}
		int togive;
		try {
			togive = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));
			if (togive > gold || togive < 0) throw new Exception();
		}
		catch (Exception e) {
			pSend("(You don't have that much gold.");
			return;
		}

		if (togive==0) {
			pSend("(You're dumb.");
			return;
		}

		//if (state > -1)
		//	resetState(giver);

		updateGold(-togive);
		
		switch (togive) {
			case 350:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(11,x,y,map);
				return;
			case 100:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(25,x,y,map);
				return;
			case 1:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(107,x,y,map);
				return;
			case 5:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(108,x,y,map);
				return;
			case 10:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(109,x,y,map);
				return;
			case 25:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(110,x,y,map);
				return;
			case 50:
				pSend("(You drop "+togive+" gold.");
				map.placeItemAt(111,x,y,map);
				return;
		}
					
		boolean normal=true;

		pSend("(You drop a pouch with "+togive+" gold in it.");

		if (map.mapnumber==31) {
			int x=cx-32,y=cy-32;
			if ((x==30 && y==50) ||  (x==32 && y==53) || (x==33 && y==55)) {
				if (togive==1000) {
					normal=false;
					if (x==30 && y==50)
						freePrisoner(29,52);
					else if (x==32 && y==53)
						freePrisoner(31,55);
					else if (x==33 && y==55)
						freePrisoner(32,57);
				}
				else
					pSend("(The Warden: Bail is *1000* gold, idiot.");
			}

		}

		if (normal) {
			BagOStuff b = map.addBag(cx-32,cy-32,togive,new int[0],map);
			b.below=parent.checkForTrans(b.below,x,y,map);
		}
	}
	public void freePrisoner(int x, int y) {
		pSend("(The Warden: Out you go. I'll see you again soon, "+name+". Hahaha.");
		pSend("[\"The rusty gate slams behind you as you leave the dank cell. You won't miss this place.");
		movePlayer(x,y,false);
	}
	public void doGive(String incoming) {
		if (trade!=null)
			trade.cancelTrade(0,trade);

		if (inhand == 0) return;

		if (parent.findIntInArray(inhand,parent.nogive)) {
			pSend("(* Magical forces prevent you from giving it away.");
			return;
		}

		int tx = parent.nextx(cx-32,cy-32,facing);
		int ty = parent.nexty(cy-32,facing);
		DSpiresSocket recipient = findSocketAtPosInSight(tx,ty,1);
		if (recipient == null) {
			if (map.npc3s != null) {
				int tcx = tx+32;
				int tcy = ty+32;
				NPC3 npc;
				for (int i = 0; i < map.npc3s.size();i++) {
					npc = (NPC3)map.npc3s.elementAt(i);
					if (npc.x == tcx && npc.y == tcy) {
						if (npc instanceof Givetoable) {
							((Givetoable)npc).get(this);
							return;
						}
						pSend("(You see no reason to give your item away.");
						return;
					}
				}
			}
			
			NPC2 npc2 = parent.npc2base.findNPC2(tx,ty,map);
			if (npc2!=null) {
				if (npc2 instanceof Givetoable) {
					((Givetoable)npc2).get(this);
					return;
				}
				pSend("(You see no reason to give your item away.");
				return;
			}

			pSend("(There's no one there!.");
			return;
		}

		if (recipient.inhand != 0) {
			pSend("("+recipient.name+"'s hands are full.");
			recipient.pSend("("+name+" tries to give you something but your hands are full.");
			return;
		}
		if (recipient.trade!=null)
			recipient.trade.cancelTrade(0,recipient.trade);
		recipient.setHands(inhand);
		setHands(0);
		recipient.pSend("[$"+name+" gives you "+parent.getItemName(recipient.inhand)+".");
		pSend("[$You give "+recipient.name+" "+parent.getItemName(recipient.inhand)+".");
		recipient.pickUpItemCheck(recipient.inhand);
	}
	public void doDig() {
		if (weapon!=163) {
			pSend("[#* Dig with what?? (you need to equip a shovel)");
			return;
		}

		int breaktest = parent.dice(1,12);
		if (breaktest > 10) {
			weapon = 0;
			weaponO=parent.getWeapon(0);
			sendEQStats(weaponO);
			pSend("[\"* ARGH. Your shovel has broke! Darn cheap shovels.");
			return;
		}

		switch (map.tilemap[cx-32][cy-32]) {
			case 5:
			case 6:
			case 8:
			case 9:
			case 62:
			case 66:
			case 77:
				break;
			default:
				pSend("[#* You just can't seem to get your shovel through the floor.");
				return;
		}

		if (!updateStam(-30))
			return;

		switch (map.itemmap[cx-32][cy-32]) {
			case 0:
				break;
			case 260:
				pSend("[#* You fill in the hole.");
				map.placeItemAt(0,cx-32,cy-32,map);
				return;
			default:
				pSend("[#* You can't dig a hole, there's something there.");
				return;
		}

		String data;

		if (map.holes != null) {
			//synchronized (map.holes) {
				Enumeration e = map.holes.elements();
				while (e.hasMoreElements()) {
					data=e.nextElement().toString();
					//pSend("("+cx+""+cy+":"+data.charAt(0)+""+data.charAt(1));
					if (data.charAt(0)==cx&&data.charAt(1)==cy) {
						int get = parent.decode(data.charAt(2)-32,data.charAt(3)-32);
						if (inhand != 0) {
							pSend("[#* You've hit something with your shovel. You'd better empty your hands so you can pick it up.");
							return;
						}
						else if (pickUpItemCheck(get)==0) {
							map.placeItemAt(260,cx-32,cy-32,map);
							pSend("[#* You find "+parent.itemnames[get]+" in the hole.");
							setHands(get);
							map.holes.removeElement(data);
							if (map.holes.size()==0)
								map.holes=null;
						}
						return;
					}
				}
			//}
		}

		map.placeItemAt(260,cx-32,cy-32,map);
		pSend("[#* You find nothing in the hole.");
	}
	public void giveGold(String incoming) {
		//if (++restCount>10)
		//	return;

		if (map.allowcombat) {
			if (okToAttack(x,y,map)) {
				pSend("(Why would you want to give gold here, in a combat area?");
				return;
			}
		}

		int togive;
		try {
			togive = Integer.parseInt(incoming.substring(incoming.indexOf(" ")+1));
			if (togive > gold || togive < 0) throw new Exception();
		}
		catch (Exception e) {
			pSend("(You don't have that much gold.");
			return;
		}

		if (togive==0) {
			pSend("(You're dumb.");
			return;
		}
		//int[] temppos = parent.maps[mapnumber].findNextFloor(facing,cx-32,cy-32);
		int tx = parent.nextx(cx-32,cy-32,facing);
		int ty = parent.nexty(cy-32,facing);
		DSpiresSocket recipient = findSocketAtPosInSight(tx,ty,1);
		if (recipient == null||recipient == this) {
			pSend("(There's no one there!");
			return;
		}
		recipient.updateGold(togive);
		updateGold(-togive);
		recipient.pSend("[$"+name+" gives you "+togive+" gold.");
		pSend("[$You give "+recipient.name+" "+togive+" gold.");
	}
	public void showAdmin(String name, String incoming) {
		DSpiresSocket c = getSocketByName("Mech");
		if (c != null)
			c.pSend("(>>"+name+": "+incoming);

		c = getSocketByName("Motorhed");
		if (c != null)
			c.pSend("(>>"+name+": "+incoming);
	}
	public void initJoin() {

		if (groupstat!=0) {
			pSend("(You're already in a group.");
			return;
		}
		if (bitIsMarked(stateCheck, ST_REST)) {
			pSend("(You're resting. You don't feel like doing that right now.");
			return;
		}
 		//if (state != -1)
		//	stateCheck("m");

		int tx = parent.nextx(cx-32,cy-32,facing);
		int ty = parent.nexty(cy-32,facing);

		DSpiresSocket toFollow = findSocketAtPosInSight(tx,ty,1);
		if (toFollow == null || toFollow == this) {
			pSend("(There's no one there!");
			return;
		}

		if (toFollow.colorstring.charAt(3)==' ') {
			pSend("[#You don't want a dog as the leader of a group, you can't understand anything they say!");
			return;
		}	

		if (parent.itemdefs[map.itemmap[tx][ty]][0]==1) {
			pSend("(You shouldn't join here.");
			return;
		}

		if (toFollow.follow && toFollow.groupstat!=1) {
			if (toFollow.Group == null) {
				toFollow.Group = new Group(toFollow);
				toFollow.pSend("(You become the leader of a group.");
			}
	
			toFollow.Group.addMember(this);
			//checkBeforeMove=true;
		}
		else
			pSend("("+toFollow.name+" isn't allowing anyone to join.");

	}
	public void setPosition(int tx, int ty) {
		x=tx;
		y=ty;
		cx=parent.toDSChar(tx);
		cy=parent.toDSChar(ty);
		//System.out.println(tx+","+ty);
	}
	public void quit() {
		//synchronized (map.sockets) {
			if (loggedin) {
				loggedin=false;
				parent.userLoggedOut();
	
				if (stateCheck!=0)
					doStateChecker("m");

				map.sockets.removeElement(this);
				map.playerPlaceBroadcast(0,colorstring,cx,cy,this,map);

				for (int i = 0; i < parent.channels.length; i++) {
					if (channels[i])
						parent.channels[i].removeElement(this);
				}

				try {
					if (groupstat==1)
						Group.removeMember(this);
					else if (groupstat==2)
						Group.disbandGroup();

					if (trade!=null)
						trade.cancelTrade(0,trade);

					switch (inhand) {
						case 149:
						case 150:
							parent.recoverFlag(this);
					}

					//for (int i = 0; i < inventory.length; i++) {
					//	if (inventory[i] > parent.maps[0].items)
					//		map.itemRecover(inventory[i],map);
					//}

				}
				catch (Exception e) {
					e.printStackTrace();
				}

				parent.channelBroadcast(name+" has left DragonSpires.",parent.INFO_CHANNEL);
	
				savePlayerData();
	
				//parent.userLoggedOut();
			}
		//}
	}
	public void doLaptopHit(int tx, int ty) {
		if (map.mapnumber == 3 && cx-32 == 24 && cy-32 == 52)
			doSlots();
		else if (map.mapnumber == 11 && cx-32 == 17 && cy-32 == 23)
			doSlots2();
		else {
			map.limitedBroadcast("[\"Mech's laptop makes threatening beeps at "+name+".",tx,ty,map);
		}
	}
	public Laptop getLaptopAtPos(int x, int y) {
		if (map.laptops==null)
			return null;
		Laptop l;
		for (int i=0;i<map.laptops.size();i++) {
			l = (Laptop)map.laptops.elementAt(i);
			if (l.x==x&&l.y==y)
				return l;
		}
		return null;
	}
	public void doLaptopCommand(String command, Laptop l) {
		try {
		//if (++restCount>10)
		//	return;
		if (l.lock && !name.equals(map.owner)) {
			pSend("[\"This laptop is locked!");
			return;
		}
		command = command.toLowerCase();
		if (command.equals("laptop:help")) {
			pSend("[\"Mech's laptop can accept commands from you and store data. Mech's laptop can store a string of text up to 100 characters long and one number. Here are the laptop commands, all of which start with -laptop:': num+, num-, num=<number>, text=<text here>, shownum, showtext, lock.");
		}
		else if (command.startsWith("laptop:num")) {
			switch (command.charAt(10)) {
				case '+':
					//if (++l.num>1000) l.num=1000;
					l.num++;
					laptopData();
					break;
				case '-':
					//if (--l.num<0) l.num=0;
					l.num--;
					laptopData();
					break;
				case '=':
					//try {
						l.num=Integer.parseInt(command.substring(command.indexOf("=")+1));
						laptopData();
						break;
					//}
					//catch (Exception e) {
					//}
				default:
					pSend("[\"Invalid command!");
			}
		}
		else if (command.startsWith("laptop:text") && command.charAt(11)=='=') {
			l.text=command.substring(command.indexOf("=")+1);
			if (l.text.length()>100) l.text=l.text.substring(0,100);
			laptopData();
		}
		else if (command.equals("laptop:showtext")) {
			map.limitedBroadcast("[\"Mech's laptop says: text="+l.text,l.x,l.y,map);
		}
		else if (command.equals("laptop:shownum")) {
			map.limitedBroadcast("[\"Mech's laptop says: num="+l.num,l.x,l.y,map);
		}
		else if (command.equals("laptop:lock")) {
			if (name.equals(map.owner)) {
				l.lock=!l.lock;
				if (l.lock)
					pSend("[\"This laptop is now locked.");
				else
					pSend("[\"This laptop is now unlocked.");
				laptopData();
			}
			else
				pSend("[\"This is not your laptop, you cannot lock it!");
		}
		else
			pSend("[\"Invalid command!");
		}
		catch (Exception e) {
			pSend("[\"Invalid command!");
		}
	}
	public void laptopData() {
		map.limitedBroadcast("[\"Mech's laptop beeps as "+name+" enters data.",x,y,map);
	}
}
