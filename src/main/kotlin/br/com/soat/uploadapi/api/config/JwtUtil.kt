package br.com.soat.uploadapi.api.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.security.KeyFactory
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.security.interfaces.RSAPublicKey
import java.util.*

@Component
class JwtUtil {

    private fun loadPublicKey(): RSAPublicKey {
        val publicKeyPem = String(Files.readAllBytes(Paths.get("/home/lucas/Documentos/publickey.pem")))

        val publicKeyPEM = publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "").trim()

        val cleanPrivateKey = publicKeyPEM.replace("\\s+".toRegex(), "")

        val decoded = Base64.getDecoder().decode(cleanPrivateKey)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey: RSAPublicKey = keyFactory.generatePublic(java.security.spec.X509EncodedKeySpec(decoded)) as RSAPublicKey

        return publicKey
    }

    fun extractClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(loadPublicKey())
                .build()
                .parseSignedClaims(token)
                .payload
        }catch (e: Exception){
            null
        }
    }

    fun extractEmail(token: String): String? {
       return extractClaims(token)?.subject ?: return null
    }
}