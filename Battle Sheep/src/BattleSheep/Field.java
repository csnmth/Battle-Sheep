package BattleSheep;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;

/**
 * A táblán található mezők logikai megvalósítása
 **/
public class Field implements Serializable {
    /**
     * A mező táblán található koordinátáját tárolja el, amely első koordinátája az oszlop száma, második a sor száma
     **/
    private Point2D coordinate;
    /**
     * A mező szomszédait tárolja el úgy, hogy a megfelelő sorszámú oldalat párosítja az ott található mezővel
     **/
    private HashMap<Integer, Field> neighbors;

    /**
     * A mező szomszédainak számát adja meg
     **/
    public int numNeighbors;

    /**
     * A rajta álló játékost tárolja el
     **/
    private Player player;
    /**
     * A mezőn álló bárányok számát adja meg
     **/
    private int sheepNumber = 0;

    /**
     * Létrehoz egy mezőt, aminek a paraméterben kapott játékos a tulajdonosa, és egyelőre nincs szomszédja
     **/
    public Field(Player pl, Point2D pt) {
        neighbors = new HashMap<>(6);
        numNeighbors = 0;
        player = pl;
        coordinate = pt;
    }

    /**
     * A paraméterben kapott irányba hozzáad egy szomszédot a mezőhöz
     **/
    public void addNeighbor(int dir, Field n) {
        if (!neighbors.containsKey(dir)) {
            neighbors.put(dir, n);
            numNeighbors++;
        }
    }

    /**
     * Visszaadja a mező paraméterben adott irányú szomszédját
     **/
    public Field getDirNeighbor(int dir) {
            return neighbors.get(dir);
    }

    /**
     * A mezőn álló játékost és a bárányszámot állítja be
     **/
    public void setPlayer(Player player, int sheeps) {
        this.player = player;
        this.sheepNumber = sheeps;
    }

    /**
     * Visszaadja a mezőn álló játékost
     **/
    public Player getPlayer() {
        return player;
    }

    /**
     * Visszadja a mezőn lévő bárányok számát.
     **/
    public int getSheepNumber() {
        return sheepNumber;
    }

    /**
     * A paraméterben megadott számmal csökkenti a mezőn álló bárányok számát
     **/
    public void setSheepNumber(int minus) {
        sheepNumber = minus;
    }

    /**
     * Visszaadja a mező szomszédait tároló Map-et
     **/
    public HashMap<Integer, Field> getNeighbors() {
        return neighbors;
    }

    /**
     * Kitörli a mező összes szomszédját.
     **/
    public void deleteNeighbors() {
        neighbors.clear();
    }

    /**
     * Visszaadja a mező koordinátáit
     * **/
    public Point2D getCoordinate() {
        return coordinate;
    }
}

