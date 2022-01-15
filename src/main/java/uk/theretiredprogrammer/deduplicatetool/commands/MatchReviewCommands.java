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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult;
import static uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult.COMPLETEDQUIT;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecordSet;
import uk.theretiredprogrammer.deduplicatetool.support.Model;

public class MatchReviewCommands {

    public final Map<String, Command> map = new HashMap<>();

    public MatchReviewCommands(Model model) throws IOException {
        map.put("q", new Quit());
        map.put("<<", new First());
        map.put(">>", new Last());
        map.put(">", new Next());
        map.put("<", new Previous());
        map.put("1", new SelectFileRecord(0));
        map.put("2", new SelectFileRecord(1));
        map.put("3", new SelectFileRecord(2));
        map.put("4", new SelectFileRecord(3));
        map.put("5", new SelectFileRecord(4));
        map.put("6", new SelectFileRecord(5));
        map.put("7", new SelectFileRecord(6));
        map.put("8", new SelectFileRecord(7));
        map.put("9", new SelectFileRecord(8));
        map.put("?", new DisplayMatch());
        map.put("set", new SetCommand());
        //
        matches = model.getMatchRecords();
        if (matches.isEmpty()) {
            throw new IOException("No Matches created");
        }
        sizeFRS = matches.size();
        first();
    }

    private final List<FileRecordSet> matches;
    private int indexFRS;
    private int sizeFRS;
    private List<FileRecord> currentMatchFileRecords;
    private int indexFR;
    private int sizeFR;
    private FileRecord currentSelectedFileRecord;
    
    private void moveToNewMatchFRS() {
        currentMatchFileRecords = new ArrayList<>(matches.get(indexFRS));
        indexFR=0;
        sizeFR= currentMatchFileRecords.size();
        currentSelectedFileRecord = currentMatchFileRecords.get(0);
        displayMatch();
    }

    private void first() {
        indexFRS = 0;
        moveToNewMatchFRS();
    }
    
    private void displayMatch() {
        for (FileRecord fr : currentMatchFileRecords) {
            System.out.println(fr.toReportString());
        }
    }

    private class Quit extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            return COMPLETEDQUIT;
        }
    }

    private class SetCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(3);
            String name = checkSyntaxAndLowercaseNAME("set");
            String option = checkOptionsSyntax("tag", "path", "parentpath", "filename", "filenameext",
                    "digest", "filesize", "filestatus", "isfilestatusupdateable");
            switch (option){
                case "tag" -> model.parameters.set(name,currentSelectedFileRecord.tag);
                case "path" -> model.parameters.set(name,currentSelectedFileRecord.path);
                case "parentpath" -> model.parameters.set(name,currentSelectedFileRecord.parentpath);
                case "filename" -> model.parameters.set(name,currentSelectedFileRecord.filename);
                case "filenameext"-> model.parameters.set(name,currentSelectedFileRecord.filenameext);
                case "digest" -> model.parameters.set(name, currentSelectedFileRecord.digest);
                case "filesize" -> model.parameters.set(name, Integer.toString(currentSelectedFileRecord.filesize));
                case "filestatus" -> model.parameters.set(name, currentSelectedFileRecord.filestatus.toString());
                case "isfilestatusupdateable" -> model.parameters.set(name, 
                        currentSelectedFileRecord.filestatus.isProcessable()?"True":"False");
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }
    
    private class First extends Command {

        @Override
        public ActionResult execute() throws IOException {
            indexFRS = 0;
            moveToNewMatchFRS();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Previous extends Command {

        @Override
        public ActionResult execute() throws IOException {
            if (indexFRS > 0) {
                indexFRS--;
                moveToNewMatchFRS();
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Next extends Command {

        @Override
        public ActionResult execute() throws IOException {
            if (indexFRS < sizeFRS - 1) {
                indexFRS++;
                moveToNewMatchFRS();
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Last extends Command {

        @Override
        public ActionResult execute() throws IOException {
            indexFRS = sizeFRS - 1;
            moveToNewMatchFRS();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class DisplayMatch extends Command {

        @Override
        public ActionResult execute() throws IOException {
            displayMatch();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }
    
    private class SelectFileRecord extends Command {

        private final int index;

        public SelectFileRecord(int index) {
            this.index = index;
        }

        @Override
        public ActionResult execute() throws IOException {
            if (index<sizeFR && index >= 0) {
                indexFR = index;
                currentSelectedFileRecord = currentMatchFileRecords.get(indexFR);
                System.out.println(currentSelectedFileRecord.toReportString());
            }
            
            return ActionResult.COMPLETEDCONTINUE;
        }
    }
}
