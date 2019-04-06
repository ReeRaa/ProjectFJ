package test.fujitsu.videostore.backend.reciept;

import test.fujitsu.videostore.backend.domain.MovieType;
import test.fujitsu.videostore.backend.domain.RentOrder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Rent order receipt printer
 */
public class PrintableOrderReceipt implements PrintableReceipt {

    private String orderId;
    private LocalDate orderDate;
    private String customerName;
    private List<Item> orderItems;
    private BigDecimal totalPrice;
    private int remainingBonusPoints;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<Item> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Item> orderItems) {
        this.orderItems = orderItems;
    }

    final static BigDecimal premiumPrice=new BigDecimal("4");
    final static BigDecimal basicPrice=new BigDecimal("3");


    public enum RentPriceClasses {
        PREMIUM_PRICE(premiumPrice),
        BASIC_PRICE(basicPrice);

        private BigDecimal rentPrice;
        RentPriceClasses(BigDecimal rentPrice){
            this.rentPrice=rentPrice;
        }

        public BigDecimal getRentPrice() {
            return this.rentPrice;
        }
    }

    public static BigDecimal price;



    public BigDecimal getTotalPrice() {
        totalPrice=calcTotalPrice();
        return totalPrice;
    }

    public BigDecimal calcTotalPrice(){
        for (int i=0;i<getOrderItems().size();i++){
           totalPrice = totalPrice.add(getOrderItems().get(i).getPaidMoney());
        }
        return totalPrice.subtract(BigDecimal.ONE);
      //  return totalPrice.subtract(getTotalPrice());
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getRemainingBonusPoints() {
        return remainingBonusPoints;
    }

    public void setRemainingBonusPoints(int remainingBonusPoints) {
        this.remainingBonusPoints = remainingBonusPoints;
    }


    String formattedDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YY");

    public String print() {
        formattedDate=getOrderDate().format(formatter);

        StringBuilder receipt = new StringBuilder()
                .append("ID: ").append(getOrderId())
                .append("\n")
                // Done Format rent date in dd-MM-YY format
                .append("Date: ").append(formattedDate)
                .append("\n").append("Customer: ").append(getCustomerName())
                .append("\n");

        boolean paidAnyUsingBonus = false;

        for (PrintableOrderReceipt.Item orderItem : getOrderItems()) {
            receipt.append(orderItem.print());

            if (orderItem.getPaidBonus() != null) {
                paidAnyUsingBonus = true;
            }
        }

        receipt.append("\n");
        receipt.append("Total price: ").append(getTotalPrice()).append(" EUR");

        if (paidAnyUsingBonus) {
            receipt.append("\nRemaining Bonus points: ").append(getRemainingBonusPoints());
        }

        return receipt.toString();
    }


    public static class Item {

        private String movieName;
        private MovieType movieType;
        private int days;
        private BigDecimal paidMoney = null;
        private Integer paidBonus = null;

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

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public BigDecimal getPaidMoney() {
            paidMoney= getRentPriceCalculation(getMovieType());
            return paidMoney;
        }

        public void setPaidMoney(BigDecimal paidMoney) {
            this.paidMoney = paidMoney;
        }

        public Integer getPaidBonus() {
            return paidBonus;
        }

        public void setPaidBonus(Integer paidBonus) {
            this.paidBonus = paidBonus;
        }

        BigDecimal defaultValue = new BigDecimal("0");

        public BigDecimal getRentPriceCalculation(MovieType movieType){
            MathContext mc=new MathContext(2);
            switch (movieType){
                case NEW: {price = BigDecimal.valueOf(getDays()).multiply(RentPriceClasses.PREMIUM_PRICE.getRentPrice());
                    return price;}
                case REGULAR: {if (getDays()<4){
                    price=RentPriceClasses.BASIC_PRICE.getRentPrice();
                }else{
                    price= (RentPriceClasses.BASIC_PRICE.getRentPrice()).add(BigDecimal.valueOf(getDays()-3).multiply(RentPriceClasses.BASIC_PRICE.getRentPrice()), mc);
                }
                    return price;}
                case OLD: { if (getDays()<6){
                    price=RentPriceClasses.BASIC_PRICE.getRentPrice();
                }else {
                    price=(RentPriceClasses.BASIC_PRICE.getRentPrice()).add(BigDecimal.valueOf(getDays()-5).multiply(RentPriceClasses.BASIC_PRICE.getRentPrice()));
                }
                    return price;}
                default: return defaultValue;
            }
        }


        public String print() {
            StringBuilder receipt = new StringBuilder();
            receipt.append(getMovieName())
                    .append(" (")
                    .append(getMovieType().getTextualRepresentation())
                    .append(") ")
                    .append(getDays());

            if (getDays()<2){
            receipt.append(" day ");}
            else {receipt.append(" days ");}

            if (getPaidBonus() != null) {
                receipt.append("(Paid with ").append(getPaidBonus()).append(" Bonus points) ");
            } else {
                receipt.append(getPaidMoney()).append(" EUR");
            }

            receipt.append("\n");

            return receipt.toString();
        }
    }
}
