package vn.com.leaselink.commons.enumeration;

public enum HttpStatusOutcome {
        INFORMATIONAL(100, 199),  // 1xx status codes
        SUCCESS(200, 299),        // 2xx status codes
        REDIRECTION(300, 399),    // 3xx status codes
        CLIENT_ERROR(400, 499),   // 4xx status codes
        SERVER_ERROR(500, 599);   // 5xx status codes

        private final int minStatus;
        private final int maxStatus;

        /**
         * Khởi tạo một HttpStatusOutcome với khoảng status code.
         *
         * @param minStatus Giá trị status code nhỏ nhất
         * @param maxStatus Giá trị status code lớn nhất
         */
        HttpStatusOutcome(int minStatus, int maxStatus) {
            this.minStatus = minStatus;
            this.maxStatus = maxStatus;
        }

        /**
         * Lấy HttpStatusOutcome tương ứng với status code cụ thể.
         *
         * @param statusCode HTTP status code
         * @return HttpStatusOutcome tương ứng
         */
        public static HttpStatusOutcome fromStatusCode(int statusCode) {
            if (statusCode < 200) return INFORMATIONAL;
            if (statusCode < 300) return SUCCESS;
            if (statusCode < 400) return REDIRECTION;
            if (statusCode < 500) return CLIENT_ERROR;
            return SERVER_ERROR;
        }

        /**
         * Kiểm tra xem status code có thuộc nhóm này không.
         *
         * @param statusCode HTTP status code cần kiểm tra
         * @return true nếu status code thuộc nhóm này, false nếu không
         */
        public boolean contains(int statusCode) {
            return statusCode >= minStatus && statusCode <= maxStatus;
        }

        /**
         * @return Giá trị status code nhỏ nhất trong nhóm
         */
        public int getMinStatus() {
            return minStatus;
        }

        /**
         * @return Giá trị status code lớn nhất trong nhóm
         */
        public int getMaxStatus() {
            return maxStatus;
        }
}
