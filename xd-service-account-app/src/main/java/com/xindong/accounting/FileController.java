package com.xindong.accounting;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xindong.accounting.api.ResponseEnum;
import com.xindong.accounting.pojo.dto.CheckDTO;
import com.xindong.accounting.pojo.dto.ResultDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/accounting")
public class FileController {

	private static final String SAVE_PATH = "D:/excel/";


	@RequestMapping(value = "/check")
	public String check(@RequestParam("file") MultipartFile file) throws Exception {
		ImportParams importParams = new ImportParams();
		importParams.setStartSheetIndex(0);
		List<CheckDTO> subList = ExcelImportUtil.importExcel(file.getInputStream(), CheckDTO.class, importParams);
		subList = subList.stream().filter(item -> StringUtils.isNotBlank(item.getSubjectNo())).map(item -> {
			item.setSubjectNo(item.getSubjectNo().replace("，", ",").trim());
			return item;
		}).collect(Collectors.toList());
		ResponseEnum.BAD_LICENCE_TYPE.assertNotNull(subList, "123");
		Map<String, BigDecimal> subDataMap = removeMultiData(subList);
		importParams.setStartSheetIndex(1);
		List<CheckDTO> detailList = ExcelImportUtil.importExcel(file.getInputStream(), CheckDTO.class, importParams);
		detailList = detailList.stream().filter(item -> StringUtils.isNotBlank(item.getSubjectNo())).map(item -> {
			item.setSubjectNo(item.getSubjectNo().replace("，", ",").trim());
			return item;
		}).collect(Collectors.toList());
		Map<String, BigDecimal> detailDataMap = removeMultiData(detailList);
		List<CheckDTO> allList = new ArrayList<>();
		allList.addAll(subList);
		allList.addAll(detailList);
		Set<String> allSubjectNoList = allList.stream().filter(item -> StringUtils.isNotBlank(item.getSubjectNo()))
				.map(CheckDTO::getSubjectNo).collect(Collectors.toSet());
		List<ResultDTO> result = Lists.newArrayList();
		allSubjectNoList.stream().forEach(subjectNo -> {
			ResultDTO resultDTO = new ResultDTO();
			resultDTO.setSubjectNo(subjectNo);
			BigDecimal subjectBalance = subDataMap.getOrDefault(subjectNo, BigDecimal.ZERO);
			BigDecimal detailBalance = detailDataMap.getOrDefault(subjectNo, BigDecimal.ZERO);
			resultDTO.setSubjectBalance(subjectBalance.toPlainString());
			resultDTO.setDetailBalance(detailBalance.toPlainString());
			resultDTO.setDiffBalance(subjectBalance.subtract(detailBalance).toPlainString());
			result.add(resultDTO);
		});
		String fileOriginName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Workbook workbook = ExcelExportUtil.exportExcel(
				new ExportParams(null, null, "测试"),
				ResultDTO.class,
				result);
		File saveFile = new File(SAVE_PATH);
		if (!saveFile.exists()) {
			saveFile.mkdirs();
		}
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String fileName = fileOriginName + time + ".xlsx";
		FileOutputStream fos = new FileOutputStream("D:/excel/" + fileName);
		workbook.write(fos);
		fos.close();
		return fileName;
	}

	@RequestMapping("/getFile")
	public void getFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws Exception {
		response.setContentType("application/force-download");
		response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName.trim(), "UTF-8"));
		byte[] buffer = new byte[1024];
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(SAVE_PATH + fileName));
		OutputStream outputStream = response.getOutputStream();
		int i = bis.read(buffer);
		while (i != -1) {
			outputStream.write(buffer, 0, i);
			i = bis.read(buffer);
		}
		outputStream.flush();
		bis.close();
		outputStream.close();
	}

	@RequestMapping("/getTemplate")
	public void getTemplate(HttpServletResponse response) throws Exception {
		String fileName = "比较模板.xlsx";
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/" + fileName);
		response.setHeader("Access-Control-Expose-Headers", "content-disposition");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/octet-stream");
		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		int len = 0;
		byte bytes[] = new byte[1024];
		OutputStream out = response.getOutputStream();
		while ((len = in.read(bytes)) > 0) {
			out.write(bytes, 0, len);
		}
		in.close();
		out.close();
	}

	private Map<String, BigDecimal> removeMultiData(List<CheckDTO> detailList) {
		Map<String, BigDecimal> map = Maps.newHashMap();
		detailList.stream().forEach(item -> {
			if (StringUtils.isBlank(item.getSubjectNo())) {
				return;
			}
			if (map.containsKey(item.getSubjectNo())) {
				map.put(item.getSubjectNo(), map.get(item.getSubjectNo()).add(item.getBalance()));
			} else {
				map.put(item.getSubjectNo(), item.getBalance());
			}
		});
		return map;
	}

}
