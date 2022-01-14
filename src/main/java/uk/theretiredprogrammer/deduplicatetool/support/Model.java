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
import java.util.List;
import java.util.Map;

public class Model extends FileRecordSet implements StorableSet {

    private final String modelname;
    private final Parameters parameters;
    private final List<FileRecordSet> matchrecords = new ArrayList<>();
    private final Map<String, FileRecordSet> sets = new HashMap<>();

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
    }

    public void load(String line) throws IOException {
        add(new FileRecord(line));
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

    public FileRecordSet getSet(String key) throws IOException {
        FileRecordSet frs = sets.get(key);
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
    
    public void putSet(String key, FileRecordSet value) {
        sets.put(key, value);
    }

    public List<FileRecordSet> getMatchRecords() {
        return matchrecords;
    }

    public void clearMatchRecords() {
        matchrecords.clear();
    }
    
    public void addToMatchRecords(FileRecordSet match) {
        matchrecords.add(match);
    }

    public String getModelName() {
        return modelname;
    }
}
