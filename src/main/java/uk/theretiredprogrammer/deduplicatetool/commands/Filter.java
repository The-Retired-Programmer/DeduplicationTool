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
import uk.theretiredprogrammer.deduplicatetool.support.FileRecordFilter;

public class Filter extends Command {

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(4);
        FileRecordFilter filter = FileRecordFilter.parse(checkSyntaxAndNAME("filter"));
        String finalaction = checkOptionsSyntax("as", "set", "output", "report");
        switch (finalaction) {
            case "as" -> {
                filter.setFinalActionIsFilter(checkSyntaxAndNAME());
            }
            case "set" -> {
                filter.setFinalActionIsSet(checkSyntaxAndNAME());
            }
            case "output" -> {
                filter.setFinalActionIsExport(checkSyntaxAndFILEPATH());
            }
            case "report" -> {
                filter.setFinalActionIsReport(checkSyntaxAndFILEPATH());
            }
        }
        filter.executeFilter(model);
        return Command.ActionResult.COMPLETEDCONTINUE;
    }
}
