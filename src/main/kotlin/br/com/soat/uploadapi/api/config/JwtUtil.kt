package br.com.soat.uploadapi.api.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.InputStream
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.public-key}") private var publicKeyPath: String
) {


    lateinit var publicKey: RSAPublicKey

    @PostConstruct
    fun init() {
        try {
            val resource: InputStream = this.javaClass.classLoader.getResourceAsStream(publicKeyPath)
                ?: throw IllegalArgumentException("Arquivo de chave pública não encontrado em $publicKeyPath")

            val keyBytes = resource.bufferedReader().readText()
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .trim()

            val decodedKey = Base64.getDecoder().decode(keyBytes)
            val keySpec = X509EncodedKeySpec(decodedKey)
            val keyFactory = KeyFactory.getInstance("RSA")
            publicKey = keyFactory.generatePublic(keySpec) as RSAPublicKey

            println("Chave pública carregada com sucesso.")
        } catch (e: Exception) {
            println("Erro ao carregar chave pública: ${e.message}")
            e.printStackTrace()
        }
    }

    fun extractClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(publicKey)
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