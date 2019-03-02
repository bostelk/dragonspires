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

import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.awt.image.ImageFilter;
import java.awt.image.IndexColorModel;

public class KnightFilter2 extends ImageFilter {
	protected ColorModel origmodel;
	protected ColorModel newmodel;
	DSMapCanvas parent;
	char[] colorstring;
	int cipos;
	int index;

	public KnightFilter2(char[] cs, DSMapCanvas p) {
		colorstring = cs;
		parent = p;
	}

	public void setColorModel(ColorModel model) {
	    ColorModel newcm = filterIndexColorModel((IndexColorModel)model);
	    substituteColorModel(model, newcm);
	    consumer.setColorModel(newcm);
	}

	public void substituteColorModel(ColorModel oldcm, ColorModel newcm) {
		origmodel = oldcm;
		newmodel = newcm;
	}

	public IndexColorModel filterIndexColorModel(IndexColorModel icm) {
		byte r[] = new byte[16];
		byte g[] = new byte[16];
		byte b[] = new byte[16];
		byte a[] = new byte[16];
		icm.getReds(r);
		icm.getGreens(g);
		icm.getBlues(b);
		icm.getAlphas(a);

		for (int i = 0; i < 16; i++) {
			cipos = -1;
			switch (i) {
				case 9:
					cipos++;
				case 7:
					cipos++;
				case 1:
					cipos+=(colorstring[0]-32)*3+1;
					break;

				case 2:
					cipos++;
				case 10:
					cipos+=(colorstring[1]-32)*3+2;
					break;

				case 13:
					cipos++;
				case 5:
					cipos+=(colorstring[2]-32)*3+1;
					break;
			}
			if (cipos != -1)
				index = parent.colorindexes[cipos];
			else
				index = i;
			r[i] = parent.colorpal[index][0];
			g[i] = parent.colorpal[index][1];
			b[i] = parent.colorpal[index][2];
		}

		return new IndexColorModel(icm.getPixelSize(), 16, r, g, b, 11);
	}	
	public void setPixels(int x, int y, int w, int h, ColorModel model, byte pixels[], int off, int scansize) {
		consumer.setPixels(x, y, w, h, newmodel, pixels, off, scansize);
	}
}
