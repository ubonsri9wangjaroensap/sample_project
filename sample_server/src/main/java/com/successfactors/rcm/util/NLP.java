package com.successfactors.rcm.util;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NLP{
    private static Map<String, String> synonym;
    static
    {
        synonym = new HashMap<>();
        synonym.put("looking", "search");
        synonym.put("look", "search");
        synonym.put("find", "search");
        synonym.put("finding", "search");
        synonym.put("search", "search");
        synonym.put("searching", "search");
        synonym.put("job", "job");
        synonym.put("done", "done");
        synonym.put("finish", "done");
    }
    public static void main(String[] args) {
        NLP nlp = new NLP();
        //i want to find job
        //i am looking for job
        //job search
        //look for job
        //looking for job
        //all map to search_job
        //done and finish all map to done
        System.out.println(nlp.getKey("finding job"));
    }
   
    public String getKey(String message){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse");
        props.setProperty("coref.algorithm", "neural");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(message);
        pipeline.annotate(document);
        CoreSentence sentence = document.sentences().get(0);
        List<String> posTags = sentence.posTags();
        SemanticGraph dependencyParse = sentence.dependencyParse();
        StringBuilder sb = new StringBuilder();
        String rootTag=dependencyParse.getFirstRoot().tag();
        if(rootTag.equals("NN") ){
            String root = synonym.get(dependencyParse.getFirstRoot().word());
            sb.append(root);
            for(int i=0; i<posTags.size();i++){
                 if(posTags.get(i).equals("NN")){
                     CoreLabel token = document.tokens().get(i);
                     if(!token.word().equals(dependencyParse.getFirstRoot().word())){
                         sb.append("_");
                         sb.append(token.word());
                     }
                 }
            }
            return sb.toString();
        }
        for(int i=0; i<posTags.size();i++){
            if(posTags.get(i).equals("VB") || posTags.get(i).equals("VBG")){
                CoreLabel token = document.tokens().get(i);
                sb.append(synonym.get(token.word()));
            }
            if(posTags.get(i).equals("NN")){
                CoreLabel token = document.tokens().get(i);
                sb.append("_");
                sb.append(token.word());
            }
        }

        return sb.toString();
    }

}
