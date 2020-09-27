package es.us.isa.restest.sampleQueries;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.PathItem;
import org.apache.jena.base.Sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.us.isa.restest.sampleQueries.Predicates.getPredicates;
import static es.us.isa.restest.sampleQueries.ReadOAS.getPathItems;
import static es.us.isa.restest.sampleQueries.Utils.generateQuery;
import static es.us.isa.restest.sampleQueries.Utils.executeSPARQLQuery;

public class Main {

    public static void main(String[] args) {

        String openApiPath = "src/test/resources/Book/swagger.yaml";


        List<PathItem> pathItems = getPathItems(openApiPath);

        // TODO: Do this automatically
        Operation operation = pathItems.get(0).getGet();

        List<Parameter> parameters = operation.getParameters();

        Map<Parameter, List<String>> parametersWithPredicates = getPredicates(parameters);


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
}
