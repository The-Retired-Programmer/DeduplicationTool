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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_DIGEST_FILESIZE;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_FILEPATH;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_PARENTPATH;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_PARENTPATH_FILENAMEEXT;

public class Model extends FileRecords implements StorableSet {

    private final String modelname;
    public final Parameters parameters;
    private final List<FileRecords> matchrecords = new ArrayList<>();
    private final Map<String, FileRecords> sets = new HashMap<>();
    private final LinkedHashMap<String, FileRecords> parentpathfilesets = new LinkedHashMap<>();

    public Model(String modelname, Parameters parameters) {
        this.modelname = modelname;
        this.parameters = parameters;
    }

    @Override
    public void load() throws IOException {
        try ( BufferedReader rdr = FileManager.openModelReader(modelname, parameters)) {
            String line = rdr.readLine();
            while (line != null) {
                add(new FileRecord(line));
                line = rdr.readLine();
            }
        }
        initExtended();
    }

    public void load(String line) throws IOException {
        add(new FileRecord(line));
    }

    public void initExtended() {
        this.sort(COMPARE_FILEPATH);
        updateHasMatch();
        extractFilerecordsByParentpath();
    }

    private void updateHasMatch() {
        FileRecords orderedset = new FileRecords(this);
        orderedset.sort(COMPARE_DIGEST_FILESIZE);
        if (!orderedset.isEmpty()) {
            Iterator<FileRecord> iterator = orderedset.iterator();
            FileRecord current = iterator.next();
            current.hasMatch = false;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                possibleduplicate.hasMatch = false;
                if (COMPARE_DIGEST_FILESIZE.compare(current, possibleduplicate) == 0) {
                    if (current.getFileStatus().isUseInMatching()) {
                        if (possibleduplicate.getFileStatus().isUseInMatching()) {
                            current.hasMatch = true;
                            possibleduplicate.hasMatch = true;
                        }
                    } else {
                        current = possibleduplicate;
                        current.hasMatch = false;
                    }
                } else {
                    current = possibleduplicate;
                    current.hasMatch = false;
                }
            }
        }
    }

    private void extractFilerecordsByParentpath() {
        parentpathfilesets.clear();
        FileRecords orderedset = new FileRecords(this);
        orderedset.sort(COMPARE_PARENTPATH_FILENAMEEXT);
        if (!orderedset.isEmpty()) {
            Iterator<FileRecord> iterator = orderedset.iterator();
            FileRecord firstfr = iterator.next();
            FileRecords sameparentset = new FileRecords();
            sameparentset.add(firstfr);
            while (iterator.hasNext()) {
                FileRecord followingfr = iterator.next();
                if (COMPARE_PARENTPATH.compare(firstfr, followingfr) == 0) {
                    sameparentset.add(followingfr);
                } else {
                    parentpathfilesets.put(firstfr.parentpath, sameparentset);
                    firstfr = followingfr;
                    sameparentset = new FileRecords();
                    sameparentset.add(firstfr);
                }
            }
            if (!sameparentset.isEmpty()) {
                parentpathfilesets.put(firstfr.parentpath, sameparentset);
            }
        }

    }

    @Override
    public void save() throws IOException {
        try ( PrintWriter wtr = FileManager.openModelWriter(modelname, parameters)) {
            for (FileRecord fr : this) {
                wtr.println(fr.toString());
            }
        }
    }

    public int getSetSize(String key) throws IOException {
        return getSet(key).size();
    }

    public FileRecords getSet(String key) throws IOException {
        FileRecords frs = sets.get(key);
        if (frs != null) {
            return frs;
        }
        throw new IOException("unknown FileRecordSet: " + key);
    }

    public void checkValidSet(String key) throws IOException {
        if (!sets.containsKey(key)) {
            throw new IOException("FileRecordSet " + key + " does not exist");
        }
    }

    public void putSet(String key, FileRecords value) {
        sets.put(key, value);
    }

    public List<FileRecords> getMatchRecords() {
        return matchrecords;
    }

    public void clearMatchRecords() {
        matchrecords.clear();
    }

    public void addToMatchRecords(FileRecords match) {
        matchrecords.add(match);
    }

    public LinkedHashMap<String, FileRecords> getParentsFileRecords() {
        return parentpathfilesets;
    }

    public String getModelName() {
        return modelname;
    }
}
