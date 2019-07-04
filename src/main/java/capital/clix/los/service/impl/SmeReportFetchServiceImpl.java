package capital.clix.los.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.CoApplicant;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.bean.cibil.ApplicantCibilReportData;
import capital.clix.los.bean.cibil.ApplicantDelphiReportData;
import capital.clix.los.bean.cibil.ReportFetchRequest;
import capital.clix.los.bean.cibil.SmeReportFetchResponse;
import capital.clix.los.bean.fsa.BalanceSummaryFsaReport;
import capital.clix.los.bean.fsa.BalanceSummaryReport;
import capital.clix.los.bean.fsa.SmeFsaReport;
import capital.clix.los.bean.gst.GstSummary;
import capital.clix.los.bean.gst.MonthwiseSummary;
import capital.clix.los.bean.gst.SmeGSTReport;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.bean.sme.SmeStageReportMapping;
import capital.clix.los.commonUtility.CommonUtility;
import capital.clix.los.constants.ApplicationConstant;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.Property;
import capital.clix.los.enums.ReportStage;
import capital.clix.los.enums.ReportType;
import capital.clix.los.exception.LosException;
import capital.clix.los.service.IReportFetchService;
import capital.clix.serilization.JsonMarshallingUtil;

@Service("smeReportFetchServiceImpl")
public class SmeReportFetchServiceImpl implements IReportFetchService {


  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(SmeReportFetchServiceImpl.class);

