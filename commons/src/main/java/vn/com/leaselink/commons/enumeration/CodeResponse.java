package vn.com.leaselink.commons.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CodeResponse implements ICodeResponse{

    public ICodeResponse codeResponse;

    @Override
    public String getCode() {
        return codeResponse.getCode();
    }

    @Override
    public String getMessage() {
        return codeResponse.getMessage();
    }

    public enum SuccessCode implements ICodeResponse {
        SUCCESS("2000", "success"),
        EVENT_SOURCE_SUCCESS("DATABASE_2000", "success"),
        TOKEN_VALID("TOKEN_2000", "Token valid"),
        ROOT_CREATE_SUCCESS("ROOT_CREATE_2000", "create root account success"),
        USER_NOT_EXISTED("LOGIN_2001", "User not existed need register"),
        REQUIRE_SALE_ADD_INFO("SALE_REGISTER_2000", "Require sale add information"),
        PACKAGE_EXISTS_PRODUCT("PACKAGE_EXISTS_PRODUCT_2001", "package exists product"),
        // Contract
        SHARE_CONTRACT_SUCCESS("CONTRACT_2004", "share contract success"),
        ASSIGN_CONTRACT_SUCCESS("CONTRACT_2005", "assign contract success"),
        CREATE_CONTRACT_REQUEST_SUCCESS("CONTRACT_REQUEST_2000", "create contract request success"),
        UPDATE_CONTRACT_REQUEST_SUCCESS("CONTRACT_REQUEST_2001", "update contract request success"),
        GET_CONTRACT_REQUEST_DETAIL_SUCCESS("CONTRACT_REQUEST_2002", "get contract request detail success"),
        GET_CONTRACT_REQUEST_LIST_SUCCESS("CONTRACT_REQUEST_2003", "get contract request list success"),
        GET_STATUS_CREATE_CONTRACT_PROCESSING("PROCESSING_2000", "Processing create contract"),
        GET_STATUS_DELETE_CONTRACT_PROCESSING("PROCESSING_2001", "Processing delete contract"),
        ;

        private String code;
        private String message;

        SuccessCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public enum SimulationSuccessCode implements ICodeResponse {
        UNKNOWN("500", "unknown"),
        SUCCESS("200", "success"),
        GET_ALL_USER_SUCCESS("2035", "Get all user success"),
        ADMIN_GET_ACCOUNT_WITH_DD_TYPE("2391", "Admin get account with dd type success"),
        TRANSACTION_SUCCESS("SIMULATION_TRANSACTION_2001", "Executed transaction success");

        private String code;
        private String message;

        SimulationSuccessCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public static SimulationSuccessCode safeValuesOf(String code) {
            for (SimulationSuccessCode status : values()) {
                if (status.getCode().equals(code)) {
                    return status;
                }
            }
            return UNKNOWN;
        }
    }

    public enum SimulationFailedCode implements ICodeResponse {
        TRANSACTION_FAILED("SIMULATION_TRANSACTION_4001", "Executed transaction failed"),
        FAILED("400", "failed");


        private String code;
        private String message;

        SimulationFailedCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public enum ServerErrorCode implements ICodeResponse {
        SERVER_ERROR(5000, "Unknown error from server"),
        DATABASE_ERROR(5001, "Error from database"),
        INTERNAL_SERVER(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        BAD_GATEWAY(5020, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        HTTP_VER_NOT_SUPPORT(505, "HTTP Version Not Supported"),
        VARIANT_NEGOTIATES(506, "Variant Also Negotiates"),
        INSUFFICIENT(507, "Insufficient Storage"),
        LOOP_DETECTED(508, "Loop Detected"),
        NOT_EXTENDED(510, "Not Extended"),
        NETWORK_AUTHEN_REQUIRED(511, "Network Authentication Required"),
        CHECK_POINT(103, "Checkpoint"),
        METHO_FAILURE(420, "Method Failure"),
        IM_A_FOX(419, "I'm a fox (Smoothwall/Foxwall)"),
        ENHANCE_UR_CALM(420, "Enhance Your Calm (Twitter)"),
        BLOCK_BY_WINDOWS(450, "Blocked by Windows Parental Controls (Microsoft)"),
        INVAILID_TOKEN(498, "Invalid Token (Esri)"),
        TOKEN_REQUIRED(499, "Token Required (Esri)"),
        ANTIVIRUS(499, "Request has been forbidden by antivirus"),
        BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),
        SITE_FROZEN(530, "Site is frozen"),
        LOGIN_TIMEOUT(440, "Login Timeout"),
        RETRY_WITH(449, "Retry With"),
        REDIRECT(451, "Redirect"),
        NO_RESPONSE(444, "No Response"),
        SSL_CERT_ERR(495, "SSL Certificate Error"),
        SSL_CERT_REQ(496, "SSL Certificate Required"),
        HTTP_REQUEST_SENT_HTTPS_PORT(497, "HTTP Request Sent to HTTPS Port"),
        CLIENT_CLOSE_REQUEST(499, "Client Closed Request"),
        UNKNOWN_ERROR(520, "Unknown Error"),
        SERVER_DOWN(521, "Web Server Is Down"),
        CONNECTION_TIMEOUT(522, "Connection Timed Out"),
        ORIGIN_UNREACHABLE(523, "Origin Is Unreachable"),
        TIMEOUT_OCCURRED(524, "A Timeout Occurred"),
        SSL_HANDSHAKE_FAILED(525, "SSL Handshake Failed"),
        INVALID_SSL(526, "Invalid SSL Certificate"),
        RAILGUN_ERROR(527, "Railgun Error");

        private int code;
        private String message;

        ServerErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getCode() {
            return String.valueOf(code);
        }

        public int getIntCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

}
