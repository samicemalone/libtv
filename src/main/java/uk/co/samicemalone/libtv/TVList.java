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
package uk.co.samicemalone.libtv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists the video files in a directory
 * @author Sam Malone
 */
public class TVList {

    private final ArrayList<String> list;

    public TVList() {
        list = new ArrayList<>();
    }
    
    public List<String> getTVList() {
        return list;
    }
    
    /**
     * Scan the file given or scan the directory given recursively for video
     * files
     * @param file file or directory
     */
    public void scan(File file) {
        scan(file, true);
    }

    /**
     * Scan the file/directory given for video files. If the File given is
     * a directory, and recursive is true, All sub directories will be scanned
     * recursively for video files. Otherwise only files in the files in the
     * directory will be scanned.
     * @param file file or directory that exists
     * @param recursive if true, scan recursively if file is a directory
     */
    public void scan(File file, boolean recursive) {
        if(file.isDirectory()) {
            File[] sub = file.listFiles(new VideoFilter(recursive));
            if(sub == null) {
                return;
            }
            for(File f : sub) {
                if(f.isDirectory() && recursive) {
                    scan(f);
                } else {
                    list.add(f.getAbsolutePath());
                }
            }
        } else {
            list.add(file.getAbsolutePath());
        }
    }
    
}