  @Override
  public List<SmeReportFetchResponse> fetchReports(ReportFetchRequest reportFetchRequest) {

    LOG.info("Processing request to fetch report for :{}", reportFetchRequest.toString());

    String applicationId = reportFetchRequest.getApplicationId();
    ReportType reportType = reportFetchRequest.getReportType();
    ReportStage reportStage = reportFetchRequest.getReportStage();

    N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(applicationId);
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");
    try {
      if (reportStage != null)
        switch (reportStage) {
          case BUREAU:
            return fetchReportBasedOnStage(applicationId, reportStage, type);
          case BANK:
            return smePerfiosDelphiReport(applicationId, reportType, type);
          case GST:
        	  return gstFetchReportOnType(applicationId, reportType, type);
		  case FSA:
			  return smeFsaReport(applicationId, reportType, type);
        }
      if (reportType != null) {
        switch (reportType) {
          case CIBIL:
            return coApplicantCibilReport(applicationId, reportType, type);

          case DELPHI:
            return coApplicantDelphiReport(applicationId, reportType, type);

          case PERFIOS:
            return smePerfiosDelphiReport(applicationId, reportType, type);
          case NONE:
        	break;
        }
      }
    } catch (Exception e) {
      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
          "Exception occured while Fetching Report");
    }
    return null;
  }
  private List<SmeReportFetchResponse> smeFsaReport(String applicationId, ReportType reportType, String type) {

	  	LOG.info("Inside smeFsaReport() of SmeReportFetchServiceImpl");
		List<SmeReportFetchResponse> smeReportFetchResponseList = new ArrayList<SmeReportFetchResponse>();
		SmeReportFetchResponse smeReportFetchResponse = new SmeReportFetchResponse();
		List<BalanceSummaryReport> balanceSummaryList = new ArrayList<BalanceSummaryReport>();

		if (LoanType.BL.getCode().equalsIgnoreCase(type)) {

			BlLoanEntity blLoanEntity = couchBaseEgestorImpl.getApplicationEntity(applicationId, BlLoanEntity.class);

			List<Map<String, Object>> bankDataList = (ArrayList<Map<String, Object>>) blLoanEntity.getDocument()
					.get("BANK_STATEMENT");

			String smeFsaReport = null;
			for (Map<String, Object> map : bankDataList) {
				if (map.get("applicantCode") != null) {

					smeFsaReport = JsonMarshallingUtil.toString(couchBaseEgestorImpl.getEntityById(
							Prefix.FSA.getCode() + applicationId,
							SmeFsaReport.class));

					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNode = null;
					try {
						jsonNode = mapper.readTree(smeFsaReport);
					} catch (Exception exception) {
						throw new LosException(ErrorState.JSON_PARSE_ERROR, "json parsing error");
					}

					JsonNode totalApplicantReports = jsonNode.path("applicantReports");
					JsonNode applicantReportsInside = totalApplicantReports.path("applicantReports");

					for (JsonNode jsonNodeArr : applicantReportsInside) {
						JsonNode financialStatement = jsonNodeArr.path("FinancialStatement");
						JsonNode financialYear = financialStatement.path("FY");
						int year = financialYear.path("year").asInt();
						JsonNode profitAndLoss = financialYear.path("ProfitAndLoss");
						Double interestToPvtOutsideParties = profitAndLoss.path("InterestToPvtOutsideParties")
								.asDouble();
						Double interestPaidToBorrowersComingOnLoan = profitAndLoss
								.path("InterestPaidToBorrowersComingOnLoan").asDouble();
						Double tax = profitAndLoss.path("Tax").asDouble();
						Double otherNonCashExpenditurePrelimExpDefRevExp = profitAndLoss
								.path("OtherNonCashExpenditurePrelimExpDefRevExp").asDouble();
						Double extraordinaryItems = profitAndLoss.path("ExtraordinaryItems").asDouble();
						Double otherIncomeNonBusinessRelated = profitAndLoss.path("OtherIncomeNonBusinessRelated")
								.asDouble();
						Double administrativeExpenses = profitAndLoss.path("AdministrativeExpenses").asDouble();
						Double salaryToPartnersDirectorsNotComingOnLoan = profitAndLoss
								.path("SalaryToPartnersDirectorsNotComingOnLoan").asDouble();
						Double purchases = profitAndLoss.path("Purchases").asDouble();
						Double wages = profitAndLoss.path("Wages").asDouble();
						Double bankFinanceCharges = profitAndLoss.path("BankFinanceCharges").asDouble();
						Double salaryRentPaidToBorrowersComingOnLoan = profitAndLoss
								.path("SalaryRentPaidToBorrowersComingOnLoan").asDouble();
						Double interestOnOdCcAC = profitAndLoss.path("InterestOnOdCcAC").asDouble();
						Double badDebtsWrittenOff = profitAndLoss.path("BadDebtsWrittenOff").asDouble();
						Double closingStock = profitAndLoss.path("ClosingStock").asDouble();
						Double otherExpenses = profitAndLoss.path("OtherExpenses").asDouble();
						Double salesReceipts = profitAndLoss.path("SalesReceipts").asDouble();
						Double openingStock = profitAndLoss.path("OpeningStock").asDouble();
						Double interestToPartnersDirectorsNotComingOnLoan = profitAndLoss
								.path("InterestToPartnersDirectorsNotComingOnLoan").asDouble();
						Double depreciation = profitAndLoss.path("Depreciation").asDouble();
						Double otherIncomeBusinessRelated = profitAndLoss.path("OtherIncomeBusinessRelated").asDouble();
						Double changesInInventories = profitAndLoss.path("ChangesInInventories").asDouble();
						Double sellingAndDistributionExpenses = profitAndLoss.path("SellingAndDistributionExpenses")
								.asDouble();
						Double interestToFiBanksOtherThanOdCcAC = profitAndLoss.path("InterestToFiBanksOtherThanOdCcAC")
								.asDouble();
						Double manufacturingExpenses = profitAndLoss.path("ManufacturingExpenses").asDouble();

						JsonNode balanceSheet = financialYear.path("BalanceSheet");
						JsonNode liabilities = balanceSheet.path("Liabilities");
						Double deferredTaxLiabilities = liabilities.path("DeferredTaxLiabilities").asDouble();
						Double reservesAndSurplusExcludingRevaluationReserve = liabilities
								.path("ReservesAndSurplusExcludingRevaluationReserve").asDouble();
						Double unsecuredLoansFromBanksFi = liabilities.path("UnsecuredLoansFromBanksFi").asDouble();
						Double revaluationReserve = liabilities.path("RevaluationReserve").asDouble();
						Double workingCapitalLimitFromBanksFi = liabilities.path("WorkingCapitalLimitFromBanksFi")
								.asDouble();
						Double capital = liabilities.path("Capital").asDouble();
						Double otherLiabilities = liabilities.path("OtherLiabilities").asDouble();
						Double unsecuredLoansQuasiCapitalFromDirectorsShareholders = liabilities
								.path("UnsecuredLoansQuasiCapitalFromDirectorsShareholders").asDouble();
						Double currentMaturityOfTermLoans = liabilities.path("CurrentMaturityOfTermLoans").asDouble();
						Double securedLoansTermAndVehicleLoansEtc = liabilities
								.path("SecuredLoansTermAndVehicleLoansEtc").asDouble();
						Double unsecuredLoansOthers = liabilities.path("UnsecuredLoansOthers").asDouble();
						Double otherLongTermLiabilities = liabilities.path("OtherLongTermLiabilities").asDouble();
						Double sundryCreditors = liabilities.path("SundryCreditors").asDouble();

						JsonNode assets = balanceSheet.path("Assets");
						Double others = assets.path("Others").asDouble();
						Double accumulatedDepreciation = assets.path("AccumulatedDepreciation").asDouble();
						Double inventories = assets.path("Inventories").asDouble();
						Double revaluationReserve_Asserts = assets.path("RevaluationReserve").asDouble();
						Double debtorsGt6Months = assets.path("DebtorsGt6Months").asDouble();
						Double miscellaenousExpenditureToTheExtentNotWOffPLDrBalance = assets
								.path("MiscellaenousExpenditureToTheExtentNotWOffPLDrBalance").asDouble();
						Double grossPlantAndMachinery = assets.path("GrossPlantAndMachinery").asDouble();
						Double grossFixedAssetsExclPlantAndMachinery = assets
								.path("GrossFixedAssetsExclPlantAndMachinery").asDouble();
						Double deferredTaxAssets = assets.path("DeferredTaxAssets").asDouble();
						Double cashAndBank = assets.path("CashAndBank").asDouble();
						Double doubtfulLoansAndAdvances = assets.path("DoubtfulLoansAndAdvances").asDouble();
						Double loansAndAdvancesBusinessRelatedShortTerm = assets
								.path("LoansAndAdvancesBusinessRelatedShortTerm").asDouble();
						Double loansAndAdvancesBusinessRelatedSecurityDepositsOtherLongTermAdvances = assets
								.path("LoansAndAdvancesBusinessRelatedSecurityDepositsOtherLongTermAdvances")
								.asDouble();
						Double debtorsLt6Months = assets.path("DebtorsLt6Months").asDouble();
						Double loansAndAdvancesNonBusinessRelated = assets.path("LoansAndAdvancesNonBusinessRelated")
								.asDouble();
						Double intangibleAssets = assets.path("IntangibleAssets").asDouble();
						Double otherNonCurrentAssets = assets.path("OtherNonCurrentAssets").asDouble();
						Double investmentsBusinessRelated = assets.path("InvestmentsBusinessRelated").asDouble();
						Double investmentsNonBusinessRelated = assets.path("InvestmentsNonBusinessRelated").asDouble();
						
						// profit/loss as per books
						Double totalIncome = salesReceipts + otherIncomeBusinessRelated + otherIncomeNonBusinessRelated;
						Double costOfSales = openingStock + purchases + changesInInventories + closingStock
								+ manufacturingExpenses + wages;
						Double profitorloss1 = totalIncome - costOfSales;
						
				        DecimalFormat df = new DecimalFormat("#.##");
				       String profitorloss1FormatValue=df.format(profitorloss1);
				       
						// salesReceipts
				       String salesReceiptsFormatValue=df.format(salesReceipts);
				       
						// EBITDA
						Double ebitda = (profitorloss1 - otherIncomeNonBusinessRelated) - (administrativeExpenses
								+ sellingAndDistributionExpenses + salaryToPartnersDirectorsNotComingOnLoan
								+ bankFinanceCharges + otherExpenses);
						String ebitdaFormatValue=df.format(ebitda);
						
						//profit/loss Excl Non Income Business
						Double profitorloss2 = profitorloss1 - otherIncomeNonBusinessRelated;
						String profitorloss2FormatValue=df.format(profitorloss2);
						
						// NetWorkCapital
						Double currentAssets=((inventories+debtorsGt6Months+debtorsLt6Months+cashAndBank+deferredTaxAssets+others)-
								(debtorsGt6Months+deferredTaxAssets+loansAndAdvancesBusinessRelatedShortTerm));
						Double currentLiabilitiesandProvisions=((sundryCreditors+currentMaturityOfTermLoans+deferredTaxLiabilities+otherLiabilities)+
								(workingCapitalLimitFromBanksFi-deferredTaxLiabilities));
						Double netWorkCapital=currentAssets-currentLiabilitiesandProvisions;
						String netWorkCapitalFormatValue=df.format(netWorkCapital);
						
						//Debtor Days
						Double debtorDays=((debtorsGt6Months+debtorsLt6Months)/(salesReceipts*365.0d));
						String debtorDaysformatvalue=df.format(debtorDays);
						
						//Total Debt/EBITDA
						Double totalOutsideBorrowing=((workingCapitalLimitFromBanksFi+securedLoansTermAndVehicleLoansEtc+unsecuredLoansOthers+otherLongTermLiabilities)+currentMaturityOfTermLoans);
						Double totalDebtorEbitda=totalOutsideBorrowing/ebitda;
						String totalDebtorEbitdaFormatValue=df.format(totalDebtorEbitda);
						
						//InvestCoverageRatio
						Double ebitdaInclIntOnOdorcc=((profitorloss2-(administrativeExpenses+sellingAndDistributionExpenses+salaryToPartnersDirectorsNotComingOnLoan+bankFinanceCharges+otherExpenses))-interestToPartnersDirectorsNotComingOnLoan);
						Double investCoverageRatio=((ebitdaInclIntOnOdorcc)/(interestOnOdCcAC+interestToFiBanksOtherThanOdCcAC+interestToPvtOutsideParties));
						String investCoverageRatioFormatValue=df.format(investCoverageRatio);
						
						
						BalanceSummaryFsaReport balanceSummaryFsaReport=new BalanceSummaryFsaReport();
						balanceSummaryFsaReport.setYear(Integer.toString(year));
						balanceSummaryFsaReport.setProfitOrLossAsperBooks(profitorloss1FormatValue);
						balanceSummaryFsaReport.setProfitOrLossExclNonBusinessIncome(profitorloss2FormatValue);
						balanceSummaryFsaReport.setSalesOrReceipts(salesReceiptsFormatValue);
						balanceSummaryFsaReport.setEbitda(ebitdaFormatValue);
						balanceSummaryFsaReport.setNetWorkingCapital(netWorkCapitalFormatValue);
						balanceSummaryFsaReport.setDebtorDays(debtorDaysformatvalue);
						balanceSummaryFsaReport.setTotalDebtOrEBITDA(totalDebtorEbitdaFormatValue);
						balanceSummaryFsaReport.setInterestCoverageRatio(investCoverageRatioFormatValue);
						smeReportFetchResponseList.add(balanceSummaryFsaReport);
					}
				}
			}
		}
		return smeReportFetchResponseList;
	}

  private List<SmeReportFetchResponse> fetchReportBasedOnStage(String applicationId,
      ReportStage reportStage, String type) {

    SmeStageReportMapping smeStageReportMapping = couchBaseEgestorImpl.getEntity(
        ConfigUtil.getPropertyValue(Property.SME_REPORT_STAGE_MAPPING_KEY, String.class),
        SmeStageReportMapping.class);

    List<SmeReportFetchResponse> reportList = new ArrayList<>();

    List<ReportType> reportTypeList =
        smeStageReportMapping.getStageReportMap().get(reportStage.toString());

    if (LoanType.BL.getCode().equalsIgnoreCase(type)) {

      BlLoanEntity blLoanEntity =
          couchBaseEgestorImpl.getApplicationEntity(applicationId, BlLoanEntity.class);

      if (blLoanEntity != null) {
        List<CoApplicant> coApplicant = blLoanEntity.getCoApplicant();

        for (CoApplicant coApp : coApplicant) {
          reportList.add(
              fetchAllReportForCoApplicantBasedOnStage(applicationId, coApp, reportTypeList, type));
        }
      }
    }
    if (LoanType.LAEP.getCode().equalsIgnoreCase(type)) {

        LAEPLoanEntity laepLoanEntity =
            couchBaseEgestorImpl.getApplicationEntity(applicationId, LAEPLoanEntity.class);

        if (laepLoanEntity != null) {
          List<CoApplicant> coApplicant = laepLoanEntity.getCoApplicant();

          for (CoApplicant coApp : coApplicant) {
            reportList.add(
                fetchAllReportForCoApplicantBasedOnStage(applicationId, coApp, reportTypeList, type));
          }
        }
      }
    return reportList;
  }

  private SmeReportFetchResponse fetchAllReportForCoApplicantBasedOnStage(String applicationId,
      CoApplicant coApp, List<ReportType> reportTypeList, String type) {
    String key = null;

    SmeReportFetchResponse smeReportFetchResponse = new SmeReportFetchResponse();
    smeReportFetchResponse.setMobile(coApp.getMobile());
    smeReportFetchResponse.setPan(coApp.getPan());
    smeReportFetchResponse.setName(coApp.getFirstName());
    for (ReportType reportType : reportTypeList) {
      switch (reportType) {
        case CIBIL: {

          key = CommonUtility.keyBuilderForReport(reportType, applicationId, coApp.getId());
          fetchAndSetCibilReportData(key, coApp, applicationId, smeReportFetchResponse);
        }
        case DELPHI: {
          key = CommonUtility.keyBuilderForReport(reportType, applicationId, coApp.getId());
          setDelphiReportData(key, coApp, applicationId, smeReportFetchResponse);
        }
      }

    }
    return smeReportFetchResponse;
  }

  private List<SmeReportFetchResponse> coApplicantCibilReport(String applicationId,
      ReportType reportType, String type) {
    List<SmeReportFetchResponse> reportList = new ArrayList<>();

    if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
      BlLoanEntity blLoanEntity =
          couchBaseEgestorImpl.getApplicationEntity(applicationId, BlLoanEntity.class);
      if (blLoanEntity != null) {
        List<CoApplicant> coApplicant = blLoanEntity.getCoApplicant();

        for (CoApplicant coApp : coApplicant) {
          String key = CommonUtility.keyBuilderForReport(reportType, applicationId, coApp.getId());

          SmeReportFetchResponse reportFetchResponse = new SmeReportFetchResponse();
          reportFetchResponse.setMobile(coApp.getMobile());
          reportFetchResponse.setPan(coApp.getPan());
          reportFetchResponse.setName(coApp.getFirstName());
          fetchAndSetCibilReportData(key, coApp, applicationId, reportFetchResponse);
          reportList.add(reportFetchResponse);
        }
      }
    }
    if (LoanType.LAEP.getCode().equalsIgnoreCase(type)) {
        LAEPLoanEntity laepLoanEntity =
            couchBaseEgestorImpl.getApplicationEntity(applicationId, LAEPLoanEntity.class);
        if (laepLoanEntity != null) {
          List<CoApplicant> coApplicant = laepLoanEntity.getCoApplicant();

          for (CoApplicant coApp : coApplicant) {
            String key = CommonUtility.keyBuilderForReport(reportType, applicationId, coApp.getId());

            SmeReportFetchResponse reportFetchResponse = new SmeReportFetchResponse();
            reportFetchResponse.setMobile(coApp.getMobile());
            reportFetchResponse.setPan(coApp.getPan());
            reportFetchResponse.setName(coApp.getFirstName());
            fetchAndSetCibilReportData(key, coApp, applicationId, reportFetchResponse);
            reportList.add(reportFetchResponse);
          }
        }
      }
    return reportList;

  }
  private List<SmeReportFetchResponse> gstFetchReportOnType(String applicationId, ReportType reportType, String type){
	  List<SmeReportFetchResponse> reportList = new ArrayList<>();
	  if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
		  
		 String smeGstReport = null;
	  
		 smeGstReport = JsonMarshallingUtil.toString(couchBaseEgestorImpl.getEntityById(Prefix.GST.getCode()+applicationId,SmeGSTReport.class));
		 String gstReport = smeGstReport.replace("\\", "").
				 replace("GSTResponse\":\"{\"", "GSTResponse\":{\"")
				 .replace("]}\"}", "]}}");		

		LOG.info("gstReport data is : "+gstReport);
		 
		ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = null;
			try {
				jsonNode = mapper.readTree(gstReport);
			} catch (Exception exception) {
				throw new LosException(ErrorState.JSON_PARSE_ERROR, "json parsing error");
			}
			GstSummary gstSummary = new GstSummary();
			JsonNode totalApplicantReports = jsonNode.path("applicantReports");
			JsonNode gSTResponse = totalApplicantReports.path("GSTResponse");
			JsonNode gstResponse = gSTResponse.path("gstResponse");
			for(JsonNode gstResponse1 : gstResponse){
			JsonNode gstTypeList = gstResponse1.path("gstData");
			 for(JsonNode gstType : gstTypeList) {
			JsonNode monthwiseSummary = gstType.path("monthwiseSummary");
			List<MonthwiseSummary> monthWiseSummaryList = new ArrayList<>();
			Double totalTaxvalue = 0.00;
			    for(JsonNode monthSummary : monthwiseSummary ) {
			    	MonthwiseSummary monthwiseSummaryObj = new MonthwiseSummary();
			    	String ret_period = monthSummary.path("ret_period").asText();
			    	String ttl_val = monthSummary.path("ttl_val").asText();
			    	MonthwiseSummary  monthwiseSummary1 = new MonthwiseSummary();
			    	monthwiseSummary1.setRet_period(ret_period);
			    	monthwiseSummary1.setTtl_val(ttl_val);
			    	monthWiseSummaryList.add(monthwiseSummary1);
                    totalTaxvalue=totalTaxvalue+Double.parseDouble(ttl_val);
                    
             }
                String gstNumber = gstType.path("gstNumber").asText();
                Double annualizedTurnOver=(totalTaxvalue/monthWiseSummaryList.size())*12;
                // Double annualizedTurnOver=(NonAvailableMonthData+totalTaxvalue);
                String annualizedTurnOver1= Double.toString(annualizedTurnOver);
                BigDecimal bd = new BigDecimal(annualizedTurnOver);
                long val = bd.longValue();
                annualizedTurnOver1=Long.toString(val);            
                gstSummary.setAnnualizedTurnOver(annualizedTurnOver1);
			    gstSummary.setGstNumber(gstNumber);
			    gstSummary.setMonthwiseSummary(monthWiseSummaryList);
			    
			}
			 reportList.add(gstSummary);
			}
			LOG.info("GSTSummary data for applicationId :: {} is : "+applicationId,gstSummary.toString());
	      }
	     
	      
	  return  reportList;   
  }


  private List<SmeReportFetchResponse> coApplicantDelphiReport(String applicationId,
      ReportType reportType, String type) {
    List<SmeReportFetchResponse> delphiReportList = new ArrayList<>();

    if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
      BlLoanEntity blLoanEntity =
          couchBaseEgestorImpl.getApplicationEntity(applicationId, BlLoanEntity.class);
      if (blLoanEntity != null) {
        List<CoApplicant> coApplicant = blLoanEntity.getCoApplicant();
        for (CoApplicant coApp : coApplicant) {
          String key = CommonUtility.keyBuilderForReport(reportType, applicationId, coApp.getId());

          SmeReportFetchResponse reportFetchResponse = new SmeReportFetchResponse();
          reportFetchResponse.setMobile(coApp.getMobile());
          reportFetchResponse.setPan(coApp.getPan());
          reportFetchResponse.setName(coApp.getFirstName());
          setDelphiReportData(key, coApp, applicationId, reportFetchResponse);
          delphiReportList.add(reportFetchResponse);
        }
      }
    }
    if (LoanType.LAEP.getCode().equalsIgnoreCase(type)) {
        LAEPLoanEntity laepLoanEntity =
            couchBaseEgestorImpl.getApplicationEntity(applicationId, LAEPLoanEntity.class);
        if (laepLoanEntity != null) {
          List<CoApplicant> coApplicant = laepLoanEntity.getCoApplicant();
          for (CoApplicant coApp : coApplicant) {
            String key = CommonUtility.keyBuilderForReport(reportType, applicationId, coApp.getId());

            SmeReportFetchResponse reportFetchResponse = new SmeReportFetchResponse();
            reportFetchResponse.setMobile(coApp.getMobile());
            reportFetchResponse.setPan(coApp.getPan());
            reportFetchResponse.setName(coApp.getFirstName());
            setDelphiReportData(key, coApp, applicationId, reportFetchResponse);
            delphiReportList.add(reportFetchResponse);
          }
        }
      }
    return delphiReportList;

  }

  private List<SmeReportFetchResponse> smePerfiosDelphiReport(String applicationId,
      ReportType reportType, String type) {

    LOG.info("Fetching Delphi Perfios Report for application ID :{}", applicationId);
    List<SmeReportFetchResponse> delphiReportList = new ArrayList<>();
    try {

      if (LoanType.BL.getCode().equalsIgnoreCase(type)) {

        BlLoanEntity blLoanEntity =
            couchBaseEgestorImpl.getApplicationEntity(applicationId, BlLoanEntity.class);

        List<Map<String, Object>> bankDataList =
            (ArrayList<Map<String, Object>>) blLoanEntity.getDocument().get("BANK_STATEMENT");

        SmeReportFetchResponse smeReportFetchResponse = null;
        SmePerfiosReport smePerfiosReport = null;
        for (Map<String, Object> map : bankDataList) {
          if (map.get("applicantCode") != null) {

            String applicantCode = map.get("applicantCode").toString();
            smeReportFetchResponse = new SmeReportFetchResponse();
            smePerfiosReport =
                couchBaseEgestorImpl.getEntityById(Prefix.PERFIOS.getCode() + applicationId
                    + ApplicationConstant.SEPARATOR + applicantCode, SmePerfiosReport.class);

            if (smePerfiosReport != null) {

              String excelRepoortDmsId =
                  smePerfiosReport.getPerfiosExcelReportData().get("docId") != null
                      ? smePerfiosReport.getPerfiosExcelReportData().get("docId").toString()
                      : null;

              LinkedHashMap<String, String> delphiReportMap =
                  smePerfiosReport.getSmePerfiosDelphiReport().getPerfiodDelphiReport();

              if (delphiReportMap != null) {

                delphiReportMap.put("documentId", excelRepoortDmsId);
                delphiReportMap.put("sanctionLimit", smePerfiosReport.getSanctionLimit());
                delphiReportMap.put("accountHolderName", smePerfiosReport.getAccountHolderName());
                delphiReportMap.put("accountType", smePerfiosReport.getAccountType());
                delphiReportMap.put("yearMonthFrom", smePerfiosReport.getYearMonthFrom());
                delphiReportMap.put("yearMonthTo", smePerfiosReport.getYearMonthTo());

              }

              smeReportFetchResponse.setPerfiosDelphiReport(delphiReportMap);
              delphiReportList.add(smeReportFetchResponse);
            }

          }
        }
      }
      if (LoanType.LAEP.getCode().equalsIgnoreCase(type)) {

          LAEPLoanEntity laepLoanEntity =
              couchBaseEgestorImpl.getApplicationEntity(applicationId, LAEPLoanEntity.class);

          List<Map<String, Object>> bankDataList =
              (ArrayList<Map<String, Object>>) laepLoanEntity.getDocument().get("BANK_STATEMENT");

          SmeReportFetchResponse smeReportFetchResponse = null;
          SmePerfiosReport smePerfiosReport = null;
          for (Map<String, Object> map : bankDataList) {
            if (map.get("applicantCode") != null) {

              String applicantCode = map.get("applicantCode").toString();
              smeReportFetchResponse = new SmeReportFetchResponse();
              smePerfiosReport =
                  couchBaseEgestorImpl.getEntityById(Prefix.PERFIOS.getCode() + applicationId
                      + ApplicationConstant.SEPARATOR + applicantCode, SmePerfiosReport.class);

              if (smePerfiosReport != null) {

                String excelRepoortDmsId =
                    smePerfiosReport.getPerfiosExcelReportData().get("docId") != null
                        ? smePerfiosReport.getPerfiosExcelReportData().get("docId").toString()
                        : null;

                LinkedHashMap<String, String> delphiReportMap =
                    smePerfiosReport.getSmePerfiosDelphiReport().getPerfiodDelphiReport();

                if (delphiReportMap != null) {

                  delphiReportMap.put("documentId", excelRepoortDmsId);
                  delphiReportMap.put("sanctionLimit", smePerfiosReport.getSanctionLimit());
                  delphiReportMap.put("accountHolderName", smePerfiosReport.getAccountHolderName());
                  delphiReportMap.put("accountType", smePerfiosReport.getAccountType());
                  delphiReportMap.put("yearMonthFrom", smePerfiosReport.getYearMonthFrom());
                  delphiReportMap.put("yearMonthTo", smePerfiosReport.getYearMonthTo());

                }

                smeReportFetchResponse.setPerfiosDelphiReport(delphiReportMap);
                delphiReportList.add(smeReportFetchResponse);
              }

            }
          }
        }
    } catch (Exception e) {
      LOG.error("Error Occured while fetching Report for application id:{}", applicationId, e);
    }
    return delphiReportList;
  }

  private SmeReportFetchResponse fetchAndSetCibilReportData(String key, CoApplicant coApp,
      String applicationId, SmeReportFetchResponse reportFetchResponse) {

    ApplicantCibilReportData cibilReport =
        couchBaseEgestorImpl.getEntity(key, ApplicantCibilReportData.class);
    if (cibilReport != null) {
      reportFetchResponse.setApplicantCode(coApp.getId());
      reportFetchResponse.setApplicationId(applicationId);
      reportFetchResponse.setDocumentId(cibilReport.getDocumentId());
      reportFetchResponse.setExternalDocumentId(cibilReport.getExternalDocumentId());
    }
    return reportFetchResponse;
  }

  private SmeReportFetchResponse setDelphiReportData(String key, CoApplicant coApp,
      String applicationId, SmeReportFetchResponse reportFetchResponse) {

    ApplicantDelphiReportData delphiReport =
        couchBaseEgestorImpl.getEntity(key, ApplicantDelphiReportData.class);

    if (delphiReport != null) {
      reportFetchResponse.setApplicantCode(coApp.getId());
      reportFetchResponse.setApplicationId(applicationId);
      reportFetchResponse.setDelphiReport(delphiReport.getDelphiReport());
    }
    return reportFetchResponse;
  }


}
