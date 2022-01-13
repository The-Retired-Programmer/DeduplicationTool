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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import static uk.theretiredprogrammer.deduplicatetool.support.Model.ALLFILERECORDS;

public class FolderModel {
    
    private final List<FileRecord> allrecords = new ArrayList<>();
    
    public void setTagSource(Model model, String tag) throws IOException{
        for (FileRecord filerecord: model.getFileRecordSet(ALLFILERECORDS)) {
            if (filerecord.tag.equals(tag)){
                allrecords.add(filerecord);
            }
        }
    }
    
    public void setFolderpathSource(Model model, String folderpath) throws IOException {
        for (FileRecord filerecord: model.getFileRecordSet(ALLFILERECORDS)) {
            if (filerecord.parentpath.equals(folderpath)){
                allrecords.add(filerecord);
            }
        }
    }
    
    public void setTagFolderpathSource(Model model, String tag, String folderpath) throws IOException {
        for (FileRecord filerecord: model.getFileRecordSet(ALLFILERECORDS)) {
            if (filerecord.parentpath.equals(folderpath) && filerecord.tag.equals(tag)){
                allrecords.add(filerecord);
            }
        }
    }
    
    public void setFileModelMatchSource(Model model, String extractkey, String matchkey) throws IOException {
        Comparator cmp = Comparator.comparing(FileRecord::getFilename).thenComparing(FileRecord::getDigest).thenComparing(FileRecord::getFilesize);
        List<FileRecord> extractlist = model.getFolderModel(extractkey).getAllProcessableFileRecords();
        extractlist.sort(cmp);
        List<FileRecord> matchlist = model.getFolderModel(matchkey).getAllFileRecords();
        matchlist.sort(cmp);
        ListIterator<FileRecord> extractiterator = extractlist.listIterator();
        while (extractiterator.hasNext()) {
            FileRecord extract = extractiterator.next();
            ListIterator<FileRecord> matchiterator = matchlist.listIterator();
            while (matchiterator.hasNext()) {
                FileRecord match = matchiterator.next();
                if(cmp.compare(extract,match)== 0) {
                    allrecords.add(extract);
                }
            }
        }
    }
    
    public List<FileRecord> getAllFileRecords() {
        return allrecords;
    }
    
    public List<FileRecord> getAllProcessableFileRecords() {
        return allrecords.stream().filter(fr -> fr.getFileStatus().isProcessable()).collect(Collectors.toList());
    }
}
