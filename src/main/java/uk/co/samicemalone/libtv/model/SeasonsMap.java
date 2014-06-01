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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import uk.co.samicemalone.libtv.comparator.EpisodeNoComparator;

/**
 *
 * @author Sam Malone
 */
public class SeasonsMap {
    
    private final Map<Integer, Map<Integer, EpisodeMatch>> seasonsMap;

    public SeasonsMap() {
        this.seasonsMap = new HashMap<>();
    }
    
    public SeasonsMap(Collection<EpisodeMatch> initial) {
        this();
        addEpisodes(initial);
    }
    
    /**
     * Add an episode to the map.
     * @param e episode to add
     */
    public void addEpisode(EpisodeMatch e) {
        if(!seasonsMap.containsKey(e.getSeason())) {
            seasonsMap.put(e.getSeason(), new HashMap<Integer, EpisodeMatch>());
        }
        for(EpisodeMatch singleEp : e.toSplitEpisodeList()) {
            int episodeNo = singleEp.getEpisode();
            if(!seasonsMap.get(e.getSeason()).containsKey(episodeNo)) {
                seasonsMap.get(e.getSeason()).put(episodeNo, e);
            }
        }
    }
    
    /**
     * Add the collection of episodes to the map
     * @param episodes episodes to add
     */
    public final void addEpisodes(Collection<EpisodeMatch> episodes) {
        for(EpisodeMatch episode : episodes) {
            addEpisode(episode);
        }
    }

    /**
     * Check if the season number is in the map
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     * @return true if found, false otherwise
     */
    public boolean containsSeason(int season) {
        return seasonsMap.containsKey(season);
    }
    
    /**
     * Get the set of seasons in the map
     * @return set of seasons in the map or empty set
     */
    public Set<Integer> getSeasons() {
        return new TreeSet<>(seasonsMap.keySet());
    }
    
    /**
     * Get a set of episodes for the given season in ascending order using an
     * {@code EpisodeNoComparator}.
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     * @return episode set or null if season is not found
     */
    public Set<EpisodeMatch> getSeasonEpisodes(int season) {
        if(containsSeason(season)) {
            Set<EpisodeMatch> set = new TreeSet<>(new EpisodeNoComparator());
            set.addAll(seasonsMap.get(season).values());
            return set;
        }
        return null;
    }
    
    /**
     * Get the number of seasons in the map.
     * @return number of seasons in the map.
     */
    public int getSeasonCount() {
        return getSeasonCount(false);
    }
    
    /**
     * Get the number of seasons in the map
     * @param includeNoSeason if true, include episodes without a season, as
     * an extra season. if false, only include episodes with seasons
     * @return number of seasons in the map
     */
    public int getSeasonCount(boolean includeNoSeason) {
        int count = seasonsMap.size();
        return !includeNoSeason && containsSeason(EpisodeMatch.NO_SEASON) ? count - 1 : count;
    }

    /**
     * Remove the episodes for the given season
     * @param season season number
     */
    public void removeSeason(int season) {
        seasonsMap.remove(season);
    }
    
}
