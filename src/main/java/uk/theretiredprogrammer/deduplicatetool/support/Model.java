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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Model extends HashMap<String,FileRecordSet> {

    private final String modelname;
    private final MatchRecord matchrecord = new MatchRecord();
    private final Map<String,FolderModel> foldermodels = new HashMap<>();
    
    public static final String ALLFILERECORDS = "*";

    public Model(String modelname) {
        this.modelname = modelname;
    }
    
    public void load(Parameters parameters) throws IOException {
        AllFileRecordSet allfilerecords = new AllFileRecordSet();
        allfilerecords.load(modelname, parameters);
        put(ALLFILERECORDS, allfilerecords);
    }
    
    public void save(Parameters parameters) throws IOException {
       ((AllFileRecordSet)getFileRecordSet(ALLFILERECORDS)).save(modelname, parameters);
    }
    
    public int getFileRecordSetSize(String key) throws IOException{
        return getFileRecordSet(key).size();
    }
    
    public FileRecordSet getFileRecordSet(String key) throws IOException {
        FileRecordSet lfr = get(key);
        if ( lfr != null) {
            return lfr;
        }
        throw new IOException("unknown FileRecordSet: "+key);
    }
    
    public void checkValidFileRecordSet(String key) throws IOException {
        if (!containsKey(key)) {
            throw new IOException("FileRecordSet "+key+ " does not exist");
        }
    }
    
    public MatchRecord getMatchRecord() {
        return matchrecord;
    }
    
    public void clearMatchRecord() {
        matchrecord.clear();
    }
    
    public void addToMatchRecord(FileRecordSet match) {
        matchrecord.add(match);
    }
    
    public FolderModel getFolderModel(String key) throws IOException{
        FolderModel fm = foldermodels.get(key);
        if (fm == null) {
            throw new IOException("unknow FolderModel: "+key);
        }
        return fm;
    }

    public void add(String key, FolderModel fmodel){
        foldermodels.put(key, fmodel);
    }
    
    public String getModelName() {
        return modelname;
    }
}
