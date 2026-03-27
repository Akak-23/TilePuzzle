import java.net.*;
import java.io.*;
import static java.awt.event.KeyEvent.*;

/**
 * Diese Klasse repräsentiert einen Spieler, der über eine
 * Netzwerkverbindung von einem entfernten Client gesteuert wird.
 *
 * Der ControlledPlayer fungiert als Server, empfängt Tasteneingaben
 * eines RemotePlayers und führt die entsprechenden Bewegungen
 * im Spiel aus.
 *
 * @author Arya Kumar Subhankar
 */
class ControlledPlayer extends Player {

    /** ServerSocket zum Warten auf eine Client-Verbindung */
    private ServerSocket server;

    /** Socket für die Verbindung zum Client */
    private Socket socket;

    /** Eingabestream zum Empfangen von Tasteneingaben */
    private DataInputStream in;

    /**
     * Erzeugt einen neuen ControlledPlayer und wartet auf
     * eine eingehende Verbindung eines Clients.
     *
     * @param x Startposition auf der X-Achse
     * @param y Startposition auf der Y-Achse
     * @param rotation Anfangsrotation des Spielers
     * @param field Spielfeld, auf dem sich der Spieler bewegt
     */
    ControlledPlayer(int x, int y, int rotation, Field field) {
        super(x, y, rotation, field);
        try {
            server = new ServerSocket(6666);
            socket = server.accept();
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Führt eine Spieleraktion aus.
     *
     * Wartet blockierend auf eine Tasteneingabe vom Client
     * und wendet diese auf den Spieler an.
     */
    @Override
    void act() {
        try {
            int key = in.readInt();
            applyKey(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wendet eine empfangene Tasteneingabe auf den Spieler an
     * und führt die entsprechende Bewegung aus.
     *
     * @param key Vom Client gesendete Taste
     */
    private void applyKey(int key) {
        if (key == VK_RIGHT && canWalk(0)) {
            setRotation(0);
            setLocation(getX() + 1, getY());
        }
        else if (key == VK_DOWN && canWalk(1)) {
            setRotation(1);
            setLocation(getX(), getY() + 1);
        }
        else if (key == VK_LEFT && canWalk(2)) {
            setRotation(2);
            setLocation(getX() - 1, getY());
        }
        else if (key == VK_UP && canWalk(3)) {
            setRotation(3);
            setLocation(getX(), getY() - 1);
        }
        else {
            playSound("error");
        }

        playSound("step");
        sleep(200);
    }

    /**
     * Setzt die Sichtbarkeit des Spielers.
     *
     * Wird der Spieler unsichtbar gemacht, werden alle
     * Netzwerkverbindungen ordnungsgemäß geschlossen.
     *
     * @param visible true, wenn der Spieler sichtbar sein soll
     */
    @Override
    void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            try {
                socket.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
