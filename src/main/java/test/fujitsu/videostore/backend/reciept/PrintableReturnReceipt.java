package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Return receipt printer
 */
public class PrintableReturnReceipt implements PrintableReceipt {

    private String orderId;
    private String customerName;
    private LocalDate rentDate;
    private LocalDate returnDate;
    private BigDecimal totalCharge;
    private List<Item> returnedItems;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public List<Item> getReturnedItems() {
        return returnedItems;
    }

    public void setReturnedItems(List<Item> returnedItems) {
        this.returnedItems = returnedItems;
    }

    String formattedRentDate;
    String formattedReturnDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YY");

    @Override
    public String print() {
        formattedRentDate=getRentDate().format(formatter);
        formattedReturnDate=getReturnDate().format(formatter);

        StringBuilder receipt = new StringBuilder()
                .append("ID: ").append(getOrderId()).append(" (Return)")
                .append("\n")
                // Done: Format rent date in dd-MM-YY format
                .append("Rent date: ").append(formattedRentDate)
                .append("\n").append("Customer: ").append(getCustomerName())
                // Done: Format return date in dd-MM-YY format
                .append("\nReturn date: ").append(formattedReturnDate)
                .append("\n");

        returnedItems.forEach(item -> receipt.append(item.print()));

        receipt.append("\n");
        receipt.append("Total late change: ").append(getTotalCharge()).append(" EUR");

        return receipt.toString();
    }

    public int helpergetExtraDays() {

        int numberOfDaysReturnedLater;
        Period period=Period.between(getRentDate(),getReturnDate());
        numberOfDaysReturnedLater=period.getDays() ;

        return numberOfDaysReturnedLater;
    }

    public static class Item implements PrintableReceipt {
        private String movieName;
        private MovieType movieType;
        private int extraDays;
        private BigDecimal extraPrice;

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public MovieType getMovieType() {
            return movieType;
        }

        public void setMovieType(MovieType movieType) {
            this.movieType = movieType;
        }

        public int getExtraDays() {
            return extraDays;
        }

        public void setExtraDays(int extraDays) {
            this.extraDays = extraDays;
        }

        //naidis
/*        public BigDecimal getRentPriceCalculation(MovieType movieType){
            MathContext mc=new MathContext(2);
            switch (movieType){
                case NEW: {price = BigDecimal.valueOf(getDays()).multiply(PrintableOrderReceipt.RentPriceClasses.PREMIUM_PRICE.getRentPrice());
                    return price;}
                case REGULAR: {if (getDays()<4){
                    price= PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice();
                }else{
                    price= (PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice()).add(BigDecimal.valueOf(getDays()-3).multiply(PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice()), mc);
                }
                    return price;}
                case OLD: { if (getDays()<6){
                    price= PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice();
                }else {
                    price=(PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice()).add(BigDecimal.valueOf(getDays()-5).multiply(PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice()));
                }
                    return price;}
                default: return defaultValue;
            }
        }*/

        //naidis loppeb siin

        public BigDecimal getExtraPrice() {
            getMovieType();
            switch (movieType){
                case NEW: extraPrice = BigDecimal.valueOf(getExtraDays()).multiply(PrintableOrderReceipt.RentPriceClasses.PREMIUM_PRICE.getRentPrice()); break;
                case REGULAR:  extraPrice= (BigDecimal.valueOf(getExtraDays()).multiply(PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice()));break;
                case OLD:  extraPrice= (BigDecimal.valueOf(getExtraDays()).multiply(PrintableOrderReceipt.RentPriceClasses.BASIC_PRICE.getRentPrice())); break;
                default: return extraPrice;
            }
            return extraPrice;
        }

        public void setExtraPrice(BigDecimal extraPrice) {
            this.extraPrice = extraPrice;
        }

        @Override
        public String print() {
            return getMovieName()
                    .concat(" (")
                    .concat(getMovieType().getTextualRepresentation())
                    .concat(") ")
                    .concat(Integer.toString(getExtraDays()))
                    .concat(" extra days ")
                    .concat(getExtraPrice().toString())
                    .concat(" EUR\n");
        }
    }
}
