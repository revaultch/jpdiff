package org.flite.jpdiff;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.Imaging;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Copyright (c) 2006-2013 Flite, Inc
 * <p/>
 * All rights reserved.
 * THIS PROGRAM IS CONFIDENTIAL AND AN UNPUBLISHED WORK AND TRADE
 * SECRET OF THE COPYRIGHT HOLDER, AND DISTRIBUTED ONLY UNDER RESTRICTION.
 */
public class JPDiffBuilderTest {

    @Test
    public void testCreate() throws Exception {

//        final ImageBuilder imageBuilder = new ImageBuilder(100, 100, false);
//
//        for (int iy = 0; iy < 100; iy++) {
//            int color = Integer.decode("#FF00CC");
//            if (iy < 75) color = Integer.decode("#000000");
////            else if (iy < 50) color = Integer.decode("#000000");
////            else if (iy < 75) color = Integer.decode("#999999");
//
//            for (int ix = 0; ix < 100; ix++) {
//
//                imageBuilder.setRGB(ix, iy, color);
//            }
//        }

        final BufferedImage imgA = Imaging.getBufferedImage(new File("/Users/nelz/tmp/jpdiff/pic1a.png"));
        final BufferedImage imgB = Imaging.getBufferedImage(new File("/Users/nelz/tmp/jpdiff/pic1b.png"));
        final Result result = new JPDiffBuilder().addComparisonImage(imgA).addComparisonImage(imgB).calculateResult();

        final File dest = File.createTempFile("nelz-" + System.currentTimeMillis() + "-",".png");
        Imaging.writeImage(result.getImage(), dest, ImageFormat.IMAGE_FORMAT_PNG, null);

        System.out.println("Results: " + dest.getAbsolutePath());
    }
}
