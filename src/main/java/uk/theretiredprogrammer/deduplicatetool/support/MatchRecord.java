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

public class MatchRecord {
    
    public static enum MatchType {
        PATH
    }
    
    private static int nextid = 1;
    
    public final int id;
    public final MatchType matchtype;
    public final List<FileRecord> fileRecords = new ArrayList<>();
    
    public MatchRecord(MatchType matchtype, FileRecord fileRecord) {
        id = nextid++;
        this.matchtype = matchtype;
        fileRecords.add(fileRecord);
    }
    
    public void add(FileRecord fileRecord) {
        fileRecords.add(fileRecord);
    }
}
