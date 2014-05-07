package com.parashift.modelGen;

import com.parashift.modelGen.args.OptionException;
import com.parashift.modelGen.args.Options;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Created by ye.yan on 5/7/2014.
 */
public class Application {

    public static void main(String[] args){
        Options opts = null;
        try {
            opts = Options.parseOptions(args);
            System.out.println(opts);

            ModelGenerator.generate(opts);
        } catch (OptionException e) {
            System.err.println(e.getMessage());
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }
}

