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
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.co.samicemalone.libtv.FileSystemEnvironment;
import uk.co.samicemalone.libtv.MockFileSystem;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.Range;
import static uk.co.samicemalone.libtv.util.EpisodeTestUtil.assertEpisodeMatchEquals;

/**
 *
 * @author Sam Malone
 */
public class EpisodeMatcherTest extends FileSystemEnvironment {
    
    private EpisodeMatcher episodeMatcher;
    
    @Before
    public void setUp() {
        episodeMatcher = new EpisodeMatcher();
    }
    
    @After
    public void tearDown() {
        episodeMatcher = null;
    }

    /**
     * Test of match method, of class EpisodeMatcher.
     */
    @Test
    public void testMatchFile() {
        Path path = MockFileSystem.getEpisodePath("Scrubs", 1, 2);
        EpisodeMatch expResult = new EpisodeMatch("Scrubs", 1, 2);
        expResult.setEpisodeFile(path.toFile());
        EpisodeMatch result = episodeMatcher.match(path);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class EpisodeMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchFileInSeason() throws IOException {
        List<Path> paths = MockFileSystem.getFullSeasonEpisodes("Scrubs", new Range(1));
        int episodeNo = 4;
        File file = MockFileSystem.getEpisodeFile("Scrubs", 1, episodeNo);
        EpisodeMatch expResult = new EpisodeMatch("Scrubs", 1, episodeNo);
        expResult.setEpisodeFile(file);
        EpisodeMatch result = episodeMatcher.match(paths, episodeNo);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class EpisodeMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchFilesInSeason() throws IOException {
        List<Path> paths = MockFileSystem.getFullSeasonEpisodes("Scrubs", new Range(1));
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches("Scrubs", new Range(1));
        List<EpisodeMatch> result = episodeMatcher.match(paths);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchRange method, of class EpisodeMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchRange() throws IOException {
        String show = "Scrubs";
        List<Path> paths = MockFileSystem.getFullSeasonEpisodes(show, new Range(1));
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 4));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 5));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 6));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 7));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 8));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 9));
        List<EpisodeMatch> result = episodeMatcher.matchRange(paths, new Range(4, 9));
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchFrom method, of class EpisodeMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchFrom() throws IOException {
        String show = "Scrubs";
        List<Path> paths = MockFileSystem.getFullSeasonEpisodes(show, new Range(1));
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 10));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 11));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 12));
        List<EpisodeMatch> result = episodeMatcher.matchFrom(paths, 10);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchLargest method, of class EpisodeMatcher.
     * @throws java.io.IOException
     */
    @Test
    public void testMatchLargest() throws IOException {
        String show = "Scrubs";
        List<Path> paths = MockFileSystem.getFullSeasonEpisodes(show, new Range(1));
        Path path = MockFileSystem.getEpisodePath(show, 1, MockFileSystem.NUM_EPISODES);
        EpisodeMatch expResult = new EpisodeMatch(show, 1, MockFileSystem.NUM_EPISODES);
        expResult.setEpisodeFile(path.toFile());
        EpisodeMatch result = episodeMatcher.matchLargest(paths);
        assertEpisodeMatchEquals(expResult, result);
    }
    
}
