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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;

/**
 * Matches: (with variants of separators)
 * the.league ep01 pilot.mkv
 * the.league.ep.01.pilot.mkv
 * the.league e02 name.mkv
 * the_league - e2 - name.mkv
 * the.league ep01ep02 pilot.mkv
 * the.league.ep_01_ep_02.pilot.mkv
 * the league season 1 episode 1 pilot.mkv
 * the league season 1 ep 1 ep 2 pilot.mkv
 * etc...
 * @author Sam Malone
 */
public class WordDelimitedMatcher extends TVGroupMatcher {
    
    private final static String separator = "[_\\-. +]*";
    private final static String epSeparator = "(?:episode|ep|e)";

    private final static Pattern patternMultiConsecutive = Pattern.compile(
        "(?:season"+separator+"(\\d+))?[_\\-. +]+"+epSeparator+separator+"(\\d+)((?:"+epSeparator+"\\d+)+)",
        Pattern.CASE_INSENSITIVE
    );

    private final static Pattern pattern = Pattern.compile(
        "(?:season"+separator+"(\\d+))?[_\\-. +]+"+epSeparator+separator+"(\\d+)",
        Pattern.CASE_INSENSITIVE
    );
    
    private final static Pattern showPattern = Pattern.compile(
        "^(.*?)"+separator+pattern.pattern(),
        Pattern.CASE_INSENSITIVE
    );

    public WordDelimitedMatcher(TVMatcherOptions options) {
        super(options);
    }

    @Override
    public EpisodeMatch match(Path path, String filteredFileName) {
        Matcher m = patternMultiConsecutive.matcher(filteredFileName);
        Matcher showMatcher = showPattern.matcher(path.getFileName().toString());
        if(m.find()) {
            String show = matchShow(path, showMatcher, 1);
            int season = matchSeason(path, m.toMatchResult(), 1);
            int episode = Integer.valueOf(m.group(2));
            EpisodeMatch em = new EpisodeMatch(show, season, episode);
            String[] multi = m.group(3).split(epSeparator);
            for(int i = 1; i < multi.length; i++) {
                em.addEpisodeNo(Integer.valueOf(multi[i]));
            }
            return em;
        }
        m = pattern.matcher(filteredFileName);
        if(m.find()) {
            String show = matchShow(path, showMatcher, 1);
            int season = matchSeason(path, m.toMatchResult(), 1);
            int episode = Integer.valueOf(m.group(2));
            EpisodeMatch em = new EpisodeMatch(show, season, episode);
            while(m.find()) {
                em.addEpisodeNo(Integer.valueOf(m.group(2)));
            }
            return em;
        }
        return null;
    }
    
}