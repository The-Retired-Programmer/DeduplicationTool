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
package uk.theretiredprogrammer.deduplicatetool.commands.filter;

import java.io.IOException;
import uk.theretiredprogrammer.deduplicatetool.commands.Command;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;

public class Filter extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(3, 4);
        FileRecordFilter filter = new FileRecordFilter();
        filter.parse(model, checkSyntaxAndNAME("filter"));
        String finalaction = checkOptionsSyntax("as", "set", "reset", "output", "report", "display", "display2", "list", "list2", "count", "hint","hintappend");
        switch (finalaction) {
            case "as" -> {
                checkTokenCount(4);
                filter.setAction("as", checkSyntaxAndLowercaseNAME());
            }
            case "set" -> {
                checkTokenCount(4);
                FileStatus[] allstatus = FileStatus.values();
                String options = "";
                for (FileStatus fs : allstatus) {
                    options += fs.toString() + ",";
                }
                options = options.substring(0, options.length() - 1).toLowerCase();
                filter.setAction("set", checkOptionsSyntax(options.split(",")).toUpperCase());
            }
            case "reset" -> {
                checkTokenCount(3);
                filter.setAction("reset");
            }
            case "output" -> {
                checkTokenCount(4);
                filter.setAction("output", checkSyntaxAndFILEPATH());
            }
            case "report" -> {
                checkTokenCount(4);
                filter.setAction("report", checkSyntaxAndFILEPATH());
            }
            case "display" -> {
                checkTokenCount(3);
                filter.setAction("display");
            }
            case "display2" -> {
                checkTokenCount(3);
                filter.setAction("display2");
            }
            case "list" -> {
                checkTokenCount(4);
                filter.setAction("list", checkSyntaxAndFILEPATH());
            }
            case "list2" -> {
                checkTokenCount(4);
                filter.setAction("list2", checkSyntaxAndFILEPATH());
            }
            case "count" -> {
                checkTokenCount(3);
                filter.setAction("count");
            }
            case "hint" -> {
                checkTokenCount(4);
                filter.setAction("hint", checkSyntaxAndNAME());
            }
            case "hintappend" -> {
                checkTokenCount(4);
                filter.setAction("hintappend", checkSyntaxAndNAME());
            }
        }
        filter.executeFilter(model);
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}
