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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExtractSignatures extends Command {

    private MessageDigest digest;
    private static final int READSIZE = 512;

    @Override
    public Command.ActionResult execute() throws IOException {
        checkTokenCount(5);
        File path = checkSyntaxAndFILEPATH("extractsignatures", "from");
        String option = checkOptionsSyntax("as", "replaces");
        String name = checkSyntaxAndNAME();
        switch (option) {
            case "as" -> {
                extractsignaturesaction(path, name, false);
            }
            case "replaces" -> {
                extractsignaturesaction(path, name, true);
            }
        }
        return Command.ActionResult.COMPLETEDCONTINUE;
    }

    private void extractsignaturesaction(File path, String keyname, boolean replace) throws IOException {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex.getMessage());
        }
        extractsignatures(path, keyname, replace);
    }

    private void extractsignatures(File path, String keyname, boolean replace) throws IOException {
        if (path.isHidden()) {
            return;
        }
        if (path.isDirectory()) {
            processDirectory(path, keyname, replace);
        } else {
            processFile(path, keyname, replace);
        }
    }

    private void processDirectory(File f, String keyname, boolean replace) throws IOException {
        for (File child : f.listFiles()) {
            extractsignatures(child, keyname, replace);
        }
    }

    private void processFile(File f, String keyname, boolean replace) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[READSIZE];
        FileInputStream in = new FileInputStream(f);
        DigestInputStream dis = new DigestInputStream(in, digest);
        dis.on(true);
        int start = 0;
        int bytes;
        do {
            bytes = dis.read(buffer, 0, READSIZE);
            start += bytes;
        } while (bytes == READSIZE);
        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder(digest.getDigestLength() * 2);
        for (int i = 0; i < hash.length; i++) {
            sb.append(toHex((hash[i] >> 4)));
            sb.append(toHex(hash[i]));
        }
        String hashhex = sb.toString();
        //resultswriter.println(f.getCanonicalPath() + "§" + hashhex + "§" + Integer.toString(start));
        digest.reset();
    }

    private static final String HEXCHARS = "0123456789abcdef";

    private char toHex(int val) {
        return HEXCHARS.charAt(val & 0xf);
    }
}
