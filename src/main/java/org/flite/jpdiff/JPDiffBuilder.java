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
    private List<BufferedImage> images = new ArrayList<BufferedImage>();

    public JPDiffBuilder setAgreeColor(final String agree) {
        this.agreeValue = Integer.decode(agree);
        return this;
    }

    public JPDiffBuilder setDisagreeColor(final String disagree) {
        this.disagreeValue = Integer.decode(disagree);
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

        for (final BufferedImage img : images) {
            final int ht = img.getHeight();
            final int wd = img.getWidth();

            if (gcHt > ht) { gcHt = ht; }
            if (gcWd > wd) { gcWd = wd; }
            if (ht > maxHt) { maxHt = ht; }
            if (wd > maxWd) { maxWd = wd; }
        }

        final long totalPixels = maxHt * maxWd;
        long agreePixels = 0;

        final ImageBuilder ib = new ImageBuilder(maxWd, maxHt, false);

        for (int iy = 0; iy < maxHt; iy++) {
            for (int ix = 0; ix < maxWd; ix++) {
                boolean agrees = (ix < gcWd && iy < gcHt);
                if (agrees) {
                    Integer rgb = null;
                    for (final BufferedImage img : images) {
                        if (rgb == null) {
                            rgb = img.getRGB(ix, iy);
                            continue;
                        }
                        if (rgb.intValue() != img.getRGB(ix, iy)) {
                            agrees = false;
                            break;
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
}
