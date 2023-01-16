package BattleSheep;

import BattleSheep.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.time.LocalDate;


/**
 * A játék logikai hátterének tesztelését végzi
 **/
public class TestClass {
    /**
     * Létrehozunk tesztobjektumokat, hogy egységesen lehessen tesztelni
     **/
    Player testPlayer0;
    Player testPlayer1;
    Player testPlayer2;
    Field testField1;
    Field testField2;
    GameLogic testGameLogic;
    Graph testGraph;
    ResultArchive testResultArchive0;

    /**
     * Inicializáljuk a tesztobjektumokat
     **/
    @Before
    public void setUp() {
        testPlayer0 = new Player();
        testPlayer1 = new Player("Vincs Eszter", 1, Color.RED);
        testPlayer2 = new Player("Teszt Elek", 2, Color.BLUE);

        testField1 = new Field(new Player(), new Point(1, 1));
        testField2 = new Field(new Player(), new Point(2, 2));

        testGameLogic = new GameLogic();
        testGraph = new Graph();
        testResultArchive0 = new ResultArchive();
    }

    /**
     * A BattleSheep.Player osztály konstruktorainak tesztelése
     **/
    @Test
    public void testPlayers() {
        Assert.assertEquals("Rossz a BattleSheep.Player default konstruktora", -1, testPlayer0.getPlayerID());
        Assert.assertEquals("Rossz a BattleSheep.Player konstruktora", Color.RED, testPlayer1.getColor());
        Assert.assertEquals("Rossz a BattleSheep.Player konstruktora", "Teszt Elek", testPlayer2.getName());
    }

    /**
     * Mezők szomszédjainak hozzáadását teszteli
     **/
    @Test
    public void testField() {
        testField1.addNeighbor(4, testField2);
        testField2.addNeighbor(1, testField1);
        Assert.assertEquals("Nem működik a szomszédok hozzáadása vagy lekérdezése", testField1, testField2.getNeighbors().values().toArray()[0]);
        Assert.assertEquals("Nem működik a szomszédok hozzáadása vagy lekérdezése", testField2, testField1.getNeighbors().values().toArray()[0]);
    }

    /**
     * A gráfhoz mezők hozzáadását teszteli
     **/
    @Test
    public void testGraph() {
        testGraph.addField(testField1);
        testGraph.addField(testField2);
        Assert.assertEquals("Nem működik jól a mezők gráfhoz adása", 2, testGraph.getNum());

    }

    /**
     * Játékosok játékhoz rendelése, kör beállítása, következő játékosra lépés és
     * játékosok mozgásának lehetőségének és játék végének és eredményének tesztelése
     **/
    @Test
    public void testGameLogic() {
        testGameLogic.setPlayer(testPlayer1);
        testGameLogic.setPlayer(testPlayer2);
        testGameLogic.setTurn(1);
        testGameLogic.nextPlayer();
        Assert.assertEquals("Nem működik jól a következő játékos léptetése", testPlayer2, testGameLogic.getTurn());

        /* A tábla mindkét mezőjére teszünk bárányoszlopot, így egyik játékos sem tud lépni, tehát vége a játéknak*/
        testField1.setPlayer(testPlayer1, 16);
        testField2.setPlayer(testPlayer2, 16);
        Assert.assertTrue("Nem működik jól a játék vége észlelése", testGameLogic.endGame(testGraph));
        Assert.assertNull("Nem működik jól a döntetlen végeredmény észlelése", testGameLogic.getWinner(testGraph));

    }

    /**
     * Az eredmény archívumhoz bejegyzés hozzáadásának tesztelése
     * **/
    @Test
    public void testResultArchive() {
        testResultArchive0.put(testPlayer1, testPlayer2, LocalDate.now());
        Assert.assertEquals("Nem működik jól a adatok archívumhoz hozzáadása", testPlayer1.getName(), testResultArchive0.getPlayers()[0][0].getName());
        Assert.assertEquals("Nem működik jól az elemek számolása az archívumban", 1, testResultArchive0.getRowCount());

        testResultArchive0.feltoltoScript();
        testResultArchive0.put(testPlayer1, testPlayer2, LocalDate.of(2022, 11, 11));
        Assert.assertEquals("Nem működik jól a adatok archívumhoz hozzáadása", testPlayer1.getName(), testResultArchive0.getPlayers()[testResultArchive0.getRowCount() - 1][0].getName());

    }
}
