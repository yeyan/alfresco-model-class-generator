package com.parashift.modelGen;

import com.parashift.modelGen.args.Options;
import com.parashift.modelGen.xml.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.*;

/**
 * Created by ye.yan on 5/7/2014.
 */
public class ModelGenerator {

    public static void generate(Options opts) throws IOException, JAXBException {
        SourceWriter source = null;
        Model model = null;
        GenerateModel generateModel = new GenerateModel();

        try {
            JAXBContext context = JAXBContext.newInstance("com.parashift.modelGen.xml");
            Unmarshaller unmarshaller = context.createUnmarshaller();

            model = (Model) unmarshaller.unmarshal(opts.getInput());
            source = new SourceWriter(opts.getOutputDir(), opts.getClassName());

            generateModel.execute(model, source);
        } finally {
            source.close();
        }
    }
}

class GenerateModel {
    private Map<String, String> prefixMap = new HashMap<>();
    private static Map<String, String> abbrevMap = new HashMap<>();

    static {
        abbrevMap.put("Property","prop");
        abbrevMap.put("ChildAssociation", "assoc");
        abbrevMap.put("Association", "assoc");
    }

    public static String getAbbrev(String value){
        String abbrev = abbrevMap.get(value);
        return abbrev == null? value: abbrev;
    }

    public void execute(Model model, SourceWriter source) {
        if (model.getNamespaces() != null) {
            source.writeComment(model.getName() + " model namespaces");
            for (Model.Namespaces.Namespace namespace : model.getNamespaces().getNamespace()) {
                prefixMap.put(namespace.getPrefix(), genUri(namespace, source));
            }
        }

        try {
            if (model.getTypes() != null) {
                for (Type type : model.getTypes().getType()) {
                    genEntity(type, source);
                }
            }

            if (model.getAspects() != null){
                for(Aspect aspect: model.getAspects().getAspect()){
                    genEntity(aspect, source);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String genUri(Model.Namespaces.Namespace namespace, SourceWriter source){
        String constName = namespace.getPrefix().toUpperCase() + "_" + getVersion(namespace.getUri()) + "_URI";
        source.writeConstant("String", constName , "\"" + namespace.getUri() + "\"");
        return constName;
    }

    public void genQName(Object entity, SourceWriter source) throws Exception {
        String entityName = (String) PropertyUtils.getSimpleProperty(entity, "name");

        String prefix = getPrefix(entityName);
        String name = getName(entityName);

        List<String> constNameParts = new ArrayList<>();

        constNameParts.add(getAbbrev(entity.getClass().getSimpleName()));
        constNameParts.add(prefix);
        constNameParts.add(StringUtils.join(name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"), "_"));

        String constName = StringUtils.join(constNameParts, "_").toUpperCase();
        source.writeConstant("QName", constName, String.format("QName.createQName(%s, \"%s\")", prefixMap.get(prefix), name));
    }

    public void genCollection(Object entity, String collName, String elementName, SourceWriter source) throws Exception{
        Object coll = PropertyUtils.getSimpleProperty(entity, collName);
        if(coll != null){
            for(Object element: (Collection) PropertyUtils.getSimpleProperty(coll, elementName)){
                genQName(element, source);
            }
        }
    }

    public void genEntity(Object entity, SourceWriter source) throws Exception {
        source.writeComment((String) PropertyUtils.getSimpleProperty(entity, "name"));

        //gen entity comment
        genQName(entity, source);
        //gen properties
        genCollection(entity,"properties", "property", source);

        //gen associations
        genCollection(entity,"associations", "association", source);
        genCollection(entity,"associations", "childAssociation", source);
    }

    public String getVersion(String uri){
        int index = uri.lastIndexOf("/");
        return uri.substring(index + 1, uri.length()).replace('.', '_');
    }

    public String getPrefix(String qname){
        int index = qname.indexOf(':');
        return qname.substring(0, index);
    }

    public String getName(String qname){
        int index = qname.indexOf(':');
        return qname.substring(index + 1, qname.length());
    }
}

