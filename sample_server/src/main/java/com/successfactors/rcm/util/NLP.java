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
        synonym.put("position", "job");
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
        System.out.println(nlp.getKey("i want to find job"));

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
        System.out.println(rootTag);
        if(rootTag.equals("NN") ){
            String root = synonym.get(dependencyParse.getFirstRoot().word());
            System.out.println(root);
            String original = dependencyParse.getFirstRoot().word();
            if(root!=null)
              sb.append(root);
            else
              sb.append(original);
            for(int i=0; i<posTags.size();i++){
                 if(posTags.get(i).equals("NN")){
                     CoreLabel token = document.tokens().get(i);
                     if(!token.word().equals(original)){
                         String word=synonym.get(token.word()) != null ? synonym.get(token.word()) : token.word();
                         sb.append("_");
                         sb.append(word);
                     }
                 }
            }
            return sb.toString();
        }
        for(int i=0; i<posTags.size();i++){
            CoreLabel token = document.tokens().get(i);
            String word=synonym.get(token.word()) != null ? synonym.get(token.word()) : token.word();
            if(posTags.get(i).equals("VB") || posTags.get(i).equals("VBG") ||posTags.get(i).equals("VBN")){
                sb.append(word);
            }
            if(posTags.get(i).equals("NN")){
                sb.append("_");
                sb.append(word);
            }
        }

        return sb.toString();
    }

}
