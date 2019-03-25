package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Override
    public String print() {
        StringBuilder receipt = new StringBuilder()
                .append("ID: ").append(getOrderId()).append(" (Return)")
                .append("\n")
                // TODO: Format rent date in dd-MM-YY format
                .append("Rent date: ").append(getRentDate().toString())
                .append("\n").append("Customer: ").append(getCustomerName())
                // TODO: Format return date in dd-MM-YY format
                .append("\nReturn date: ").append(getReturnDate().toString())
                .append("\n");

        returnedItems.forEach(item -> receipt.append(item.print()));

        receipt.append("\n");
        receipt.append("Total late change: ").append(getTotalCharge()).append(" EUR");

        return receipt.toString();
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

        public BigDecimal getExtraPrice() {
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
