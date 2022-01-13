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
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class FRFFilter {
    
    private final static BiPredicate<FileRecord,String> predicate_null = (FileRecord fr, String p) -> true;
    private final static BiPredicate<FileRecord,String> predicate_only_unset = (FileRecord fr, String p) -> fr.filestatus.isProcessable();
    private final static BiPredicate<FileRecord,String> predicate_equal_parentpath = (FileRecord fr, String p) -> fr.parentpath.equals(p);
    private final static BiPredicate<FileRecord,String> predicate_equal_tag = (FileRecord fr, String p) -> fr.tag.equals(p);
    private final static BiPredicate<FileRecord, String> predicate_equal_tag_and_parentpath = (FileRecord fr, String p) -> {
        String[] split = p.split(",");
        return split[0].trim().equals(fr.tag) && split[1].trim().equals(fr.parentpath);
    };
    private final static BiPredicate<FileRecord, FileRecordSet> predicate_filenameext_exists_in = (FileRecord fr, FileRecordSet comparelist)
        -> !comparelist.stream().noneMatch((cf) -> fr.filenameext.equals(cf.filenameext));       

    private static final FilterDefinition[] filterDefinitions = new FilterDefinition[]{
        new FilterDefinition("null", 0, false, predicate_null),
        new FilterDefinition("only-unset", 0, false, predicate_only_unset),
        new FilterDefinition("equal-parentpath", 1, false, predicate_equal_parentpath),
        new FilterDefinition("equal-tag", 1, false, predicate_equal_tag),
        new FilterDefinition("equal-tag-and-parentpath", 2, false, predicate_equal_tag_and_parentpath),
        new FilterDefinition("filenameext-exists-in", 1, true, predicate_filenameext_exists_in)
    };

    public static FRFFilter parse(String[] filters) throws IOException {
        FRFFilter filter = new FRFFilter();
        for (var filterstring : filters) {
            filter.parsefilter(filter.extractfiltercommand(filterstring), filter.extractfilterparameters(filterstring));
        }
        filter.checkCorrect();
        return filter;
    }

    private final List<PredicateAndParameter> pANDp = new ArrayList<>();

    private FRFFilter() {
    }

    private String extractfiltercommand(String filterstring) {
        int p = filterstring.indexOf("(");
        return (p == -1 ? filterstring : filterstring.substring(0, p)).trim();
    }

    private String extractfilterparameters(String filterstring) throws IOException {
        int p = filterstring.indexOf("(");
        if (p == -1) {
            return null;
        }
        int q = filterstring.indexOf(")", p);
        if (q == -1) {
            throw new IOException("Missing closing bracket on filter " + filterstring.substring(0, p));
        }
        return filterstring.substring(p + 1, q).trim();
    }

    private void parsefilter(String filtercommand, String filterparameters) throws IOException {
        for (FilterDefinition fd : filterDefinitions) {
            if (fd.syntaxname.equals(filtercommand)) {
                if (filterparameters == null) {
                    if (fd.numberofparameters == 0) {
                        pANDp.add(new PredicateAndParameter(fd.predicate, null));
                        return;
                    } else {
                        throw new IOException("Filter " + filtercommand + " expects " + fd.numberofparameters + " parameters");
                    }
                } else {
                    if (filterparameters.split(",").length != fd.numberofparameters) {
                        throw new IOException("Filter " + filtercommand + " expects " + fd.numberofparameters + " parameters");
                    }
                    pANDp.add(new PredicateAndParameter(fd.predicate, fd.isModelSelector ? new ModelSelector(filterparameters) : filterparameters));
                    return;
                }
            }
        }
        throw new IOException("unknown filter: " + filtercommand);
    }

    void checkCorrect() throws IOException {
    }

    public Stream<FileRecord> executeFilter(Model model, Stream<FileRecord> stream) throws IOException {
        for (PredicateAndParameter p : pANDp) {
            if (p.parameter instanceof ModelSelector k) {
                var set = k.get(model);
                stream = stream.filter((fr) -> p.predicate.test(fr, set));
            } else {
                stream = stream.filter((fr) -> p.predicate.test(fr, p.parameter));
            }
        }
        return stream;
    }

    private class PredicateAndParameter {

        public final BiPredicate predicate;
        public final Object parameter;

        public PredicateAndParameter(BiPredicate predicate, Object parameter) {
            this.predicate = predicate;
            this.parameter = parameter;
        }
    }

    private class ModelSelector {

        private final String key;

        public ModelSelector(String key) {
            this.key = key;
        }

        public FileRecordSet get(Model model) throws IOException {
            return model.getFileRecordSet(key);
        }
    }

    private static class FilterDefinition {

        public final String syntaxname;
        public final int numberofparameters;
        public final BiPredicate predicate;
        public final boolean isModelSelector;

        public FilterDefinition(String syntaxname, int numberofparameters, boolean isModelSelector, BiPredicate predicate) {
            this.syntaxname = syntaxname;
            this.numberofparameters = numberofparameters;
            this.predicate = predicate;
            this.isModelSelector = isModelSelector;
        }
    }
}
