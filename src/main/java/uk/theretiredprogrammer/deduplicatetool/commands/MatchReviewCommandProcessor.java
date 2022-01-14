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
import java.util.List;
import uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult;
import static uk.theretiredprogrammer.deduplicatetool.commands.Command.ActionResult.COMPLETEDQUIT;
import uk.theretiredprogrammer.deduplicatetool.support.Model;
import uk.theretiredprogrammer.deduplicatetool.support.SYSIN;

public class MatchReviewCommandProcessor {

    private final Model model;
    private final MatchReviewCommands commands;

    public MatchReviewCommandProcessor(Model model) throws IOException {
        this.model = model;
        commands = new MatchReviewCommands();
    }

    public void executeSYSIN() throws IOException {
        String line = SYSIN.readLine("¿");
        while (line != null) {
            boolean quit = false;
            try {
                quit = execute(line) == COMPLETEDQUIT;
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            line = quit ? null : SYSIN.readLine("¿");
        }
    }

    private ActionResult execute(String commandline) throws IOException {
        commandline = commandline.trim();
        if (commandline.isBlank() || commandline.startsWith("#") || commandline.startsWith("//")) {
            return ActionResult.COMPLETEDCONTINUE; // empty or comment
        }
        List<String> tokens = extractTokens(commandline);
        int tokencount = tokens.size();
        if (tokencount == 0) {
            throw new IOException("no command found: " + commandline);
        }
        String commandFirstWord = tokens.get(0).toLowerCase();
        Command c = commands.map.get(commandFirstWord);
        if (c == null) {
            throw new IOException("unknown command found: " + commandline);
        }
        c.attach(model, model.parameters);
        c.setTokens(tokens);
        return c.execute();
    }

    private enum State {
        INWHITESPACE, INTOKEN, INQUOTEDTOKEN, INSINGLEQUOTEDTOKEN, INBRACESTOKEN
    };

    private List<String> extractTokens(String commandline) throws IOException {
        List<String> tokens = new ArrayList<>();
        StringBuilder tokenbuffer = new StringBuilder();
        State state = State.INWHITESPACE;
        for (char c : commandline.trim().toCharArray()) {
            switch (state) {
                case INWHITESPACE -> {
                    if (c == '"') {
                        tokenbuffer.setLength(0);
                        state = State.INQUOTEDTOKEN;
                    } else if (c == '\'') {
                        tokenbuffer.setLength(0);
                        state = State.INSINGLEQUOTEDTOKEN;
                    } else if (c == '{') {
                        tokenbuffer.setLength(0);
                        state = State.INBRACESTOKEN;
                    } else if (!Character.isWhitespace(c)) {
                        tokenbuffer.setLength(0);
                        tokenbuffer.append(c);
                        state = State.INTOKEN;
                    }
                }
                case INTOKEN -> {
                    if (Character.isWhitespace(c)) {
                        tokens.add(tokenbuffer.toString());
                        state = State.INWHITESPACE;
                    } else {
                        tokenbuffer.append(c);
                    }
                }
                case INQUOTEDTOKEN -> {
                    if (c == '"') {
                        tokens.add(tokenbuffer.toString());
                        state = State.INWHITESPACE;
                    } else {
                        tokenbuffer.append(c);
                    }
                }
                case INSINGLEQUOTEDTOKEN -> {
                    if (c == '\'') {
                        tokens.add(tokenbuffer.toString());
                        state = State.INWHITESPACE;
                    } else {
                        tokenbuffer.append(c);
                    }
                }
                case INBRACESTOKEN -> {
                    if (c == '}') {
                        tokens.add(tokenbuffer.toString());
                        state = State.INWHITESPACE;
                    } else {
                        tokenbuffer.append(c);
                    }
                }
            }
        }
        switch (state) {
            case INQUOTEDTOKEN ->
                throw new IOException("unterminated quoted string at line end - " + commandline);
            case INSINGLEQUOTEDTOKEN ->
                throw new IOException("unterminated single quoted string at line end - " + commandline);
            case INBRACESTOKEN ->
                throw new IOException("unterminated {...} token at line end - " + commandline);
            case INTOKEN ->
                tokens.add(tokenbuffer.toString());
        }
        return tokens;
    }
}
