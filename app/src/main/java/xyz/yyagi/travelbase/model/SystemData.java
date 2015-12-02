package xyz.yyagi.travelbase.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by yaginuma on 15/12/02.
 */
public class SystemData extends RealmObject {
   private Date api_last_acquisition_time;

   public Date getApi_last_acquisition_time() {
      return api_last_acquisition_time;
   }

   public void setApi_last_acquisition_time(Date api_last_acquisition_time) {
      this.api_last_acquisition_time = api_last_acquisition_time;
   }
}
