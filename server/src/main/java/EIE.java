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

class EIE {
	String name="";
	String colorstring="";
	String attack="";

	int[] drop;
	int gold;
	int maxhp;
	int maxtilltrig;

	short spell;

	//byte str;
	//byte def;
	//byte agi;
	//byte dex=1;
	byte shapecat;

	short align;

	Weapon weapon;
	Armor armor;

	boolean projectile=false;
	int projectile_item;
	short projectile_length=1;

	boolean is_item=false;
	int enemy_item=22;


	public EIE() {}
	/*public EIE(String n, String c, String a, int d, int mh, int mt, int s, int st, int de, int ag, int cat, int aln) {

		name=n;
		colorstring=c;
		attack=a;

		drop=d;
		maxhp=mh;
		maxtilltrig=mt;

		spell=(short)s;

		str=(byte)st;
		def=(byte)de;
		agi=(byte)ag;
		dex=1;
		shapecat=(byte)cat;

		align=(short)aln;
	}*/
}