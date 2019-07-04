package capital.clix.los.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Copyright 2018 CLIX CAPITAL (P) Limited . All Rights Reserved. CLIX CAPITAL
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @author Naveen
 * @version 1.0, 28/11/18
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.AdditionalDetails;
import capital.clix.los.bean.ApplicationClose;
import capital.clix.los.bean.BackupFile;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.CommonRequest;
import capital.clix.los.bean.DocumentCollection;
import capital.clix.los.bean.ErrorResponseDto;
import capital.clix.los.bean.FieldsSearchResponse;
import capital.clix.los.bean.PersonalLoanEntity;
import capital.clix.los.bean.ProcessorDto;
import capital.clix.los.bean.bl.ApplicationSearch;
import capital.clix.los.bean.bl.BankStatementRequest;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.bean.cibil.ReportFetchRequest;
import capital.clix.los.bean.cibil.ReportFetchResponse;
import capital.clix.los.bean.fsa.FinancialStatementAnalysisRequest;
import capital.clix.los.bean.fsa.SmeFsaReport;
import capital.clix.los.bean.gst.GstStatementRequest;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.bean.pb.PBExcelGenerateInput;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.constants.ApplicationConstant;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.EgestionChainer;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.Property;
import capital.clix.los.exception.LosException;
import capital.clix.los.kafka.IKafkaProducer;
import capital.clix.los.scheduler.PBExcelGenerator;
import capital.clix.los.service.IDataService;
import capital.clix.los.validator.ValidatorFactory;
import capital.clix.serilization.JsonMarshallingUtil;
import capital.clix.startup.ILoadProperties;

@RestController
@RequestMapping("/los/v1")
public class Controller {

  @Autowired
  private CouchbaseTemplate template;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  private EgestionChainer egestionChainer;

  @Autowired
  ValidatorFactory validatorFactory;

  @Autowired
  @Qualifier("dataServiceImpl")
  IDataService dataServiceImpl;

  @Autowired
  private ILoadProperties loadProperty;

  @Autowired
  private IKafkaProducer kafkaProducer;

  @Autowired
  private PBExcelGenerator excelGenerator;
  
  private static final Logger LOG = LogManager.getLogger(Controller.class);

  @GetMapping("/loan")
  public ResponseEntity<BaseResponse> getLoanBasedOnId(@RequestParam String id,
      HttpServletRequest request) {

    BaseResponse response = new BaseResponse();
    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);
    try {
      LOG.info("Loan Fetch Request Received with id :{}, corelation_id:{} ", id, corelation_id);
      BaseEntity req = new BaseEntity();
      req.setId(id);
      req.setUuid(corelation_id);


      egestionChainer.fetchLoan(req, response);
    } catch (LosException ex) {

    }
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/niql")
  public ResponseEntity<PersonalLoanEntity> getLoanBasedOnNiqlQuery(@RequestParam String id) {

    LOG.info("Loan Fetch Request Received with id :- " + id);
    PersonalLoanEntity reqData = couchBaseIntegrationImpl.getLoanBasedOnBranch("");
    return ResponseEntity.ok().body(reqData);
  }

  @PostMapping("/loan")
  public ResponseEntity<BaseResponse> createLoan(@RequestBody CommonRequest commonRequest,
      BindingResult bindingResult, Errors error, BaseResponse response,
      HttpServletRequest request) {

    String uuid = request.getHeader(ApplicationConstant.HEADER_CORELATION);
    LOG.info("Loan Create Request Received with data :{} and uuid:{} ",
        JsonMarshallingUtil.toString(commonRequest), uuid);

    try {
      System.out.println(new Date().getTime());
      if (LoanType.BL == commonRequest.getLoanType()) {
        BlLoanEntity blLoanRequest = commonRequest.getReq();
        blLoanRequest.setUuid(uuid);

        blLoanRequest.setType(commonRequest.getLoanType().getCode());
        blLoanRequest.setEntityType(commonRequest.getLoanType());
        egestionChainer.createLoan(blLoanRequest, response);
        response.setSuccess(true);
      } else if (LoanType.PL == commonRequest.getLoanType()) {
        PersonalLoanEntity plLoanEntity = commonRequest.getPersonalLoanEntity();
        plLoanEntity.setEntityType(commonRequest.getLoanType());
        plLoanEntity.setType(commonRequest.getLoanType().getCode());
        egestionChainer.createLoan(plLoanEntity, response);
        response.setSuccess(true);
      } else if (LoanType.SME_PB == commonRequest.getLoanType()) {
        PBLoanEntity pbEntity = commonRequest.getPbLoanEntity();
        pbEntity.setEntityType(commonRequest.getLoanType());
        pbEntity.setType(commonRequest.getLoanType().getCode());
        egestionChainer.createPBLoan(pbEntity, response);
        response.setSuccess(true);
      }
      else if (LoanType.LAEP == commonRequest.getLoanType()) {
          LAEPLoanEntity laepEntity = commonRequest.getLaepLoanEntity();
          laepEntity.setUuid(uuid);
          laepEntity.setEntityType(commonRequest.getLoanType());
          laepEntity.setType(commonRequest.getLoanType().getCode());
          egestionChainer.createLoan(laepEntity, response);
          response.setSuccess(true);
        }
      
      System.out.println(new Date().getTime());
      return ResponseEntity.ok().body(response);
    } catch (LosException ex) {
      ex.printStackTrace();
      LOG.error("Error occured with message:-" + ex.getMessage());
      response.setSuccess(false);
      return ResponseEntity.ok().body(response);
    }
  }

