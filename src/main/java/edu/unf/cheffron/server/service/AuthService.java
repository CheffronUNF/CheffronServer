package edu.unf.cheffron.server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class AuthService
{
    private static final Logger LOG = Logger.getLogger("CheffronWebService");

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SIZE = 128;
    
    private static final String ID = "$31$";
    private static final Pattern layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");

    private static final SecureRandom random = new SecureRandom();

    private static final int cost = 16;
    
    private static PrivateKey privateKey;

    private static JwtParser jwtParser;

    static
    {
        try
        {
            byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readAllBytes(Path.of("jwtRS256.key")));
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
    
            KeyFactory factory = KeyFactory.getInstance("RSA");

            privateKey = factory.generatePrivate(privateKeySpec);
            jwtParser = Jwts.parserBuilder().setSigningKey(privateKey).build();
        }
        catch (IOException | NullPointerException ex) 
        {
            LOG.log(Level.SEVERE, "FATAL! Public and private keys not found in package! Authentication impossible.");
            System.exit(1);
        } 
        catch (NoSuchAlgorithmException | InvalidKeySpecException ex) 
        {
            LOG.log(Level.SEVERE, "FATAL! Unable to read public/private key pair!");
            System.exit(1);
        }
    }

    public static String createJWT(String userId, String username) 
    {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public static String authenticateJWT(String jws) 
    {
        try
        {
            Jws<Claims> jwt = jwtParser.parseClaimsJws(jws);
            String userId = jwt.getBody().getSubject();

            return userId;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private static int iterations(int cost)
    {
        if ((cost < 0) || (cost > 30))
        {
            throw new IllegalArgumentException("cost: " + cost);
        }

        return 1 << cost;
    }

    public static String hash(char[] password)
    {
        byte[] salt = new byte[SIZE / 8];
        random.nextBytes(salt);

        byte[] dk = pbkdf2(password, salt, 1 << cost);
        byte[] hash = new byte[salt.length + dk.length];

        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);

        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return ID + cost + '$' + enc.encodeToString(hash);
    }

    public static boolean authenticate(char[] password, String token)
    {
        Matcher m = layout.matcher(token);

        if (!m.matches())
        {
            throw new IllegalArgumentException("Invalid token format");
        }

        int iterations = iterations(Integer.parseInt(m.group(1)));
        byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
        byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);

        byte[] check = pbkdf2(password, salt, iterations);

        int zero = 0;
        for (int idx = 0; idx < check.length; ++idx)
        {
            zero |= hash[salt.length + idx] ^ check[idx];
        }

        return zero == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations)
    {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);

        try
        {
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);

            return f.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException ex) 
        {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        }
        catch (InvalidKeySpecException ex) 
        {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }
}