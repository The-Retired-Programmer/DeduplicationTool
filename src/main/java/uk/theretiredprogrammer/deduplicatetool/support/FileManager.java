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
package uk.theretiredprogrammer.deduplicatetool.support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public class FileManager {

    private static final String DATAROOT = "?RL?DeduplicateTool-Data";
    
    private static File file(String path, Parameters parameters) throws IOException {
        return new File(parameters.substitute(path));
    }

    public static BufferedReader openModelReader(String modelname, Parameters parameters) throws IOException {
        File modelpath = file(DATAROOT + "/" + modelname, parameters);
        File datapath = new File(modelpath + "/" + modelname);
        if (modelpath.exists()) {
            if (modelpath.isDirectory()) {
                return openreader(datapath);
            } else {
                throw new IOException("Malformed DepulicateTool-Data/" + modelname);
            }
        } else {
            if (modelpath.mkdirs()) {
                if (datapath.createNewFile()) {
                    return openreader(datapath);
                } else {
                    throw new IOException("Could not create new datafile for DepulicateTool-Data/" + modelname);
                }
            } else {
                throw new IOException("Could not create folder structure for DepulicateTool-Data/" + modelname);
            }
        }
    }
    
    public static BufferedReader openCommandFileReader(String modelname, String filename, Parameters parameters) throws IOException {
        File modelpath = file(DATAROOT + "/" + modelname, parameters);
        File configpath = new File(modelpath + "/"+filename );
        if (modelpath.exists()) {
            if (modelpath.isDirectory()) {
                return configpath.exists()? openreader(configpath): null;
            } else {
                throw new IOException("Malformed DepulicateTool-Data/" + modelname);
            }
        } else {
            throw new IOException("Missing DepulicateTool-Data/" + modelname);
        }
    }
    
    public static BufferedReader openInReader() throws IOException {
        Reader rdr = new InputStreamReader(System.in);
        return new BufferedReader(rdr);
    }

    private static BufferedReader openreader(File path) throws FileNotFoundException {
        Reader rdr = new FileReader(path);
        return new BufferedReader(rdr);
    }

    public static PrintWriter openModelWriter(String modelname, Parameters parameters) throws IOException {
        File modelpath = file(DATAROOT + "/" + modelname, parameters);
        File datapath = new File(modelpath + "/" + modelname);
        File datapathbackup = new File(modelpath + "/" + modelname + ".bak");
        if (datapath.exists()) {
            datapathbackup.delete();
            datapath.renameTo(datapathbackup);
            if (datapath.createNewFile()) {
                Writer fw = new FileWriter(datapath);
                BufferedWriter bw = new BufferedWriter(fw);
                return new PrintWriter(bw);
            } else {
                throw new IOException("Could not create replacement datafile for DepulicateTool-Data/" + modelname);
            }
        } else {
            throw new IOException("Model datafile missing: " + modelname);
        }
    }
    
    public static PrintWriter openWriter(File datapath) throws IOException {
        if (datapath.createNewFile()) {
            Writer fw = new FileWriter(datapath);
            BufferedWriter bw = new BufferedWriter(fw);
            return new PrintWriter(bw);
        } else {
            throw new IOException("Could not create export/report datafile for " + datapath.getCanonicalPath());
        }
    }
}
