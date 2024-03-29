package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.Customer;
import test.fujitsu.videostore.backend.domain.RentOrder;
import test.fujitsu.videostore.backend.domain.ReturnOrder;

import java.math.BigDecimal;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple receipt creation service
 * <p>
 * Note! All calculations should be in another place. Here we just setting already calculated data. Feel free to refactor.
 */
public class OrderToReceiptService {

    /**
     * Converts rent order to printable receipt
     *
     * @param order rent object
     * @return Printable receipt object
     */


    public PrintableOrderReceipt convertRentOrderToReceipt(RentOrder order) {
        PrintableOrderReceipt printableOrderReceipt = new PrintableOrderReceipt();

        printableOrderReceipt.setOrderId(order.isNewObject() ? "new" : Integer.toString(order.getId()));
        printableOrderReceipt.setOrderDate(order.getOrderDate());
        printableOrderReceipt.setCustomerName(order.getCustomer().getName());

        int customerPoints= order.getCustomer().getPoints();
        int customerId = order.getCustomer().getId();
        int isItEnough=0;
        int remainingDaystoBePaid;
        int remainingPoints=0;
        int usedPoints=0;


        List<PrintableOrderReceipt.Item> itemList = new ArrayList<>();
        printableOrderReceipt.setOrderItems(itemList);

        for (RentOrder.Item orderItem : order.getItems()) {
            PrintableOrderReceipt.Item item = new PrintableOrderReceipt.Item();
            if (orderItem.isPaidByBonus()){
                isItEnough= customerPoints/25;
                if (isItEnough<=orderItem.getDays()){
                    remainingDaystoBePaid=orderItem.getDays() - isItEnough;
                    usedPoints=order.getCustomer().getPoints()- (isItEnough * 25);
                    remainingPoints=customerPoints - usedPoints;

                } else{
                    usedPoints=order.getCustomer().getPoints()- (orderItem.getDays() * 25);
                    remainingPoints=customerPoints - usedPoints;

                }
            }
            item.setDays(orderItem.getDays());
            item.setMovieName(orderItem.getMovie().getName());
            item.setMovieType(orderItem.getMovieType());
           // PrintableOrderReceipt.Item.
            // TODO: Add calculated data
            //  RentOrder.Item::getMovieType,
            //  PrintableOrderReceipt.Item::getDays,
            //  PrintableOrderReceipt.Item::getDays::isPaidByBonus
            if (orderItem.isPaidByBonus()) {
                item.setPaidBonus(isItEnough*25); //how much was paid with bonusPoints
            } else {
                item.setPaidMoney(BigDecimal.ONE);
            }

            itemList.add(item);
        }

        // TODO: Set here calculated total price of renting order
        printableOrderReceipt.setTotalPrice(BigDecimal.ONE);

        // TODO: Set how many bonus points remaining for customer
        printableOrderReceipt.setRemainingBonusPoints(remainingPoints);

        order.getCustomer().setPoints(remainingPoints);

        return printableOrderReceipt;
    }

    /**
     * Converts return order to printable receipt
     *
     * @param order return object
     * @return Printable receipt object
     */

    public PrintableReturnReceipt convertRentOrderToReceipt(ReturnOrder order) {
        PrintableReturnReceipt receipt = new PrintableReturnReceipt();
        PrintableOrderReceipt.Item orderReceipt=new PrintableOrderReceipt.Item();

        receipt.setOrderId(Integer.toString(order.getRentOrder().getId()));
        receipt.setCustomerName(order.getRentOrder().getCustomer().getName());
        receipt.setRentDate(order.getRentOrder().getOrderDate());
        receipt.setReturnDate(order.getReturnDate());

        List<PrintableReturnReceipt.Item> returnedItems = new ArrayList<>();
        if (order.getItems() != null) {
            for (RentOrder.Item rentedItem : order.getItems()) {
                PrintableReturnReceipt.Item item = new PrintableReturnReceipt.Item();
                item.setMovieName(rentedItem.getMovie().getName());
                item.setMovieType(rentedItem.getMovieType());
                // TODO: DONE Set calculated data how much later rented movie was returned <-calculated!

                //numberOfDaysReturnedLater=period.getDays() ;

//                item.setExtraDays(numberOfDaysReturnedLater);
//                item.setExtraDays((int)((order.getRentOrder().getOrderDate()).until(rentedItem.getReturnedDay(), ChronoUnit.DAYS))) ;
                item.setExtraDays((int)ChronoUnit.DAYS.between(receipt.getRentDate(),receipt.getReturnDate())- orderReceipt.getDays());
                //               item.setExtraDays(item.getExtraDays()) ;

               // ifPresent(movieToReturn -> movieToReturn.setReturnedDay(returnOrder.getReturnDate

                // TODO: DONE Set calculated data how much it will cost extra days
                item.setExtraPrice(BigDecimal.ZERO);
             //   item.setExtraPrice(receipt.getTotalCharge());

                returnedItems.add(item);
            }
        }
        receipt.setReturnedItems(returnedItems);

        // TODO: Set calculated total extra charge for all movies
        receipt.setTotalCharge(BigDecimal.ZERO);

        return receipt;
    }

}
