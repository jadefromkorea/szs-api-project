package com.api.szsapiproject.controller;

import com.api.szsapiproject.dto.Result;
import com.api.szsapiproject.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/szs")
@RestController
public class ScrapInfoController {

    public static final int MIN_STD_TOT_SALARY_AMT = 33000000;          // 총 급여액 - 최소 구간 기준 금액
    public static final int MAX_STD_TOT_SALARY_AMT = 70000000;          // 총 급여액 - 최대 구간 기준 금액
    public static final int BASE_TAX_CREDIT_LIMIT_AMT1 = 740000;        // 기본 세액공제 한도액(총 급여액 [3,300만원] 이하)
    public static final int BASE_TAX_CREDIT_LIMIT_AMT2 = 660000;        // 기본 세액공제 한도액(총 급여액 [3,300만원] 초과 [7,000만원] 이하)
    public static final int BASE_TAX_CREDIT_LIMIT_AMT3 = 500000;        // 기본 세액공제 한도액(총 급여액 [7,000만원] 초과)
    public static final int STD_FINAL_TAX_AMT_FOR_TAX_CREDIT = 1300000; // 세액공제 계산을 위한 산출세액 기준액
    public static final int BASE_TAX_CREDIT_AMT = 715000;               // 세액공제 계산시 사용되는 공제 금액(130만원 초과시)

    @PostMapping("/scrap")
    public ResponseEntity getScrapInfo(@RequestBody Member member) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://codetest.3o3.co.kr/scrap/";

        Result result = restTemplate.postForObject(url, member, Result.class);

        // 총급여
        int totSalary = Integer.valueOf(result.getJsonList().getScrap001().get(0).get("총지급액"));

        // 산출세액
        int finalTax = Integer.valueOf(result.getJsonList().getScrap002().get(0).get("총사용금액"));

        // 근로소득 세액공제 한도액 가져오기
        int taxCreditLimitAmt = getTaxCreditLimit(totSalary);

        // 근로소득 세액공제액 가져오기
        int taxCreditAmt = getTaxCredit(finalTax);

        // 환급액 가져오기
        int returnAmt = getReturnAmt(taxCreditLimitAmt, taxCreditAmt);

        // Map형식의 응답데이터 가져오기 - 반환 시 map형식을 json형식으로 변환하여 반환한다.
        Map resultMap = getResultMap(member, taxCreditLimitAmt, taxCreditAmt, returnAmt);

        if (resultMap != null) {
            return new ResponseEntity(resultMap, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 근로소득 세액공제 한도액을 계산한다.
     * @param totSalary 총급여
     * @return 세액공제 한도액
     */
    private int getTaxCreditLimit(int totSalary) {
        int taxCreditLimit;  // 세액공제 한도액

        if(totSalary <= MIN_STD_TOT_SALARY_AMT) { // 총 금여액이 3,300만원 이하
            taxCreditLimit = BASE_TAX_CREDIT_LIMIT_AMT1;
        } else if(totSalary < MAX_STD_TOT_SALARY_AMT) { // 총 금여액이 7,000만원 초과
            taxCreditLimit = (int)(BASE_TAX_CREDIT_LIMIT_AMT1 - ((totSalary - MIN_STD_TOT_SALARY_AMT) * 0.008));
            taxCreditLimit = Math.max(taxCreditLimit, BASE_TAX_CREDIT_LIMIT_AMT2);
        } else { // 총 금여액이 3,300만원 초가 7,000만원 이하하
            taxCreditLimit = (int)(BASE_TAX_CREDIT_LIMIT_AMT2 - ((totSalary - MAX_STD_TOT_SALARY_AMT) * 0.5));
            taxCreditLimit = Math.max(taxCreditLimit, BASE_TAX_CREDIT_LIMIT_AMT3);
        }

        return taxCreditLimit;
    }

    /**
     * 근로소득 세액공제액을 계산한다.
     * @param finalTax
     * @return 세액공제액
     */
    private int getTaxCredit(int finalTax) {
        int taxCredit;  // 세액공제액

        if(finalTax <= STD_FINAL_TAX_AMT_FOR_TAX_CREDIT) {
            taxCredit = (int)(finalTax * 0.55);
        } else {
            taxCredit = (int)(BASE_TAX_CREDIT_AMT + ((finalTax - STD_FINAL_TAX_AMT_FOR_TAX_CREDIT) * 0.3));
        }
        return taxCredit;
    }

    /**
     * 세액공제 한도액과 산출세액을 비교하여 적은 금액을 환급액으로 한다.
     * @param taxCreditLimit    근로소득 세액공제 한도액
     * @param taxCredit 근로소득 세액공제액
     * @return 환급액
     */
    private int getReturnAmt(int taxCreditLimit, int taxCredit) {
        int returnAmt = Math.min(taxCreditLimit, taxCredit);
        log.info(">>>>> 환급액 returnAmt: " + returnAmt);

        return returnAmt;
    }

    /**
     * 필요한 정보들을 받아 Map형식의 응답데이터를 만든다.
     * @param member            이름
     * @param taxCreditLimit    근로소득 세액공제 한도액
     * @param taxCredit         근로소득 세액공제액
     * @param returnAmt         환급액
     * @return
     */
    private Map getResultMap(Member member, int taxCreditLimit, int taxCredit, int returnAmt) {
        Map resultMap = new HashMap();

        resultMap.put("이름", member.getName());
        resultMap.put("한도", taxCreditLimit);
        resultMap.put("공제액", taxCredit);
        resultMap.put("환급액", returnAmt);

        return resultMap;
    }
}
