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

import java.io.*;

public class DSPCleanupThread extends Thread {
	DSpiresServer parent;
	//PrintWriter w;
	//int testno=0;
	//boolean active=false;
	long pong_wait=30000;
	long ping_interval=30000;

	public DSPCleanupThread(DSpiresServer p) {
		this.setPriority(Thread.MAX_PRIORITY);
		parent=p;
	}
	public void run() {
		try {
			while (true) {
				//if (active) {
					//switch (testno) {
						//case 0:
							for (int i=0;i<parent.SOCKETS;i++) {
								if (parent.socketbase[i] != null) {
									try {
										parent.socketbase[i].gotPong=0;
										parent.socketbase[i].pSend("ping");
										//if (parent.socketbase[i].name.equals("Mech"))
										//	parent.socketbase[i].pSend("[#Sending ping");
										//sleep(1);
									}
									catch (Exception e) {
										e.printStackTrace();
										if (parent.socketbase[i]!=null)
											//parent.socketbase[i].quit("Error in cleanup. (0)",parent.socketbase[i]);
											//parent.socketbase[i].stopIt(false,parent.socketbase[i]);
											parent.socketbase[i].closeIt("?");
									}
								}
							}
							//testno=1;
							sleep(pong_wait);
							/*for (int i=0;i<parent.SOCKETS;i++) {
								if (parent.socketbase[i] != null) {
									try {
										parent.socketbase[i].pSend("ping");
										//sleep(1);
									}
									catch (Exception e) {
										e.printStackTrace();
										if (parent.socketbase[i]!=null)
											//parent.socketbase[i].quit("Error in cleanup. (1)",parent.socketbase[i]);
											//parent.socketbase[i].stopIt(false,parent.socketbase[i]);
											parent.socketbase[i].closeIt("?");
									}
								}
							}
							sleep(pong_wait);*/
							//break;
						//case 1:

							for (int i=0;i<parent.SOCKETS;i++) {
								if (parent.socketbase[i] != null) {
									try {
										if (parent.socketbase[i].gotPong==0) {
											//parent.socketbase[i].quit("Timeout",parent.socketbase[i]);
											//parent.socketbase[i].stopIt(false,parent.socketbase[i]);
											//parent.socketbase[i].setPriority(Thread.MIN_PRIORITY);
											parent.socketbase[i].closeIt("Timeout");
										}
										//sleep(200);
									}
									catch (Exception e) {
										e.printStackTrace();
										if (parent.socketbase[i]!=null)
											//parent.socketbase[i].quit("Error in cleanup. (2)",parent.socketbase[i]);
											//parent.socketbase[i].stopIt(false,parent.socketbase[i]);
											parent.socketbase[i].closeIt("?");
									}
								}
							}
							//active=false;
							sleep(ping_interval);
					//}
				//}
				//else
				//	sleep(600000);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	//public void sendPing() {
	//}
	/*public void checkPong() {
		for (int i=0;i<parent.SOCKETS;i++) {
			if (parent.socketbase[i] != null) {
				try {
					if (!parent.socketbase[i].gotPong)
						//parent.socketbase[i].quit("Timeout",parent.socketbase[i]);
						parent.socketbase[i].stopIt(parent.socketbase[i]);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//testno=0;
	}*/
}