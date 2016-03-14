# AgreementMakerLight

## Project Description
Given a source and target ontology and/or a set of expected mappings, the challenge is to map the instances in the source dataset to the instances in the target dataset for the five different project tasks described above

The 2 tasks given were:

    Task 1: Match all the publications
    
    Task 2: Match persons based on the result of task 1

## Building with Maven

Use the following command to build an über jar:

    mvn install

## Running AML Swing UI

After building the über jar, type the following:

    java -jar target/aml-*-SNAPSHOT.jar

## Results

     Precision     Recall      F-Score
       93.7%        93.4%       93.6%

-------------------------------------------------------

    Correct Mappings    Found Mappings          Total
          852               798                  854
