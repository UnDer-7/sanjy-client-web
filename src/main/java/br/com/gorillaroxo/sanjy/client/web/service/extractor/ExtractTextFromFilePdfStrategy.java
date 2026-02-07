package br.com.gorillaroxo.sanjy.client.web.service.extractor;

import br.com.gorillaroxo.sanjy.client.web.exception.FailToExtractTextFromPdfFileException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractTextFromFilePdfStrategy implements ExtractTextFromFileStrategy {

    @Override
    public String extract(final MultipartFile file) {
        log.info(
                LogField.Placeholders.FOUR.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Extracting text String from PDF file using iText"),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()));

        try (InputStream inputStream = file.getInputStream();
                PdfReader reader = new PdfReader(inputStream);
                PdfDocument pdfDocument = new PdfDocument(reader)) {

            StringBuilder textBuilder = new StringBuilder();
            int numberOfPages = pdfDocument.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {
                String pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i));
                textBuilder.append(pageText);
                if (i < numberOfPages) {
                    textBuilder.append("\n");
                }
            }

            final String text = textBuilder.toString();

            log.info(
                    LogField.Placeholders.FOUR.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Successfully extract text String from PDF file"),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()));

            return text;
        } catch (final Exception e) {
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
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
    public List<MediaType> mediaTypeAccepted() {
        return List.of(MediaType.APPLICATION_PDF);
    }
}
