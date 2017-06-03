package org.pminin.tb.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Trade {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Trades {
		List<Trade> trades = new ArrayList<>();
	}

	/*
	  "trades": [
		{
		  "currentUnits": "-600",
		  "financing": "0.00000",
		  "id": "6397",
		  "initialUnits": "-600",
		  "instrument": "USD_CAD",
		  "openTime": "2016-06-22T18:41:48.262344782Z",
		  "price": "1.28241",
		  "realizedPL": "0.00000",
		  "state": "OPEN",
		  "unrealizedPL": "-0.08525"
		}
	  ]
	*/

	private String id;
	private int currentUnits;
	private double financing;
	private int initialUnits;
	private String instrument;
	@JsonDeserialize(using = UnixTimestampDeserializer.class)
	private DateTime openTime;
	private double price;
	private double realizedPL;
	private State state;
	private double unrealizedPL;

	//todo EBALA  надо тут чет придумать
	public double getStopLoss() {
		return (price - Math.abs(unrealizedPL));
	}

	public void setStopLoss(double val) {
		this.unrealizedPL = (-1) * (price - val);
	}

	/*private String id;
	private int units;
	private String side;
	private String instrument;
	@JsonDeserialize(using = UnixTimestampDeserializer.class)
	private DateTime time;
	private double price;
	private double takeProfit;
	private double stopLoss;
	private double trailingStop;
	private double trailingAmount;
	private double profit;*/
}
