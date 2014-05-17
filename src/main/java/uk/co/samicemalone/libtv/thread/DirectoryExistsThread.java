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
package uk.co.samicemalone.libtv.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Sam Malone
 */
public class DirectoryExistsThread implements Callable<String> {
    
    private final String dir;
    
    /**
     * Create a new instance of DirectoryExistsThread
     * @param dir directory to check for existence
     */
    public DirectoryExistsThread(String dir) {
        this.dir = dir;
    }
    
    /**
     * Get a list of directories that exist from the given list of directories.
     * Wrapper for {@link #getExistingDirs(java.util.List, int)} with no timeout
     * @param dirs List of directory paths
     * @return List of directory paths that exist in dirs
     */
    public static List<String> getExistingDirs(List<String> dirs) {
        return getExistingDirs(dirs, 0);
    }
    
    /**
     * Get a list of directories that exist from the given list of directories.
     * A new thread is spawned for each directory being checked and will timeout
     * after the value of {@code timeout}. If a directory thread times out, it
     * assumes the directory does not exist.
     * @param dirs List of directory paths to check for existence
     * @param timeout timeout in milliseconds.
     * @return List of directory paths that exist in dirs
     */
    public static List<String> getExistingDirs(List<String> dirs, int timeout) {
        List<String> existentDirs = Collections.synchronizedList(new ArrayList<String>());
        if(dirs.isEmpty()) {
            return existentDirs;
        }
        ExecutorService es = Executors.newFixedThreadPool(dirs.size(), new DaemonThreadFactory());
        List<Callable<String>> threads = new ArrayList<>(dirs.size());
        for(String curDir : dirs) {
            threads.add(new DirectoryExistsThread(curDir));
        }
        try {
            List<Future<String>> futures = (timeout <= 0) ? es.invokeAll(threads) : es.invokeAll(threads, timeout, TimeUnit.MILLISECONDS);
            for(Future<String> future : futures) {
                if(!future.isCancelled() && future.get() != null) {
                    existentDirs.add(future.get());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            
        } finally {
            es.shutdownNow();
        }
        return existentDirs;
    }

    @Override
    public String call() throws Exception {
        return new File(dir).exists() ? dir : null;
    }
 
    /**
     * Daemon Thread Factory is used to prevent the JVM from waiting for any
     * unfinished threads to finish.
     */
    private static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
    
}
