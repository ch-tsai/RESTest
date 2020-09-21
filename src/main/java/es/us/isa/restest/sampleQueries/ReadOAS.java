package es.us.isa.restest.sampleQueries;

import es.us.isa.restest.specification.OpenAPISpecification;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadOAS {

    public static List<PathItem> getPathItems(String  openApiPath){

        List<PathItem> res = new ArrayList<>();

        OpenAPISpecification spec = new OpenAPISpecification(openApiPath);

        Paths paths = spec.getSpecification().getPaths();

        // TODO: Common parameters
        for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
            PathItem pathItem = entry.getValue();
            res.add(pathItem);
        }

        return res;
    }

}
