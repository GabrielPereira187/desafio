package br.com.desafio.service;

import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.exception.File.EmptyFieldList;
import br.com.desafio.exception.File.FieldNotExistException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileGeneratorService {

    Map<String, String> headerMap = new HashMap<>();


    public void generateCSV(HttpServletResponse response, Page<ProductResponse> productResponseList, List<String> fields) throws Exception {
        if(fields.isEmpty()) {
            throw new EmptyFieldList();
        }

        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + generateFileName(".csv");
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeaders = mapHeaders(fields).toArray(new String[0]);
        String[] csvFieldMapping = fields.toArray(new String[0]);

        csvBeanWriter.writeHeader(csvHeaders);

        for (ProductResponse productResponse : productResponseList.getContent()) {
            csvBeanWriter.write(productResponse,csvFieldMapping);
        }

        csvBeanWriter.close();
    }

    public void generateXLS(HttpServletResponse response, Page<ProductResponse> productResponseList, List<String> fields) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        Row headerRow = sheet.createRow(0);
        List<String> xlsHeaders = mapHeaders(fields);
        for (int i = 0; i < xlsHeaders.size(); i++) {
            headerRow.createCell(i).setCellValue(xlsHeaders.get(i));
        }

        for (ProductResponse productResponse : productResponseList.getContent()) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1); // Adiciona uma nova linha ao final

            for (int i = 0; i < fields.size(); i++) {
                String fieldName = fields.get(i);

                // Obtenha o valor do campo dinâmico (fieldName) usando reflexão
                try {
                    Field field = ProductResponse.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(productResponse);

                    Cell cell = row.createCell(i);
                    if (value != null) {
                        cell.setCellValue(value.toString()); // Insere o valor na célula
                    } else {
                        cell.setCellValue(""); // Define como vazio se o valor for nulo
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace(); // Trate a exceção adequadamente
                }
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + generateFileName(".xlsx"));

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private List<String> mapHeaders(List<String> fields) throws Exception {
        List<String> headers = new ArrayList<>();
        headerMap.put("productid", "ID do produto");
        headerMap.put("name", "Nome");
        headerMap.put("sku", "SKU");
        headerMap.put("icms", "ICMS");
        headerMap.put("categoryid", "Categoria do produto");
        headerMap.put("cost", "Custo(R$)");
        headerMap.put("revenuevalue", "Valor de revenda(R$)");
        headerMap.put("entrydate", "Data de inserção");
        headerMap.put("updateddate", "Data de atualização");
        headerMap.put("userid", "ID do usuário");
        headerMap.put("quantity", "Quantidade");
        for (String field : fields) {
            field = field.toLowerCase();
            String value = headerMap.get(field);
            if(value == null) {
                throw new FieldNotExistException(field);
            }
            headers.add(value);
        }

        return headers;
    }

    private String generateFileName(String format) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        return String.format("Products_%s_%s", timeStamp, format);
    }
}
