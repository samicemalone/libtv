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

/**
 * Range represents all the integer values from the starting number to the ending
 * number
 * @author Sam Malone
 */
public class Range implements Iterable<Integer>, Iterator<Integer> {

    /**
     * Create a new instance of Range with the given start value and the
     * maximum possible end value.
     * @param start starting number in the range
     * @return Range between start and {@link Integer#MAX_VALUE}
     */
    public static Range maxRange(int start) {
        return new Range(start, Integer.MAX_VALUE);
    }

    private final int start;
    private final int end;
    
    private int cur = Integer.MAX_VALUE;

    /**
     * Create a new instance of Range with same start and end value
     * @param value start and end value
     */
    public Range(int value) {
        this.start = value;
        this.end = value;
    }

    /**
     * Create a new instance of Range
     * @param start starting value in the range
     * @param end ending value in the range
     */
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the ending value in this range
     * @return 
     */
    public int getEnd() {
        return end;
    }

    /**
     * Get the starting value in this range
     * @return 
     */
    public int getStart() {
        return start;
    }
    
    /**
     * Check if each value in the Range given, is contained within this Range. 
     * In other words, check if the given range is a subset of this Range.
     * @param range Range
     * @return true if range is contained within this Range, false otherwise
     */
    public boolean contains(Range range) {
        return range.getEnd() >= start && range.getStart() <= end;
    }
    
    /**
     * Check if the value given is contained within this Range.
     * @param value value
     * @return true if the value is contained within this Range, false otherwise
     */
    public boolean contains(int value) {
        return value >= start && value <= end;
    }

    @Override
    public Iterator<Integer> iterator() {
        cur = start;
        return this;
    }

    @Override
    public boolean hasNext() {
        return cur <= end;
    }

    @Override
    public Integer next() {
        return cur++;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing from range not supported");
    }

    @Override
    public String toString() {
        return String.format("%d - %d", start, end);
    }
    
}
