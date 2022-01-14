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
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecordFilter;

public class Filter extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(3,4);
        FileRecordFilter filter = FileRecordFilter.parse(model, checkSyntaxAndNAME("filter"));
        String finalaction = checkOptionsSyntax("as", "set", "reset", "output", "report", "display");
        switch (finalaction) {
            case "as" -> {
                checkTokenCount(4);
                filter.setFinalActionIsFilter(checkSyntaxAndLowercaseNAME());
            }
            case "set" -> {
                checkTokenCount(4);
                FileStatus[] allstatus = FileStatus.values();
                String options = "";
                for (FileStatus fs : allstatus ) {
                    options += fs.toString()+",";
                }
                options = options.substring(0,options.length()-1).toLowerCase();
                filter.setFinalActionIsSet(checkOptionsSyntax(options.split(",")).toUpperCase());
            }
            case "reset" -> {
                checkTokenCount(3);
                filter.setFinalActionIsReset();
            }
            case "output" -> {
                checkTokenCount(4);
                filter.setFinalActionIsExport(checkSyntaxAndFILEPATH());
            }
            case "report" -> {
                checkTokenCount(4);
                filter.setFinalActionIsReport(checkSyntaxAndFILEPATH());
            }
            case "display" -> {
                checkTokenCount(3);
                filter.setFinalActionIsDisplay();
            }
        }
        filter.executeFilter(model);
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}
