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
import java.net.*;
import java.io.*;

public class DragonSpiresConsole extends Panel {

	Object parent;
	Button btnSound,btnNew,btnQuery;
	Checkbox fs;
	Font sf;
	boolean applet=false;
	Choice server;
	Color ff=new Color(179,207,235);
	private final static char habl[][] = {
	    {'d','s','-','w','e','s','t','.','b','o','s','t','e','l','k','.','c','a'},
	    {'1','2','7','.','0','.','0','.','1'}
    };

	public DragonSpiresConsole(Object p, int smallfont) {
		parent=p;
		if (parent instanceof DragonSpiresApplet)
			applet=true;
		init(smallfont);
	}
	public void init(int smallfont) {
		setLayout(null);

		sf=new Font("TimesRoman", Font.BOLD, smallfont);

		setBackground(new Color(49,49,82));

		btnNew = new Button("New Game");
		btnNew.move(101,22);
		btnNew.resize(75,20);
		btnNew.setBackground(new Color(49,49,82));
		btnNew.setForeground(Color.yellow);
		btnNew.setFont(sf);
		add(btnNew);

		btnQuery = new Button("Query Server");
		btnQuery.move(21,101);
		btnQuery.resize(157,20);
		btnQuery.setBackground(new Color(49,49,82));
		btnQuery.setForeground(Color.yellow);
		btnQuery.setFont(sf);
		add(btnQuery);

		if (applet) {
			btnSound = new Button("Sounds");
			btnSound.move(21,22);
			btnSound.resize(75,20);
			btnSound.setBackground(new Color(49,49,82));
			btnSound.setForeground(Color.yellow);
			btnSound.setFont(sf);
			add(btnSound);
			
			btnQuery.move(21,64);
		}
		else {
			btnNew.move(21,22);
			btnNew.resize(157,20);

			server=new Choice();
			server.addItem("US West (main)");
			server.addItem("Localhost (test)");
			server.resize(100,20);
			server.move(75,70);
			server.setFont(sf);
			server.setBackground(new Color(49,49,82));
			server.setForeground(Color.yellow);
			add(server);
		}

		fs=new Checkbox("Fullscreen Mode");
		fs.move(50,43);
		fs.resize(125,20);
		fs.setBackground(new Color(49,49,82));
		fs.setForeground(Color.yellow);
		fs.setFont(sf);
		add(fs);
	}
	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.drawRect(0,0,198,129);
		g.drawLine(0,17,198,17);
		g.setFont(sf);
		g.setColor(Color.orange);
		g.drawString("DragonSpires Console",39,12);
		g.setColor(Color.red);
		if (applet) {
			g.drawString("*Warning*",67,96);
			g.drawString("Do not close this window until",20,108);
			g.drawString("you're ready to quit the game.",22,120);
		}
		else
			g.drawString("Server:",25,86);

	}
	public boolean action (Event e, Object arg) {
		if (e.target == btnSound && applet) {
			//p.requestFocus();
			//p.tryfocus = false;
			((DragonSpiresApplet)parent).showSound();
			return true;
		}
		else if (e.target == btnNew) {
			if (applet) {
				new DragonSpiresFrame((DragonSpiresApplet)parent,fs.getState()).p.start();
			}
			else {
				int i=1;
				if (fs.getState())
					i=0;
				new DragonSpiresFrame(server.getSelectedIndex(),i);
			}
			return true;
		}
		else if (e.target == btnQuery) {
			if (applet)
				new DSQueryFrame(new String(habl[0]));
			else
				new DSQueryFrame(new String(habl[server.getSelectedIndex()]));
		}
		//return parent.handleEvent (e);
		return true;
	}
}
class DSQueryFrame extends Frame {
	Button closeit;
	TextArea message;
	static int count=0;

	public DSQueryFrame(String server) {
		super("DS Server Query ("+count+")");
		closeit = new Button("Close");

		message = new TextArea("Querying server... '"+server+"'\n\n");

		add("South",closeit);
		add("Center",message);
		resize(215,221);
		show();

		try {
			Socket s;
			DataInputStream i;
			String line;
	
			s = new Socket(server,7736);
			i = new DataInputStream(new BufferedInputStream(s.getInputStream()));

			while ((line=i.readLine())!=null) {
				message.append(line+"\n");
			}

			i.close();
			s.close();
		}
		catch (Exception e) {
			message.append("Couldn't connect to the server!");
		}
	}
	public boolean action(Event e, Object arg) {
		if (e.target == closeit)
			this.dispose();
		return true;
	}
}
