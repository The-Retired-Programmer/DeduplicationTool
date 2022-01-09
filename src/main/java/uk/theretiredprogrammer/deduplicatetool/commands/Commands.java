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

    public Commands() {
        map.put("quit", new QuitCommand());
        map.put("?", new HelpCommand());
        map.put("alias", new AliasCommand());
        map.put("export", new Export());
        map.put("newmodel", new NewModel());
        map.put("loadsignatures", new LoadSignatures());
        map.put("extractsignatures", new ExtractSignatures());
        map.put("echo", new EchoCommand());
        map.put("set", new SetCommand());
        map.put("match", new Matching());
        map.put("extract", new Extract());
        map.put("mark", new Mark());
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

    private class HelpCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(1);
            System.out.println("export matches as <filename> - export the match results to filename");
            System.out.println("newmodel <modelname> - save the current model and open a new model");
            System.out.println("loadsignatures from <file or folder> - loads sets of signature files");
            System.out.println("extractsignatures from <file or folder> [as|replace] signaturesetkey");
            System.out.println("match by [filepath|digest|filename|filepath-digest-filesize|digest-filesize|filename-digest-filesize]");
            System.out.println("match");
            System.out.println("echo - display all parameter values");
            System.out.println("echo <parameter> - display parameter value");
            System.out.println("set <parameter> <value> - define a parameter and set its value");
            System.out.println("alias <name>  is <command> - create a command alias - note <command> should be quoted if it contains spaces");
            System.out.println("<aliasname> - execute a command alias");
            System.out.println("? - list commands");
            System.out.println("quit | q | exit | end  - exit program");
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class EchoCommand extends Command {

        @Override
        public ActionResult execute() throws IOException {
            int l = checkTokenCount(1, 2);
            if (l == 1) {
                for (Entry<String, String> e : parameters.getAll()) {
                    System.out.println(e.getKey() + " = " + e.getValue());
                }
            } else {
                String name = checkSyntaxAndNAME("echo");
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
    
    private class AliasCommand extends Command {
        
        @Override
        public ActionResult execute() throws IOException {
            checkTokenCount(4);
            String name = checkSyntaxAndNAME("alias").toLowerCase();
            String val = checkSyntaxAndNAME("is");
            alias.put(name,val);
            return ActionResult.COMPLETEDCONTINUE;
        }
        
    }
}
