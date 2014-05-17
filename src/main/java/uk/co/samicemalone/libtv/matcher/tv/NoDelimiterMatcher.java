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
 * the.league 101 pilot.mkv
 * the.league 1.01 pilot.mkv
 * @author Sam Malone
 */
public class NoDelimiterMatcher extends TVGroupMatcher {

    private final String separator = "[_\\-. +]*";
    private final String yearRegex = "(19|20)?\\d\\d";
    private final String dateRegex = String.join(separator, 
        yearRegex,"\\d\\d", "\\d\\d|\\d\\d", "\\d\\d", yearRegex, "|(19|20)\\d\\d"
    );
    private final Pattern pattern = Pattern.compile(
        "^(.*)"+separator+"(\\d+)"+separator+"(\\d\\d)", Pattern.CASE_INSENSITIVE
    );
        
    public NoDelimiterMatcher(TVMatcherOptions options) {
        super(options);
    }
    
    @Override
    public EpisodeMatch match(Path path, String filteredFileName) {
        Matcher showMatcher = pattern.matcher(filteredFileName);
        if(showMatcher.find()) {
            String show = matchShow(path, showMatcher.toMatchResult(), 1);
            if(show != null) {
                show = show.replaceAll(separator + '$', "");
            }
            Matcher m = pattern.matcher(filteredFileName.replaceAll(dateRegex, ""));
            if(m.find()) {
                int season = matchSeason(path, m.toMatchResult(), 2);
                int episode = Integer.valueOf(m.group(3));
                return new EpisodeMatch(show, season, episode);
            }
        }
        return null;
    }

}
