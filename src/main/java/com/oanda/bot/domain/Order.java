package com.oanda.bot.domain;

import com.oanda.bot.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String id;
    private String replacesOrderID;
    private String createTime;
    private OrderState state;
    private OrderType type = OrderType.MARKET;
    private String instrument;
    private Integer units;
    private Double price;
    private Double priceBound;
    private TimeInForce timeInForce = TimeInForce.GTD;
    private String gtdTime = DateTimeUtil.rfc3339Plus2Days();
    private OrderPositionFill positionFill = OrderPositionFill.DEFAULT;
    private OrderTriggerCondition triggerCondition = OrderTriggerCondition.DEFAULT;
    private Details takeProfitOnFill;
    private Details stopLossOnFill;
    private TrailingStopLossDetails trailingStopLossOnFill;
    private String cancelledTime = DateTimeUtil.rfc3339Plus2Days();

    public enum OrderState {
        PENDING, FILLED, TRIGGERED, CANCELLED
    }

    public enum OrderType {
        MARKET, LIMIT, STOP, MARKET_IF_TOUCHED, TAKE_PROFIT, STOP_LOSS, TRAILING_STOP_LOSS,
        MARKET_ORDER, LIMIT_ORDER, STOP_ORDER, MARKET_IF_TOUCHED_ORDER, TAKE_PROFIT_ORDER, STOP_LOSS_ORDER, TRAILING_STOP_LOSS_ORDER
    }

    public enum TimeInForce {
        GTC, GTD, GFD, FOK, IOC
    }

    public enum OrderPositionFill {
        OPEN_ONLY, REDUCE_FIRST, REDUCE_ONLY, DEFAULT
    }

    public enum OrderTriggerCondition {
        DEFAULT, INVERSE, BID, ASK, MID
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Details {
        private double price;
        private TimeInForce timeInForce = TimeInForce.GTC;

        public Details(double price) {
            this.price = price;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TrailingStopLossDetails {
        private double distance;
        private TimeInForce timeInForce = TimeInForce.GTC;

        public TrailingStopLossDetails(double distance) {
            this.distance = distance;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Orders {
        List<Order> orders = new ArrayList<>();
    }

    public void setType(OrderType type) {
        if (type.equals(OrderType.LIMIT_ORDER)) type = OrderType.LIMIT;
        if (type.equals(OrderType.STOP_ORDER)) type = OrderType.STOP;
        if (type.equals(OrderType.MARKET_ORDER)) type = OrderType.MARKET;
        if (type.equals(OrderType.MARKET_IF_TOUCHED_ORDER)) type = OrderType.MARKET_IF_TOUCHED;

        this.type = type;
    }

    public double getStopLoss() {
        return stopLossOnFill != null ? getStopLossOnFill().getPrice() : 0D;
    }

    public double getTakeProfit() {
        return takeProfitOnFill != null ? getTakeProfitOnFill().getPrice() : 0D;
    }

    public double getTrailingStopLoss() {
        return trailingStopLossOnFill != null ? getTrailingStopLossOnFill().getDistance() : 0D;
    }

    public String getOrderType() {
        return this.getType().name();
    }

    public Date getDateTime() {
        return DateTime.now().toDate();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                (state != null ? ", state='" + state + '\'' : "") +
                ", instrument='" + instrument + '\'' +
                ", units=" + units +
                (price != null ? ", price=" + price : "") +
                (priceBound != null ? ", priceBound=" + priceBound : "") +
                ", takeProfit=" + getTakeProfit() +
                ", stopLoss=" + getStopLoss() +
                ", trailingStopLoss=" + getTrailingStopLoss() +
                '}';
    }
}