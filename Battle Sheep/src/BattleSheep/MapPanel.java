package BattleSheep;

import javax.swing.*;
import java.awt.*;

/**
 * A játék rajzolását megvalósító osztály
 **/
public class MapPanel extends JPanel {
    /**
     * Az ablak, amelyen a JPanel megjelenik majd
     **/
    PlayMap playMap;

    /**
     * A játék gráfja, hogy könnyen végezhessen számításokat
     **/
    Graph graph;

    /**
     * A BattleSheep.MapPanel konstruktora, amely az attribútumokat inicializálja
     **/
    public MapPanel(PlayMap rhsPlayMap, Graph g) {
        playMap = rhsPlayMap;
        graph = g;
    }

    /**
     * A rajzolásért felelős felülírt függvény, ami minden esetben kirajzolja gráf tartalmát a mezőkre
     **/
    @Override
    protected void paintComponent(Graphics g3) {
        Graphics2D g2 = (Graphics2D) g3;
        super.paintComponent(g2);
        g2.setColor(Color.green);

        /* Az üres hatszögek kirajzolása */
        for (java.util.Map.Entry<Field, Polygon> polygon : playMap.getPolygons().entrySet()) {
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g2.drawPolygon(polygon.getValue());
        }

        /* Ezután kirajzoljuk a színezett mezőket */
        for (java.util.Map.Entry<Field, Polygon> p : playMap.getPolygons().entrySet()) {
            for (Field field : graph.getGraph()) {
                /* Ha valaki birtokolja már a mezőt: */
                if (field.getCoordinate().equals(p.getKey().getCoordinate())) {
                    if (p.getKey().getPlayer().getPlayerID() != -1) {
                        g2.setColor(p.getKey().getPlayer().getColor());
                        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f);
                        g2.setComposite(ac);
                        g2.fillPolygon(p.getValue());
                        g2.setColor(Color.BLACK);
                        g2.drawString(Integer.toString(p.getKey().getSheepNumber()), p.getValue().xpoints[5] + 50, p.getValue().ypoints[4] - 43);
                    }
                    /* Ha nem áll senki a mezőn:*/
                    else {
                        g2.setColor(new Color(0, 172, 0));
                        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f);
                        g2.setComposite(ac);
                        g2.fillPolygon(p.getValue());
                    }
                    break;
                }
            }
        }

    }
}

