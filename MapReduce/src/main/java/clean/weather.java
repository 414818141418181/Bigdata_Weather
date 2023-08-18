package clean;

import java.util.Date;

public class weather {
    String high;//高温
    String low;//低温
    Date ymd;//日期
    String sumrise;//日出
    String sumset;//日落
    String type;//天气

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public Date getYmd() {
        return ymd;
    }

    public void setYmd(Date ymd) {
        this.ymd = ymd;
    }

    public String getSumrise() {
        return sumrise;
    }

    public void setSumrise(String sumrise) {
        this.sumrise = sumrise;
    }

    public String getSumset() {
        return sumset;
    }

    public void setSumset(String sumset) {
        this.sumset = sumset;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
