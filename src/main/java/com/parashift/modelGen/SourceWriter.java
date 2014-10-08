package com.parashift.modelGen;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by ye.yan on 5/7/2014.
 */
public class SourceWriter implements Closeable{

    private PrintStream source;
    private int indent = 4;

    public SourceWriter(File baseDir, String qualifiedName) throws IOException {
        File sourceFile = new File(baseDir.getCanonicalPath() + File.separator + qualifiedName.replace('.', File.separatorChar) + ".java");
        sourceFile.getParentFile().mkdirs();
        source = new PrintStream(sourceFile);

        String packageName = getPackageName(qualifiedName);
        if(packageName != null){
            source.println("package " + packageName + ";\n\n");
        }

        source.println("import org.alfresco.service.namespace.QName;\n");
        source.println("public interface " + getClassName(qualifiedName) + " {");
    }

    public void writeConstant(String type, String name, String value){
        indent();
        source.format("static final %s %s = %s;\n", type, name, value);
    }

    public void writeComment(String comment){
        source.println();
        indent();
        source.format("// %s\n", comment);
    }

    @Override
    public void close() {
        source.println("}\n");
        source.close();
    }

    public String getPackageName(String qualifiedName){
        int index = qualifiedName.lastIndexOf('.');
        return index == -1? null:qualifiedName.substring(0, index);
    }

    public String getClassName(String qualifiedName){
        int index = qualifiedName.lastIndexOf('.');
        return index == -1? qualifiedName:qualifiedName.substring(index + 1, qualifiedName.length());
    }

    private void indent(){
        for(int i =0;i<indent;i++){
            source.print(' ');
        }
    }
}
