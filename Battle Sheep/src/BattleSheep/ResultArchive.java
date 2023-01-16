package BattleSheep;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A régebbi eredményeket eltároló osztály, amely megjeleníthető JTable-ban is
 **/
public class ResultArchive extends AbstractTableModel implements Serializable {
    /**
     * Az egykor egymás ellen játszó játékos párokat tárolja el
     **/
    private Player[][] players;
    /**
     * A megfelelő indexű helyen a megfelelő időbélyeget tárolja a játékhoz
     **/
    private ArrayList<LocalDate> time = new ArrayList<>(100);

    /**
     * Az archívumban eltárolt sorok számát tartja számon
     **/
    private int num;

    /**
     * Az BattleSheep.ResultArchive konstrukrora, amely létrehozza a játékosok tárolóját üresen
     **/
    public ResultArchive() {
        players = new Player[100][2];
        num = 0;
    }

    /**
     * Betesz egy játékospárt és egy időt a tárolóba
     **/
    public void put(Player p1, Player p2, LocalDate date) {
        if (num > 100) {
            players[0][0] = p1;
            players[0][1] = p2;
            time.add(date);
        } else {
            players[num][0] = p1;
            players[num][1] = p2;
            time.add(date);
        }
        num++;
    }

    /**
     * Elmenti az archívumot a megaott fájlba
     **/
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream("eredmenyek");
            ObjectOutputStream ous = new ObjectOutputStream(fos);
            ous.writeObject(this);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /**
     * Betölti az archívumot egy fájlból.
     **/
    public void load() {
        try {
            FileInputStream fis = new FileInputStream("eredmenyek");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ResultArchive tmp = (ResultArchive) ois.readObject();
            this.players = tmp.players;
            this.time = tmp.time;

            while (this.players[num][0] != null) {
                num++;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Visszaadja a játékosokkat tároló tárolót
     **/
    public Player[][] getPlayers() {
        return players;
    }

    /** Visszaadja, hogy mennyi sor található a táblában **/
    @Override
    public int getRowCount() {
        return num;
    }

    /** Visszaadja, hogy mennyi oszlop található a táblában **/
    @Override
    public int getColumnCount() {
        return 5;
    }

    /** A táblamodell alapja, amely megadja hogy adott indexű helyre mi kerül.
     * A játékosok neveit, pontszámait és az eredmény születésének időpontját jelenítjük meg egy sorban
     **/
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Player[] pl = players[rowIndex];
        LocalDate t = time.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> pl[0].getName();
            case 1 -> pl[0].getScore();
            case 2 -> pl[1].getName();
            case 3 -> pl[1].getScore();
            case 4 -> t;
            default -> pl.length;
        };
    }

    /** Az oszlopok neveit adja meg **/
    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Játékos 1 név:";
            case 1 -> "Játékos 1 pont:";
            case 2 -> "Játékos 2 név:";
            case 3 -> "Játékos 2 pont:";
            case 4 -> "Dátum:";
            default -> "Cím";
        };
    }

    protected void feltoltoScript() {
        Player p1 = new Player("Andris", 1, new Color(142, 123, 1));
        Player p2 = new Player("Peti", 1, new Color(1, 103, 1));
        p1.setScore(4);
        p2.setScore(6);

        Player p3 = new Player("Bazsi", 1, new Color(42, 109, 150));
        Player p4 = new Player("pasman07", 1, new Color(100, 103, 100));
        p3.setScore(15);
        p4.setScore(12);

        Player p5 = new Player("Hanga", 1, new Color(122, 1, 1));
        Player p6 = new Player("Zsiga", 1, new Color(100, 30, 140));
        p5.setScore(14);
        p6.setScore(11);

        Player p7 = new Player("Zsombor", 1, new Color(142, 90, 1));
        Player p8 = new Player("Viola", 1, new Color(30, 40, 100));
        p7.setScore(16);
        p8.setScore(16);

        Player p9 = new Player("Evelin", 1, new Color(90, 90, 90));
        Player p10 = new Player("Péter", 1, new Color(45, 154, 100));
        p9.setScore(0);
        p10.setScore(4);

        this.put(p1, p2, LocalDate.of(2022, 11, 1));
        this.put(p10, p9, LocalDate.of(2022, 11, 4));
        this.put(p7, p8, LocalDate.of(2022, 11, 10));
        this.put(p6, p5, LocalDate.of(2022, 11, 15));
        this.put(p3, p4, LocalDate.of(2022, 11, 18));
        this.put(p2, p5, LocalDate.now());
        this.put(p7, p4, LocalDate.now());
        this.put(p4, p1, LocalDate.now());

    }
}


