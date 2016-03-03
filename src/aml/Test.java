
package aml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import aml.match.Alignment;
import aml.match.Mapping;
import aml.ontology.Individual;
import aml.ontology.Lexicon;
import aml.ontology.Ontology;
import aml.ontology.Ontology2Match;
import aml.ontology.Relationship;
import aml.ontology.RelationshipMap;

public class Test
{/**
	* Implements algorithm to match instances in AML to be applied to Author Disambiguation Task (author­dis task) of OAEI 2015.
	* @authors  Suriya Sundararaj, Roshna Ramesh
	* @version 1.0
	* @since   2015-12-04
	*/
public static Ontology source;
public static Ontology target;
public static Lexicon sLex;
public static Lexicon tLex;
public static Map<Individual,String> sourceTitles=new HashMap<Individual,String>();
public static Map<Individual,String> targetTitles=new HashMap<Individual,String>();
public static Map<Individual,String> individualNames=new HashMap<Individual,String>();
public static Map<Individual,Set<Individual>> matchingPublication=new HashMap<Individual,Set<Individual>>(); 
public static Map<Individual,Set<Individual>> sourcePersons = new HashMap<Individual, Set<Individual>>();
public static Map<Individual,Set<String>> targetPersons = new HashMap<Individual, Set<String>>();
public static Map<String,Integer> sourceIndividuals=new HashMap<String, Integer>();
public static Map<String,Integer> targetIndividuals=new HashMap<String, Integer>();
public static Map<Individual,Set<Individual>> matchingNames = new HashMap<Individual, Set<Individual>>();

public static Map<Individual,Individual> alignments=new HashMap<Individual, Individual>();
public static Map<Set<Individual>,Integer> alignmentSet=new HashMap<Set<Individual>,Integer>();

public static Multimap<Individual, Individual> multiAlignmentMap = ArrayListMultimap.create();
//Main Method
	
