package com.successfactors.rcm;

import com.successfactors.rcm.util.NLP;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import java.util.*;
import java.io.IOException; 
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "http://localhost:3000")
@SpringBootApplication
public class Application {

    @RequestMapping("/welcome")
    String home() {
        testConvertToJson();
        return "Welcome from server!";
    }

    @Bean
    public Jedis jedis() {
        Jedis jedis = new Jedis("redis-server");
//        jedis.flushAll();
        System.out.println("Connection to server sucessfully");
        //check whether server is running or not
        System.out.println("Server is running: "+jedis.ping());
        //jedis.set("tutorial-name", "Redocker-dis tutorial");
        return jedis;
    }

    @Bean
    public NLP nlp() {
        return new NLP();
    }

    @PostMapping("/training")
    TrainingModel createTraining(@RequestBody TrainingModel training){
         return getTrainingModel();
    }

    private TrainingModel getTrainingModel(){
        List<String> keys = new ArrayList<String>();
       keys.add("test");
       keys.add("happy");
       TrainingModel training = new TrainingModel();
       training.setMessage("messsage happy");
       training.setKeys(keys);
       return training;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void testRedis(){
         Jedis jedis = new Jedis("redis-server");
      System.out.println("Connection to server sucessfully");
      //check whether server is running or not
      System.out.println("Server is running: "+jedis.ping());
      //jedis.set("tutorial-name", "Redis tutorial");

    }

    public void testNLP(){
        String text = "I am feeling very sad and frustrated.";
      Properties props = new Properties();
      props.setProperty("annotators", "tokenize,ssplit,pos");
      props.setProperty("coref.algorithm", "neural");
      System.out.println("before create pipeline");
      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
      System.out.println("before create document");
      CoreDocument document = new CoreDocument(text);
      System.out.println("before annotate");
      pipeline.annotate(document);
      CoreLabel token = document.tokens().get(0);
      System.out.println("Example: token");
      System.out.println(token);
      // text of the first sentence
    String sentenceText = document.sentences().get(0).text();
    System.out.println("Example: sentence");
    System.out.println(sentenceText);
    System.out.println();
    CoreSentence sentence = document.sentences().get(0);

    // list of the part-of-speech tags for the second sentence
    List<String> posTags = sentence.posTags();
    System.out.println("Example: pos tags");
    System.out.println(posTags);
    System.out.println();

    }

    public void testConvertToJson(){

       ObjectMapper Obj = new ObjectMapper(); 
       List<String> keys = new ArrayList<String>();
       keys.add("test");
       keys.add("happy");
       TrainingModel training = new TrainingModel();
       training.setMessage("messsage happy");
       training.setKeys(keys);
        try { 
  
            // get Oraganisation object as a json string 
            String jsonStr = Obj.writeValueAsString(training); 
  
            // Displaying JSON String 
            System.out.println(jsonStr); 
        } 
  
        catch (IOException e) { 
            e.printStackTrace(); 
        } 

   
   }

}

class TrainingModel{
  String message;
  List<String> keys;

  public void setMessage(String message){
     this.message=message;
 }
  public String getMessage(){
    return message;
 }
 public void setKeys(List<String> keys){
    this.keys=keys;
 }
 public List<String> getKeys(){
    return keys;
 }
}

