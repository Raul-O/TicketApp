package org.example;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;

public class QRCodeReaderFromJPG {

    //public static void main(String args[]) { }

    public static String readQR(File file, String out) {
        PDDocument pd = null;
        try {
            pd = PDDocument.load (file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PDFRenderer pr = new PDFRenderer (pd);
        BufferedImage bi = null;
        try {
            bi = pr.renderImageWithDPI (0, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write (bi, "JPEG", new File (out + "tempimage.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BinaryBitmap binaryBitmap = null;
        try {
            binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(
                            ImageIO.read(new FileInputStream(out + "tempimage.jpg")))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Result qrCodeResult = null;
        try {
            qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        try {
            pd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return qrCodeResult.getText();

    }

}
