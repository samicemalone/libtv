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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * EpisodeMatch represents a TV episode with a matching episode file.
 * @author Sam Malone
 */
public class EpisodeMatch extends AbstractTVEpisode {
    
    private String show;
    private int season;
    private File episodeFile;
    private List<Integer> episodes;

    public EpisodeMatch() {
        season = NO_SEASON;
        episodes = new ArrayList<>();
    }

    public EpisodeMatch(int episode) {
        this(null, NO_SEASON, episode);
    }

    public EpisodeMatch(int season, int episode) {
        this(null, season, episode);
    }

    public EpisodeMatch(String show, int episode) {
        this(show, NO_SEASON, episode);
    }

    public EpisodeMatch(String show, int season, int episode) {
        this(show, season, new ArrayList<Integer>());
        episodes.add(episode);
    }

    public EpisodeMatch(String show, int season, List<Integer> episodes) {
        this.show = show;
        this.season = season;
        this.episodes = episodes;
    }
    
    public EpisodeMatch(EpisodeMatch m) {
        this(m.getShow(), m.getSeason(), new ArrayList<>(m.getEpisodes()));
        episodeFile = m.getEpisodeFile();
    }
    
    /**
     * Add episode number to this match
     * @param episodes episode numbers to add
     */
    public void addEpisodeNo(int... episodes) {
        for(int e : episodes) {
            this.episodes.add(e);
        }
    }
    
    /**
     * Add episode number to this match
     * @param episodes episode numbers to add
     */
    public void addEpisodeNo(Collection<Integer> episodes) {
        for(int e : episodes) {
            this.episodes.add(e);
        }
    }

    /**
     * Get the TV show
     * @return TV show or null if not present
     */
    @Override
    public String getShow() {
        return show;
    }

    /**
     * Set TV show
     * @param show TV show 
     */
    public void setShow(String show) {
        this.show = show;
    }

    @Override
    public int getSeason() {
        return season;
    }

    /**
     * Set season number
     * @param season season or {@link #NO_SEASON}
     */
    public void setSeason(int season) {
        this.season = season;
    }

    @Override
    public List<Integer> getEpisodes() {
        return episodes;
    }

    /**
     * Get the episode File 
     * @return episode File or null if not present
     */
    public File getEpisodeFile() {
        return episodeFile;
    }

    /**
     * Set the episode File
     * @param episodeFile episode File
     */
    public void setEpisodeFile(File episodeFile) {
        this.episodeFile = episodeFile;
    }
    
    /**
     * Split this EpisodeMatch into single episodes. E.g. s01e02e03 would be
     * split into two EpisodeMatch objects s01e02 and s01e03
     * @return 
     */
    @Override
    public List<EpisodeMatch> toSplitEpisodeList() {
        List<EpisodeMatch> list = new ArrayList<>(episodes.size());
        for(Integer episodeNo : episodes) {
            EpisodeMatch m = new EpisodeMatch(show, season, episodeNo);
            m.setEpisodeFile(episodeFile);
            list.add(m);
        }
        return list;
    }
    
}
