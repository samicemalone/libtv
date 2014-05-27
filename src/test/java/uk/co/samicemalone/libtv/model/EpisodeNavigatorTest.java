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

import java.util.Arrays;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.co.samicemalone.libtv.FileSystemEnvironment;
import uk.co.samicemalone.libtv.MockFileSystem;
import uk.co.samicemalone.libtv.matcher.TVEpisodeMatcher;
import uk.co.samicemalone.libtv.matcher.path.StandardTVPath;
import uk.co.samicemalone.libtv.matcher.path.TVPath;

/**
 *
 * @author Sam Malone
 */
public class EpisodeNavigatorTest extends FileSystemEnvironment {
    
    private EpisodeNavigator episode;
    
    @Before
    public void setUp() {
        TVPath tvPath = new StandardTVPath(MockFileSystem.getMockRoot());
        episode = new EpisodeNavigator(new TVEpisodeMatcher(tvPath), tvPath);
    }
    
    @After
    public void tearDown() {
        episode = null;
    }

    /**
     * Test of navigate method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateCurrent() {
        EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", 1, 5);
        EpisodeMatch expResult = new EpisodeMatch(1, 5);
        EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.CUR);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());        
    }

    /**
     * Test of navigate method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateNext() {
        EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", 1, 5);
        EpisodeMatch expResult = new EpisodeMatch(1, 6);
        EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.NEXT);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());  
    }

    /**
     * Test of navigate method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateNextDoubleEp() {
        EpisodeMatch toNavigate = new EpisodeMatch("The Walking Dead", 1, 2);
        EpisodeMatch expResult = new EpisodeMatch(1, 4);
        EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.NEXT);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());  
    }

    /**
     * Test of navigate method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigatePrevious() {
        EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", 1, 6);
        EpisodeMatch expResult = new EpisodeMatch(1, 5);
        EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.PREV);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());  
    }
    

    /**
     * Test of navigate method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigatePreviousDoubleEp() {
        String show = "The Walking Dead";
        // the mock episode 1x02 is a double (ep 2 and 3).
        EpisodeMatch toNavigate = new EpisodeMatch(show, 1, 4);
        EpisodeMatch expResult = new EpisodeMatch(show, 1, Arrays.asList(2, 3));
        EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.PREV);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
        toNavigate = new EpisodeMatch(expResult);
        expResult = new EpisodeMatch(1, 1);
        result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.PREV);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
    }

    /**
     * Test of navigateSeason method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateSeasonPrevious() {
        EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", 3, 1);
        EpisodeMatch expResult = new EpisodeMatch(2, 12);
        EpisodeMatch result = episode.navigateSeason(toNavigate, EpisodeNavigator.Pointer.PREV);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
    }
    
    /**
     * Test of navigateSeason method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateSeasonNext() {
        EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", 2, 12);
        EpisodeMatch expResult = new EpisodeMatch(3, 1);
        EpisodeMatch result = episode.navigateSeason(toNavigate, EpisodeNavigator.Pointer.NEXT);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
    }
    
    /**
     * Test of navigateSeason method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateShowAscending() {
        for(int i = 1; i <= MockFileSystem.NUM_SEASONS; i++) {
            for(int j = 1; j <= MockFileSystem.NUM_EPISODES; j++) {
                int season = (j == MockFileSystem.NUM_EPISODES) ? i + 1 : i;
                int expEp = (j == MockFileSystem.NUM_EPISODES) ? 1 : j + 1;
                EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", i, j);
                EpisodeMatch expResult = new EpisodeMatch(season, expEp);
                EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.NEXT);
                if(result == null && i == MockFileSystem.NUM_SEASONS && j == MockFileSystem.NUM_EPISODES) {
                    return;
                }
                System.out.println("Navigating: " + toNavigate + " : to : " + expResult);
                assertEquals(expResult.getSeason(), result.getSeason());
                assertEquals(expResult.getEpisodes(), result.getEpisodes());
            }
        }
    }
    
    /**
     * Test of navigateSeason method, of class EpisodeNavigatorTest.
     */
    @Test
    public void testNavigateShowDescending() {
        for(int i = MockFileSystem.NUM_SEASONS; i > 0; i--) {
            for(int j = MockFileSystem.NUM_EPISODES; j > 0; j--) {
                int season = (j == 1) ? i - 1 : i;
                int expEp = (j == 1) ? MockFileSystem.NUM_EPISODES : j - 1;
                EpisodeMatch toNavigate = new EpisodeMatch("Scrubs", i, j);
                EpisodeMatch expResult = new EpisodeMatch(season, expEp);
                EpisodeMatch result = episode.navigate(toNavigate, EpisodeNavigator.Pointer.PREV);
                if(result == null && i == 1 && j == 1) {
                    return;
                }
                System.out.println("Navigating: " + toNavigate + " : to : " + expResult);
                assertEquals(expResult.getSeason(), result.getSeason());
                assertEquals(expResult.getEpisodes(), result.getEpisodes());
            }
        }
    }
    
}

