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

/**
 * Utility class for converting a String into TV show case. TV show case is a
 * variant of title case but makes exceptions to capitalise bracketed strings
 * which are common for foreign TV show remakes e.g. the.office.(us) =&gt; The Office (US)
 * @author Sam Malone
 */
public class ShowCase {
    
    private static final String[] titleExceptionsTwoChar = new String[] {
        "As", "At", "By", "In", "Of", "On", "Or", "To", "Vs", "VS"
    };
    
    private static final String[] titleExceptionsThreeChar = new String[] {
        "And", "For", "The"
    };
    
    private static final String[] titleExceptionsFourChar = new String[] {
        "From", "With"
    };
    
    /**
     * Converts a String into TV show case. TV show case is variant of title case
     * but makes exceptions to capitalise bracketed strings which are common
     * for foreign TV show remakes e.g. the.office.(us) =&gt; The Office (US)
     * <p>
     * This implementation treats the characters [_.] as separators which
     * can give a non optimal results for shows which include these characters
     * e.g. the o.c. =&gt; The O C
     * @param s String to be converted to title case
     * @return String s in title case
     */
    public static String convert(String s) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        boolean forceTitleCase = false;
        char[] chars = s.replaceAll("[_.]+", " ").toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isSpaceChar(chars[i]) || chars[i] == '-') {
                nextTitleCase = true;
            } else if(chars[i] == '(') {
                forceTitleCase = true;
            } else if(chars[i] == ')') {
                forceTitleCase = false;
            } else if (nextTitleCase || forceTitleCase) {
                chars[i] = Character.toTitleCase(chars[i]);
                nextTitleCase = false;
            }
            titleCase.append(chars[i]);
        }
        return fixWordCase(titleCase.toString());
    }
    
    /**
     * Fix any common words that should not be capitalised in show case.
     * E.g. From =&gt; from, And =&gt; and, Of =&gt; of etc.. 
     * @param show title case show to fix common words
     * @return show
     */
    private static String fixWordCase(String show) {
        String[] words = show.split(" +");
        for(int i = 1; i < words.length; i++) {
            String[] exceptions;
            switch(words[i].length()) {
                case 1:
                    exceptions = new String[] { "A" };
                    break;
                case 2:
                    exceptions = titleExceptionsTwoChar;
                    break;
                case 3:
                    exceptions = titleExceptionsThreeChar;
                    break;
                case 4:
                    exceptions = titleExceptionsFourChar;
                    break;
                default:
                    exceptions = new String[] {};
            }
            for(String word : exceptions) {
                if(word.equals(words[i])) {
                    words[i] = words[i].toLowerCase();
                    break;
                }
            }
        }
        return String.join(" ", words);
    }
}
