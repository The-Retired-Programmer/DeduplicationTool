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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.MatchRecord;
import uk.theretiredprogrammer.deduplicatetool.support.MatchRecord.MatchType;

public class Matching extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(4);
        checkSyntax("match", "duplicates", "using");
        String option = checkOptionsSyntax("filepath");
        switch (option) {
            case "filepath" -> {
                filepath();
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    private void filepath() {
        List<List<FileRecord>> dups;
        model.sortbydigest();
        System.out.println("CHECKING FOR DUPLICATE FILEPATHS");
        dups = extractallduplicatepaths();
        System.out.println("NUMBER OF DUPLICATES: " + dups.size());
        System.out.println("NUMBER OF DUPLICATE FILES: " + getflatsize(dups));
        //bld.dump(dups, "duplicate-digests");
        System.out.println("NUMBER OF MATCHES: "+ model.getAllMatchRecords().size());
    }

    @SuppressWarnings("null")
    public List<List<FileRecord>> extractallduplicatepaths() {
        List<List<FileRecord>> allduplicates = new ArrayList<>();
        List<FileRecord> allrecords = model.getAllFileRecords();
        if (allrecords.size() > 1) {
            ListIterator<FileRecord> iterator = allrecords.listIterator();
            FileRecord current = iterator.next();
            boolean induplicateset = false;
            List<FileRecord> duplicates = null;
            MatchRecord rec = null;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                if (current.path.equals(possibleduplicate.path)) {
                    if (induplicateset) {
                        duplicates.add(possibleduplicate);
                        rec.add(possibleduplicate);
                        System.out.println(possibleduplicate.toString());
                    } else {
                        duplicates = new ArrayList<>();
                        duplicates.add(current);
                        duplicates.add(possibleduplicate);
                        //
                        rec = new MatchRecord(MatchType.PATH, current);
                        rec.add(possibleduplicate);
                        //
                        System.out.println("DUPLICATE");
                        System.out.println(current.toString());
                        System.out.println(possibleduplicate.toString());
                        induplicateset = true;
                    }
                } else {
                    if (induplicateset) {
                        induplicateset = false;
                        allduplicates.add(duplicates);
                        model.add(rec);
                    }
                    current = possibleduplicate;
                }
            }
        }
        return allduplicates;
    }

    private static int getflatsize(List<List<FileRecord>> dups) {
        int flatsize = 0;
        for (var list : dups) {
            flatsize += list.size();
        }
        return flatsize;
    }
}
