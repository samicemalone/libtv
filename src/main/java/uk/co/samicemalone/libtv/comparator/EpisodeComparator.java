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
package uk.co.samicemalone.libtv.comparator;

import java.util.Comparator;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.Range;

/**
 * Comparator to compare EpisodeMatch instances using the following criteria:
 *   Show Name (ascending)
 *   Season (ascending)
 *   Starting episode in range (ascending)
 *   End episode in range (ascending)
 * <p>
 * E.g. 24 s01e01 &lt; 24 s01e01e02 &lt; 24 s01e01e02e03 &lt; 24 s01e02e03
 * @author Sam Malone
 */
public class EpisodeComparator implements Comparator<EpisodeMatch> {

    @Override
    public int compare(EpisodeMatch o1, EpisodeMatch o2) {
        if(o1 == o2) {
            return 0;
        }
        int compareVal = o1.getShow().compareTo(o2.getShow());
        if(compareVal != 0) {
            return compareVal;
        }
        compareVal = Integer.compare(o1.getSeason(), o2.getSeason());
        if(compareVal != 0) {
            return compareVal;
        }
        Range o1Range = o1.getEpisodesAsRange();
        Range o2Range = o2.getEpisodesAsRange();
        compareVal = Integer.compare(o1Range.getStart(), o2Range.getStart());
        if(compareVal != 0 || (!o1.isMultiEpisode() && !o2.isMultiEpisode())) {
            return compareVal;
        }
        return Integer.compare(o1Range.getEnd(), o2Range.getEnd());
    }
    
}
