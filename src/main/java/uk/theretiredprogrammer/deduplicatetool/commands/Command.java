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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import uk.theretiredprogrammer.deduplicatetool.support.Model;

public abstract class Command {

    protected Model model;
    protected List<String> tokens;
    private ListIterator<String> tokenreader;

    public static enum ActionResult {
        COMPLETEDCONTINUE, COMPLETEDQUIT
    }

    public void attach(Model model) {
        this.model = model;
    }

    public abstract ActionResult execute() throws IOException;

    protected void setTokens(List<String> tokens) {
        this.tokens = tokens;
        tokenreader = this.tokens.listIterator();
    }

    private String nextToken() throws IOException {
        if (tokenreader.hasNext()) {
            return tokenreader.next();
        } else {
            throw new IOException("Command reader failure");
        }
    }
    
    protected int checkTokenCount(int... expectedlength) throws IOException {
        int actuallength = tokens.size();
        for (int length: expectedlength){
            if ( actuallength == length) {
                return length;
            }
        }
        throw new IOException("Bad command syntax - incorrect number of parameters - found "+tokens.size());
    }

    protected void checkTokenCount(int expectedlength) throws IOException {
        if (tokens.size() != expectedlength) {
            throw new IOException("Bad command syntax - incorrect number of parameters - expected "+expectedlength+"; found "+tokens.size());
        }
    }

    protected void checkSyntax(String... syntax) throws IOException {
        for (var syntaxitem : syntax) {
            if (!nextToken().equalsIgnoreCase(syntaxitem)) {
                throw new IOException("Bad command syntax - expected " + syntaxitem);
            }
        }
    }

    protected File checkSyntaxAndFILEPATH(String... syntax) throws IOException {
        checkSyntax(syntax);
        return new File(model.parameters.substitute(nextToken()));
    }

    protected String checkSyntaxAndNAME(String... syntax) throws IOException {
        checkSyntax(syntax);
        return nextToken();
    }
    
    protected String checkSyntaxAndLowercaseNAME(String... syntax) throws IOException {
        checkSyntax(syntax);
        return nextToken().toLowerCase();
    }

    protected String checkOptionsSyntax(String... syntax) throws IOException {
        String token = nextToken();
        for (var syntaxitem : syntax) {
            if (token.equalsIgnoreCase(syntaxitem)) {
                return syntaxitem;
            }
        }
        throw new IOException("Bad command syntax - option not present");
    }
}
