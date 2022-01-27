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
//import java.util.ArrayList;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
//import java.util.Iterator;
//import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
//import java.util.stream.Collectors;
import uk.theretiredprogrammer.deduplicatetool.support.FileManager;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_DIGEST;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_DIGEST_FILESIZE;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILENAME;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILENAME_DIGEST_FILESIZE;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILEPATH;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILEPATH_DIGEST_FILESIZE;
//import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_PARENTPATH;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecords;
//import uk.theretiredprogrammer.deduplicatetool.support.FileRecords;

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
        String subcommand = checkOptionsSyntax("statusreport", "statusreview", "unmatched-auto-fix-report", "unmatched-auto-fix");
        switch (subcommand) {

            case "statusreview" -> {
                checkTokenCount(2);
                MatchStatusReviewCommandProcessor reviewCP = new MatchStatusReviewCommandProcessor(model);
                reviewCP.executeSYSIN();
            }

            case "statusreport" -> {
                checkTokenCount(3);
                File path = checkSyntaxAndFILEPATH();
                try ( PrintWriter wtr = FileManager.openWriter(path)) {
                    buildandoutputstatusreport(wtr);
                }
            }

            case "unmatched-auto-fix-report" -> {
                checkTokenCount(3);
                File path = checkSyntaxAndFILEPATH();
                try ( PrintWriter wtr = FileManager.openWriter(path)) {
                    model.stream().map(fr -> fr.parentpath).distinct().forEachOrdered(pp -> {
                        List<FileRecord> files = model.stream().filter(fr -> fr.parentpath.equals(pp)).collect(Collectors.toList());
                        long matchedNONECount = files.stream().filter(fr -> fr.getFileStatus().equals(FileStatus.NONE) && fr.hasMatch()).count();
                        long unmatchedNONECount = files.stream().filter(fr -> fr.getFileStatus().equals(FileStatus.NONE) && !fr.hasMatch()).count();
                        if (matchedNONECount == 0 && unmatchedNONECount > 0) {
                            wtr.println(pp);
                        }
                    });
                }
            }

            case "unmatched-auto-fix" -> {
                checkTokenCount(2);
                model.stream().map(fr -> fr.parentpath).distinct().forEachOrdered(pp -> {
                    List<FileRecord> files = model.stream().filter(fr -> fr.parentpath.equals(pp)).collect(Collectors.toList());
                    long matchedNONECount = files.stream().filter(fr -> fr.getFileStatus().equals(FileStatus.NONE) && fr.hasMatch()).count();
                    long unmatchedNONECount = files.stream().filter(fr -> fr.getFileStatus().equals(FileStatus.NONE) && !fr.hasMatch()).count();
                    if (matchedNONECount == 0 && unmatchedNONECount > 0) {
                        files.stream().filter(fr -> fr.getFileStatus().equals(FileStatus.NONE) && !fr.hasMatch()).forEachOrdered(fr-> fr.setFileStatus(FileStatus.KEEP_BUT_MOVE));
                    }
                });
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    private void buildandoutputstatusreport(PrintWriter writer) {
        Map<String, FileRecords> parentsFileRecords = model.getParentsFileRecords();
        for (var parentFileRecords : parentsFileRecords.entrySet()) {
            processParentpathSet(parentFileRecords.getValue(), writer);
        }
    }

    private void processParentpathSet(FileRecords ppset, PrintWriter writer) {
        Map<String, Integer> record = new HashMap<>();
        boolean hasNONEfile = false;
        for (FileRecord fr : ppset) {
            String fsname = fr.getFileStatusName();
            String hashname = fsname.equals("NONE") ? "NONE" + (fr.hasMatch ? "+" : "-") + "Matches" : fsname;
            if (record.containsKey(hashname)) {
                record.put(hashname, record.get(hashname) + 1);
            } else {
                record.put(hashname, 1);
            }
            if (fsname.equals("NONE")) {
                hasNONEfile = true;
            }
        }
        if (hasNONEfile) {
            writer.println("ACT: " + ppset.get(0).parentpath);
            ppset.stream().filter(fr -> (fr.getFileStatus().equals(FileStatus.NONE))).forEachOrdered(fr -> writer.println("        " + fr.toFilenameListString()));
        } else {
            writer.println("OK:  " + ppset.get(0).parentpath);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("        ");
        for (var e : record.entrySet()) {
            sb.append(e.getValue());
            sb.append(" cases of ");
            sb.append(e.getKey());
            sb.append(";  ");
        }
        writer.println(sb.toString());
        writer.println();
    }
}
