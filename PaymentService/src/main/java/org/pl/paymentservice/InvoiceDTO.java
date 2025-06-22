package org.pl.paymentservice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDTO {

    private Long paymentId;
    private LocalDateTime invoiceDate;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String currency;
    private List<ClientInfo> clients;

    public static class ClientInfo {
        private Long id;
        private String name;
        private String clientType;

        public ClientInfo() {}
        public ClientInfo(Long id, String name, String clientType) {
            this.id = id;
            this.name = name;
            this.clientType = clientType;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getClientType() { return clientType; }
        public void setClientType(String clientType) { this.clientType = clientType; }
    }
    
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<ClientInfo> getClients() { return clients; }
    public void setClients(List<ClientInfo> clients) { this.clients = clients; }
}

