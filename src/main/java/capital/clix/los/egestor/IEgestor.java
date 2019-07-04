package capital.clix.los.egestor;

import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;

public interface IEgestor {

  void preProcess(BaseEntity request, BaseResponse response);

  void process(BaseEntity request, BaseResponse response);

  void postProcess(BaseEntity request, BaseResponse response);

  default void egest(BaseEntity request, BaseResponse response) {
    this.preProcess(request, response);
    this.process(request, response);
    this.postProcess(request, response);
  }
}
