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
import java.util.stream.Stream;

public class FRFSource {

    private static enum FILTERSOURCE {
        UNDEFINED, ALLFILES, NAMEDFILTER
    }

    public static FRFSource parse(String filterstring) throws IOException {
        FRFSource filter = new FRFSource();
        filter.source = FILTERSOURCE.ALLFILES;
        filter.checkCorrect();
        return filter;
    }

    private FILTERSOURCE source = FILTERSOURCE.UNDEFINED;
    private String sourcename;

    private FRFSource() {
    }

    void checkCorrect() throws IOException {
        if (source == FILTERSOURCE.UNDEFINED) {
            throw new IOException("Bad Filter source definition");
        }
    }

    public Stream<FileRecord> getSource(Model model) throws IOException {
        checkCorrect();
        return (source == FILTERSOURCE.ALLFILES ? model.getAllFileRecords() : model.getFilteredModel(sourcename)).stream();
    }
}
