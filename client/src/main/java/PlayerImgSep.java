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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;

public class PlayerImgSep {

	int shapes[] = {
1,1,44,55,
46,1,41,56,
88,1,43,56,
132,1,43,54,
176,1,43,54,
220,1,43,55,
264,1,45,56,
310,1,49,56,
360,1,62,54,
423,1,58,54,
1,58,42,54,
44,58,40,54,
85,58,41,54,
127,58,43,53,
171,58,43,52,
215,58,47,54,
263,58,42,54,
306,58,44,54,
351,58,52,53,
404,58,62,52,
1,113,47,54,
49,113,47,55,
97,113,44,54,
142,113,41,54,
184,113,43,54,
228,113,44,54,
273,113,49,54,
323,113,43,54,
367,113,45,54,
413,113,49,54,
1,169,62,54,
64,169,64,54,
129,169,41,54,
171,169,40,55,
212,169,41,55,
254,169,43,53,
298,169,44,52,
343,169,47,54,
391,169,43,55,
435,169,46,55,
1,225,52,53,
54,225,64,52,
119,225,47,60,
167,225,43,59,
211,225,43,55,
255,225,41,56,
297,225,43,56,
341,225,42,54,
384,225,42,54,
427,225,43,55,
1,286,45,56,
47,286,49,56,
97,286,53,54,
151,286,48,54,
200,286,41,54,
242,286,40,55,
283,286,41,55,
325,286,43,53,
369,286,43,52,
413,286,47,54,
1,343,42,55,
44,343,44,55,
89,343,52,53,
142,343,56,52,
199,343,47,55,
247,343,47,55,
295,343,43,55,
339,343,41,56,
381,343,40,56,
422,343,42,54,
1,400,44,54,
46,400,41,55,
88,400,42,56,
131,400,47,56,
179,400,62,54,
242,400,58,54,
301,400,36,53,
338,400,39,53,
378,400,40,51,
419,400,40,52,
1,457,40,52,
42,457,43,53,
86,457,38,53,
125,457,36,51,
162,457,51,52,
214,457,53,52,
268,457,37,52,
306,457,39,54,
346,457,44,54,
391,457,41,54,
433,457,43,55,
1,513,43,54,
45,513,43,54,
89,513,44,54,
134,513,40,54,
175,513,42,55,
218,513,51,54,
270,513,51,54,
322,513,44,53,
367,513,38,54,
406,513,44,54,
1,569,43,54,
45,569,43,54,
89,569,43,53,
133,569,41,55,
175,569,44,54,
220,569,48,54,
269,569,51,54,
321,569,47,55,
369,569,43,55
};
	ImageProducer p;
	Toolkit t;

	public PlayerImgSep(Image i, Toolkit tt) {
		p = i.getSource();
		t = tt;
	}
	public ImageProducer[] playerSeperate() {
		ImageProducer[] retval = new ImageProducer[110];

		int which = 0;
		for (int i = 0; i < 110; i++) {
			retval[i] = doCrop(shapes[which++],shapes[which++],shapes[which++],shapes[which++]).getSource();
			//which+=4;
		}

		return retval;
	}
	public Image doCrop(int x, int y, int w, int h) {
		return t.createImage(new FilteredImageSource(p, new CropImageFilter(x,y,w,h)));
	}
}