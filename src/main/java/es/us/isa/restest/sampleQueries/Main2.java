package es.us.isa.restest.sampleQueries;

import org.apache.commons.text.similarity.LevenshteinDistance;

import static es.us.isa.restest.sampleQueries.NLPUtils.posTagging;


public class Main2 {

    public static void main(String[] args) {

        LevenshteinDistance leven = new LevenshteinDistance();
        Integer distance = leven.apply("title", "tit");
        System.out.println(distance);




    }

}
