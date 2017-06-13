package org.pminin.tb;

import org.pminin.tb.model.Instrument;
import org.pminin.tb.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InstrumentStorage {

	private final Map<String, Instrument> instruments = new HashMap<>();

	@Autowired
	AccountService accountService;

	public Instrument getInstrument(String instrument) {
		Instrument result = instruments.get(instrument);
		if (result == null) {
			result = updateInstrument(instrument);
		}
		return result;
	}

	private Instrument updateInstrument(String instrument) {
		Instrument result;
		result = accountService.getInstrument(instrument);
		instruments.put(instrument, result);
		return result;
	}

}
