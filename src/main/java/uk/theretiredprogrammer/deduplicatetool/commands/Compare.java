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
import java.util.Iterator;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_DIGEST_FILESIZE;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILENAMEEXT;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecords;

public class Compare extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(10);
        String pp1 = checkSyntaxAndNAME("compare");
        String pp2 = checkSyntaxAndNAME();
        checkSyntax("using");
        String sortoption = checkOptionsSyntax("digest-filesize", "filenameext");
        String onlypp1 = checkSyntaxAndNAME("create");
        String bothpp1 = checkSyntaxAndNAME();
        String bothpp2 = checkSyntaxAndNAME();
        String onlypp2 = checkSyntaxAndNAME();
        Comparator comparatoroption = switch (sortoption) {
            case "digest-filesize" ->
                COMPARE_DIGEST_FILESIZE;
            case "filenameext" ->
                COMPARE_FILENAMEEXT;
            default ->
                throw new IOException("Compare: illegal sort option");
        };

        //
        model.putSet(onlypp1, findOnly(model.getSet(pp1), model.getSet(pp2), comparatoroption));
        model.putSet(bothpp1, findinBoth(model.getSet(pp1), model.getSet(pp2), comparatoroption));
        model.putSet(bothpp2, findinBoth(model.getSet(pp2), model.getSet(pp1), comparatoroption));
        model.putSet(onlypp2, findOnly(model.getSet(pp2), model.getSet(pp1), comparatoroption));
        //
        System.out.println("COMPARE: "
                + model.getSetSize(onlypp1) + " files only in " + pp1 + "; "
                + model.getSetSize(bothpp1) + " matching files in " + pp1 + "; "
                + model.getSetSize(bothpp2) + " matching files in " + pp2 + "; "
                + model.getSetSize(onlypp2) + " files only in " + pp2);
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    private FileRecords findOnly(FileRecords set, FileRecords compareset, Comparator<FileRecord> comparator) {
        FileRecords only = new FileRecords();
        Iterator<FileRecord> iterator = set.iterator();
        while (iterator.hasNext()) {
            FileRecord current = iterator.next();
            if (!existsIn(compareset, current, comparator)) {
                only.add(current);
            }
        }
        return only;
    }

    private boolean existsIn(FileRecords set, FileRecord tofind, Comparator<FileRecord> comparator) {
        Iterator<FileRecord> iterator = set.iterator();
        while (iterator.hasNext()) {
            FileRecord current = iterator.next();
            if (comparator.compare(current, tofind) == 0) {
                return true;
            }
        }
        return false;
    }

    private FileRecords findinBoth(FileRecords set1, FileRecords set2, Comparator<FileRecord> comparator) {
        FileRecords both = new FileRecords();
        Iterator<FileRecord> iterator = set1.iterator();
        while (iterator.hasNext()) {
            FileRecord current = iterator.next();
            if (existsIn(set2, current, comparator)) {
                both.add(current);
            }
        }
        return both;
    }
}
