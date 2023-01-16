package BattleSheep;

import java.awt.*;
import java.io.Serializable;

/**
 * A játékos adatait eltároló osztály, amely alapján azonosítható egy játékos
 **/
public class Player implements Serializable {
    /**
     * A játékos neve
     **/
    private String name;
    /**
     * A játékos ID-ja, amely 1 vagy 2 lehet, vagy -1, ha egy nem létező játékost hozunk létre.
     **/
    private int playerID;
    /**
     * A játékos pontszámát tárolja el.
     **/
    private int score;
    /**
     * A játékos színét tároljuk el benne, amely bármilyen szín lehet, ami nem egyezik az ellenfél színével.
     **/
    private Color color;

    /**
     * Default konstruktor, amely létrehoz egy nem létező játékost.
     * A tulajdonossal egyelőre nem rendelkező mezők azonosításának egyszerűsítése miatt van erre szükség.
     **/
    public Player() {
        name = "-1-1";
        playerID = -1;
        color = Color.BLACK;
    }

    /**
     * Létrehoz létező, névvel és színnel rendelkező játékost.
     * **/
    public Player(String rhsName, int ID, Color c) {
        name = rhsName;
        score = 0;
        playerID = ID;
        color = c;
    }

    /**
     * Visszadja a játékos ID-ját.
     * **/
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Visszaadja a játékos színét.
     * **/
    public Color getColor() {
        return color;
    }

    /**
     * Visszaadja a játékos nevét.
     * **/
    public String getName() {
        return name;
    }

    /**
     * Beállítja a játékos pontszámát.
     * **/
    public void setScore(int rhsScore) {
        score = rhsScore;
    }

    /**
     * Lekéri a játékos pontszámát.
     * **/
    public int getScore() {
        return score;
    }
}
