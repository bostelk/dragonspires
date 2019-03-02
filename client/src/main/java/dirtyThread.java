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

public class dirtyThread extends Thread {
	DSMapCanvas map;
	int x,y;//,xpos,ypos;
	public dirtyThread(/*int xpos,int ypos,*/int x,int y,DSMapCanvas m) {
		map=m;
		this.x=x;
		this.y=y;
		//this.xpos=xpos;
		//this.ypos=ypos;
		start();
	}
	public void run() {
		try {
		if (map.omdisplaymode!=0) {//||(++map.dirtycount)==2) {
			//map.dirtycount=0;
			map.repaint();
			return;
		}

		if (map.drawok==1) {
			int xx=160-((map.xpos-x)*64);
			if (map.ypos%2!=y%2) {
				if (map.oe==0)
					xx-=32;
				else
					xx+=32;
			}
			int yy=48-((map.ypos-y)*16);
			
			Image i=getDirtyImage();
			//if (map.xpos==xpos && map.ypos==ypos) {
				map.g.drawImage(i,xx,yy,map);

				if (map.drawcompass&&x>=271&&y<=49)
					map.g.drawImage(map.parent.compass,333,1,map);
			//}
		}
		}
		catch (Exception e) {}
	}
	public Image getDirtyImage() {//int x, int y) {
		Image i = map.createImage(62,96);
		Graphics g = i.getGraphics();
		int dxs,dxe,dys=y-5,dye=y+2,currx,curry=-16;
		boolean indentline=false;

		if (y%2==0) {
			dxs=x;
			dxe=x+2;

			// drawTileEven2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);

				for (x = dxs; x < dxe; x++) {
					map.drawTileSimple2(x,y,currx,curry,g);
					currx+=64;
				}

				if (y%2!=0)
					map.drawTileSimple2(x,y,currx,curry,g);

				curry+=16;
				indentline = !indentline;
			}

			dye+=4;
			indentline=false;
			curry=-16;

			// drawIPEven2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);
				for (x = dxs; x < dxe; x++) {
					map.drawIPSimple2(x,y,currx,curry,g);
					currx+=64;
				}

				if (y%2!=0)
					map.drawIPSimple2(x,y,currx,curry,g);
					
				curry+=16;
				indentline = !indentline;
			}
		}
		else {
			dxs=x-1;
			dxe=x+1;

			// drawTileOdd2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);
				x=dxs;
				if (y%2!=0)
					x++;
				for (;x <= dxe; x++) {
					map.drawTileSimple2(x,y,currx,curry,g);
					currx+=64;
				}
				curry+=16;
				indentline = !indentline;
			}

			dye+=4;
			indentline=false;
			curry=-16;

			// drawIPOdd2
			for (y = dys; y <= dye; y++) {
				currx = ((indentline) ? 1 : -31);
				x=dxs;
				if (y%2!=0)
					x++;

				for (; x <= dxe; x++) {
					map.drawIPSimple2(x,y,currx,curry,g);
					currx+=64;
				}
				curry+=16;
				indentline = !indentline;
			}
		}

		//g.drawRect(0,0,61,95);

		return i;
	}
}