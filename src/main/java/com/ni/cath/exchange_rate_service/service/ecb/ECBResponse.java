package com.ni.cath.exchange_rate_service.service.ecb;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(namespace = "http://www.gesmes.org/xml/2002-08-01", localName = "ECBResponse")
public class ECBResponse {

    @JacksonXmlProperty(localName = "subject", namespace = "http://www.gesmes.org/xml/2002-08-01")
    private String subject;

    @JacksonXmlProperty(localName = "Sender", namespace = "http://www.gesmes.org/xml/2002-08-01")
    private Sender sender;

    @JacksonXmlProperty(localName = "Cube", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref")
    private ExchangeRatesWrapper exchangeRatesWrapper;


    @Getter
    @Setter
    static class Sender {
        @JacksonXmlProperty(localName = "name", namespace = "http://www.gesmes.org/xml/2002-08-01")
        private String name;
    }

    @Getter
    @Setter
    public static class ExchangeRatesWrapper {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Cube")
        private List<ExchangeRates> exchangeRates;
    }

    @Getter
    @Setter
    public static class ExchangeRates {
        @JacksonXmlProperty(isAttribute = true)
        private String time;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Cube")
        private List<ExchangeRate> rates;
    }
}
