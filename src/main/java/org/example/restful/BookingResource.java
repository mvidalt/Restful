package org.example.restful;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/bookings")
public class BookingResource {
    private List<Booking> bookings;

    public BookingResource() {
        this.bookings = new ArrayList<>();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> getAllBookings() {

        try {
            File xmlFile = new File("./resources/bookings.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");

            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Node bookingNode = bookingNodes.item(i);
                if (bookingNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element bookingElement = (Element) bookingNode;

                    String locationNumberStr = bookingElement.getAttribute("location_number");
                    int location_number = 0;
                    if (!locationNumberStr.isEmpty()) {
                        location_number = Integer.parseInt(locationNumberStr);
                    } else {
                    }                    String clientName = bookingElement.getElementsByTagName("client").item(0).getTextContent();
                    String agencyName = bookingElement.getElementsByTagName("agency").item(0).getTextContent();
                    double price = Double.parseDouble(bookingElement.getElementsByTagName("price").item(0).getTextContent().replace(",", "."));
                    String roomType = bookingElement.getElementsByTagName("room").item(0).getTextContent();
                    String hotelName = bookingElement.getElementsByTagName("hotel").item(0).getTextContent();
                    String checkIn = bookingElement.getElementsByTagName("check_in").item(0).getTextContent();
                    int roomNights = Integer.parseInt(bookingElement.getElementsByTagName("room_nights").item(0).getTextContent());

                    Booking booking = new Booking(location_number, clientName, agencyName, price, roomType, hotelName, checkIn, roomNights);
                    bookings.add(booking);
                }
            }


            for (Booking booking : bookings) {
                System.out.println(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBooking(Booking booking) {

        // Añadir el booking al XML
        try {
            File xmlFile = new File("./resources/bookings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();

            // Crear el elemento booking
            Element bookingElement = doc.createElement("booking");
            rootElement.appendChild(bookingElement);

            // Añadir los elementos hijos
            Element clientElement = doc.createElement("client");
            clientElement.appendChild(doc.createTextNode(booking.getClient()));
            bookingElement.appendChild(clientElement);

            Element agencyElement = doc.createElement("agency");
            agencyElement.appendChild(doc.createTextNode(booking.getAgency()));
            bookingElement.appendChild(agencyElement);

            // Añadir más elementos según sea necesario

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (SAXException | IOException | TransformerException |
                 ParserConfigurationException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.CREATED).build();
    }
}

