package com.drobucs.histology.payment.controllers;

import com.drobucs.base.web.request.*;
import com.drobucs.histology.payment.controllers.exceptions.CreateRequestException;
import com.drobucs.histology.payment.controllers.exceptions.ValidationNullParamException;
import com.drobucs.histology.payment.models.*;
import com.drobucs.histology.payment.models.factory.ObjectFactory;
import com.drobucs.histology.payment.models.notifications.Notification;
import com.drobucs.histology.payment.models.notifications.NotificationPayment;
import com.drobucs.histology.payment.models.notifications.NotificationRefund;
import com.drobucs.histology.payment.models.notifications.NotificationEvent;
import com.drobucs.histology.payment.models.yookassa.client.CannotCreateClientException;
import com.drobucs.histology.payment.models.yookassa.exceptions.StatusInfoException;
import com.drobucs.histology.payment.models.yookassa.exceptions.UnknownPaymentStatusException;
import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.drobucs.histology.payment.models.yookassa.payment.Payment;
import com.drobucs.histology.payment.models.yookassa.payment.capture.Capture;
import com.drobucs.histology.payment.models.yookassa.payment.capture.QueryCaptureException;
import com.drobucs.histology.payment.models.yookassa.payment.create.*;
import com.drobucs.histology.payment.models.yookassa.refund.Refund;
import com.drobucs.histology.payment.models.yookassa.refund.create.RefundCreate;
import com.drobucs.histology.payment.network.NetworkBotService;
import com.drobucs.histology.payment.network.NetworkUsersService;
import com.drobucs.histology.payment.network.NetworkYookassa;
import com.drobucs.histology.payment.services.PaymentService;
import com.drobucs.histology.payment.services.SubscriptionService;
import com.drobucs.histology.payment.services.UserService;
import com.drobucs.histology.payment.services.exceptions.RefundAuthException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment/1/service")
@RequiredArgsConstructor
public class APIPaymentController extends Api {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final UserService userService;

    @Value("${payment.currency}")
    private String currency;
    private final Set<String> YOOKASSA_IP = Set.of(
            "000.00.00.0/00"
    );

