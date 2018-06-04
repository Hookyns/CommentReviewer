/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.jumbo.isict.reviewer;

import java.io.IOException;
import weka.core.converters.ConverterUtils;
import java.nio.file.InvalidPathException;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Roman
 */
public class CommentReviewerFiltered {
    
    /**
     * Cesta k trénovacím datům
     */
    private final String trainFilePath;
    
    /**
     * Trénovací dataset
     */
    private Instances trainData;
    
    /**
     * Klasifikátor
     */
    private Classifier classifier;

    /**
     * Ctor
     * @param trainFilePath
     * @throws Exception
     * @throws InvalidPathException
     * @throws IOException 
     */
    public CommentReviewerFiltered(String trainFilePath) throws Exception, InvalidPathException, IOException {
        this.trainFilePath = trainFilePath;
        this.init();
    }
    
    /**
     * Klasifikuje review
     * @param comment
     * @return
     * @throws Exception 
     */
    public int classify(String comment) throws Exception {
        Instance newInstance = new DenseInstance(2);
        newInstance.setDataset(this.trainData);
        //newInstance.setValue(0, 1);
        newInstance.setValue(1, newInstance.attribute(1).addStringValue(comment));
        newInstance.setClassValue(1);
        
        double result = this.classifier.classifyInstance(newInstance);
        return Integer.parseInt(this.trainData.classAttribute().value((int) result));
    }
    
    /**
     * Inicializace
     * @throws Exception
     * @throws InvalidPathException 
     */
    private void init() throws Exception, InvalidPathException {
        // Příprava trénovacích dat
        DataSource trainDdataSource = new ConverterUtils.DataSource(this.trainFilePath);
        this.trainData = trainDdataSource.getDataSet();
        //this.trainData = Filter.useFilter(this.trainData, this.getWordFilter());
        this.trainData.setClassIndex(0); // Třída je na nultém indexu
        
        // Vytvoření filtrovacího klasifikátoru
        this.classifier = new FilteredClassifier();
        
        // Nastavení filtru
        ((FilteredClassifier)this.classifier).setFilter(this.getWordFilter());
        
        // Nastavení klasifikátoru
        ((FilteredClassifier)this.classifier).setClassifier(new NaiveBayes());
        
        // Vytvoření modelu
        this.classifier.buildClassifier(this.trainData);
    }
    
    /**
     * Vytvoří filtr
     * @return
     * @throws Exception 
     */
    private StringToWordVector getWordFilter() throws Exception {
        StringToWordVector filter = new StringToWordVector();
        filter.setOptions(Utils.splitOptions("-R first-last -W 1000 -prune-rate -1.0 -N 0 -stemmer" + 
                " weka.core.stemmers.NullStemmer -stopwords-handler weka.core.stopwords.Null -M 1" + 
                " -tokenizer \"weka.core.tokenizers.WordTokenizer" + 
                " -delimiters \\\" \\\\r\\\\n\\\\t.,;:\\\\\\'\\\\\\\"()?!\\\"\""));
        
        filter.setInputFormat(this.trainData);
        return filter;
    }
}
