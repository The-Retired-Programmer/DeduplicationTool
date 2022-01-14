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
package uk.theretiredprogrammer.deduplicatetool.support;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;

public class FRFConsumer {

    private static enum FILTERCONSUMER {
        UNDEFINED, SET, RESET, NAMEDFILTER, EXPORT, REPORT, DISPLAY
    }

    public static FRFConsumer parse(String filterstring) throws IOException {
        FRFConsumer filter = new FRFConsumer();
        return filter;
    }

    private FILTERCONSUMER consumer = FILTERCONSUMER.UNDEFINED;
    private String consumername;
    private File consumerpath;

    private FRFConsumer() {
    }

    void setFinalActionIsFilter(String name) {
        consumer = FILTERCONSUMER.NAMEDFILTER;
        consumername = name;
    }

    void setFinalActionIsSet(String name) {
        consumer = FILTERCONSUMER.SET;
        consumername = name;
    }
    
    void setFinalActionIsReset() {
        consumer = FILTERCONSUMER.RESET;
    }

    void setFinalActionIsExport(File path) {
        consumer = FILTERCONSUMER.EXPORT;
        consumerpath = path;
    }

    void setFinalActionIsReport(File path) {
        consumer = FILTERCONSUMER.REPORT;
        consumerpath = path;
    }

    void setFinalActionIsDisplay() {
        consumer = FILTERCONSUMER.DISPLAY;
    }

    void checkCorrect() throws IOException {
        if (consumer == FILTERCONSUMER.UNDEFINED) {
            throw new IOException("Bad Filter chain definition");
        }
    }

    public void ConsumerAction(Model model, Stream<FileRecord> stream) throws IOException {
        checkCorrect();
        switch (consumer) {
            case SET -> {
                long size = stream.filter((fr) -> {
                    if (fr.filestatus == FileStatus.NONE) {
                        fr.filestatus = FileStatus.valueOf(consumername);
                        return true;
                    }
                    return false;
                }).count();
                System.out.println("Successfully Updated " + size + " records");
            }
            case RESET -> {
                Function<FileRecord, FileRecord> resetStatus = (fr) -> {
                    fr.filestatus = FileStatus.NONE;
                    return fr;
                };
                long size = stream.map(resetStatus).count();
                System.out.println("Reset " + size + " records");
            }
            case NAMEDFILTER -> {
                model.put(consumername, new FileRecordSet(stream.collect(Collectors.toSet())));
                System.out.println("Collected " + model.getFileRecordSetSize(consumername) + " records");
            }
            case EXPORT -> {
                long recordcounter;
                try ( PrintWriter pwtr = FileManager.openWriter(consumerpath)) {
                    recordcounter = stream.map((fr) -> {
                        pwtr.println(fr.toString());
                        return fr;
                    }).count();
                }
                System.out.println("Exported " + recordcounter + " records");
            }
            case DISPLAY -> {
                long recordcounter = stream.map((fr) -> {
                    System.out.println(fr.toReportString());
                    return fr;
                }).count();
                System.out.println("Displayed " + recordcounter + " records");
            }
            case REPORT -> {
                long recordcounter;
                try ( PrintWriter pwtr = FileManager.openWriter(consumerpath)) {
                    recordcounter = stream.map((fr) -> {
                        pwtr.println(fr.toReportString());
                        return fr;
                    }).count();
                }
                System.out.println("Reported " + recordcounter + " records");
            }
        }
    }
}