    @PostMapping("/throw/token")
    public ThrowTokenResult throwToken(@RequestParam("apiKey") String apiKey,
                                       @RequestParam("login") String login,
                                       @RequestParam("token") String token,
                                       @RequestParam("paymentMethodType") PaymentMethodType paymentMethodType,
                                       @RequestParam("subscriptionName") String subscriptionName,
                                       @RequestParam("returnUrl") String returnUrl,
                                       @RequestParam("userEmail") String userEmail) {
        if (isBadApiKey(login, apiKey)) {
            return new ThrowTokenResult(null, "bad apiKey or login", true, null);
        }
        if (userEmail == null) {
            logInfo("throwToken: User email is null.");
            return new ThrowTokenResult(null, "User email is null.", true, null);
        }
        if (paymentMethodType == null) {
            logInfo("Payment method is null.");
            return new ThrowTokenResult(null, "Payment method type is null.", true, null);
        }
        if (token == null) {
            logInfo("throwToken: Token is null.");
            return new ThrowTokenResult(null, "Token is null.", true, null);
        }
        if (returnUrl == null) {
            logInfo("Return url is null.");
            return new ThrowTokenResult(null, "Return url is null.", true, null);
        }
        Subscription subscription = subscriptionService.getSubscriptionByName(subscriptionName);
        if (subscription == null) {
            logInfo("throwToken: Invalid subscription name or something else.");
            return new ThrowTokenResult(null, "Invalid subscription name or something else.", true, null);
        }
        long userId = getUserId(login);
        if (userId < 0) {
            logInfo("throwToken: User id less then 0.");
            return new ThrowTokenResult(null, "Cannot find user.", true, null);
        }
        String uuid = UUID.randomUUID().toString();
        String confirmType;
        if (paymentMethodType == PaymentMethodType.BANK_CARD) {
            confirmType = ConfirmationType.REDIRECT;
        } else if (paymentMethodType == PaymentMethodType.YOO_MONEY) {
            confirmType = ConfirmationType.REDIRECT;
        } else if (paymentMethodType == PaymentMethodType.SBERBANK) {
            confirmType = ConfirmationType.MOBILE_APPLICATION;
        } else {
            logInfo("Unknown paymentMethodType='" + paymentMethodType + "'.");
            return new ThrowTokenResult(null, "Unknown paymentMethodType='" + paymentMethodType + "'.", true, null);
        }
        PaymentCreate paymentCreate = createPayment(
                buildAmount(subscription), token, returnUrl, confirmType, subscription, userEmail, userId
        );
        Payment paymentObject = sendRequestCreatePayment(paymentCreate, uuid);
        if (paymentObject == null) {
            logInfo("paymentObject is null.");
            return new ThrowTokenResult(null, "paymentObject is null.", true, null);
        }
        if (paymentObject.getConfirmation() == null) {
            logInfo("Payment confirmation is null.");
            com.drobucs.histology.payment.models.Payment paymentDB = paymentService.savePayment(
                    userId,
                    paymentObject.getId(),
                    paymentMethodType.toString(),
                    Double.parseDouble(paymentObject.getAmount().getValue()),
                    paymentObject.getAmount().getCurrency(),
                    uuid,
                    subscription.getId(),
                    paymentObject.getStatus(),
                    false
            );
            if (paymentDB == null) {
                logInfo("Payment not saved.");
                return new ThrowTokenResult(null, "Payment not saved.", true, null);
            }
            return new ThrowTokenResult(null,
                    "Confirmation is not required. Payment confirmation is null.",
                    false, Long.toString(paymentDB.getId()));
        }
        if (paymentObject.getPaymentMethod() == null) {
            logInfo("PaymentMethod is null.");
        }
        logJsonPayment(paymentObject);
        logInfo("Save payment...");
        com.drobucs.histology.payment.models.Payment paymentDB = paymentService.savePayment(
                userId,
                paymentObject.getId(),
                paymentObject.getPaymentMethod() == null ? null : paymentObject.getPaymentMethod().getType(),
                Double.parseDouble(paymentObject.getAmount().getValue()),
                paymentObject.getAmount().getCurrency(),
                uuid,
                subscription.getId(),
                paymentObject.getStatus(),
                true
        );
        if (paymentDB == null) {
            logInfo("Payment not saved.");
            return new ThrowTokenResult(null, "Payment not saved.", true, null);
        }
        logInfo("Payment saved.");
        if (paymentObject.getConfirmation().getConfirmationUrl() == null) {
            logInfo("Confirmation url is null.");
        }
        logInfo("ConfirmationType: '" + paymentObject.getConfirmation().getType() + "'.");
        String paymentConfirmationType = paymentObject.getConfirmation().getType();
        if (Objects.equals(paymentConfirmationType, ConfirmationType.REDIRECT) ||
                Objects.equals(paymentConfirmationType, ConfirmationType.MOBILE_APPLICATION)) {
            return new ThrowTokenResult(
                    paymentObject.getConfirmation().getConfirmationUrl(),
                    "ok", false, Long.toString(paymentDB.getId())
            );
        }
        return new ThrowTokenResult(null,
                "Confirmation type isn't a 'redirect' or 'mobile_application'", true, null);
    }

    @NotNull
    private Receipt createReceipt(@NotNull String userEmail, @NotNull Subscription subscription) {
        Receipt receipt = new Receipt();
        receipt.setCustomer(createCustomer(userEmail));
        receipt.setItems(createItems(subscription));
        logJson(receipt);
        return receipt;
    }

