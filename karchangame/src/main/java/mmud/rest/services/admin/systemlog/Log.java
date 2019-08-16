package mmud.rest.services.admin.systemlog;

import java.time.Instant;

/**
 * For example [2019-01-18T13:10:02.420+0100] [Payara 5.184] [INFO] [NCLS-LOGGING-00009] [javax.enterprise.logging] [tid: _ThreadID=31 _ThreadName=RunLevelControllerThread-1547813402253] [timeMillis: 1547813402420] [levelValue: 800] [[ Running Payara Version: Payara Server 5.184.1 #badassfish (build 90)]]
 */
public class Log {
  public Instant timestamp;
  public String organizationID;
  public String messageTypeLevel;
  public String messageID;
  public String loggerName;
  public String message;

}
