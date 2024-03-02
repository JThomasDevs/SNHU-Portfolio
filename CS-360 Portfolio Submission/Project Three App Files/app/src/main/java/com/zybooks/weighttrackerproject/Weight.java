package com.zybooks.weighttrackerproject;

import androidx.annotation.NonNull;
import java.sql.Date;

public class Weight {
    private final int id;
    private final double weight;
    private final Date date;
    private final String weightUnit;
    private final String weightText;

    public Weight(int id, double weight, Date date, String weightUnit, String weightText) {
        this.id = id;
        this.weight = weight;
        this.date = date;
        this.weightUnit = weightUnit;
        this.weightText = weightText;
    }

    public Weight(double weight, Date date, String weightUnit, String weightText) {
        this.id = -1;
        this.weight = weight;
        this.date = date;
        this.weightUnit = weightUnit;
        this.weightText = weightText;
    }

    @NonNull
    public String toString() {
        return String.format(weightText, weight, weightUnit, Date.from(date.toInstant()));
    }

    public double getWeight() {
        return weight;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getUnit() {
        return weightUnit;
    }

    public static double lbToKg(double lb) {
        return lb * 0.453592;
    }

    public static double kgToLb(double kg) {
        return kg * 2.20462;
    }
}
