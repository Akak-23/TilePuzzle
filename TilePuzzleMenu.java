import static java.awt.event.KeyEvent.*;

/**
 * Start menu for the Tile Rotation Puzzle game.
 * Displays options to start the game, select level, view instructions.
 */
class TilePuzzleMenu extends Game
{
    static final int ACTION_PLAY = 0;
    static final int ACTION_LEVEL_SELECT = 1;
    static final int ACTION_INSTRUCTIONS = 2;
    static final int ACTION_QUIT = 3;
    
    static class MenuChoice {
        int action;
        int levelIndex;
        
        MenuChoice(final int action, final int levelIndex) {
            this.action = action;
            this.levelIndex = levelIndex;
        }
    }
    
    static MenuChoice showMenu()
    {
        int selection = ACTION_PLAY;
        
        while (true) {
            clearScreen();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   TILE ROTATION PUZZLE GAME - MENU     ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();
            
            String[] options = {"START GAME", "SELECT LEVEL", "INSTRUCTIONS", "QUIT"};
            
            for (int i = 0; i < options.length; i++) {
                if (i == selection) {
                    System.out.println("  ► " + options[i]);
                } else {
                    System.out.println("    " + options[i]);
                }
            }
            
            System.out.println();
            System.out.println("Use UP/DOWN arrows to navigate");
            System.out.println("Press SPACE or ENTER to select");
            System.out.println();
            
            final int key = getNextKey();
            
            if (key == VK_UP) {
                selection = (selection - 1 + 4) % 4;
            } else if (key == VK_DOWN) {
                selection = (selection + 1) % 4;
            } else if (key == VK_SPACE || key == VK_ENTER) {
                if (selection == ACTION_PLAY) {
                    return new MenuChoice(ACTION_PLAY, 0);
                } else if (selection == ACTION_LEVEL_SELECT) {
                    final int level = showLevelSelect();
                    if (level >= 0) {
                        return new MenuChoice(ACTION_PLAY, level);
                    }
                    // Loop back to main menu if cancelled
                } else if (selection == ACTION_INSTRUCTIONS) {
                    showInstructions();
                } else if (selection == ACTION_QUIT) {
                    return new MenuChoice(ACTION_QUIT, 0);
                }
            }
        }
    }
    
    private static int showLevelSelect()
    {
        int selection = 0;
        
        while (true) {
            clearScreen();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║         SELECT A LEVEL                 ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();
            
            String[] levels = {"Level 1", "Level 2", "Level 3"};
            
            for (int i = 0; i < levels.length; i++) {
                if (i == selection) {
                    System.out.println("  ► " + levels[i]);
                } else {
                    System.out.println("    " + levels[i]);
                }
            }
            
            System.out.println();
            System.out.println("Use UP/DOWN arrows to navigate");
            System.out.println("Press SPACE or ENTER to select");
            System.out.println("Press ESC to go back");
            System.out.println();
            
            final int key = getNextKey();
            
            if (key == VK_UP) {
                selection = Math.max(selection - 1, 0);
            } else if (key == VK_DOWN) {
                selection = Math.min(selection + 1, 2);
            } else if (key == VK_SPACE || key == VK_ENTER) {
                return selection;
            } else if (key == VK_ESCAPE) {
                return -1;
            }
        }
    }
    
    static void showInstructions()
    {
        clearScreen();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         HOW TO PLAY                    ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        System.out.println("OBJECTIVE:");
        System.out.println("  Create a valid path from START (p) to");
        System.out.println("  GOAL (G) by rotating tile pieces.");
        System.out.println();
        System.out.println("CONTROLS:");
        System.out.println("  ← → ↑ ↓  - Move cursor to select tile");
        System.out.println("  SPACE    - Rotate selected tile");
        System.out.println();
        System.out.println("HINTS:");
        System.out.println("  - Tiles must connect end-to-end");
        System.out.println("  - When connected properly, you auto-win!");
        System.out.println();
        System.out.println("Press any key to return to menu...");
        getNextKey();
    }    
    
    private static void clearScreen()
    {
        // Print newlines to simulate screen clear
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}