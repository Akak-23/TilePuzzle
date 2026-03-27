import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class Level
{
    enum PlayerMode {
        LOCAL,
        REMOTE,
        CONTROLLED
    }

    private final Field field;
    private final List<Actor> actors = new ArrayList<>();
    private final List<GameObject> gameObjects = new ArrayList<>();

    Level(final String fileName, PlayerMode mode)
    {
        final List<String> lines;
        try (final BufferedReader stream =
                 new BufferedReader(new InputStreamReader(Game.Jar.getInputStream(fileName)))) {
            lines = stream.lines().toList();
        }
        catch (final FileNotFoundException e) {
            throw new IllegalArgumentException("Level '" + fileName + "' wurde nicht gefunden.");
        }
        catch (final IOException e) {
            throw new IllegalArgumentException("Fehler beim Lesen des Levels '" + fileName + "'.");
        }

        field = new Field(lines.toArray(new String[lines.size()]));

        final Player player;
        switch (mode) {
            case LOCAL:
                player = new Player(-1, 0, 0, field);
                break;
            case REMOTE:
                player = new RemotePlayer(-1, 0, 0, field);
                break;
            case CONTROLLED:
                player = new ControlledPlayer(-1, 0, 0, field);
                break;
            default:
                throw new IllegalStateException("Unbekannter PlayerMode");
        }

        actors.add(player);
        gameObjects.add(player);

        IntStream.iterate(0, y -> y < lines.size(), y -> y + 2)
                .forEach(y -> IntStream.iterate(0, x -> x < lines.get(y).length(), x -> x + 2)
                .forEach(x -> {

            final int index = "pPqQlLiIcCdDsSzZGbBWO ".indexOf(lines.get(y).charAt(x));

            if (index == -1) {
                throw new IllegalArgumentException(
                        "Unbekanntes Symbol '" + lines.get(y).charAt(x)
                        + "' in Level '" + fileName + "', Zeile " + (y + 1)
                        + ", Spalte " + (x + 1));
            }
            else if(index < 4) {
                if (player.getX() != -1) {
                    throw new IllegalArgumentException(
                            "Zweite Spielfigur in Level '" + fileName + "' gefunden.");
                }
                player.setLocation(x / 2, y / 2);
                player.setRotation(index);
            }
            else if (index < 16) {
                final String[] images = {"laila", "claudius", "child"};
                final Actor actor = new Walker(
                        x / 2, y / 2, index % 4,
                        images[index / 4 - 1],
                        field, player
                );
                actors.add(actor);
                gameObjects.add(actor);
            }
            else if (index < 19) {
                final String[] images = {"goal", "bridge-0", "bridge-1"};
                gameObjects.add(new GameObject(x / 2, y / 2, 0, images[index - 16]));
            }
        }));

        if (player.getX() == -1) {
            throw new IllegalArgumentException(
                    "Keine Spielfigur in Level '" + fileName + "' gefunden");
        }
    }

    List<Actor> getActors()
    {
        return actors;
    }

    void hide()
    {
        gameObjects.forEach(gameObject -> gameObject.setVisible(false));
        field.hide();
    }
}
