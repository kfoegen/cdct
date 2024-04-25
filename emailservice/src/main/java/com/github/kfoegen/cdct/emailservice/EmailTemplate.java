package com.github.kfoegen.cdct.emailservice;

public enum EmailTemplate {
    INVOICE("""
            > Dear Customer
            > We have received your order ${orderNo} on ${orderDate}.
            > Please transfer the amount of ${totalAmount} EUR to our bank account.
            > Best regards,
            > your online shop
            """),

    SHIPPING_CONFIRMATION("""
            > Dear Customer
            > Your order ${orderNo} from ${orderDate} is on its way.
            > You can track your parcel with the following tracking number.
            > - ${trackingNumber}
            > Best regards,
            > your online shop
            """);

    private final String templateText;

    EmailTemplate(String templateText) {
        this.templateText = templateText;
    }

    public String getTemplateText() {
        return templateText;
    }
}
