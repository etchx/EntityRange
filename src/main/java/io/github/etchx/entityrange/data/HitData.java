package io.github.etchx.entityrange.data;

/**
 * Data object storing hit data
 */
public class HitData {
    private long time;
    private float charge;
    private HitType type;
    private double distance;
    private double damage;

    public HitData(long time, float charge, HitType type, double distance, double damage) {
        this.time = time;
        this.charge = charge;
        this.type = type;
        this.distance = distance;
        this.damage = damage;
    }

    public long getTime() {
        return this.time;
    }

    public float getCharge() {
        return this.charge;
    }

    public HitType getType() {
        return this.type;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

    public void setType(HitType type) {
        this.type = type;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
