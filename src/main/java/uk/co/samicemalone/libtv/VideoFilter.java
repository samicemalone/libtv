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
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Filter video files in a directory with the option of accepting directories
 * @author Sam Malone
 */
public class VideoFilter implements FileFilter, DirectoryStream.Filter<Path> {
    
    private final boolean allowDirectories;
    
    private final String[] validExtensions = new String[] {
        "mkv", "mp4", "avi", "mpg", "mpeg", "mov"
    };
    
    public VideoFilter() {
        this.allowDirectories = false;
    }
    
    public VideoFilter(boolean allowDirectories) {
        this.allowDirectories = allowDirectories;
    }

    @Override
    public boolean accept(File file) {
        try {
            return accept(file.toPath());
        } catch(IOException ex) {
            return false;
        }
    }

    @Override
    public boolean accept(Path entry) throws IOException {
        if(allowDirectories && Files.isDirectory(entry)) {
            return true;
        }
        Path filename = entry.getFileName();
        if(filename != null) {
            for(String ext : validExtensions) {
                if(filename.toString().endsWith(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
