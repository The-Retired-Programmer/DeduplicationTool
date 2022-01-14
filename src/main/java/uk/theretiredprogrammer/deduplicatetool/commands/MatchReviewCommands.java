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
import uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult;
import static uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult.COMPLETEDQUIT;

public class MatchReviewCommands {

    public final Map<String, Command> map = new HashMap<>();

    public MatchReviewCommands() {
        map.put("q", new Quit());
        map.put(">", new Next());
        map.put("<", new Previous());
        map.put("1", new SelectFileRecord(1));
        map.put("2", new SelectFileRecord(2));
        map.put("3", new SelectFileRecord(3));
        map.put("4", new SelectFileRecord(4));
        map.put("5", new SelectFileRecord(5));
        map.put("6", new SelectFileRecord(6));
        map.put("7", new SelectFileRecord(7));
        map.put("8", new SelectFileRecord(8));
        map.put("9", new SelectFileRecord(9));
        map.put("?", new DisplayMatch());
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
//            checkTokenCount(3);
//            String name = checkSyntaxAndLowercaseNAME("set");
//            String val = checkSyntaxAndNAME();
//            parameters.set(name, val);
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Next extends Command {

        @Override
        public ActionResult execute() throws IOException {
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class Previous extends Command {

        @Override
        public ActionResult execute() throws IOException {
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class SelectFileRecord extends Command {

        private final int recno;

        public SelectFileRecord(int recno) {
            this.recno = recno;
        }

        @Override
        public ActionResult execute() throws IOException {
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

    private class DisplayMatch extends Command {

        @Override
        public ActionResult execute() throws IOException {
            return ActionResult.COMPLETEDCONTINUE;
        }
    }

}
