package com.imcczy.tencent2016c_keygen;

import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.System;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.CRC32;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by imcczy on 16/9/4.
 */

public class keygen {
    public static String b(String arg7) {
        int v1 = 0;
        String v0 = null;
        try {
            byte[] v2 = MessageDigest.getInstance("MD5").digest(arg7.getBytes());
            StringBuffer v3 = new StringBuffer();
            int v4 = v2.length;
            while (v1 < v4) {
                int v5 = v2[v1] & 255;
                if (v5 < 16) {
                    v3.append(0);
                }

                v3.append(Integer.toHexString(v5));
                ++v1;
            }

            v0 = v3.toString();
        } catch (NoSuchAlgorithmException v1_1) {
            v1_1.printStackTrace();
        }
        return v0;
    }

    public static String decrypt_id(String android_id){
        byte[] id = android_id.getBytes();
        byte[] sha1id = byteMerger(id,new byte[] {0x00});
        byte[] byte_id = null;
        byte[] ciphertext = new byte[16];
        byte[] passwd = new byte[16];

        try {
            byte_id = MessageDigest.getInstance("SHA-1").digest(sha1id);
        }catch (NoSuchAlgorithmException v1_1) {
            v1_1.printStackTrace();
        }
        if (byte_id != null && byte_id.length == 20){
            System.arraycopy(byte_id,0,ciphertext,0,16);
            System.arraycopy(byte_id,4,passwd,0,16);
        }
        byte[] de_aes_id = AES(ciphertext,passwd,Cipher.DECRYPT_MODE);
        CRC32 crc = new CRC32();
        crc.update(id);
        byte[] crc32_id = toHH((int)crc.getValue());
        byte[] des_passwd = byteMerger(new byte[]{0x3F, 0x5D, 0x23, 0x13},crc32_id);
        byte[] lisence = DES(de_aes_id,des_passwd,Cipher.ENCRYPT_MODE);
        lisence = byteMerger(lisence,crc32_id);
        String key = en_rsa(lisence);
        return key;
    }


    private static byte[] toHH(int number) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & number);
        abyte0[1] = (byte) ((0xff00 & number) >> 8);
        abyte0[2] = (byte) ((0xff0000 & number) >> 16);
        abyte0[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte0;
    }
    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
    private static byte [] CipherBase(byte[] data, byte[] password, int Mode, String Algorithm){
        try {
            Cipher cipher = Cipher.getInstance(Algorithm);
            if(Algorithm.contains("/")){
                Algorithm = Algorithm.substring(0, Algorithm.indexOf("/"));
            }
            SecretKeySpec key = new SecretKeySpec(password, Algorithm);
            cipher.init(Mode, key);
            return cipher.doFinal(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "".getBytes();
    }

    private  static byte [] DES(byte[] data, byte[] password, int Mode){
        return CipherBase(data, password, Mode, "DES/ECB/NoPadding");
    }

    private static byte [] AES(byte[] data, byte[] password, int Mode){
        return CipherBase(data, password, Mode, "AES/ECB/NoPadding");
    }

    private static byte[] RSA(byte[] data, Key key, int Mode){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Mode, key);
            return cipher.doFinal(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String en_rsa(byte[] data){
        final BigInteger modulus =
                new BigInteger("CC3C089E80CEFEDA93ECBE0042667A7E1430C3E4190E43D9006B1BBBCA717E97", 16);
        final  BigInteger privateExponent =
                new BigInteger("20CC9F61BD34010FDF63CCDBC3CE2B6B9CA360C453848F27BC7A65B82BD39359", 16);
        try{
            RSAPrivateKeySpec PrivateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
            RSAPrivateKey PrivateKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(PrivateKeySpec);
            byte[] LicenseRaw = byteMerger(new byte[]{0x0, (byte)data.length}, data);

            int ModulusLen = PrivateKey.getModulus().bitLength() / 8;

            if (LicenseRaw.length < ModulusLen){
                byte padding[] = new byte[ModulusLen - LicenseRaw.length];
                LicenseRaw = byteMerger(LicenseRaw, padding);
            }
            byte[] LicenseEncrypt = RSA(LicenseRaw, PrivateKey, Cipher.ENCRYPT_MODE);
            if(LicenseEncrypt == null){
                return null;
            }

            String LicenseBase64 = Base64.encodeToString(LicenseEncrypt, Base64.DEFAULT);
            return LicenseBase64;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /*
    private static byte[] de_aes(byte[] passwd,byte[] ciphertext){
        try {

            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(passwd));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥

            //SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            SecretKeySpec key = new SecretKeySpec(passwd, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(ciphertext);
            return result; // 明文

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    */
}
