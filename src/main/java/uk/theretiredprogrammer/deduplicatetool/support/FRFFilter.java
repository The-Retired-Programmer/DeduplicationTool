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

    private static final BiPredicate<FileRecord, String> only_unset = (fr, p) -> fr.filestatus.isProcessable();
    private static final BiPredicate<FileRecord, String> equal_parentpath = (fr, p) -> fr.parentpath.equals(p);
    private static final BiPredicate<FileRecord, String> equal_tag = (fr, p) -> fr.tag.equals(p);
    private static final BiPredicate<FileRecord, String> equal_tag_and_parentpath = (fr, parameters) -> {
        String[] split = parameters.split(",");
        return split[0].equals(fr.tag) && split[1].equals(fr.parentpath);
    };
    private static final BiPredicate<FileRecord, List<FileRecord>> filenameext_exists_in = (fr, comparelist) -> !comparelist.stream().noneMatch((cf) -> fr.filenameext.equals(cf.filenameext));

    public static FRFFilter parse(String filterstring) throws IOException {
        FRFFilter filter = new FRFFilter();
        // temp
        filter.add(only_unset);
        filter.add(equal_parentpath, "/Users/richard/Desktop/exportphotos");
//        filter.add(equal_tag, "fred");
//        filter.add(equal_tag_and_parentpath, "fred,/Users/richard/Desktop/exportphotos");
//        filter.addModelSelector(filenameext_exists_in, "filterlist1");
        //
//        if (split.length != 2){
//            throw new IOException("Filter function EQUAL-TAG-AND-PARENTPATH should have two comma separated parameters");
//        }

        //
        filter.checkCorrect();
        return filter;
    }

    private final List<PredicateAndParameter> pANDp = new ArrayList<>();

    private FRFFilter() {
    }

    private void add(BiPredicate<FileRecord, String> predicate) {
        add(predicate, null);
    }

    private void add(BiPredicate<FileRecord, String> predicate, String parameter) {
        pANDp.add(new PredicateAndParameter<>(predicate, parameter));
    }

    private void addModelSelector(BiPredicate<FileRecord, List<FileRecord>> predicate, String parameter) {
        pANDp.add(new PredicateAndParameter<>(predicate, new ModelSelector(parameter)));
    }

    void checkCorrect() throws IOException {
        if (false) {
            throw new IOException("Bad Filter chain definition");
        }
    }

    public Stream<FileRecord> executeFilter(Model model, Stream<FileRecord> stream) throws IOException {
        for (PredicateAndParameter p : pANDp) {
            if (p.parameter instanceof ModelSelector k) {
                var list = k.get(model);
                stream = stream.filter((fr) -> p.predicate.test(fr, list));
            } else {
                stream = stream.filter((fr) -> p.predicate.test(fr, p.parameter));
            }
        }
        return stream;
    }

    private class PredicateAndParameter<T, U> {

        public final BiPredicate<FileRecord, T> predicate;
        public final U parameter;

        public PredicateAndParameter(BiPredicate<FileRecord, T> predicate, U parameter) {
            this.predicate = predicate;
            this.parameter = parameter;
        }
    }

    private class ModelSelector {

        private final String key;

        public ModelSelector(String key) {
            this.key = key;
        }

        public List<FileRecord> get(Model model) throws IOException {
            return model.getFilteredModel(key);
        }
    }
}
