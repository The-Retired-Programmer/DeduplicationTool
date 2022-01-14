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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult;
import static uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult.COMPLETEDQUIT;

public class Commands {

    public final Map<String, Command> map = new HashMap<>();
    public final Map<String, String> alias = new HashMap<>();
    private final CommandProcessor commandprocessor;

    public Commands(CommandProcessor commandprocessor) {
        this.commandprocessor = commandprocessor;
        map.put("quit", new QuitCommand());
        map.put("alias", new AliasCommand());
        map.put("export", new Export());
        map.put("newmodel", new NewModel());
        map.put("loadsignatures", new LoadSignatures());
        map.put("extractsignatures", new ExtractSignatures());
        map.put("list", new ListCommand());
        map.put("set", new SetCommand());
        map.put("match", new Matching());
        map.put("run", new RunCommand());
        map.put("filter", new Filter());
        //
        alias.put("q", "quit");
        alias.put("end", "quit");
        alias.put("exit", "quit");
    }

    private class QuitCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            return COMPLETEDQUIT;
        }
    }

    private class ListCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2, 3);
            checkSyntax("list");
            String option = checkOptionsSyntax("parameters", "parameter", "aliases", "alias", "commands");
            switch (option) {
                case "parameters" -> {
                    checkTokenCount(2);
                    for (Entry<String, String> e : parameters.getAll()) {
                        System.out.println(e.getKey() + " = " + e.getValue());
                    }
                }
                case "parameter" -> {
                    checkTokenCount(3);
                    String name = checkSyntaxAndLowercaseNAME();
                    System.out.println(name + " = " + parameters.get(name));
                }
                case "aliases" -> {
                    checkTokenCount(2);
                    for (Entry<String, String> a : alias.entrySet()) {
                        System.out.println(a.getKey() + " = " + a.getValue());
                    }
                }
                case "alias" -> {
                    checkTokenCount(3);
                    String name = checkSyntaxAndLowercaseNAME();
                    System.out.println(name + " = " + alias.get(name));
                }
                case "commands" -> {
                    checkTokenCount(2);
                    System.out.println("filter <filterchanindescriptor> set <filestatus value> - run the filter chanin to create a subset of input filerecordset, set the filestatus to the value on all the subset, only if filestatus was NONE");
                    System.out.println("filter <filterchanindescriptor> reset  - run the filter chanin to create a subset of input filerecordset, reset the filestatus to NONE on all the subset");
                    System.out.println("filter <filterchanindescriptor> as <subset name> - run the filter chanin to create a subset of input filerecordset, and save as a named subset for reuseT");
                    System.out.println("filter <filterchanindescriptor> display - run the filter chanin to create a subset of input filerecordset, display it on SYSOUT");
                    System.out.println("filter <filterchanindescriptor> output <filepath> - run the filter chanin to create a subset of input filerecordset, output it to the filepath (format is same as used in load signature)");
                    System.out.println("filter <filterchanindescriptor> report <filepath> - run the filter chanin to create a subset of input filerecordset, output it to the filepath (format is easier for human reading)");
                    System.out.println("export as <filename> - export the match results to filename");
                    System.out.println("newmodel <modelname> - save the current model and open a new model");
                    System.out.println("loadsignatures from <file or folder> - loads sets of signature files");
                    System.out.println("extractsignatures from <file or folder> [as|replace] signaturesetkey");
                    System.out.println("match by [filepath|digest|filename|filepath-digest-filesize|digest-filesize|filename-digest-filesize]");
                    System.out.println("list parameters - display all parameter values");
                    System.out.println("list parameter <name> - display parameter value");
                    System.out.println("list aliases - display all aliases");
                    System.out.println("list alias <name> - display alias");
                    System.out.println("list commands - list all commands");
                    System.out.println("set <parameter> <value> - define a parameter and set its value");
                    System.out.println("alias <name>  is <command> - create a command alias - note <command> should be quoted if it contains spaces");
                    System.out.println("<aliasname> - execute a command alias");
                    System.out.println("run <command file> - run a command file (located in the model data folder)");
                    System.out.println("quit | q | exit | end  - exit program");
                    return ActionResult.COMPLETEDCONTINUE;
                }
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SetCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(3);
            String name = checkSyntaxAndLowercaseNAME("set");
            String val = checkSyntaxAndNAME();
            parameters.set(name, val);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class AliasCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(4);
            String name = checkSyntaxAndLowercaseNAME("alias");
            String val = checkSyntaxAndNAME("is");
            alias.put(name, val);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class RunCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String name = checkSyntaxAndNAME("run");
            commandprocessor.executeCommandfile(model.getModelName(), name);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }
}
