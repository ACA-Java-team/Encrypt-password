import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.PDFStyle;
import org.faceless.pdf2.StandardFont;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class encrypt {
    public static final int keyLength = 128;
    public static final String charEnc = "UTF-8";
    public static final String transformationString = "AES/CFB/NoPadding";



        public void encyrypting(String message)  {



        String cipherText;

        try {
            // Step 1
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keyLength);
            SecretKey secretKey = keyGen.generateKey();

            // Step 2
            Cipher aesCipherForEncryption = Cipher.getInstance(transformationString);

            // Step 3
            byte[] iv = new byte[aesCipherForEncryption.getBlockSize()];
            SecureRandom prng = new SecureRandom();
            prng.nextBytes(iv);

            // Step 4
            aesCipherForEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // Step 5
            byte[] encrypted = aesCipherForEncryption.doFinal(message.getBytes(charEnc));
            ByteBuffer cipherData = ByteBuffer.allocate(iv.length + encrypted.length);
            cipherData.put(iv);
            cipherData.put(encrypted);
            cipherText = new String(Base64.getEncoder().encode(cipherData.array()), charEnc);
            PDF pdf= new PDF();
            PDFPage page = pdf.newPage("A4");
            PDFStyle mystyle =new PDFStyle();
            mystyle.setFont(new StandardFont(StandardFont.TIMES), 24);
            mystyle.setFillColor(Color.black);
            page.setStyle(mystyle);
            String s=new String(Base64.getEncoder().encode(encrypted), charEnc);
            page.drawText(s, 100, page.getHeight()-100);
            OutputStream out = new FileOutputStream("pdf_file1.pdf");
            pdf.render(out);
            out.close();


            // Step 6
            Cipher aesCipherForDecryption = Cipher.getInstance(transformationString);

            // Step 7
            cipherData = ByteBuffer.wrap(Base64.getDecoder().decode(cipherText.getBytes(charEnc)));
            iv = new byte[aesCipherForDecryption.getBlockSize()];
            cipherData.get(iv);
            encrypted = new byte[cipherData.remaining()];
            cipherData.get(encrypted);
            aesCipherForDecryption.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // Step 8
            byte[] decrypted = aesCipherForDecryption.doFinal(encrypted);


            PDFPage page1 = pdf.newPage("A4");
            PDFStyle mystyle1 =new PDFStyle();
            mystyle1.setFont(new StandardFont(StandardFont.TIMES), 24);
            mystyle1.setFillColor(Color.black);
            page1.setStyle(mystyle1);
            String k=new String(decrypted, charEnc);
            page1.drawText(k, 100, page1.getHeight()-100);
            OutputStream out1 = new FileOutputStream("pdf_file1.pdf");
            pdf.render(out1);
            out1.close();

        } catch(NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | UnsupportedEncodingException | FileNotFoundException ex) {
            System.err.println(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

