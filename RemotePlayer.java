/**
 * Diese Klasse repräsentiert einen Spieler, dessen Eingaben über eine
 * Netzwerkverbindung an einen Server gesendet werden.
 *
 * Der RemotePlayer führt die Bewegung lokal aus und überträgt
 * die gedrückte Taste an einen entfernten Empfänger, um die
 * Synchronisation mit anderen Spielern zu ermöglichen.
 *
 * @author Arya Kumar Subhankar
 */
import java.net.*;
import java.io.*;
import static java.awt.event.KeyEvent.*;

class RemotePlayer extends Player {

    /** Socket zur Verbindung mit dem Server */
    private Socket socket;

    /** Ausgabestream zum Senden von Tasteneingaben */
    private DataOutputStream out;

    /** Zuletzt gedrückte Taste */
    private int lastKey;

    /**
     * Erzeugt einen neuen RemotePlayer und baut eine Verbindung
     * zu einem Server auf dem lokalen Rechner auf.
     *
     * @param x Startposition auf der X-Achse
     * @param y Startposition auf der Y-Achse
     * @param rotation Anfangsrotation des Spielers
     * @param field Spielfeld, auf dem sich der Spieler bewegt
     */
    RemotePlayer(int x, int y, int rotation, Field field) {
        super(x, y, rotation, field);
        try {
            socket = new Socket("localhost", 6666);
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Führt eine Spieleraktion aus.
     *
     * Wartet auf eine Tasteneingabe, simuliert die Bewegung lokal
     * und sendet die gedrückte Taste an den Server.
     */
    @Override
    void act() {
        lastKey = getNextKey();
        simulateKey(lastKey);

        try {
            out.writeInt(lastKey);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simuliert eine Tasteneingabe, indem die entsprechende Bewegung
     * direkt auf den Spieler angewendet wird.
     *
     * @param key Gedrückte Taste (Pfeiltasten)
     */
    private void simulateKey(int key) {
        if (key == VK_RIGHT && canWalk(0)) {
            setRotation(0);
            setLocation(getX() + 1, getY());
        } else if (key == VK_DOWN && canWalk(1)) {
            setRotation(1);
            setLocation(getX(), getY() + 1);
        } else if (key == VK_LEFT && canWalk(2)) {
            setRotation(2);
            setLocation(getX() - 1, getY());
        } else if (key == VK_UP && canWalk(3)) {
            setRotation(3);
            setLocation(getX(), getY() - 1);
        }
    }

    /**
     * Setzt die Sichtbarkeit des Spielers.
     *
     * Wird der Spieler unsichtbar gemacht, wird zusätzlich
     * die Netzwerkverbindung geschlossen.
     *
     * @param visible true, wenn der Spieler sichtbar sein soll
     */
    @Override
    void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
