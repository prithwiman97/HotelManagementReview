package com.hotel.service;

import com.hotel.model.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public ByteArrayInputStream generateInvoice(Booking booking) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
            Paragraph title = new Paragraph("INVOICE", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // Row 1
            table.addCell("Booking ID");
            table.addCell(String.valueOf(booking.getId()));

            // Row 2
            table.addCell("Customer Name");
            table.addCell(booking.getUser().getName());

            // Row 3
            table.addCell("Room Number");
            table.addCell(booking.getRoom().getRoomNumber());

            // Row 4
            table.addCell("Check-In Date");
            table.addCell(booking.getCheckInDate().toString());

            // Row 5
            table.addCell("Check-Out Date");
            table.addCell(booking.getCheckOutDate().toString());

            // Row 6
            table.addCell("Total Amount");
            table.addCell(String.valueOf(booking.getTotalAmount()));

            document.add(table);
            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
