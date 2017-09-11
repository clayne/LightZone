/* Copyright (C) 2005-2011 Fabio Riccardi */
/* Copyright (C) 2017-     Masahiro Kitagawa */

package com.lightcrafts.ui.crop;

import com.lightcrafts.model.CropBounds;
import static com.lightcrafts.ui.crop.Locale.LOCALE;

import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AspectConstraint {

    private static final String NoConstraintName =
        LOCALE.get("NoConstraintName");

    private int numerator;
    private int denominator;
    private String name;

    // The default constructor makes a no-constraint object that accepts
    // any rectangle.
    AspectConstraint() {
        name = NoConstraintName;
    }

    AspectConstraint(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        name = String.format("%.2f", 1 / getAspectRatio()) + " | "
                + denominator + " x " + numerator;
    }

    AspectConstraint(int numerator, int denominator, String description) {
        this(numerator, denominator);
        name += " (" + description + ")";
    }

    AspectConstraint getInverse() {
        if (name.equals(NoConstraintName)) {
            return new AspectConstraint();
        }
        return new AspectConstraint(denominator, numerator);
    }

    @Override
    public String toString() {
        return name;
    }

    // Decode a String generated by toString().
    static AspectConstraint fromString(String s) {
        if (s.equals(NoConstraintName)) {
            return new AspectConstraint();
        }
        Pattern pattern = Pattern.compile(".* \\| ([0-9]+) x ([0-9]+).*");
        Matcher matcher = pattern.matcher(s);
        if (! matcher.matches()) {
            return null;
        }
        String numText = matcher.replaceAll("$1");
        String denText = matcher.replaceAll("$2");
        int num = Integer.parseInt(numText);
        int den = Integer.parseInt(denText);

        return new AspectConstraint(num, den);
    }

    double getAspectRatio() {
        return numerator / (double) denominator;
    }

    int getNumerator() {
        return numerator;
    }

    int getDenominator() {
        return denominator;
    }

    AspectConstraint transpose() {
        return new AspectConstraint(denominator, numerator);
    }

    // Make a new crop with the same center and area as the given
    // crop but having the constrained aspect ratio.
    CropBounds adjust(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        double area = bad.getWidth() * bad.getHeight();
        Point2D center = bad.getCenter();
        double width = Math.sqrt(area / ratio);
        double height = Math.sqrt(area * ratio);
        double angle = bad.getAngle();
        CropBounds good = new CropBounds(
            center, width, height, angle
        );
        return good;
    }

    // Make a new crop like the given one but with left edge adjusted to
    // match the constrained aspect ratio.
    CropBounds adjustLeft(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        double badW = bad.getWidth();
        double goodW = bad.getHeight() / ratio;
        double angle = bad.getAngle();
        double dx = - Math.cos(angle) * (goodW - badW) / 2;
        double dy = - Math.sin(angle) * (goodW - badW) / 2;
        Point2D center = bad.getCenter();
        center.setLocation(center.getX() + dx, center.getY() + dy);
        CropBounds good = new CropBounds(
            center, goodW, bad.getHeight(), angle
        );
        return good;
    }

    // Make a new crop like the given one but with top edge adjusted to
    // match the constrained aspect ratio.
    CropBounds adjustTop(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        double badH = bad.getHeight();
        double goodH = bad.getWidth() * ratio;
        double angle = bad.getAngle();
        double dx = Math.sin(angle) * (goodH - badH) / 2;
        double dy = - Math.cos(angle) * (goodH - badH) / 2;
        Point2D center = bad.getCenter();
        center.setLocation(center.getX() + dx, center.getY() + dy);
        CropBounds good = new CropBounds(
            center, bad.getWidth(), goodH, angle
        );
        return good;
    }

    // Make a new crop like the given one but with right edge adjusted to
    // match the constrained aspect ratio.
    CropBounds adjustRight(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        double badW = bad.getWidth();
        double goodW = bad.getHeight() / ratio;
        double angle = bad.getAngle();
        double dx = Math.cos(angle) * (goodW - badW) / 2;
        double dy = Math.sin(angle) * (goodW - badW) / 2;
        Point2D center = bad.getCenter();
        center.setLocation(center.getX() + dx, center.getY() + dy);
        CropBounds good = new CropBounds(
            center, goodW, bad.getHeight(), angle
        );
        return good;
    }

    // Make a new crop like the given one but with bottom edge adjusted to
    // match the constrained aspect ratio.
    CropBounds adjustBottom(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        double badH = bad.getHeight();
        double goodH = bad.getWidth() * ratio;
        double angle = bad.getAngle();
        double dx = - Math.sin(angle) * (goodH - badH) / 2;
        double dy = Math.cos(angle) * (goodH - badH) / 2;
        Point2D center = bad.getCenter();
        center.setLocation(center.getX() + dx, center.getY() + dy);
        CropBounds good = new CropBounds(
            center, bad.getWidth(), goodH, angle
        );
        return good;
    }

    // Make a new crop like the given one but with width adjusted to
    // match the constrained aspect ratio.
    CropBounds adjustWidth(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        Point2D center = bad.getCenter();
        double height = bad.getHeight();
        double width = height / ratio;
        double angle = bad.getAngle();
        CropBounds good = new CropBounds(
            center, width, height, angle
        );
        return good;
    }

    // Make a new crop like the given one but with height adjusted to
    // match the constrained aspect ratio.
    CropBounds adjustHeight(CropBounds bad) {
        if (isNoConstraint()) {
            return bad;
        }
        double ratio = getAspectRatio();
        Point2D center = bad.getCenter();
        double width = bad.getWidth();
        double height = width * ratio;
        double angle = bad.getAngle();
        CropBounds good = new CropBounds(
            center, width, height, angle
        );
        return good;
    }

    boolean isNoConstraint() {
        return ((numerator == 0) && (denominator == 0));
    }
}
