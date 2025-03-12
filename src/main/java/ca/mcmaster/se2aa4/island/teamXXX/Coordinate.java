package ca.mcmaster.se2aa4.island.teamXXX;

public class Coordinate {

    private int x;
    private int y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public void changeX(int x){
        this.x += x;
    }

    public void changeY(int y){
        this.y += y;
    }

    public void changeXY(int x, int y){
        changeX(x);
        changeY(y);
    }
}