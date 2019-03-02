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

public class Group {
	
	static final int MAX_GROUP_SIZE=4;
	DSpiresSocket leader;
	DSpiresSocket[] members;
	char positions[];
	int shapes[];
	boolean moved[];

	public Group(DSpiresSocket l) {
		leader=l;
		leader.groupstat=2;
		members=new DSpiresSocket[MAX_GROUP_SIZE];
		positions=new char[MAX_GROUP_SIZE*2];
		moved = new boolean[MAX_GROUP_SIZE];
		shapes = new int[MAX_GROUP_SIZE];
		reset();
	}
	public void reset() {
		for (int i = 0; i < positions.length; i++) {
			positions[i]=(char)1;
			if (i<MAX_GROUP_SIZE)
				moved[i]=false;
 		}
	}
	public void addMember(DSpiresSocket s) {
		int n=-1;
		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]==null) {
				n=i;
				break;
			}
		}

		if (n!=-1) {
			//s.pSend("@"+leader.cx+""+leader.cy);
			//s.map.playerPlaceBroadcast(0,"",s.cx,s.cy,s,s.map);
			//s.setPosition(leader.x,leader.y);
			groupSend("("+s.name+" has joined the group.");
			members[n]=s;
			moved[n]=false;
			s.groupstat=1;
			s.stateCheck|=s.ST_GROUP_MEMBER;
			s.Group=this;
			s.pSend("(You've joined "+leader.name+"'s group.");
		}
		else
			s.pSend("(The group is full.");
	}
	public void removeMember(DSpiresSocket s) {
		int count=0;
		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]==s) {
				members[i]=null;
				groupSend("("+s.name+" has left the group.");
				s.groupstat=0;
				s.Group=null;
				s.stateCheck^=s.ST_GROUP_MEMBER;
				s.pSend("(You have left "+leader.name+"'s group.");
				break;
			}
		}
		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]==null)
				count++;
		}
		if (count==MAX_GROUP_SIZE) {
			try {
				leader.Group=null;
				leader.groupstat=0;
				leader=null;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void groupSend(String message) {
		try {
			leader.pSend(message);
		} catch (Exception e) {}
		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]!=null) {
				try {
					members[i].pSend(message);
				} catch (Exception e) {}
			}
		}
	}
	public String groupList() {
		String retval = ""+leader.name;

		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]!=null)
				retval+=", "+members[i].name;
		}

		return retval+".";
	}
	public void moveGroup(char bx, char by, int shape) {
		System.arraycopy(positions,0,positions,2,positions.length-2);
		System.arraycopy(shapes,0,shapes,1,MAX_GROUP_SIZE-1);
		positions[0]=bx;
		positions[1]=by;
		shapes[0]=shape;
		DSpiresSocket member;

		int poscount=0,thefacing=leader.facing,thex,they;

		groupSend("~");

		leader.pSend("@"+leader.cx+""+leader.cy);
		leader.map.playerMoveBroadcast(leader.visishape,leader.colorstring,leader.cx,leader.cy,bx,by,leader,leader.map);

		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]!=null) {
				if (positions[poscount*2]!=(char)1) {
					member=members[i];
					if (member.stateCheck!=4)
						member.doStateChecker("mg1");
					bx=member.cx;
					by=member.cy;

					if (moved[i]) {
						member.facing=thefacing;
						thex=positions[poscount*2]-32;
						they=positions[poscount*2+1]-32;
					}
					else {
						//member.map.playerPlaceBroadcast(shapes[poscount],member.colorstring,member.cx,member.cy,member,leader.map);
						DSpiresSocket tocompare=leader;
						if (i!=0) {
							int n;
							for (n=i-1;n>=0;n--) {
								if (members[n]!=null)
									break;
							}
							if (n>=0)
								tocompare=members[n];
						}

						thex=leader.parent.nextx(tocompare.x,tocompare.y,leader.parent.faceopp[tocompare.facing]);
						they=leader.parent.nexty(tocompare.y,leader.parent.faceopp[tocompare.facing]);

						if (!leader.map.canWalk(thex,they,leader.map)) {
							poscount++;
							continue;
						}

						/*int fdir=0;
						for (int i=0;i<4;i++) {
							fdir=parent.shortfaceconv[i];
							int x=parent.nextx(member.x, member.y, fdir);
							if (x==fx) {
								int y=parent.nexty(member.y, fdir);
								if (y==fy)
									break;
							}
						}*/

						moved[i]=true;
					}

					member.setPosition(thex,they);
					member.pSend("@"+member.cx+""+member.cy);

					member.map.playerMoveBroadcast(shapes[poscount],member.colorstring,member.cx,member.cy,bx,by,member,leader.map);

					if (member.parent.itemdefs[member.map.itemmap[thex][they]][4]==1)
						member.stepOnItem(thex,they,member.map.itemmap[thex][they],0);

					if (member.map.itemmap[thex][they] != member.pfooti)
						member.setFeet(member.map.itemmap[thex][they]);

					if (member.map.walktrig)
						member.map.walkTrigger(member);

					poscount++;
				}
				else
					break;
			}
		}
		groupSend("=");
	}
	public void disbandGroup() {
		groupSend("("+leader.name+" disbands the group.");

		leader.Group=null;
		leader.groupstat=0;
		leader=null;

		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]!=null) {
				members[i].Group=null;
				members[i].groupstat=0;
				members[i].stateCheck^=members[i].ST_GROUP_MEMBER;
				members[i]=null;
			}
		}
	}
	public void groupChangeMap(DSMapServer map) {
		reset();
		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]!=null) {
				leader.map.playerPlaceBroadcast(0,members[i].colorstring,members[i].cx,members[i].cy,members[i],leader.map);
				members[i].setPosition(leader.cx-32,leader.cy-32);
				members[i].facing=leader.facing;

				members[i].changeMap(map,1,0,0);
			}
		}
	}
	public void kickMember(String name) {
		name=name.toLowerCase();
		for (int i=0;i<MAX_GROUP_SIZE;i++) {
			if (members[i]!=null) {
				if (members[i].name.toLowerCase().equals(name)) {
					removeMember(members[i]);
					break;
				}
			}
		}		
	}
	public void transpose() {
		int[][] coords = new int[MAX_GROUP_SIZE+1][2];
		int i,m;
		DSpiresSocket everyone[] = new DSpiresSocket[MAX_GROUP_SIZE+1],it;
		DSpiresServer p = leader.parent;
		DSMapServer themap = leader.map;

		groupSend("[!"+leader.name+" transposes the group.");

		everyone[0]=leader;
		for (i=1, m=0;m<MAX_GROUP_SIZE;m++) {
			if (members[m]!=null) {
				everyone[i++]=members[m];
			}
		}

		for (i=0;i<MAX_GROUP_SIZE+1;i++) {
			coords[i][0]=-1;
		}

		coords[0][0]=leader.x;
		coords[0][1]=leader.y;
		for (i=1, m=0;m<MAX_GROUP_SIZE;m++) {
			if (members[m]!=null) {
				coords[i][0]=members[m].x;
				coords[i++][1]=members[m].y;
			}
		}

		themap.limitedBroadcast("~",coords[0][0],coords[0][1],themap);

		for (m=i-1,i=0;i<m && m>-1 && i<MAX_GROUP_SIZE+1;m--,i++) {
			it=everyone[m];
			if (m==i) {
				it.facing=p.faceopp[it.facing];
				it.doStateChecker("mg1");
				themap.playerPlaceBroadcast(p.longShapeStart[1][it.facing-1],it.colorstring,it.cx,it.cy,it,themap);
				break;
			}

			it.facing=p.faceopp[it.facing];
			it.doStateChecker("mg1");
			it.setPosition(coords[i][0],coords[i][1]);
			themap.playerPlaceBroadcast(p.longShapeStart[1][it.facing-1],it.colorstring,it.cx,it.cy,it,themap);
			
			it=everyone[i];

			it.facing=p.faceopp[it.facing];
			it.doStateChecker("mg1");
			it.setPosition(coords[m][0],coords[m][1]);
			themap.playerPlaceBroadcast(p.longShapeStart[1][it.facing-1],it.colorstring,it.cx,it.cy,it,themap);
		}

		themap.limitedBroadcast("=",coords[0][0],coords[0][1],themap);
	}
}