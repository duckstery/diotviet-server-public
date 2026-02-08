package diotviet.server.templates.Product;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record ProductInteractRequest(
        Long id,
        Long category,
        Long[] groups,
        String code,
        String title,
        String description,
        Long originalPrice,
        Long discount,
        String discountUnit,
        Long actualPrice,
        String measureUnit,
        Long imgId,
        String src,
        Integer weight,
        Boolean canBeAccumulated,
        Boolean isInBusiness,
        MultipartFile file
) {
}
