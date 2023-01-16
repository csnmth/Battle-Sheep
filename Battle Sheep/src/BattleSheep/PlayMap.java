package BattleSheep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * A játékpályát megjelenítő osztály, amely egy ablakot hoz létre
 **/
public class PlayMap extends JFrame implements Serializable {

    /**
     * A pálya mezőit tartja számon, a kirajzolt hatszögeket, valamint logikai mezőket összepárosítva
     **/
    private HashMap<Field, Polygon> polygons;

    /**
     * Az ablakban találató panel, amire a játéktáblát rajzoljuk.
     **/
    private transient MapPanel map;

    /**
     * BattleSheep.PlayMap osztály konstruktora,
     * Elindítja az ablak megjelenítéséért felelős initPlayMap függvényt
     **/
    public PlayMap(Graph gr, GameLogic gl, MainMenu mainMenu) {
        initPlayMap(gr, gl, mainMenu);
    }

    /**
     * A játékot megjelenítő ablakot jeleníti meg, valamint a játék során használható menüsort alkotja meg.
     **/
    public void initPlayMap(Graph gr, GameLogic gl, MainMenu mainMenu) {
        setLayout(new BorderLayout());
        setSize(800, 800);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JMenuBar jmb = new JMenuBar();
        JMenu file = new JMenu("Navigáció");
        JMenu lepes = new JMenu("Lépés");
        jmb.add(file);

        JMenuItem backToMain = new JMenuItem("Vissza a főmenübe");
        backToMain.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
            mainMenu.setVisible(true);
        });

        JMenuItem menuItem = new JMenuItem("Új játék");
        menuItem.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
            mainMenu.cardLayout.show(mainMenu.getMainPane(), "newg");
            mainMenu.setVisible(true);
        });

        JMenuItem menuItem1 = new JMenuItem("Mentés");
        menuItem1.addActionListener(e -> {
            save();
            gr.save();
            gl.save();
            gl.getArchive().save();
        });

        JMenuItem menuItem2 = new JMenuItem("Kilépés");
        menuItem2.addActionListener(e -> {
            save();
            gr.save();
            gl.save();
            gl.getArchive().save();
            System.exit(0);
        });

        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(e -> {
            this.setVisible(false);
            this.dispose();
            mainMenu.load();
        });

        file.add(backToMain);
        file.add(menuItem);
        file.add(menuItem1);
        file.add(menuItem2);
        lepes.add(undo);

        jmb.add(file);
        jmb.add(lepes);
        this.setJMenuBar(jmb);
    }

    /**
     * A játék első fázisa.
     * Kirajzolja a pálya keretét alkotó hatszögeket, melyekbe később legelőket helyezhetnek a játékosok.
     **/
    public void drawEmptyMap(Graph gr) {
        setVisible(true);
        int pX[] = {25, 75, 100, 75, 25, 0};
        int pY[] = {0, 0, 43, 87, 87, 43};
        polygons = new HashMap<>(60);
        //6*10 tabla epites oszloponként, ez állítható, ha nagyobb térben szeretnénk játszani.
        int i = 0;
        for (int col = 1; col < 11; col++) {

            for (int depth = 0; depth < 6; depth++, i++) {

                polygons.put(new Field(new Player(), new Point(col, depth)), new Polygon(pX, pY, pY.length));

                for (int j = 0; j < pY.length; j++) {
                    pY[j] += 87;
                }
            }
            //fent kezdett oszlop (páratlan sorszámú)
            if (col % 2 != 0) {
                for (int j = 0; j < pY.length; j++) {
                    pY[j] -= 479;
                    pX[j] += 75;
                }
            }
            //lefelé eggyel eltolt oszlop (páratlan sorszámú)
            else {
                for (int j = 0; j < pY.length; j++) {
                    pY[j] -= 565;
                    pX[j] += 75;
                }
            }
        }
        map = new MapPanel(this, gr);
        add(map, BorderLayout.CENTER);
    }

    /**
     * A játék második fázisa.
     * Itt hozza létre azt a mouseListenert, aminek segítségével a játékosok legelőket képesek elhelyezni a táblára,
     * összesen 32 darabot. Amint ezt a számot eléri a legelők száma, töröljük a MouseListenert.
     **/
    public void buildMapUI(Graph map1, GameLogic gameLogic) {
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (map1.getNum() >= 32) {
                        map.removeMouseListener(this);
                        gameLogic.setTurn(0);
                        placeTowers(gameLogic, map1);
                    } else {
                        for (Map.Entry<Field, Polygon> p : polygons.entrySet()) {
                            if (p.getValue().contains(e.getX(), e.getY())) {
                                /*Temporálisan hozzáadjuk előtte*/
                                checkNeighbors(p.getValue(), map1);

                                boolean isValidStep = false;
                                for (int i = 0; i < map1.getGraph().size(); i++) {
                                    for (Map.Entry<Integer, Field> entry : p.getKey().getNeighbors().entrySet()) {
                                        if (map1.getGraph().get(i).getCoordinate().equals(entry.getValue().getCoordinate())) {
                                            isValidStep = true;         //Tehát szabályos a lépés, mert van szomszédja
                                            break;
                                        }
                                    }
                                }

                                /*Ha nem szabályos a lépés, akkor töröljük a szomszédeit*/
                                if (!isValidStep) {
                                    p.getKey().deleteNeighbors();
                                }

                                /*Legelső legelő lerakása, ha üres a gráf bárhova tehetünk*/
                                if (map1.getGraph().isEmpty()) {
                                    Graphics2D g = (Graphics2D) map.getGraphics();
                                    g.setColor(new Color(0, 172, 0));
                                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f);
                                    g.setComposite(ac);
                                    g.fillPolygon(p.getValue());
                                    map1.addField(p.getKey());
                                    checkNeighbors(p.getValue(), map1);
                                    break;

                                    /*A többi lerakása, ahol már szükséges, hogy ne legyen még benne a gráfban és hogy szabályos lépés legyen */
                                } else if (!map1.getGraph().contains(p.getKey()) && isValidStep) {
                                    Graphics2D g = (Graphics2D) map.getGraphics();
                                    g.setColor(new Color(0, 172, 0));
                                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f);
                                    g.setComposite(ac);
                                    g.fillPolygon(p.getValue());
                                    map1.addField(p.getKey());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * A játék harmadik fázisa,
     * Kezdeti báránytornyok lehelyezéséért felelős MouseListenert hozzuk létre, mely a játékosok jobb-klikkjére lehelyez 1-1 tornyot.
     * Ha újra a kezdőjátékosra kerül a sor, akkor indulhat a játék, hiszen mindkét játékos lehelyezte a tornyait, így töröljük a MouseListenert
     **/
    public void placeTowers(GameLogic game, Graph graph) {
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    for (Map.Entry<Field, Polygon> p : polygons.entrySet()) {
                        if (p.getValue().contains(e.getX(), e.getY()) && graph.getGraph().contains(p.getKey()) && p.getKey().getPlayer().getPlayerID() == -1) {
                            Graphics2D g = (Graphics2D) map.getGraphics();
                            g.setColor(game.getTurn().getColor());
                            g.fillPolygon(p.getValue());
                            g.setColor(Color.BLACK);
                            g.drawString(Integer.toString(16), p.getValue().xpoints[5] + 50, p.getValue().ypoints[4] - 43);
                            p.getKey().setPlayer(game.getTurn(), 16);
                            game.nextPlayer();
                            break;
                        }
                    }
                    if (game.getTurn().getPlayerID() == 1) {
                        map.removeMouseListener(this);
                        map.repaint();
                        realGame(graph, game);
                    }
                }
            }
        });
    }

    /**
     * A játék negyedik, fő fázisa.
     * A valódi játék lefolytatása során két MouseListenert alkalmazunk.
     * Az elsővel kijelöli a soron következő játékos, hogy melyik mezőről és hány bárányt szeretne áthelyezni.
     * Ha ezt megtette, akkor a második a játékos által kijelölt mezőre helyez megfelelő számú bárányt, majd a következő játékoson a sor.
     **/
    public void realGame(Graph graph, GameLogic gameLogic) {
        map.addMouseListener(new MouseAdapter() {
            /* Ezt bármikor újra végrehajthatjuk, amíg nem helyeztünk le bárányokat a körünkben! */
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Map.Entry<Field, Polygon> p : polygons.entrySet()) {
                    for (Field field : graph.getGraph()) {
                        if (p.getValue().contains(e.getX(), e.getY()) && p.getKey().getCoordinate().equals(field.getCoordinate())) {
                            if (p.getKey().getPlayer().equals(gameLogic.getTurn()) && p.getKey().getSheepNumber() > 1) {
                                /* OptionPane alkotása */
                                Object[] possib = new Object[p.getKey().getSheepNumber() - 1];
                                int i = 0;
                                int number = 1;
                                while (i < possib.length) {
                                    possib[i] = number;
                                    i++;
                                    number++;
                                }
                                Integer chosenNum = (Integer) JOptionPane.showInputDialog(map, "Mennyi bárányt szeretnél áthelyezni?", "Bárányok áthelyezése", JOptionPane.QUESTION_MESSAGE, null, possib, 1);
                                if (chosenNum != null) {
                                    gameLogic.setMoveSheeps(chosenNum);
                                }
                                /*kiinduló mezők mentése későbbre*/
                                gameLogic.setSource(p.getKey());
                                gameLogic.setSourcePoly(p.getValue());
                            }
                            break;
                        }
                    }
                }
            }
        });

        /* A kijelölt mezőre helyezi a megadott számú bárányokat a megfelelő játékosnak */
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*Elmentjük, hogy visszaléphessünk ha akarunk*/
                save();
                gameLogic.getArchive().save();
                gameLogic.save();
                graph.save();

                /* Csak akkor lehessen helyezni, ha már kijelöltünk egy induló pontot */
                if (gameLogic.getSource() != null) {
                    for (Map.Entry<Field, Polygon> pIn : polygons.entrySet()) {
                        if (pIn.getValue().contains(e.getX(), e.getY()) && graph.getGraph().contains(pIn.getKey()) && pIn.getKey().getSheepNumber() == 0) {
                            for (Map.Entry<Integer, Field> iterator : gameLogic.getSource().getNeighbors().entrySet()) {
                                if (graph.getStep(iterator.getValue(), iterator.getKey()).equals(pIn.getKey())) {
                                    gameLogic.getSource().setSheepNumber(gameLogic.getSource().getSheepNumber() - gameLogic.getMoveSheeps());
                                    pIn.getKey().setPlayer(gameLogic.getTurn(), gameLogic.getMoveSheeps());
                                    Graphics2D g = (Graphics2D) map.getGraphics();
                                    g.setColor(gameLogic.getTurn().getColor());
                                    /*régi mező kitöltésének frissítése*/
                                    g.fillPolygon(gameLogic.getSourcePoly());
                                    /* új mező kitöltése: */
                                    g.fillPolygon(pIn.getValue());

                                    /* Bárányok számának frissítése */
                                    g.setColor(Color.BLACK);
                                    g.drawString(Integer.toString(pIn.getKey().getSheepNumber()), pIn.getValue().xpoints[5] + 50, pIn.getValue().ypoints[4] - 43);
                                    g.drawString(Integer.toString(gameLogic.getSource().getSheepNumber()), gameLogic.getSourcePoly().xpoints[5] + 50, gameLogic.getSourcePoly().ypoints[4] - 43);

                                    /*következő kör:*/
                                    gameLogic.nextPlayer();

                                    /* Játék végének ellenőrzése */
                                    if (gameLogic.endGame(graph)) {
                                        Player winner = gameLogic.getWinner(graph);

                                        /*Döntetlen esetén*/
                                        if (winner == null) {
                                            JOptionPane.showMessageDialog(map,
                                                    "Döntetlen\nPontok:\n" + gameLogic.getPlayer(0).getName() + " " + gameLogic.getPlayer(0).getScore() + "\n" +
                                                            gameLogic.getPlayer(1).getName() + " " + gameLogic.getPlayer(1).getScore(),
                                                    "Vége a játéknak!", JOptionPane.INFORMATION_MESSAGE);

                                        }

                                        /*Ha valamelyik játékos nyert */
                                        else {
                                            JOptionPane.showMessageDialog(map, "A győztes: " + winner.getName() + "\nPontok:\n" +
                                                            gameLogic.getPlayer(0).getName() + " " + gameLogic.getPlayer(0).getScore() + "\n" +
                                                            gameLogic.getPlayer(1).getName() + " " + gameLogic.getPlayer(1).getScore(),
                                                    "Vége a játéknak", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }

                                    /* Ha a soron következő játékos nem tud lépni, akkor újra az előző jön */
                                    if (!graph.playerCanMove(gameLogic.getTurn())) {
                                        gameLogic.nextPlayer();
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * A táblán lévő hatszögek csúcsainak környezetének meghatározásában segíti a számításokat.
     **/
    public boolean checkRange(int a, int b) {
        return Math.abs(a - b) <= 2;
    }

    /**
     * Megvizsgálja egy hatszög szomszédjait a gráfban, a csúcsainak segítségével, mind a hat oldalra.
     * Ha valamelyik oldalon talál szomszédot, akkor azt a mező szomszédei közé adja.
     **/
    public void checkNeighbors(Polygon polygon, Graph graph) {

        for (Map.Entry<Field, Polygon> I : polygons.entrySet()) {
            if (I.getValue().equals(polygon)) {
                for (Map.Entry<Field, Polygon> iInside : polygons.entrySet()) {
                    if (graph.getGraph().contains(iInside.getKey()) && !iInside.equals(I)) {
                        //1-4 oldalak
                        if (checkRange(I.getValue().xpoints[0], iInside.getValue().xpoints[4]) &&
                                checkRange(I.getValue().ypoints[0], iInside.getValue().ypoints[4]) &&
                                checkRange(I.getValue().ypoints[1], iInside.getValue().ypoints[3]) &&
                                checkRange(I.getValue().xpoints[1], iInside.getValue().xpoints[3])) {
                            I.getKey().addNeighbor(1, iInside.getKey());
                            iInside.getKey().addNeighbor(4, I.getKey());
                        }
                        //2-5 oldalak
                        if (checkRange(I.getValue().xpoints[1], iInside.getValue().xpoints[5]) &&
                                checkRange(I.getValue().ypoints[1], iInside.getValue().ypoints[5]) &&
                                checkRange(I.getValue().ypoints[2], iInside.getValue().ypoints[4]) &&
                                checkRange(I.getValue().xpoints[2], iInside.getValue().xpoints[4])) {
                            I.getKey().addNeighbor(2, iInside.getKey());
                            iInside.getKey().addNeighbor(5, I.getKey());
                        }
                        //3-6 oldalak
                        if (checkRange(I.getValue().xpoints[2], iInside.getValue().xpoints[0]) &&
                                checkRange(I.getValue().ypoints[2], iInside.getValue().ypoints[0]) &&
                                checkRange(I.getValue().ypoints[3], iInside.getValue().ypoints[5]) &&
                                checkRange(I.getValue().xpoints[3], iInside.getValue().xpoints[5])) {
                            I.getKey().addNeighbor(3, iInside.getKey());
                            iInside.getKey().addNeighbor(6, I.getKey());
                        }
                        //4-1 oldalak
                        if (checkRange(I.getValue().xpoints[3], iInside.getValue().xpoints[1]) &&
                                checkRange(I.getValue().ypoints[3], iInside.getValue().ypoints[1]) &&
                                checkRange(I.getValue().ypoints[4], iInside.getValue().ypoints[0]) &&
                                checkRange(I.getValue().xpoints[4], iInside.getValue().xpoints[0])) {
                            I.getKey().addNeighbor(4, iInside.getKey());
                            iInside.getKey().addNeighbor(1, I.getKey());
                        }
                        //5-2 oldalak
                        if (checkRange(I.getValue().xpoints[4], iInside.getValue().xpoints[2]) &&
                                checkRange(I.getValue().ypoints[4], iInside.getValue().ypoints[2]) &&
                                checkRange(I.getValue().ypoints[5], iInside.getValue().ypoints[1]) &&
                                checkRange(I.getValue().xpoints[5], iInside.getValue().xpoints[1])) {
                            I.getKey().addNeighbor(5, iInside.getKey());
                            iInside.getKey().addNeighbor(2, I.getKey());
                        }
                        //6-3 oldalak
                        if (checkRange(I.getValue().xpoints[5], iInside.getValue().xpoints[3]) &&
                                checkRange(I.getValue().ypoints[5], iInside.getValue().ypoints[3]) &&
                                checkRange(I.getValue().ypoints[0], iInside.getValue().ypoints[2]) &&
                                checkRange(I.getValue().xpoints[0], iInside.getValue().xpoints[2])) {
                            I.getKey().addNeighbor(6, iInside.getKey());
                            iInside.getKey().addNeighbor(3, I.getKey());
                        }
                    }
                }
                return;
            }
        }
    }

    /**
     * Elmenti a játéktábla állását szerializásással a playmap fájlba
     **/
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream("playmap");
            ObjectOutputStream ous = new ObjectOutputStream(fos);
            ous.writeObject(this);
            ous.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /**
     * Betölti a megadott fájlból a játéktábla állását és azt meg is jeleníti.
     **/
    public void load(GameLogic gl, Graph gr) {
        /* A gráfbeli mezőket bele másoljuk a megfelelő hatszögek mellé, hogy kesőbb egyszerűbb legyen az azonosításuk */
        HashMap<Field, Polygon> tmp = new HashMap<>(polygons);
        for (Map.Entry<Field, Polygon> pmap : getPolygons().entrySet()) {
            for (Field field : gr.getGraph()) {
                if (field.getCoordinate().equals(pmap.getKey().getCoordinate())) {
                    Polygon tmpPolygon = tmp.remove(pmap.getKey());
                    tmp.put(field, tmpPolygon);
                    break;
                }
            }
        }

        /* El se mentjük a játékosokat, hanem a gráfból szedjük össze őket*/
        for (Field field : gr.getGraph()) {
            if (field.getPlayer().getPlayerID() == 1 && !gl.getPlayer(0).equals(field.getPlayer())) {
                gl.removePlayer(1);
                gl.setPlayer(field.getPlayer());
            } else if (field.getPlayer().getPlayerID() == 2 && !gl.getPlayer(1).equals(field.getPlayer())) {
                gl.removePlayer(2);
                gl.setPlayer(field.getPlayer());
            }
        }
        gl.setTurn(gl.getTURN());
        polygons = tmp;

        /* Megjelenítés */
        map = new MapPanel(this, gr);
        add(map, BorderLayout.CENTER);
        this.setVisible(true);

        /* Kirajzolás után, pedig folytatódjon a játék */
        realGame(gr, gl);
    }

    /**
     * Visszaadja a tábla mezőket és hatszögeket párosító tárolóját
     **/
    public HashMap<Field, Polygon> getPolygons() {
        return polygons;
    }
}
