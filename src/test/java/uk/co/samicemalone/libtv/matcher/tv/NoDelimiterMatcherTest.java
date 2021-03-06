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
package uk.co.samicemalone.libtv.matcher.tv;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import uk.co.samicemalone.libtv.matcher.TVMatcher;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;
import static uk.co.samicemalone.libtv.util.EpisodeTestUtil.assertEpisodeMatchEquals;

/**
 *
 * @author Sam Malone
 */
public class NoDelimiterMatcherTest {

    /**
     * Test of match method, of class NoDelimiterMatcher.
     */
    @Test
    public void testMatch() {
        String[] fileNames = new String[] {
            "the.league 102 pilot.mkv",
            "the_league_1_02_pilot.mkv",
            "the.league - 102 - pilot.mkv",
            "the.league 1.02 pilot.mkv"
        };
        NoDelimiterMatcher instance = new NoDelimiterMatcher(new TVMatcherOptions());
        EpisodeMatch expResult = new EpisodeMatch("The League", 1, 2);
        String path = "C:\\TV\\The League\\Season 1\\";
        for(String fileName : fileNames) {
            Path p = Paths.get(path, fileName);
            EpisodeMatch result = instance.match(p, TVMatcher.stripCommonTags(fileName));
            assertEpisodeMatchEquals(expResult, result);
        }
    }

    /**
     * Test of match method, of class NoDelimiterMatcher.
     */
    @Test
    public void testMatchDateShow() {
        String fileName = "house.of.cards.(2013).102.pilot.mkv";
        NoDelimiterMatcher instance = new NoDelimiterMatcher(new TVMatcherOptions());
        EpisodeMatch expResult = new EpisodeMatch("House of Cards (2013)", 1, 2);
        String path = "C:\\TV\\House of Cards (2013)\\Season 1\\";
        Path p = Paths.get(path, fileName);
        EpisodeMatch result = instance.match(p, TVMatcher.stripCommonTags(fileName));
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class NoDelimiterMatcher.
     */
    @Test
    public void testMatchNoShow() {
        String fileName = "102.mkv";
        NoDelimiterMatcher instance = new NoDelimiterMatcher(new TVMatcherOptions());
        EpisodeMatch expResult = new EpisodeMatch(1, 2);
        String path = "C:\\TV\\House of Cards (2013)\\Season 1\\";
        Path p = Paths.get(path, fileName);
        EpisodeMatch result = instance.match(p, TVMatcher.stripCommonTags(fileName));
        assertEpisodeMatchEquals(expResult, result);
    }
    
}
