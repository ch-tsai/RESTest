package es.us.isa.restest.main;

import es.us.isa.restest.configuration.pojos.Operation;
import es.us.isa.restest.configuration.pojos.TestConfigurationObject;
import es.us.isa.restest.configuration.pojos.TestParameter;
import es.us.isa.restest.sampleQueries.SemanticOperation;
import es.us.isa.restest.specification.OpenAPISpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static es.us.isa.restest.util.FileManager.createDir;
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
    private static String csvPath = "src/main/resources/TestData/Generated/";

    // TODO: Required and optional parameters in TestConf
    // TODO: Override enums
    // TODO: Take restrictions (regExp, min, max, etc.) into consideration
    // TODO: Take arrays into consideration
    // TODO: Take datatypes into consideration
    public static void main(String[] args) {
        // Generate an updated testConf.yaml file with auto-generated inputs
//        if(args.length > 0)
//            setEvaluationParameters(args[0]);
//        else
        setEvaluationParameters(readProperty("evaluation.properties.dir") + "/book.properties");

        TestConfigurationObject conf = loadConfiguration(confPath, spec);


        // DBPedia Endpoint
        String szEndpoint = "http://dbpedia.org/sparql";

        // Key: OperationName       Value: Parameters
        // Change to List<SemanticOperation>
        List<SemanticOperation> semanticOperations = getSemanticOperations(conf);

        // TODO: For loop and convert to class
        for(SemanticOperation semanticOperation: semanticOperations){
            Set<TestParameter> parameters = semanticOperation.getSemanticParameters().keySet();

            Map<TestParameter, List<String>> parametersWithPredicates = getPredicates(parameters);

            String queryString = generateQuery(parametersWithPredicates);
            System.out.println(queryString);

            // Query DBPedia
            Map<String, Set<String>> result = new HashMap<>();
            try{
                result = executeSPARQLQuery(queryString, szEndpoint);
            }catch(Exception ex){
                System.err.println(ex);
            }

            for(TestParameter testParameter: semanticOperation.getSemanticParameters().keySet()){
                Map<TestParameter, Set<String>> map = semanticOperation.getSemanticParameters();
                map.put(testParameter, result.get(testParameter.getName()));
                semanticOperation.setSemanticParameters(map);
            }

        }

        // TODO: Write csv
        // TODO: Update TestConf
        // TODO: Add log messages

        // Create dir for automatically generated csv files
        createDir(csvPath);

        // Generate a csv file for each parameter
        // File name = OperationName_ParameterName
        // Delete file if it exists
       for(SemanticOperation operation: semanticOperations){
           for(TestParameter parameter: operation.getSemanticParameters().keySet()){
               String fileName = "/" + operation.getOperationName() + "_" + parameter.getName();

           }
       }

    }

    private static void setEvaluationParameters(String evalPropertiesFilePath) {
        OAISpecPath = readExperimentProperty(evalPropertiesFilePath, "oaispecpath");
        confPath = readExperimentProperty(evalPropertiesFilePath, "confpath");
        spec = new OpenAPISpecification(OAISpecPath);
        csvPath = csvPath + spec.getSpecification().getInfo().getTitle();
    }

    public static List<SemanticOperation> getSemanticOperations(TestConfigurationObject testConfigurationObject){
        List<SemanticOperation> semanticOperations = new ArrayList<>();
        // TODO: Take operations without semantic parameters into consideration
        for(Operation operation: testConfigurationObject.getTestConfiguration().getOperations()){
            List<TestParameter> semanticParameters = getSemanticParameters(operation);
            if(semanticParameters.size() > 0){
                semanticOperations.add(new SemanticOperation(operation, semanticParameters));
            }
        }

        return semanticOperations;
    }

    private static List<TestParameter> getSemanticParameters(Operation operation){

        List<TestParameter> res = operation.getTestParameters().stream()
                .filter(x -> x.getGenerator().getType().equalsIgnoreCase(SEMANTIC_PARAMETER))
                .collect(Collectors.toList());

        return res;
    }


}
