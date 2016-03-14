package xyz.yyagi.travelbase.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by yaginuma on 15/12/02.
 */
public class SystemData extends RealmObject {
   private Date api_last_acquisition_time;
   private String table_name;

   public Date getApi_last_acquisition_time() {
      return api_last_acquisition_time;
   }

   public void setApi_last_acquisition_time(Date api_last_acquisition_time) {
      this.api_last_acquisition_time = api_last_acquisition_time;
   }

   public String getTable_name() {
      return table_name;
   }

   public void setTable_name(String table_name) {
      this.table_name = table_name;
   }
}
