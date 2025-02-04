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

import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Sam Malone
 */
public class RangeTest {

    /**
     * Test of contains method, of class Range.
     */
    @Test
    public void testContainsRange() {
        Range instance = new Range(2, 20);
        Range range = new Range(5, 10);
        boolean result = instance.contains(range);
        assertTrue(result);
    }

    /**
     * Test of contains method, of class Range.
     */
    @Test
    public void testContainsRangeInclusive() {
        Range instance = new Range(2, 20);
        Range range = new Range(2, 20);
        boolean result = instance.contains(range);
        assertTrue(result);
    }

    /**
     * Test of contains method, of class Range.
     */
    @Test
    public void testContains_int() {
        Range r = new Range(1, 8);
        assertTrue(r.contains(1));
        assertTrue(r.contains(7));
        assertTrue(r.contains(8));
        assertFalse(r.contains(9));
        assertFalse(r.contains(-1));
    }

    /**
     * Test of iterator method, of class Range.
     */
    @Test
    public void testIterator() {
        int i = 1;
        Iterator<Integer> result = new Range(1, 3).iterator();
        assertTrue(result.hasNext());
        assertEquals(Integer.valueOf(i++), result.next());
        assertTrue(result.hasNext());
        assertEquals(Integer.valueOf(i++), result.next());
        assertTrue(result.hasNext());
        assertEquals(Integer.valueOf(i++), result.next());
        assertFalse(result.hasNext());
    }

    /**
     * Test of hasNext method, of class Range.
     */
    @Test
    public void testHasNext() {
        Range instance = new Range(-3, 3);
        Iterator<Integer> it = instance.iterator();
        for(int i = -3; i <= 3; i++) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
    }

    /**
     * Test of next method, of class Range.
     */
    @Test
    public void testNext() {
        Range instance = new Range(1, 3);
        Iterator<Integer> it = instance.iterator();
        for(int i = 1; i <= 3; i++) {
            assertEquals(Integer.valueOf(i), it.next());
        }
    }
    
}
