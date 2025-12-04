package com.example.app.model;

public class BonusConfig {
    private double minAmount;
    private double maxAmount;
    private int count;

    public BonusConfig() {}
    public BonusConfig(double minAmount, double maxAmount, int count) {
        this.minAmount = minAmount; this.maxAmount = maxAmount; this.count = count;
    }
    public double getMinAmount() { return minAmount; }
    public void setMinAmount(double minAmount) { this.minAmount = minAmount; }
    public double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(double maxAmount) { this.maxAmount = maxAmount; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
