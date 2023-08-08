package org.ticker.auth.model;

import lombok.Data;
import lombok.NonNull;

public class User {
    @Data
    public static class TickerUser {
        @NonNull
        private String username;
        @NonNull
        private String password;
        @NonNull
        private String email;
    }
}
