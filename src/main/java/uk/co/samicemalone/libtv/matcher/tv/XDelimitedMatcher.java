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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;

/**
 * Matches: (with variants of separators)
 * the.league - 1x01 - pilot.mkv
 * the.league - 1x01x02 - pilot.mkv
 * the.league - 1x01x02x03x04 - pilot.mkv
 * the.league 1x1x2x3x4 pilot.mkv
 * the.league.1x01_1x02.pilot.mkv
 * the.league.1x01.pilot.1x02.pilot.cont.mkv
 * the league - 1x01+1x02 - pilot.mkv
 * etc...
 * @author Sam Malone
 */
public class XDelimitedMatcher extends TVGroupMatcher {
    
    private final static String separator = "[_\\-. +]*";

    private final static Pattern pattern = Pattern.compile(
        "^(.*?)"+separator+"(\\d+)"+separator+"x(\\d+)"+separator+"((?:(?:(?:.*?)(?:\\d+)"
            + separator+")?x\\d+"+separator+")*)",
        Pattern.CASE_INSENSITIVE
    );

    public XDelimitedMatcher(TVMatcherOptions options) {
        super(options);
    }

    @Override
    public EpisodeMatch match(Path path, String filteredFileName) {
        Matcher m = pattern.matcher(filteredFileName);
        if(m.find()) {
            MatchResult r = m.toMatchResult();
            String show = matchShow(path, r, 1);
            int season = matchSeason(path, r, 2);
            int episode = Integer.valueOf(m.group(3));
            EpisodeMatch em = new EpisodeMatch(show, season, episode);
            em.addEpisodeNo(parseMultiEpisodes(m.group(4)));
            return em;
        }
        return null;
    }
        
    private List<Integer> parseMultiEpisodes(String multiEpisodes) {
        List<Integer> list = new ArrayList<>();
        if(multiEpisodes == null || multiEpisodes.isEmpty()) {
            return list;
        }
        String[] matches = multiEpisodes.split("[_\\-. +]+");
        Pattern p = Pattern.compile("(?:\\d+)?x(\\d+)", Pattern.CASE_INSENSITIVE);
        for(String match : matches) {
            Matcher m = p.matcher(match);
            while(m.find()) {
                list.add(Integer.valueOf(m.group(1)));
            }
        }
        return list;
    }

}
