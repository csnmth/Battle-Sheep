package BattleSheep;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
/**
 * A Főmenüt megvalósító osztály, amelyből a játék funkciói érhetőek el.
 **/
public class MainMenu extends JFrame {
    /**
     * A JPanel, amelyen megjelenítjük a éppen aktuális kártyát.
     **/
    private JPanel mainPane;
    /**
     * A mainPane Layoutja, amelyen cserélgetjk az éppen látható kártyát
     **/
    public CardLayout cardLayout = new CardLayout();


    /**
     * A menü ablak konstruktora, csak a címet adja neki
     **/
    public MainMenu() {
        super("Main menu");
    }

    /**
     * A menü kirajzolását hajtja végre, valamint létrehozza az új játék kártyáját is
     **/
    public void Init(GameLogic gameLogic) {
        setSize(250, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mainPane = new JPanel(cardLayout);
        initMenu(gameLogic);
        add(mainPane);

        JMenuBar backBar = new JMenuBar();
        JMenu back = new JMenu("Vissza");
        JMenuItem b = new JMenuItem("Vissza a főmenübe!");
        b.addActionListener(e -> cardLayout.show(mainPane, "menu"));
        back.add(b);
        backBar.add(back);
        this.setJMenuBar(backBar);


        cardLayout.show(mainPane, "menu");
        setVisible(true);

        initnewgPane(gameLogic);

    }

    /**
     * Játékosfelvétel kártya kirajzolása
     **/
    public void initnewgPane(GameLogic gameLogic) {
        JPanel newg = new JPanel();
        JLabel name1 = new JLabel("Játékos 1 név:");
        JTextField nameTF1 = new JTextField(20);
        JLabel name2 = new JLabel("Játékos 2 név:");
        JTextField nameTF2 = new JTextField(20);

        /* A játékosoknak választaniuk kell maguknak egy színt*/
        JColorChooser jColorChooser = new JColorChooser();
        jColorChooser.remove(1);
        Color[] colors = new Color[2];
        jColorChooser.getSelectionModel().addChangeListener(e -> {
            Color playerColor = jColorChooser.getColor();
            if (colors[0] == null) {
                colors[0] = playerColor;
            } else if (colors[1] == null){
                colors[1] = playerColor;
            }
        });

        /* Ha ugyanazt a színt választják ki, vagy nem választanak, akkor piros és kék lesz*/
        JButton OK = new JButton("START");
        OK.addActionListener(e -> {
            if (!nameTF1.getText().equals("") && !nameTF2.getText().equals("")) {
                if (colors[0] == null) {
                    colors[0] = Color.RED;
                }
                gameLogic.setPlayer(new Player(nameTF1.getText(), 1, colors[0]));
                if (colors[1] == null) {
                    colors[1] = Color.BLUE;
                }
                gameLogic.setPlayer(new Player(nameTF2.getText(), 2, colors[1]));
                cardLayout.show(mainPane, "menu");
                Graph gr = new Graph();
                PlayMap pm = new PlayMap(gr, gameLogic, this);
                pm.drawEmptyMap(gr);
                pm.setVisible(true);
                pm.buildMapUI(gr, gameLogic);
                setVisible(false);
                nameTF2.setText("");
                nameTF1.setText("");
            }
        });

        newg.add(name1);
        newg.add(nameTF1);
        newg.add(name2);
        newg.add(nameTF2);
        newg.add(OK);
        newg.add(jColorChooser);

        mainPane.add(newg, "newg");
    }

    /**
     * Eredmények kártya megalkotása
     **/
    public void initResults(ResultArchive resultArchive) {
        JPanel table = new JPanel();
        JTable results = new JTable(resultArchive);
        results.setFillsViewportHeight(true);
        results.setRowSorter(new TableRowSorter<>(results.getModel()));
        JScrollPane js = new JScrollPane(results);
        table.add(js);

        mainPane.add(table, "table");
    }

    /**
     * Főmenü kártya megalkotása
     **/
    public void initMenu(GameLogic gl) {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(200, 500));
        menu.setLayout(new GridLayout(5, 1));

        JLabel title = new JLabel("Főmenü", SwingConstants.CENTER);
        title.setForeground(Color.GREEN);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        menu.add(title);

        JButton menu1 = new JButton("Új játék");
        menu1.setForeground(Color.GREEN);
        menu1.setFont(new Font("Times New Roman", Font.BOLD, 20));
        menu.add(menu1);
        menu1.addActionListener(e -> cardLayout.show(mainPane, "newg"));

        JButton menu2 = new JButton("Eredmények");
        menu2.setForeground(Color.GREEN);
        menu2.setFont(new Font("Times New Roman", Font.BOLD, 20));
        menu.add(menu2);
        menu2.addActionListener(e -> {
            initResults(gl.getArchive());
            cardLayout.show(mainPane, "table");
        });

        JButton menu3 = new JButton("Játék betöltése");
        menu3.setForeground(Color.GREEN);
        menu3.setFont(new Font("Times New Roman", Font.BOLD, 20));
        menu.add(menu3);
        menu3.addActionListener(e -> {
            load();
            this.setVisible(false);
        });

        JButton exit = new JButton("Kilépés");
        exit.setForeground(Color.RED);
        exit.setFont(new Font("Times New Roman", Font.BOLD, 20));
        menu.add(exit);
        exit.addActionListener(e -> {
            gl.getArchive().save();
            System.exit(0);
        });

        JScrollPane js = new JScrollPane(menu);
        mainPane.add(js, "menu");

    }

    /**
     * A fájlból visszatöltést hajtja végre, ami visszalépéskor és játék betöltésekor hajtódik végre
     **/
    public void load() {
        Graph ujG = null;
        PlayMap ujPM = null;
        GameLogic ujGL = null;
        try {
            FileInputStream fis2 = new FileInputStream("graph");
            ObjectInputStream ois2 = new ObjectInputStream(fis2);
            ujG = (Graph) ois2.readObject();

            /*nem biztos hogy kell*/
            FileInputStream fis1 = new FileInputStream("gamelogic");
            ObjectInputStream ois1 = new ObjectInputStream(fis1);
            ujGL = (GameLogic) ois1.readObject();

            FileInputStream fis = new FileInputStream("playmap");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ujPM = (PlayMap) ois.readObject();

        } catch (IOException | ClassNotFoundException ioE) {
            System.out.println(ioE);
        }

        ujGL.setArchive();
        ujPM.initPlayMap(ujG, ujGL, this);
        ujPM.load(ujGL, ujG);
    }
       /**
     * A fő JPanelt adja vissza.
     * **/
    public JPanel getMainPane(){
        return mainPane;
    }
}
