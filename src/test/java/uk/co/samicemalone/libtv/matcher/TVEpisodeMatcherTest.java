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

package uk.co.samicemalone.libtv.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.samicemalone.libtv.FileSystemEnvironment;
import uk.co.samicemalone.libtv.MockFileSystem;
import uk.co.samicemalone.libtv.exception.EpisodePathNotFoundException;
import uk.co.samicemalone.libtv.matcher.path.StandardTVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.EpisodeRange;
import uk.co.samicemalone.libtv.model.Range;
import uk.co.samicemalone.libtv.model.Season;
import static uk.co.samicemalone.libtv.util.EpisodeTestUtil.assertEpisodeMatchEquals;
import static uk.co.samicemalone.libtv.util.EpisodeTestUtil.assertSeasonEquals;

/**
 *
 * @author Sam Malone
 */
public class TVEpisodeMatcherTest extends FileSystemEnvironment {
    
    private TVEpisodeMatcher tvMatcher;
    private StandardTVPath tvPath;
    
    @Before
    public void setUp() {
        tvPath = new StandardTVPath(MockFileSystem.getMockRoot());
        tvMatcher = new TVEpisodeMatcher(tvPath);
    }
    
    @After
    public void tearDown() {
        tvPath = null;
        tvMatcher = null;
    }

    /**
     * Test of matchEpisode method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test(expected = EpisodePathNotFoundException.class)
    public void testMatchEpisodeThrow() throws IOException {
        tvMatcher.matchEpisode("Scrubs", 0, 1);
    }

    /**
     * Test of matchEpisode method, of class TVMatcher.
     */
    @Test
    public void testMatchEpisode() {
        String show = "Scrubs";
        int s = 1;
        Season season = new Season(s, tvPath.getEpisodesPath(show, s));
        File expFile = MockFileSystem.getEpisodeFile(show, s, 4);
        EpisodeMatch expResult = new EpisodeMatch(show, s, 4);
        expResult.setEpisodeFile(expFile);
        EpisodeMatch result = tvMatcher.matchEpisode(season.getPath(), 4);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of matchLargestSeasonEpisode method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test(expected = EpisodePathNotFoundException.class)
    public void testMatchLargestEpisodeThrow() throws IOException {
        tvMatcher.matchLargestEpisode("Scrubs", 0);
    }

    /**
     * Test of matchLargestSeasonEpisode method, of class TVMatcher.
     */
    @Test
    public void testMatchLargestEpisode() {
        String show = "Scrubs";
        int s = 1;
        Season season = new Season(s, tvPath.getEpisodesPath(show, s));
        File expFile = MockFileSystem.getEpisodeFile(show, s, MockFileSystem.NUM_EPISODES);
        EpisodeMatch expResult = new EpisodeMatch(show, s, MockFileSystem.NUM_EPISODES);
        expResult.setEpisodeFile(expFile);
        EpisodeMatch result = tvMatcher.matchLargestEpisode(season.getPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of matchLatestEpisode method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchLatestEpisode() throws IOException {
        String show = "Scrubs";
        int season = MockFileSystem.NUM_SEASONS;
        int episode = MockFileSystem.NUM_EPISODES;
        File expFile = MockFileSystem.getEpisodeFile(show, season, episode);
        EpisodeMatch result = tvMatcher.matchLatestEpisode(show);
        assertEquals(season, result.getSeason());
        assertEquals(expFile, result.getEpisodeFile());
        assertTrue(result.isEpisodeNo(episode));
    }

    /**
     * Test of matchSeason method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchSeason() throws IOException {
        String show = "Scrubs";
        int season = 1;
        List<EpisodeMatch> result = tvMatcher.matchSeason(show, season);
        assertEquals(MockFileSystem.NUM_EPISODES, result.size());
        for(int i = 0; i < MockFileSystem.NUM_EPISODES; i++) {
            assertEquals(season, result.get(i).getSeason());
            assertTrue(result.get(i).isEpisodeNo(i+1));
            assertEquals(MockFileSystem.getEpisodeFile(show, season, i+1), result.get(i).getEpisodeFile());
        }
    }

    /**
     * Test of matchSeasonRange method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchSeasonRange() throws IOException {
        String show = "Scrubs";
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, 2, 3);
        List<EpisodeMatch> result = tvMatcher.matchSeasonRange(show, new Range(2, 3));
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchSeasonsFrom method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchSeasonsFrom() throws IOException {
        String show = "Scrubs";
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, 2, MockFileSystem.NUM_SEASONS);
        List<EpisodeMatch> result = tvMatcher.matchSeasonsFrom(show, 2);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchSeasons method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchSeasons() throws IOException {
        String show = "Scrubs";
        List<Season> expResult = new ArrayList<>(MockFileSystem.NUM_SEASONS);
        for(int i = 1; i <= MockFileSystem.NUM_SEASONS; i++) {
            expResult.add(new Season(i, tvPath.getEpisodesPath(show, i)));
        }
        List<Season> result = tvMatcher.matchSeasons(tvPath.getSeasonsPath(show));
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertSeasonEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchLargestSeasonSeason method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchLargestSeason() throws IOException {
        String show = "Scrubs";
        int s = MockFileSystem.NUM_SEASONS;
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, s, s);
        List<EpisodeMatch> result = tvMatcher.matchLargestSeason(show);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchLargestSeason method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchLargest() throws IOException {
        String show = "Scrubs";
        Path seasonPath = tvPath.getSeasonsPath(show);
        int season = MockFileSystem.NUM_SEASONS;
        Season expResult = new Season(season, tvPath.getEpisodesPath(show, season));
        Season result = tvMatcher.matchLargestSeason(tvMatcher.matchSeasons(seasonPath));
        assertSeasonEquals(expResult, result);
    }

    /**
     * Test of matchEpisodesFrom method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchEpisodesFrom() throws IOException {
        String show = "Scrubs";
        int season = 1;
        int episode = 9;
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 9));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 10));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 11));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 12));
        List<EpisodeMatch> result = tvMatcher.matchEpisodesFrom(show, season, episode);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchEpisodeRange method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchEpisodeRange() throws IOException {
        String show = "Scrubs";
        EpisodeRange range = new EpisodeRange(1, 10, 2, 2);
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 10));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 11));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 12));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 2, 1));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 2, 2));
        List<EpisodeMatch> result = tvMatcher.matchEpisodeRange(show, range);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchAllEpisodes method, of class TVMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchAllEpisodes() throws IOException {
        String show = "Scrubs";
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, 1, MockFileSystem.NUM_SEASONS);
        List<EpisodeMatch> result = tvMatcher.matchAllEpisodes(show);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }
    
    
}
