package domainapp.modules.simple.dom.impl;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

public class PDF {

    public void crearPDFVacio(SimpleObject s){
        final PDPage singlePage = new PDPage();
        final PDFont courierBoldFont = PDType1Font.COURIER_BOLD;
        final int fontSize = 12;
        PDPageContentStream contentStream;
        ByteArrayOutputStream output = new ByteArrayOutputStream();


        try(final PDDocument document = new PDDocument()){
            document.addPage(singlePage);
            contentStream = new PDPageContentStream(document, singlePage);
            contentStream.beginText();
            contentStream.setFont(courierBoldFont, fontSize);
            contentStream.newLineAtOffset(150,750);
           // contentStream.showText("Nombre:" + s.getName);
            //contentStream.showText("Apellido Paterno:" + s.getApaterno);
           // contentStream.showText("Apellido Materno:" + s.getAmaterno);
            //contentStream.showText("Sexo:" + s.getsexo);
            //contentStream.showText("Fecha Nacimiento:" + s.getFechan);
            contentStream.endText();
            contentStream.close();



            final ByteArrayOutputStream target = new ByteArrayOutputStream();
            //final String name = "Pdf-" + s.getName() + ".pdf";
            final String mimeType = "application/pdf";
            //document.save(name);



        }catch (IOException ioEx){

        }
    }



  }