    private void logJson(Receipt receipt) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            logInfo(mapper.writeValueAsString(receipt));
        } catch (JsonProcessingException e) {
            logInfo("logJson(Receipt receipt): JsonProcessException: " + e.getMessage());
        }
    }

    private Item[] createItems(@NotNull Subscription subscription) {
        Item item = new Item();
        item.setAmount(buildAmount(subscription));
        item.setDescription(subscription.getDescriptionForReceipt());
        item.setQuantity("1");
        item.setVatCode(1L);
        return new Item[]{item};
    }

    private Customer createCustomer(@NotNull String email) {
        Customer customer = new Customer();
        customer.setEmail(email);
        return customer;
    }

    private void capture(@NotNull String paymentId, @NotNull Amount amount) throws QueryCaptureException {
        String status;
        try {
            status = getPaymentStatusByPaymentId(paymentId);
        } catch (StatusInfoException e) {
            throw new QueryCaptureException("StatusInfoException: " + e.getMessage());
        }
        if (!status.equals(PaymentStatus.WAITING_FOR_CAPTURE)) {
            throw new QueryCaptureException("Payment status not a " + PaymentStatus.WAITING_FOR_CAPTURE
                    + ". Actual status is '" + status + "'.");
        }
        String json = getJsonCapture(amount);
        if (json == null) {
            throw new QueryCaptureException("jsonString is null.");
        }
        Request request = new RequestPostJson(NetworkYookassa.queryPaymentCapture(paymentId))
                .setJson(json)
                .addHeader("Idempotence-Key", UUID.randomUUID().toString());
        RequestResult<String> res;
        try {
            res = request.executeString(getYookassaClient());
        } catch (CannotCreateClientException e) {
            throw new QueryCaptureException(e.getMessage());
        }
        if (res.haveErrors()) {
            throw new QueryCaptureException("Request result have errors: " + res.getMessage());
        }
        if (res.getResult() == null) {
            throw new QueryCaptureException("No errors but result is null.");
        }
    }

    private void logJsonPayment(@NotNull Payment payment) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(payment);
            if (jsonString != null) {
                logInfo(jsonString);
                return;
            }
            logInfo("String Json Payment is null.");
        } catch (JsonProcessingException e) {
            logInfo("Json error: " + e.getMessage());
        }
    }

    @NotNull
    private String getPaymentStatusByPaymentId(@NotNull String paymentId) throws StatusInfoException {
        Request request = new RequestGet(NetworkYookassa.queryPaymentInfo(paymentId));
        RequestResult<String> res;
        try {
            res = request.executeString(getYookassaClient());
        } catch (CannotCreateClientException e) {
            throw new StatusInfoException(e.getMessage());
        }
        if (res.haveErrors()) {
            throw new StatusInfoException("Request result have errors: " + res.getMessage());
        }
        if (res.getResult() == null) {
            throw new StatusInfoException("No errors but result is null.");
        }
        Payment payment = getPaymentFromJson(res.getResult());
        if (payment == null) {
            throw new StatusInfoException("Parse payment json to null.");
        }
        String status = payment.getStatus();
        if (status == null) {
            throw new StatusInfoException("Payment status is null.");
        }
        try {
            return PaymentStatus.convertFromYookassa(status);
        } catch (UnknownPaymentStatusException e) {
            throw new StatusInfoException("StatusInfoException: " + e.getMessage());
        }
    }

    @Nullable
    private String getJsonCapture(Amount amount) {
        if (amount == null) {
            logInfo("getJsonCapture: Amount is null.");
            return null;
        }
        Capture capture = new Capture();
        capture.setAmount(amount);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(capture);
            if (jsonString == null) {
                logInfo("getJsonCapture: jsonString is null.");
            }
            return jsonString;
        } catch (JsonProcessingException e) {
            logInfo("getJsonCapture: " + e.getMessage());
            return null;
        }
    }

    @PostMapping("/get/status")
    public PaymentStatus getStatus(@RequestParam("login") String login,
                                   @RequestParam("apiKey") String apiKey) {
        if (isBadApiKey(login, apiKey)) {
            return new PaymentStatus(null, "Bad api key.", null);
        }
        long userId = getUserId(login);
        if (userId < 0) {
            return new PaymentStatus(null, "Cannot find userId.", null);
        }
        Payment payment = getPaymentStatus(userId);
        if (payment == null) {
            return new PaymentStatus(null, "Payment is null.", null);
        }
        String status = payment.getStatus();
        if (status == null) {
            return new PaymentStatus(null, "Payment status is null.", null);
        }
        try {
            return new PaymentStatus(PaymentStatus.convertFromYookassa(status), "ok", payment.getCancellationDetails());
        } catch (UnknownPaymentStatusException e) {
            return new PaymentStatus(null, "Error: " + e.getMessage(), null);
        }
    }


    private void notificationRefund(@NotNull Notification notification) {
        // event=success
        Refund refund = (Refund) notification.getObject();
        if (refund == null) {
            logInfo("Refund is null.");
            return;
        }
        com.drobucs.histology.payment.models.Refund refundDB = ObjectFactory.newRefund();
        refundDB.setAmountValue(refund.getAmount().getValue());
        refundDB.setCurrency(refund.getAmount().getCurrency());
        refundDB.setStatus(refund.getStatus());
        refundDB.setDescription(refund.getDescription());
        refundDB.setCreatedAt(refund.getCreatedAt());
        refundDB.setRefundId(refund.getId());
        refundDB.setPaymentId(refund.getPaymentId());
        com.drobucs.histology.payment.models.Payment payment = paymentService.getPaymentByPaymentId(refund.getPaymentId());
        if (payment == null) {
            logInfo("Cannot change status of payment. reason: Cannot find payment with such id='" + refund.getPaymentId() + "'.");
        } else {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentService.savePayment(payment);
            refundDB.setUserId(payment.getUserId());
        }
        paymentService.saveRefund(refundDB);
    }

    private void notificationPayment(@NotNull Notification notification) {
        Payment paymentYookassa = (Payment) notification.getObject();
        com.drobucs.histology.payment.models.Payment payment =
                paymentService.getPaymentByPaymentId(paymentYookassa.getId());
        if (payment == null) {
            logInfo("[notificationPayment]: models.Payment is null.");
            return;
        }
        final String event = notification.getEvent();
        if (event == null) {
            logInfo("[notificationPayment]: Notification event is null.");
            return;
        }
        if (NotificationEvent.isPaymentSucceeded(event)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentService.savePayment(payment);
            notifyBotService(payment);
            int res = userService.issueSubscription(payment.getUserId(), payment.getSubscriptionId());
            if (res != 0) {
                logInfo("Error: SUCCESS_BUT_ERROR: result is not null: res='" + res + "'.");
                payment.setStatus(PaymentStatus.SUCCESS_BUT_ERROR);
                paymentService.savePayment(payment);
            }
        } else if (NotificationEvent.isPaymentCanceled(event)) {
            logInfo("Payment canceled.");
            payment.setStatus(PaymentStatus.CANCELED);
            paymentService.savePayment(payment);
        } else if (NotificationEvent.isPaymentWaitingForCapture(event)) {
            payment.setStatus(PaymentStatus.WAITING_FOR_CAPTURE);
            paymentService.savePayment(payment);
            try {
                capture(payment.getPaymentId(), new Amount(payment.getAmount(), payment.getCurrency()));
            } catch (QueryCaptureException e) {
                logInfo("QueryCaptureException: " + e.getMessage());
            }
        } else {
            logInfo("Useless notification. event='" + notification.getEvent() + "'.");
        }
    }

    private void notifyBotService(com.drobucs.histology.payment.models.Payment payment) {
        long amount = (long)payment.getAmount();
        Request req = new RequestGet(NetworkBotService.queryNotifyNewPayment())
                .setParam("sum", Long.toString(amount));
        req.executeNotify();
    }

    @PostMapping("/yookassa/notification")
    public void yookassaNotification(HttpServletResponse response,
                                     HttpServletRequest request,
                                     @RequestBody String stringNotification) {
        response.setStatus(200);
        final String remoteAddr = request.getRemoteAddr();
        final String sNotify = stringNotification;
        new Thread(() -> {
            logInfo("Notification: " + stringNotification);
            logInfo("Yookassa remote addr: " + remoteAddr);
            boolean isYookassa = YOOKASSA_IP.contains(remoteAddr);
            logInfo("isYookassa=" + isYookassa);
            Notification notification = getNotificationFromJson(sNotify);
            if (notification == null) {
                logInfo("Notification is null.");
                return;
            }
            if (NotificationEvent.isRefundEvent(notification.getEvent())) {
                notificationRefund(notification);
            } else {
                notificationPayment(notification);
            }
        }).start();
    }

    @PostMapping("/cancel")
    public String cancelPayment(@RequestParam("paymentId") String paymentId,
                                @RequestParam("cancelCode") String code) {
        if (code == null) {
            logInfo("Cancel code is null.");
            return "Auth failed. Bad cancel code. code='null'";
        }
        if (!Objects.equals(code, paymentService.getPaymentCancelCode())) {
            logInfo("Bad cancel code='" + code + "'.");
            return "Auth failed. Bad cancel code.";
        }
        Request request = new RequestPostJson(NetworkYookassa.queryPaymentCancel(paymentId))
                .setJson("")
                .addHeader("Idempotence-Key", UUID.randomUUID().toString());
        RequestResult<String> res;
        try {
            res = request.executeString(getYookassaClient());
        } catch (CannotCreateClientException e) {
            return "Error: " + e.getMessage();
        }
        if (res.haveErrors()) {
            logInfo("Error [cancelPayment] : " + res.getMessage());
            return "Error: " + res.getMessage();
        }
        if (res.getResult() == null) {
            logInfo("No errors but result is null.");
            return "No errors but result is null.";
        }
        Payment payment = getPaymentFromJson(res.getResult());
        if (payment == null) {
            logInfo("Result json string: " + res.getResult());
            return "payment parse to null.";
        }
        return "Payment status: " + payment.getStatus();
    }

    @PostMapping("/payment-info")
    public Payment getPaymentInfo(@RequestParam("paymentId") String paymentId, HttpServletResponse httpServletResponse) {
        Request request = new RequestGet(NetworkYookassa.queryPaymentInfo(paymentId))
                .addHeader("Idempotence-Key", UUID.randomUUID().toString());
        RequestResult<String> res;
        try {
            res = request.executeString(getYookassaClient());
        } catch (CannotCreateClientException e) {
            logInfo("CannotCreateClientException: " + e.getMessage());
            return null;
        }
        if (res.haveErrors()) {
            logInfo("Error [getPaymentInfo] : " + res.getMessage());
            httpServletResponse.setStatus(401);
            return null;
        }
        if (res.getResult() == null) {
            logInfo("No errors but result is null.");
            httpServletResponse.setStatus(402);
            return null;
        }
        Payment payment = getPaymentFromJson(res.getResult());
        if (payment == null) {
            logInfo("Payment json string: " + res.getResult());
            logInfo("payment parse to null.");
            httpServletResponse.setStatus(403);
        }
        return payment;
    }

    private Notification getNotificationFromJson(@Nullable String stringNotification) {
        if (stringNotification == null) {
            logInfo("stringNotification is null.");
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Notification notification;
        try {
            notification = mapper.readValue(stringNotification, NotificationPayment.class);
            if (notification == null) {
                logInfo("No errors but object 'notification' is null.");
            }
            return notification;
        } catch (JsonProcessingException e) {
            logInfo("Cannot parse to NotificationPayment: " + e.getMessage());
        }
        try {
            notification = mapper.readValue(stringNotification, NotificationRefund.class);
            return notification;
        } catch (JsonProcessingException e) {
            logInfo("Cannot parse to NotificationRefund: " + e.getMessage());
            return null;
        }
    }


    @Nullable
    private Payment getPaymentStatus(long userId) {
        com.drobucs.histology.payment.models.Payment payment = paymentService.getPaymentByUserId(userId);
        if (payment == null) {
            logInfo("Payment is null. [userId=" + userId + "]");
            return null;
        }
        if (payment.getPaymentId() == null) {
            logInfo("Payment id is null. [userId=" + userId + "]");
            return null;
        }
        Request request = new RequestGet(NetworkYookassa.queryPaymentInfo(payment.getPaymentId()));
        RequestResult<String> res;
        try {
            res = request.executeString(getYookassaClient());
        } catch (CannotCreateClientException e) {
            logInfo("CannotCreateClientException  : " + e.getMessage());
            return null;
        }
        if (res.haveErrors()) {
            logInfo("Bad request: " + res.getMessage());
            return null;
        }
        Payment paymentYK = getPaymentFromJson(res.getResult());
        if (paymentYK == null) {
            logInfo("Yookasses payment parsed to null.");
            logInfo("Yookasses json payment string: " + res.getResult());
            return null;
        }
        return paymentYK;
    }

    @Nullable
    private String getJsonString(PaymentCreate paymentCreate) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(paymentCreate);
            if (jsonString == null) {
                logInfo("getJsonString: jsonString is null.");
                return null;
            }
        } catch (JsonProcessingException e) {
            logInfo("getJsonString: " + e.getMessage());
            return null;
        }
        return jsonString;
    }

    @Nullable
    private Payment getPaymentFromJson(@NotNull String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Payment payment = mapper.readValue(jsonString, Payment.class);
            if (payment == null) {
                logInfo("Mapper convert to payment but payment is null.");
            }
            return payment;
        } catch (JsonProcessingException e) {
            logInfo("Json Error: " + e.getMessage());
            return null;
        }
    }

    @Nullable
    private Payment sendRequestCreatePayment(PaymentCreate paymentCreate, String idempotenceKey) {
        String jsonString = getJsonString(paymentCreate);
        if (jsonString == null) {
            logInfo("sendRequestCreatePayment: jsonString is null.");
            return null;
        }
        Request request = new RequestPostJson(NetworkYookassa.queryCreatePayment())
                .setJson(jsonString)
                .addHeader("Idempotence-Key", idempotenceKey);
        RequestResult<String> res;
        try {
            res = request.executeString(getYookassaClient());
        } catch (CannotCreateClientException e) {
            logInfo("CannotCreateClientException: " + e.getMessage());
            return null;
        }
        if (res.haveErrors()) {
            logInfo("sendRequestCreatePayment: error occurred: " + res.getMessage() + ", result='" + res.getResult() + "'.");
            return null;
        }
        if (res.getResult() == null) {
            logInfo("sendRequestCreatePayment: No errors but res.getResult() is null.");
            return null;
        }
        return getPaymentFromJson(res.getResult());
    }

    @NotNull
    private Amount buildAmount(@NotNull Subscription subscription) {
        return new Amount(Long.toString(subscription.getPrice()), currency);
    }


    @NotNull
    private PaymentCreate createPayment(@NotNull Amount amount,
                                        @NotNull String token,
                                        @Nullable String returnUrl,
                                        @NotNull String confirmType,
                                        @NotNull Subscription subscription,
                                        @NotNull String userEmail,
                                        long userId) {
        PaymentCreate paymentCreate = new PaymentCreate();
        paymentCreate.setAmount(amount);
        paymentCreate.setPaymentToken(token);
        if (returnUrl != null && !returnUrl.equals("null")) {
            paymentCreate.setConfirmation(new Confirmation(confirmType, returnUrl, null));
        } else {
            logInfo("returnUrl is null or 'null'.");
        }
        paymentCreate.setReceipt(createReceipt(userEmail, subscription));
        paymentCreate.setMerchantCustomerId(Long.toString(userId));
        paymentCreate.setDescription(subscription.getDescriptionForReceipt());
        logJson(paymentCreate);
        return paymentCreate;
    }

    private void logJson(PaymentCreate paymentCreate) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(paymentCreate);
            logInfo("PaymentCreate json: " + json);
        } catch (JsonProcessingException e) {
            logInfo("Cannot parse paymentCreate: " + e.getMessage());
        }
    }

    private long getUserId(@NotNull String login) {
        Request request = new RequestPost(NetworkUsersService.queryGetUserId(backendApiKey))
                .setParams("login", login);
        RequestResult<Long> res = request.executeLong();
        if (res == null) {
            logInfo("cannot take user id because RequestResult is null.");
            return -1L;
        }
        if (res.haveErrors()) {
            logInfo(res.getMessage());
            return -1L;
        }
        if (res.getResult() == null) {
            logInfo("getUserId: No errors but res.getResult() is null.");
            return -1L;
        }
        return res.getResult();
    }

    @PostMapping("/get-yookassa-credentials")
    private Credentials getYookassaCredentials(@RequestParam("login") String login,
                                               @RequestParam("apiKey") String apiKey) {
        if (isBadApiKey(login, apiKey)) {
            logInfo("Bad api Key.");
            return Credentials.ERROR;
        }
        Shop shop = paymentService.getShop();
        if (shop == null) {
            logInfo("[getYookassaCredentials] Shop is null.");
            return Credentials.ERROR;
        }
        return Shop.convertToCredentials(shop);
    }

    // if price changed or data about payment lost use it, otherwise use /create-refund/full-amount
    @PostMapping("/create-refund/specific-amount")
    private String createRefund(@RequestParam("paymentId") String paymentId,
                                @RequestParam("amountValue") Double amountValue,
                                @RequestParam("currency") String currency,
                                @RequestParam("refundCode") String refundCode,
                                @RequestParam(value = "description", required = false) String description) {
        try {
            paymentService.refundAuth(refundCode);
            notNullValidation(names("paymentId", "amountValue", "currency"), paymentId, amountValue, currency);
        } catch (RefundAuthException | ValidationNullParamException e) {
            return e.getMessage();
        }
        Amount amount = new Amount(amountValue, currency);
        RefundCreate refundCreate = paymentService.createRefundCreate(paymentId, amount, description);
        Refund refund;
        try {
            refund = paymentService.createRefund(refundCreate, getYookassaClient());
        } catch (CreateRequestException | CannotCreateClientException e) {
            return "Error: " + e.getMessage();
        }
        return createRefundSuccess(refund.getId());
    }

    // took amount from db.
    // No receipt needed
    @PostMapping("/create-refund/full-amount")
    private String createRefund(@RequestParam("paymentId") String paymentId,
                                @RequestParam("refundCode") String refundCode,
                                @RequestParam(value = "description", required = false) String description) {
        try {
            paymentService.refundAuth(refundCode);
            notNullValidation(names("paymentId", "refundCode"), paymentId, refundCode);
        } catch (RefundAuthException | ValidationNullParamException e) {
            return e.getMessage();
        }

        com.drobucs.histology.payment.models.Payment payment = paymentService.getPaymentByPaymentId(paymentId);
        if (payment == null) {
            return "Cannot find payment with such id='" + paymentId + "'.";
        }
        RefundCreate refundCreate = paymentService.createRefundCreate(paymentId, buildAmount(payment), description);
        Refund refund;
        try {
            refund = paymentService.createRefund(refundCreate, getYookassaClient());
        } catch (CreateRequestException | CannotCreateClientException e) {
            return "Error: " + e.getMessage();
        }
        return createRefundSuccess(refund.getId());
    }

    private String createRefundSuccess(@NotNull String refundId) {
        return "Ok. Refund created. refundId='" + refundId + "'.";
    }

    @NotNull
    private OkHttpClient getYookassaClient() throws CannotCreateClientException {
        OkHttpClient client = paymentService.getClient();
        if (client == null) {
            throw new CannotCreateClientException("Cannot get yookasses client.");
        }
        return client;
    }

    private Amount buildAmount(com.drobucs.histology.payment.models.Payment payment) {
        Amount amount = new Amount();
        amount.setValue(Double.toString(payment.getAmount()));
        amount.setCurrency(payment.getCurrency());
        return amount;
    }
}
