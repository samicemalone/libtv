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
package uk.co.samicemalone.libtv.matcher.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Formatter;
import uk.co.samicemalone.libtv.exception.EpisodePathNotFoundException;
import uk.co.samicemalone.libtv.exception.SeasonsPathNotFoundException;
import uk.co.samicemalone.libtv.model.Season;

/**
 * StandardTVPath models a standard TV directory structure.
 * <p>For example:
 * <ul>
 *   <li>/path/to/TV/Show Name/Season 1/EpisodeFile.mkv</li>
 *   <li>/path/to/TV/Show Name/Season 01/EpisodeFile.mkv</li>
 *   <li>/path/to/TV/Show Name/Series 1/EpisodeFile.mkv</li>
 *   <li>/path/to/TV/Show Name/Series 01/EpisodeFile.mkv</li>
 * </ul>
 * <p>Using the first example, the path can be split into:
 * <p>
 *   Shows Path   : {@code /path/to/TV/} (directory containing shows) <br>
 *   Seasons Path : {@code /path/to/TV/Show Name} (directory containing a shows seasons) <br>
 *   Episodes Path: {@code /path/to/TV/Show Name/Season 1} (directory containing episodes) <br>
 * @author Sam Malone
 */
public class StandardTVPath extends TVPath {
    
    /**
     * SeasonFormat is used to format directory names for the standard
     * episodes path
     */
    public enum SeasonFormat {
        /** Example: Season 1 **/
        SEASON("Season %d"),
        /** Example: Season 01 **/
        SEASON_PADDED("Season %02d"),
        /** Example: Series 1 **/ 
        SERIES("Series %d"),
        /** Example: Series 01 **/
        SERIES_PADDED("Series %02d");
        
        private final String format;

        private SeasonFormat(String pattern) {
            this.format = pattern;
        }
        
        /**
         * Format the season according to the season format
         * @param season season number
         * @return 
         */
        public String format(int season) {
            return String.format(format, season);
        }
        
        /**
         * Get the season format to be used with {@link Formatter}
         * @return 
         */
        public String getFormat() {
            return format;
        }
    }
    
    private final Path tvRoot;
    
    /**
     * Create a new instance of StandardTVPath with the given tv directory
     * @param tvRoot Path to the tv root directory (the directory containing
     * the TV shows)
     */
    public StandardTVPath(Path tvRoot) {
        this.tvRoot = tvRoot;
    }

    /**
     * Get the path to the directory containing the TV shows
     * @return
     */
    public Path getShowsPath() {
        return tvRoot;
    }

    @Override
    public Path getSeasonsPath(String show) {
        Path p = getShowsPath().resolve(show);
        return Files.exists(p) ? p : null;
    }

    @Override
    public Path getEpisodesPath(String show, int season) {
        Path p = getSeasonsPath(show);
        if(p != null) {
            for(SeasonFormat seasonFormat : SeasonFormat.values()) {
                p = p.resolve(seasonFormat.format(season));
                if(Files.exists(p)) {
                    return p;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the Season for the given show and season if present
     * @param show TV show
     * @param season season number
     * @return Season
     * @throws EpisodePathNotFoundException if unable to find the episodes path
     */
    public Season getSeason(String show, int season) throws EpisodePathNotFoundException {
        Path episodesPath = getEpisodesPath(show, season);
        if(episodesPath == null) {
            throw new EpisodePathNotFoundException(show, season);
        }
        return new Season(season, episodesPath);
    }
    
    /**
     * Detect the SeasonFormat being used for the given show.
     * @param show TV show
     * @return SeasonFormat if detected, otherwise null
     */
    public SeasonFormat detectSeasonFormat(String show) {
        TVElementMatcher elm = new StandardTVElementMatcher();
        Path p = getSeasonsPath(show);
        if(p == null) {
            return null;
        }
        for(Path path : listPaths(p)) {
            int season = elm.matchSeason(path);
            if(season >= 0 && season < 10) {
                for(SeasonFormat seasonFormat : SeasonFormat.values()) {
                    if(path.getFileName().endsWith(seasonFormat.format(season))) {
                        return seasonFormat;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Create a new directory to store the seasons for the given show
     * @param show TV show
     * @return new seasons path created or the existing path if it already exists
     * @throws IOException if unable to create the directory
     */
    public Path newSeasonsPath(String show) throws IOException {
        Path p = getShowsPath().resolve(show);
        if(!Files.exists(p)) {
            return Files.createDirectory(p);
        }
        return p;
    }
    
    /**
     * Create a new directory to store the episodes for the given show and season.
     * If format is null, the season format is detected from the seasons in the
     * season path. If the the season format cannot be detected, {@link SeasonFormat#SEASON}
     * will be used as the default.
     * @param show TV show
     * @param season season number
     * @param format season format to use, or null to detect from the seasons path
     * @return new episodes path created or the existing path if it already exists
     * @throws IOException if unable to create the new episodes directory path
     * @throws SeasonsPathNotFoundException if unable to find the seasons path
     */
    public Path newEpisodesPath(String show, int season, SeasonFormat format) throws IOException {
        Path p = getSeasonsPath(show);
        if(p != null) {
            SeasonFormat sf = format == null ? detectSeasonFormat(show) : format;
            Path episodesPath = p.resolve(sf == null ? SeasonFormat.SEASON.format(season) : sf.format(season));
            return Files.exists(episodesPath) ? episodesPath : Files.createDirectory(episodesPath);
        }
        throw new SeasonsPathNotFoundException(show);
    }
    
}
