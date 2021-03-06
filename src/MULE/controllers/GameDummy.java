package MULE.controllers;


import MULE.models.*;
import com.google.gson.GsonBuilder;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

//Created by Aaron on 9/17/2015.
@SuppressWarnings("ALL")
public class GameDummy extends Game{
    public static GameDummy instance = new GameDummy();
    private String lastEvent = "---"; //ONLY DEBUG
    private ArrayList<Color> notAllowed = new ArrayList<Color>(Arrays.asList(Color.WHITE));
    public int numOfPlayers = 1;
    public final int DEFAULT_PLAYER_AMOUNT = 0; //why is this 0?
    public int round = 0;
    public Player[] players = new Player[DEFAULT_PLAYER_AMOUNT];
    public Player[] originalPlayers = new Player[DEFAULT_PLAYER_AMOUNT];
    private Diffuculty difficulty;
    private int mapType = 1;
//    private int[] playerTurn; //unused?
    private int totalTurns = 1;
    private int turn = 1;
    public State currentState = State.MAIN;
    public PlayerTimer timer = new PlayerTimer();
    public ResourceStore store = new ResourceStore();
    public int[] resourcePoints = {1, 500, 1, 1, 1}; //holds point values of money, land, energy, smithore, food
    private static RandomEvent[] possibleEvents = {new EventOne(), new EventTwo(), new EventThree(), new EventFour(), new EventFive(), new EventSix(), new EventSeven()};
    private boolean[][] muleArray = new boolean[5][9];
    private Color[][] colorArray = new Color[5][9];
    private transient MediaPlayer mediaPlayer = null;

    private Map theMap = new Map();
    private int buyPhaseSkipped = 0;

    public int LAND_PRICE = 300;

    public static GameDummy getInstance(){
        return instance;
    }

    public Color[][] getColorArray() {
        return colorArray;
    }

    public boolean[][] getMuleArray() {
        return muleArray;
    }

    public void saveGame() {
        try {
            try (PrintWriter out = new PrintWriter(new File("data.json"))) {
                Gson gs = new GsonBuilder().registerTypeAdapter(Color.class, new ColorInstanceCreator()).registerTypeAdapter(Resource.class, new InterfaceAdapter()).create();
                String gson = gs.toJson(this);
                out.print(gson);
                System.out.println(gson);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameDummy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadGame() {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("data.json"))) {
                String json = br.readLine();
                System.out.println(json);
                Gson gs = new GsonBuilder().registerTypeAdapter(Color.class, new ColorInstanceCreator()).registerTypeAdapter(Resource.class, new InterfaceAdapter()).create();
                instance = gs.fromJson(json, GameDummy.class);

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameDummy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameDummy.class.getName()).log(Level.SEVERE, null, ex);
        }
        switch (instance.currentState) {
            case MAP:
            case MULE_PLACING:
            case BUY_PHASE:
                break;
            case STORE:
                break;
            case IN_TOWN:
                break;
            case MAIN:
                break;
            case CONFIG:
                break;
        }
        if (instance.colorArray[0][0] == null) {
            System.out.println("Null");
        } else {
            System.out.println(instance.colorArray[0][0].getClass());
        }
        if (instance.players[0] == null) {
            System.out.println("Null Players");
        }
        if (instance.theMap.whatLand(0,0) == null) {
            System.out.println("Null Map");
        }
    }


    public void leaveTown(String side) {
        currentState = State.MAP;
    }

    //TODO for animations later
    public void leaveStore(String side) {
        currentState = State.IN_TOWN;
    }


    public void randomEvent(){
        instance = this;
        Random random = new Random();
        int chance = random.nextInt(101);
        if (chance <= 27) {
            int x = chance % possibleEvents.length;
            if (currentPlayer() == getPlayers()[0] && x > 3) {
                x = chance % 4;
            }
            RandomEvent event = possibleEvents[x];
            lastEvent = event.apply(currentPlayer());
        }
    }
    //ONLY FOR DEBUG
    public String getLastEvent() {
        return lastEvent;
    }

//    public enum State{
//        MAIN, CONFIG, IN_TOWN, AUCTION, BUYPHASE, MAP, STORE, MULE_PLACING
//    }

    public void setMediaPlayer(MediaPlayer mp) {
        mediaPlayer = mp;
    }

    public void playMusic() {
        mediaPlayer.play();
    }

    public void pauseMusic() {
        mediaPlayer.pause();
    }

    public void changeState(State s) {
        currentState = s;
    }

    public Diffuculty getDifficulty() {
        return difficulty;
    }


    public int getMapType() {
        return mapType;
    }

