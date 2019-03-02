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

/*
This is the fastest filter I made for player sprite color remapping.
Maybe it won't work for you. If you can't figure anything out, try
figuring out how to use KnightFilter2. Be sure to scroll to the bottom
here first, though.
*/

import java.awt.image.*;

public class KnightFilter3 extends ImageFilter {
	protected ColorModel origmodel;
	protected ColorModel newmodel;
	static int[] base;
	static byte[] styles;
	char[] colorstring;
	int index;

	public KnightFilter3(char[] cs, int[] b, byte[] s) {
		colorstring = cs;
		base = b;
		styles=s;
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
		int argb[] = new int[16];
		icm.getRGBs(argb);

		int a=(colorstring[0]-32)*3;
		int b=(colorstring[1]-32)*3+1;
		int c=(colorstring[2]-32)*3;

		argb[1] = base[styles[a]];
		argb[7] = base[styles[++a]];
		argb[9] = base[styles[++a]];

		argb[10] = base[styles[b]];
		argb[2] = base[styles[++b]];

		argb[5] = base[styles[c]];
		argb[13] = base[styles[++c]];
		
		// Here, I used to have it set to 'TYPE_INT'. I can't remember why. I just know that now
		// with Java 1.4 it doesn't work. It prefers 'TYPE_USHORT'.
		return new IndexColorModel(4, 16, argb, 0, true, 11, DataBuffer.TYPE_USHORT/*TYPE_INT*/);
	}
	public void setPixels(int x, int y, int w, int h, ColorModel model, byte pixels[], int off, int scansize) {
		consumer.setPixels(x, y, w, h, newmodel, pixels, off, scansize);
	}
}
