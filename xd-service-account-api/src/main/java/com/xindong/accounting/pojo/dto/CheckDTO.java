package com.xindong.accounting.pojo.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckDTO implements Serializable {
	@Excel(name = "科目编码")
	private String subjectNo;
	@Excel(name = "比较金额")
	private BigDecimal balance;

}