    public void setNumOfPlayers(int num) {
        instance = this;
        numOfPlayers = num;
        players = new Player[num];
        originalPlayers = new Player[num];
//        playerTurn = new int[num];
//        for (int i = 0; i < num; i++) {
//            playerTurn[i] = i + 1;
//        }
    }

    public ArrayList<Color> getColors() {
        return notAllowed;
    }

    public boolean isColorAvailable(Color c) {
        return !notAllowed.contains(c);
    }

    public void useAssay() {

    }

    public void useLandOffice() {

    }

    public int getRound() {
        return round;
    }

    public State getPhase() {
        return currentState;
    }

    public Player[] getPlayers() {
        return players;
    }
    public Player[] getOriginalPlayers() {
        return originalPlayers;
    }

    public void gamble() {
        int[] roundBonus = {50,50,50,100,100,100,100,150,150,150,150,200};
        int timeBonus;
        int bonus;
        Random rand = new Random();
        int time = timer.getTime();
        System.out.println(time);
        if (time<=12){
            timeBonus=50;
        } else if (time<=25){
            timeBonus=100;
        } else if (time<=37){
            timeBonus=150;
        } else{
            timeBonus=200;
        }
        bonus = roundBonus[round - 3]+rand.nextInt(timeBonus + 1);
        if (bonus>250){
            bonus=250;
        }
        players[turn-1].addMoney(bonus);
        System.out.println("Gambled and won " + bonus + ".");
        timer.stopTime();
    }

    public void buyPhaseSkip() {
        instance = this;
        System.out.println("buyPhaseSkip() called.");
        if (currentState.equals(State.BUY_PHASE)) {
            System.out.println("buyPhaseSkip() executed.");
            buyPhaseSkipped++;
            buyPhaseEndTurn();
        }
    }

    public Player currentPlayer() {
        return players[getTurn() - 1];
    }
    public void landClicked(String landLoc, Rectangle rec, Rectangle mul) {
        instance = this;
        if (currentState.equals(State.BUY_PHASE)) {
            System.out.println("Round:" + round);
            int i = Integer.parseInt(landLoc.substring(3, 5)) / 10;
            int j = Integer.parseInt(landLoc.substring(3, 5)) % 10;
            System.out.println(i + ", " + j);
            Land plot = theMap.whatLand(i, j);
            System.out.println("Turn: " + turn + ", Total Turns: " + totalTurns);
            if (!plot.isOwned()) {
                Player p = players[turn - 1];
                if (round < 3) {
                    plot.setOwner();
                    p.addLand(plot);
                    rec.setStroke(p.getColor());
                    colorArray[i][j] = p.getColor();
                    rec.setStrokeWidth(4.0);
                    p.incrementLand();
                    buyPhaseSkipped = 0;
                    buyPhaseEndTurn();
                } else {
                    if (p.getMoney() >= LAND_PRICE) {
                        plot.setOwner();
                        p.addLand(plot);
                        rec.setStroke(p.getColor());
                        colorArray[i][j] = p.getColor();
                        rec.setStrokeWidth(4.0);
                        p.subtractMoney(LAND_PRICE);
                        p.incrementLand();
                        buyPhaseSkipped = 0;
                        buyPhaseEndTurn();
                    }
                }
            }



            System.out.println(playersToString()); //debug statement
        } else if (currentState.equals(State.MULE_PLACING)) {
            int i = Integer.parseInt(landLoc.substring(3, 5)) / 10;
            int j = Integer.parseInt(landLoc.substring(3, 5)) % 10;
            System.out.println(i + ", " + j);
            Land plot = theMap.whatLand(i, j);
            Player p = players[turn - 1];
            //System.out.println(plot.isOwned() + " " + p.equals(plot.getOwner()) + " " + !plot.hasMule());
            if (plot.isOwned() & p.equals(plot.getOwner(players, numOfPlayers)) & !plot.hasMule()) {
                //if (plot.isOwned() & doesPlayerOwn(p, plot) & !plot.hasMule()) {
                plot.setMule(p.getMule());
                muleArray[i][j] = true;

                //Image muleImage = new Image("/views/M.U.L.E..png", 20, 20, true, false);
                //ImagePattern imagePattern = new ImagePattern(muleImage);
                //mul.setFill(imagePattern); //check to make sure this doesn't override color


                currentState = State.MAP; //is this where we want the screen to go to?
            } else {
                //TODO present proper error message
                System.out.println("Improper location for your MULE. It has successfully escaped your grasp and ran away.");
                p.getMule();
            }
            currentState = State.MAP;
        }
    }

