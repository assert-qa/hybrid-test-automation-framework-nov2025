package factory;

import helpers.DataFakerHelper;
import pages.dto.EventBookDetailDataObject;

public class BookingDataFactory {
  // Business rule for this website is, if booked ticket = 1 (Eligible) and booked ticket > 1 (Non Eligible)
    public static EventBookDetailDataObject createBooking(int tickets) {
        return EventBookDetailDataObject.builder()
                .numOfTickets(tickets)
                .fullName(DataFakerHelper.getFaker().name().fullName())
                .email(DataFakerHelper.getFaker().internet().emailAddress())
                .phoneNumber("+62" + DataFakerHelper.getFaker().number().digits(11))
                .build();
    }
}
