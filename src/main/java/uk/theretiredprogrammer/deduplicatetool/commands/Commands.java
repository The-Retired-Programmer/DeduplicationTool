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

    public Commands() {
        Command quit = new QuitCommand();
        map.put("quit", quit);
        map.put("end", quit);
        map.put("exit", quit);
        map.put("?", new HelpCommand());
        map.put("export", new Export());
        map.put("newmodel", new NewModel());
        map.put("loadsignatures", new LoadSignatures());
        map.put("extractsignatures", new ExtractSignatures());
        map.put("find", new FindDuplicates());
        map.put("echo", new EchoCommand());
        map.put("set", new SetCommand());
        map.put("match", new Matching());
    }

    private class QuitCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            return COMPLETEDQUIT;
        }
    }

    private class HelpCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            System.out.println("? - list commands");
            System.out.println("quit | exit | end  - exit program");
            System.out.println("export as <filename> - dump the results to filename");
            System.out.println("newmodel <modelname> - save the current model and open a new model");
            System.out.println("loadsignatures from <file or folder> - loads sets of signature files");
            System.out.println("extractsignatures from <file or folder> [as|replace] signaturesetkey");
            System.out.println("find duplicates using [digests|filenames]");
            System.out.println("match duplicates using [filepaths]");
            System.out.println("echo ALL - display all parameter values");
            System.out.println("echo <parameter> - display parameter value");
            System.out.println("set <parameter> <value> - define a parameter and set its value");
            return ActionResult.COMPLETEDCONTINUE;
        }
    }


    private class EchoCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(2);
            String name = checkSyntaxAndNAME("echo");
            if (name.equalsIgnoreCase("all")) {
                for (Entry<String, String> e : parameters.getAll()) {
                    System.out.println(e.getKey() + " = " + e.getValue());
                }
            } else {
                System.out.println(name + " = " + parameters.get(name));
            }
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SetCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(3);
            String name = checkSyntaxAndNAME("set");
            String val = checkSyntaxAndNAME();
            parameters.set(name, val);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }
}
