package com.example.learning_api.constant;

public class ProjectConstant {
    public static final int ONSITE_OPERATING_RANGE = 12000;
    // fixed data string
    public static final String COIN_REFUND_CANCELLED_ORDER = "Coins refunded from canceled paid orders";
    public static final String DEDUCT_COIN_TO_PAY = "Deduct coins to pay bill ";
    public static final String REVIEW_COIN = "Bonus coins for product reviews";
    public static final String REFUND_COIN_ORDER = "Coins refunded when order is canceled";

    // error message
    public static final String TIMEOUT_REQUEST_REFUND_ERR_MSG = "A refund request cannot be submitted after 3 hours from the time the order is successfully delivered";
    public static final String TOKEN_NOT_FOUND_ERR_MSG = "Token not found in database";

    // config
    public static final int OPERATING_RANGE_DISTANCE_METTER = 12000;
    public static final int ORDER_BOOM_COUNT_LIMIT = 3;
    public static final int HOURS_REQUEST_REFUND = 3;
    public static final int TIMEOUT_REFUSE_ORDER_MINUTES = 30;
    public static final int ACCESS_TOKEN_EXPIRE_MINUTES_TIME = 60 * 24 * 7;
    public static final int REFRESH_TOKEN_EXPIRE_MINUTES_TIME = 60 * 24 * 7;
    public static final int TIMEOUT_VNPAY_TRANSACTION_MINUTES = 17;
    public static final int TIMEOUT_ZALO_TRANSACTION_MINUTES = 15;
    public static final int TIME_AFTER_PENDING_PICKUP_MINUTES = 30;

    // notification messages

    public static final String SYSTEM_FCM_TOPIC = "system_notification";
    public static final String NEW_ORDER_MSG = "%s order %s created. Check now";
    public static final String EMPLOYEE_NOTI_TITLE_MSG = "Shopfee For Employee";
    public static final String USER_NOTI_TITLE_MSG = "Shopfee";
    public static final String SHIPPING_ORDER_TITLE_MSG = "Home delivery";
    public static final String ONSITE_ORDER_TITLE_MSG = "Take away";


    // Order status description
    public static final String PAYMENT_FAILED_MSG = "Payment failed";
}
