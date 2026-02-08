package diotviet.server.traits;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import diotviet.server.exceptions.ExportCSVException;
import diotviet.server.exceptions.FileUploadingException;
import diotviet.server.templates.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseController {

    // ****************************
    // Properties
    // ****************************

    @Autowired
    private MessageSource messageSource;

    // ****************************
    // Protected API
    // ****************************

    /**
     * OK response wrapper without message
     *
     * @param any
     * @return
     */
    protected ResponseEntity<GeneralResponse> ok(Object any) {
        return ResponseEntity.ok(GeneralResponse.success(any));
    }

    /**
     * OK response wrapper with message
     *
     * @param any
     * @return
     */
    protected ResponseEntity<GeneralResponse> ok(String message, Object any) {
        return ResponseEntity.ok(GeneralResponse.success(message, any));
    }

    /**
     * Translate key with default message
     *
     * @param key
     * @param defaultMessage
     * @return
     */
    protected String __(String key, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * Translate key without default message
     *
     * @param key
     * @return
     */
    protected String __(String key) {
        return __(key, key);
    }

    /**
     * @param key
     * @param args
     * @return
     */
    protected String __(String key, Object[] args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }

    /**
     * Export Bean to CSV
     *
     * @param beans
     * @param <T>
     * @return
     * @throws CsvRequiredFieldEmptyException
     * @throws CsvDataTypeMismatchException
     */
    protected <T> byte[] export(List<T> beans) {
        try {
            // Create a StringWriter
            StringWriter writer = new StringWriter();
            // Create a BeanToCsv writer
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).build();

            // Write to writer
            beanToCsv.write(beans);

            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            throw new ExportCSVException(e.getMessage());
        }
    }

    /**
     * Parse CSV and return Entity list
     *
     * @param file
     * @param type
     * @param <T>
     * @return
     */
    protected <T> List<T> parse(MultipartFile file, Class<T> type) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException ignored) {
            throw new FileUploadingException();
        }
    }
}
