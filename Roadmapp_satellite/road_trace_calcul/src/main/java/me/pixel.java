package me;


import java.util.*;
public class pixel {
int x;
int y;
pixel prev=null;
pixel(int xp,int yp){
	x=xp;y=yp;
}

public ArrayList<pixel> mouvment(int w,int h) {
	pixel p1,p2,p3,p4,p5,p6,p7,p8;
	p1=new pixel(x+1,y);p2=new pixel(x-1,y);p3=new pixel(x,y+1);
	p4=new pixel(x,y-1);p5=new pixel(x+1,y+1);p6=new pixel(x-1,y-1);
	p7=new pixel(x-1,y+1);p8=new pixel(x+1,y-1); 
	ArrayList<pixel> listp=new ArrayList<pixel>();
	
	listp.add(p1);listp.add(p2);listp.add(p3);listp.add(p4);listp.add(p5);listp.add(p6);listp.add(p7);listp.add(p8);

	return listp;
	}


@Override
public boolean equals(Object o) {

if((this.x==((pixel)o).x)&&(this.y==((pixel)o).y)) {
	return true;
}else {
	return false;
}

}


}
