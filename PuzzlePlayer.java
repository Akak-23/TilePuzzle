/**
 * The player character for the tile rotation puzzle.
 * 
 * The player starts at the start tile and must reach the goal tile.
 * Once the puzzle is solved, the player automatically traverses the correct path.
 */
class PuzzlePlayer extends GameObject
{
    private final int startX;
    private final int startY;

    PuzzlePlayer(final int x, final int y, final TilePuzzleLevel level)
    {
        super(x, y, 0, "woman");
        this.startX = x;
        this.startY = y;
    }

    /**
     * Make the player traverse the completed path from start to goal.
     */
    void traversePath(final RotatableTile[][] tiles, final int width, final int height)
    {
        // Find start and goal positions
        int curX = startX, curY = startY;
        int goalX = -1, goalY = -1;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[y][x].getType() == 'G') {
                    goalX = x;
                    goalY = y;
                }
            }
        }

        // Traverse the path using BFS to find the shortest path
        boolean[][] visited = new boolean[height][width];
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        int[][][] parent = new int[height][width][];
        
        queue.add(new int[]{curX, curY});
        visited[curY][curX] = true;

        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        while (!queue.isEmpty()) {
            final int[] pos = queue.poll();
            final int x = pos[0];
            final int y = pos[1];

            if (x == goalX && y == goalY) {
                // Reconstruct and walk the path
                walkPath(parent, curX, curY, x, y);
                setVisible(false);
                return;
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
                            parent[ny][nx] = new int[]{x, y};
                            queue.add(new int[]{nx, ny});
                        }
                    }
                }
            }
        }
    }

    /**
     * Walk the player along the path using the parent array to reconstruct it.
     */
    private void walkPath(final int[][][] parent, final int startX, final int startY,
                          final int goalX, final int goalY)
    {
        // Reconstruct path
        java.util.List<int[]> path = new java.util.ArrayList<>();
        int x = goalX, y = goalY;
        
        while (!(x == startX && y == startY)) {
            path.add(0, new int[]{x, y});
            final int[] p = parent[y][x];
            x = p[0];
            y = p[1];
        }
        path.add(0, new int[]{startX, startY});

        // Walk the path
        for (int i = 0; i < path.size(); i++) {
            final int[] pos = path.get(i);
            setLocation(pos[0], pos[1]);
            
            // Calculate rotation based on direction to next position
            if (i < path.size() - 1) {
                final int[] nextPos = path.get(i + 1);
                final int dx = nextPos[0] - pos[0];
                final int dy = nextPos[1] - pos[1];
                
                // Determine direction: 0=right, 1=down, 2=left, 3=up
                int direction = 0;
                if (dx > 0) {
                    direction = 0;  // right
                } else if (dy > 0) {
                    direction = 1;  // down
                } else if (dx < 0) {
                    direction = 2;  // left
                } else if (dy < 0) {
                    direction = 3;  // up
                }
                
                setRotation(direction);
            }
            
            Game.playSound("step");
            Game.sleep(300);
        }
    }
}
