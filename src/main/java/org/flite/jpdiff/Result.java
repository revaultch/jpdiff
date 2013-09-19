package org.flite.jpdiff;

import java.awt.image.BufferedImage;

/**
 * Copyright (c) 2006-2013 Flite, Inc
 * <p/>
 * All rights reserved.
 * THIS PROGRAM IS CONFIDENTIAL AND AN UNPUBLISHED WORK AND TRADE
 * SECRET OF THE COPYRIGHT HOLDER, AND DISTRIBUTED ONLY UNDER RESTRICTION.
 */
public class Result {
    private final float agreementPercent;
    private final BufferedImage image;

    public Result(float agreementPercent, BufferedImage image) {
        this.agreementPercent = agreementPercent;
        this.image = image;
    }

    public float getAgreementPercent() {
        return agreementPercent;
    }

    public BufferedImage getImage() {
        return image;
    }
}
