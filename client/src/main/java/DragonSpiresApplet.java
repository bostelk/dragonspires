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

import java.applet.*;
import java.awt.Frame;
import java.awt.Checkbox;
import java.awt.TextArea;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Font;

public class DragonSpiresApplet extends Applet {

	DragonSpiresFrame f;
	DSpiresSound sound;
	//Button btnSound,btnNew;
	//Checkbox fs;
	//Font sf;
	DragonSpiresConsole console;

	public void init() {
		setLayout(null);
		console = new DragonSpiresConsole(this,12);
		sound = new DSpiresSound(this);
		console.resize(199,130);
		console.move(0,0);
		add(console);
		
/*		setLayout(null);

		//f = new DragonSpiresFrame(this,false);

		sf=new Font("TimesRoman", Font.BOLD, 12);
		sound = new DSpiresSound(this);

		setBackground(new Color(49,49,82));

		btnSound = new Button("Sounds");
		btnSound.move(21,22);
		btnSound.resize(75,20);
		btnSound.setBackground(new Color(49,49,82));
		btnSound.setForeground(Color.yellow);
		btnSound.setFont(sf);
		add(btnSound);

		btnNew = new Button("New Game");
		btnNew.move(101,22);
		btnNew.resize(75,20);
		btnNew.setBackground(new Color(49,49,82));
		btnNew.setForeground(Color.yellow);
		btnNew.setFont(sf);
		add(btnNew);

		fs=new Checkbox("Fullscreen Mode");
		fs.move(50,43);
		fs.resize(125,20);
		fs.setBackground(new Color(49,49,82));
		fs.setForeground(Color.yellow);
		fs.setFont(sf);
		add(fs);

		//f.p.start();
		*/
	}
	/*public void paint(Graphics g) {
		g.setColor(Color.white);
		g.drawRect(0,0,199,104);
		g.drawLine(0,17,199,17);
		g.setFont(sf);
		g.setColor(Color.yellow);
		g.drawString("DragonSpires Applet Console",22,12);
		g.setColor(Color.red);
		g.drawString("*Warning*",67,75);
		g.drawString("Do not close this window until",20,87);
		g.drawString("you're ready to quit the game.",22,99);
	}*/
	public void stop() {
		System.exit(0);
	}
 	/*public boolean action (Event e, Object arg) {
		if (e.target == btnSound) {
			//p.requestFocus();
			//p.tryfocus = false;
			sound.show();
			return true;
		}
		else if (e.target == btnNew) {
			new DragonSpiresFrame(this,fs.getState()).p.start();
			return true;
		}
		//return super.handleEvent (e);
		return true;
	}*/
	public void showSound() {
		sound.show();
	}
}
class DSpiresSound extends Frame {

	AudioClip[] aus;
	AudioClip bgm;
	Checkbox ps,musicb;
	TextArea warn;

	public DSpiresSound(DragonSpiresApplet parent) {
		super("DS Sound Panel");
		setBackground(new Color(179,207,235));
		aus = new AudioClip[7];
		aus[0] = parent.getAudioClip(parent.getCodeBase(),"pop.au");
		aus[1] = parent.getAudioClip(parent.getCodeBase(),"bouncy.au");
		aus[2] = parent.getAudioClip(parent.getCodeBase(),"arnold2.au");
		aus[3] = parent.getAudioClip(parent.getCodeBase(),"swish2.au");
		aus[4] = parent.getAudioClip(parent.getCodeBase(),"clank.au");
		aus[5] = parent.getAudioClip(parent.getCodeBase(),"terrier.au");
		aus[6] = parent.getAudioClip(parent.getCodeBase(),"entity.au");
		bgm = parent.getAudioClip(parent.getCodeBase(),"chess.au");
		ps = new Checkbox("Play Sounds");
		musicb = new Checkbox("Play Music");
		warn = new TextArea();
		warn.setFont(parent.console.sf);
		warn.appendText("WARNING: Read before enabling sounds or music:\n"+
				"DragonSpires will most likely freeze if\n"+
				"it conflicts with other sound-playing programs\n"+
				"and you're using an old sound card.\n"+
				"If you are not using any other sound-playing\n"+
				"programs or don't have an old sound card\n"+
				"everything should be fine.\n"+
				"Note: The music may take a moment to load.");
		add("North",ps);
		add("Center",musicb);
		add("South",warn);
		resize(300,275);
		//show();
	}
	public void play(int n) {
		aus[n].play();
	}
 	public boolean handleEvent (Event e) {
		if (e.id == Event.WINDOW_DESTROY) {
			hide();
		}
		else if (e.target==musicb && e.id==Event.ACTION_EVENT) {
			if (musicb.getState())
				bgm.loop();
			else
				bgm.stop();
		}
	   	return super.handleEvent (e);
	}
}