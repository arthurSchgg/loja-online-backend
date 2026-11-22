package br.com.lojinhadakatia.media;

import br.com.lojinhadakatia.shared.exception.BusinessException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.*;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.springframework.stereotype.Component;

/** Accept raster photos only, bound decode size and re-encode without original metadata. */
@Component
public class PhotoCodec {
    public static final int MAX_INPUT = 8 * 1024 * 1024;
    public static final int MAX_OUTPUT = 768 * 1024;
    private static final int MAX_SIDE = 1600;
    private static final long MAX_PIXELS = 24_000_000;
    public record Photo(byte[] bytes, String contentType, int width, int height) {}

    public Photo sanitize(byte[] bytes) {
        if (bytes==null || bytes.length==0 || bytes.length>MAX_INPUT)
            throw new BusinessException("Escolha uma foto de até 8 MB após a otimização.");
        // A trusted built-in reader must identify actual JPEG/PNG data.
        try (var input=new MemoryCacheImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> readers=ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new BusinessException("Formato de foto inválido. Use JPEG ou PNG.");
            ImageReader reader=readers.next();
            try {
                String format=reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!format.equals("jpeg")&&!format.equals("png"))
                    throw new BusinessException("Formato não suportado. Use JPEG ou PNG.");
                reader.setInput(input,true,true);
                int w=reader.getWidth(0), h=reader.getHeight(0);
                if(w<=0||h<=0||(long)w*h>MAX_PIXELS||w>12000||h>12000)
                    throw new BusinessException("Foto muito grande. Reduza as dimensões e tente novamente.");
                // Decode directly at a reduced resolution to limit memory.
                int sampling=Math.max(1,(int)Math.floor((double)Math.max(w,h)/MAX_SIDE));
                ImageReadParam params=reader.getDefaultReadParam();
                params.setSourceSubsampling(sampling,sampling,0,0);
                BufferedImage decoded=reader.read(0,params);
                if(decoded==null)throw new BusinessException("Não foi possível abrir a foto.");
                try {return normalize(decoded);}
                finally {decoded.flush();}
            } finally {reader.dispose();}
        } catch(BusinessException ex){throw ex;}
          catch(IOException|RuntimeException ex){
            throw new BusinessException("Não foi possível abrir a foto. Selecione uma imagem válida.");
        }
    }

    private Photo normalize(BufferedImage original) throws IOException {
        boolean alpha=original.getColorModel().hasAlpha();
        String format=alpha?"png":"jpeg";
        int size=Math.min(MAX_SIDE,Math.max(original.getWidth(),original.getHeight()));
        while(true){
            double scale=Math.min(1.0,(double)size/Math.max(original.getWidth(),original.getHeight()));
            int width=Math.max(1,(int)Math.round(original.getWidth()*scale));
            int height=Math.max(1,(int)Math.round(original.getHeight()*scale));
            BufferedImage resized=new BufferedImage(width,height,alpha?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
            Graphics2D g=resized.createGraphics();
            try{
                if(!alpha){g.setColor(Color.WHITE);g.fillRect(0,0,width,height);}
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.drawImage(original,0,0,width,height,null);
            }finally{g.dispose();}
            try{
                byte[] data=encode(resized,format,0.83f);
                if(!alpha&&data.length>MAX_OUTPUT)data=encode(resized,format,0.67f);
                if(data.length<=MAX_OUTPUT)return new Photo(data,alpha?"image/png":"image/jpeg",width,height);
            }finally{resized.flush();}
            if(size<=256)break;
            size=Math.max(256,(int)(size*0.75));
        }
        throw new BusinessException("A foto não pôde ser compactada. Escolha uma imagem mais leve.");
    }

    private byte[] encode(BufferedImage image,String format,float quality) throws IOException {
        var writers=ImageIO.getImageWritersByFormatName(format);
        if(!writers.hasNext())throw new IOException("Raster encoder unavailable");
        var writer=writers.next();
        var bytes=new ByteArrayOutputStream();
        try(var output=new MemoryCacheImageOutputStream(bytes)){
            writer.setOutput(output);
            var params=writer.getDefaultWriteParam();
            if(format.equals("jpeg")&&params.canWriteCompressed()){
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(quality);
            }
            // No EXIF, original file name, ICC comments or arbitrary user metadata.
            writer.write(null,new IIOImage(image,null,null),params);
            output.flush();
        }finally{writer.dispose();}
        return bytes.toByteArray();
    }
}
