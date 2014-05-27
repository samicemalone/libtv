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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import uk.co.samicemalone.libtv.exception.EpisodesPathNotFoundException;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.EpisodeRange;
import uk.co.samicemalone.libtv.model.Range;
import uk.co.samicemalone.libtv.model.Season;
import uk.co.samicemalone.libtv.model.TVMatcherOptions;

/**
 * TVEpisodeMatcher can match TV episodes or sets of TV episodes in a variety
 * of ways. TVEpisodeMatcher can match:
 * <ul>
 *   <li>Individual episodes</li>
 *   <li>All episodes in a season</li>
 *   <li>Ranges of episodes (including ranges across seasons)</li>
 *   <li>Ranges of seasons</li>
 *   <li>Remaining episodes in a season</li>
 *   <li>Remaining seasons</li>
 *   <li>Latest episode for a TV show</li>
 *   <li>Largest episode in a season</li>
 *   <li>Largest season number</li>
 *   <li>All episodes</li>
 * </ul>
 * <p>
 * TVEpisodeMatcher is multi-episode aware.
 * @author Sam Malone
 */
public class TVEpisodeMatcher {
    
    private final TVPath tvPath;
    private final EpisodeMatcher episodeMatcher;

    /**
     * Create a new instance of TVEpisodeMatcher
     * @param tvPath tv path used to find episodes 
     */
    public TVEpisodeMatcher(TVPath tvPath) {
        this.tvPath = tvPath;
        this.episodeMatcher = new EpisodeMatcher();
    }

    /**
     * Create a new instance of TVEpisodeMatcher
     * @param tvPath tv path used to find episodes 
     */
    public TVEpisodeMatcher(TVPath tvPath, TVMatcherOptions options) {
        this.tvPath = tvPath;
        this.episodeMatcher = new EpisodeMatcher(options);
    }
    
    /**
     * Match an episode with the given show, season number and episode number
     * @param show TV show
     * @param season season number of episode to match
     * @param episode episode number to match
     * @return EpisodeMatch if found, otherwise null
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public EpisodeMatch matchEpisode(String show, int season, int episode) throws IOException  {
        return episodeMatcher.match(tvPath.listPaths(show, season), episode);
    }
    
    /**
     * Match an episode from the given episodes path
     * @param episodesPath path to directory containing episode files
     * @param episode episode number to match in episodesPath
     * @return EpisodeMatch or null if no match found
     */
    public EpisodeMatch matchEpisode(Path episodesPath, int episode) {
        return episodeMatcher.match(tvPath.listPaths(episodesPath), episode);
    }
    
    /**
     * Match the largest episode in the given season for the given show.
     * @param show TV show
     * @param season season number to match largest episode
     * @return largest episode or null if no episode matches
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public EpisodeMatch matchLargestEpisode(String show, int season) throws IOException {
        return episodeMatcher.matchLargest(tvPath.listPaths(show, season));
    }
    
    /**
     * Match the largest episode from the files in the given episodes path
     * @param episodesPath path to directory containing episode files
     * @return largest episode or null if no episode matches
     */
    public EpisodeMatch matchLargestEpisode(Path episodesPath) {
        return episodeMatcher.matchLargest(tvPath.listPaths(episodesPath));
    }
    
    /**
     * Match the latest episode of the given show. That is, the latest episode
     * in the latest season
     * @param show TV show
     * @return latest episode match or null if no episode matches
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public EpisodeMatch matchLatestEpisode(String show) throws IOException {
        Season season = matchLargestSeason(tvPath.listSeasons(show));
        return episodeMatcher.matchLargest(tvPath.listPaths(season.getPath()));
    }
    
    /**
     * Match the episodes in the specified season for the given show
     * @param show TV show
     * @param season season number to match episodes from
     * @return list of files matched or empty array if none found
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchSeason(String show, int season) throws IOException {
        return matchSeasonRange(show, new Range(season));
    }
    
    /**
     * Match the episodes in the given range for show
     * @param show TV Show
     * @param range Range of seasons
     * @return list of files matched or empty array if none found
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchSeasonRange(String show, Range range) throws IOException {
        List<EpisodeMatch> matches = new ArrayList<>();
        for(Season season : tvPath.listSeasons(show)) {
            if(range.contains(season.asInt())) {
                matches.addAll(episodeMatcher.match(tvPath.listPaths(season.getPath())));
            }
        }
        return matches;
    }
    
    /**
     * Get a list of all the episodes for the given show starting from season
     * @param season Starting Season
     * @param show TV Show
     * @return list of files matched or empty array if none found
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchSeasonsFrom(String show, int season) throws IOException {
        return new ArrayList<>(matchSeasonRange(show, Range.maxRange(season)));
    }
    
    /**
     * Matches the largest season for the given show
     * @param show tv show
     * @return largest season or null if seasons is empty
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchLargestSeason(String show) throws IOException {
        Season largest = matchLargestSeason(tvPath.listSeasons(show));
        return episodeMatcher.match(tvPath.listPaths(largest.getPath()));
    }
    
    /**
     * Matches the largest season from the list of seasons
     * @param seasons list of seasons
     * @return largest season or null if seasons is empty
     */
    public Season matchLargestSeason(List<Season> seasons) {
        Season largest = null;
        int max = -1;
        for(Season season : seasons) {
            if(season.asInt() > max) {
                max = season.asInt();
                largest = season;
            }
        }
        return largest;
    }
    
    /**
     * Match episodes in season from the given start episode. In other words,
     * match each file to an episode which is greater than or equal to startEp.
     * @param show tv show
     * @param season season to match episodes
     * @param episode episode to start from in season
     * @return episode matches or empty list
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchEpisodesFrom(String show, int season, int episode) throws IOException {
        return episodeMatcher.matchFrom(tvPath.listPaths(show, season), episode);
    }
    
    /**
     * Match the episodes in the given episode range
     * @param show TV Show
     * @param range EpisodeRange
     * @return List of episode matches in the given range or empty list
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchEpisodeRange(String show, EpisodeRange range) throws IOException {
        if(range.getStartSeason() > range.getEndSeason()) {
            return new ArrayList<>();
        } else if(range.getStartSeason() == range.getEndSeason()) {
            return episodeMatcher.matchRange(tvPath.listPaths(show, range.getStartSeason()), range.toRange());
        }
        List<EpisodeMatch> list = new ArrayList<>();
        // add episodes from starting season in range
        list.addAll(episodeMatcher.matchFrom(tvPath.listPaths(show, range.getStartSeason()), range.getStartEpisode()));
        // add full seasons in between range if applicable
        list.addAll(matchSeasonRange(show, new Range(range.getStartSeason() + 1, range.getEndSeason() - 1)));
        // add remaining episodes of the end season in range
        List<Path> tmpList = tvPath.listPaths(show, range.getEndSeason());
        list.addAll(episodeMatcher.matchRange(tmpList, new Range(0, range.getEndEpisode())));
        return list;
    }
    
    /**
     * Match all the episodes from all the seasons of the given show
     * @param show TV Show
     * @return Episode List or empty array
     * @throws EpisodesPathNotFoundException if unable to find a season
     * @throws IOException if unable to list any directories
     */
    public List<EpisodeMatch> matchAllEpisodes(String show) throws IOException {
        return matchSeasonsFrom(show, 1);
    }
    
}
