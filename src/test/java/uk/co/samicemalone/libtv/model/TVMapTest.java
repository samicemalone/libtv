/*
 * Copyright (c) 2014, Sam Malone. All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of Sam Malone nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package uk.co.samicemalone.libtv.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sam Malone
 */
public class TVMapTest {
    
    public TVMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addEpisode method, of class TVMap.
     */
    @Test
    public void testAddEpisode() {
        EpisodeMatch e = new EpisodeMatch("24", 1, 1);
        TVMap instance = new TVMap();
        instance.addEpisode(e);
        assertTrue(instance.getSeasonEpisodes("24", 1).contains(e));
        assertEquals(1, instance.getSeasonCount("24"));
    }

    /**
     * Test of contains method, of class TVMap.
     */
    @Test
    public void testContains() {
        EpisodeMatch episode = new EpisodeMatch("24", 1, 1);
        TVMap instance = new TVMap();
        instance.addEpisode(episode);
        assertTrue(instance.contains(episode));
        episode = new EpisodeMatch("24", 1, 2);
        assertFalse(instance.contains(episode));
        instance.addEpisode(episode);
        assertTrue(instance.contains(episode));
        instance.removeEpisode(episode);
        assertFalse(instance.contains(episode));
    }

    /**
     * Test of getShows method, of class TVMap.
     */
    @Test
    public void testGetShows() {
        TVMap instance = new TVMap();
        instance.addEpisode(new EpisodeMatch("24", 1, 1));
        instance.addEpisode(new EpisodeMatch("Scrubs", 2, 1));
        List<String> expResult = Arrays.asList("24", "Scrubs");
        Set<String> result = instance.getShows();
        assertTrue(result.containsAll(expResult));
        assertEquals(2, result.size());
    }

    /**
     * Test of getEpisodes method, of class TVMap.
     */
    @Test
    public void testGetEpisodes() {
        String show = "24";
        EpisodeMatch eOne = new EpisodeMatch(show, 1, 1);
        EpisodeMatch eTwo = new EpisodeMatch(show, 1, 2);
        TVMap instance = new TVMap();
        instance.addEpisode(eOne);
        instance.addEpisode(eTwo);
        List<EpisodeMatch> expResult = Arrays.asList(eOne, eTwo);
        Set<EpisodeMatch> result = instance.getEpisodes(show);
        assertTrue(result.containsAll(expResult));
        assertEquals(2, result.size());
    }

    /**
     * Test of getSeasonCount method, of class TVMap.
     */
    @Test
    public void testGetSeasonCount() {
        String show = "24";
        TVMap instance = new TVMap();
        List<EpisodeMatch> list = Arrays.asList(
            new EpisodeMatch(show, 1, 1),
            new EpisodeMatch(show, 2, 1),
            new EpisodeMatch(show, 3, 1)
        );
        for(int i = 0; i < list.size(); i++) {
            instance.addEpisode(list.get(i));
            assertEquals(i + 1, instance.getSeasonCount(show));
        }
        for(int i = list.size() - 1; i >= 0; i--) {
            instance.removeEpisode(list.get(i));
            assertEquals(i, instance.getSeasonCount(show));
        }
    }

    /**
     * Test of getSeasonCount method, of class TVMap.
     */
    @Test
    public void testGetSeasonCount_String_boolean() {
        String show = "24";
        TVMap instance = new TVMap();
        instance.addEpisode(new EpisodeMatch(show, AbstractTVEpisode.NO_SEASON, 1));
        instance.addEpisode(new EpisodeMatch(show, 1, 1));
        instance.addEpisode(new EpisodeMatch(show, 2, 1));
        assertEquals(2, instance.getSeasonCount(show, false));
        assertEquals(3, instance.getSeasonCount(show, true));
    }

