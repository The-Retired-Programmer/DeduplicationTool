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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FolderModel {
    
    private final String folderkey;
    private final String folderpath;
    private final String tag;
    private final List<FileRecord> allrecords = new ArrayList<>();
    
    public FolderModel(String folderkey, String folderpath, Model model)  {
        this.folderkey = folderkey;
        this.folderpath = folderpath;
        this.tag = null;
        for (FileRecord filerecord: model.getAllFileRecords()) {
            if (filerecord.parentpath.equals(folderpath)){
                allrecords.add(filerecord);
            }
        }
    }
    
    public FolderModel(String folderkey, Model model, String tag)  {
        this.folderkey = folderkey;
        this.folderpath = null;
        this.tag = tag;
        for (FileRecord filerecord: model.getAllFileRecords()) {
            if (filerecord.tag.equals(tag)){
                allrecords.add(filerecord);
            }
        }
    }
    
    public FolderModel(String folderkey, String folderpath, Model model, String tag)  {
        this.folderkey = folderkey;
        this.folderpath = folderpath;
        this.tag = tag;
        for (FileRecord filerecord: model.getAllFileRecords()) {
            if (filerecord.parentpath.equals(folderpath) && filerecord.tag.equals(tag)){
                allrecords.add(filerecord);
            }
        }
    }
    
    public List<FileRecord> getAllFileRecords() {
        return allrecords;
    }
    
    public List<FileRecord> getAllProcessableFileRecords() {
        return allrecords.stream().filter(fr -> fr.getFileStatus().isProcessable()).collect(Collectors.toList());
    }
    
    public String getFolderkey(){
        return folderkey;
    }
    
    public String getFolderpath() {
        return folderpath;
    }
    
    public String getTag() {
        return tag;
    }
    
}
