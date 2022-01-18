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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.deduplicatetool.support.FileManager;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecordSet;

public class Match extends Command {

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

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(2, 3);
        checkSyntax("match");
        String subcommand = checkOptionsSyntax("create", "review", "report");
        switch (subcommand) {
            case "create" -> {
                checkTokenCount(3);
                String option = checkOptionsSyntax("filepath", "digest", "filename", "filepath-digest-filesize", "digest-filesize", "filename-digest-filesize");
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
            case "review" -> {
                checkTokenCount(2);
                MatchReviewCommandProcessor reviewCP = new MatchReviewCommandProcessor(model);
                reviewCP.executeSYSIN();
            }
            case "report" -> {
                checkTokenCount(3);
                File path = checkSyntaxAndFILEPATH();
                try ( PrintWriter wtr = FileManager.openWriter(path)) {
                    for (FileRecordSet frs : model.getMatchRecords()) {
                        for (FileRecord fr : frs) {
                            wtr.println(fr.toReportString());
                        }
                        wtr.println();
                    }
                }

            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
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

    @SuppressWarnings("null")
    private void findMatches(MatchType matchtype, Comparator<FileRecord> comparefilerecords) {
        model.clearMatchRecords();
        List<FileRecord> set = new ArrayList<>(model);
        var orderedset = set.stream().filter((fr) -> fr.filestatus.isUseInMatching()).collect(Collectors.toList());
        orderedset.sort(comparefilerecords);
        if (orderedset.size() > 1) {
            Iterator<FileRecord> iterator = orderedset.iterator();
            FileRecord current = iterator.next();
            boolean induplicateset = false;
            FileRecordSet rec = null;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                if (comparefilerecords.compare(current, possibleduplicate) == 0) {
                    if (induplicateset) {
                        rec.add(possibleduplicate);
                    } else {
                        rec = new FileRecordSet();
                        rec.add(current);
                        rec.add(possibleduplicate);
                        induplicateset = true;
                    }
                } else {
                    if (induplicateset) {
                        induplicateset = false;
                        model.addToMatchRecords(rec);
                    }
                    current = possibleduplicate;
                }
            }
        }
        System.out.println("MATCHES: type=" + matchtype.description + ", number of matches=" + model.getMatchRecords().size());
    }
}
