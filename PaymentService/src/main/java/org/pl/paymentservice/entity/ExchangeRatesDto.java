package org.pl.paymentservice.entity;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRatesDto(String base, Map<String, BigDecimal> rates) {}