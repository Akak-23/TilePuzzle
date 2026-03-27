/**
 * A clever puzzle game where players must rotate map tiles to create a valid path.
 * 
 * The player is presented with a grid of scrambled path tiles that must be rotated
 * to form a continuous path from the start (marked with 'p') to the goal (marked with 'G').
 * 
 * Controls:
 * - Arrow keys: Move the cursor to select a tile
 * - SPACE: Rotate the selected tile clockwise
 * - When a valid solution is found, the player moves through the path automatically
 */
public class TileRotationGame extends Game
{
    private static final String[] LEVEL_FILES = {
        "levels/puzzle.lvl",
        "levels/2.lvl",
        "levels/3.lvl"
    };

    public static void main()
    {
        while (true) {
            final TilePuzzleMenu.MenuChoice choice = TilePuzzleMenu.showMenu();

            if (choice.action == TilePuzzleMenu.ACTION_INSTRUCTIONS) {
                // Instructions selected
                TilePuzzleMenu.showInstructions();
            }
            else if (choice.action == TilePuzzleMenu.ACTION_QUIT) {
                // Quit selected
                break;
            }
            else {
                // Start game with selected level
                playLevels(choice.levelIndex);
                break;
            }
        }
    }

    private static void playLevels(final int startLevel)
    {
        for (int levelIndex = startLevel;
             levelIndex < LEVEL_FILES.length;
             levelIndex++) {

            playLevel(LEVEL_FILES[levelIndex]);
        }
    }

    private static void playLevel(final String levelFile)
    {
        final TilePuzzleLevel level = new TilePuzzleLevel(levelFile);

        // Start input handling in a separate thread
        final Thread inputThread = new Thread(() -> {
            while (level.getPlayer().isVisible()) {
                level.handleInput();
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();

        // Game loop: continue until player reaches the goal
        while (level.getPlayer().isVisible()) {
            level.act();
            sleep(100);
        }

        level.hide();
        sleep(2000);  // Pause before next level
    }
}
