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

import java.net.*;

public class DSPing extends Thread {
	DragonSpiresPanel parent;
	String[] servers;

	public DSPing(DragonSpiresPanel p, String[] s) {
		parent=p;
		servers=s;
		start();
	}
	public void run() {
		try {
			byte[] message = new byte[32];
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(message,message.length,InetAddress.getByName(servers[parent.server]),7735);
			DatagramPacket reply = new DatagramPacket(message,message.length);
			long sendTime,recTime;
			
			socket.setSoTimeout(10000);
			parent.append("~ Pinging...",2);

			sendTime = System.currentTimeMillis();
			socket.send(packet);
			socket.receive(reply);
			recTime = System.currentTimeMillis();

			parent.append("~ Ping received: "+(recTime-sendTime)+"ms",2);

		}
		catch (Exception e) {
			//e.printStackTrace();
			parent.append("~ Ping timed out. (>=10000ms)",2);
		}
	}
}