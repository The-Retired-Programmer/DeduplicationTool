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
import uk.theretiredprogrammer.deduplicatetool.support.FileManager;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecordSet;
import uk.theretiredprogrammer.deduplicatetool.support.MatchRecord;

public class Export extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(4, 5);
        checkSyntax("export");
        String option = checkOptionsSyntax("match", "selection");
        switch (option) {
            case "match" -> {
                checkTokenCount(4);
                File path = checkSyntaxAndFILEPATH("as");
                try ( PrintWriter wtr = FileManager.openWriter(path)) {
                    for (FileRecordSet frs : model.getMatchRecord()) {
                        for (FileRecord fr : frs) {
                            wtr.println(fr.toString());
                        }
                        wtr.println();
                    }
                }
            }
            case "selection" -> {
                checkTokenCount(5);
                String name = checkSyntaxAndLowercaseNAME();
                File path = checkSyntaxAndFILEPATH("as");
                try ( PrintWriter wtr = FileManager.openWriter(path)) {
                    for (FileRecord fr : model.getFolderModel(name).getAllFileRecords()) {
                        wtr.println(fr.toString());
                    }
                }
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}
