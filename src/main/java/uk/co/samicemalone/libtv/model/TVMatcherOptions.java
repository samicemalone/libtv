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

import uk.co.samicemalone.libtv.matcher.SeasonMatcher;
import uk.co.samicemalone.libtv.matcher.ShowMatcher;
import uk.co.samicemalone.libtv.matcher.TVMatcher;
import uk.co.samicemalone.libtv.matcher.path.TVElementMatcher;

/**
 * TVMatcherOptions are used to determine how to match a TV episode.
 * The default matchers are set when constructing an instance.
 * Fallback matchers can also be set to be used if the default matchers cannot
 * match the TV show and season.
 * @see TVMatcher.Matcher
 * @author Sam Malone
 */
public class TVMatcherOptions {

    private ShowMatcher showMatcher;
    private SeasonMatcher seasonMatcher;
    private ShowMatcher fallbackShowMatcher;
    private SeasonMatcher fallbackSeasonMatcher;
    private boolean fallbackTVMatcherShow;
    private boolean fallbackTVMatcherSeason;

    /**
     * Create an instance of TVMatcherOptions that defers matching the TV show
     * and season to the implementation of {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)} 
     */
    public TVMatcherOptions() {
        
    }
    
    /**
     * Create an instance of TVMatcherOptions that defers matching the TV show
     * to showMatcher and defers matching the season to the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)}
     * @param showMatcher ShowMatcher for matching the TV show
     */
    public TVMatcherOptions(ShowMatcher showMatcher) {
        this.showMatcher = showMatcher;
    }
    
    /**
     * Create an instance of TVMatcherOptions that defers matching the TV show
     * to the implementation of {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)}
     * and defers matching the season to seasonMatcher
     * @param seasonMatcher SeasonMatcher for matching the season
     */
    public TVMatcherOptions(SeasonMatcher seasonMatcher) {
        this.seasonMatcher = seasonMatcher;
    }
    
    /**
     * Create an instance of TVMatcherOptions that defers matching the TV show
     * and season to tvPathMatcher
     * @param tvPathMatcher TVElementMatcher to defer TV show and season matching
     */
    public TVMatcherOptions(TVElementMatcher tvPathMatcher) {
        this((ShowMatcher) tvPathMatcher, (SeasonMatcher) tvPathMatcher);
    }
    
    /**
     * Create an instance of TVMatcherOptions that defers matching the TV show
     * to showMatcher and defers matching season to seasonMatcher
     * @param showMatcher TV show matcher
     * @param seasonMatcher Season matcher
     */
    public TVMatcherOptions(ShowMatcher showMatcher, SeasonMatcher seasonMatcher) {
        this.showMatcher = showMatcher;
        this.seasonMatcher = seasonMatcher;
    }
    
    /**
     * Get the SeasonMatcher to fall back to if no match was found via the
     * default SeasonMatcher
     * @return SeasonMatcher or null if no fallback season matcher is present
     */
    public SeasonMatcher getFallbackSeasonMatcher() {
        return fallbackSeasonMatcher;
    }

    /**
     * Get the ShowMatcher to fall back to if no match was found via the
     * default ShowMatcher
     * @return ShowMatcher or null if no fallback show matcher is present
     */
    public ShowMatcher getFallbackShowMatcher() {
        return fallbackShowMatcher;
    }

    /**
     * Get the SeasonMatcher to be used to match the season
     * @return SeasonMatcher or null to defer matching to the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)}
     */
    public SeasonMatcher getSeasonMatcher() {
        return seasonMatcher;
    }

    /**
     * Get the ShowMatcher to be used to match the show
     * @return ShowMatcher or null to defer matching to the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)}
     */
    public ShowMatcher getShowMatcher() {
        return showMatcher;
    }
    
    /**
     * Set the ShowMatcher to fall back to if the show isn't matched via the
     * default ShowMatcher
     * @param showMatcher ShowMatcher to fall back to
     * @return this instance
     */
    public TVMatcherOptions fallbackShow(ShowMatcher showMatcher) {
        fallbackShowMatcher = showMatcher;
        return this;
    }
    
    /**
     * Set the SeasonMatcher to fall back to if the season isn't matched via the
     * default SeasonMatcher
     * @param seasonMatcher SeasonMatcher to fall back to
     * @return this instance
     */
    public TVMatcherOptions fallbackSeason(SeasonMatcher seasonMatcher) {
        fallbackSeasonMatcher = seasonMatcher;
        return this;
    }
    
    /**
     * Set the ShowMatcher and SeasonMatcher to fall back to if the show or
     * season isn't matched via the default ShowMatcher or SeasonMatcher
     * @param showMatcher ShowMatcher to fall back to
     * @param seasonMatcher SeasonMatcher to fail back to
     * @return this instance
     */
    public TVMatcherOptions fallback(ShowMatcher showMatcher, SeasonMatcher seasonMatcher) {
        fallbackShowMatcher = showMatcher;
        fallbackSeasonMatcher = seasonMatcher;
        return this;
    }
    
    /**
     * Set the TVElementMatcher to fall back to if the show or season isn't 
     * matched via the default ShowMatcher or SeasonMatcher
     * @param tvPathMatcher TVElementMatcher to fall back to
     * @return this instance
     */
    public TVMatcherOptions fallback(TVElementMatcher tvPathMatcher) {
        return fallback((ShowMatcher) tvPathMatcher, (SeasonMatcher) tvPathMatcher);
    }
    
    /**
     * Fall back to matching the TV show and season using the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)} if
     * no match was found via the default ShowMatcher and SeasonMatcher
     * @return this instance
     */
    public TVMatcherOptions fallbackTVMatcher() {
        return fallbackTVMatcherShow().fallbackTVMatcherSeason();
    }
    
    /**
     * Fall back to matching the TV show using the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)} if
     * no match was found via the default ShowMatcher
     * @return this instance
     */
    public TVMatcherOptions fallbackTVMatcherShow() {
        fallbackTVMatcherShow = true;
        return this;
    }

    /**
     * Fall back to matching the season using the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)} if
     * no match was found via the default SeasonMatcher
     * @return this instance
     */
    public TVMatcherOptions fallbackTVMatcherSeason() {
        fallbackTVMatcherSeason = true;
        return this;
    }
    
    /**
     * Check if TV show matching should fall back to the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)} 
     * @return true if match should fall back to Matcher implementation, false otherwise
     */
    public boolean isFallbackTVMatcherShow() {
        return fallbackTVMatcherShow;
    }

    /**
     * Check if season matching should fall back to the implementation of
     * {@link TVMatcher.Matcher#match(java.nio.file.Path, java.lang.String)} 
     * @return true if match should fall back to Matcher implementation, false otherwise
     */
    public boolean isFallbackTVMatcherSeason() {
        return fallbackTVMatcherSeason;
    }

}