package com.example.barcodereader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BarCodeGen {

    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return null;
            }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            System.out.println("loadFromFile=" + e);
            return null;
        }
    }

    public static void createPDF(File pdfFilename) {

        System.out.println("dddddddddd");
        System.out.println("pdfFilename=" + pdfFilename.toString());
        try {

            OutputStream file = new FileOutputStream(pdfFilename);
            Document document = new Document();

            Rectangle A4 = new Rectangle(580, 900);
            document.setPageSize(A4);
            PdfWriter.getInstance(document, file);

/*            // Assume block needs to be inside a Try/Catch block.
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File files = new File(extStorageDirectory, "barcodeimage.jpg");
            Bitmap bmp = loadFromFile(files.toString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(72f, 32f);//image width,height*/

            /*
            //Inserting Image in PDF
            Drawable d = App.GetAppContext().getResources().getDrawable(R.drawable.logo);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleAbsolute(72f, 72f);//image width,height
*/

            PdfPTable billTable = new PdfPTable(6); //one page contains 15 records
            billTable.setWidthPercentage(100);

            Long id = System.currentTimeMillis() / 100L;

            billTable.setWidths(new float[]{2, 2, 2, 2, 2, 2});
/*            billTable.setSpacingBefore(30.0f);
            billTable.setSpacingAfter(30.0f);*/
            int culumn = 0;
            for (int i = 0; i <= 82; i++) {
                culumn++;
                id++;
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(id + "", BarcodeFormat.CODABAR, 72, 32);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bmp = barcodeEncoder.createBitmap(bitMatrix);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleAbsolute(72f, 32f);//image width,height

                PdfPCell cell = new PdfPCell(image);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(1.0f);
                cell.setPaddingTop(8.0f);
                cell.setBorder(1);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthBottom(0);
                billTable.addCell(cell);

                if (culumn == 6) {
                    id = id - 6;
                    for (int j = 1; j <= 6; j++) {
                        id++;
                        FontSelector fs = new FontSelector();
                        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
                        fs.addFont(font);
                        Phrase phrase = fs.process(id + "");
                        PdfPCell idcell = new PdfPCell(phrase);
                        idcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        idcell.setPaddingBottom(8.0f);
                        idcell.setPaddingTop(0f);
                        idcell.setBorder(0);
                        idcell.setBorderWidthLeft(1);
                        idcell.setBorderWidthRight(1);
                        idcell.setBorderWidthBottom(1);
                        billTable.addCell(idcell);
                    }
                    culumn = 0;
                }

            }


            document.open();//PDF document opened........

/*            PdfPCell cell = new PdfPCell(billTable);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5.0f);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);*/
            document.add(billTable);


            document.close();

            file.close();

            System.out.println("Pdf created successfully..");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception=" + e);
        }
    }

    public static void setHeader() {

    }


    public static PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    public static PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        return cell;
    }

    public static PdfPCell getBillRowCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getBillFooterCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(0);
        return cell;
    }

    public static PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding(5.0f);
        return cell;
    }

    public static PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    public static PdfPCell getdescCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(0);
        return cell;
    }

}
