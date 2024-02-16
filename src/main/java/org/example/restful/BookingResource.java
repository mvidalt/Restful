package org.example.restful;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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

    @Context
    private UriInfo uriInfo;



    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response getAllBookings(@Context HttpHeaders headers) {
        try {
            File xmlFile = new File("./resources/bookings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList bookingNodes = doc.getElementsByTagName("booking");

            bookings.clear();

            for (int i = 0; i < bookingNodes.getLength(); i++) {
                Node bookingNode = bookingNodes.item(i);
                if (bookingNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element bookingElement = (Element) bookingNode;

                    String locationNumberStr = bookingElement.getAttribute("location_number");
                    int locationNumber = Integer.parseInt(locationNumberStr);

                    String clientName = bookingElement.getElementsByTagName("client").item(0).getTextContent();
                    String agencyName = bookingElement.getElementsByTagName("agency").item(0).getTextContent();

                    Node priceNode = bookingElement.getElementsByTagName("price").item(0);
                    double price = 0.0;
                    if (priceNode != null) {
                        String priceStr = priceNode.getTextContent().replace(",", ".");
                        if (!priceStr.isEmpty()) {
                            price = Double.parseDouble(priceStr);
                        }
                    }

                    String roomType = bookingElement.getElementsByTagName("room").item(0).getTextContent();
                    String hotelName = bookingElement.getElementsByTagName("hotel").item(0).getTextContent();
                    String checkIn = bookingElement.getElementsByTagName("check_in").item(0).getTextContent();

                    int roomNights = 0;
                    Node roomNightsNode = bookingElement.getElementsByTagName("room_nights").item(0);
                    if (roomNightsNode != null) {
                        String roomNightsStr = roomNightsNode.getTextContent();
                        if (!roomNightsStr.isEmpty()) {
                            roomNights = Integer.parseInt(roomNightsStr);
                        }
                    }


                    // Crear un objeto Booking y agregarlo a la lista
                    Booking booking = new Booking(locationNumber, clientName, agencyName, price, roomType, hotelName, checkIn, roomNights);
                    bookings.add(booking);
                }
            }

            List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();
            MediaType mediaType = acceptableMediaTypes.isEmpty() ? MediaType.TEXT_PLAIN_TYPE : acceptableMediaTypes.get(0);

            StringBuilder table = new StringBuilder();
            table.append("<table border=\"1\">");
            table.append("<tr>");
            table.append("<th>Location Number</th>");
            table.append("<th>Client</th>");
            table.append("<th>Agency</th>");
            table.append("<th>Price</th>");
            table.append("<th>Room</th>");
            table.append("<th>Hotel</th>");
            table.append("<th>Check-In</th>");
            table.append("<th>Room Nights</th>");
            table.append("</tr>");

            for (Booking booking : bookings) {
                table.append("<tr>");
                table.append("<td style=\"text-align: center;\">").append(booking.getLocation_number()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getClient()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getAgency()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getPrice()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getRoom()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getHotel()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getCheckIn()).append("</td>");
                table.append("<td style=\"text-align: center;\">").append(booking.getRoomNights()).append("</td>");
                table.append("</tr>");
            }

            table.append("</table>");
            return Response.ok(table.toString(), mediaType).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{location_number}")
    @Produces(MediaType.TEXT_HTML)
    public Response getBookingByLocationNumber(@PathParam("location_number") int locationNumber) {
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
                    int currentLocationNumber = Integer.parseInt(locationNumberStr);

                    if (currentLocationNumber == locationNumber) {
                        String clientName = bookingElement.getElementsByTagName("client").item(0).getTextContent();
                        String agencyName = bookingElement.getElementsByTagName("agency").item(0).getTextContent();
                        double price = 0.0;
                        Node priceNode = bookingElement.getElementsByTagName("price").item(0);
                        if (priceNode != null) {
                            String priceStr = priceNode.getTextContent().replace(",", ".");
                            if (!priceStr.isEmpty()) {
                                price = Double.parseDouble(priceStr);
                            }
                        }
                        String roomType = bookingElement.getElementsByTagName("room").item(0).getTextContent();
                        String hotelName = bookingElement.getElementsByTagName("hotel").item(0).getTextContent();
                        String checkIn = bookingElement.getElementsByTagName("check_in").item(0).getTextContent();
                        int roomNights = Integer.parseInt(bookingElement.getElementsByTagName("room_nights").item(0).getTextContent());

                        // Crear una tabla HTML con la información de la reserva
                        StringBuilder table = new StringBuilder();
                        table.append("<table border=\"1\">");
                        table.append("<tr>");
                        table.append("<th>Location Number</th>");
                        table.append("<th>Client</th>");
                        table.append("<th>Agency</th>");
                        table.append("<th>Price</th>");
                        table.append("<th>Room</th>");
                        table.append("<th>Hotel</th>");
                        table.append("<th>Check-In</th>");
                        table.append("<th>Room Nights</th>");
                        table.append("</tr>");

                        table.append("<tr>");
                        table.append("<td style=\"text-align: center;\">").append(currentLocationNumber).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(clientName).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(agencyName).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(price).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(roomType).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(hotelName).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(checkIn).append("</td>");
                        table.append("<td style=\"text-align: center;\">").append(roomNights).append("</td>");
                        table.append("</tr>");

                        table.append("</table>");

                        return Response.ok(table.toString()).build();
                    }
                }
            }

            // Si no se encuentra la reserva con el locationNumber dado, devolver 404 (Not Found)
            return Response.status(Response.Status.NOT_FOUND).entity("Reserva " +  locationNumber + " no encontrada").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
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

            // Verificar si el ID ya está en uso
            String locationNumberStr = String.valueOf(booking.getLocation_number());
            NodeList existingBookings = doc.getElementsByTagName("booking");
            for (int i = 0; i < existingBookings.getLength(); i++) {
                Node node = existingBookings.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element existingBookingElement = (Element) node;
                    String existingLocationNumberStr = existingBookingElement.getAttribute("location_number");
                    if (existingLocationNumberStr.equals(locationNumberStr)) {
                        // Si el ID ya está en uso, devolver un mensaje de error
                        return Response.status(Response.Status.BAD_REQUEST).entity("ID repetido: " + locationNumberStr).build();
                    }
                }
            }

            // Si el ID no está repetido, continuar con la creación de la reserva

            // Crear el elemento booking
            Element bookingElement = doc.createElement("booking");

            // Añadir el atributo location_number
            bookingElement.setAttribute("location_number", locationNumberStr);

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

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (SAXException | IOException | TransformerException |
                 ParserConfigurationException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.CREATED).entity("Reserva añadida correctamente").build();
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

                        return Response.status(Response.Status.OK).entity("Reserva borrada correctamente").build();
                    }
                }
            }


            return Response.status(Response.Status.NOT_FOUND).entity("Reserva no encontrada: " + locationNumber).build();

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

                        return Response.status(Response.Status.OK).entity("Reserva editada correctamente").build();
                    }
                }
            }

            // Si no se encuentra el booking con el locationNumber dado, devolver 404 (Not Found)
            return Response.status(Response.Status.NOT_FOUND).entity("Reserva no encontrada: " + locationNumber).build();

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

        return (int) (Math.random() * 10);
    }


    }


