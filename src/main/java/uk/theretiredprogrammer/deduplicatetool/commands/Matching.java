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
package uk.theretiredprogrammer.deduplicatetool.commands;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.MatchRecord;

public class Matching extends Command {

    public static enum MatchType {
        PATH("filepath"),
        DIGEST("digest"),
        FILENAME("filename"),
        PATH_DIGEST_SIZE("filepath,digest,filesize"),
        DIGEST_SIZE("digest,filesize"),
        FILENAME_DIGEST_SIZE("filename_digest,filesize");

        public final String description;

        private MatchType(String description) {
            this.description = description;
        }
    }

    private void findMatchesUsingFilePath_Digest_Filesize() {
        findMatches(MatchType.PATH_DIGEST_SIZE, Comparator.comparing(FileRecord::getFilepath).thenComparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize));
    }

    private void findMatchesUsingFilepath() {
        findMatches(MatchType.PATH, Comparator.comparing(FileRecord::getFilepath));
    }

    private void findMatchesUsingDigest() {
        findMatches(MatchType.DIGEST, Comparator.comparing(FileRecord::getDigest));
    }
    
    private void findMatchesUsingDigest_Filesize() {
        findMatches(MatchType.DIGEST_SIZE, Comparator.comparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize));
    }

    private void findMatchesUsingFilename() {
        findMatches(MatchType.FILENAME, Comparator.comparing(FileRecord::getFilename));
    }
    
    private void findMatchesUsingFilename_Digest_Filesize() {
        findMatches(MatchType.FILENAME_DIGEST_SIZE, Comparator.comparing(FileRecord::getFilename).thenComparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize));
    }

    @Override
    public Command.ActionResult execute() throws IOException {
        int l = checkTokenCount(1, 3);
        if (l == 1) {
            findMatchesUsingFilePath_Digest_Filesize();
            findMatchesUsingFilepath();
            findMatchesUsingDigest();
            findMatchesUsingDigest_Filesize();
            findMatchesUsingFilename();
            findMatchesUsingFilename_Digest_Filesize();
        } else {
            checkSyntax("match", "by");
            String option = checkOptionsSyntax("filepath", "digest", "filename", "filepath-digest-filesize","digest-filesize", "filename-digest-filesize");
            switch (option) {
                case "filepath" -> {
                    findMatchesUsingFilepath();
                }
                case "digest" -> {
                    findMatchesUsingDigest();
                }
                case "filename" -> {
                    findMatchesUsingFilename();
                }
                case "filepath-digest-filesize" -> {
                    findMatchesUsingFilePath_Digest_Filesize();
                }
                case "digest-filesize" -> {
                    findMatchesUsingDigest_Filesize();
                }
                case "filename-digest-filesize" -> {
                    findMatchesUsingFilename_Digest_Filesize();
                }
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    @SuppressWarnings("null")
    private void findMatches(MatchType matchtype, Comparator<FileRecord> comparefilerecords) {
        List<FileRecord> allrecords = model.getAllProcessableFileRecords();
        allrecords.sort(comparefilerecords);
        if (allrecords.size() > 1) {
            ListIterator<FileRecord> iterator = allrecords.listIterator();
            FileRecord current = iterator.next();
            boolean induplicateset = false;
            MatchRecord rec = null;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                if (comparefilerecords.compare(current, possibleduplicate) == 0) {
                    if (induplicateset) {
                        rec.add(possibleduplicate);
                    } else {
                        rec = new MatchRecord(matchtype, current);
                        rec.add(possibleduplicate);
                        induplicateset = true;
                    }
                } else {
                    if (induplicateset) {
                        induplicateset = false;
                        model.add(rec);
                    }
                    current = possibleduplicate;
                }
            }
        }
        System.out.println("MATCHES: type=" + matchtype.description + ", number=" + model.getAllMatchRecords().size());
    }
}
