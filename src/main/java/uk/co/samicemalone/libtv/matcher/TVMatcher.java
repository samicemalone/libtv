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

import java.nio.file.Path;
import java.util.regex.Pattern;
import uk.co.samicemalone.libtv.exception.MatchElementNotFoundException;
import uk.co.samicemalone.libtv.exception.MatchException;
import uk.co.samicemalone.libtv.exception.MatchNotFoundException;
import uk.co.samicemalone.libtv.matcher.tv.NoDelimiterMatcher;
import uk.co.samicemalone.libtv.matcher.tv.PartMatcher;
import uk.co.samicemalone.libtv.matcher.tv.SEDelimitedMatcher;
import uk.co.samicemalone.libtv.matcher.tv.WordDelimitedMatcher;
import uk.co.samicemalone.libtv.matcher.tv.XDelimitedMatcher;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;

/**
 * TVMatcher matches an episode file path to an EpisodeMatch with the ability
 * to specify which TV show elements are required.
 * The episode matching is deferred to the implementations of {@link Matcher} in
 * {@link uk.co.samicemalone.libtv.matcher.tv}
 * @author Sam Malone
 */
public class TVMatcher {
    
    /**
     * Matcher used to match an episode file path to an EpisodeMatch according
     * the TVMatcherOptions.
     */
    public static interface Matcher {
        
        /**
         * Get TV matcher options
         * @see TVMatcherOptions
         * @return TV matcher options
         */
        public TVMatcherOptions getOptions();        
        
        /**
         * Match a file path to an episode to determine the episode number(s). 
         * The show and season will be matched if found.
         * @param path absolute episode path
         * @param filteredFileName filtered file name. see {@link #stripCommonTags(java.lang.String)}
         * @return EpisodeMatch if found, otherwise null
         */
        public EpisodeMatch match(Path path, String filteredFileName);
        
    }
    
    /**
     * MatchElement represents data to be matched: SHOW, SEASON or ALL
     */
    public static enum MatchElement {
        SHOW, SEASON, ALL
    }
    
    private final TVMatcherOptions options;
    
    /**
     * Strip common tags from an episode filename that may interfere with
     * matching. Tags removed:
     *   Qualities: e.g. 720p, 1080i, 480p
     *   Codecs: e.g. ac3, dd5.1, aac2.0, dd 7.1, h.264, x264
     * @param fileName fileName to strip of tags
     * @return stripped filename
     */
    public static String stripCommonTags(String fileName) {
        String regex = "(?:720|480|1080)[ip]|([hx][_\\-. +]*264)|dd[_\\-. +]?[257][_\\-. +]*[01]|ac3|aac[_\\-. +]*(?:[257][_\\-. +]*[01])*";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return p.matcher(fileName).replaceAll("");
    }

    /**
     * Create a new instance of TVMatcher with the default matcher options to
     * match the tv show, season and episode(s) from the file name.
     */
    public TVMatcher() {
        options = new TVMatcherOptions();
    }

    /**
     * Create a new instance of TVMatcher using the specified TVMatcherOptions
     * @param options TV matcher options
     */
    public TVMatcher(TVMatcherOptions options) {
        this.options = options;
    }
    
    /**
     * Match a file path to an episode to determine the episode number(s).
     * The show and season will be matched if found.
     * @param path path to match
     * @return EpisodeMatch if found, otherwise null.
     */
    public EpisodeMatch match(Path path) {
        return match(path,
            new SEDelimitedMatcher(options),
            new XDelimitedMatcher(options),
            new WordDelimitedMatcher(options),
            new NoDelimiterMatcher(options),
            new PartMatcher(options)
        );
    }
    
    /**
     * Match a file path to an episode to determine the episode number(s).
     * The show and season will be matched if found.
     * Throws a MatchElementNotFoundException if an episode match was found
     * but the required match component wasn't e.g. no season.
     * @param path path to match
     * @param requiredMatch require the match to have the given MatchElement
     * @return EpisodeMatch
     * @throws uk.co.samicemalone.libtv.exception.MatchElementNotFoundException
     * if an episode match was found but the required match component was not.
     * @throws uk.co.samicemalone.libtv.exception.MatchNotFoundException
     * if an episode match was not found
     */
    public EpisodeMatch matchOrThrow(Path path, MatchElement requiredMatch) throws MatchException {
        EpisodeMatch e = matchElementOrThrow(path, requiredMatch);
        if(e == null) {
            throw new MatchNotFoundException("Match not found: " + path);
        }
        return e;
    }
    
    /**
     * Match file path to an episode to determine the episode number(s).
     * The show and season will be matched if found.
     * If an episode match was found but the required match component wasn't,
     * e.g. no season, then null will be returned
     * @param path path to match
     * @param requiredMatch require the match to have the given MatchElement
     * @return EpisodeMatch if found and has the required elements, otherwise null.
     */
    public EpisodeMatch matchElement(Path path, MatchElement requiredMatch) {
        EpisodeMatch e = match(path);
        if(e == null) {
            return null;
        }
        if(e.getShow() == null && (requiredMatch == MatchElement.SHOW || requiredMatch == MatchElement.ALL)) {
            return null;
        }
        if(e.getSeason() == EpisodeMatch.NO_SEASON && (requiredMatch == MatchElement.SHOW || requiredMatch == MatchElement.ALL)) {
            return null;
        }
        return e;
    }
    
    /**
     * Match file path to an episode to determine the episode number(s).
     * The show and season will be matched if found.
     * Throws a MatchElementNotFoundException if an episode match was found
     * but the required match component wasn't e.g. no season.
     * @param path path to match
     * @param requiredMatch require the match to have the given MatchElement
     * @return EpisodeMatch if found, otherwise null.
     * @throws uk.co.samicemalone.libtv.exception.MatchElementNotFoundException
     * if an episode match was found but the required match component was not.
     */
    public EpisodeMatch matchElementOrThrow(Path path, MatchElement requiredMatch) throws MatchElementNotFoundException {
        EpisodeMatch e = match(path);
        if(e == null) {
            return null;
        }
        switch(requiredMatch) {
            case SHOW:
                assertHasShow(e, path);
                break;
            case SEASON:
                assertHasSeason(e, path);
                break;
            case ALL:
                assertHasShow(e, path);
                assertHasSeason(e, path);
        }
        return e;
    }
    
    private void assertHasShow(EpisodeMatch m, Path path) throws MatchElementNotFoundException {
        if(m.getShow() == null) {
            throw new MatchElementNotFoundException("Show not found: " + path);
        }
    }
    
    private void assertHasSeason(EpisodeMatch m, Path path) throws MatchElementNotFoundException {
        if(m.getSeason() == EpisodeMatch.NO_SEASON) {
            throw new MatchElementNotFoundException("Season not found: " + path);
        }
    }
    
    /**
     * Match a file path to an episode to determine the episode number(s).
     * The show and season will be matched if found.
     * Each Matcher is checked in array order and returns as soon as a match is found
     * @param path path to match
     * @param matchers Matchers to check in the order given
     * @return EpisodeMatch if found, otherwise null.
     */
    private EpisodeMatch match(Path path, Matcher... matchers) {
        String filteredName = stripCommonTags(path.getFileName().toString());
        for(Matcher matcher : matchers) {
            EpisodeMatch m = matcher.match(path, filteredName);
            if(m != null) {
                m.setEpisodeFile(path.toFile());
                return m;
            }
        }
        return null;
    }
    
}
