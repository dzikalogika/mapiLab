package app.model;

import brackettree.reader.BracketTree;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.util.Cascade;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.*;

public class Font {

    private static final Subject $sized = Suite.set();

    public static ColorTextDrawer getForSize(double size) {
        int s = ((int)Math.min(Math.ceil(Math.abs(size)), 191.) + 32) / 32;
        var $tg = $sized.in(s).set();
        if($tg.absent()) {
            $tg.set(ColorTextDrawer.generate(Suite.put("size", s * 32)));
        }
        return $tg.asExpected();
    }

    private final String fontPath;
    private final STBTTFontinfo fontInfo;
    private final ByteBuffer trueType;
    private final int bitmapWidth;
    private final int bitmapHeight;
    private final float ascend;
    private final float descent;
    private final float lineGap;

    public static Font generate(Subject $sub) {
        String fontPath = $sub.in("font").orGiven("ttf/trebuc.ttf");

        return new Font(fontPath);
    }

    public Font(String fontPath) {

        this.fontPath = fontPath;
        this.bitmapWidth = 1024;
        this.bitmapHeight = 512;

        try {
            trueType = IOUtil.ioResourceToByteBuffer(fontPath, bitmapWidth * bitmapHeight);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, trueType)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        int[] ascend = new int[1], descent = new int[1], lineGap = new int[1];

        stbtt_GetFontVMetrics(fontInfo, ascend, descent, lineGap);

        this.ascend = ascend[0];
        this.descent = descent[0];
        this.lineGap = lineGap[0];
    }

    public float getAscend() {
        return ascend;
    }

    public float getDescent() {
        return descent;
    }

    public float getLineGap() {
        return lineGap;
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public ByteBuffer getTrueType() {
        return trueType;
    }

    public Subject bake(int firstCodePoint, int lastCodepoint, float size) {

        var $chars = Suite.set();
        int textureID = glGenTextures();
        STBTTBakedChar.Buffer buffer = STBTTBakedChar.malloc(1 + lastCodepoint - firstCodePoint);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
        stbtt_BakeFontBitmap(trueType, size, bitmap, bitmapWidth, bitmapHeight, firstCodePoint, buffer);

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, bitmapWidth, bitmapHeight, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for(int i = firstCodePoint;i <= lastCodepoint; ++i) {
            $chars.put(i, new CharacterTexture(textureID, buffer, i - firstCodePoint));
        }

        glBindTexture(GL_TEXTURE_2D, 0);

        return $chars;
    }

    public float getStringWidth(String text, float fontHeight) {
        int width = 0;
//        int lastCp = 0;
        int[] advancedWidth = new int[1];
        int[] leftSideBearing = new int[1];
        Cascade<Integer> ci = new Cascade<>(text.chars().iterator());
        for(int cp : ci) {
            stbtt_GetCodepointHMetrics(fontInfo, cp, advancedWidth, leftSideBearing);
            width += advancedWidth[0];
//            if(isKerningEnabled()) {
//                if(lastCp != 0) {
//                    width += stbtt_GetCodepointKernAdvance(info, lastCp, cp);
//                }
//                lastCp = cp;
//            }
        }

        return width * stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
    }
}