  // @PutMapping("/loan")
  public ResponseEntity<BaseResponse> updateLoan(@RequestBody PersonalLoanEntity loanRequest,
      BindingResult bindingResult, Errors error, BaseResponse response) {


    LOG.info("Loan Update Request Received with data :- " + loanRequest.toString());
    try {

      Validator createLoanValidator = validatorFactory.getValidatorInstance(loanRequest);

      createLoanValidator.validate(loanRequest, error);
      if (error.hasErrors()) {

        List<FieldError> errorList = error.getFieldErrors();
        Iterator<FieldError> itr = errorList.iterator();
        ArrayList<String> msgList = new ArrayList<String>();
        while (itr.hasNext()) {
          msgList.add(itr.next().getDefaultMessage());
        }
        response.setErrorMessage(msgList);
        throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
            error.getFieldError().getDefaultMessage());
      }

      BaseEntity req = null;// couchBaseIntegrationImpl.getLoanEntity(loanRequest.getId().toString());
      couchBaseIntegrationImpl.updateLoanEntity(loanRequest);
      response.setSuccess(true);
      response.setResponse("Doc updated");
    } catch (Exception ex) {
      ex.printStackTrace();
      response.setSuccess(false);
      response.setResponse("No Doc Found");
    }
    return ResponseEntity.ok().body(response);
  }

  // @GetMapping("/getUser")
  // public List<Person> getUser() {
  // List<Person> li = null;
  // try {
  // // return repo.findByFirstName("firstName");
  // Iterable<Person> itr = repo.findAllAdmins();// findAll();
  //
  // li = new ArrayList<Person>();
  //
  // Iterator<Person> itr1 = itr.iterator();
  //
  // while (itr1.hasNext())
  // li.add(itr1.next());
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // return li;
  // }

  @PostMapping("/perfios/callback")
  public ResponseEntity<BaseResponse> perfiosCallback(@RequestBody LinkedTreeMap applicantReports,
      BaseResponse response, HttpServletRequest request) {

    LOG.info("Perfios Callback Received with Data :{}", applicantReports);
    
    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);
    
    SmePerfiosReport perfiosCallbackRequest = new SmePerfiosReport();

    try {
      perfiosCallbackRequest
          .setLoanApplicationId(applicantReports.get("loanApplicationId").toString());
      perfiosCallbackRequest.setUuid(corelation_id);
      perfiosCallbackRequest.setApplicantReports(applicantReports);
      perfiosCallbackRequest
          .setSuccess(Boolean.valueOf(applicantReports.get("success").toString()));
      LOG.info("Perfios Callback Reveived with request Data:{},corelation_id:{}",
          perfiosCallbackRequest.toString(), corelation_id);
      N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(perfiosCallbackRequest.getLoanApplicationId());
      
      
      egestionChainer.updatePerfiosReport(perfiosCallbackRequest, response);
    } catch (Exception e) {
      LOG.info("Unable to process Perfios report for application id:- {}, with error:{} ",
          applicantReports.get("loanApplicationId"), e.getMessage());
      response.setApplicationId(perfiosCallbackRequest.getLoanApplicationId());
      response.setResponse("Perfios Exited with error");
      ErrorResponseDto responseDto = new ErrorResponseDto();
      responseDto.setMessage("Perfios Exited with error");
      response.setSuccess(false);
      response.setErrorResponseDto(responseDto);

    }
    return ResponseEntity.ok().body(response);
  }



  @PostMapping("/fsa/callback")
  public ResponseEntity<BaseResponse> fsaCallback(@RequestBody LinkedTreeMap applicantReports,
      BaseResponse response, HttpServletRequest request) {

    LOG.info("FSA Callback Received with Data :{}", applicantReports);
    
    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);
   
    SmeFsaReport fsaCallbackRequest = new SmeFsaReport();

    try {
      fsaCallbackRequest.setLoanApplicationId(applicantReports.get("loanApplicationId").toString());
      fsaCallbackRequest.setUuid(corelation_id);
      fsaCallbackRequest.setApplicantReports(applicantReports);
      fsaCallbackRequest.setSuccess(Boolean.valueOf(applicantReports.get("success").toString()));
      LOG.info("FSA Callback Reveived with request Data:{},corelation_id:{}",
          fsaCallbackRequest.toString(), corelation_id);
      
      egestionChainer.updateFsaReport(fsaCallbackRequest, response);
      LOG.info("FSA Callback Completed");

    } catch (Exception e) {
      response.setApplicationId(fsaCallbackRequest.getLoanApplicationId());
      response.setResponse("FSA Exited with error");
      ErrorResponseDto responseDto = new ErrorResponseDto();
      responseDto.setMessage("FSA Exited with error");
      response.setSuccess(false);
      response.setErrorResponseDto(responseDto);

    }
    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/loan/search")
  public ResponseEntity<BaseResponse> getLoanBasedOnSearchParams(
      @RequestBody ApplicationSearch applicationSearch, HttpServletRequest request) {

    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);
    LOG.info("Search Request Received is:{}, corelation_id:{}", applicationSearch.toString(),
        corelation_id);
    FieldsSearchResponse response = new FieldsSearchResponse();

    List<Map> dataList = null;
    Map finalDataMap = new HashMap();
    try {
      applicationSearch.setUuid(corelation_id);
      dataList = dataServiceImpl.applicationSearch(applicationSearch);
      if (dataList != null && !dataList.isEmpty()) {
        response.setSuccess(true);
        response.setDataList(dataList);
      }

    } catch (LosException ex) {
      LOG.error(
          "An exception occured while search for application with request Data:{},corelation_id is:{}, Exception is:{}",
          applicationSearch.toString(), corelation_id, ex.getMessage());
    }
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/loan/status")
  public ResponseEntity<ApplicationStage> getApplicationStatus(@RequestParam String id,
      HttpServletRequest request) {


    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);
    LOG.info(
        "Fetch Application Status request received for applicationId:{}, with corelation_id:{} ",
        id, corelation_id);

    ApplicationStage status = null;
    try {
      // Sentry.capture("Captured");
      // LOG.error("Sentry Spring");
      // int i = 1 / 0;

      status = dataServiceImpl.fetchStatusById(id);

      LOG.info("Status Received for application Id:{},corelation_id:{},is:{}", id, corelation_id,
          status);

    } catch (LosException ex) {
      LOG.error(
          "An exception occured while Fetching Status for application id:{},corelation_id:{}, Exception is:{}",
          id, corelation_id, ex.getMessage());
    }
    return ResponseEntity.ok().body(status);
  }

  @PostMapping("/loan/bankStatement")
  public ResponseEntity<BaseResponse> saveBankStatement(
      @RequestBody BankStatementRequest bankStatementRequest, BaseResponse response,
      HttpServletRequest request) {

    LOG.info("Bank Statement received for applicatioin Id :{} and doc received is:{}",
        bankStatementRequest.getLoanApplicationId(), bankStatementRequest.getDocument());

    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);

    try {
      bankStatementRequest.setUuid(corelation_id);
      egestionChainer.updateBankStatement(bankStatementRequest, response);
      response.setResponse("Bank statement Updated Successfully");
      response.setSuccess(true);
    } catch (Exception e) {
      response.setApplicationId(bankStatementRequest.getLoanApplicationId());
      response.setResponse("Unable To update Bank statement");
      ErrorResponseDto responseDto = new ErrorResponseDto();
      responseDto.setMessage("Report Does not Exist");
      response.setSuccess(false);
      response.setErrorResponseDto(responseDto);
    }
    return ResponseEntity.ok().body(response);

  }

  @PostMapping("/loan/fsa")
  public ResponseEntity<BaseResponse> saveFinancialStatementAnalysis(
      @RequestBody FinancialStatementAnalysisRequest financialStatementAnalysisRequest,
      BaseResponse response, HttpServletRequest request) {

    LOG.info("FSA Statement received for applicatioin Id :{} and doc received is:{}",
        financialStatementAnalysisRequest.getLoanApplicationId(),
        financialStatementAnalysisRequest.getDocument());

    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);

    try {
      financialStatementAnalysisRequest.setUuid(corelation_id);
      egestionChainer.updateFinancialStatementAnalysis(financialStatementAnalysisRequest, response);
      response.setResponse(" FSA Updated Successfully");
      response.setSuccess(true);
    } catch (Exception e) {
      response.setApplicationId(financialStatementAnalysisRequest.getLoanApplicationId());
      response.setResponse("Unable To update FSA statement");
      ErrorResponseDto responseDto = new ErrorResponseDto();
      responseDto.setMessage("Report Does not Exist");
      response.setSuccess(false);
      response.setErrorResponseDto(responseDto);
    }
    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/loan/gst")
  public ResponseEntity<BaseResponse> saveGstStatement(
      @RequestBody GstStatementRequest gstStatementRequest, BaseResponse response,
      HttpServletRequest request) {

    LOG.info("Bank Statement received for applicatioin Id :{} and doc received is:{}",
        gstStatementRequest.getLoanApplicationId(), gstStatementRequest.getDocument());

    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);

    try {
      gstStatementRequest.setUuid(corelation_id);
      egestionChainer.updateGstStatement(gstStatementRequest, response);
      response.setResponse("Gst statement Updated Successfully");
      response.setSuccess(true);
    } catch (Exception e) {
      response.setApplicationId(gstStatementRequest.getLoanApplicationId());
      response.setResponse("Unable To update Gst statement");
      ErrorResponseDto responseDto = new ErrorResponseDto();
      responseDto.setMessage("Report Does not Exist");
      response.setSuccess(false);
      response.setErrorResponseDto(responseDto);
    }
    return ResponseEntity.ok().body(response);

  }

  @PostMapping("/loan/close")
  public ResponseEntity<BaseResponse> closeLoan(@RequestBody ApplicationClose applicationClose,
      BaseResponse response, HttpServletRequest request) {

    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);
    LOG.info("Application Close Request received :{}, corelation_id:{}",
        applicationClose.toString(), corelation_id);

    try {
      applicationClose.setUuid(corelation_id);
      dataServiceImpl.closeLoan(applicationClose, response);
    } catch (Exception e) {
      LOG.error(
          "An exception occured while closing application with request Data:{},corelation_id:{}, Exception is:{}",
          applicationClose.toString(), corelation_id, e);
    }
    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/loan/report")
  public ResponseEntity<ReportFetchResponse> getReports(
      @RequestBody ReportFetchRequest reportFetchRequest) {

    LOG.info("Report Fetch Request Received for applicationId:{},with data:{}",
        reportFetchRequest.getApplicationId(), reportFetchRequest.toString());
    ReportFetchResponse smeReportResponse = new ReportFetchResponse();
    try {
      List reportDataList = dataServiceImpl.getReports(reportFetchRequest);
      LOG.info("Report Fetched for applicationId :: {} "+ reportFetchRequest.getApplicationId());
      if (reportDataList == null || reportDataList.isEmpty()) {

        ErrorResponseDto responseDto = new ErrorResponseDto();
        responseDto.setMessage("Report Does not Exist");
        smeReportResponse.setSuccess(false);
      } else {
        smeReportResponse.setSuccess(true);
        smeReportResponse.setLi(reportDataList);
      }
    } catch (Exception e) {
      smeReportResponse.setSuccess(false);
      ErrorResponseDto errorResponseDto = new ErrorResponseDto();
      errorResponseDto.setMessage(e.getMessage());
      smeReportResponse.setErrorResponseDto(errorResponseDto);
    }
    return ResponseEntity.ok().body(smeReportResponse);

  }

  @GetMapping("/reloadCache")
  public ResponseEntity<String> reloadCache(@RequestParam String service, @RequestParam String pass)
      throws IOException {
    if (!"LOS".equalsIgnoreCase(service) || !ConfigUtil
        .getPropertyValue(Property.CACHE_RELOAD_PASSWORD, String.class).equals(pass)) {
      return ResponseEntity.ok().body("UNAUTHORIZED");
    }
    loadProperty.loadAllCache(true);

    System.out.println(ConfigUtil.getPropertyValue(Property.NOTIFICATION_EMAIL_URL, String.class));

    return ResponseEntity.ok().body("SUCCESS");
  }

  @PostMapping("/sme/workflow")
  public void initiateSmeWorkflow(@RequestBody ProcessorDto processorDto) {

    LOG.info(" Request Received to initiate Workflow Directly with Data:{}",
        processorDto.toString());
    try {
      kafkaProducer.initiateWorkFlow(processorDto);
    } catch (Exception e) {
      LOG.error("An Exception Occured while intiating Work flow with Data :{}, Exception :{}",
          processorDto.toString(), e.getCause());
    }
  }

  @GetMapping("/loan/sme/perfios/report")
  public ResponseEntity<SmePerfiosReport> getSmePerfiosReport(@RequestParam String applicationId,
      @RequestParam String applicantCode, HttpServletRequest request) {

    LOG.info("Request Received to fetch perfios report for application id:{} and applicantCode:{}",
        applicationId, applicantCode);

    SmePerfiosReport smePerfiosreport = dataServiceImpl.fetchSmePerfiosReport(
        Prefix.PERFIOS.getCode() + applicationId + ApplicationConstant.SEPARATOR + applicantCode);

    LOG.info("Perfios Report fetched for application id:{} and applicantCode :{} is :{}",
        applicationId, applicantCode, smePerfiosreport);

    return ResponseEntity.ok().body(smePerfiosreport);
  }

  @PutMapping("/loan/{id}")
  public ResponseEntity<String> addAdditionalDetails(@PathVariable("id") String id,
      @RequestBody AdditionalDetails additionalDetail, BaseResponse response) {

    try {
      additionalDetail.setId(id);
      egestionChainer.updateLoan(additionalDetail, response);

    } catch (Exception e) {
      LOG.error(" Error Occured while updating the loan with id :{},error is :{} ", id, e);
    }
    return ResponseEntity.ok().body("Done");
  }

  @PostMapping("/loan/bl/gst")
  public ResponseEntity<BaseResponse> saveGstStatement(
      @RequestBody DocumentCollection docCollectionRequest, BaseResponse response,
      HttpServletRequest request) {

    LOG.info(" GST Statement received for applicatioin Id :{} and doc received is:{}",
        docCollectionRequest.getLoanApplicationId(), docCollectionRequest.getDocument());

    String corelation_id = request.getHeader(ApplicationConstant.HEADER_CORELATION);

    try {
      docCollectionRequest.setUuid(corelation_id);
      // egestionChainer.updateGstStatement(docCollectionRequest, response);
      response.setResponse("Bank statement Updated Successfully");
      response.setSuccess(true);
    } catch (Exception e) {
      response.setApplicationId(docCollectionRequest.getLoanApplicationId());
      response.setResponse("Unable To update Bank statement");
      ErrorResponseDto responseDto = new ErrorResponseDto();
      responseDto.setMessage("Report Does not Exist");
      response.setSuccess(false);
      response.setErrorResponseDto(responseDto);
    }
    return ResponseEntity.ok().body(response);

  }


  @GetMapping("/backup")
  public void createBackupFile(@RequestParam String id) {

    ArrayList<String> keyList = new ArrayList<>();
    ArrayList<BackupFile> dataList = new ArrayList<>();
    keyList.add("PUSH_NOTIFICATION_URL");
    keyList.add("SME:REPORT-STAGE");
    keyList.add("CONFIG_PROPERTY");
    BackupFile bkp = new BackupFile();
    for (String str : keyList) {
      String content = couchBaseIntegrationImpl.fetchData(str);

      bkp = new BackupFile();
      bkp.setKey(id);
      bkp.setData(content);
      dataList.add(bkp);
    }



    BufferedWriter writer;
    try {
      writer = new BufferedWriter(new FileWriter("/home/CLIX/100405/Desktop/samplefile1.txt"));
      writer.write(new Gson().toJson(bkp));
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @GetMapping("/dateWisePBCases")
  public void dateWisePBCases(@RequestParam String id) {

    excelGenerator.dateWisePBCases(id);

  }

  @PostMapping("/getMisFromList")
  public void getMisFromList(@RequestBody PBExcelGenerateInput pBExcelGenerateInput) {


    LOG.info("Ids received for list :{} ", pBExcelGenerateInput.getPbIdList());
    // System.out.println(pBExcelGenerateInput.getPbIdList());
    // ArrayList<String> pbList = new ArrayList<>();
    excelGenerator.prepareFromPBIds(pBExcelGenerateInput.getPbIdList());

  }

}

