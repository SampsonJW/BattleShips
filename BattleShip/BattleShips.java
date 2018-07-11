//Sampson John Ward
//ID: 1312744
//Andy Shen
//ID: 1304441

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import static java.lang.Math.abs;

public class BattleShips {

    static int k;
    static int n;


    static int[] ships;
    static int[] xHits;
    static int[] yHits;

    int score = 0;
    ArrayList<Ship> shipArrayList = new ArrayList<>();
    ArrayList<Ship> sizeArrayList = new ArrayList<>();
    static Random rand = new Random();

    static int temperature = 10000;

    public static void main(String[] args) throws IOException {

        FileReader fr = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(fr);
        String[] shipSizes = br.readLine().split("\\s");
        String[] cols = br.readLine().split("\\s");
        String[] rows = br.readLine().split("\\s");

        n = shipSizes.length;
        k = cols.length;

        ships = new int[shipSizes.length];
        xHits = new int[cols.length];
        yHits = new int[rows.length];

        for (int i = 0; i < shipSizes.length ; i++) {
            ships[i] = Integer.parseInt(shipSizes[i]);
        }
        for (int i = 0; i < rows.length ; i++) {
            xHits[i] = Integer.parseInt(cols[i]);
            yHits[i] = Integer.parseInt(rows[i]);
        }


//        BattleShips bs = annealing((Integer.parseInt(args[1])), temperature);
        BattleShips bs = hillClimbing((Integer.parseInt(args[1])));
        bs.printGrid(bs.getGrid());
        System.out.println("SCORE: "+bs.score);
    }


    /*------------------------------------------------------*/
    /*-----------Fills the map up with valid ships----------*/
    /*------------------------------------------------------*/
    public void populateMap(){

        if(!shipArrayList.isEmpty()){
            shipArrayList.clear();
        }

        int limit=1000;
        boolean[] placed = new boolean[ships.length];
        while (shipArrayList.size()<ships.length) {
            int i = rand.nextInt(ships.length);
            if(placed[i]) {
                continue;
            }
            else{
                placed[i]=generateShip(ships[i]);
            }
            if(--limit==0) {
                limit=1000;
                Arrays.fill(placed,false);
                shipArrayList.clear();
            }
        }
    }
    /*------------------------------------------------------------------------*/
    /*Picks one ship on the map and removed and replaces it at random position*/
    /*------------------------------------------------------------------------*/
    public void randomiseShip(int changes) {
        int getShip;
        int shipSize;
        Ship removed = null;

        do {
            if (removed != null) {
                shipArrayList.add(removed);
            }
            //chooses a random ship to remove
            getShip = rand.nextInt(shipArrayList.size());
            shipSize = shipArrayList.get(getShip).size;
            removed = shipArrayList.remove(getShip);
            if (changes == 0) {
                break;
            }

        } while (!generateShip(shipSize));          //Randomly places the removed ship back on the map

    }


    /*-------------------------------------------------------------------*/
    /*Generates a ship at a random orientation and a random x and y value*/
    /*-------------------------------------------------------------------*/
    public boolean generateShip(int size) {
        Ship ship;
        int loopLimit = 100;
        do {
            //Picks a random x and y and direction
            int dir = rand.nextInt(2);
            int x = rand.nextInt(xHits.length);
            int y = rand.nextInt(yHits.length);

            Positions position = new Positions(x, y);

            ship = new Ship(position, size, dir);
            if(--loopLimit==0) return false;
        } while (!isValidPos(ship)); //If the ship placement is not valid generate another x and y to place it
        shipArrayList.add(ship);
        return true;
    }

    /*------------------------------------------------------*/
    /*Checks if the ship with in bounds and does not collide*/
    /*------------------------------------------------------*/
    public boolean isValidPos(Ship ship){

        int dir = ship.direction;
        int x = ship.x;
        int y = ship.y;
        int size = ship.size;

        if(dir == 0){
            if(y + size > yHits.length || checkCollisions(ship)){ //if the ships collides or is out or bound

                return false;
            }
        }

        else if (dir == 1){
            if(x + size > xHits.length || checkCollisions(ship)){

                return false;
            }
        }
        return true;
    }