    /**
     * Test of getSeasonEpisodes method, of class TVMap.
     */
    @Test
    public void testGetSeasonEpisodes() {
        String show = "24";
        int season = 1;
        EpisodeMatch eOne = new EpisodeMatch(show, 1, 1);
        EpisodeMatch eTwo = new EpisodeMatch(show, 2, 2);
        TVMap instance = new TVMap();
        instance.addEpisode(eOne);
        instance.addEpisode(eTwo);
        List<EpisodeMatch> expResult = Arrays.asList(eOne);
        Set<EpisodeMatch> result = instance.getSeasonEpisodes(show, season);
        assertTrue(result.containsAll(expResult));
        assertEquals(1, result.size());
    }

    /**
     * Test of replaceEpisode method, of class TVMap.
     */
    @Test
    public void testReplaceEpisode() {
        EpisodeMatch old = new EpisodeMatch("24", 1, 1);
        EpisodeMatch replacement = new EpisodeMatch("24", 2, 1);
        TVMap instance = new TVMap();
        instance.addEpisode(old);
        assertTrue(instance.contains(old));
        assertFalse(instance.contains(replacement));
        instance.replaceEpisode(old, replacement);
        assertFalse(instance.contains(old));
        assertTrue(instance.contains(replacement));
    }

    /**
     * Test of removeAll method, of class TVMap.
     */
    @Test
    public void testRemoveAll() {
        EpisodeMatch eOne = new EpisodeMatch("24", 1, 1);
        EpisodeMatch eTwo = new EpisodeMatch("24", 2, 2);        
        EpisodeMatch eThree = new EpisodeMatch("24", 3, 3);        
        List<EpisodeMatch> toRemove = Arrays.asList(eOne, eTwo);
        TVMap instance = new TVMap();
        instance.addEpisode(eOne);
        instance.addEpisode(eTwo);
        instance.addEpisode(eThree);
        instance.removeAll(toRemove);
        assertFalse(instance.contains(eOne));
        assertFalse(instance.contains(eTwo));
        assertTrue(instance.contains(eThree));
    }

    /**
     * Test of removeShow method, of class TVMap.
     */
    @Test
    public void testRemoveShow() {
        String show = "24";
        TVMap instance = new TVMap();
        instance.addEpisode(new EpisodeMatch(show, 1, 2));
        instance.addEpisode(new EpisodeMatch(show, 1, 3));
        instance.addEpisode(new EpisodeMatch("Scrubs", 2, 1));
        instance.removeShow(show);
        assertFalse(instance.getShows().contains(show));
    }

    /**
     * Test of removeEpisode method, of class TVMap.
     */
    @Test
    public void testRemoveEpisode() {
        EpisodeMatch episode = new EpisodeMatch("24", 1, 2);
        TVMap instance = new TVMap();
        instance.addEpisode(episode);
        assertTrue(instance.contains(episode));
        assertEquals(1, instance.getSeasonCount("24"));
        assertEquals(1, instance.getShowCount());
        instance.removeEpisode(episode);
        assertFalse(instance.contains(episode));
        assertEquals(0, instance.getSeasonCount("24"));
        assertEquals(0, instance.getShowCount());
    }

    /**
     * Test of removeEpisodeSubset method, of class TVMap.
     */
    @Test
    public void testRemoveEpisodeSubset() {
        EpisodeMatch episode = new EpisodeMatch("24", 1, new ArrayList<>(Arrays.asList(1, 2)));
        TVMap instance = new TVMap();
        instance.addEpisode(episode);
        instance.removeEpisodeSubset(new EpisodeMatch("24", 1, 2));
        assertTrue(instance.contains(new EpisodeMatch("24", 1, 1)));
    }

    /**
     * Test of getShowCount method, of class TVMap.
     */
    @Test
    public void testGetShowCount() {
        TVMap instance = new TVMap();        
        List<EpisodeMatch> list = Arrays.asList(
            new EpisodeMatch("24", 1, 1),
            new EpisodeMatch("Scrubs", 1, 1),
            new EpisodeMatch("Friends", 1, 1)
        );
        for(int i = 0; i < list.size(); i++) {
            instance.addEpisode(list.get(i));
            assertEquals(i + 1, instance.getShowCount());
        }
        for(int i = list.size() - 1; i >= 0; i--) {
            instance.removeShow(list.get(i).getShow());
            assertEquals(i, instance.getShowCount());
        }
    }
    
}
