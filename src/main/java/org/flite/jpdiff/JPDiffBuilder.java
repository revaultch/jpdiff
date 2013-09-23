package org.flite.jpdiff;

import org.apache.commons.imaging.common.ImageBuilder;

import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2006-2013 Flite, Inc
 * <p/>
 * All rights reserved.
 * THIS PROGRAM IS CONFIDENTIAL AND AN UNPUBLISHED WORK AND TRADE
 * SECRET OF THE COPYRIGHT HOLDER, AND DISTRIBUTED ONLY UNDER RESTRICTION.
 */
public class JPDiffBuilder {

    private int agreeValue = Integer.decode("#000000");
    private int disagreeValue = Integer.decode("#ff0000");
    private int tolerancePerChannel = 32;
    private List<BufferedImage> images = new ArrayList<BufferedImage>();

    public JPDiffBuilder setAgreeColor(final String agree) {
        this.agreeValue = Integer.decode(agree);
        return this;
    }

    public JPDiffBuilder setDisagreeColor(final String disagree) {
        this.disagreeValue = Integer.decode(disagree);
        return this;
    }

    public JPDiffBuilder setTolerancePerChannel(final int diff) {
        this.tolerancePerChannel = diff;
        return this;
    }

    public JPDiffBuilder addComparisonImage(final BufferedImage img) {
        if (img == null) { throw new InvalidParameterException("Non-null images are required."); }
        images.add(img);
        return this;
    }

    public Result calculateResult() {
        if (images.size() < 1) { throw new IllegalStateException("No images submitted for comparison."); }

        // Greatest common height/width
        int gcHt = Integer.MAX_VALUE;
        int gcWd  = Integer.MAX_VALUE;

        // Maximum height/width
        int maxHt = 0;
        int maxWd = 0;
        boolean hasAlpha = false;

        for (final BufferedImage img : images) {
            final int ht = img.getHeight();
            final int wd = img.getWidth();

            if (gcHt > ht) { gcHt = ht; }
            if (gcWd > wd) { gcWd = wd; }
            if (ht > maxHt) { maxHt = ht; }
            if (wd > maxWd) { maxWd = wd; }

            if (img.getColorModel().hasAlpha()) { hasAlpha = true; }
        }

        final long totalPixels = maxHt * maxWd;
        long agreePixels = 0;

        final ImageBuilder ib = new ImageBuilder(maxWd, maxHt, false);

        for (int iy = 0; iy < maxHt; iy++) {
            for (int ix = 0; ix < maxWd; ix++) {
                boolean agrees = (ix < gcWd && iy < gcHt);
                if (agrees) {
                    Integer rgb = null;
                    int maxR = 0, minR = 0, maxG = 0, minG = 0, maxB = 0, minB = 0, maxA = 0, minA = 0;
                    for (final BufferedImage img : images) {
                        final Integer current = img.getRGB(ix, iy);
                        if (rgb == null) {
                            rgb = current;
                            maxR = getR(rgb) + tolerancePerChannel;
                            minR = getR(rgb) - tolerancePerChannel;
                            maxG = getG(rgb) + tolerancePerChannel;
                            minG = getG(rgb) - tolerancePerChannel;
                            maxB = getB(rgb) + tolerancePerChannel;
                            minB = getB(rgb) - tolerancePerChannel;
                            maxA = getA(rgb) + tolerancePerChannel;
                            minA = getA(rgb) - tolerancePerChannel;
                            continue;
                        }
                        int currentR = getR(current);
                        int currentG = getG(current);
                        int currentB = getB(current);
                        int currentA = getA(current);
                        if (currentR > maxR || currentR < minR
                                || currentG > maxG || currentG < minG
                                || currentB > maxB || currentB < minB
                                || (hasAlpha && currentA > maxA) || (hasAlpha && currentA < minA)) {
                            agrees = false;
//                            System.out.println("(" + ix + ", " + iy + "); " +
//                                    "Base [" + getA(rgb) + ", " + getR(rgb) + ", " + getG(rgb) + ", " + getB(rgb) + "]; " +
//                                    "Miss [" + currentA + ", " + currentR + ", " + currentG + ", " + currentB + "]");
                        }
                    }
                }

                ib.setRGB(ix, iy, agrees ? agreeValue : disagreeValue);
                if (agrees) { agreePixels++; }
            }
        }

        final float agreePercent = (float) agreePixels / totalPixels;
        return new Result(agreePercent, ib.getBufferedImage());
    }

    protected static int getR(final int argb) {
        return (argb >> 16) & 0xFF;
    }

    protected static int getG(final int argb) {
        return (argb >> 8) & 0xFF;
    }

    protected static int getB(final int argb) {
        return argb & 0xFF;
    }

    protected static int getA(final int argb) {
        return (argb >> 24) & 0xFF;
    }
}
