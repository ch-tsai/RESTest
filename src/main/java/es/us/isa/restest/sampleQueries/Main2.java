package es.us.isa.restest.sampleQueries;

import org.apache.commons.text.similarity.LevenshteinDistance;

import static es.us.isa.restest.sampleQueries.NLPUtils.posTagging;
import static es.us.isa.restest.sampleQueries.NLPUtils.splitCamelAndSnakeCase;


public class Main2 {

    public static void main(String[] args) {

        String description = "date in YYYYMMDD format that represents the 'version' of the API for which you expect from Foursquare.";
        String name = "v";

        System.out.println(posTagging(description, name));

    }

}
