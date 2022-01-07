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
package uk.theretiredprogrammer.deduplicatetool;

import uk.theretiredprogrammer.deduplicatetool.commands.CommandProcessor;
import java.io.IOException;

public class App {

    public static void main(String args[]) {
        try {
            var cp = new CommandProcessor(args[0]);
            cp.executeSYSIN();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
