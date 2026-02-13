package com.canteen.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * QR Code Service - Generates QR codes for tokens
 */
@Service
public class QRCodeService {
    
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    
    /**
     * Generate QR Code as Base64 string
     */
    public String generateQRCodeBase64(String data) throws WriterException, IOException {
        BufferedImage qrImage = generateQRCodeImage(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * Generate QR Code Image
     */
    public BufferedImage generateQRCodeImage(String data) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Generate unique token data for QR code
     */
    public String generateTokenData(String employeeId, String tokenNumber) {
        return String.format("CANTEEN_TOKEN|%s|%s|%s", 
                tokenNumber, 
                employeeId, 
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
    
    /**
     * Generate token number
     */
    public String generateTokenNumber() {
        return "TKN" + System.currentTimeMillis();
    }
    
    /**
     * Parse QR code data
     */
    public QRCodeData parseQRCodeData(String qrData) {
        String[] parts = qrData.split("\\|");
        if (parts.length >= 3 && "CANTEEN_TOKEN".equals(parts[0])) {
            return new QRCodeData(parts[1], parts[2]);
        }
        return null;
    }
    
    /**
     * QR Code Data inner class
     */
    public static class QRCodeData {
        private final String tokenNumber;
        private final String employeeId;
        
        public QRCodeData(String tokenNumber, String employeeId) {
            this.tokenNumber = tokenNumber;
            this.employeeId = employeeId;
        }
        
        public String getTokenNumber() {
            return tokenNumber;
        }
        
        public String getEmployeeId() {
            return employeeId;
        }
    }
}
