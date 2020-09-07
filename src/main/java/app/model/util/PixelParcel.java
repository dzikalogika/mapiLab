package app.model.util;

import java.util.Objects;

public class PixelParcel {
    public Object ware;
    public Object waybill;

    public PixelParcel(Object ware, Object waybill) {
        this.ware = ware;
        this.waybill = waybill;
    }

    public String getWaybillStr() {
        return Objects.toString(waybill);
    }
}
