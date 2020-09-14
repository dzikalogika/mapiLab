package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;

public enum Unit {
    PIXEL, PERCENT;

    public static Unit PX = PIXEL, PC = PERCENT;

    public Object parcel(Object o) {
        return switch (this) {
            case PIXEL -> new PixelParcel(o, null);
            case PERCENT -> new PercentParcel(o, null);
        };
    }

    public Object parcel(Object ware, Object waybill) {
        return switch (this) {
            case PIXEL -> new PixelParcel(ware, waybill);
            case PERCENT -> new PercentParcel(ware, waybill);
        };
    }
}
