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
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.co.samicemalone.libtv.MockFileSystem;
import uk.co.samicemalone.libtv.matcher.path.StandardTVElementMatcher;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;
import static uk.co.samicemalone.libtv.util.EpisodeTestUtil.assertEpisodeMatchEquals;

/**
 *
 * @author Sam Malone
 */
public class TVMatcherTest {
    
    /**
     * Test of stripCommonTags method, of class EpisodeFileMatcher.
     */
    @Test
    public void testStripCommonTags() {
        String fileName = "Modern.Family.S05E17.720p.DD5.1.AAC2.0.H.264.mkv";
        String expResult = "Modern.Family.S05E17.....mkv";
        assertEquals(expResult, TVMatcher.stripCommonTags(fileName));
        fileName = "the.walking.dead.s03e01.1080p.bluray.x264.mkv";
        expResult = "the.walking.dead.s03e01..bluray..mkv";
        assertEquals(expResult, TVMatcher.stripCommonTags(fileName));
        fileName = "house.of.cards.(2013).s01e01.bluray.dts.x264.mkv";
        expResult = "house.of.cards.(2013).s01e01.bluray.dts..mkv";
        assertEquals(expResult, TVMatcher.stripCommonTags(fileName));
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatch() {
        File file = MockFileSystem.getEpisodeFile("The Office (US)", 1, 2);
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        EpisodeMatch result = new TVMatcher().match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsTVPath() {
        File season = MockFileSystem.getSeasonDir("The Office (US)", 1);
        File file = new File(season, "The Office US pt 2.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions(new StandardTVElementMatcher());
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsShow() {
        File season = MockFileSystem.getSeasonDir("The Office (US)", 1);
        File file = new File(season, "The Office US - 1x02.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions((ShowMatcher) new StandardTVElementMatcher());
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsSeason() {
        File season = MockFileSystem.getSeasonDir("The Office (US)", 1);
        File file = new File(season, "The Office (US) - Part 2.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions((SeasonMatcher) new StandardTVElementMatcher());
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsFallbackTVPath() {
        File season = MockFileSystem.getSeasonDir("The Office (US)", 1);
        File file = new File(season, "pt 2.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions().fallback(new StandardTVElementMatcher());
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsFallbackShow() {
        File season = MockFileSystem.getSeasonDir("The Office (US)", 1);
        File file = new File(season, "1x02.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions().fallbackShow(new StandardTVElementMatcher());
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsFallbackSeason() {
        File season = MockFileSystem.getSeasonDir("The Office (US)", 1);
        File file = new File(season, "The Office (US) - Part 2.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions().fallbackSeason(new StandardTVElementMatcher());
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class TVMatcher.
     */
    @Test
    public void testMatchOptionsFallbackRegex() {
        File file = new File(MockFileSystem.getMockRoot().toFile(), "The Office (US) - 1x02.mkv");
        EpisodeMatch expResult = new EpisodeMatch("The Office (US)", 1, 2);
        expResult.setEpisodeFile(file);
        TVMatcherOptions o = new TVMatcherOptions(new StandardTVElementMatcher()).fallbackTVMatcher();
        EpisodeMatch result = new TVMatcher(o).match(file.toPath());
        assertEpisodeMatchEquals(expResult, result);
    }

}
