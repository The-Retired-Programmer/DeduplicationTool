/*
 * Copyright 2022 Richard Linsdale (richard at theretiredprogrammer.uk).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.deduplicatetool.support;

import java.io.File;
import java.io.IOException;

public class FileRecord {
    
    public final String tag; 
    public final String path; 
    public final String filename;
    public final String filenameext;
    public final String digest;
    public final int filesize;
    
    
    public FileRecord(String serialisedrecord) throws IOException {
        String[] serialisedrecordparts = serialisedrecord.split("§");
        if (serialisedrecordparts.length != 4 ) {
            throw new IOException("Badly formatted data source: "+serialisedrecord);
        }
        tag = serialisedrecordparts[0];
        path = serialisedrecordparts[1];
        digest = serialisedrecordparts[2];
        filesize = Integer.parseInt(serialisedrecordparts[3]);
        File f = new File(path);
        filenameext = f.getName();
        int dotindex = filenameext.indexOf(".");
        if (dotindex == -1) {
            filename = filenameext;
        } else {
            filename = filenameext.substring(0, dotindex);
        }
    }
    
    public String getFilepath() {
        return path;
    }
    
    public String getDigest() {
        return digest;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String toPrintString() {
        return path+"\n"+ Integer.toString(filesize) + "; " + digest;
    }
    
    @Override
    public String toString() {
        return tag+"§"+path+"§"+digest+"§"+Integer.toString(filesize);
    }
}
