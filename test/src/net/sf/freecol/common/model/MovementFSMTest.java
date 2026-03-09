package net.sf.freecol.common.model;

import net.sf.freecol.server.model.ServerUnit;
import net.sf.freecol.util.test.FreeColTestCase;

/**
 * States:
 * S0 — Ready
 * Everything initialized and set up.
 * S1 — Allowed
 * Movement is Legal
 * S2 — Rejected
 * Movement denied due to 0 remaining movement points.
 * S3 — Rejected_EmbarkDenied
 * Movement denied due to Land Unit attempts to enter ocean tile.
 * S5 — Rejected_LandAccessDenied
 * Movement denied due to Naval Unit attempts to enter land tile.
 *
 * **/

/**
 * G1: land → land AND moves > 0
 * G2: land → ocean AND moves > 0
 * G3: naval → ocean AND moves > 0
 * G4: naval → land AND moves > 0
 * G5: moves = 0
 * **/
/**
 * FSM-based tests for move validity classification.
 * Model states correspond to key MoveType outcome categories.
 */
public class MovementFSMTest extends FreeColTestCase {

    private static final TileType PLAINS = spec().getTileType("model.tile.plains");
    private static final TileType OCEAN  = spec().getTileType("model.tile.ocean");

    private static final UnitType COLONIST = spec().getUnitType("model.unit.freeColonist");
    private static final UnitType GALLEON  = spec().getUnitType("model.unit.galleon");

    /**
     * G1: land -> land with moves > 0 => Allowed (MOVE)
     * Covers transition S0 -> S1.
     */
    public void testAllowed_LandToLand_MovesPositive() {
        Game game = getStandardGame();
        Map map = getTestMap(PLAINS, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(PLAINS);
        to.setType(PLAINS);
        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit u = new ServerUnit(game, from, dutch, COLONIST);

        Unit.MoveType mt = u.getMoveType(from, to, 3);
        assertTrue("Expected MOVE to be legal, got: " + mt, mt.isLegal());
        assertEquals(Unit.MoveType.MOVE, mt);
    }

    /**
     * G2: land -> ocean with moves > 0 => Rejected (NO_ACCESS_EMBARK)
     * Covers transition S0 -> S3.
     */
    public void testRejected_LandToOcean_EmbarkDenied() {
        Game game = getStandardGame();
        Map map = getTestMap(PLAINS, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(PLAINS);
        to.setType(OCEAN);
        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit u = new ServerUnit(game, from, dutch, COLONIST);

        Unit.MoveType mt = u.getMoveType(from, to, 3);
        assertFalse("Expected embark-denied move to be illegal, got: " + mt, mt.isLegal());
        assertEquals(Unit.MoveType.MOVE_NO_ACCESS_EMBARK, mt);
    }

    /**
     * G3: naval -> ocean with moves > 0 => Allowed (MOVE)
     * Covers transition S0 -> S1 (naval case).
     */
    public void testAllowed_NavalToOcean_MovesPositive() {
        Game game = getStandardGame();
        Map map = getTestMap(PLAINS, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(OCEAN);
        to.setType(OCEAN);
        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit ship = new ServerUnit(game, from, dutch, GALLEON);

        Unit.MoveType mt = ship.getMoveType(from, to, 3);
        assertTrue("Expected MOVE to be legal for naval->ocean, got: " + mt, mt.isLegal());
        assertEquals(Unit.MoveType.MOVE, mt);
    }

    /**
     * G4: naval -> land with moves > 0 => Rejected (NO_ACCESS_LAND)
     * Covers transition S0 -> S4.
     *
     * NOTE: If your branch uses a different MoveType for this case,
     * adjust the expected enum accordingly after running once.
     */
    public void testRejected_NavalToLand_LandAccessDenied() {
        Game game = getStandardGame();
        Map map = getTestMap(PLAINS, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(OCEAN);
        to.setType(PLAINS);
        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit ship = new ServerUnit(game, from, dutch, GALLEON);

        Unit.MoveType mt = ship.getMoveType(from, to, 3);
        assertFalse("Expected naval->land to be illegal, got: " + mt, mt.isLegal());
        assertEquals(Unit.MoveType.MOVE_NO_ACCESS_LAND, mt);
    }

    /**
     * G5: moves = 0 => Rejected (NO_MOVES)
     * Covers transition S0 -> S2.
     */
    public void testBoundary_Rejected_MovesZero() {
        Game game = getStandardGame();
        Map map = getTestMap(PLAINS, true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        Tile from = map.getTile(5, 8);
        Tile to   = map.getTile(4, 8);

        from.setType(PLAINS);
        to.setType(PLAINS);
        from.setExplored(dutch, true);
        to.setExplored(dutch, true);

        Unit u = new ServerUnit(game, from, dutch, COLONIST);

        Unit.MoveType mt = u.getMoveType(from, to, 0);
        assertFalse("Expected moves=0 to be illegal, got: " + mt, mt.isLegal());
        assertEquals(Unit.MoveType.MOVE_NO_MOVES, mt);
    }
}
