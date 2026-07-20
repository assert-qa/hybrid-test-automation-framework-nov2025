package factory;

import helpers.DataFakerHelper;
import pages.dto.EventBookDetailDataObject;

public class BookingDataFactory {
  // Business rule for this website is, if booked ticket = 1 (Eligible) and booked ticket > 1 (Non Eligible)
    public static EventBookDetailDataObject createBooking(int tickets) {
        String email = DataFakerHelper.getFaker().internet().emailAddress();

        return EventBookDetailDataObject.builder()
                .numOfTickets(tickets)
                .fullName(DataFakerHelper.getFaker().name().fullName())
                .email(normalizeEmailLocalPart(email))
                .phoneNumber("+62" + DataFakerHelper.getFaker().number().digits(11))
                .build();
    }

    private static String normalizeEmailLocalPart(String email) {
        String[] parts = email.split("@", 2);

        if (parts.length != 2) {
            return email;
        }

        return parts[0].replace(".", "") + "@" + parts[1];
    }
}
