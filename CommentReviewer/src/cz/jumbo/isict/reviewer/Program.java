/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.jumbo.isict.reviewer;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 *
 * @author Roman
 */
public class Program {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Kontrola parametrů
        if (args.length < 2) {
            System.out.println("ERROR: At least 2 input arguments required. First is path to train data set and second (and rest) is string for classification.");
        }
        
        try {
            // Určení absolutní normalizované cesty
            String trainFilePath = Paths.get(args[0]).toAbsolutePath().normalize().toString();
            
            // Vytvoření klasifikátoru recenzí
            CommentReviewerFiltered cr = new CommentReviewerFiltered(trainFilePath);
            
            StringBuilder sb = new StringBuilder();
            
            for (int i = 1; i < args.length; i++) {
                sb.append(cr.classify(args[i]));
                sb.append(",");
            }

            // Do output streamu vrátíme výsledek
            System.out.println(sb.toString());
        } catch (InvalidPathException ex) {
            System.out.println("ERROR: Invalid train data path.");
        } catch (Exception ex) {
            System.out.println("ERROR: Some error occurs.\n" + ex.getMessage() + "\n" + ex.getStackTrace());
        } 
    }
}
