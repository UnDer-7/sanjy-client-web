package br.com.gorillaroxo.sanjy.client.web.service.extractor;

import org.springframework.web.multipart.MultipartFile;

public interface ExtractTextFromFileStrategy {

    String extract(final MultipartFile file);

    boolean accept(final MultipartFile file);

}
