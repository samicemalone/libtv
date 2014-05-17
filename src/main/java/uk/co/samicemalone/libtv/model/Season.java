/*
 * Copyright (c) 2013, Sam Malone. All rights reserved.
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

/**
 * Season contains a season number and the path to the episode files for the
 * season. It implements the {@link Comparable} interface to compare episodes
 * by season number.
 * @author Sam Malone
 */
public class Season implements Comparable<Season> {
    
    private final Path episodesPath;
    private final int seasonNo;
    
    /**
     * Create a new Season instance
     * @param season season number
     * @param episodesPath path to the episode files for the season
     */
    public Season(int season, Path episodesPath) {
        this.seasonNo = season;
        this.episodesPath = episodesPath;
    }

    /**
     * Gets the path to the directory containing the episode files for this
     * season
     * @return episode directory path for this season
     */
    public Path getPath() {
        return episodesPath;
    }
    
    /**
     * Get the season number
     * @return 
     */
    public int asInt() {
        return seasonNo;
    }
    
    /**
     * Gets the season number as a string, zero padded to at least 2 characters
     * @return Season String e.g "02"
     */
    public String asString() {
        return String.format("%02d", seasonNo);
    }

    @Override
    public int compareTo(Season o) {
        return Integer.compare(seasonNo, o.asInt());
    }
}