	public static void main(String[] args) throws Exception
    {        //Path to input ontology files (edit manually)
		String sourcePath = "/Users/roshnaramesh/Documents/AgreementMakerLight/store/author disambiguation sandbox/ontoA.owl";
		String targetPath = "/Users/roshnaramesh/Documents/AgreementMakerLight/store/author disambiguation sandbox/ontoB.owl";
		//Path to reference alignment (for evaluation / repair)
		String referencePath = "/Users/roshnaramesh/Documents/AgreementMakerLight/store/author disambiguation sandbox/refalign.rdf";
        //Path to save output alignment (edit manually, or leave blank for no evaluation)
        String outputPath = "/Users/roshnaramesh/Documents/AgreementMakerLight/store/author disambiguation sandbox/output.rdf";
		
		AML aml = AML.getInstance();
		aml.openOntologies(sourcePath, targetPath);
	
		Ontology2Match ontObjA=new Ontology2Match(sourcePath);
		Ontology2Match ontObjB=new Ontology2Match(targetPath);
		
		RelationshipMap rm=aml.getRelationshipMap();
		
		Alignment a=new Alignment(true);
		//Objects for Ontology2Match
		Set<Integer> individuals=ontObjA.getIndividuals();
		Set<Integer> individualsB=ontObjB.getIndividuals();
		
		//Data Property & Object Property
		String titleA=null;
		String titleB=null;
		
		/*Get the title for the publications and pre-process the title
		 * Store it in map: sourceTitles and targetTitles
		 */
		for (int i : individuals) {
			Individual ind=ontObjA.getIndividual(i);
				if(ind.getDataValue(3)!=null);
					Set<String> title=ind.getDataValue(3);
					if(title != null)
					for(String s:title)
					{
						titleA=s;
						sourceTitles.put(ind, preprocess(titleA));
					}
			}

				for (int i : individualsB) {
					Individual ind=ontObjB.getIndividual(i);
						if(ind.getDataValue(3)!=null);
							Set<String> title=ind.getDataValue(3);
						if(title != null)
							for(String t:title)
							{
								titleB=t;
								
								targetTitles.put(ind,preprocess(titleB));
							}
				}
				
		
		 /* Finding the names of the Individuals of person class	
		  * Storing it in the map: individualNames	(for both the source and target ontologies)	
		  */
		 
				for(int i:individuals)
				{
					Individual indA=ontObjA.getIndividual(i);
					String nameA="";
					if(indA.getDataValue(9) != null)
					for(String s:indA.getDataValue(9))
						{
							String[] temp=s.split(" ");
							for(String t: temp)
							{
								if(t.length()!=1) nameA=nameA+t+" ";
							}
							nameA=nameA.trim();
						}
					individualNames.put(indA, nameA);
				}
					for(int j:individualsB)
					{
						Individual indB=ontObjB.getIndividual(j);
						String nameB="";
						if(indB.getDataValue(9) != null)
						for(String s:indB.getDataValue(9))
							{
								String[] temp=s.split(" ");
								for(String t: temp)
								{
									if(t.length()!=1) nameB=nameB+t+" ";
								}
								nameB=nameB.trim();
							}
						individualNames.put(indB, nameB);}

		//Matching the Publication class
		/*Obtain the individuals of the publication class and their corresponding titles
		 *Clean the title strings to get rid of HTML tags,special characters and multiple spaces
		 * */
				for(Map.Entry<Individual, String> entry:sourceTitles.entrySet()){
					Individual pubA=entry.getKey();
					Individual authorA=null;
					Set<Integer> authorASet=rm.getChildrenIndividuals(pubA.getIndex(), 14);//get the author names of each individual
					for(int auth:authorASet){ authorA=ontObjA.getIndividual(auth);}
					String srcTitle=entry.getValue();
					for(Map.Entry<Individual,String> entry2:targetTitles.entrySet()){
						Individual pubB=entry2.getKey();
						Individual authorB=null;
						Set<Integer> authorBSet=rm.getChildrenIndividuals(pubB.getIndex(), 14);
						for(int auth:authorBSet){ authorB=ontObjB.getIndividual(auth);}
						String targetTitle=entry2.getValue();
						if(srcTitle.equalsIgnoreCase(targetTitle))//get matched publications
						{
							if(individualNames.get(authorA).equals(individualNames.get(authorB)))//Match the authors of the mapped publications 
							{	
								if(!matchingPublication.containsKey(pubA))
								{
									Set<Individual> val=new HashSet<Individual>();
									val.add(pubB);
									matchingPublication.put(pubA, val);								
								}
								else
								{
									Set<Individual> val=matchingPublication.get(pubA);
									val.add(pubB);
									matchingPublication.put(pubA, val);
								}
							multiAlignmentMap.put(authorA, authorB);//add matched authors to a multimap
							}
						}
					}
				}
				/*
				 * To handle cases where each each publication in source ontology has multiple publications with same
				 * author name and title i.e a person in ontology A has multiple mappings with a persons in ontology B
				 */
				 for(Individual indA : multiAlignmentMap.keySet()) {
					 List<Individual> listOfIndB = (List<Individual>) multiAlignmentMap.get(indA);
					 float weight=0.0f; Individual finalIndB = null;
					 for(int i = 0; i < listOfIndB.size(); i++) {
						Individual tempInd=listOfIndB.get(i);
						int pubCountB=rm.getParentIndividuals(tempInd.getIndex(), 14).size();
						//For every author in ontology A, find the number of common publications for author in ontology B
						int freq=Collections.frequency(listOfIndB, tempInd);
						//Calculate weight for an alignment as ((No.of.Matching Publication)/TotalPublicationsofB) *100
						float totweight=((float)freq/(float)pubCountB)*100;
			            if(totweight>weight) {
			            	weight=totweight;
			            	finalIndB=tempInd;
			            }
			         }
					 if(finalIndB != null)
						 //assign similarity score of 1.0 to the alignment containing the matched author names
					 a.add(new Mapping(indA.getIndex(),finalIndB.getIndex(),1.0));
			        }
			
		aml.setAlignment(a);
		
		if(!referencePath.equals(""))
		{
			aml.openReferenceAlignment(referencePath);
			aml.getReferenceAlignment();
			aml.evaluate();
			System.out.println(aml.getEvaluation());
		}
		if(!outputPath.equals(""))
			aml.saveAlignmentRDF(outputPath);
				
    }
	
	public static String preprocess(String label)
	{
	/**
   * This is the method to pre-process a given string
   * @param publication title
   * @return pre-processed string
   */
		label=label.replaceAll("\\<[^>]*>", "");//remove HTML tags
		label=label.replaceAll("[^A-Za-z0-9]", " ");//remove special characters
		label=label.replaceAll("\\s+", " ");//remove multiple spaces
		return label.trim();
	}
}