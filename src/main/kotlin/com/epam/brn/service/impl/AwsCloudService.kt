package com.epam.brn.service.impl

import com.amazonaws.util.Base64
import com.epam.brn.config.AwsConfig
import com.epam.brn.service.CloudService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@ConditionalOnProperty(name = ["cloud.provider"], havingValue = "aws")
@Service
class AwsCloudService(@Autowired private val awsConfig: AwsConfig) : CloudService {

    private final val mapperIndented = ObjectMapper()
    init {
        mapperIndented.enable(SerializationFeature.INDENT_OUTPUT)
    }

    override fun listBucket(): String = awsConfig.bucketLink

    override fun signatureForClientDirectUpload(fileName: String?): Map<String, Any> {
        val conditions = awsConfig.Conditions()
        val policy: String = policy(conditions)
        val signature = sign(conditions.date, policy)

        val inputs = arrayListOf(mapOf("policy" to policy), mapOf("x-amz-signature" to signature))
        for (condition in listOf(
            conditions.uploadKey,
            conditions.acl,
            conditions.uuid,
            conditions.serverSideEncryption,
            conditions.credential,
            conditions.algorithm,
            conditions.dateTime,
            conditions.successActionRedirect,
            conditions.contentTypeStartsWith,
            conditions.metaTagStartsWith
        )) {
            if (condition.second.isNotEmpty())
                inputs.add(mapOf(condition))
        }
        return mapOf("action" to awsConfig.bucketLink, "input" to inputs)
    }

    private fun policy(conditions: AwsConfig.Conditions): String {
        val includedFields: ArrayList<Any> = ArrayList()
        for (condition in listOf(
            conditions.bucket,
            conditions.acl,
            conditions.uploadKeyStartsWith,
            conditions.uuid,
            conditions.serverSideEncryption,
            conditions.credential,
            conditions.algorithm,
            conditions.dateTime,
            conditions.successActionRedirect,
            conditions.contentTypeStartsWith,
            conditions.metaTagStartsWith
        )) {
            if (condition.second.isNotEmpty()) {
                if (condition in arrayOf(conditions.uploadKeyStartsWith, conditions.contentTypeStartsWith, conditions.metaTagStartsWith))
                    includedFields.add(arrayOf("starts-with", "\$${condition.first}", condition.second))
                else
                    includedFields.add(hashMapOf(condition))
            }
        }
        val policy = hashMapOf(
            conditions.expiration,
            "conditions" to includedFields
        )
        return toJsonBase64(policy)
    }

    private fun toJsonBase64(rawObject: Any): String {
        val json = mapperIndented.writeValueAsString(rawObject)
        return base64Encoded(json.toByteArray())
    }

    private fun sign(date: String, policy: String): String {
        val signature = getSignatureKey(awsConfig.secretAccessKey, date, awsConfig.region, awsConfig.serviceName)
        val hmacSHA256 = hmacSHA256(policy, signature)
        return toHex(hmacSHA256)
    }

    private fun getSignatureKey(key: String, dateStamp: String, regionName: String, serviceName: String): ByteArray {
        val kSecret = ("AWS4$key").toByteArray()
        val kDate = hmacSHA256(dateStamp, kSecret)
        val kRegion = hmacSHA256(regionName, kDate)
        val kService = hmacSHA256(serviceName, kRegion)
        return hmacSHA256("aws4_request", kService)
    }

    private fun base64Encoded(bytes: ByteArray): String = Base64.encodeAsString(*bytes)

    private fun hmacSHA256(data: String, key: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data.toByteArray())
    }

    private fun toHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
}