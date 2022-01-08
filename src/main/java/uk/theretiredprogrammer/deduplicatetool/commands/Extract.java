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
import uk.theretiredprogrammer.deduplicatetool.support.FolderModel;

public class Extract extends Command {
 
    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(6,7);
        checkSyntax("extract", "by");
        String option = checkOptionsSyntax("tag","parentpath", "tag-parentpath");
        switch (option){
            case "tag" -> {
                checkTokenCount(6);
                String tag = checkSyntaxAndNAME();
                String key = checkSyntaxAndNAME("as");
                FolderModel fm = new FolderModel(key, model, tag);
                System.out.println("Extracted "+fm.getAllFileRecords().size()+" FileRecords");
                model.add(key, fm);
            }
            case "parentpath" -> {
                checkTokenCount(6);
                File f = checkSyntaxAndFILEPATH();
                String key = checkSyntaxAndNAME("as");
                FolderModel fm = new FolderModel(key, f.getCanonicalPath(), model);
                System.out.println("Extracted "+fm.getAllFileRecords().size()+" FileRecords");
                model.add(key, fm);
            }
            case "tag-parentpath" -> {
                checkTokenCount(7);
                String tag = checkSyntaxAndNAME();
                File f = checkSyntaxAndFILEPATH();
                String key = checkSyntaxAndNAME("as");
                FolderModel fm = new FolderModel(key, f.getCanonicalPath(), model, tag);
                System.out.println("Extracted "+fm.getAllFileRecords().size()+" FileRecords");
                model.add(key, fm);
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}