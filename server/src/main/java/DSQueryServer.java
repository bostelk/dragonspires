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

public class DSQueryServer extends Thread {
	DSpiresServer parent;

	public DSQueryServer(DSpiresServer p) {
		parent=p;
		setPriority(Thread.MIN_PRIORITY);
		start();
	}

	public void run() {
		ServerSocket server=null;
		Socket client;
		PrintWriter o;

		for (int count=0;count<100;count++) {
			try {
				server=new ServerSocket(7736,0);
				while (true) {
					client = server.accept();
					o = new PrintWriter(new BufferedOutputStream(client.getOutputStream()));

					o.println("Name: "+parent.servername);
					o.println("Uptime: "+parent.uptime+" hours.");
					o.println("");
					o.println("Connections: "+parent.currTotal+"/"+parent.SOCKETS);
					o.println("Max conns per ip: "+parent.maxconn);
					o.println("");
					o.println("Character creation: "+parent.creation);
					o.println("Character saving: "+parent.saving);

					o.println("");
					o.flush();
					o.close();
					client.close();
				}
			}
			catch (Exception e) {
				System.err.println("ERROR: Query died: "+count);
			}

			try {
				server.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.err.println("ERROR: Shutting down query server.");
	}
}
