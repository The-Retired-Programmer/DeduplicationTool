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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.theretiredprogrammer.deduplicatetool.support.FileManager;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecordSet;
import uk.theretiredprogrammer.deduplicatetool.support.Model;

public class FRFConsumer {

    private String action ="";
    private String name;
    private File path;

    public FRFConsumer() {
    }

    void setAction(String action, String name) {
        this.action = action;
        this.name = name;
    }

    void setAction(String action) {
        this.action = action;
    }

    void setAction(String action, File path) {
        this.action = action;
        this.path = path;
    }
    
    void checkCorrect() throws IOException {
        if (action.isBlank()) {
            throw new IOException("Bad Filter chain definition - no final action defined");
        }
    }

    public void streamProcess(Model model, Stream<FileRecord> stream) throws IOException {
        checkCorrect();
        switch (action) {
            case "set" -> {
                long size = stream.filter((fr) -> {
                    if (fr.filestatus == FileStatus.NONE) {
                        fr.filestatus = FileStatus.valueOf(name);
                        return true;
                    }
                    return false;
                }).count();
                System.out.println("Successfully Updated " + size + " records");
            }
            case "reset" -> {
                Function<FileRecord, FileRecord> resetStatus = (fr) -> {
                    fr.filestatus = FileStatus.NONE;
                    return fr;
                };
                long size = stream.map(resetStatus).count();
                System.out.println("Reset " + size + " records");
            }
            case "as" -> {
                model.putSet(name, new FileRecordSet(stream.collect(Collectors.toSet())));
                System.out.println("Collected " + model.getSetSize(name) + " records");
            }
            case "output" -> {
                long recordcounter;
                try ( PrintWriter pwtr = FileManager.openWriter(path)) {
                    recordcounter = stream.map((fr) -> {
                        pwtr.println(fr.toString());
                        return fr;
                    }).count();
                }
                System.out.println("Exported " + recordcounter + " records");
            }
            case "display" -> {
                long recordcounter = stream.map((fr) -> {
                    System.out.println(fr.toReportString());
                    return fr;
                }).count();
                System.out.println("Displayed " + recordcounter + " records");
            }
            case "report" -> {
                long recordcounter;
                try ( PrintWriter pwtr = FileManager.openWriter(path)) {
                    recordcounter = stream.map((fr) -> {
                        pwtr.println(fr.toReportString());
                        return fr;
                    }).count();
                }
                System.out.println("Reported " + recordcounter + " records");
            }
            case "list" -> {
                long recordcounter;
                try ( PrintWriter pwtr = FileManager.openWriter(path)) {
                    recordcounter = stream.map((fr) -> {
                        pwtr.println(fr.toListString());
                        return fr;
                    }).count();
                }
                System.out.println("Listed " + recordcounter + " records");
            }
            case "list2" -> {
                long recordcounter;
                try ( PrintWriter pwtr = FileManager.openWriter(path)) {
                    recordcounter = stream.map((fr) -> {
                        pwtr.println(fr.toList2String());
                        return fr;
                    }).count();
                }
                System.out.println("Listed " + recordcounter + " records");
            }

        }
    }
}
