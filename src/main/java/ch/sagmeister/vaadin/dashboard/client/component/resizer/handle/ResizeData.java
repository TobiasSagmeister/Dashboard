package ch.sagmeister.vaadin.dashboard.client.component.resizer.handle;

import ch.sagmeister.vaadin.dashboard.client.component.util.Location;

public class ResizeData {

    public final Location location;
    public final ResizeSign resizeSign;
    public final double pxTop;
    public final double pctLeft;
    public final double pctWidth;
    public final double pxHeight;

    private ResizeData(Builder builder) {
        this.location = builder.location;
        this.resizeSign = builder.resizeSign;
        this.pxTop = builder.pxTop;
        this.pctLeft = builder.pctLeft;
        this.pctWidth = builder.pctWidth;
        this.pxHeight = builder.pxHeight;
    }

    public static class Builder {
        private final Location location;
        private final ResizeSign resizeSign;
        private double pxTop;
        private double pctLeft;
        private double pctWidth;
        private double pxHeight;

        public Builder(Location location, ResizeSign resizeSign) {
            this.location = location;
            this.resizeSign = resizeSign;
        }

        public Builder setPxTop(double pxTop) {
            this.pxTop = pxTop;
            return this;
        }

        public Builder setPctLeft(double pctLeft) {
            this.pctLeft = pctLeft;
            return this;
        }

        public Builder setPctWidth(double pctWidth) {
            this.pctWidth = pctWidth;
            return this;
        }

        public Builder setPxHeight(double pxHeight) {
            this.pxHeight = pxHeight;
            return this;
        }

        public ResizeData build() {
            return new ResizeData(this);
        }
    }
}
