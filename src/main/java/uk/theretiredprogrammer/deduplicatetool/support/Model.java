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
import java.util.stream.Collectors;

public class Model {

    private final String modelname;
    private final List<FileRecord> allrecords = new ArrayList<>();
    private final List<MatchRecord> allmatches = new ArrayList<>();
    private final Map<String,FolderModel> foldermodels = new HashMap<>();
    private final Map<String,List<FileRecord>> filteredmodels = new HashMap<>();

    public Model(String modelname, Parameters parameters) throws IOException {
        this.modelname = modelname;
        try ( BufferedReader rdr = FileManager.openModelReader(modelname, parameters)) {
            String line = rdr.readLine();
            while (line != null) {
                allrecords.add(new FileRecord(line));
                line = rdr.readLine();
            }
        }
    }
    
    public void save(Parameters parameters) throws IOException {
        try (PrintWriter wtr = FileManager.openModelWriter(modelname, parameters)){
            for (FileRecord fr: allrecords){
                wtr.println(fr.toString());
            }
        }
    }
    
    public List<FileRecord> getAllFileRecords() {
        return allrecords;
    }
    
    public int add(String key, List<FileRecord> filteredmodel){
        filteredmodels.put(key, filteredmodel);
        return filteredmodel.size();
    }
    
    public List<FileRecord> getFilteredModel(String key) throws IOException {
        List<FileRecord> lfr = filteredmodels.get(key);
        if ( lfr != null) {
            return lfr;
        }
        throw new IOException("unknown FilteredModel: "+key);
    }
    
    public void checkValidFilteredModel(String key) throws IOException {
        if (!filteredmodels.containsKey(key)) {
            throw new IOException("Filtered Model "+key+ " does not exist");
        }
    }
    
    public List<FileRecord> getAllProcessableFileRecords() {
        return allrecords.stream().filter(fr -> fr.getFileStatus().isProcessable()).collect(Collectors.toList());
    }
    
    public List<MatchRecord> getAllMatchRecords() {
        return allmatches;
    }
    
    public FolderModel getFolderModel(String key) throws IOException{
        FolderModel fm = foldermodels.get(key);
        if (fm == null) {
            throw new IOException("unknow FolderModel: "+key);
        }
        return fm;
    }

    public void add(FileRecord fr) {
        allrecords.add(fr);
    }
    
    public void add(MatchRecord mr) {
        allmatches.add(mr);
    }
    
    public void add(String key, FolderModel fmodel){
        foldermodels.put(key, fmodel);
    }
    
    public String getModelName() {
        return modelname;
    }
}
