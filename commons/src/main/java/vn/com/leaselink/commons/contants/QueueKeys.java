package vn.com.leaselink.commons.contants;

public class QueueKeys {

    public enum SocketQueue {
        SEND_TO_SOCKET("send_to_socket");
        private static final String prefix = "socket_";
        private String queueName;

        SocketQueue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }


    public enum NotificationQueue {
        SEND_SMS(NotificationQueue.prefix + "send_sms"),
        SEND_EMAIL(NotificationQueue.prefix + "send_email"),
        SEND_APN(NotificationQueue.prefix + "send_apn"),
        SEND_FIREBASE(NotificationQueue.prefix + "send_firebase"),
        SEND_FIREBASE_TOPIC(NotificationQueue.prefix + "send_firebase_topic"),
        PUBLISH_MQTT(NotificationQueue.prefix + "publish_mqtt");

        private static final String prefix = "notification_";
        private String queueName;

        NotificationQueue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }


    public enum SimulationQueue {
        CHECK_USER_EXISTED(SimulationQueue.prefix + "check_user_existed"),
        TRANSFER_QUEUE(SimulationQueue.prefix + "transfer"),
        TRANSFER_LIST_QUEUE(SimulationQueue.prefix + "transfer_list"),
        MAIN_CARD_REQUESTER(SimulationQueue.prefix + "main_card_requester"),
        SUB_CARD_REQUESTER(SimulationQueue.prefix + "sub_card_requester");

        private static final String prefix = "simulation_";
        private String queueName;

        SimulationQueue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }

    public enum TransactionQueue {
        TRANSFER_QUEUE(TransactionQueue.prefix + "transfer"),
        TRANSFER_LIST_QUEUE(TransactionQueue.prefix + "transfer_list"),
        TOP_UP_QUEUE(TransactionQueue.prefix + "topUp"),
        ADD_HISTORY(TransactionQueue.prefix + "add_history"),
        BATCH_JOB_AUTO_GOAL_SAVE_QUEUE(TransactionQueue.prefix + "batch_job_auto_goal_save"),
        BATCH_JOB_TERM_DEPOSIT_MATURITY_QUEUE(TransactionQueue.prefix + "batch_job_deposit_maturity_save"),
        PAY_INTEREST_TERM_DEPOSIT_QUEUE(TransactionQueue.prefix + "pay_interest_term_deposit");

        private static final String prefix = "transaction_";
        private String queueName;

        TransactionQueue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }

    public enum AccountQueue {
        UPDATE_GOAL_SAVE_ACCOUNT_STATUS(AccountQueue.prefix + "update_goal_save_account_status"),
        BALANCE_CHANGE(AccountQueue.prefix + "balance_change_queue"),
        CREATE_FD_ACCOUNT_QUEUE(AccountQueue.prefix + "create_fd_account"),
        UPDATE_REQUEST_MAIN_CARD_QUEUE(AccountQueue.prefix + "update_request_main_card_queue"),
        UPDATE_REQUEST_SUB_CARD_QUEUE(AccountQueue.prefix + "update_request_sub_card_queue");
        private static final String prefix = "account_";
        private String queueName;

        AccountQueue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }

    public enum EventSourceQueue {
        NO_SQL_INTEGRATE_QUEUE(EventSourceQueue.prefix + "no_sql_integrate_queue");
        private static final String prefix = "event_source_";
        private String queueName;

        EventSourceQueue(String queueName) {
            this.queueName = queueName;
        }

        public String getQueueName() {
            return queueName;
        }
    }

}
