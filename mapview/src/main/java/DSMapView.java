import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class DSMapView extends Canvas {
	int FLOORS=90;

	Frame f;
	int[][] tilemap,itemmap;
	Image mapImage,lilMap;
	Image[] floors,items;
	int mwidth = 52, mheight = 100;
	byte[] itall=null;
	boolean drawmap=false;

	public static void main(String argv[]) {
		if (argv.length == 1)
			new DSMapView(argv[0]);
		else
			System.out.println("Usage: DSMapViewer <mapfile>");
	}

	public DSMapView(String mapname) {
		f = new Frame();
		f.resize(847,433); // +9, +29

		f.add("Center",this);

		f.show();

		f.setTitle("Starting up...");
		tilemap = new int[mwidth][mheight];
		itemmap = new int[mwidth][mheight];
		floors=new Image[FLOORS];	

		doTracker();

		f.setTitle("Loading map...");
		getMapData(mapname);

		f.setTitle("Assembling map...");
		mapImage = createImage(3351,1616);
		drawMap();

		f.setTitle("Scaling...");

		lilMap = createImage(new FilteredImageSource(mapImage.getSource(), new AreaAveragingScaleFilter(838,404)));
		f.setTitle("Loading image... (this may take a minute)");
	// OR
		//lilMap=createImage(838,404); //  /4
		//Graphics lg = lilMap.getGraphics();
		//lg.drawImage(mapImage,0,0,837,403,0,0,3350,1615,this);

		mapImage.flush();

		drawmap=true;
		repaint();
		f.setTitle("All done. ["+mapname+"]");
	}
	public void paint(Graphics g) {
		if (drawmap)
			g.drawImage(lilMap,0,0,this);
	}
	public void getMapData(String mapname) {
		try {
			//int head,tack;
			//InputStream i = new URL(parent.path+mapname).openConnection().getInputStream();
			InputStream i = new FileInputStream(mapname);
			//System.out.println(theurl.toString());
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					tilemap[x][y]=decode(i.read(),i.read());
					//tilemap[x][y] = i.read();
					//System.out.print(""+tilemap[x][y]);
					//i.skip(1);
				}
			}
			for (int x = 0; x < mwidth; x++) {
				for (int y = 0; y < mheight; y++) {
					itemmap[x][y]=decode(i.read(),i.read());
					//itemmap[x][y] = i.read();
					//i.skip(1);
				}
			}
			i.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int decode(int head, int tack) {
		return (head*95+tack);
	}
	public void doTracker() {

		//Load source images
		String[] ttt = {"floors","items"};
		String[] tttn = {"floor.gif","item256.gif"};
		Image[] ttti = new Image[tttn.length];
		MediaTracker tracker = new MediaTracker(this);
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		for (int i = 0; i < tttn.length; i++) {
			f.setTitle("Loading "+ttt[i]+"...");
			ttti[i] = toolkit.getImage(tttn[i]);
			tracker.addImage(ttti[i],i);
			try {
				tracker.waitForID(i);
				//while(!(tracker.checkID(i))){}
			}
			catch (InterruptedException e) {
			}
		}

		//seperate sprites
		seperateFloors(ttti[0],toolkit);
		seperateItems(ttti[1],toolkit);

		//pre-load floors
		for (int i = 0; i < floors.length; i++) {
			tracker.addImage(floors[i],i+tttn.length);
		}
		try {
			tracker.waitForAll();
			while(!(tracker.checkAll())){}
		}
		catch (InterruptedException e) {
		}

		//pre-load items
		for (int i = 0; i < items.length; i++) {
			tracker.addImage(items[i],i+tttn.length);
		}
		try {
			tracker.waitForAll();
			while(!(tracker.checkAll())){}
		}
		catch (InterruptedException e) {
		}
	}
	public void seperateFloors(Image src, Toolkit t) {
		ImageProducer ip = src.getSource();
		int[] dj;
		for (int c=0;c<FLOORS;c++) {
			dj=getFloorCoords(c);
			floors[c]=t.createImage(new FilteredImageSource(ip, new CropImageFilter(dj[0],dj[1],62,32)));
		}
	}
	public int[] getFloorCoords(int floor) {
		int[] retval = new int[2];
		int column = 0;
		int row = 0;
		for (int r = 1; r <= 10; r++) {
			for (int c = 1; c <= 9; c++) {
				if (floor == ((r - 1)*9)+(c-1)) {
					column = c;
					row = r;
					break;
				}
			}
		}
		retval[0] = column+((column-1)*62);
		retval[1] = row+((row-1)*32);
		return retval;
	}
	public void seperateItems(Image src, Toolkit t) {
		ItemImgSep iis = new ItemImgSep();
		itall=iis.itall;
		items=new Image[iis.items];
		ImageProducer ip = src.getSource();
		for (int i=0,c=0;i<iis.itemcoords.length;i+=4,c++)
			items[c]=t.createImage(new FilteredImageSource(ip, new CropImageFilter(iis.itemcoords[i],
																					iis.itemcoords[i+1],
																					iis.itemcoords[i+2],
																					iis.itemcoords[i+3])));
	}
	public void drawMap() {
		Graphics g = mapImage.getGraphics();
				int currx = 0;
				int curry = 0;
				int currline = 1;
				int currt = 32;
			try {
				for (int y = 0; y < mheight; y++) {
					for (int x = 0; x < mwidth; x++) {
						g.drawImage(floors[tilemap[x][y]],currx+currt,curry,this);
						currx+=64;
					}
					curry+=16;
					if (currline == 1) {
						currt = 0;
						currline = 0;
					}
					else {
						currt = 32;
						currline = 1;
					}
					currx = 0;
				}
			}
			catch (Exception e) {
				//System.out.println("Floors");
				e.printStackTrace();
			}
				currx = 0;
				curry = 0;
				currline = 1;
				currt = 32;
			try {
				for(int y = 0; y < mheight; y++) {
					for (int x = 0; x < mwidth; x++) {
						if (itemmap[x][y] != 0) {
							if (itemmap[x][y] < items.length)
								g.drawImage(items[itemmap[x][y]-1],currx+currt,curry-itall[itemmap[x][y]],this);
						}
						currx+=64;
					}

					curry+=16;
					if (currline == 1) {
						currt = 0;
						currline = 0;
					}
					else {
						currt = 32;
						currline = 1;
					}
					currx = 0;
				}
			}
			catch (Exception e) {
				//System.out.println("Items");
				e.printStackTrace();
			}
	}
}