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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult;
import static uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult.COMPLETEDQUIT;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord;
import static uk.theretiredprogrammer.deduplicatetool.support.FileRecord.COMPARE_DIGEST_FILESIZE;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecord.FileStatus;
import uk.theretiredprogrammer.deduplicatetool.support.FileRecords;
import uk.theretiredprogrammer.deduplicatetool.support.Model;

public class MatchStatusReviewCommands {

    public final Map<String, Command> map = new HashMap<>();

    public MatchStatusReviewCommands(Model model) throws IOException {
        map.put("q", new Quit());
        map.put("|<<<", new First());
        map.put("|>>>", new Last());
        map.put("|>", new Next());
        map.put("|>>", new NextWithNONE());
        map.put("|<", new Previous());
        map.put("|<<", new PreviousWithNONE());
        map.put("<<<", new FirstRecord());
        map.put(">>>", new LastRecord());
        map.put(">", new NextRecord());
        map.put(">>", new NextRecordWithNONE());
        map.put("<", new PreviousRecord());
        map.put("<<", new PreviousRecordWithNONE());
        map.put("?", new DisplayParentFileRecords());
        //
        map.put("|h", new AddHints());
        map.put("h", new AddHint());
        map.put("|h+", new AppendHints());
        map.put("h+", new AppendHint());
        //
        map.put("|s-", new SetUnMatchedNONEs());
        map.put("|s+", new SetMatchedNONEs());
        map.put("|s", new SetAllNONEs());
        map.put("|sa", new SetAll());
        map.put("s", new Set());
        map.put("|m", new MatchesDisplay());
        map.put("m", new MatchDisplay());
        map.put("|mf+", new MatchesFolderDisplay());
        map.put("|mf-", new MatchesLimitedFolderDisplay());
        map.put("|mfe", new MatchesFolderDisplayEditor());

        //
        parentFileRecordsSet = new LinkedList<>(model.getParentsFileRecords().values());
        if (parentFileRecordsSet.isEmpty()) {
            throw new IOException("Empty ParentsFileRecords");
        }
        parentiterator = parentFileRecordsSet.listIterator();
        currentParentFileRecords = parentiterator.next();
        completeparentchange();
    }

    private final List<FileRecords> parentFileRecordsSet;
    private final ListIterator<FileRecords> parentiterator;
    private FileRecords currentParentFileRecords;
    private ListIterator<FileRecord> filerecorditerator;
    private FileRecord currentFileRecord;

    private void completeparentchange() {
        filerecorditerator = currentParentFileRecords.listIterator();
        displayCurrentParentFileRecords();
    }

    private void displayCurrentParentFileRecords() {
        System.out.println(currentParentFileRecords.get(0).parentpath);
        currentParentFileRecords.stream().filter(fr -> (fr.getFileStatus().equals(FileStatus.NONE))).forEachOrdered(fr -> System.out.println(fr.toFilenameListString()));
        System.out.println(currentParentFileRecords.size() + " files in this folder");
    }

    private void displayAllCurrentParentFileRecords() {
        System.out.println(currentParentFileRecords.get(0).parentpath);
        for (FileRecord fr : currentParentFileRecords) {
            System.out.println(fr.toFilenameListString());
        }
    }

    private void displayCurrentFileRecord() {
        System.out.println(currentFileRecord.toFilenameListString());
    }

