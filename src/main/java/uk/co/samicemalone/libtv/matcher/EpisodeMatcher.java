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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import uk.co.samicemalone.libtv.comparator.EpisodeNoComparator;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.MatchCondition;
import uk.co.samicemalone.libtv.model.Range;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;

/**
 * EpisodeMatcher matches a path, or a list of paths, to episodes.
 * Each path given is assumed to be within the same season.
 * EpisodeMatcher can match individual episodes, a range of episodes, the
 * remaining episodes in a season, the largest episode number in a season, and
 * whole seasons. Episodes can also be matched according to the criteria defined
 * by a {@link MatchCondition} implementation.
 * The EpisodeMatch objects that have been matched will contain episode numbers
 * and the path to the match. The TV show will be matched if found. The season
 * number will be matched if found, or {@link EpisodeMatch#NO_SEASON} will be
 * used.
 * @author Sam Malone
 */
public class EpisodeMatcher {
    
    private final TVMatcher tvMatcher;

    /**
     * Creates a new instance of EpisodeMatcher
     */
    public EpisodeMatcher() {
        this.tvMatcher = new TVMatcher();
    }

    /**
     * Creates a new instance of EpisodeMatcher
     * @param options options to use when matching
     */
    public EpisodeMatcher(TVMatcherOptions options) {
        this.tvMatcher = new TVMatcher(options);
    }
    
    /**
     * Match the path to an episode to determine the episode number(s) and the
     * show and season if present
     * @param path path to match
     * @return EpisodeMatch if found, otherwise null.
     */
    public EpisodeMatch match(Path path) {
        return tvMatcher.match(path);
    }
    
    /**
     * Match the paths to the episode number given by episodeNo
     * @param paths files to search for an episode match
     * @param episodeNo episode number
     * @return episode match or null if not found
     */
    public EpisodeMatch match(Collection<Path> paths, int episodeNo) {
        for(Path path : paths) {
            EpisodeMatch m = tvMatcher.match(path);
            if(m != null && m.isEpisodeNo(episodeNo)) {
                return m;
            }
        }
        return null;
    }
    
    /**
     * Match each path to an episode to determine the episode number(s) and the
     * show and season if present.
     * Wrapper for match(files, null)
     * @param paths paths in same season to search for episode matches
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> match(Collection<Path> paths) {
        return match(paths, null);
    }
    
    /**
     * Match each path to an episode to determine the episode number(s) and the
     * show and season if present.
     * In addition, the given MatchCondition must be satisfied in order for the episode
     * to be matched.
     * The path collection is assumed to only contain episodes within the same season
     * @param paths files in same season to search for episode matches
     * @param condition additional condition to be satisfied before accepting a match
     * or null to place no extra conditions on the match
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> match(Collection<Path> paths, MatchCondition<EpisodeMatch> condition) {
        List<EpisodeMatch> matches = new ArrayList<>();
        for(Path path : paths) {
            EpisodeMatch match = tvMatcher.match(path);
            if(match != null && (condition == null || condition.matches(match))) {
                matches.add(match);
            }
        }
        Collections.sort(matches, new EpisodeNoComparator());
        return matches;
    }
    
    /**
     * Match each path name to an episode in the Range given.
     * The path list is assumed to only contain episodes within the same season
     * @param paths files in same season to search for episode matches
     * @param range range of episodes to match
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> matchRange(Collection<Path> paths, final Range range) {
        return match(paths, new MatchCondition<EpisodeMatch>() {
            @Override
            public boolean matches(EpisodeMatch match) {
                return range.contains(match.getEpisodesAsRange());
            }
        });
    }
    
    /**
     * Match paths to episodes from the given start episode. In other words,
     * match each path to an episode which is greater than or equal to startEp.
     * @param paths files in same season to search for episode matches
     * @param startEp starting episode or higher to match
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> matchFrom(Collection<Path> paths, final int startEp) {
        return match(paths, new MatchCondition<EpisodeMatch>() {
            @Override
            public boolean matches(EpisodeMatch match) {
                return match.isEpisodeInRange(Range.maxRange(startEp));
            }
        });
    }
    
    /**
     * Match each file name to an episode and return the largest match
     * @param files files to search for episode matches
     * @return largest episode match or null if no episode matches
     */
    public EpisodeMatch matchLargest(Collection<Path> files) {
        List<EpisodeMatch> matches = match(files);
        EpisodeMatch largest = null;
        int max = -1;
        for(EpisodeMatch match : matches) {
            int matchMax = match.getEpisodesAsRange().getEnd();
            if(matchMax > max) {
                max = matchMax;
                largest = match;
            }
        }
        return largest;
    }
    
}
