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
package uk.co.samicemalone.libtv.matcher.path;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.co.samicemalone.libtv.FileSystemEnvironment;
import uk.co.samicemalone.libtv.MockFileSystem;
import uk.co.samicemalone.libtv.exception.EpisodePathNotFoundException;
import uk.co.samicemalone.libtv.model.Range;
import uk.co.samicemalone.libtv.model.Season;
import static uk.co.samicemalone.libtv.util.EpisodeTestUtil.assertSeasonEquals;

/**
 *
 * @author Sam Malone
 */
public class StandardTVPathTest extends FileSystemEnvironment {
        
    /**
     * Test of getShowsPath method, of class StandardTVPath.
     */
    @Test
    public void testGetShowPath() {
        StandardTVPath instance = new StandardTVPath(MockFileSystem.getMockRoot());
        Path expResult = MockFileSystem.getMockRoot();
        Path result = instance.getShowsPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSeasonsPath method, of class StandardTVPath.
     */
    @Test
    public void testGetSeasonsPath() {
        String show = "Scrubs";
        StandardTVPath instance = new StandardTVPath(MockFileSystem.getMockRoot());
        Path expResult = MockFileSystem.getShowDir(show).toPath();
        Path result = instance.getSeasonsPath(show);
        assertEquals(expResult, result);
    }

    /**
     * Test of getEpisodesPath method, of class StandardTVPath.
     */
    @Test
    public void testGetEpisodesPath() {
        String show = "Scrubs";
        int season = 1;
        StandardTVPath instance = new StandardTVPath(MockFileSystem.getMockRoot());
        Path expResult = MockFileSystem.getSeasonDir(show, season).toPath();
        Path result = instance.getEpisodesPath(show, season);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSeason method, of class TVMatcher.
     * @throws uk.co.samicemalone.libtv.exception.EpisodePathNotFoundException
     */
    @Test
    public void testGetSeason() throws EpisodePathNotFoundException {
        String show = "Scrubs";
        int season = 1;
        StandardTVPath instance = new StandardTVPath(MockFileSystem.getMockRoot());
        Season expResult = new Season(season, instance.getEpisodesPath(show, season));
        Season result = instance.getSeason(show, season);
        assertSeasonEquals(expResult, result);
    }

    /**
     * Test of listPaths method, of class StandardTVPath.
     * @throws java.io.IOException
     */
    @Test
    public void testListPaths() throws IOException {
        String show = "Scrubs";
        int season = 1;
        StandardTVPath instance = new StandardTVPath(MockFileSystem.getMockRoot());
        List<Path> expResult = MockFileSystem.getFullSeasonEpisodes(show, new Range(season));
        List<Path> result = instance.listPaths(show, season);
        assertEquals(expResult, result);
    }

}
