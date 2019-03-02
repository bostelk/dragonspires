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

public class Trade {
	DSpiresSocket initiator,recipient;
	int tradetype,itemgold;

	public Trade () {}
	public boolean initTrade (int type, int ig, DSpiresSocket i) {
		//DSpiresSocket r=i.findSocketAtFacing(i);
		int tx = i.parent.nextx(i.cx-32,i.cy-32,i.facing);
		int ty = i.parent.nexty(i.cy-32,i.facing);
		DSpiresSocket r = i.findSocketAtPosInSight(tx,ty,1);

		if (r==null) {
			i.pSend("(* There's no one here to trade with.");
			return false;
		}
		else if (i.trade!=null) {
			i.pSend("(* You're already involved with a trade.");
			return false;
		}
		else if (r.trade!=null) {
			i.pSend("(* "+r.name+" is already involved with a trade.");
			return false;
		}
		else if (r.inhand!=0) {
			i.pSend("(* "+r.name+"'s hands are full.");
			return false;
		}
		else if (i.inhand==0) {
			i.pSend("(* You're holding nothing to trade.");
			return false;
		}
		else if (i.inhand>=i.map.items) {
			i.pSend("(* Sorry, you can't trade that because things suck.");
			return false;
		}

		itemgold=ig;
		initiator=i;
		recipient=r;
		tradetype=type;

		i.trade=this;
		r.trade=this;

		i.pSend("[&Asking if "+r.name+" wants to give you "+(type==0 ? itemgold+" gold" : i.parent.itemnames[itemgold])+" for "+i.parent.itemnames[i.inhand]+"...");
		r.pSend("[&"+i.name+" offers to trade you "+i.parent.itemnames[i.inhand]+" for "+(type==0 ? itemgold+" gold" : i.parent.itemnames[itemgold])+".");
		r.pSend("[&Do you accept? Y/N");

		//r.checkBeforeMove=true;
		//i.checkBeforeMove=true;
		i.stateCheck|=i.ST_TRADE;

		r.requestDialog(3);

		return true;		
	}
	public static void cancelTrade(int anotherdialog, Trade t) {
		t.initiator.trade=null;
		t.recipient.trade=null;
		t.initiator.stateCheck^=t.initiator.ST_TRADE;
		if (anotherdialog==0) {
			t.recipient.requestDialog(0);
		}
		t.initiator.pSend("(The trade with "+t.recipient.name+" was cancelled.");
		t.recipient.pSend("(The trade with "+t.initiator.name+" was cancelled.");
	}
	public static void trade(Trade t) {
		if (t.tradetype==0&&t.recipient.gold<t.itemgold) {
			t.initiator.pSend("(* "+t.recipient.name+" doesn't have that much gold!");
			t.recipient.pSend("(* You don't have that much gold!");
		}
		else {
			t.initiator.pSend("[&"+t.recipient.name+" gives you "+(t.tradetype==0 ? t.itemgold+" gold" : t.recipient.parent.itemnames[t.itemgold])+" for "+t.recipient.parent.itemnames[t.initiator.inhand]+".");
			t.recipient.pSend("[&"+t.initiator.name+" gives you "+t.recipient.parent.itemnames[t.initiator.inhand]+" for "+(t.tradetype==0 ? t.itemgold+" gold" : t.recipient.parent.itemnames[t.itemgold])+".");
			t.recipient.setHands(t.initiator.inhand);
			t.initiator.setHands(0);
			t.initiator.updateGold(t.itemgold);
			t.recipient.updateGold(-t.itemgold);
		}
		t.initiator.trade=null;
		t.initiator.stateCheck^=t.initiator.ST_TRADE;
		t.recipient.trade=null;
		t.recipient.requestDialog(0);
	}
}