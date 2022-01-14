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
import java.util.Arrays;
import java.util.stream.Stream;

public class FileRecordFilter {

    public static FileRecordFilter parse(Model model, String filterstring) throws IOException {
        String[] filterparts = filterstring.split(">>");
        FileRecordFilter filter = new FileRecordFilter();
        filter.source = FRFSource.parse(model, filterparts[0]);
        filter.filter = FRFFilter.parse(Arrays.copyOfRange(filterparts,1,filterparts.length));
        filter.consumer = FRFConsumer.parse(filterstring);
        return filter;
    }

    private FRFSource source;
    private FRFFilter filter;
    private FRFConsumer consumer;

    private FileRecordFilter() {
    }
    
    public void setFinalActionIsFilter(String name) {
        consumer.setFinalActionIsFilter(name);
    }

    public void setFinalActionIsSet(String name) {
        consumer.setFinalActionIsSet(name);
    }
    
    public void setFinalActionIsReset() {
        consumer.setFinalActionIsReset();
    }

    public void setFinalActionIsExport(File path) {
        consumer.setFinalActionIsExport(path);
    }

    public void setFinalActionIsReport(File path) {
        consumer.setFinalActionIsReport(path);
    }
    
    public void setFinalActionIsDisplay() {
        consumer.setFinalActionIsDisplay();
    }

    void checkCorrect() throws IOException {
        source.checkCorrect();
        filter.checkCorrect();
        consumer.checkCorrect();
    }

    public void executeFilter(Model model) throws IOException {
        checkCorrect();
        Stream<FileRecord> stream = source.getSource(model);
        stream = filter.executeFilter(model,stream);
        consumer.ConsumerAction(model, stream);
    }
}
