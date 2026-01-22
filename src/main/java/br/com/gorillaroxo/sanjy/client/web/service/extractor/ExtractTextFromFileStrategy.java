package br.com.gorillaroxo.sanjy.client.web.service.extractor;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExtractTextFromFileStrategy {

    String extract(final MultipartFile file);

    List<MediaType> mediaTypeAccepted();

    default boolean accept(final MultipartFile file) {
        return mediaTypeAccepted()
            .stream()
            .map(MimeType::toString)
            .anyMatch(mediaType -> mediaType.equals(file.getContentType()));
    }
}
