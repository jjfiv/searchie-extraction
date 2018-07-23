package edu.umass.cs.ciir.searchie;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.crf.CRFDatum;
import edu.stanford.nlp.ie.crf.CRFLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

/**
 * Example wrangling of Stanford CoreNLP API to dump out their NER CRF features on a per-term basis.
 * @author jfoley, jpjiang@cs.umass.edu
 */
public class ExtractSearchIEFeatures {
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    // Boot up Stanford CoreNLP
    Properties props = new Properties();
    props.put("annotators", "tokenize,ssplit,pos,lemma");
    // Skip time-tagging since that's rules and not CRF for a small speed boost.
    props.put("ner.useSUTime", "false");
    StanfordCoreNLP nlp = new StanfordCoreNLP(props);

    // Load up a known CRF-classifier from which we will extract features.
    CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(
        new GZIPInputStream(
            ExtractSearchIEFeatures.class.getResourceAsStream("/edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz")));
    classifier.flags.useTags = true;
    classifier.flags.useWordTag = true;

    String testSentence = "The quick brown fox jumped over the lazy dog.";
    Annotation nlpDoc = new Annotation(testSentence);

    // Slow, run NLP
    nlp.annotate(nlpDoc);

    // Extract CRF features:
    List<CoreLabel> coreLabels = nlpDoc.get(CoreAnnotations.TokensAnnotation.class);

    for (int i = 0; i < coreLabels.size(); i++) {
      CoreLabel coreLabel = coreLabels.get(i);
      CRFDatum<List<String>, CRFLabel> crfDatum = classifier.makeDatum(coreLabels, i, classifier.featureFactories);
      HashSet<String> mashedFeatures = new HashSet<>();
      for (List<String> features : crfDatum.asFeatures()) {
        mashedFeatures.addAll(features);
      }
      System.out.println("Term: " + coreLabel.getString(CoreAnnotations.TextAnnotation.class));
      System.out.println("Features: " + mashedFeatures);
      System.out.println("Compressed, Readable-Features: " + SNLPFeatures.parseAllFeatures(mashedFeatures));
    }

  }
}
