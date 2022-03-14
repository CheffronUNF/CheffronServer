package edu.unf.cheffron.server.service.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import edu.unf.cheffron.server.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Endpoint {

    protected static final Logger LOG = Logger.getLogger("CheffronWebService");
    protected static final Gson GSON = new Gson();

    static protected PrivateKey privateKey;
    static protected PublicKey publicKey;

    static protected Cipher rsaEncrypt;
    static protected Cipher rsaDecrypt;

    static {
        try {
            // code to generate RS256 key pair
//            KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
//            PrintWriter writer = new PrintWriter("/home/elian/javaprojects/school_projects/CheffronServer/jwtRS256.key");
//            writer.write(new String(Base64.getEncoder().encode(keyPair.getPrivate().getEncoded())));
//            writer.flush();
//            writer.close();
//            writer = new PrintWriter("/home/elian/javaprojects/school_projects/CheffronServer/jwtRS256.key.pub");
//            writer.write(new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded())));
//            writer.flush();
//            writer.close();

            byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readAllBytes(Path.of("jwtRS256.key")));
            byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readAllBytes(Path.of("jwtRS256.key.pub")));

            KeyFactory factory = KeyFactory.getInstance("RSA");

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            privateKey = factory.generatePrivate(privateKeySpec);
            publicKey = factory.generatePublic(publicKeySpec);
        } catch (IOException | NullPointerException ex) {
            LOG.log(Level.SEVERE, "FATAL! Public and private keys not found in package! Authentication impossible.");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOG.log(Level.SEVERE, "FATAL! Unable to read public/private key pair!");
            ex.printStackTrace();
        }

        try {
            rsaEncrypt = Cipher.getInstance("RSA");
            rsaEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            rsaDecrypt = Cipher.getInstance("RSA");
            rsaDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            LOG.log(Level.SEVERE, "FATAL! Could not initialize password Cipher. Passwords cannot be encrypted!", e);
            e.printStackTrace();
        }
    }

    protected String createJWTToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    protected User authenticateUser(HttpExchange exchange) {
        String jwt = exchange.getRequestHeaders().getFirst("Bearer");

        // validate jwt
        // check if issued date is after token expiration date for this user
        // return User object from database if authentication successful
        // return null if null successful
        // TODO : handle database authentication

        return null;
    }

    protected JsonObject getJsonBody(HttpExchange exchange) {
        try {
            String line = new String(exchange.getRequestBody().readAllBytes());
            JsonElement element = JsonParser.parseString(line);
            if (element == null || element.isJsonNull() || !element.isJsonObject()) {
                respondError(exchange, 400, "Invalid json received.");
            } else {
                // return appropriate json object
                return element.getAsJsonObject();
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Could not read line from request body!", ex);
            respondError(exchange, 500, "Could not read request");
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.WARNING, "Received invalid JSON from client!", ex);
            respondError(exchange, 400, "Invalid json received: " + ex.getMessage());
        }

        // errored out
        return null;
    }

    protected void respond(HttpExchange exchange, int statusCode, String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        try {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream body = exchange.getResponseBody();

            body.write(bytes);
            body.flush();
            exchange.close();
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Could not respond to client!", ex);
        }
    }

    protected void respond(HttpExchange exchange, int statusCode, JsonObject body) {
        String json = GSON.toJson(body);
        respond(exchange, statusCode, json);
    }

    protected void respondError(HttpExchange exchange, int statusCode, String error) {
        JsonObject object = new JsonObject();
        object.addProperty("error", error);
        respond(exchange, statusCode, object);
    }
}
