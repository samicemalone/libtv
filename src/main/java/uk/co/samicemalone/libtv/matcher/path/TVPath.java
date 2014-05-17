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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import uk.co.samicemalone.libtv.VideoFilter;
import uk.co.samicemalone.libtv.exception.EpisodePathNotFoundException;
import uk.co.samicemalone.libtv.util.PathUtil;

/**
 * TVPath models a TV directory structure, allowing access to filesystem paths 
 * containing TV show seasons and episodes.
 * @author Sam Malone
 */
public abstract class TVPath {

    /**
     * Get the path to the directory containing the season directories for the
     * given show
     * @param show TV show
     * @return
     */
    public abstract Path getSeasonsPath(String show);

    /**
     * Get the path to the directory containing the episodes for the given show
     * and season
     * @param show TV show
     * @param season season
     * @return
     */
    public abstract Path getEpisodesPath(String show, int season);

    /**
     * List the episode paths for the given TV show and season.
     * @param show TV show
     * @param season season
     * @return list of episode paths or empty list if none found
     * @throws EpisodePathNotFoundException if unable to find the episodes path
     * @throws IOException if unable to list the directory contents
     */
    public List<Path> listPaths(String show, int season) throws IOException {
        Path p = getEpisodesPath(show, season);
        if(p == null) {
            throw new EpisodePathNotFoundException(show, season);
        }
        return PathUtil.listPaths(p, new VideoFilter());
    }
    
    /**
     * List the episode paths that are contained in dir
     * @param dir directory to list
     * @return list of episode paths or empty list if directory is empty or an
     * IO error occurs
     */
    public List<Path> listPaths(Path dir) {
        try {
            return PathUtil.listPaths(dir, new VideoFilter());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
}
