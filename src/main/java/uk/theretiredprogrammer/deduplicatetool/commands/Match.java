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
//import java.util.Iterator;
//import java.util.List;
import java.util.Map;
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
        String subcommand = checkOptionsSyntax(/*"create", "review", "report", "mark", "parentpathreport", */"statusreport", "statusreview");
        switch (subcommand) {
//            case "create" -> {
//                checkTokenCount(3);
//                String option = checkOptionsSyntax("filepath", "digest", "filename", "filepath-digest-filesize", "digest-filesize", "filename-digest-filesize");
//                switch (option) {
//                    case "filepath" -> {
//                        findMatchesUsingFilepath();
//                    }
//                    case "digest" -> {
//                        findMatchesUsingDigest();
//                    }
//                    case "filename" -> {
//                        findMatchesUsingFilename();
//                    }
//                    case "filepath-digest-filesize" -> {
//                        findMatchesUsingFilePath_Digest_Filesize();
//                    }
//                    case "digest-filesize" -> {
//                        findMatchesUsingDigest_Filesize();
//                    }
//                    case "filename-digest-filesize" -> {
//                        findMatchesUsingFilename_Digest_Filesize();
//                    }
//                }
//            }
//            
//            case "review" -> {
//                checkTokenCount(2);
//                MatchReviewCommandProcessor reviewCP = new MatchReviewCommandProcessor(model);
//                reviewCP.executeSYSIN();
//            }
            
            case "statusreview" -> {
                checkTokenCount(2);
                MatchStatusReviewCommandProcessor reviewCP = new MatchStatusReviewCommandProcessor(model);
                reviewCP.executeSYSIN();
            }
            
//            case "parentpathreport" -> {
//                checkTokenCount(3);
//                File path = checkSyntaxAndFILEPATH();
//
//                try ( PrintWriter wtr = FileManager.openWriter(path)) {
//                    for (ParentCount pc : findmatchedbyparentpathandcount()) {
//                        wtr.println(pc.toString());
//                    }
//                }
//            }
//            
//            case "report" -> {
//                checkTokenCount(3);
//                File path = checkSyntaxAndFILEPATH();
//                try ( PrintWriter wtr = FileManager.openWriter(path)) {
//                    for (FileRecords frs : model.getMatchRecords()) {
//                        for (FileRecord fr : frs) {
//                            wtr.println(fr.toReportString());
//                        }
//                        wtr.println();
//                    }
//                }
//            }
            
            case "statusreport" -> {
                checkTokenCount(3);
                File path = checkSyntaxAndFILEPATH();
                try ( PrintWriter wtr = FileManager.openWriter(path)) {
                    buildandoutputstatusreport(wtr);
                }
            }
            
//            case "mark" -> {
//                checkTokenCount(2);
//                mark();
//            }

        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

//    private void findMatchesUsingFilePath_Digest_Filesize() {
//        findMatches(MatchType.PATH_DIGEST_SIZE, COMPARE_FILEPATH_DIGEST_FILESIZE);
//    }
//
//    private void findMatchesUsingFilepath() {
//        findMatches(MatchType.PATH, COMPARE_FILEPATH);
//    }
//
//    private void findMatchesUsingDigest() {
//        findMatches(MatchType.DIGEST, COMPARE_DIGEST);
//    }
//
//    private void findMatchesUsingDigest_Filesize() {
//        findMatches(MatchType.DIGEST_SIZE, COMPARE_DIGEST_FILESIZE);
//    }
//
//    private void findMatchesUsingFilename() {
//        findMatches(MatchType.FILENAME, COMPARE_FILENAME);
//    }
//
//    private void findMatchesUsingFilename_Digest_Filesize() {
//        findMatches(MatchType.FILENAME_DIGEST_SIZE, COMPARE_FILENAME_DIGEST_FILESIZE);
//    }

//    @SuppressWarnings("null")
//    private void findMatches(MatchType matchtype, Comparator<FileRecord> comparefilerecords) {
//        model.clearMatchRecords();
//        List<FileRecord> set = new ArrayList<>(model);
//        var orderedset = set.stream().filter((fr) -> fr.filestatus.isUseInMatching()).collect(Collectors.toList());
//        orderedset.sort(comparefilerecords);
//        if (orderedset.size() > 1) {
//            Iterator<FileRecord> iterator = orderedset.iterator();
//            FileRecord current = iterator.next();
//            boolean induplicateset = false;
//            FileRecords rec = null;
//            while (iterator.hasNext()) {
//                FileRecord possibleduplicate = iterator.next();
//                if (comparefilerecords.compare(current, possibleduplicate) == 0) {
//                    if (induplicateset) {
//                        rec.add(possibleduplicate);
//                    } else {
//                        rec = new FileRecords();
//                        rec.add(current);
//                        rec.add(possibleduplicate);
//                        induplicateset = true;
//                    }
//                } else {
//                    if (induplicateset) {
//                        induplicateset = false;
//                        model.addToMatchRecords(rec);
//                    }
//                    current = possibleduplicate;
//                }
//            }
//        }
//        System.out.println("MATCHES: type=" + matchtype.description + ", number of matches=" + model.getMatchRecords().size());
//    }
//
//    @SuppressWarnings("null")
//    private void mark() {
//        List<FileRecord> orderedset = new ArrayList<>(model);
//        orderedset.sort(COMPARE_DIGEST_FILESIZE);
//        if (!orderedset.isEmpty()) {
//            Iterator<FileRecord> iterator = orderedset.iterator();
//            FileRecord current = iterator.next();
//            current.hasMatch = false;
//            boolean induplicateset = false;
//            while (iterator.hasNext()) {
//                FileRecord possibleduplicate = iterator.next();
//                possibleduplicate.hasMatch = false;
//                if (COMPARE_DIGEST_FILESIZE.compare(current, possibleduplicate) == 0) {
//                    if (induplicateset) {
//                        possibleduplicate.hasMatch = true;
//                    } else {
//                        current.hasMatch = true;
//                        possibleduplicate.hasMatch = true;
//                        induplicateset = true;
//                    }
//                } else {
//                    if (induplicateset) {
//                        induplicateset = false;
//                    }
//                    current = possibleduplicate;
//                }
//            }
//        }
//        System.out.println("Mark: number of records matched=" + getMatchedCount() + "; number of records unmatched=" + getUnmatchedCount());
//    }

//    private long getMatchedCount() {
//        return model.stream().filter((fr) -> fr.hasMatch).count();
//    }
//
//    private long getUnmatchedCount() {
//        return model.stream().filter((fr) -> !fr.hasMatch).count();
//    }
    
    private void buildandoutputstatusreport(PrintWriter writer) {
        Map<String,FileRecords> parentsFileRecords = model.getParentsFileRecords();
        for (var parentFileRecords: parentsFileRecords.entrySet()) {
            processParentpathSet(parentFileRecords.getValue(),writer);
        }
    }
    
    private void processParentpathSet(FileRecords ppset, PrintWriter writer){
        Map<String,Integer> record = new HashMap<>();
        boolean hasNONEfile = false;
        for ( FileRecord fr : ppset) {
            String fsname = fr.filestatus.toString();
            String hashname = fsname.equals("NONE")? "NONE"+(fr.hasMatch?"+":"-")+"Matches":fsname;
            if (record.containsKey(hashname)){
                record.put(hashname,record.get(hashname)+1);
            } else {
                record.put(hashname, 1);
            }
            if (fsname.equals("NONE")) {
                hasNONEfile = true;
            }
        }
        if (hasNONEfile) {
            writer.println("ACT: "+ppset.get(0).parentpath);
            for ( FileRecord fr : ppset) {
                if (fr.filestatus.equals(FileStatus.NONE)) {
                    writer.println("        "+fr.toList2String());
                }    
            }
        } else {
            writer.println("OK:  "+ppset.get(0).parentpath);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("        ");
        for (var e: record.entrySet()){
            sb.append(e.getValue());
            sb.append(" cases of ");
            sb.append(e.getKey());
            sb.append(";  ");
        }
        writer.println(sb.toString());
        writer.println();
    }
    

//    private List<ParentCount> findmatchedbyparentpathandcount() {
//        List<ParentCount> parentcounts = new ArrayList<>();
//        List<FileRecord> orderedset = new ArrayList<>(model);
//        orderedset.sort(COMPARE_FILEPATH);
//        if (!orderedset.isEmpty()) {
//            Iterator<FileRecord> iterator = orderedset.iterator();
//            FileRecord current = iterator.next();
//            int insameparentcount = 1;
//            int countmatches = current.hasMatch ? 1 : 0;
//            while (iterator.hasNext()) {
//                FileRecord possiblesameparent = iterator.next();
//                if (COMPARE_PARENTPATH.compare(current, possiblesameparent) == 0) {
//                    insameparentcount++;
//                    if (possiblesameparent.hasMatch) {
//                        countmatches++;
//                    }
//                } else {
//                    parentcounts.add(new ParentCount(current.parentpath, insameparentcount, countmatches));
//                    current = possiblesameparent;
//                    insameparentcount = 1;
//                    countmatches = current.hasMatch ? 1 : 0;
//                }
//            }
//            parentcounts.add(new ParentCount(current.parentpath, insameparentcount, countmatches));
//        }
//        parentcounts.sort(Comparator.comparing(ParentCount::getMatched).thenComparing(ParentCount::getTotal));
//        return parentcounts;
//    }
//
//    private class ParentCount {
//
//        private final String parentpath;
//        private final int total;
//        private final int totalmatched;
//
//        public ParentCount(String parentpath, int total, int totalmatched) {
//            this.parentpath = parentpath;
//            this.total = total;
//            this.totalmatched = totalmatched;
//        }
//        
//        public Integer getMatched(){
//            return totalmatched;
//        }
//        
//        public Integer getTotal() {
//            return total;
//        }
//
//        @Override
//        public String toString() {
//            return parentpath + "§" + total + "§" + totalmatched;
//        }
//    }
}
