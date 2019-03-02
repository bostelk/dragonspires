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

public class PortraitEditorPanel extends Canvas {

	DragonSpiresPanel parent;
	Image portrait,gui;

	public PortraitEditorPanel(DragonSpiresPanel p) {
		parent=p;
		gui=parent.dsGetImage("pgui.gif");
		portrait=parent.assemblePortrait(parent.myparray);

	}
	public void paint(Graphics g) {
		g.drawImage(gui,0,0,this);
		g.setColor(parent.dsbg2);
		g.fillRect(4,18,98,10);
		g.fillRect(4,28,42,60);
		g.drawImage(portrait,6,18,this);
	}
	public boolean handleEvent(Event e) {
		//System.out.println(e.id);
		switch (e.id) {
			case Event.MOUSE_UP:
				if (e.x >= 53 && e.y >= 32 && e.x <= 102 && e.y <= 46) {
					if (++parent.myparray[4]-32 > parent.HAIR)
						parent.myparray[4] = ' ';
					portrait=parent.assemblePortrait(parent.myparray);
					repaint();
				}
				else if (e.x >= 53 && e.y >= 47 && e.x <= 102 && e.y <= 61) {
					if (++parent.myparray[5]-32 > parent.ARMOR)
						parent.myparray[5] = ' ';
					portrait=parent.assemblePortrait(parent.myparray);
					repaint();
				}
				else if (e.x >= 53 && e.y >= 62 && e.x <= 102 && e.y <= 76) {
					if (++parent.myparray[6]-32 > parent.ACC*2)
						parent.myparray[6] = ' ';
					portrait=parent.assemblePortrait(parent.myparray);
					repaint();
				}
				else if (e.x >= 53 && e.y >= 77 && e.x <= 102 && e.y <= 91)
					parent.closePep();
	
				//parent.requestFocus();
			case Event.MOUSE_DOWN:
				parent.mousemoveclick=true;
		}
		return true;
	}
}