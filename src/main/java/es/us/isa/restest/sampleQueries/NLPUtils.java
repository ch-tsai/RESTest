package es.us.isa.restest.sampleQueries;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class NLPUtils {

    private static String stopwordsPath = "src/main/java/es/us/isa/restest/sampleQueries/englishStopWords.txt";

    // TODO: Optional Comparator
    public static List<String> posTagging(String description){
        String res = description.toLowerCase().trim();
        Properties props = new Properties();

        props.setProperty("annotators","tokenize,ssplit,pos,lemma");

        //Build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        CoreDocument document = pipeline.processToCoreDocument(res);

        pipeline.annotate(document);

        List<String> stopWords = getStopWords();

        //        FW	Foreign word
        //        NN	Noun, singular or mass
        //        NNS	Noun, plural
        //        NNP	Proper noun, singular
        //        NNPS	Proper noun, plural
        List<String> names = document.tokens().stream()
                .filter(x -> (x.tag().equals("FW") || x.tag().equals("NN") ||
                        x.tag().equals("NNS") || x.tag().equals("NNP") || x.tag().equals("NNPS"))
                        &&  (!stopWords.contains(x.lemma())))
                .map(x -> x.lemma())
                .collect(Collectors.toList());


        return names;
    }

    // TODO: Implement
    // Use as a comparator
    public static Integer levenshteinDistance(String word1, String word2){
        return null;
    }

    private static List<String> getStopWords(){
        List<String> lines = Collections.emptyList();
        try{
            lines = Files.readAllLines(Paths.get(stopwordsPath), StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        return lines;
    }

}
