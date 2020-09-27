package es.us.isa.restest.sampleQueries;

import org.apache.commons.text.similarity.LevenshteinDistance;

import static es.us.isa.restest.sampleQueries.NLPUtils.posTagging;


public class Main2 {

    public static void main(String[] args) {

        String description = "todoJuntoSinSentido Title of movie or ti series Title";


        System.out.println(posTagging(description));




    }

}
