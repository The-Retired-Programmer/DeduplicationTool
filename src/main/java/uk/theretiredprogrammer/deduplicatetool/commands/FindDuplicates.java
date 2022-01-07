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
import java.util.List;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;

public class FindDuplicates extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(4);
        checkSyntax("find", "duplicates", "using");
        String option = checkOptionsSyntax("digests", "filenames");
        switch (option) {
            case "digests" -> {
                digest();
            }
            case "filenames" -> {
                filename();
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    private void digest() {
        List<List<FileRecord>> dups;
        model.sortbydigest();
        System.out.println("CHECKING FOR DUPLICATE DIGESTS");
        dups = model.extractallduplicatedigests();
        System.out.println("NUMBER OF DUPLICATES: " + dups.size());
        System.out.println("NUMBER 1OF DUPLICATE FILES: " + getflatsize(dups));
        //bld.dump(dups, "duplicate-digests");
    }

    private void filename() {
        List<List<FileRecord>> dups;
        model.sortbyfilename();
        System.out.println("CHECKING FOR DUPLICATE FILENAMES");
        dups = model.extractallduplicatefilenames();
        System.out.println("NUMBER OF DUPLICATES: " + dups.size());
        System.out.println("NUMBER OF DUPLICATE FILES: " + getflatsize(dups));
        //bld.dump(dups, "duplicate-filenames");
    }

    private static int getflatsize(List<List<FileRecord>> dups) {
        int flatsize = 0;
        for (var list : dups) {
            flatsize += list.size();
        }
        return flatsize;
    }
}
