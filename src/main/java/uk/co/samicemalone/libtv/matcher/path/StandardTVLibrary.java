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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import uk.co.samicemalone.libtv.exception.SeasonsPathNotFoundException;

/**
 * StandardTVLibrary models a standard TV library structure. It consists of the
 * specified TV source directories that follow the directory structure of
 * {@link StandardTVPath}.
 * @author Sam Malone
 */
public class StandardTVLibrary extends TVPath {
    
    private final Map<String, StandardTVPath> sourceMap;

    /**
     * Create a new instance of StandardTVLibrary with the specified collection
     * of tv sources. The sources must follow the same directory structure as {@link StandardTVPath}
     * @param tvSources tv sources that use a {@link StandardTVPath} e.g [/mnt/TV, /media/TV]
     */
    public StandardTVLibrary(Collection<String> tvSources) {
        sourceMap = new HashMap<>(tvSources.size());
        for(String source : tvSources) {
            sourceMap.put(source, new StandardTVPath(Paths.get(source)));
        }
    }

    @Override
    public Path getSeasonsPath(String show) {
        for(StandardTVPath tvPath : sourceMap.values()) {
            Path p = tvPath.getSeasonsPath(show);
            if(p != null) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Path getEpisodesPath(String show, int season) {
        for(StandardTVPath.SeasonFormat seasonFormat : StandardTVPath.SeasonFormat.values()) {
            Path p = getSeasonsPath(show);
            if(p != null) {
                p = p.resolve(seasonFormat.format(season));
                if(Files.exists(p)) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public TVElementMatcher getTVElementMatcher() {
        return new StandardTVElementMatcher();
    }
    
    /**
     * Create a new directory to store the episodes for the given show and season.
     * If format is null, the season format is detected from the seasons in the
     * season path. If the the season format cannot be detected, {@link StandardTVPath.SeasonFormat#SEASON}
     * will be used as the default.
     * @param show TV show
     * @param season season number
     * @param format season format to use, or null to detect from the seasons path
     * @return new episodes path created or the existing path if it already exists
     * @throws IOException if unable to create the new episodes directory path
     * @throws SeasonsPathNotFoundException if unable to find the seasons path
     */
    public Path newEpisodesPath(String show, int season, StandardTVPath.SeasonFormat format) throws IOException {
        for(StandardTVPath tvPath : sourceMap.values()) {
            Path p = tvPath.getSeasonsPath(show);
            if(p != null) {
                return tvPath.newEpisodesPath(show, season, null);
            }
        }
        throw new SeasonsPathNotFoundException(show);
    }
    
    /**
     * Create a new directory to store the episodes for the given show and season.
     * It is a wrapper for {@code #newEpisodesPath(show, season, null)}.
     * If the season format cannot be detected, then {@link StandardTVPath.SeasonFormat#SEASON}
     * will be used.
     * @param show TV show
     * @param season season number
     * @return new episodes path created or the existing path if it already exists
     * @throws IOException if unable to create the new episodes directory path
     * @throws SeasonsPathNotFoundException if unable to find the seasons path
     */
    public Path newEpisodesPath(String show, int season) throws IOException {
        return newEpisodesPath(show, season, null);
    }
    
}
