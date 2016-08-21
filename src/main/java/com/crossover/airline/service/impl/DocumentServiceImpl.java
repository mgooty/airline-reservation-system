package com.crossover.airline.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crossover.airline.entity.Booking;
import com.crossover.airline.entity.Flight;
import com.crossover.airline.repository.BookingRepository;
import com.crossover.airline.repository.FlightRepository;
import com.crossover.airline.service.DocumentService;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class DocumentServiceImpl implements DocumentService {

	@Value("${generated.document.folder}")
	private String generatedDocsFolder;
	
	@Value("${api.base.path:http://localhost:8080}")
	private String apiBasePath;
	
	private final String BASEPATH = "/v1/flight/booking";
	private final String CHECKIN_PATH = "/checkin";
	private final String MYBOOKINGS_PATH = "/my";
	private final String CANCEL_BOOKING_PATH = "/cancel";
	
	@Autowired
	private FlightRepository flightRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Override
	public String generateTicketAsPdf(Long bookingId, Long paymentTxnId, Long flightId) throws Exception {
		Flight flight = flightRepository.findOne(flightId);
		Booking booking = bookingRepository.findOne(bookingId);
		
		Document document = new Document();
		File file = new File(generatedDocsFolder + '/' + booking.getId() + ".pdf");
		file.getParentFile().mkdirs();
		file.createNewFile();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		
		List orderedList = new List(List.ORDERED);
		orderedList.add(new ListItem("Booking ID - " + bookingId));
		orderedList.add(new ListItem("From - " + flight.getFromCity()));
		orderedList.add(new ListItem("To - " + flight.getToCity()));
		orderedList.add(new ListItem("Flight Code - " + flight.getFlightCode()));
		orderedList.add(new ListItem("Flight Class - " + flight.getFlightClass()));
		
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		orderedList.add(new ListItem("Departure Date - " + dateFormat.format(flight.getDepartureDate())));
		
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		orderedList.add(new ListItem("Departure Time - " + timeFormat.format(flight.getDepartureDate())));
		
		orderedList.add(new ListItem("Number of seats - " + booking.getNumOfSeats()));
		document.add(orderedList);
		
		Paragraph paragraph1 = new Paragraph();
		paragraph1.setSpacingBefore(10);
		paragraph1.setSpacingAfter(10);
		paragraph1.add(getMyBookingsLink(""));
		
		Paragraph paragraph2 = new Paragraph();
		paragraph2.setSpacingBefore(10);
		paragraph2.setSpacingAfter(10);
		paragraph2.add(getCheckinLink(bookingId));
		
		Paragraph paragraph3 = new Paragraph();
		paragraph3.setSpacingBefore(10);
		paragraph3.setSpacingAfter(10);
		paragraph3.add(getCancelBookingLink(bookingId));
		
		document.add(paragraph1);
		document.add(paragraph2);
		document.add(paragraph3);
		
		document.close();
		writer.close();
		
		return file.getAbsolutePath();
	}
	
	private Anchor getMyBookingsLink(String email) {
		Font font = new Font();
		font.setColor(BaseColor.BLUE);
		Anchor anchor = new Anchor("My Bookings", font);
		anchor.setReference(new StringBuffer().append(apiBasePath).append(BASEPATH).append(MYBOOKINGS_PATH).toString());
		return anchor;
	}

	private Anchor getCheckinLink(Long bookingId) {
		Font font = new Font();
		font.setColor(BaseColor.BLUE);
		Anchor anchor = new Anchor("Check-in now", font);
		anchor.setReference(new StringBuffer().append(apiBasePath).append(BASEPATH).append(bookingId).append('/').append(CHECKIN_PATH).toString());
		return anchor;
	}
	
	private Anchor getCancelBookingLink(Long bookingId) {
		Font font = new Font();
		font.setColor(BaseColor.BLUE);
		Anchor anchor = new Anchor("Cancel Booking", font);
		anchor.setReference(new StringBuffer().append(apiBasePath).append(BASEPATH).append(bookingId).append('/').append(CANCEL_BOOKING_PATH).toString());
		return anchor;
	}

	@Override
	public String generateTicketAsPdf(Long onwardBookingId, Long returnBookingId, Long paymentTxnId, Long onwardFlightId,
			Long returnFlightId)  throws Exception {
		Flight onwardflight = flightRepository.findOne(onwardBookingId);
		Booking onwardBooking = bookingRepository.findOne(onwardFlightId);
		
		Flight returnFlight = flightRepository.findOne(returnBookingId);
		Booking returnBooking = bookingRepository.findOne(returnFlightId);
		
		Document document = new Document();
		File file = new File(generatedDocsFolder + '/' + onwardBooking.getId() + ".pdf");
		file.getParentFile().mkdirs();
		file.createNewFile();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		
		Paragraph onwardJourneyParagraph = new Paragraph("Onward Journey");
		List onwardOrderedList = new List(List.ORDERED);
		onwardOrderedList.add(new ListItem("Booking ID - " + onwardBookingId));
		onwardOrderedList.add(new ListItem("From - " + onwardflight.getFromCity()));
		onwardOrderedList.add(new ListItem("To - " + onwardflight.getToCity()));
		onwardOrderedList.add(new ListItem("Flight Code - " + onwardflight.getFlightCode()));
		onwardOrderedList.add(new ListItem("Flight Class - " + onwardflight.getFlightClass()));
		
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		onwardOrderedList.add(new ListItem("Departure Date - " + dateFormat.format(onwardflight.getDepartureDate())));
		
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		onwardOrderedList.add(new ListItem("Departure Time - " + timeFormat.format(onwardflight.getDepartureDate())));
		
		Paragraph paragraph1 = new Paragraph();
		paragraph1.setSpacingBefore(10);
		paragraph1.setSpacingAfter(10);
		paragraph1.add(getMyBookingsLink(""));
		
		Paragraph paragraph2 = new Paragraph();
		paragraph2.setSpacingBefore(10);
		paragraph2.setSpacingAfter(10);
		paragraph2.add(getCheckinLink(onwardBookingId));
		
		Paragraph paragraph3 = new Paragraph();
		paragraph3.setSpacingBefore(10);
		paragraph3.setSpacingAfter(10);
		paragraph3.add(getCancelBookingLink(onwardBookingId));
		
		document.add(onwardJourneyParagraph);
		document.add(onwardOrderedList);
		document.add(paragraph1);
		document.add(paragraph2);
		document.add(paragraph3);
		
		Paragraph returnJourneyParagraph = new Paragraph("Onward Journey");
		List returnOrderedList = new List(List.ORDERED);
		returnOrderedList.add(new ListItem("Booking ID - " + returnBookingId));
		returnOrderedList.add(new ListItem("From - " + returnFlight.getFromCity()));
		returnOrderedList.add(new ListItem("To - " + returnFlight.getToCity()));
		returnOrderedList.add(new ListItem("Flight Code - " + returnFlight.getFlightCode()));
		returnOrderedList.add(new ListItem("Flight Class - " + returnFlight.getFlightClass()));
		
		returnOrderedList.add(new ListItem("Departure Date - " + dateFormat.format(returnFlight.getDepartureDate())));
		returnOrderedList.add(new ListItem("Departure Time - " + timeFormat.format(returnFlight.getDepartureDate())));
		
		Paragraph paragraph4 = new Paragraph();
		paragraph1.setSpacingBefore(10);
		paragraph1.setSpacingAfter(10);
		paragraph1.add(getMyBookingsLink(""));
		
		Paragraph paragraph5 = new Paragraph();
		paragraph2.setSpacingBefore(10);
		paragraph2.setSpacingAfter(10);
		paragraph2.add(getCheckinLink(returnBookingId));
		
		Paragraph paragraph6 = new Paragraph();
		paragraph3.setSpacingBefore(10);
		paragraph3.setSpacingAfter(10);
		paragraph3.add(getCancelBookingLink(returnBookingId));
		
		document.add(returnJourneyParagraph);
		document.add(returnOrderedList);
		document.add(paragraph4);
		document.add(paragraph5);
		document.add(paragraph6);
		
		document.close();
		writer.close();
		
		return file.getAbsolutePath();
	}
}
