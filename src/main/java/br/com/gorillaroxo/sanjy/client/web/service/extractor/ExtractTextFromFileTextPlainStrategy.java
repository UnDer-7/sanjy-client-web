package br.com.gorillaroxo.sanjy.client.web.service.extractor;

import br.com.gorillaroxo.sanjy.client.web.exception.FailToExtractTextFromPlainTextFileException;
import br.com.gorillaroxo.sanjy.client.web.util.LogField;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
public class ExtractTextFromFileTextPlainStrategy implements ExtractTextFromFileStrategy {

    @Override
    public String extract(final MultipartFile file) {
        log.info(
                LogField.Placeholders.FOUR.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Extracting text String from Plain Text file"),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()));

        try {
            byte[] bytes = file.getBytes();
            final String text = new String(bytes, StandardCharsets.UTF_8);

            log.info(
                    LogField.Placeholders.FOUR.getPlaceholder(),
                    StructuredArguments.kv(
                            LogField.MSG.label(), "Successfully extract text String from Plain Text file"),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()));

            return text;
        } catch (final IOException e) {
            log.warn(
                    LogField.Placeholders.FIVE.getPlaceholder(),
                    StructuredArguments.kv(LogField.MSG.label(), "Fail to extract text from Plain Text file"),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_NAME.label(), file.getOriginalFilename()),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_CONTENT_TYPE.label(), file.getContentType()),
                    StructuredArguments.kv(LogField.DIET_PLAN_FILE_SIZE_BYTES.label(), file.getSize()),
                    StructuredArguments.kv(LogField.EXCEPTION_MESSAGE.label(), e.getMessage()),
                    e);

            throw new FailToExtractTextFromPlainTextFileException(e);
        }
    }

    @Override
    public List<MediaType> mediaTypeAccepted() {
        return List.of(MediaType.TEXT_MARKDOWN, MediaType.TEXT_PLAIN);
    }
}
