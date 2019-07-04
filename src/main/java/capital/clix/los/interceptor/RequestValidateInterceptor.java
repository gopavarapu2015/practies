package capital.clix.los.interceptor;

import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component("requestValidateInterceptor")
public class RequestValidateInterceptor extends HandlerInterceptorAdapter {

  static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    long startTime = System.currentTimeMillis();

    String uuidStr = request.getHeader("uuid");
    if (uuidStr == null) {
      throw new Exception("Invalid Request");
    }
    UUID uuid = UUID.fromString(uuidStr);
    if (new Date().getTime()
        - ((uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000) > 100000)
      throw new Exception("Old Request");

    return true;
  }

}
