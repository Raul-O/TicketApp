package org.example;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import com.itextpdf.text.Image;

public class CreatePDF {
//    public static void main(String[] args) throws IOException, NotFoundException {
//
//        List list = new ArrayList();
//        list.add("aa");
//        list.add("aa");
//        list.add("aa");
//        createPDF(list,"Test", "1235");
//        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
//        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//
//        //QRCode.readQR("\\\\TRD57L5Z43\\TicketApp\\Fise\\Fisa Incident 1235.pdf", "\\\\TRD57L5Z43\\TicketApp\\test.jpg");
//    }
    public static void createPDF(List<String> info, String obs, String ticketID) {
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

        PdfPCell cell;
        Document document = new Document();
        try
        {

            System.out.println(App.setProperties().getProperty("pdfPath") + "Fisa Incident " + ticketID + ".pdf");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(App.setProperties().getProperty("pdfPath") + "Fisa Incident " + ticketID + ".pdf"));
            document.open();
            Paragraph title = new Paragraph("Fisa Incident");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);


            //Convertire ID tichet in Imagine png din BufferedImage

            BufferedImage qrImage = QRCode.createQR(ticketID, "UTF-8", 150, 150);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            Image iTextImage = Image.getInstance(baos.toByteArray());



            PdfPTable table = new PdfPTable(info.size()/2); // 3 columns.
            table.setWidthPercentage(90); //Width 100%
            table.setSpacingBefore(10f); //Space before table
            table.setSpacingAfter(10f); //Space after table




            for(String item:info){
                PdfPCell cell1 = new PdfPCell(new Paragraph(item));
                cell1.setBorderColor(BaseColor.GRAY);
                cell1.setPaddingLeft(5);
                cell1.setBorderWidth(1);
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell1);

            }

            PdfPTable tableObs = new PdfPTable(1);
            tableObs.setWidthPercentage(90); //Width 100%
            tableObs.setSpacingBefore(10f); //Space before table
            tableObs.setSpacingAfter(10f); //Space after table
            //Set Column widths
//            float[] columnWidths = {1f, 4f};
//            table.setWidths(columnWidths);

            cell = new PdfPCell(new Paragraph("Observatii"));
            cell.setPaddingLeft(5);
            cell.setBorderWidth(1);
            //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableObs.addCell(cell);
            PdfPCell obsCell = new PdfPCell(new Phrase(obs));
            obsCell.setBorderWidth(1);
            tableObs.addCell(obsCell);
            tableObs.setWidthPercentage(90); //Width 100%
            tableObs.setSpacingBefore(10f); //Space before table
            tableObs.setSpacingAfter(10f); //Space after table


            PdfPTable tableObsMentenanta = new PdfPTable(1);
            PdfPCell obsCellMent = new PdfPCell();
            obsCellMent.setFixedHeight(300);
            tableObsMentenanta.addCell("Observatii Mentenanta");
            tableObsMentenanta.addCell(obsCellMent);
            tableObsMentenanta.setWidthPercentage(90); //Width 100%
            tableObsMentenanta.setSpacingBefore(10f); //Space before table
            tableObsMentenanta.setSpacingAfter(10f);



            document.add(table);
            document.add(tableObs);
            document.add(tableObsMentenanta);
            document.add(iTextImage);
            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}

