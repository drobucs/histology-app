package com.drobucs.histology.payment.services;

import com.drobucs.base.web.request.Request;
import com.drobucs.base.web.request.RequestPostJson;
import com.drobucs.base.web.request.RequestResult;
import com.drobucs.histology.payment.controllers.exceptions.CreateRequestException;
import com.drobucs.histology.payment.models.*;
import com.drobucs.histology.payment.models.yookassa.client.Client;
import com.drobucs.histology.payment.models.yookassa.exceptions.UnknownPaymentStatusException;
import com.drobucs.histology.payment.models.yookassa.payment.Amount;
import com.drobucs.histology.payment.models.yookassa.refund.Refund;
import com.drobucs.histology.payment.models.yookassa.refund.create.RefundCreate;
import com.drobucs.histology.payment.network.NetworkYookassa;
import com.drobucs.histology.payment.repositories.*;
import com.drobucs.histology.payment.services.exceptions.RefundAuthException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final String SHOP_STATE_NAME = "main-state";
    private static final String PAYMENT_CANCEL_CODE_NAME = "cancel-code";
    private static final String REFUND_CODE_NAME = "refund-code";

    private final Logger logger = LoggerFactory.getLogger("PaymentService");
    private final PaymentRepository paymentRepository;
    private final ShopRepository shopRepository;
    private final ShopStateRepository shopStateRepository;
    private final CancelCodeRepository cancelCodeRepository;
    private final RefundCodeRepository refundCodeRepository;
    private final RefundRepository refundRepository;

    @Nullable
    public Payment savePayment(@NotNull Long userId,
                               @NotNull String paymentId,
                               @Nullable String paymentMethodType,
                               double amount,
                               @NotNull String currency,
                               @NotNull String uuid,
                               long subscriptionId,
                               @NotNull String status,
                               boolean needConfirmation) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPaymentId(paymentId);
        payment.setPaymentMethodType(paymentMethodType);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        try {
            payment.setStatus(PaymentStatus.convertFromYookassa(status));
        } catch (UnknownPaymentStatusException e) {
            payment.setStatus(PaymentStatus.UNKNOWN);
        }
        payment.setUuid(uuid);
        payment.setSubscriptionId(subscriptionId);
        payment.setNeedConfirmed(needConfirmation);
        return paymentRepository.save(payment);
    }

    @Nullable
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Nullable
    public Payment getPaymentByUserId(long userId) {
        return paymentRepository.findFirstByUserIdOrderByPaymentTimeDesc(userId);
    }

    @Nullable
    public Payment getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findPaymentByPaymentId(paymentId);
    }

    @Nullable
    public Shop getShop() {
        ShopState state = shopStateRepository.getShopStateByShopStateName(SHOP_STATE_NAME);
        if (state == null) {
            logger.info("No shopState with such shopStateName='" + SHOP_STATE_NAME + "'.");
            return null;
        }
        Shop shop = shopRepository.getShopByShopName(state.getActiveShop());
        if (shop == null) {
            logger.info("No shop with such shopName='" + state.getActiveShop() + "'.");
        }
        return shop;
    }

    @Nullable
    public String getPaymentCancelCode() {
        CancelCode code = cancelCodeRepository.getCancelCodeByCancelCodeName(PAYMENT_CANCEL_CODE_NAME);
        if (code == null) {
            logger.info("CancelCode is null(No cancel code with such name='" + PAYMENT_CANCEL_CODE_NAME + "').");
            return null;
        }
        if (code.getCode() == null) {
            logger.info("Cancel code is null(string value is null).");
        }
        return code.getCode();
    }

    @Nullable
    public String getRefundCode() {
        RefundCode code = refundCodeRepository.getRefundCodeByRefundCodeName(REFUND_CODE_NAME);
        if (code == null) {
            logger.info("RefundCode is null.(No refund code with such name='" + REFUND_CODE_NAME + "')");
            return null;
        }
        if (code.getRefundCode() == null) {
            logger.info("Refund code is null(string value is null).");
        }
        return code.getRefundCode();
    }

    @NotNull
    public RefundCreate createRefundCreate(@NotNull String paymentId, @NotNull Amount amount, @Nullable String description) {
        RefundCreate refund = new RefundCreate();
        refund.setPaymentId(paymentId);
        refund.setAmount(amount);
        refund.setDescription(description);
        return refund;
    }

    public void refundAuth(@Nullable String refundCode) throws RefundAuthException {
        String tag = "refundAuth";
        if (refundCode == null) {
            log(tag, "refundAuth: refundCode is null.");
            throw new RefundAuthException("Bad auth code. code='null'");
        }
        if (!Objects.equals(refundCode, getRefundCode())) {
            log(tag, "Bad auth code: refundCode='" + refundCode + "', actualCode='" + getRefundCode() + "'.");
            throw new RefundAuthException("Bad auth code.");
        }
    }

    @NotNull
    public Refund createRefund(@NotNull RefundCreate refundCreate, OkHttpClient client) throws CreateRequestException {
        String tag = "createRefund";
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(refundCreate);
            if (json == null) {
                throw new CreateRequestException(log(tag, "RefundCreate parsed ot json but json string is null."));
            }
        } catch (JsonProcessingException e) {
            throw new CreateRequestException(log(tag, "Cannot parse to json: " + e.getMessage()));
        }
        Request request = new RequestPostJson(NetworkYookassa.queryCreateRefund())
                .setJson(json)
                .addHeader("Idempotence-Key", UUID.randomUUID().toString());
        RequestResult<String> res = request.executeString(client);
        if (res.haveErrors()) {
            throw new CreateRequestException(log(tag, "Request result have errors: " + res.getMessage()));
        }
        if (res.getResult() == null) {
            throw new CreateRequestException(log(tag, "No errors but result is null."));
        }
        Refund refund;
        try {
            refund = mapper.readValue(res.getResult(), Refund.class);
            if (refund == null) {
                throw new CreateRequestException(log(tag, "Mapper parse object from json string but object is null. json='" + res.getResult() + "'."));
            }
        } catch (JsonProcessingException e) {
            throw new CreateRequestException(log(tag, "Cannot parse from json: " + e.getMessage()));
        }
        return refund;
    }

    @NotNull
    private String log(@Nullable Object msg) {
        return log("log", msg);
    }

    @NotNull
    private String log(String tag, Object msg) {
        String str = "[" + tag + "]: " + msg;
        logger.info(str);
        return str;
    }

    public com.drobucs.histology.payment.models.Refund saveRefund(com.drobucs.histology.payment.models.Refund refund) {
        return refundRepository.save(refund);
    }

    public OkHttpClient getClient() {
        Shop shop = getShop();
        if (shop == null) {
            log("Cannot get shop. Shop is null.");
            return null;
        }
        return Client.getClient(shop.getShopId(), shop.getAuthCenterClientId());
    }
}
