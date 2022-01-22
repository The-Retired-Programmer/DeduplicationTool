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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.Model;

public class FileRecordFilter {

    private FRFSource source;
    private final List<FilterItem> filters = new ArrayList<>();
    private final FRFConsumer consumer = new FRFConsumer();

    public FileRecordFilter() {
    }

    public void parse(Model model, String filterstring) throws IOException {
        String[] filterparts = filterstring.split(">>");
        source = FRFSource.parse(model, filterparts[0]);
        for (int i = 1; i < filterparts.length; i++) {
            String command = extractfiltercommand(filterparts[i]);
            FilterItem filteritem = getFilterItem(command);
            String parameter = extractfilterparameters(filterparts[i]);
            filteritem.parseParameters(command, parameter);
            filters.add(filteritem);
        }
    }

    private FilterItem getFilterItem(String command) throws IOException {
        return switch (command) {
            case "locked" ->
                new LockedFilter();
            case "unlocked" ->
                new UnLockedFilter();
            case "sort" ->
                new SortFunction();
            case "parentpath-is" ->
                new ParentpathIsFilter();
            case "tag-is" ->
                new TagIsFilter();
            case "filenameext-exists-in" ->
                new FilenameextExistsInSetFilter();
            case "filepath-is" ->
                new FilepathIsFilter();
            case "filepath-startswith" ->
                new FilepathStartswithFilter();
            case "filename-is" ->
                new FilenameIsFilter();
            case "fileext-is" -> 
                new FileExtIsFilter();
            case "filenameext-is" ->
                new FilenameextIsFilter();
            case "digest-is" ->
                new DigestIsFilter();
            case "filesize-is" ->
                new FilesizeIsFilter();
            case "filestatus-is" ->
                new FileStatusIsFilter();
            case "filestatus-isnot" ->
                new FileStatusIsnotFilter();
            case "matched" ->
                new HasMatchFilter();
            case "unmatched" ->
                new IsUnmatchedFilter();
            default ->
                throw new IOException("Filter chain item " + command + " does not exist");
        };
    }

    private String extractfiltercommand(String filterstring) {
        int p = filterstring.indexOf("(");
        return (p == -1 ? filterstring : filterstring.substring(0, p)).trim();
    }

    private String extractfilterparameters(String filterstring) throws IOException {
        int p = filterstring.indexOf("(");
        if (p == -1) {
            return null;
        }
        int q = filterstring.indexOf(")", p);
        if (q == -1) {
            throw new IOException("Missing closing bracket on filter " + filterstring.substring(0, p));
        }
        return filterstring.substring(p + 1, q).trim();
    }

    public void setAction(String action, String name) {
        consumer.setAction(action, name);
    }

    void setAction(String action) {
        consumer.setAction(action);
    }

    void setAction(String action, File path) {
        consumer.setAction(action, path);
    }

    void checkCorrect() throws IOException {
        source.checkCorrect();
        consumer.checkCorrect();
    }

    public void executeFilter(Model model) throws IOException {
        checkCorrect();
        Stream<FileRecord> stream = source.getSource(model);
        for (FilterItem fi : filters) {
            stream = fi.streamProcess(stream, model);
        }
        consumer.streamProcess(model, stream);
    }
}
