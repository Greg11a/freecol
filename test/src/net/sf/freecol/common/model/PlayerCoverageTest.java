package net.sf.freecol.common.model;

import net.sf.freecol.util.test.FreeColTestCase;

import java.util.List;

public class PlayerCoverageTest extends FreeColTestCase {

    private TileType plains() {
        return spec().getTileType("model.tile.plains");
    }

    private TileType ocean() {
        TileType t = spec().getTileType("model.tile.ocean");
        // if can't find ocean then find highseas
        if (t == null) t = spec().getTileType("model.tile.highSeas");
        return t;
    }

//    private Player getAnyOtherPlayer(Game game, Player self) {
//        return game.getPlayers(p -> true)
//                .filter(p -> p != null && p != self)
//                .findFirst()
//                .orElseThrow(() -> new AssertionError("Could not find a second player in standard game."));
//    }

    private Player getAnyNativePlayer(Game game) {
        return game.getPlayers(p -> true)
                .filter(p -> p != null && p.isIndian())
                .findFirst()
                .orElse(null);
    }


    // modifyGold() coverage

    public void testModifyGold_NotAccounted_NoChange() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        // Force "unknown gold" state (GOLD_NOT_ACCOUNTED)
        dutch.setGold(Player.GOLD_NOT_ACCOUNTED);
        dutch.modifyGold(100);

        assertEquals("Gold should remain GOLD_NOT_ACCOUNTED when not accounted.",
                Player.GOLD_NOT_ACCOUNTED, dutch.getGold());
    }

    public void testModifyGold_Positive_Adds() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        dutch.setGold(100);
        dutch.modifyGold(50);

        assertEquals("Gold should increase by amount when accounted.", 150, dutch.getGold());
    }

    public void testModifyGold_Negative_ClampsToZero() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        dutch.setGold(10);
        dutch.modifyGold(-999);

        assertEquals("Gold should clamp to 0 when modification would make it negative.", 0, dutch.getGold());
    }


    // getLandPrice()

    public void testGetLandPrice_OwnerNull_IsFree() {
        Game game = getStandardGame();
        Map map = getTestMap(plains(), true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        Tile t = map.getTile(5, 8);
        t.setType(plains());
        t.setOwner(null);

        int price = dutch.getLandPrice(t);
        assertEquals("Unowned land should have price 0.", 0, price);
    }

    public void testGetLandPrice_OwnerSelf_IsFree() {
        Game game = getStandardGame();
        Map map = getTestMap(plains(), true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        Tile t = map.getTile(5, 8);
        t.setType(plains());
        t.setOwner(dutch);

        int price = dutch.getLandPrice(t);
        assertEquals("Own land should have price 0.", 0, price);
    }

    public void testGetLandPrice_NativeOwner_IsNonNegative() {
        Game game = getStandardGame();
        Map map = getTestMap(plains(), true);
        game.changeMap(map);

        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        Player nativePlayer = getAnyNativePlayer(game);

        Tile t = map.getTile(5, 8);
        t.setType(plains());
        t.setOwner(nativePlayer);

        int price = dutch.getLandPrice(t);

        assertTrue("Native-owned land should not yield a negative price.", price >= 0);


    }

    public void testCheckGold_Branches() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        // GOLD_NOT_ACCOUNTED
        dutch.setGold(Player.GOLD_NOT_ACCOUNTED);
        assertTrue(dutch.checkGold(9999));

        // enough gold
        dutch.setGold(100);
        assertTrue(dutch.checkGold(100));
        assertTrue(dutch.checkGold(50));

        // not enough gold
        assertFalse(dutch.checkGold(200));
    }

    public void testAIFlag_SetAndGet() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        dutch.setAI(true);
        assertTrue(dutch.isAI());

        dutch.setAI(false);
        assertFalse(dutch.isAI());
    }

    public void testScore_SetAndGet() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        dutch.setScore(1234);
        assertEquals(1234, dutch.getScore());

        dutch.setScore(0);
        assertEquals(0, dutch.getScore());
    }


    // Are we be able to set up colonies?
    public void testGetAllColonyValues_WaterTile_EarlyReturn() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        Map map = getTestMap(plains(), true);
        game.changeMap(map);

        TileType water = ocean();
        assertNotNull("Need an ocean/highSeas TileType in spec.", water);

        Tile t = map.getTile(5, 8);
        t.setType(water);
        t.setOwner(null);
        t.setSettlement(null);

        List<Double> values = dutch.getAllColonyValues(t);
        assertNotNull(values);
        assertTrue(values.size() >= Player.ColonyValueCategory.values().length);

        // Player-Line 3729
        /**
         *         case TERRAIN: case WATER:
         *             values.set(ColonyValueCategory.A_OVERRIDE.ordinal(),
         *                        NoValueType.TERRAIN.getDouble());
         *             return values;
         * **/
        assertEquals(Player.NoValueType.TERRAIN.getDouble(),
                values.get(Player.ColonyValueCategory.A_OVERRIDE.ordinal()));
    }

    public void testGetAllColonyValues_PlainsTile_NormalPath_ReturnsValues() {
        Game game = getStandardGame();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        assertNotNull(dutch);

        Map map = getTestMap(plains(), true);
        game.changeMap(map);

        Tile t = map.getTile(5, 8);
        t.setType(plains());
        t.setOwner(null);
        t.setSettlement(null);

        List<Double> values = dutch.getAllColonyValues(t);
        assertNotNull(values);
        assertTrue(values.size() >= Player.ColonyValueCategory.values().length);

        Double override = values.get(Player.ColonyValueCategory.A_OVERRIDE.ordinal());
        assertNotNull(override);
    }

}
