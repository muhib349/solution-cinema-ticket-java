package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.Type;
import uk.gov.dwp.uc.pairtest.exception.*;

import java.util.Arrays;

public class TicketServiceImpl implements TicketService {

    private static final int MAXIMUM_TICKETS = 20;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        isAccountValid(accountId);
        isTicketTypeRequestValid(ticketTypeRequests);
        isAdultTicketPurchaserPresent(ticketTypeRequests);
        validateMaximumTicketsNumber(ticketTypeRequests);
        ticketPaymentService.makePayment(accountId, getTotalPriceOfTickets(ticketTypeRequests));
        seatReservationService.reserveSeat(accountId, getTotalNumOfValidTickets(ticketTypeRequests));
    }


    private void isAdultTicketPurchaserPresent(TicketTypeRequest... ticketTypeRequests) {
        if(Arrays.stream(ticketTypeRequests)
                .noneMatch(ticket -> ticket.getTicketType() == Type.ADULT && ticket.getNoOfTickets() > 0)){
            throw new AdultPurchaserNotPresentException("No adult purchaser has been found");
        }
    }

    private void isAccountValid(Long accountId){
        if(accountId == null || accountId < 1){
            throw new InvalidAccountException("Invalid account id");
        }
    }

    private void isTicketTypeRequestValid(TicketTypeRequest... ticketTypeRequests){
        if(ticketTypeRequests == null || ticketTypeRequests.length < 1){
            throw new InvalidTicketTypeRequestException("Invalid ticket type request, at least one ticket request is required");
        }
    }

    private void validateMaximumTicketsNumber(TicketTypeRequest... ticketTypeRequests) {
        int numOfTotalTickets = Arrays.stream(ticketTypeRequests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
        if (numOfTotalTickets > MAXIMUM_TICKETS) {
            throw new ExceedTicketLimitException("You can buy up to 20 tickets at a time");
        }
    }


    private int getTotalNumOfValidTickets(TicketTypeRequest... ticketTypeRequests){
        return Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() != Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int getTotalPriceOfTickets(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() != Type.INFANT)
                .mapToInt(request -> request.getNoOfTickets() * request.getTicketType().getPrice())
                .sum();
    }

}