    public boolean doesPlayerOwn(Player p, Land l) {
        boolean returnValue = false;
        for (Land pl : p.getLand()) {
            if (pl.getRow() == l.getRow() && pl.getCol() == l.getCol()) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    public void buyPhaseEndTurn() {
        instance = this;
        boolean noMoney = true; //make prettier later
        while (noMoney && buyPhaseSkipped < numOfPlayers) {
            turn = turn % numOfPlayers + 1;
            totalTurns++;
            round = (totalTurns-1) / numOfPlayers;
            if (players[turn - 1].getMoney() < LAND_PRICE) {
                buyPhaseSkipped++;
            } else {
                noMoney = false;
            }
        }

        if (buyPhaseSkipped >= numOfPlayers) {
            currentState = State.MAP;

            if (turn != 1) {
                totalTurns = totalTurns + numOfPlayers - turn + 1;
                round++;
                turn = 1;
            }
            reorderPlayers();
            //timer.startTime();
        }
    }
    public void endTurn() {
        instance = this;
        ArrayList<Land> plots = currentPlayer().getLand();
        for (Land plot: plots) {
            plot.produce(players, numOfPlayers);
        }
        turn = turn % numOfPlayers + 1;
        totalTurns++;
        round = (totalTurns-1) / numOfPlayers;
        if (turn == 1) {
            reorderPlayers();
        }
        System.out.println(round + " " + (round > 14));
        if (round <= 14) {
            //timer.startTime();
            currentState = State.MAP;
        } else {
            currentState = State.MAIN; //swap out with display scores later

        }
    }

    public void incrementTurn() {
        turn = turn % numOfPlayers + 1;
        totalTurns++;
        round = (totalTurns-1) / numOfPlayers;
        //use this only for player config
    }

    public void reorderPlayers() {
        System.out.println("Reordered");
        for(int i = 0; i < numOfPlayers - 1; i++) {
            System.out.println("i: " + i);
            for (int j = i + 1; j < numOfPlayers; j++) {
                System.out.println("j: " + j);
                System.out.println(players[i].getName() + " checked with " + players[j].getName());
                if (players[i].getScore() > players[j].getScore()) {
                    System.out.println(players[i].getName() + " switched with " + players[j].getName());
                    Player temp = players[i];
                    players[i] = players[j];
                    players[j] = temp;
                }
            }

        }
        System.out.println("New player order:\n" + playersToString());

    }

    public void townClicked() {
        if (currentState == State.MAP || currentState == State.STORE) {

            currentState = State.IN_TOWN;
        }
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public int getTurn() {
        return turn;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setConfigurationSettings(Diffuculty difficulty, int numOfPlayers) {
        this.difficulty = difficulty;
        setNumOfPlayers(numOfPlayers);
        currentState = State.CONFIG;

    }

    public void addPlayer(String race, Color c, String name) {
        instance = this;
        notAllowed.add(c);
        players[turn-1] = new Player(name, race, c, Diffuculty.BEGINNER.getPlayerFood(), Diffuculty.BEGINNER.getPlayerEnergy());
        originalPlayers[turn-1] = players[turn-1];
        incrementTurn();
        if (getNumOfPlayers() >= getTurn() && getTotalTurns() == getTurn()) {

        } else {

            currentState = State.BUY_PHASE;
        }
    }

    public void enterStore() {
        if (currentState == State.IN_TOWN) {

            currentState = State.STORE;
        }
    }

    public ResourceStore getStore() {
        return store;
    }

    //replaced
//    public void getTurnOrder() {
//        int temp;
//        for(int i = 0; i < players.length; i++) {
//            for (int j = i; j < players.length; j++) {
//                if (players[j].getMoney() > players[i].getMoney()) {
//                    temp = playerTurn[i];
//                    playerTurn[i] = j;
//                    playerTurn[j] = temp;
//                }
//            }
//
//        }
//    }

    public void buyMULE(Resource resource) {
        instance = this;
        Player p = players[turn - 1];
        int price = 100;
        price += resource.getStorePriceExtra();
        if (p.getMoney() >= price) {
            Mule newMule = new Mule(resource);
            p.subtractMoney(price);
            currentState = State.MULE_PLACING;
            p.giveMule(newMule); //TODO remove MULE when player's turn ends

        } else {
            System.out.println("Not enough money"); //TODO proper error message
        }
    }

    public String playersToString() {
        String returnString = "";
        for(Player p : players) {
            if (p != null) {
                returnString = returnString + p.toString() + "\n";
            } else {
                returnString = returnString +  "null\n";
            }
        }
        return returnString;
    }

    public int moneyValue() {
        return resourcePoints[0];
    }

    public int landValue() {
        return resourcePoints[1];
    }

    public int foodValue() {
        return resourcePoints[2];
    }

    public int smithoreValue() {
        return resourcePoints[3];
    }

    public int energyValue() {
        return resourcePoints[4];
    }

}



