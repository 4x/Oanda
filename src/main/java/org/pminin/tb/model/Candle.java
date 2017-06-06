package org.pminin.tb.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.pminin.tb.constants.Step;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Candle {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candles {
        private String instrument;

        @JsonDeserialize(using = CustomGranularityDeserializer.class)
        private Step granularity;

        private List<Candle> candles = new ArrayList<Candle>();

        @Override
        public String toString() {
            if (instrument == null) {
                return "No candles";
            }
            String string = "Candles [instrument=" + instrument + ", granularity=" + granularity + ", candles=\n";
            if (candles != null) {
                for (Candle c : candles) {
                    string += "\t";
                    string += c.toString();
                    string += "\n";
                }
            }
            string += "]";
            return string;
        }
    }

    public static class CustomGranularityDeserializer extends JsonDeserializer<Step> {
        @Override
        public Step deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext)
                throws IOException, JsonProcessingException {
            return Step.valueOf(jsonparser.getText());
        }

    }

    private DateTime time;
    private double o;
    private double h;
    private double l;
    private double c;

    public void setOpenMid(double val) {
        this.o = val;
    }

    public void setHighMid(double val) {
        this.h = val;
    }

    public void setLowMid(double val) {
        this.l = val;
    }

    public void setCloseMid(double val) {
        this.c = val;
    }

    public double getOpenMid() {
        return o;
    }

    public double getHighMid() {
        return h;
    }

    public double getLowMid() {
        return l;
    }

    public double getCloseMid() {
        return c;
    }

    private int volume;
    private boolean complete;
    private boolean broken;

    private DateTime brokenTime;

    private int direction;

    public Date getBrokenDateTime() {
        return time.toDate();
    }

    public Date getDateTime() {
        return time.toDate();
    }

    public void setBrokenDateTime(Date date) {
        time = new DateTime(date.getTime(), DateTimeZone.getDefault());
    }

    //TODO json to Date
    public void setTime(DateTime date) {
        time = date;
    }

    public void setTime(Date date) {
        time = new DateTime(date.getTime(), DateTimeZone.getDefault());
    }

    public void setTime(String date) {
        time = new DateTime(date, DateTimeZone.getDefault());
    }

}
