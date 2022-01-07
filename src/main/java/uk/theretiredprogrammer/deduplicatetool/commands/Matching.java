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
        FILENAME("filename");

        public final String description;

        private MatchType(String description) {
            this.description = description;
        }
    }

    private void findMatchesUsingFilepath() {
        findMatches(MatchType.PATH, Comparator.comparing(FileRecord::getFilepath));
    }

    private void findMatchesUsingDigest() {
        findMatches(MatchType.DIGEST, Comparator.comparing(FileRecord::getDigest));
    }

    private void findMatchesUsingFilename() {
        findMatches(MatchType.FILENAME, Comparator.comparing(FileRecord::getFilename));//.thenComparing(...)
    }

    @Override
    public Command.ActionResult execute() throws IOException {
        int l = checkTokenCount(1, 4);
        if (l == 1) {

        } else {
            checkSyntax("match", "duplicates", "using");
            String option = checkOptionsSyntax("filepath", "digest", "filename");
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
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    @SuppressWarnings("null")
    private void findMatches(MatchType matchtype, Comparator<FileRecord> comparefilerecords) {
        List<FileRecord> allrecords = model.getAllFileRecords();
        allrecords.sort(comparefilerecords);
        if (allrecords.size() > 1) {
            ListIterator<FileRecord> iterator = allrecords.listIterator();
            FileRecord current = iterator.next();
            boolean induplicateset = false;
            MatchRecord rec = null;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                if (comparefilerecords.compare(current, possibleduplicate) == 0) {
                    //if (current.path.equals(possibleduplicate.path)) {
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
