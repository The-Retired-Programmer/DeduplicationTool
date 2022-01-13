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
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;
import uk.theretiredprogrammer.deduplicatetool.support.FolderModel;
import static uk.theretiredprogrammer.deduplicatetool.support.Model.ALLFILERECORDS;

public class Mark extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(2,3,5);
        checkSyntax("mark");
        String option = checkOptionsSyntax("set", "reset","reset-all");
        switch (option) {
            case "set" -> {
                checkTokenCount(5);
                String key = checkSyntaxAndLowercaseNAME();
                checkSyntax("to");
                String value = checkOptionsSyntax("none", "duplicate-ignore", "to-be-deleted", "file-deleted", "duplicate-candidate");
                value = value.replace("-", "_").toUpperCase();
                FolderModel fm = model.getFolderModel(key);
                for (FileRecord f : fm.getAllFileRecords()) {
                    FileStatus newStatus = FileStatus.valueOf(value);
                    if (f.filestatus.isProcessable()) {
                        f.filestatus = newStatus;
                    } else {
                        System.out.println("MARK NOT COMPLETED - trying to update a non processable record's status from " + f.filestatus + " to " + newStatus);
                        System.out.println(f.toString());
                    }
                }
            }
            case "reset" -> {
                checkTokenCount(3);
                String key = checkSyntaxAndLowercaseNAME();
                FolderModel fm = model.getFolderModel(key);
                for (FileRecord f : fm.getAllFileRecords()) {
                    f.filestatus = FileStatus.NONE;
                }
            }
            case "reset-all" -> {
                checkTokenCount(2);
                for (FileRecord f : model.getFileRecordSet(ALLFILERECORDS)) {
                    f.filestatus = FileStatus.NONE;
                }
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}
