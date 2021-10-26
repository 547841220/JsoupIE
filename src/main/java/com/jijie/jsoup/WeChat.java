package com.jijie.jsoup;

import okhttp3.HttpUrl;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

public class WeChat {

    public static PrivateKey privateKey = null;
    public static final String prefix = "https://api.mch.weixin.qq.com/";

    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        WeChat weChat = new WeChat();

        //1.要有商户api证书，商户API证书的压缩包中包含了签名必需的私钥
        //私钥文件路径
        String filePath = "c://jijie//jijie.txt";
        privateKey = getPrivateKey(filePath);//这里只做模拟

        //2.组装请求url
        String url = weChat.generateUrl();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        //设置请求头
        HttpUrl httpurl = HttpUrl.parse(url);
        //get请求
        String singInfo = weChat.getToken("GET", httpurl, "");
        httpGet.addHeader("Authorization","WECHATPAY2-SHA256-RSA2048"+singInfo);
        //实际发起调用
        CloseableHttpResponse execute = httpClient.execute(httpGet);







    }

    //请求url设置
    public String generateUrl() {
        long time = System.currentTimeMillis();
        String currentTime = String.valueOf(time);

        String suijishu = "54785465448";


        return prefix + "GET\n"+"/v3/certificates\n/"+currentTime+"\n/"+suijishu+"\n/"+"\n";
    }

    /**
     * 获取私钥。
     *
     * @param filename 私钥文件路径  (required)
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKey(String filename) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
        try {
            String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效的密钥格式");
        }
    }

    String getToken(String method, HttpUrl url, String body) throws Exception {
        String nonceStr = "your nonce string";
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));

        String yourMerchantId = "你的商户id";
        /*如何查看证书序列号？
        登录商户平台【API安全】->【API证书】->【查看证书】，可查看商户API证书序列号。*/
        String yourCertificateSerialNo = "xxxxxxxxxxxxxx";

        return "mchid=\"" + yourMerchantId + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + yourCertificateSerialNo + "\","
                + "signature=\"" + signature + "\"";
    }

    String sign(byte[] message) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message);

        return Base64.getEncoder().encodeToString(sign.sign());
    }

    String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }


}
