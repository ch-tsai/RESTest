package es.us.isa.restest.main;

import es.us.isa.restest.configuration.pojos.Operation;
import es.us.isa.restest.configuration.pojos.TestConfigurationObject;
import es.us.isa.restest.configuration.pojos.TestParameter;
import es.us.isa.restest.specification.OpenAPISpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.us.isa.restest.configuration.TestConfigurationIO.loadConfiguration;
import static es.us.isa.restest.sampleQueries.Predicates.getPredicates;
import static es.us.isa.restest.sampleQueries.Utils.executeSPARQLQuery;
import static es.us.isa.restest.sampleQueries.Utils.generateQuery;
import static es.us.isa.restest.util.PropertyManager.readExperimentProperty;
import static es.us.isa.restest.util.PropertyManager.readProperty;
import static es.us.isa.restest.configuration.generators.DefaultTestConfigurationGenerator.SEMANTIC_PARAMETER;

public class SemanticInputGenerator {

    private static final Logger log = LogManager.getLogger(SemanticInputGenerator.class);
    private static OpenAPISpecification spec;
    private static String OAISpecPath;
    private static String confPath;

    // TODO: Required and optional parameters in TestConf
    // TODO: Override enums
    // TODO: Take restrictions (regExp, min, max, etc.) into consideration
    // TODO: Take arrays into consideration
    // TODO: Take datatypes into consideration
    public static void main(String[] args) {
        // Generate an updated testConf.yaml file with auto-generated inputs
        if(args.length > 0)
            setEvaluationParameters(args[0]);
        else
            setEvaluationParameters(readProperty("evaluation.properties.dir") + "/book.properties");

        if(spec == null) {
            spec = new OpenAPISpecification(OAISpecPath);
        }

        TestConfigurationObject conf = loadConfiguration(confPath, spec);

        Map<String, List<TestParameter>> semanticOperations = getSemanticOperations(conf);

        List<TestParameter> parameters = semanticOperations.get("getBook");

        Map<TestParameter, List<String>> parametersWithPredicates = getPredicates(parameters);

        String queryString = generateQuery(parametersWithPredicates);

        System.out.println(queryString);

        // DBPedia Endpoint
        String szEndpoint = "http://dbpedia.org/sparql";

        List<Map<String,String>> result = new ArrayList<>();

        // Query DBPedia
        try{
            result = executeSPARQLQuery(queryString, szEndpoint);
        }catch(Exception ex){
            System.err.println(ex);
        }

        int i = 1;
        for(Map<String, String> entry: result){
            System.out.println("Result " + i + ": ");
            for(String parameterName: entry.keySet()){
                System.out.println("[" + parameterName + "]: " + entry.get(parameterName));
            }
            i++;
            System.out.println("\n");
        }





    }

    private static void setEvaluationParameters(String evalPropertiesFilePath) {
        OAISpecPath = readExperimentProperty(evalPropertiesFilePath, "oaispecpath");
        confPath = readExperimentProperty(evalPropertiesFilePath, "confpath");
    }

    public static Map<String, List<TestParameter>> getSemanticOperations(TestConfigurationObject testConfigurationObject){
        Map<String, List<TestParameter>> operations = new HashMap<>();
        // TODO: Take operations without semantic parameters into consideration
        for(Operation operation: testConfigurationObject.getTestConfiguration().getOperations()){
            List<TestParameter> semanticParameters = getSemanticParameters(operation);
            if(semanticParameters.size() > 0){
                operations.put(operation.getOperationId(), semanticParameters);
            }
        }

        return operations;
    }

    private static List<TestParameter> getSemanticParameters(Operation operation){

        List<TestParameter> res = operation.getTestParameters().stream()
                .filter(x -> x.getGenerator().getType().equalsIgnoreCase(SEMANTIC_PARAMETER))
                .collect(Collectors.toList());

        return res;
    }


}
