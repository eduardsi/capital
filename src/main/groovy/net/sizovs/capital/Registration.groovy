package net.sizovs.capital

class Registration {

    String productName
    String pricing
    BillingInformation billingInformation
    Collection<Ticket> tickets

    static class Ticket {
        String role
        String name
        String employer
        String email
    }

    static class BillingInformation {
        String address
        String name
        String identificationNumber
        String type
        String email
        String vatNumber
    }

}
