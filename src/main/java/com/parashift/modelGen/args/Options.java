package com.parashift.modelGen.args;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ye.yan on 5/7/2014.
 */
public class Options {
    @Option(name="-c", usage="full qualified class name", required = true, metaVar = "CLASS")
    private String className = null;

    @Option(name="-d", usage="base directory where generated class will be placed to")
    private File outputDir = new File(".");

    @Argument
    private List<String> arguments = new ArrayList<>();

    private File input;

    public String getClassName() {
        return className;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public File getInput() {

        return input;
    }

    public static Options parseOptions(String[] args) throws OptionException {
        return new Options().doParse(args);
    }

    private Options doParse(String[] args) throws OptionException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);

        try {
            parser.parseArgument(args);

            if(arguments.size() != 1) {
                throw new CmdLineException(parser, "Please specify one model xml file at a time");
            } else {
                input = new File(arguments.get(0));
            }

            if(!outputDir.exists()){
                throw new CmdLineException(parser, "Output directory must exist");
            }

            return this;
        } catch (CmdLineException e) {

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            Writer buffer = new OutputStreamWriter(byteBuffer);
            try {
                buffer.append(e.getMessage()).append("\n\n");
                buffer.append("ModelGen [options ...] file\n");
                buffer.flush();
                parser.printUsage(byteBuffer);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            throw new OptionException(byteBuffer.toString());
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("class_name = ").append(className).append("\n");
        buffer.append("input_file = ").append(input.getAbsolutePath()).append("\n");
        buffer.append("output_directory = ").append(outputDir.getAbsolutePath()).append("\n");

        return buffer.toString();
    }
}
