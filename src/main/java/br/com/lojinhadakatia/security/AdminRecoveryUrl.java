package br.com.lojinhadakatia.security;

import java.net.URI;

/** Reject incomplete or credential-bearing recovery URLs before creating a token. */
final class AdminRecoveryUrl {
    private AdminRecoveryUrl() {}

    static boolean valid(String value) {
        if (value == null || value.isBlank()) return false;
        try {
            URI uri=URI.create(value);
            return "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost()!=null
                && uri.getUserInfo()==null && uri.getRawQuery()==null && uri.getRawFragment()==null
                && uri.getPath()!=null && !uri.getPath().isBlank() && !"/".equals(uri.getPath());
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
