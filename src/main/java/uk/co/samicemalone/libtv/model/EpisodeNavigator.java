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

package uk.co.samicemalone.libtv.model;

import java.nio.file.Path;
import uk.co.samicemalone.libtv.matcher.TVEpisodeMatcher;
import uk.co.samicemalone.libtv.matcher.path.TVPath;

/**
 * EpisodeNavigator can find an episode from an offset. In other words, find
 * the previous or next episode.
 * @author Sam Malone
 */
public class EpisodeNavigator {
    
    /**
     * Pointer describes which way to navigate an episode
     */
    public enum Pointer {
        PREV(-1),
        CUR(0),
        NEXT(1);

        private final int pointer;
        
        private Pointer(int pointer) {
            this.pointer = pointer;
        }
        
        public int value() {
            return pointer;
        }
    }
    
    private final TVEpisodeMatcher tvEpisodeMatcher;
    private final TVPath tvPath;

    /**
     * Create a new instance of EpisodeNavigator
     * @param tvMatcher TVEpisodeMatcher to match offset episode paths
     * @param tvPath TVPath containing the episode paths to match
     */
    public EpisodeNavigator(TVEpisodeMatcher tvMatcher, TVPath tvPath) {
        this.tvEpisodeMatcher = tvMatcher;
        this.tvPath = tvPath;
    }
    
    /**
     * Navigate from the episode by the offset given. The episode given will
     * be checked for a multi episode match before navigating. If the episode
     * given cannot be found, it is assumed to be a single episode and will be
     * navigated as normal.
     * @param episode episode to navigate from
     * @param offset offset. see {@link Pointer}
     * @return navigated Episode with the given offset or null if navigated 
     * episode is not found
     */
    public EpisodeMatch navigate(EpisodeMatch episode, Pointer offset) {
        Path episodesPath = tvPath.getEpisodesPath(episode.getShow(), episode.getSeason());
        Range episodeRange = episode.getEpisodesAsRange();
        if(episodesPath == null) {
            boolean isNavigateSeason = offset == Pointer.PREV && (episodeRange.getStart() - 1) < 1;
            return isNavigateSeason ? navigateSeason(episode, offset) : null;
        }
        int episodeNoBound = (offset == Pointer.NEXT) ? episodeRange.getEnd() : episodeRange.getStart();
        EpisodeMatch curMatch = tvEpisodeMatcher.matchEpisode(episodesPath, episodeNoBound);
        if(curMatch != null) {
            episodeRange = curMatch.getEpisodesAsRange();
            episodeNoBound = (offset == Pointer.NEXT) ? episodeRange.getEnd() : episodeRange.getStart();
        }
        EpisodeMatch m = tvEpisodeMatcher.matchEpisode(episodesPath, episodeNoBound + offset.value());
        if(m != null || offset == Pointer.CUR) { 
            return m;
        }
        // check another offset episode to see if it's missing or likely to be end of season
        if(tvEpisodeMatcher.matchEpisode(episodesPath, episodeNoBound + (2 * offset.value())) != null) {
            return null; // found extra offset episode so dont skip to offset season
        }
        return navigateSeason(episode, offset);
    }
    
    /**
     * Navigate to the start of the next season or the end of the previous
     * season.
     * @param toNavigate episode to navigate from
     * @param offset {@link Pointer}
     * @return navigated episode at the start of the next season or the end of
     * the previous season or null if no offset episode is found
     */
    public EpisodeMatch navigateSeason(EpisodeMatch toNavigate, Pointer offset) {
        int season = toNavigate.getSeason() + offset.value();
        Path episodesPath = tvPath.getEpisodesPath(toNavigate.getShow(), season);
        if(episodesPath == null) {
            return null;
        }
        EpisodeMatch match = null;
        if(offset == Pointer.PREV) {
            if((match = tvEpisodeMatcher.matchLargestEpisode(episodesPath)) == null) {
                return null;
            }
        } else if(offset == Pointer.NEXT) {
            if((match = tvEpisodeMatcher.matchEpisode(episodesPath, 0)) == null) {
                match = tvEpisodeMatcher.matchEpisode(episodesPath, 1);
            }
        }
        return match;
    }
    
}
