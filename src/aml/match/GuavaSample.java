package aml.match;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class GuavaSample {

    public static void main(String[] args) {
        Multimap<String, String> multiMap = ArrayListMultimap.create();
        multiMap.put("BE0004429", "DB00515");
        multiMap.put("BE0004429", "DB00515");
        multiMap.put("BE0004429", "DB00513");

        multiMap.put("BE0000059", "DB00603");
        multiMap.put("BE0000059", "DB01285");

        multiMap.put("BE0001052", "DB00366");
        multiMap.put("BE0001052", "DB00366");
        multiMap.put("BE0001052", "DB00856");
        multiMap.put("BE0001052", "DB01104");
        multiMap.put("BE0001052", "DB01104");

        for (String key : multiMap.keySet()) {
            List<String> list = (List<String>) multiMap.get(key);
            for (int i = 0; i < list.size(); i++) {
               
                    System.out.println(list.get(i) );
                   System.out.println("DB00515 has occured"+ Collections.frequency(list, "DB00515")+" times");
            }
            System.out.println();
        }
    }

}