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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class Model {

    private final String modelname;
    private final List<FileRecord> allrecords = new ArrayList<>();

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

    public void add(FileRecord fr) {
        allrecords.add(fr);
    }

    public void sortbydigest() {
        Collections.sort(allrecords, (fr1, fr2) -> fr1.digest.compareTo(fr2.digest));
    }

    public void sortbyfilename() {
        Collections.sort(allrecords, (fr1, fr2) -> fr1.filename.compareTo(fr2.filename));
    }

    @SuppressWarnings("null")
    public List<List<FileRecord>> extractallduplicatedigests() {
        List<List<FileRecord>> allduplicates = new ArrayList<>();
        if (allrecords.size() > 1) {
            ListIterator<FileRecord> iterator = allrecords.listIterator();
            FileRecord current = iterator.next();
            boolean induplicateset = false;
            List<FileRecord> duplicates = null;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                if (current.digest.equals(possibleduplicate.digest)) {
                    if (induplicateset) {
                        duplicates.add(possibleduplicate);
                        System.out.println(possibleduplicate.toString());
                    } else {
                        duplicates = new ArrayList<>();
                        duplicates.add(current);
                        duplicates.add(possibleduplicate);
                        System.out.println("DUPLICATE");
                        System.out.println(current.toString());
                        System.out.println(possibleduplicate.toString());
                        induplicateset = true;
                    }
                } else {
                    if (induplicateset) {
                        induplicateset = false;
                        allduplicates.add(duplicates);
                    }
                    current = possibleduplicate;
                }
            }
        }
        return allduplicates;
    }

    @SuppressWarnings("null")
    public List<List<FileRecord>> extractallduplicatefilenames() {
        List<List<FileRecord>> allduplicates = new ArrayList<>();
        if (allrecords.size() > 1) {
            ListIterator<FileRecord> iterator = allrecords.listIterator();
            FileRecord current = iterator.next();
            boolean induplicateset = false;
            List<FileRecord> duplicates = null;
            while (iterator.hasNext()) {
                FileRecord possibleduplicate = iterator.next();
                if (current.filename.equals(possibleduplicate.filename)) {
                    if (induplicateset) {
                        duplicates.add(possibleduplicate);
                        System.out.println(possibleduplicate.toPrintString());
                    } else {
                        duplicates = new ArrayList<>();
                        duplicates.add(current);
                        duplicates.add(possibleduplicate);
                        System.out.println("DUPLICATE");
                        System.out.println(current.toPrintString());
                        System.out.println(possibleduplicate.toPrintString());
                        induplicateset = true;
                    }
                } else {
                    if (induplicateset) {
                        induplicateset = false;
                        allduplicates.add(duplicates);
                    }
                    current = possibleduplicate;
                }
            }
        }
        return allduplicates;
    }
}
