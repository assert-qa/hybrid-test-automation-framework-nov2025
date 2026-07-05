package api.assertions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class BookingAssertions {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final File GET_BOOKING_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/bookings/get-booking-response-200.schema.json");

    private static final File GET_BOOKING_RESPONSE_500_SCHEMA =
            new File("src/test/java/api/schemas/bookings/get-booking-response-500.schema.json");

    private static final File CREATE_BOOKING_REQUEST_SCHEMA =
            new File("src/test/java/api/schemas/bookings/create-booking-request.schema.json");

    private static final File CREATE_BOOKING_RESPONSE_201_SCHEMA =
            new File("src/test/java/api/schemas/bookings/create-booking-response-201.schema.json");

    private static final File CREATE_BOOKING_RESPONSE_400_SCHEMA =
            new File("src/test/java/api/schemas/bookings/create-booking-response-400.schema.json");

    private static final File CREATE_BOOKING_RESPONSE_404_SCHEMA =
            new File("src/test/java/api/schemas/bookings/create-booking-response-404.schema.json");

    private static final File CREATE_BOOKING_RESPONSE_500_SCHEMA =
            new File("src/test/java/api/schemas/bookings/create-booking-response-500.schema.json");

    private static final File GET_BOOKING_REF_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/bookings/reference-booking-response-200.schema.json");

    private static final File GET_BOOKING_REF_RESPONSE_404_SCHEMA =
            new File("src/test/java/api/schemas/bookings/reference-booking-response-404.schema.json");

    private static final File GET_BOOKING_REF_RESPONSE_500_SCHEMA =
            new File("src/test/java/api/schemas/bookings/reference-booking-response-500.schema.json");

    private static final File GET_BOOKING_ID_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/bookings/get-booking-id-response-200.schema.json");

    private static final File GET_BOOKING_ID_RESPONSE_404_SCHEMA =
            new File("src/test/java/api/schemas/bookings/get-booking-id-response-404.schema.json");
}
