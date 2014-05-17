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
package uk.co.samicemalone.libtv.util;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Sam Malone
 */
public class ShowCaseTest {

    /**
     * Test of toShowCase method, of class ShowMatcher.
     */
    @Test
    public void testToShowCase() {
        Map<String, String> shows = new HashMap<>();
        shows.put("scrubs", "Scrubs");
        shows.put("the.office.(us)", "The Office (US)");
        shows.put("brooklyn nine-nine", "Brooklyn Nine-Nine");
        shows.put("house of cards (2013)", "House of Cards (2013)");
        shows.put("The fresh prince of Bel-Air", "The Fresh Prince of Bel-Air");
        shows.put("parks And recreation", "Parks and Recreation");
        shows.put("it's always sunny in philadelphia", "It's Always Sunny in Philadelphia");
        shows.put("the IT crowd", "The IT Crowd");
        shows.put("man VS food", "Man vs Food");
        shows.put("10 things i hate about you", "10 Things I Hate About You");
        shows.put("only in america with larry the cable guy", "Only in America with Larry the Cable Guy");
        shows.put("two_and_a_half_men", "Two and a Half Men");
        for(String testShow : shows.keySet()) {
            String result = ShowCase.convert(testShow);
            assertEquals(shows.get(testShow), result);
        }
    }
    
}