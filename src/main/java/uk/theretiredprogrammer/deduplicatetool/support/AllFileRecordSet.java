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
import java.util.Collection;

public class AllFileRecordSet extends FileRecordSet {
    
    public AllFileRecordSet() {
        super();
    } 
    
    public AllFileRecordSet(Collection<FileRecord> a){
        super(a);
    }
    
    public void load(String modelname, Parameters parameters) throws IOException {
        try ( BufferedReader rdr = FileManager.openModelReader(modelname, parameters)) {
            String line = rdr.readLine();
            while (line != null) {
                add(new FileRecord(line));
                line = rdr.readLine();
            }
        }
    }
    
    public void load(String line) throws IOException{
        add(new FileRecord(line));
    }
    
    public void save(String modelname, Parameters parameters) throws IOException {
        try (PrintWriter wtr = FileManager.openModelWriter(modelname, parameters)){
            for (FileRecord fr: this){
                wtr.println(fr.toString());
            }
        }
    }
    
}
