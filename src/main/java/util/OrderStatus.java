package util;

/**
 * Created by Diogo on 26/07/2017 10:23.
 */
public enum OrderStatus {
    DONE {
        @Override
        public String toString() {
            return "Done";
        }
    },
    PENDING {
        @Override
        public String toString() {
            return "Pending";
        }
    },
    CANCEL {
        @Override
        public String toString() {
            return "Cancel";
        }
    }
}
