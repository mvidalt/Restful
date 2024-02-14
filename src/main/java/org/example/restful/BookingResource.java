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
import javax.xml.transform.*;
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
                    }
                    Node clientNode = bookingElement.getElementsByTagName("client").item(0);
                    String clientName = (clientNode != null) ? clientNode.getTextContent() : "";
                    String agencyName = bookingElement.getElementsByTagName("agency").item(0).getTextContent();
                    Node priceNode = bookingElement.getElementsByTagName("price").item(0);
                    double price = 0.0;
                    if (priceNode != null) {
                        String priceStr = priceNode.getTextContent().replace(",", ".");
                        if (!priceStr.isEmpty()) {
                            price = Double.parseDouble(priceStr);
                        }
                    }

                    Node roomNode = bookingElement.getElementsByTagName("room").item(0);
                    String roomType = (roomNode != null) ? roomNode.getTextContent() : "";

                    Node hotelNode = bookingElement.getElementsByTagName("hotel").item(0);
                    String hotelName = (hotelNode != null) ? hotelNode.getTextContent() : "";

                    Node checkInNode = bookingElement.getElementsByTagName("check_in").item(0);
                    String checkIn = (checkInNode != null) ? checkInNode.getTextContent() : "";

                    Node roomNightsNode = bookingElement.getElementsByTagName("room_nights").item(0);
                    int roomNights = 0;
                    if (roomNightsNode != null) {
                        String roomNightsStr = roomNightsNode.getTextContent();
                        if (!roomNightsStr.isEmpty()) {
                            roomNights = Integer.parseInt(roomNightsStr);
                        }
                    }


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
        try {
            File xmlFile = new File("./resources/bookings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();

            // Crear el elemento booking
            Element bookingElement = doc.createElement("booking");

            // Añadir el atributo location_number
            bookingElement.setAttribute("location_number", String.valueOf(booking.getLocation_number()));

            // Generar IDs inventados
            int clientId = generateId("client");
            int agencyId = generateId("agency");
            int roomId = generateId("room");
            int hotelId = generateId("hotel");

            // Añadir los elementos hijos con los IDs inventados
            addIndentedElement(doc, bookingElement, "client", "id_client", String.valueOf(clientId), booking.getClient());
            addIndentedElement(doc, bookingElement, "agency", "id_agency", String.valueOf(agencyId), booking.getAgency());
            addIndentedElement(doc, bookingElement, "price", null, null, String.valueOf(booking.getPrice()));
            addIndentedElement(doc, bookingElement, "room", "id_type", String.valueOf(roomId), booking.getRoom());
            addIndentedElement(doc, bookingElement, "hotel", "id_hotel", String.valueOf(hotelId), booking.getHotel());
            addIndentedElement(doc, bookingElement, "check_in", null, null, booking.getCheckIn());
            addIndentedElement(doc, bookingElement, "room_nights", null, null, String.valueOf(booking.getRoomNights()));

            // Agregar el nuevo elemento booking al final del archivo XML
            rootElement.appendChild(bookingElement);

            // Guardar los cambios en el archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Configurar la salida para que esté bien indentada
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // Espacios de indentación

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

    @DELETE
    @Path("/{locationNumber}")
    public Response deleteBooking(@PathParam("locationNumber") int locationNumber) {
        try {
            File xmlFile = new File("./resources/bookings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");

            // Iterar sobre los nodos de booking para encontrar el que coincida con el locationNumber
            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Node bookingNode = bookingNodes.item(i);
                if (bookingNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element bookingElement = (Element) bookingNode;
                    String locationNumberStr = bookingElement.getAttribute("location_number");
                    int currentLocationNumber = Integer.parseInt(locationNumberStr);

                    // Si el locationNumber coincide, eliminar el nodo booking
                    if (currentLocationNumber == locationNumber) {
                        bookingNode.getParentNode().removeChild(bookingNode);

                        // Guardar los cambios en el archivo XML
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(xmlFile);
                        transformer.transform(source, result);

                        return Response.status(Response.Status.OK).build();
                    }
                }
            }

            // Si no se encuentra el booking con el locationNumber dado, devolver 404 (Not Found)
            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/{locationNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBooking(@PathParam("locationNumber") int locationNumber, Booking updatedBooking) {
        try {
            File xmlFile = new File("./resources/bookings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");

            // Iterar sobre los nodos de booking para encontrar el que coincida con el locationNumber
            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Node bookingNode = bookingNodes.item(i);
                if (bookingNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element bookingElement = (Element) bookingNode;
                    String locationNumberStr = bookingElement.getAttribute("location_number");
                    int currentLocationNumber = Integer.parseInt(locationNumberStr);

                    // Si el locationNumber coincide, actualizar el nodo booking con los nuevos datos
                    if (currentLocationNumber == locationNumber) {
                        // Actualizar los datos del nodo booking con los nuevos valores
                        bookingElement.getElementsByTagName("client").item(0).setTextContent(updatedBooking.getClient());
                        bookingElement.getElementsByTagName("agency").item(0).setTextContent(updatedBooking.getAgency());
                        bookingElement.getElementsByTagName("price").item(0).setTextContent(String.valueOf(updatedBooking.getPrice()));
                        bookingElement.getElementsByTagName("room").item(0).setTextContent(updatedBooking.getRoom());
                        bookingElement.getElementsByTagName("hotel").item(0).setTextContent(updatedBooking.getHotel());
                        bookingElement.getElementsByTagName("check_in").item(0).setTextContent(updatedBooking.getCheckIn());
                        bookingElement.getElementsByTagName("room_nights").item(0).setTextContent(String.valueOf(updatedBooking.getRoomNights()));

                        // Guardar los cambios en el archivo XML
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(xmlFile);
                        transformer.transform(source, result);

                        return Response.status(Response.Status.OK).build();
                    }
                }
            }

            // Si no se encuentra el booking con el locationNumber dado, devolver 404 (Not Found)
            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void addIndentedElement(Document doc, Element parent, String tagName, String attributeName, String attributeValue, String textContent) {
        Element element = doc.createElement(tagName);
        if (attributeName != null && attributeValue != null) {
            element.setAttribute(attributeName, attributeValue);
        }
        if (textContent != null) {
            element.appendChild(doc.createTextNode(textContent));
        }
        parent.appendChild(element);
    }

    private int generateId(String elementType) {
        // Implementa aquí la lógica para generar IDs inventados
        // Por ejemplo, puedes usar un contador que se incrementa cada vez que se llama a esta función
        // o puedes usar algún otro método que te parezca conveniente
        // Aquí se devuelve un valor ficticio, reemplázalo con tu lógica real
        return (int) (Math.random() * 1000);
    }


    }


