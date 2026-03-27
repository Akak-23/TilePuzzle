/**
 * A rotatable tile for the puzzle game.
 * 
 * Each tile represents a piece of the path that can be rotated 0-3 times.
 * Uses the existing image naming convention where rotation is indicated by 0-3 suffix.
 * 
 * Connections are represented as bits: 0=right, 1=down, 2=left, 3=up
 * The tile type determines the base connection pattern (before rotation is applied).
 */
class RotatableTile extends GameObject
{
    private final char type;
    private int rotation = 0;
    private final String baseName;
    private final int baseConnections;
    
    // Connection patterns (as bitmask: 0=right, 1=down, 2=left, 3=up)
    private static final int ENDPOINT = 0b0001;   // only right (will be rotated)
    private static final int CORNER = 0b0011;     // right and down (0 and 1) - will be rotated
    private static final int STRAIGHT = 0b0101;  // right and left (0 and 2)
    private static final int THREE_WAY = 0b1011; // right, down, up (0, 1, 3) - missing left, matches t-0
    private static final int FOUR_WAY = 0b1111;  // all directions
    
    RotatableTile(final int x, final int y, final char type)
    {
        super(x, y, 0, "grass");
        this.type = type;
        this.rotation = (int)(Math.random() * 4); // Randomize initial rotation
        
        // Determine base name and connection pattern based on type
        if (type == 'p' || type == 'P') {
            baseName = "path-x";
            baseConnections = FOUR_WAY;
        } else if (type == 'x' || type == 'X') {
            baseName = "path-x";
            baseConnections = FOUR_WAY;
        } else if (type == 'i' || type == 'I') {
            baseName = "path-i";
            baseConnections = STRAIGHT;
        } else if (type == 'e' || type == 'E') {
            baseName = "path-e";
            baseConnections = ENDPOINT;  // Only 1 connection (right), rotated based on tile rotation
        } else if (type == 'l' || type == 'L') {
            baseName = "path-l";
            baseConnections = CORNER;  // 2 connections at right angle (right and down)
        } else if (type == 't' || type == 'T') {
            baseName = "path-t";
            baseConnections = THREE_WAY;  // 3 connections: right, down, left (rotated)
        } else if (type == 'G') {
            baseName = "path-x";
            baseConnections = FOUR_WAY;  // Goal is a 4-way junction
        } else {
            baseName = "grass";
            baseConnections = 0;
        }
        
        updateImage();
    }
    
    /**
     * Rotate this tile 90 degrees clockwise.
     */
    void rotate()
    {
        rotation = (rotation + 1) % 4;
        updateImage();
    }
    
    /**
     * Update the displayed image based on current rotation.
     * Different tile types have different numbers of rotation variants:
     * - path-x: 1 image (symmetrical in all directions)
     * - path-i: 2 images (0=horizontal, 1=vertical, 2 mirrors 0, 3 mirrors 1)
     * - path-e: 4 images (0, 1, 2, 3) - one connection each
     * - path-l: 4 images (0, 1, 2, 3) - corner pieces
     * - path-t: 4 images (0, 1, 2, 3) - three connections each
     */
    private void updateImage()
    {
        if (baseConnections == 0) {
            setImage("grass");
        } else if (baseName.equals("path-x")) {
            // path-x (four-way) is symmetrical, no rotation needed
            setImage(baseName);
        } else if (baseName.equals("path-i")) {
            // path-i (straight) only has 2 orientations: horizontal (0) and vertical (1)
            // Rotations 2 and 3 map back to 0 and 1
            final int imageIndex = rotation % 2;
            setImage(baseName + "-" + imageIndex);
        } else {
            // path-e, path-l, and path-t have all 4 rotation variants
            setImage(baseName + "-" + rotation);
        }
    }
    
    /**
     * Check if this tile has a connection in a given direction.
     * @param direction 0=right, 1=down, 2=left, 3=up
     */
    boolean hasConnection(final int direction)
    {
        if (baseConnections == 0) return false;
        
        final int rotatedDir = (direction - rotation + 4) % 4;
        return ((baseConnections >> rotatedDir) & 1) == 1;
    }
    
    char getType()
    {
        return type;
    }
}
