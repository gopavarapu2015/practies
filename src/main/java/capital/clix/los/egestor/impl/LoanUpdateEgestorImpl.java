package capital.clix.los.egestor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;

public class LoanUpdateEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  @Override
  public void egest(BaseEntity request, BaseResponse response) {

    // BaseEntity loanReq = couchBaseEgestorImpl.getLoanEntity(request.getId().toString());

  }

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    // TODO Auto-generated method stub

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    // TODO Auto-generated method stub

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    // TODO Auto-generated method stub

  }

}
