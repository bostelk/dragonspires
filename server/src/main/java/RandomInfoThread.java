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

import java.util.*;
import java.io.*;

public class RandomInfoThread extends Thread {

	DSpiresServer parent;
	String[] randomInfos = new String[0];

	public RandomInfoThread(DSpiresServer p) {
		parent=p;
		loadRandomInfos();
		this.setPriority(1);
	}
	public void run() {
		if (randomInfos.length==0)
			return;
		while (true) {
			try {
				sleep(300100);
				parent.channelBroadcast("* "+randomInfos[(int)Math.round(Math.random()*(randomInfos.length-1))],parent.INFO_CHANNEL);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void loadRandomInfos() {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader("../conf/info.random"));
			String line;
			Vector collector=new Vector();

			while ((line=in.readLine())!=null) {
				if (line.length()>0) {
					collector.addElement(line);
				}
			}
			in.close();

			randomInfos=new String[collector.size()];
			for (int i=0;i<collector.size();i++)
				randomInfos[i]=collector.elementAt(i).toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}