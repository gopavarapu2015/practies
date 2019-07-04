package capital.clix.los.commonUtility;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import com.datastax.driver.core.utils.UUIDs;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.ReportType;

public class CommonUtility {

  static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

  public static UUID getTimeBasedUUID() {
    UUID uuid = UUIDs.timeBased();
    System.out.println(uuid);
    System.out.println((uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000);
    System.out.println(new Date((uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000));
    return uuid;
  }

  public static String keyBuilderForReport(ReportType reportType, String applicationId,
      String coAppId) {

    String key = null;

    if (reportType == ReportType.CIBIL)
      key = Prefix.CIBIL_REPORT.getCode() + applicationId + Prefix.COAPPLICANT.getCode() + coAppId;

    if (reportType == ReportType.DELPHI)
      key = Prefix.DELPHI_REPORT.getCode() + applicationId + Prefix.COAPPLICANT.getCode() + coAppId;



    return key;
  }
}
