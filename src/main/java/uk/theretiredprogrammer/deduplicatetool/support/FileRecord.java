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
import java.util.Comparator;

public class FileRecord {
    
    private static final int MAXFILESSTATUSSTRINGLENGTH = 16;
    private static final int TABPOINT = MAXFILESSTATUSSTRINGLENGTH + 4;// minimum of 4 spaces tabbing
    
    public static enum FileStatus {
        NONE(false, true), DUPLICATE_IGNORE(true, false),
        PHOTOS_MASTER(true, true), PROPOSED_MASTER(true, true),
        TO_BE_DELETED(true, false), FILE_DELETED(true, false),
        CHECK_IS_WANTED(false, false), NOT_AN_IMAGE(true, false),
        FOLLOW_UP_LATER(true, true), KEEP_BUT_MOVE(true, true);
        
        private final boolean locked;
        private final boolean useinmatching;
        
        private FileStatus(boolean locked, boolean useinmatching) {
            this.locked = locked;
            this.useinmatching = useinmatching;
        }
        
        public boolean isLocked() {
            return locked;
        }
        
        public boolean isUseInMatching(){
            return useinmatching;
        }
    }
    
    public static final Comparator<FileRecord> COMPARE_FILEPATH = Comparator.comparing(FileRecord::getFilepath);
    public static final Comparator<FileRecord> COMPARE_FILENAME = Comparator.comparing(FileRecord::getFilename);
    public static final Comparator<FileRecord> COMPARE_FILENAMEEXT = Comparator.comparing(FileRecord::getFilenameExt);
    public static final Comparator<FileRecord> COMPARE_PARENTPATH = Comparator.comparing(FileRecord::getParentpath);
    public static final Comparator<FileRecord> COMPARE_DIGEST = Comparator.comparing(FileRecord::getDigest);
    public static final Comparator<FileRecord> COMPARE_FILESTATUS_FILEPATH = Comparator.comparing(FileRecord::getFileStatus).thenComparing(FileRecord::getFilepath);
    public static final Comparator<FileRecord> COMPARE_DIGEST_FILESIZE = Comparator.comparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize);
    public static final Comparator<FileRecord> COMPARE_FILEPATH_DIGEST_FILESIZE = Comparator.comparing(FileRecord::getFilepath).thenComparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize); 
    public static final Comparator<FileRecord> COMPARE_FILENAME_DIGEST_FILESIZE = Comparator.comparing(FileRecord::getFilename).thenComparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize); 

    public final String tag; 
    public final String path;
    public final String parentpath;
    public final String filename;
    public final String ext;
    public final String filenameext;
    public final String digest;
    public final int filesize;
    public FileStatus filestatus;
    public boolean hasMatch;
    public String hint;
    
    public FileRecord(String serialisedrecord) throws IOException {
        String[] serialisedrecordparts = serialisedrecord.split("§");
        if ((serialisedrecordparts.length == 4) || (serialisedrecordparts.length == 5)
                || (serialisedrecordparts.length == 6)|| (serialisedrecordparts.length == 7)) {
            // common for old and new formats
            tag = serialisedrecordparts[0];
            path = serialisedrecordparts[1];
            digest = serialisedrecordparts[2];
            filesize = Integer.parseInt(serialisedrecordparts[3]);
            File f = new File(path);
            filenameext = f.getName();
            int dotindex = filenameext.indexOf(".");
            if (dotindex == -1) {
                filename = filenameext;
                ext = "";
            } else {
                filename = filenameext.substring(0, dotindex);
                ext = filenameext.substring(dotindex+1);
            }
            parentpath = f.getParent();
            // handle this differently for various file versions
            filestatus = serialisedrecordparts.length > 4 ? FileStatus.valueOf(serialisedrecordparts[4]) : FileStatus.NONE;
            hasMatch = serialisedrecordparts.length >5 ? Boolean.parseBoolean(serialisedrecordparts[5]) : false;
            hint = serialisedrecordparts.length >6 ? serialisedrecordparts[6] : "";
        } else {
            throw new IOException("Badly formatted data source: " + serialisedrecord);
        }
    }
    
    public String getParentpath() {
        return parentpath;
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
    
    public String getFileExt() {
        return ext;
    }
    
    public String getFilenameExt() {
        return filenameext;
    }
    
    public int getFilesize() {
        return filesize;
    }
    
    public FileStatus getFileStatus() {
        return filestatus;
    }
    
    public boolean hasMatch() {
        return hasMatch;
    }
    
    public String getHint() {
        return hint;
    }
    
    @Override
    public String toString() {
        return tag+"§"+path+"§"+digest+"§"+Integer.toString(filesize)+"§"+filestatus+"§"+hasMatch+"§"+hint;
    }
    
    public String toReportString() {
        return path+'\n'+
                "    hasMatch="+hasMatch+
                "    status="+filestatus+
                "    hint="+hint+
                "    filesize="+filesize+
                "    tag="+tag+
                "    digest="+digest;
    }
    
    public String toListString() {
        return path;
    }
    
    public String toList2String() {
        StringBuilder sb = new StringBuilder();
        appendStringAndPadding(sb, filestatus.toString(), MAXFILESSTATUSSTRINGLENGTH, TABPOINT);
        appendStringAndPadding(sb, hasMatch ? "Has Matches" : "UnMatched", 11, 14);
        sb.append(path);
        if (!hint.isBlank()) {
            sb.append("  HINT:");
            sb.append(hint);
        }
        return sb.toString();
    }
    
    private void appendStringAndPadding(StringBuilder sb, String s, int maxstringlength, int fieldsize) {
        if (s.length()  > maxstringlength ) {
            s = s.substring(0, maxstringlength );
        }
        sb.append(s);
        for (int i = 0; i< fieldsize-s.length();i++) {
            sb.append(' ');
        }
    }
}
