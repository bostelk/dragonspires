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

import java.awt.Frame;
import java.awt.Window;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.Dimension;

public class DragonSpiresFrame extends Frame {

	DragonSpiresPanel p;
	Window w;
	Frame f;
	boolean fullscreen=true;
	boolean onetoclose=false;
	static int smallfont=12,normalfont=14;
	static int windowcount=0;

	public static void main(String args[]) {
		/*if (args.length==1) {
			System.out.println(args[0]);
			if (args[0].equals("-fs")) {
				new DragonSpiresFrame(0);
				return;
			}	
		}*/
		if (args.length>0) {
			if (args[0].equals("-smallfonts")) {
				smallfont=10;
				normalfont=12;
			}
		}
		new DragonSpiresFrame();
	}
	public DragonSpiresFrame() {
		super("DS Console");
		System.out.println("Starting DragonSpires...");
		setIconImage(Toolkit.getDefaultToolkit().getImage("dragon.gif"));
		//resize(208,132);
		DragonSpiresConsole aconsole = new DragonSpiresConsole(this,smallfont);
		add(aconsole,"Center");
		show();
		aconsole.resize(199,130);
		pack();
		onetoclose=true;
		System.out.println("Done.");
	}
	public DragonSpiresFrame(int server, int mode) {
		
		super(" DragonSpires ("+windowcount+")");
		windowcount++;

		p = new DragonSpiresPanel(this, "file:/"+System.getProperty("user.dir")+System.getProperty("file.separator"), false, server, null);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		int tx = d.width/2-320;
		int ty = d.height/2-240;

		if (tx<0)
			tx=0;
		if (ty<0)
			ty=0;

		switch (mode) {
			case 0:
				w = new Window(this);
				w.add("Center", p);
				w.resize(640,480);

				w.move(tx,ty);
				move(tx,ty);

				w.show();
				p.start();
				show();
				
				break;
			case 1:
				add("Center",p);
				//resize(586,441);
				move(tx,ty);
				show();
				fullscreen=false;
				p.resize(640,480);
				pack();
				p.start();
		}
		setIconImage(p.dsGetImage("dragon.gif"));
	}
	public DragonSpiresFrame(DragonSpiresApplet a, boolean fs) {
		super(" DragonSpires ("+windowcount+")");
		windowcount++;
		p = new DragonSpiresPanel(this, a.getCodeBase().toString(), true, 0,a);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		int tx = d.width/2-320;
		int ty = d.height/2-255;

		if (tx<0)
			tx=0;
		if (ty<0)
			ty=0;

		move(tx,ty);
			
		fullscreen=fs;

		if (fullscreen) {
			w = new Window(this);
			w.add("Center", p);
			w.move(tx,ty);
			w.resize(640,480);
			w.show();
			show();
		}
		else {
			add("Center",p);
			move(tx,ty);
			show();
			p.resize(640,480);
			pack();
		}
		setIconImage(p.dsGetImage("dragon.gif"));
	}
 	public boolean handleEvent (Event e) {
		try {
		if (e.id == Event.WINDOW_DESTROY) {
			haltIt();
			return true;
		}
		else if (e.id==Event.GOT_FOCUS) {
			p.requestFocus();
			return true;
		}
		}
		catch (Exception ex) {}
		//return true;
   		return super.handleEvent (e);
	}
	public void haltIt() {
		try { p.stop(); } catch (Exception ex) {}
		dispose();
		if (onetoclose) {
			System.out.println("\nCome back to DragonSpires soon!");
			System.exit(0);
		}
		else if (!p.amapplet&&onetoclose) {
			System.out.println("\nCome back to DragonSpires soon!");
			System.exit(0);
		}
	}
	/*public void switchWindow() {
		if (fullscreen) {
			w.hide();
			p.hide();
			add("Center",p);
			p.show();
			pack();
			fullscreen=false;
			//p.requestFocus();
			p.map.validate();
		}
		else {
			w.resize(640,480);
			resize(1,1);
			w.move(p.fx,p.fy);
			p.hide();
			w.add("Center",p);
			p.show();
			w.show();
			fullscreen=true;
			//p.requestFocus();
			p.map.validate();
		}
	}*/
}