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
import java.util.stream.Stream;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILEPATH;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILESTATUS_FILEPATH;
import uk.theretiredprogrammer.deduplicatetool.support.Model;

public class SortFunction implements FilterItem {

    private String parameter;

    public SortFunction() {
    }

    @Override
    public void parseParameters(String command, String parameter) throws IOException {
       if (parameter == null || parameter.isBlank()) {
            throw new IOException("no parameter defined for " + command);
        }
        this.parameter = parameter;
    }

    @Override
    public Stream<FileRecord> streamProcess(Stream<FileRecord> fromStream, Model model) throws IOException {
        return switch (parameter) {
            case "filepath" -> fromStream.sorted(COMPARE_FILEPATH);
            case "filestatus-filepath" -> fromStream.sorted(COMPARE_FILESTATUS_FILEPATH);
            default -> throw new IOException("Unknown sort parameter");
        };
    }
}
