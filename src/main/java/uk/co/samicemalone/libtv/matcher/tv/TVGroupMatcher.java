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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.samicemalone.libtv.matcher.SeasonMatcher;
import uk.co.samicemalone.libtv.matcher.ShowMatcher;
import uk.co.samicemalone.libtv.matcher.TVMatcher;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;
import uk.co.samicemalone.libtv.util.ShowCase;

/**
 * TVGroupMatcher provides methods to match a TV show name and season number
 * from a {@link Matcher} or {@link MatchResult} using the TVMatcherOptions
 * supplied. This allows extending classes to define a regex {@link Pattern} and 
 * pass the matcher/result and group number to TVGroupMatcher to conform to the
 * TVMatcherOptions
 * @author Sam Malone
 */
public abstract class TVGroupMatcher implements TVMatcher.Matcher {
    
    private final TVMatcherOptions options;

    public TVGroupMatcher(TVMatcherOptions options) {
        this.options = options;
    }

    @Override
    public TVMatcherOptions getOptions() {
        return options;
    }
    
    private int getSeasonInt(MatchResult result, int group) {
        String match = result.group(group);
        return match == null ? EpisodeMatch.NO_SEASON : Integer.valueOf(match);
    }
    
    private String getShow(String showMatch) {
        return (showMatch == null || showMatch.isEmpty()) ? null : ShowCase.convert(showMatch);
    }
    
    /**
     * Match the season from the episode path given as per the TVMatcherOptions
     * specified.
     * @param path absolute episode path
     * @param result MatchResult containing the matches
     * @param group group number that matches the season in result
     * @return season number or {@link EpisodeMatch#NO_SEASON}
     */
    public int matchSeason(Path path, MatchResult result, int group) {
        SeasonMatcher s = options.getSeasonMatcher();
        int season = s == null ? getSeasonInt(result, group) : s.matchSeason(path);
        if(season == EpisodeMatch.NO_SEASON) {
            if(options.getFallbackSeasonMatcher() != null) {
                return options.getFallbackSeasonMatcher().matchSeason(path);
            } else if(options.isFallbackTVMatcherSeason()) {
                return getSeasonInt(result, group);
            }
        }
        return season;
    }
    
    /**
     * Match the show from the episode path given as per the TVMatcherOptions
     * specified. The matcher given, will be used once to find the show match.
     * @param path absolute episode path
     * @param matcher Matcher to use to find a show match
     * @param group group number that matches the show in matcher
     * @return TV show or null if not found
     */
    public String matchShow(Path path, Matcher matcher, int group) {
        ShowMatcher showMatcher = options.getShowMatcher();
        String show = null;
        if(showMatcher != null) {
            show = showMatcher.matchShow(path);
        } else if(showMatcher == null && matcher.find()) {
            show = getShow(matcher.group(group));
        }
        if(show != null && !show.isEmpty()) {
            return show;
        }
        if(getOptions().getFallbackShowMatcher() != null) {
            show = getOptions().getFallbackShowMatcher().matchShow(path);
        } else if(getOptions().isFallbackTVMatcherShow()) {
            if(showMatcher != null) {
                matcher.find();
            }
            show = getShow(matcher.group(group));
        }
        return show;
    }
    
    /**
     * Match the show from the episode path given as per the TVMatcherOptions
     * specified.
     * @param path absolute episode path
     * @param result MatchResult containing the matches
     * @param group group number that matches the show in result
     * @return TV show or null if not found
     */
    public String matchShow(Path path, MatchResult result, int group) {
        ShowMatcher showMatcher = options.getShowMatcher();
        String show = showMatcher == null ? getShow(result.group(group)) : showMatcher.matchShow(path);
        if(show != null && !show.isEmpty()) {
            return show;
        } else if(options.getFallbackShowMatcher() != null) {
            return options.getFallbackShowMatcher().matchShow(path);
        } else if(options.isFallbackTVMatcherShow()) {
            return getShow(result.group(group));
        }
        return null;
    }
    
}
