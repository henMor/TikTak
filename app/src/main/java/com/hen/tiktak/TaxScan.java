package com.hen.tiktak;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaxScan {

    String image;
    int id;

    public TaxScan() {
    }

    public TaxScan(int id, String image) {

        this.image = image;
        this.id = id;
        /*
        Date date1 = new Date();
    //    SimpleDateFormat formatNowDay = new SimpleDateFormat("dd");
        SimpleDateFormat formatNowMonth = new SimpleDateFormat("MM");
        SimpleDateFormat formatNowYear = new SimpleDateFormat("YY");

   //     String currentDay = formatNowDay.format(date1);
        String currentMonth = formatNowMonth.format(date1);
        String currentYear = formatNowYear.format(date1);

*/

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


