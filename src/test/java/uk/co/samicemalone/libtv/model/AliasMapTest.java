package uk.co.samicemalone.libtv.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AliasMapTest {

    private String show;
    private String alias;
    private AliasMap map;

    @Before
    public void setUp() throws Exception {
        map = new AliasMap();
        show = "It's Always Sunny In Philadelphia";
        alias = "Its Always Sunny In Philadelphia";
    }

    @Test
    public void testAddAlias() throws Exception {
        map.addAlias(show, alias);
        assertEquals(alias, map.getShowAlias(show));
    }

    @Test
    public void testHasAlias() throws Exception {
        map.addAlias(show, alias);
        assertTrue(map.hasAlias(show));
    }

}