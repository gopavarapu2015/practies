package capital.clix.los.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.PersonalLoanEntity;

@Component
public class ValidatorFactory {

  @Autowired
  @Qualifier("createLoanRequestValidator")
  CreateLoanRequestValidator createLoanRequestValidator;

  public Validator getValidatorInstance(BaseEntity req) {

    if (req.getClass().equals(PersonalLoanEntity.class))
      return createLoanRequestValidator;

    return null;
  }
}
