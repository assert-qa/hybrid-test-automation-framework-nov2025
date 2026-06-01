package factory;

import helpers.DataFakerHelper;
import pages.models.EventBookDetailDataObject;

public class BookingDataFactory {

    public static EventBookDetailDataObject createBooking() {
        return EventBookDetailDataObject.builder()
                .numOfTickets(DataFakerHelper.getFaker().number().numberBetween(1, 10))
                .fullName(DataFakerHelper.getFaker().name().fullName())
                .email(DataFakerHelper.getFaker().internet().emailAddress())
                .phoneNumber("+62" + DataFakerHelper.getFaker().number().digits(11))
                .build();
    }
}
