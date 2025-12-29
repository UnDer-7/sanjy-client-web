package br.com.gorillaroxo.sanjy.client.web.service.extractor;

import br.com.gorillaroxo.sanjy.client.web.exception.FailToExtractTextFromPdfFileException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractTextFromFilePdfStrategy implements ExtractTextFromFileStrategy {

    @Override
    public String extract(final MultipartFile file) {
        log.info(
            LogField.Placeholders.FOUR.placeholder,
            StructuredArguments.kv(LogField.MSG.label(), "Extracting text String from PDF file"),
            StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
            StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
            StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()));

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();

            final String text = stripper.getText(document);

            log.info(
                LogField.Placeholders.FOUR.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Successfully extract text String from PDF file"),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()));

            return text;
        } catch (final IOException e) {
            log.warn(
                LogField.Placeholders.FIVE.placeholder,
                StructuredArguments.kv(LogField.MSG.label(), "Fail to extract text from PDF file"),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()),
                StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                e);

            throw new FailToExtractTextFromPdfFileException(e);
        }
    }

    @Override
    public boolean accept(final MultipartFile file) {
        return MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType());
    }

}