    private class DisplayParentFileRecords extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (filerecorditerator.hasPrevious()) {
                currentFileRecord = filerecorditerator.previous();
            }
            displayAllCurrentParentFileRecords();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Quit extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            return COMPLETEDQUIT;
        }
    }

    private boolean containsNONE(FileRecords filerecords) {
        for (FileRecord fr : filerecords) {
            if (fr.getFileStatus().equals(FileStatus.NONE)) {
                return true;
            }
        }
        return false;
    }

    private class AddHints extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String hint = checkSyntaxAndNAME("|h");
            for (FileRecord fr : currentParentFileRecords) {
                if (fr.getFileStatus().equals(FileStatus.NONE)) {
                    fr.hint = hint;
                }
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class AddHint extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String hint = checkSyntaxAndNAME("h");
            currentFileRecord.setHint(hint);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class AppendHints extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String hint = checkSyntaxAndNAME("|h+");
            currentParentFileRecords.stream().filter(fr -> (fr.getFileStatus().equals(FileStatus.NONE))).forEachOrdered(fr -> fr.appendHint(hint));
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class AppendHint extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String hint = checkSyntaxAndNAME("h+");
            currentFileRecord.appendHint(hint);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SetUnMatchedNONEs extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String newstatus = checkSyntaxAndNAME("|s-");
            for (FileRecord fr : currentParentFileRecords) {
                if (fr.getFileStatus().equals(FileStatus.NONE) && !fr.hasMatch()) {
                    fr.setFileStatus(newstatus);
                }
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SetMatchedNONEs extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String newstatus = checkSyntaxAndNAME("|s+");
            for (FileRecord fr : currentParentFileRecords) {
                if (fr.getFileStatus().equals(FileStatus.NONE) && fr.hasMatch()) {
                    fr.setFileStatus(newstatus);
                }
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SetAllNONEs extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String newstatus = checkSyntaxAndNAME("|s");
            for (FileRecord fr : currentParentFileRecords) {
                if (fr.getFileStatus().equals(FileStatus.NONE)) {
                    fr.setFileStatus(newstatus);
                }
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SetAll extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String newstatus = checkSyntaxAndNAME("|sa");
            for (FileRecord fr : currentParentFileRecords) {
                fr.setFileStatus(newstatus);
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private List<FileRecords> matchingFilesByParentpath;
    private FileRecords matchedFiles;

    private class MatchesDisplay extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            checkSyntax("|m");
            System.out.println(currentParentFileRecords.get(0).parentpath);
            FileRecords mrs = new FileRecords();
            matchedFiles = new FileRecords();
            for (FileRecord fr : currentParentFileRecords) {
                if (fr.getFileStatus().equals(FileStatus.NONE) && fr.hasMatch()) {
                    matchedFiles.add(fr);
                    System.out.println(fr.toFilenameListString());
                    for (FileRecord mr : model) {
                        if (fr != mr && COMPARE_DIGEST_FILESIZE.compare(fr, mr) == 0 && mr.getFileStatus().isUseInMatching()) {
                            mrs.add(mr);
                            System.out.println("    " + mr.toList2String());
                        }
                    }
                }
            }
            matchingFilesByParentpath = mrs.stream().map(fr -> fr.parentpath).distinct().map(parentpath -> createparentpathlist(parentpath, mrs)).collect(Collectors.toList());
            return ActionResult.COMPLETEDCONTINUE;
        }

        private FileRecords createparentpathlist(String parentpath, FileRecords mrs) {
            return new FileRecords(mrs.stream().filter(fr -> fr.parentpath.equals(parentpath)).collect(Collectors.toList()));
        }
    }

    private class MatchesFolderDisplay extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            checkSyntax("|mf+");
            System.out.println(currentParentFileRecords.get(0).parentpath);
            currentParentFileRecords.forEach(fr -> System.out.println(fr.toFilenameListString()));
            matchingFilesByParentpath.forEach(mr -> displayFullFolder(mr.get(0).parentpath));
            return ActionResult.COMPLETEDCONTINUE;
        }

        private void displayFullFolder(String parentpath) {
            System.out.println("    " + parentpath);
            model.stream().filter(mr -> (mr.parentpath.equals(parentpath))).forEachOrdered(mr -> System.out.println("      " + mr.toFilenameListString()));
        }
    }

    private class MatchesLimitedFolderDisplay extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            checkSyntax("|mf-");
            System.out.println(currentParentFileRecords.get(0).parentpath);
            currentParentFileRecords.forEach(fr -> System.out.println(fr.toFilenameListString()));
            matchingFilesByParentpath.forEach(mr -> displayLimitedFolder(mr));
            return ActionResult.COMPLETEDCONTINUE;
        }

        private void displayLimitedFolder(FileRecords files) {
            System.out.println("    " + files.get(0).parentpath);
            files.stream().forEachOrdered(mr -> System.out.println("      " + mr.toFilenameListString()));
        }
    }

    private class MatchesFolderDisplayEditor extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2 + matchingFilesByParentpath.size());
            checkSyntax("|mfe");
            List<FileStatus> newstatus = new ArrayList<>();
            for (int i = 0; i < matchingFilesByParentpath.size() + 1; i++) {
                newstatus.add(FileStatus.valueOf(checkSyntaxAndNAME()));
            }
            matchedFiles.stream().forEachOrdered((fr) -> fr.setFileStatus(newstatus.get(0)));
            int index = 1;
            for (FileRecords frs : matchingFilesByParentpath) {
                for (FileRecord fr : frs) {
                    fr.setFileStatus(newstatus.get(index));
                }
                index++;
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class MatchDisplay extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            checkSyntax("m");
            System.out.println(currentFileRecord.toList2String());
            for (FileRecord mr : model) {
                if (currentFileRecord != mr && COMPARE_DIGEST_FILESIZE.compare(currentFileRecord, mr) == 0) {
                    System.out.println("    " + mr.toList2String());
                }
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Set extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String newstatus = checkSyntaxAndNAME("s");
            currentFileRecord.setFileStatus(newstatus);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class First extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (parentiterator.hasPrevious()) {
                currentParentFileRecords = parentiterator.previous();
            }
            completeparentchange();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Previous extends Command {

        @Override
        public ActionResult execute() throws IOException {
            if (parentiterator.hasPrevious()) {
                currentParentFileRecords = parentiterator.previous();
            }
            completeparentchange();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class PreviousWithNONE extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (parentiterator.hasPrevious()) {
                currentParentFileRecords = parentiterator.previous();
                if (containsNONE(currentParentFileRecords)) {
                    break;
                }
            }
            completeparentchange();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Next extends Command {

        @Override
        public ActionResult execute() throws IOException {
            if (parentiterator.hasNext()) {
                currentParentFileRecords = parentiterator.next();
            }
            completeparentchange();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class NextWithNONE extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (parentiterator.hasNext()) {
                currentParentFileRecords = parentiterator.next();
                if (containsNONE(currentParentFileRecords)) {
                    break;
                }
            }
            completeparentchange();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Last extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (parentiterator.hasNext()) {
                currentParentFileRecords = parentiterator.next();
            }
            completeparentchange();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class FirstRecord extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (filerecorditerator.hasPrevious()) {
                currentFileRecord = filerecorditerator.previous();
            }
            displayCurrentFileRecord();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class PreviousRecord extends Command {

        @Override
        public ActionResult execute() throws IOException {
            if (filerecorditerator.hasPrevious()) {
                currentFileRecord = filerecorditerator.previous();
            }
            displayCurrentFileRecord();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class PreviousRecordWithNONE extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (filerecorditerator.hasPrevious()) {
                currentFileRecord = filerecorditerator.previous();
                if (currentFileRecord.getFileStatus().equals(FileStatus.NONE)) {
                    break;
                }
            }
            displayCurrentFileRecord();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class NextRecord extends Command {

        @Override
        public ActionResult execute() throws IOException {
            if (filerecorditerator.hasNext()) {
                currentFileRecord = filerecorditerator.next();
            }
            displayCurrentFileRecord();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class NextRecordWithNONE extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (filerecorditerator.hasNext()) {
                currentFileRecord = filerecorditerator.next();
                if (currentFileRecord.getFileStatus().equals(FileStatus.NONE)) {
                    break;
                }
            }
            displayCurrentFileRecord();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class LastRecord extends Command {

        @Override
        public ActionResult execute() throws IOException {
            while (filerecorditerator.hasNext()) {
                currentFileRecord = filerecorditerator.next();
            }
            displayCurrentFileRecord();
            return ActionResult.COMPLETEDCONTINUE;
        }
    }
}
