import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.awt.event.KeyEvent.*;

/**
 * Manages the tile rotation puzzle level.
 * 
 * The level consists of a grid of RotatableTiles that the player must rotate
 * to form a complete path from start to goal. The level handles tile selection,
 * rotation, and path validation.
 */
class TilePuzzleLevel
{
    private final RotatableTile[][] tiles;
    private final int width;
    private final int height;
    private final PuzzlePlayer player;
    private final List<GameObject> gameObjects = new ArrayList<>();
    private int selectedX = 0;
    private int selectedY = 0;
    private boolean pathComplete = false;
    private GameObject selector;

    TilePuzzleLevel(final String fileName)
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

        // Parse level using Level.java format (every other row/column)
        // Odd rows contain connection info, even rows contain tiles
        int maxX = 0;
        for (final String line : lines) {
            maxX = Math.max(maxX, line.length());
        }
        width = (maxX + 1) / 2;  // Every other character is a tile
        height = (lines.size() + 1) / 2;  // Every other row is a tile row
        
        tiles = new RotatableTile[height][width];
        int startX = 0, startY = 0;

        // Parse tiles from tile rows (rows 0, 2, 4, ...)
        for (int tileY = 0; tileY < height; tileY++) {
            final int lineIdx = tileY * 2;
            if (lineIdx >= lines.size()) break;
            
            final String line = lines.get(lineIdx);
            for (int tileX = 0; tileX < width; tileX++) {
                final int charIdx = tileX * 2;
                if (charIdx >= line.length()) break;
                
                final char cell = line.charAt(charIdx);
                final RotatableTile tile = new RotatableTile(tileX, tileY, cell);
                tiles[tileY][tileX] = tile;
                gameObjects.add(tile);
                
                // Add goal overlay if this is the goal tile
                if (cell == 'G') {
                    gameObjects.add(new GameObject(tileX, tileY, 0, "goal"));
                }
                
                if (cell == 'p') {
                    startX = tileX;
                    startY = tileY;
                }
            }
        }

        // Create the player
        player = new PuzzlePlayer(startX, startY, this);
        gameObjects.add(player);
        
        // Create selector overlay
        selector = new GameObject(selectedX, selectedY, 0, "indicator");
        gameObjects.add(selector);
    }

    /**
     * Process one game tick: handle input and check if path is complete.
     */
    void act()
    {
        // Check if path is complete
        if (!pathComplete && isPathComplete()) {
            pathComplete = true;
            playSound("goal");
            // Let player traverse the path
            player.traversePath(tiles, width, height);
        }
    }

    /**
     * Handle keyboard input (runs in separate thread).
     */
    void handleInput()
    {
        final int key = getNextKey();
        
        if (key == VK_RIGHT) {
            selectedX = Math.min(selectedX + 1, width - 1);
            selector.setLocation(selectedX, selectedY);
        }
        else if (key == VK_LEFT) {
            selectedX = Math.max(selectedX - 1, 0);
            selector.setLocation(selectedX, selectedY);
        }
        else if (key == VK_DOWN) {
            selectedY = Math.min(selectedY + 1, height - 1);
            selector.setLocation(selectedX, selectedY);
        }
        else if (key == VK_UP) {
            selectedY = Math.max(selectedY - 1, 0);
            selector.setLocation(selectedX, selectedY);
        }
        else if (key == VK_SPACE) {
            // Rotate selected tile
            if (selectedY < height && selectedX < width) {
                tiles[selectedY][selectedX].rotate();
                playSound("step");
            }
        }
    }

    /**
     * Check if a valid path exists from start to goal.
     */
    private boolean isPathComplete()
    {
        // Find start and goal positions
        int startX = -1, startY = -1, goalX = -1, goalY = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x].getType() == 'p') {
                    startX = x;
                    startY = y;
                } else if (tiles[y][x].getType() == 'G') {
                    goalX = x;
                    goalY = y;
                }
            }
        }

        if (startX == -1 || goalX == -1) return false;

        // BFS to check if path exists
        boolean[][] visited = new boolean[height][width];
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        while (!queue.isEmpty()) {
            final int[] pos = queue.poll();
            final int x = pos[0];
            final int y = pos[1];

            if (x == goalX && y == goalY) {
                return true;
            }

            final RotatableTile currentTile = tiles[y][x];
            for (int dir = 0; dir < 4; dir++) {
                if (currentTile.hasConnection(dir)) {
                    final int nx = x + directions[dir][0];
                    final int ny = y + directions[dir][1];

                    if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[ny][nx]) {
                        final RotatableTile neighbor = tiles[ny][nx];
                        final int oppositeDir = (dir + 2) % 4;
                        
                        if (neighbor.hasConnection(oppositeDir)) {
                            visited[ny][nx] = true;
                            queue.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }

        return false;
    }

    GameObject getPlayer()
    {
        return player;
    }

    void hide()
    {
        gameObjects.forEach(gameObject -> gameObject.setVisible(false));
    }

    private static int getNextKey()
    {
        return GameObject.Canvas.getInstance().getNextKey();
    }

    private static void playSound(final String fileName)
    {
        Game.playSound(fileName);
    }
}
