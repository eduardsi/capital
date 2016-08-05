package net.sizovs.capital

import com.fasterxml.jackson.annotation.JsonAnySetter

class Product {

    enum PricingRef {
        EARLY, REGULAR
    }

    String name
    String description

    Map<String, Pricing> pricing = [:]

    Map<String, Coupon> coupons = [:]

    static class Pricing {
        BigDecimal price
        Object validUntil
    }

    static class Coupon {
        String description
        BigDecimal discountPercent
    }

    @JsonAnySetter
    public void setDynamicProperty(String ref, Pricing object) {
        pricing.put(ref, object)
    }

}
