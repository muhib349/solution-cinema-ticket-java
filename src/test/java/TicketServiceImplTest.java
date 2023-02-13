import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.Type;
import uk.gov.dwp.uc.pairtest.exception.AdultPurchaserNotPresentException;
import uk.gov.dwp.uc.pairtest.exception.ExceedTicketLimitException;
import uk.gov.dwp.uc.pairtest.exception.InvalidAccountException;
import uk.gov.dwp.uc.pairtest.exception.InvalidTicketTypeRequestException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @Mock
    private TicketPaymentService ticketPaymentService;
    @Mock
    private SeatReservationService seatReservationService;

    private TicketServiceImpl ticketService;

    @Before
    public void setUp(){
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void testPurchaseTicketsWithNoAdultPurchaser() {
        Long accountId = 12345L;

        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(Type.CHILD, 2),
                new TicketTypeRequest(Type.INFANT, 1)
        };

        assertThrows(AdultPurchaserNotPresentException.class, () -> {
            ticketService.purchaseTickets(accountId, ticketTypeRequests);
        });
    }

    @Test
    public void testPurchaseTicketsWithInvalidAccount() {
        Long accountId = null;

        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(Type.ADULT, 2),
                new TicketTypeRequest(Type.CHILD, 1)
        };

        assertThrows(InvalidAccountException.class, () -> {
            ticketService.purchaseTickets(accountId, ticketTypeRequests);
        });
    }

    @Test
    public void testPurchaseTicketsWithNoTicketRequest() {
        Long accountId = 12345L;
        TicketTypeRequest[] ticketTypeRequests = {};

        assertThrows(InvalidTicketTypeRequestException.class, () -> {
            ticketService.purchaseTickets(accountId, ticketTypeRequests);
        });
    }

    @Test
    public void testPurchaseTicketsWithExceedTicketLimit() {
        Long accountId = 12345L;
        TicketTypeRequest[] ticketTypeRequests = { new TicketTypeRequest(Type.ADULT, 21) };

        assertThrows(ExceedTicketLimitException.class, () -> {
            ticketService.purchaseTickets(accountId, ticketTypeRequests);
        });
    }


    @Test
    public void testPurchaseTicketsWithValidTicketPrice() {
        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(Type.ADULT, 10),
                new TicketTypeRequest(Type.CHILD, 5),
                new TicketTypeRequest(Type.INFANT, 5)
        };

        ticketService.purchaseTickets(50L, ticketTypeRequests);
        Mockito.verify(ticketPaymentService).makePayment(50, 250);
    }

    @Test
    public void testPurchaseTicketsWithValidSeatCount() {
        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(Type.ADULT, 10),
                new TicketTypeRequest(Type.CHILD, 5),
                new TicketTypeRequest(Type.INFANT, 5)
        };

        ticketService.purchaseTickets(50L, ticketTypeRequests);
        Mockito.verify(seatReservationService).reserveSeat(50, 15);
    }

}
