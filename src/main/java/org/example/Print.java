package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;

import javax.print.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Print {

    public static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            System.out.println(printService.getName().equals(printerName));

            if (printService.getName().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    public static boolean print(String ticketID) {

//        Properties prop=null;
//        try (
//                InputStream input = new FileInputStream("\\\\TRD57L5Z43\\TicketApp\\config.properties")) {
//
//            prop = new Properties();
//            prop.load(input);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        if(findPrintService(App.setProperties().getProperty("printerName")) == null){
            return false;
        }else {
            PrintService myPrintService = findPrintService(App.setProperties().getProperty("printerName"));

            PDDocument pdDocument = null;
            try {
                System.out.println(App.setProperties().getProperty("pdfPath") + "Fisa Incident " + ticketID + ".pdf");
                pdDocument = PDDocument.load(new File(App.setProperties().getProperty("pdfPath") + "Fisa Incident " + ticketID + ".pdf"));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            PDFPrintable pdfPageable = new PDFPrintable(pdDocument);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(pdDocument));
            try {
                job.setPrintService(myPrintService);
            } catch (PrinterException e) {
                e.printStackTrace();
                return false;
            }
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
                return false;
            }
            try {
                pdDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

    }
}
