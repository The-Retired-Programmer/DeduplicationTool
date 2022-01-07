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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Parameters {

    private static final char BRA = '?';
    private static final char KET = '?';

    private final Map<String, String> parameters = new HashMap<>();

    public String substitute(String s) throws IOException {
        int nextsubat = s.indexOf(BRA);
        if (nextsubat == -1) {
            return s;
        }
        int endsubat = s.indexOf(KET, nextsubat + 1);
        if (endsubat == -1) {
            throw new IOException("Missing end substitute bracket - " + s);
        }
        String pname = s.substring(nextsubat + 1, endsubat);
        String pvalue = parameters.get(pname);
        if (pvalue == null) {
            throw new IOException("Parameter " + pname + " is undefined - " + s);
        }
        return s.substring(0, nextsubat)
                + pvalue
                + substitute(s.substring(endsubat + 1));
    }

    public String get(String pname) throws IOException {
        if (!parameters.containsKey(pname)) {
            throw new IOException("Parameter " + pname + " does not exist");
        }
        return parameters.get(pname);
    }

    public Set<Map.Entry<String, String>> getAll() {
        return parameters.entrySet();
    }

    public void set(String pname, String pvalue) throws IOException {
        if (parameters.containsKey(pname)) {
            throw new IOException("Attempting to redefine parameter " + pname + "=" + pvalue);
        }
        parameters.put(pname, pvalue);
    }
}
