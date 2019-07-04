package capital.clix.los.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import capital.clix.los.bean.Address;
import capital.clix.los.bean.Applicant;
import capital.clix.los.bean.PersonalLoanEntity;

@Component("createLoanRequestValidator")
public class CreateLoanRequestValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return PersonalLoanEntity.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "portfolio", "",
        "portfolio Detail is Mandatory");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicant", "",
        "Applicant Details are Mandatory");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "loanDetails", "",
        "Loan Details are Mandatory");


    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingDay", "",
        "meetingDay Detail is Mandatory");

    PersonalLoanEntity createLoanRequest = (PersonalLoanEntity) target;
    Applicant applicant = createLoanRequest.getApplicant();

    if (StringUtils.isEmpty(applicant.getPan())) {
      errors.rejectValue("applicant", "", "Pan is Mandatory");
    }
    if (StringUtils.isEmpty(applicant.getAadhar())) {
      errors.rejectValue("applicant", "", "Aadhaar is Mandatory");
    }
    if (StringUtils.isEmpty(applicant.getFirstName())) {
      errors.rejectValue("applicant", "", "First Name is Mandatory");
    }
    if (StringUtils.isEmpty(applicant.getLastName())) {
      errors.rejectValue("applicant", "", "Last Name is Mandatory");
    }

    if (StringUtils.isEmpty(applicant.getRegisteredEmail())) {
      errors.rejectValue("applicant", "", "Email Id is Mandatory");
    }
    if (StringUtils.isEmpty(applicant.getNetIncome())) {
      errors.rejectValue("applicant", "", "Net Income is Mandatory");
    }
    if (StringUtils.isEmpty(applicant.getGender())) {
      errors.rejectValue("applicant", "", "Gender is Mandatory");
    }

    Address address = applicant.getPermAddress();

    if (StringUtils.isEmpty(address.getAddress1())) {
      errors.rejectValue("permAddress", "", "Permanent Address is Mandatory");
    }
    if (StringUtils.isEmpty(address.getCity())) {
      errors.rejectValue("applicant", "", "Permanent Address city is Mandatory");
    }
    if (StringUtils.isEmpty(address.getPincode())) {
      errors.rejectValue("applicant", "", "Permanent Address pincode is Mandatory");
    }
    if (StringUtils.isEmpty(address.getState())) {
      errors.rejectValue("applicant", "", "Permanent Address state is Mandatory");
    }


  }

}
