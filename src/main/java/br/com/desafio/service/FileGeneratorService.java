package br.com.desafio.service;

import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.exception.File.EmptyFieldList;
import br.com.desafio.exception.File.FieldNotExistException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileGeneratorService {

    Map<String, String> headerMap = new HashMap<>();


    public void generateCSV(HttpServletResponse response, Page<ProductResponse> productResponseList, List<String> fields) throws Exception {
        if(fields.isEmpty()) {
            throw new EmptyFieldList();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timeStamp = dateFormat.format(new Date());
        String fileName = "Product_" + timeStamp + ".csv";

        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
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

    private List<String> mapHeaders(List<String> fields) throws Exception {
        List<String> headers = new ArrayList<>();
        headerMap.put("productId", "ID do produto");
        headerMap.put("name", "Nome");
        headerMap.put("SKU", "SKU");
        headerMap.put("ICMS", "ICMS");
        headerMap.put("categoryId", "Categoria do produto");
        headerMap.put("cost", "Custo(R$)");
        headerMap.put("revenueValue", "Valor de revenda(R$)");
        headerMap.put("entryDate", "Data de inserção");
        headerMap.put("updatedDate", "Data de atualização");
        headerMap.put("userId", "ID do usuário");
        headerMap.put("quantity", "Quantidade");
        for (String field : fields) {
            String value = headerMap.get(field);
            if(value == null) {
                throw new FieldNotExistException(field);
            }
            headers.add(value);
        }

        return headers;
    }
}
