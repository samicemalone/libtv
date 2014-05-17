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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TVMap creates a mapping between shows, seasons and episodes to allow for
 * more efficient episode lookups. TV show names are case insensitive.
 * Episode paths that contain more than one episode number are duplicated in
 * map to allow for quick retrieval using partial episode number matches e.g.
 * searching for episode 1 when the map contains a double episode [1, 2]
 * @author Sam Malone
 */
public class TVMap {
    
    private final Map<String, String> showMap;
    private final Map<String, Map<Integer, Map<Integer, EpisodeMatch>>> tvMap;
    
    /**
     * Create a new empty instance of TVMap
     */
    public TVMap() {
        this.showMap = new HashMap<>();
        this.tvMap = new HashMap<>();
    }
    
    /**
     * Create a new instance of TVMap containing the elements of the collection
     * given.
     * @param episodes elements to initially add to TVMap
     */
    public TVMap(Collection<EpisodeMatch> episodes) {
        this();
        addEpisodes(episodes);
    }
    
    /**
     * Add an episode to the map.
     * @param e episode to add
     */
    public void addEpisode(EpisodeMatch e) {
        String show = e.getShow().toLowerCase();
        if(!tvMap.containsKey(show)) {
            showMap.put(show, e.getShow());
            tvMap.put(show, new HashMap<Integer, Map<Integer, EpisodeMatch>>());
        }
        Map<Integer, Map<Integer, EpisodeMatch>> seasonsMap = tvMap.get(show);
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
     * Check if the episode given is in the map
     * @param episode episode
     * @return true if found, false otherwise
     */
    public boolean contains(EpisodeMatch episode) {
        return contains(episode.getShow(), episode.getSeason(), episode.getEpisode());
    }
    
    /**
     * Check if the episode is in the map
     * @param show tv show
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     * @param episode episode number
     * @return true if found, false otherwise
     */
    public boolean contains(String show, int season, int episode) {
        show = show.toLowerCase();
        return  tvMap.containsKey(show) &&
                tvMap.get(show).containsKey(season) &&
                tvMap.get(show).get(season).containsKey(episode);
    }
    
    /**
     * Check if the show is in the map
     * @param show tv show
     * @return true if found, false otherwise
     */
    public boolean containsShow(String show) {
        return tvMap.containsKey(show.toLowerCase());
    }
    
    /**
     * Check if the tv show and season are in the map
     * @param show tv show
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     * @return true if found, false otherwise
     */
    public boolean containsSeason(String show, int season) {
        show = show.toLowerCase();
        return tvMap.containsKey(show) && tvMap.get(show).containsKey(season);
    }
    
    /**
     * Get the set of shows in the map
     * @return set of shows in the map or empty set
     */
    public Set<String> getShows() {
        return new HashSet<>(showMap.values());
    }
    
    /**
     * Get the set of seasons in the map for the given show
     * @param show tv show
     * @return set of seasons in the map or empty set
     */
    public Set<Integer> getSeasons(String show) {
        return containsShow(show) ? new HashSet<>(tvMap.get(show.toLowerCase()).keySet()) : new HashSet<Integer>();
    }
    
    /**
     * Get the set of episodes in the map for the given show
     * @param show tv show
     * @return set of episodes in the map for the given show or empty set
     */
    public Set<EpisodeMatch> getEpisodes(String show) {
        show = show.toLowerCase();
        Set<EpisodeMatch> set = new HashSet<>();
        if(tvMap.containsKey(show)) {
            for(int season : tvMap.get(show).keySet()) {
                set.addAll(tvMap.get(show).get(season).values());
            }
        }
        return set;
    }
    
    /**
     * Get an episode from the map
     * @param show tv show
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     * @param episode episode number
     * @return episode or null if not present
     */
    public EpisodeMatch getEpisode(String show, int season, int episode) {
        return contains(show, season, episode) ? tvMap.get(show.toLowerCase()).get(season).get(episode) : null;
    }
    
    /**
     * Get the number of shows in the map
     * @return number of shows in the map
     */
    public int getShowCount() {
        return showMap.size();
    }
    
    /**
     * Get the number of seasons in the map for given show.
     * @param show tv show
     * @return number of seasons in the map for given show.
     */
    public int getSeasonCount(String show) {
        return getSeasonCount(show, false);
    }
    
    /**
     * Get the number of seasons in the map for the given show.
     * @param show tv show
     * @param includeNoSeason if true, include episodes without a season, as
     * an extra season. if false, only include episodes with seasons
     * @return number of seasons in the map for the given show.
     */
    public int getSeasonCount(String show, boolean includeNoSeason) {
        show = show.toLowerCase();
        int count = 0;
        if(tvMap.containsKey(show) && !tvMap.get(show).isEmpty()) {
            for(int season : tvMap.get(show).keySet()) {
                if(season == EpisodeMatch.NO_SEASON && !includeNoSeason) {
                    continue;
                }
                if(!tvMap.get(show).get(season).isEmpty()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Get the episodes for the given show and season
     * @param show tv show
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     * @return season episodes or empty set
     */
    public Set<EpisodeMatch> getSeasonEpisodes(String show, int season) {
        Set<EpisodeMatch> set = new HashSet<>();
        show = show.toLowerCase();
        if(tvMap.containsKey(show) && tvMap.get(show).containsKey(season)) {
            set.addAll(tvMap.get(show).get(season).values());
        }
        return set;
    }
    
    /**
     * Get an episode from the map
     * @param episode to find
     * @return episode or null
     */
    private EpisodeMatch getEpisode(EpisodeMatch e) {
        String show = e.getShow().toLowerCase();
        return contains(e) ? tvMap.get(show).get(e.getSeason()).get(e.getEpisode()) : null;
    }
    
    /**
     * Check whether the map is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return getShowCount() == 0;
    }
    
    /**
     * Replace an episode with another.
     * @param old episode to remove
     * @param replacement episode to add
     */
    public void replaceEpisode(EpisodeMatch old, EpisodeMatch replacement) {
        removeEpisode(old);
        addEpisode(replacement);
    }
    
    /**
     * Remove all the episodes from this map, that appear in the collection
     * given.
     * @param collection collection of episodes to remove from this instance
     */
    public void removeAll(Collection<EpisodeMatch> collection) {
        for(EpisodeMatch episode : collection) {
            removeEpisodeSubset(episode);
        }
    }
    
    /**
     * Remove the season for the given show from this map
     * @param show tv show
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     */
    public void removeSeason(String show, int season) {
        if(containsShow(show)) {
            show = show.toLowerCase();
            tvMap.get(show).remove(season);
            cleanEmptyShow(show);
        }
    }
    
    /**
     * Remove the episodes for the given show from the map
     * @param show TV show to remove
     */
    public void removeShow(String show) {
        show = show.toLowerCase();
        if(tvMap.containsKey(show)) {
            showMap.remove(show);
            tvMap.remove(show);
        }
    }
    
    /**
     * Remove the episode match given from the map.
     * @param episode episode match to remove
     */
    public void removeEpisode(EpisodeMatch episode) {
        if(contains(episode)) {
            String show = episode.getShow().toLowerCase();
            Map<Integer, EpisodeMatch> seasonEpisodes = tvMap.get(show).get(episode.getSeason());
            for(int episodeNo : episode.getEpisodes()) {
                seasonEpisodes.remove(episodeNo);
            }
            cleanEmptySeason(show, episode.getSeason());
        }
    }
    
    /**
     * Remove the subset of episodes, given by the episode match, from the map.
     * For example, if 24 - S01E01E02 is in the map, then removing 24 - S01E02
     * would leave 24 - S01E01 in the map.
     * @param episode 
     */
    public void removeEpisodeSubset(EpisodeMatch episode) {
        for(EpisodeMatch singleEp : episode.toSplitEpisodeList()) {
            EpisodeMatch cacheEp = getEpisode(singleEp);
            if(cacheEp == null) {
                continue;
            }
            removeEpisode(cacheEp);
            EpisodeMatch newEp = new EpisodeMatch(cacheEp);
            newEp.getEpisodes().remove(new Integer(singleEp.getEpisode()));
            if(!newEp.getEpisodes().isEmpty()) {
                addEpisode(newEp);
            }
        }
    }
    
    /**
     * Clean the show from the map if it contains no seasons
     * @param show lower case show
     */
    private void cleanEmptyShow(String show) {
        if(tvMap.get(show).isEmpty()) {
            tvMap.remove(show);
            showMap.remove(show);
        }
    }
    
    /**
     * Clean the season of show from the map if it contains no episodes
     * @param show lower case show
     * @param season season number or {@link EpisodeMatch#NO_SEASON}
     */
    private void cleanEmptySeason(String show, int season) {
        if(tvMap.get(show).get(season).isEmpty()) {
            tvMap.get(show).remove(season);
            cleanEmptyShow(show);
        }
    }
    
}
