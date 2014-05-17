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

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a mapping of show names to their aliases.
 * For example:
 *   House of Cards -> House of Cards (2013)
 *   The Office -> The Office (US)
 *   IASIP -> It's Always Sunny in Philadelphia
 * @author Sam Malone
 */
public class AliasMap {
    
    private final Map<String, String> aliasMap;

    /**
     * Creates a new empty instance of AliasMap
     */
    public AliasMap() {
        aliasMap = new HashMap<>();
    }
    
    /**
     * Add an alias for the given show
     * @param show TV show
     * @param alias Alias for TV show 
     * @return this instance
     */
    public AliasMap addAlias(String show, String alias) {
        if(!aliasMap.containsKey(show)) {
            aliasMap.put(show, alias);
        }
        return this;
    }
    
    /**
     * Checks if the given TV show has an alias present
     * @param show TV show name
     * @return true if the TV show has an alias, false otherwise
     */
    public boolean hasAlias(String show) {
        return aliasMap.containsKey(show);
    }
    
    /**
     * Gets the show alias for the given show name
     * @param show TV show Name
     * @return TV show alias or null if show alias not present
     */
    public String getShowAlias(String show) {
        return aliasMap.get(show);
    }
    
}
