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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * AbstractTVEpisode represents a TV episode consisting of a show name and season
 * number with the episode numbers being inherited from AbstractEpisode.
 * <p>
 * The {@link #equals(java.lang.Object)} and {@link #hashCode()} methods are
 * overridden. Episodes are considered equal if the show name, season number
 * and every episode number is the same. The hashCode implementation is
 * dependent on the show name, season number and episode numbers so implementors
 * should beware not to alter the episode when used with a {@link HashMap}
 * or {@link HashSet} for example.
 * @author Sam Malone
 */
public abstract class AbstractTVEpisode extends AbstractEpisode {
    
    /**
     * Season number is not known
     */
    public final static int NO_SEASON = -1;
    
    /**
     * Get the show name
     * @return Show name
     */
    public abstract String getShow();
    
    /**
     * Get the season
     * @return season or {@link #NO_SEASON}
     */
    public abstract int getSeason();
    
    /**
     * Split this EpisodeMatch into single episodes. E.g. s01e02e03 would be
     * split into two EpisodeMatch objects s01e02 and s01e03
     * @return 
     */
    public abstract List<? extends AbstractTVEpisode> toSplitEpisodeList();
    
    @Override
    public String toString() {
        int season = getSeason();
        if(season == NO_SEASON) {
            return super.toString();
        }
        return String.format("s%02d%s", getSeason(), super.toString());
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(getShow());
        hash = 43 * hash + getSeason();
        hash = 43 * hash + Objects.hashCode(getEpisodes());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final EpisodeMatch other = (EpisodeMatch) obj;
        if(!Objects.equals(getShow(), other.getShow())) {
            return false;
        }
        if(getSeason() != other.getSeason()) {
            return false;
        }
        return Objects.equals(getEpisodes(), other.getEpisodes());
    }
    
}
