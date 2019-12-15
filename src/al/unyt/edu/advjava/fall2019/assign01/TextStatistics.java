/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al.unyt.edu.advjava.fall2019.assign01;

/**
 *
 * @author tedie
 */
public class TextStatistics {

    public static void main(String[] args) {

        if (args.length < 1 || args[0] == null || args[0].trim().equals("") || args[0].isEmpty()) {
            System.out.printf("%s", "Attention! The required local folder path is not passed as argument!");
            return;
        }

        String folderPath = args[0].trim();
        System.out.println("Got the local folder path argument:" + folderPath);
        Controller fileController = new Controller(folderPath);
        fileController.beginProcessing();
    }
}
