package com.xys.zxinglib;


import android.util.Base64;
import android.util.Log;


import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import javax.crypto.Cipher;

import javax.crypto.spec.SecretKeySpec;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.zip.CRC32;

public class KeyGen {
    private static final String LOG_TAG = "TEN-ROUND2-KEYGEN";

    static byte [] SHA1(byte[] content) {
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA1");
            hash.update(content);
            return hash.digest();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    static long Crc32Calc(byte[] content){
        CRC32 crc = new CRC32();
        crc.update(content);
        return crc.getValue();
    }

    static byte [] CipherBase(byte[] data, byte[] password, int Mode, String Algorithm){
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

    static byte [] DES(byte[] data, byte[] password, int Mode){
        return CipherBase(data, password, Mode, "DES/ECB/NoPadding");
    }

    static byte [] AES(byte[] data, byte[] password, int Mode){
        return CipherBase(data, password, Mode, "AES/ECB/NoPadding");
    }

    public static byte[] toHH(int number) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & number);
        abyte0[1] = (byte) ((0xff00 & number) >> 8);
        abyte0[2] = (byte) ((0xff0000 & number) >> 16);
        abyte0[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte0;
    }

    public static byte[] IntToByteArray(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[3] = (byte) (0xff & i);
        abyte0[2] = (byte) ((0xff00 & i) >> 8);
        abyte0[1] = (byte) ((0xff0000 & i) >> 16);
        abyte0[0] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static void Log(String msg){
        Log.i(LOG_TAG, msg);
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

    static byte[] KeyGenNative(byte[] MachineId){
        if (MachineId.length < 6 || MachineId.length > 100){
            return "".getBytes();
        }
        // SHA1
        byte [] midSha1 = SHA1(byteMerger(MachineId, new byte[] {0x00}));
        Log("SHA1: " + bytesToHexString(midSha1));
        // AES
        byte [] midAes = AES(Arrays.copyOfRange(midSha1, 0, 16),
                Arrays.copyOfRange(midSha1, 4, 20), Cipher.DECRYPT_MODE);
        Log("AES: " + bytesToHexString(midAes));
        // CRC
        int midCrc = (int)Crc32Calc(MachineId);
        Log.i(LOG_TAG, "MidCrc: " + midCrc);
        // keygen
        byte[] desPassWord = byteMerger(new byte[]{0x3F, 0x5D, 0x23, 0x13}, toHH(midCrc));
        Log.i(LOG_TAG, "DESKEY: " + bytesToHexString(desPassWord));
        byte[] license =  DES(midAes, desPassWord, Cipher.ENCRYPT_MODE);
        Log.i(LOG_TAG, bytesToHexString(IntToByteArray(midCrc)));
        license = byteMerger(license, toHH(midCrc));
        Log.i(LOG_TAG, "License: " + bytesToHexString(license));
        return license;
    }
    // RSA PUBKEY
    private static final String x509pem =
            "MDwwDQYJKoZIhvcNAQEBBQADKwAwKAIhAMw8CJ6Azv7ak+y+AEJmen4UMMPkGQ5D2QBrG7vKcX6XAgMBAAE=";
    // RSA PrivateKey
    private static final  BigInteger modulus =
            new BigInteger("CC3C089E80CEFEDA93ECBE0042667A7E1430C3E4190E43D9006B1BBBCA717E97", 16);
    private static final  BigInteger privateExponent =
            new BigInteger("20CC9F61BD34010FDF63CCDBC3CE2B6B9CA360C453848F27BC7A65B82BD39359", 16);
    static private boolean TestRSA(){
        try {
            X509EncodedKeySpec x509Key = new X509EncodedKeySpec(Base64.decode(x509pem, 0));
            RSAPublicKey PublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(x509Key);
            int ModulusLen = PublicKey.getModulus().bitLength();
            String mod = PublicKey.getModulus().toString(16);
            String exp = PublicKey.getPublicExponent().toString(16);

            Log.i(LOG_TAG, "pubkey len: " + ModulusLen);
            Log.i(LOG_TAG, "module:" + mod);
            Log.i(LOG_TAG, "PublicExponent: " + exp);

            RSAPrivateKeySpec PrivateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
            RSAPrivateKey PrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(PrivateKeySpec);

            String TestData = "CrackAllCrackAllCrackAllCrackAll";

            byte[] EncryptData = RSA(TestData.getBytes(), PrivateKey, Cipher.ENCRYPT_MODE);
            byte[] DecryptData = RSA(EncryptData, PublicKey, Cipher.DECRYPT_MODE);
            if (DecryptData != null){
                Log("TestRSA: " + new String(DecryptData));
            }
            else
            {
                Log("TestRSA: " + "Test Failed");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    static String GenLicense(String MachineID) {
        TestRSA();
        try {

            byte[] NativePart = KeyGenNative(MachineID.getBytes());
            if (NativePart == null){
                return null;
            }
            RSAPrivateKeySpec PrivateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
            RSAPrivateKey PrivateKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(PrivateKeySpec);

            byte[] LicenseRaw = byteMerger(new byte[]{0x0, (byte)NativePart.length}, NativePart);

            int ModulusLen = PrivateKey.getModulus().bitLength() / 8;

            if (LicenseRaw.length < ModulusLen){
                byte padding[] = new byte[ModulusLen - LicenseRaw.length];
                LicenseRaw = byteMerger(LicenseRaw, padding);
            }

            Log("Before RSA: " + bytesToHexString(LicenseRaw));

            byte[] LicenseEncrypt = RSA(LicenseRaw, PrivateKey, Cipher.ENCRYPT_MODE);
            if(LicenseEncrypt == null){
                return null;
            }

            String LicenseBase64 = Base64.encodeToString(LicenseEncrypt, Base64.DEFAULT);
            Log.i(LOG_TAG, "FinalLicense: " + LicenseBase64);
            return LicenseBase64;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
