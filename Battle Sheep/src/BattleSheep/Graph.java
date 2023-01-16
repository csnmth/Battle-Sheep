package BattleSheep;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * A játék logikai hátterét megvalósító osztály.
 * A játéktábla legelőit tároljuk el és végzünk rajta műveleteket.
 **/
public class Graph implements Serializable {
    /**
     * A játéktábla legelőit tároljuk el benne
     **/
    private ArrayList<Field> graph;

    /**
     * A legelők számát adja meg
     **/
    private int num = 0;

    /**
     * Létrehoz egy 32 mező méretű tárolót a gráfnak, amelybe majd később kerülnek bele a legelőket alkotó mezők
     **/
    public Graph() {
        graph = new ArrayList<>(32);
    }

    /**
     * Hozzáad egy mezőt a gráfhoz
     **/
    public void addField(Field field) {
        num++;
        graph.add(field);
    }

    /**
     * Visszaadja a legelők számát
     **/
    public int getNum() {
        return num;
    }

    /**
     * Kiszámol egy induló mezőről egy adott irányba induló lépést, amely
     * az adott irányban a legtávolabbi legelő lesz.
     **/
    public Field getStep(Field init, int dir) {
        Field I = null;

        if (!graph.contains(init)) {
            return I;
        }

        /* Kikeressük az induló mezőt */
        for (Field It : graph) {
            if (It.equals(init)) {
                I = It;
                break;
            }
        }

        /* Addig megy, amíg az adott irányban nincs már szomszédja az aktuális mezőnek */
        while (I.getDirNeighbor(dir) != null) {
            if (I.getDirNeighbor(dir).getPlayer().getPlayerID() != -1) {
                break;
            }
            I = I.getDirNeighbor(dir);
        }
        return I;
    }


    /**
     * Megnézi, hogy tud-e az adott játékos lépni még
     **/
    public boolean playerCanMove(Player player) {
        int cnt = 0;
        for (Field f : graph) {
            if (f.getPlayer().equals(player) && f.getSheepNumber() > 1) {
                for (Map.Entry<Integer, Field> i : f.getNeighbors().entrySet()) {
                    if (i.getValue().getPlayer().getPlayerID() == -1) {
                        cnt++;
                    }
                }
            }
        }
        return cnt != 0;
    }

    /**
     * Visszaadja a gráfot
     **/
    public ArrayList<Field> getGraph() {
        return graph;
    }

    /**
     * Elmenti a gráfot a megadott fájlba
     * **/
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream("graph");
            ObjectOutputStream ous = new ObjectOutputStream(fos);
            ous.writeObject(this);
            ous.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
