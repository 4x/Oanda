package com.oanda.bot.strategies.parameters.exit;

import com.oanda.bot.strategies.enums.StopType;
import eu.verdelhan.ta4j.Decimal;

public class FixedStopLossParameters {

	private StopType type;
	
	private Decimal value;
	
	public FixedStopLossParameters() {
	}

	public FixedStopLossParameters(StopType type, Decimal value) {
		this.type = type;
		this.value = value;
	}

	public StopType getType() {
		return type;
	}

	public Decimal getValue() {
		return value;
	}
}
