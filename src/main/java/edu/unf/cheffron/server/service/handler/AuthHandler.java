package edu.unf.cheffron.server.service.handler;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.unf.cheffron.server.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;

public class AuthHandler extends Endpoint implements HttpHandler
{
    private static final String AuthHeader = "Authorization";

    private Cipher cipher;

    public AuthHandler() {
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            LOG.log(Level.SEVERE, "FATAL! Could not initialize password Cipher. Passwords cannot be encrypted!", e);
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        switch (exchange.getRequestMethod()) {
            case "GET":
                Login(exchange);
                break;
            case "POST":
                break;
            case "PATCH":
                break;
            case "DELETE":
                break;
            default:
                throw new Error("Unexpected request type");
        }
    }

    private void Login(HttpExchange exchange)
    {
        var headers = exchange.getRequestHeaders();

        if (!headers.containsKey(AuthHeader))
        {
            respondError(exchange, 401, "No Authorization header");
            return;
        }

        var auth = headers.get(AuthHeader);

        if (auth.isEmpty())
        {
            respondError(exchange, 401, "No username or password");
            return;
        }

        String[] userpass;

        try
        {
            // remove the "Basic " from the string
            String basic = auth.get(0);
            String credentials = basic.substring(6, basic.length());
            var encoded = DecodeBase64(credentials);

            userpass = encoded.split(":");
        }
        catch (Exception ex)
        {
            respondError(exchange, 401, "Malformed Authorization header");
            ex.printStackTrace();
            return;
        }

        if (userpass.length != 2)
        {
            respondError(exchange, 401, "Incorrect username or password");
            return;
        }

        String username = userpass[0];

        try {
            String encryptedPassword = new String(Base64.getEncoder().encode(cipher.doFinal(userpass[1].getBytes(StandardCharsets.UTF_8))));

            String userId = UserRepository.getUserRepository().validateUserPassword(username, encryptedPassword);

            if (userId == null) {
                respondError(exchange, 401, "Login failed");
            } else {
                String jwt = createJWTToken(userId, username);

                JsonObject response = new JsonObject();
                response.addProperty("jwt", jwt);

                respond(exchange, 200, response);
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            respondError(exchange, 500, "Internal server error encountered during password encryption");
            e.printStackTrace();
        } catch (SQLException e) {
            respondError(exchange, 500, "Internal server error encountered when validating login");
            e.printStackTrace();
        }
    }

    private String DecodeBase64(String str)
    {
        return new String(java.util.Base64.getDecoder().decode(str.getBytes()));
    }

    private String createJWTToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
