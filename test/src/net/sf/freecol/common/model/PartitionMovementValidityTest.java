package net.sf.freecol.common.model;

import net.sf.freecol.server.model.ServerUnit;
import net.sf.freecol.util.test.FreeColTestCase;

public class PartitionMovementValidityTest extends FreeColTestCase {

    private static final TileType plains = spec().getTileType("model.tile.plains");
    private static final TileType ocean  = spec().getTileType("model.tile.ocean");

    private static final UnitType colonistType = spec().getUnitType("model.unit.freeColonist");
    private static final UnitType galleonType  = spec().getUnitType("model.unit.galleon");

    /**
     * P1 — Land unit → Land tile (moves > 0): allowed
     * Representative: A land unit on a land tile attempts to move to an adjacent land tile
     * with at least one movement point remaining.
     */
    public void testP1_LandToLand_MovesPositive_IsAllowed() {
        Game game = getStandardGame();
        Map map = getTestMap(plains, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");

        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(plains);
        to.setType(plains);

        // Keep consistent with existing tests: mark explored.
        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit colonist = new ServerUnit(game, from, dutch, colonistType);

        Unit.MoveType mt = colonist.getMoveType(from, to, 3);
//        Unit.MoveType mt = colonist.getMoveType(from, to, 0);

        assertTrue("Expected legal movement (MOVE) for land->land with moves>0, got: " + mt,
                mt.isLegal());
        assertEquals("Expected MOVE for land->land with moves>0",
                Unit.MoveType.MOVE, mt);
    }

    /**
     * P2 — Land unit → Ocean tile (moves > 0): rejected
     * Representative: A land unit on a coastal land tile attempts to move to an adjacent ocean tile
     * with movement points available.
     */
    public void testP2_LandToOcean_MovesPositive_IsRejected() {
        Game game = getStandardGame();
        Map map = getTestMap(plains, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");

        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(plains);
        to.setType(ocean);

        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit colonist = new ServerUnit(game, from, dutch, colonistType);

        Unit.MoveType mt = colonist.getMoveType(from, to, 3);
        assertFalse("Expected illegal movement for land->ocean, got: " + mt,
                mt.isLegal());
        // Existing tests show the specific rejection type for land embarking attempt.
        assertEquals("Land unit moving to ocean should be MOVE_NO_ACCESS_EMBARK",
                Unit.MoveType.MOVE_NO_ACCESS_EMBARK, mt);
    }

    /**
     * P3 — Naval unit → Ocean tile (moves > 0): allowed
     * Representative: A naval unit on an ocean tile attempts to move to an adjacent ocean tile
     * with movement points available.
     */
    public void testP3_NavalToOcean_MovesPositive_IsAllowed() {
        Game game = getStandardGame();
        Map map = getTestMap(plains, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");

        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(ocean);
        to.setType(ocean);

        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit galleon = new ServerUnit(game, from, dutch, galleonType);

        Unit.MoveType mt = galleon.getMoveType(from, to, 3);
        assertTrue("Expected legal movement (MOVE) for naval->ocean with moves>0, got: " + mt,
                mt.isLegal());
        assertEquals("Expected MOVE for naval->ocean with moves>0",
                Unit.MoveType.MOVE, mt);
    }

    /**
     * Boundary — moves = 0: rejected
     * Representative: A unit with zero remaining movement points attempts to move to an adjacent tile.
     */
    public void testBoundary_MovesZero_IsRejected() {
        Game game = getStandardGame();
        Map map = getTestMap(plains, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");

        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(plains);
        to.setType(plains);

        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit colonist = new ServerUnit(game, from, dutch, colonistType);

        Unit.MoveType mt = colonist.getMoveType(from, to, 0);
        assertFalse("Expected illegal movement when moves=0, got: " + mt,
                mt.isLegal());
        assertEquals("Expected MOVE_NO_MOVES when moves=0",
                Unit.MoveType.MOVE_NO_MOVES, mt);
    }
}
