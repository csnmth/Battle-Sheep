package BattleSheep;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A játék logikáját megvalósító osztály, amely számontartja a köröket, a győztest, valamint a játékosokat.
 **/
public class GameLogic implements Serializable {
    /**
     * A játékosokat tárolja el.
     **/
    private ArrayList<Player> players = new ArrayList<>(2);

    /**
     * Az éppen soron következő játékos ID-ját tárolja el, és ezt is mentjük ki a fájlba későbbre.
     **/
    private int Turn;
    /**
     * Az éppen soron következő játékost tárolja el
     **/
    private transient Player turn;

    /**
     * Az éppen elmozdítani kívánt bárányok számát tárolja el
     **/
    private int moveSheeps;

    /**
     * Az éppen elmozdítani kívánt bárányok kiinduló mezőjét tárolja
     **/
    private Field source;

    /**
     * Az éppen elmozdítani kívánt bárányok kiinduló hatszögét tárolja
     **/
    private Polygon sourcePoly;

    /**
     * A régebbi eredményeket eltároló archívumot jegyzi
     **/
    private transient ResultArchive archive;

    /**
     * Ezzel a függvénnyel indítható a játék
     **/
    public static void main(String[] args) {
        GameLogic gl = new GameLogic();
    }

    /**
     * Létre hozza a játék indításához szükséges objektumokat, valamint betölti az archívumot és elindítja a Főmenüt.
     **/
    public GameLogic() {
        /*Játékosok hozzáadása*/
        setArchive();
        MainMenu m = new MainMenu();
        m.Init(this);
    }

    /**
     * Létrehozza és betölti az archívumot a megfelelő fájlból
     **/
    public void setArchive() {
        archive = new ResultArchive();
        //archive.feltoltoScript();  //opcionális
        archive.load();
    }

    /**
     * Hozzáad egy játékost a játékosok közé
     **/
    public void setPlayer(Player p) {
        players.add(p.getPlayerID() - 1, p);
    }

    /**
     * Eltávolít egy játékost a játékosok közül, ha van legalább egy játékos
     **/
    public void removePlayer(int playerID) {
        if (players.size() > 0) {
            players.remove(playerID - 1);
        }
    }

    /**
     * Lekérdezhetünk vele egy játékost.
     **/
    public Player getPlayer(int i) {
        return players.get(i);
    }

    /**
     * Lekérdezi a soron lévő játékost
     **/
    public Player getTurn() {
        return turn;
    }

    /**
     * Lekérdezi a soron lévő játékos ID-ját-
     **/
    public int getTURN() {
        return Turn;
    }

    /**
     * A másik játékosnak passzolja tovább a kört.
     **/
    public void nextPlayer() {
        if (getTurn().equals(players.get(0))) {
            turn = players.get(1);
        } else {
            turn = players.get(0);
        }
    }

    /**
     * A játékosok lépési lehetőségeiből megállapítja a játék végét és visszaadja a győztest
     **/
    public boolean endGame(Graph graph) {
        /*Ha van olyan játékos, aki még tud lépni, akkor nincs vége*/
        if (graph.playerCanMove(players.get(0)) || graph.playerCanMove(players.get(1))) {
            return false;
        }
        /*egyébként meg vége van*/
        else {
            archive.put(getPlayer(0), getPlayer(1), LocalDate.now());
            return true;
        }
    }

    /**
     * Megállapítja a pontszámokat és ez alapján a végeredményt
     **/
    public Player getWinner(Graph graph) {
        int player1score = 0;
        int player2score = 0;
        for (Field f : graph.getGraph()) {
            if (f.getPlayer().getPlayerID() == 1) {
                player1score++;
            } else if (f.getPlayer().getPlayerID() == 2) {
                player2score++;
            }
        }
        getPlayer(0).setScore(player1score);
        getPlayer(1).setScore(player2score);
        if (player1score == player2score) {
            return null;
        } else if (player1score > player2score) {
            return getPlayer(0);        //player1 nyert
        } else {
            return getPlayer(1);        //player2 nyert
        }
    }

    /**
     * Az ID alapján állítjuk be a soron lévő játékost.
     **/
    public void setTurn(int i) {
        if (0 < i && i < 3) {
            turn = players.get(i - 1);
        } else turn = players.get(0);
    }

    /**
     * Beállítja a elmozdítani kívánt bárányok számát
     **/
    public void setMoveSheeps(int moveSheeps) {
        this.moveSheeps = moveSheeps;
    }

    /**
     * Lekérdezi a elmozdítani kívánt bárányok számát
     **/
    public int getMoveSheeps() {
        return moveSheeps;
    }

    /**
     * Beállítja az elmozdítás kiinduló mezőjét
     **/
    public void setSource(Field source) {
        this.source = source;
    }

    /**
     * Lekérdezi az elmozdítás kiinduló mezőjét
     **/
    public Field getSource() {
        return source;
    }

    /**
     * Beállítja az elmozdítás kiinduló hatszögét
     **/
    public void setSourcePoly(Polygon value) {
        sourcePoly = value;
    }

    /**
     * Lekérdezi az elmozdítás kiinduló hatszögét
     **/
    public Polygon getSourcePoly() {
        return sourcePoly;
    }

    /**
     * Lekérdezi a régebbi eredményeket tároló archívumot
     **/
    public ResultArchive getArchive() {
        return archive;
    }

    /**
     * Elmenti az aktuállis játéklogikát
     **/
    public void save() {
        if (turn != null) {
            Turn = turn.getPlayerID();
        }
        try {
            FileOutputStream fos = new FileOutputStream("gamelogic");
            ObjectOutputStream ous = new ObjectOutputStream(fos);
            ous.writeObject(this);
            ous.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
