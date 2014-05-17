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
import java.util.Collection;
import uk.co.samicemalone.libtv.exception.SeasonsPathNotFoundException;
import uk.co.samicemalone.libtv.model.AliasMap;

/**
 * AliasedTVLibrary extends StandardTVLibrary to provide support for TV show
 * aliases. This can be useful for foreign remakes of shows or shows with the
 * duplicate names that are released in different years.
 * For example, "House of Cards" could refer to "House of Cards (UK)",
 * "House of Cards (US)", "House of Cards (1990)" or  House of Cards (2013).
 * @author Sam Malone
 */
public class AliasedTVLibrary extends StandardTVLibrary {
    
    private final AliasMap aliasMap;

    /**
     * Create a new instance of AliasedTVLibrary
     * @param tvSources Source TV root paths e.g. [/mnt/TV, /media/TV]
     * @param aliasMap Map of show names to their aliases
     */
    public AliasedTVLibrary(Collection<String> tvSources, AliasMap aliasMap) {
        super(tvSources);
        this.aliasMap = aliasMap;
    }

    @Override
    public Path getSeasonsPath(String show) {
        Path p = super.getSeasonsPath(show);
        if(p == null) {
            String alias = aliasMap.getShowAlias(show);
            if(alias != null) {
                p = super.getSeasonsPath(alias);
            }
        }
        return p;
    }

    @Override
    public Path newEpisodesPath(String show, int season, StandardTVPath.SeasonFormat format) throws IOException {
        try {
            return super.newEpisodesPath(show, season, format);
        } catch(SeasonsPathNotFoundException ex) {
            String alias = aliasMap.getShowAlias(show);
            if(alias != null) {
                return super.newEpisodesPath(alias, season, format);
            }
            throw ex;
        }
    }
    
}
