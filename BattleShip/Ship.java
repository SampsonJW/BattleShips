//Sampson John Ward
//ID: 1312744
//Andy Shen
//ID: 1304441

import java.awt.*;

public class Ship {
    int x,y;
    int size;
    int direction;

    public Ship(Positions position, int size, int direction) {
        this(position.x,position.y,size,direction);
    }

    public Ship(int x, int y, int size, int direction) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.direction = direction;
    }
    /*----------------------------------------------------*/
    /*Checks collisions with other ships using a rectangle*/
    /*----------------------------------------------------*/
    public boolean collidesWith(Ship ship) {
        int w,h,shipW,shipH;
        if(direction==0) {
            w=3;
            h=size+2;
        } else {
            h=3;
            w=size+2;
        }
        if(ship.direction==0) {
            shipW=1;
            shipH=ship.size;
        } else {
            shipW=ship.size;
            shipH=1;
        }
        //Makes a rectangle of the size of the ship bounds
        Rectangle bound = new Rectangle(x-1,y-1,w,h);
        //Makes positions of just the ship itself
        Rectangle shipR = new Rectangle(ship.x,ship.y,shipW,shipH);
        return bound.intersects(shipR);
    }
}