    boolean checkCollisions(Ship ship) {
        return shipArrayList.stream().anyMatch(ship::collidesWith);
    }
    /*------------------------------------------------------*/
    /*-----Removes and replaces only one ship at a time-----*/
    /*------------------------------------------------------*/
    static BattleShips hillClimbing(int limit) {
        BattleShips bs = new BattleShips();
        bs.populateMap();
        ArrayList<Ship> oldShips;
        int score,newScore,limit2=10000;
        do {
            score = bs.getScore(bs.getGrid());
            oldShips = (ArrayList<Ship>) bs.shipArrayList.clone();
            bs.randomiseShip(1);
            newScore = bs.getScore(bs.getGrid()); //generates a new map with one ship changed
            if(newScore<score) { //checks if the new score if better than the current best
                score=newScore; //make new score the current best
            }
            else if(score == 0){
                bs.printGrid(bs.getGrid());
                System.out.println();
                System.out.println("SCORE: " + score);
                break;
            }
            else {
                bs.shipArrayList=oldShips;
            }
            if(--limit2==0&&bs.shipArrayList!=oldShips) {
                limit2=10000;
                bs.populateMap();
                score=bs.getScore(bs.getGrid());
            }
        } while (--limit!=0&&score!=0);
        return bs;
    }


    /*INCOMPLETE*/
    /*-----------------------------------------------------------------*/
    /*----Removes and replaces all ships randomly when temp is high----*/
    /*As temp cools, removes and replaces less and less amount of ships*/
    /*-----------------------------------------------------------------*/
    /*INCOMPLETE*/
    static BattleShips annealing(int limit, int temp) {
        BattleShips bs = new BattleShips();
        bs.populateMap();
        ArrayList<Ship> oldShips;
        int score,newScore,limit2=10000, ships = n;
        int change = temp / n;
        int thresh = temp - change;

        do {
            temp--;
            if(temp <= 1) {
                temp = 1;
            }
            // if the temp drops below the threshold amount
            if(temp <= thresh) {
                if (ships > 1) {
                    --ships; //reduce how many ships to remove and randomly place
                    thresh -= change; // Calculates the new threshold value
                }
            }

            score = bs.getScore(bs.getGrid());
            oldShips = (ArrayList<Ship>) bs.shipArrayList.clone();
            bs.randomiseShip(ships);
            newScore = bs.getScore(bs.getGrid());

            //if the new score is better than current best score
            if(newScore<score) {
                score=newScore; //make the new score the current best score
            }
            else if(score == 0){ //if the solution is found
                bs.printGrid(bs.getGrid());
                System.out.println();
                System.out.println("SCORE: " + score);
                break;
            }
            else {
                bs.shipArrayList=oldShips;
            }
            if(--limit2==0 && bs.shipArrayList != oldShips) {
                limit2=10000;
                bs.populateMap();
                score=bs.getScore(bs.getGrid());
            }
        } while (--limit!=0 &&score!=0);
        return bs;
    }
    /*------------------------------------------------------*/
    /*-----------Returns the score for a given map----------*/
    /*------------------------------------------------------*/
    public int getScore(boolean[][] grid){

        int rowHits = 0;
        int colHits = 0;
        score=0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {

                if(grid[j][i] == true){
                    colHits++; //sums all the ship hits for each column
                }
                if(grid[i][j] == true) {
                    rowHits++; //sums all the ship hits for each row
                }
            }
            //calcuates the score by subtracting the expected x and y hits by the actual hits
            score += (abs(xHits[i] - colHits));
            score += (abs(yHits[i] - rowHits));
            colHits = 0;
            rowHits = 0;
        }
        return score;
    }
    /*-----------------------------------------------------*/
    /*---Returns the grid for each ship in the ship list---*/
    /*-----------------------------------------------------*/
    public boolean[][] getGrid(){
        boolean[][] grid = new boolean[k][k];
        for (Ship s : shipArrayList) {
            if (s.direction == 0) { //if the direction is vertical
                for (int i = 0; i < s.size; i++) {
                    grid[s.y + i][s.x] = true; //increase the y by the size of ship
                }
            } else { //direction is horizontal
                for (int i = 0; i < s.size; i++) {
                    grid[s.y][s.x + i] = true; // increase the x by the ship size
                }
            }
        }
        return grid;
    }
    /*------------------------------------------------------*/
    /*-----------------Prints a given map-------------------*/
    /*------------------------------------------------------*/
    public void printGrid(boolean[][] grid){

        System.out.print("  | " );

        for (int i = 0; i < k; i++) {
            System.out.print(xHits[i] + " ");
        }
        System.out.print("\n--+");
        for (int i = 0; i < k; i++) {
            System.out.print("--");
        }
        System.out.println();


        for (int i = 0; i < k; i++) {
            System.out.print(yHits[i] + " | ");
            for (int j = 0; j < k; j++) {

                System.out.print(grid[i][j]?"X ":"0 ");
            }
            System.out.println();
        }
    }
}