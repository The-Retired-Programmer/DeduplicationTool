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

public class Mark extends Command {
 
    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(4);
        String key = checkSyntaxAndNAME("mark");
        checkSyntax("set");
        String value = checkOptionsSyntax("none", "duplicate-ignore", "to-be-deleted", "file-deleted", "duplicate-candidate");
        value = value.replace("-", "_").toUpperCase();
        FolderModel fm = model.getFolderModel(key);
        for (FileRecord f: fm.getAllFileRecords()){
            FileStatus newStatus = FileStatus.valueOf(value);
            if (f.filestatus.isProcessable()) {
                f.filestatus = newStatus;
                System.out.println(f.toString());
            } else {
                System.out.println("MARK NOT COMPLETED - trying to update a non processable record's status from "+f.filestatus+" to "+ newStatus);
            }
            System.out.println(f.toString());
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}
