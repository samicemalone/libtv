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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.samicemalone.libtv.model.EpisodeMatch;

/**
 * StandardTVElementMatcher is an implementation of TVElementMatcher used to 
 * match an episode file path to determine its TV 
 * elements (show name and season number). The TV directory structure follows
 * {@link StandardTVPath} to determine the TV elements e.g.
 * /path/to/TV/Show Name/Season 1/etc.mkv
 * @author Sam Malone
 */
public class StandardTVElementMatcher implements TVElementMatcher {
    
    private static final Pattern SEASON_PATTERN = Pattern.compile("(?:Season|Series) ([0-9]+)", Pattern.CASE_INSENSITIVE);
    
    @Override
    public String matchShow(Path episodePath) {
        Path season = episodePath.getParent();
        Path seasonName;
        if(season == null || (seasonName = season.getFileName()) == null || !SEASON_PATTERN.matcher(seasonName.toString()).matches()) {
            return null;
        }
        Path show = season.getParent();
        if(show != null) {
            Path showName = show.getFileName();
            return showName == null ? null : showName.toString();
        }
        return null;
    }

    @Override
    public int matchSeason(Path episodePath) {
        Path season = Files.isDirectory(episodePath) ? episodePath : episodePath.getParent();
        Path seasonName;
        if(season != null && (seasonName = season.getFileName()) != null) {
            return matchSeason(seasonName.toString());
        }
        return EpisodeMatch.NO_SEASON;
    }
    
    /**
     * Match the season number for the given directory name
     * @param directoryName directory name to match
     * @return season number or {@link EpisodeMatch#NO_SEASON}
     */
    public static int matchSeason(String directoryName) {
        Matcher m = SEASON_PATTERN.matcher(directoryName);
        if(m.find()) {
            return Integer.valueOf(m.group(1));
        }
        return EpisodeMatch.NO_SEASON;
    }
    
}
