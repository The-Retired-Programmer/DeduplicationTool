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
package uk.theretiredprogrammer.deduplicatetool.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class LoadSignatures extends Command {

    private int filecount = 0;

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(3);
        File path = checkSyntaxAndFILEPATH("loadsignatures", "from");
        filecount = 0;
        load(path);
        System.out.println(Integer.toString(filecount) + " signature files loaded");
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    private void load(File f) throws IOException {
        if (f.isHidden()) {
            return;
        }
        if (f.isDirectory()) {
            recurseDirectory(f);
        } else {
            loadFile(f);
        }
    }

    private void recurseDirectory(File f) throws IOException {
        for (File child : f.listFiles()) {
            load(child);
        }
    }

    private void loadFile(File f) throws FileNotFoundException, IOException {
        String tag = f.getName();
        try ( Reader rdr = new FileReader(f);  BufferedReader brdr = new BufferedReader(rdr)) {
            String line = brdr.readLine();
            while (line != null) {
                model.load(tag + '§' + line);
                line = brdr.readLine();
            }
        }
        filecount++;
    }
}
